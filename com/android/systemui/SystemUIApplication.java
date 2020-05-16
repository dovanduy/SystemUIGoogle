// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.pm.ApplicationInfo;
import android.app.ActivityThread;
import com.android.systemui.util.NotificationChannels;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.content.res.Configuration;
import com.android.systemui.dump.DumpManager;
import java.lang.reflect.InvocationTargetException;
import android.content.Context;
import android.util.TimingsTraceLog;
import android.os.Process;
import android.os.SystemProperties;
import android.util.Log;
import com.android.systemui.dagger.SystemUIRootComponent;
import com.android.systemui.dagger.ContextComponentHelper;
import android.app.Application;

public class SystemUIApplication extends Application implements ContextInitializer
{
    private BootCompleteCacheImpl mBootCompleteCache;
    private ContextComponentHelper mComponentHelper;
    private ContextAvailableCallback mContextAvailableCallback;
    private SystemUIRootComponent mRootComponent;
    private SystemUI[] mServices;
    private boolean mServicesStarted;
    
    public SystemUIApplication() {
        Log.v("SystemUIService", "SystemUIApplication constructed.");
    }
    
    private void startServicesIfNeeded(final String str, final String[] array) {
        if (this.mServicesStarted) {
            return;
        }
        this.mServices = new SystemUI[array.length];
        if (!this.mBootCompleteCache.isBootComplete() && "1".equals(SystemProperties.get("sys.boot_completed"))) {
            this.mBootCompleteCache.setBootComplete();
        }
        final DumpManager dumpManager = this.mRootComponent.createDumpManager();
        final StringBuilder sb = new StringBuilder();
        sb.append("Starting SystemUI services for user ");
        sb.append(Process.myUserHandle().getIdentifier());
        sb.append(".");
        Log.v("SystemUIService", sb.toString());
        final TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SystemUIBootTiming", 4096L);
        timingsTraceLog.traceBegin(str);
        final int length = array.length;
        int i = 0;
        while (i < length) {
            final String str2 = array[i];
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(str2);
            timingsTraceLog.traceBegin(sb2.toString());
            final long currentTimeMillis = System.currentTimeMillis();
            try {
                SystemUI resolveSystemUI;
                if ((resolveSystemUI = this.mComponentHelper.resolveSystemUI(str2)) == null) {
                    resolveSystemUI = (SystemUI)Class.forName(str2).getConstructor(Context.class).newInstance(this);
                }
                (this.mServices[i] = resolveSystemUI).start();
                timingsTraceLog.traceEnd();
                final long lng = System.currentTimeMillis() - currentTimeMillis;
                if (lng > 1000L) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("Initialization of ");
                    sb3.append(str2);
                    sb3.append(" took ");
                    sb3.append(lng);
                    sb3.append(" ms");
                    Log.w("SystemUIService", sb3.toString());
                }
                if (this.mBootCompleteCache.isBootComplete()) {
                    this.mServices[i].onBootCompleted();
                }
                dumpManager.registerDumpable(this.mServices[i].getClass().getName(), this.mServices[i]);
                ++i;
                continue;
            }
            catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                final Object cause;
                throw new RuntimeException((Throwable)cause);
            }
            break;
        }
        this.mRootComponent.getInitController().executePostInitTasks();
        timingsTraceLog.traceEnd();
        this.mServicesStarted = true;
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        if (this.mServicesStarted) {
            this.mRootComponent.getConfigurationController().onConfigurationChanged(configuration);
            for (int length = this.mServices.length, i = 0; i < length; ++i) {
                final SystemUI[] mServices = this.mServices;
                if (mServices[i] != null) {
                    mServices[i].onConfigurationChanged(configuration);
                }
            }
        }
    }
    
    public void onCreate() {
        super.onCreate();
        Log.v("SystemUIService", "SystemUIApplication created.");
        final TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SystemUIBootTiming", 4096L);
        timingsTraceLog.traceBegin("DependencyInjection");
        this.mContextAvailableCallback.onContextAvailable((Context)this);
        final SystemUIRootComponent rootComponent = SystemUIFactory.getInstance().getRootComponent();
        this.mRootComponent = rootComponent;
        this.mComponentHelper = rootComponent.getContextComponentHelper();
        this.mBootCompleteCache = this.mRootComponent.provideBootCacheImpl();
        timingsTraceLog.traceEnd();
        this.setTheme(R$style.Theme_SystemUI);
        if (Process.myUserHandle().equals((Object)UserHandle.SYSTEM)) {
            final IntentFilter intentFilter = new IntentFilter("android.intent.action.BOOT_COMPLETED");
            intentFilter.setPriority(1000);
            this.registerReceiver((BroadcastReceiver)new BroadcastReceiver() {
                public void onReceive(final Context context, final Intent intent) {
                    if (SystemUIApplication.this.mBootCompleteCache.isBootComplete()) {
                        return;
                    }
                    SystemUIApplication.this.unregisterReceiver((BroadcastReceiver)this);
                    SystemUIApplication.this.mBootCompleteCache.setBootComplete();
                    if (SystemUIApplication.this.mServicesStarted) {
                        for (int length = SystemUIApplication.this.mServices.length, i = 0; i < length; ++i) {
                            SystemUIApplication.this.mServices[i].onBootCompleted();
                        }
                    }
                }
            }, intentFilter);
            this.registerReceiver((BroadcastReceiver)new BroadcastReceiver() {
                public void onReceive(final Context context, final Intent intent) {
                    if ("android.intent.action.LOCALE_CHANGED".equals(intent.getAction())) {
                        if (!SystemUIApplication.this.mBootCompleteCache.isBootComplete()) {
                            return;
                        }
                        NotificationChannels.createAll(context);
                    }
                }
            }, new IntentFilter("android.intent.action.LOCALE_CHANGED"));
        }
        else {
            final String currentProcessName = ActivityThread.currentProcessName();
            final ApplicationInfo applicationInfo = this.getApplicationInfo();
            if (currentProcessName != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append(applicationInfo.processName);
                sb.append(":");
                if (currentProcessName.startsWith(sb.toString())) {
                    return;
                }
            }
            this.startSecondaryUserServicesIfNeeded();
        }
    }
    
    public void setContextAvailableCallback(final ContextAvailableCallback mContextAvailableCallback) {
        this.mContextAvailableCallback = mContextAvailableCallback;
    }
    
    void startSecondaryUserServicesIfNeeded() {
        this.startServicesIfNeeded("StartSecondaryServices", this.getResources().getStringArray(R$array.config_systemUIServiceComponentsPerUser));
    }
    
    public void startServicesIfNeeded() {
        this.startServicesIfNeeded("StartServices", this.getResources().getStringArray(R$array.config_systemUIServiceComponents));
    }
}
