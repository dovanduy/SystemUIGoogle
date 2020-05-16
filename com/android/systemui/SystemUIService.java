// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.os.UserHandle;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService;
import android.util.Slog;
import android.os.Process;
import com.android.internal.os.BinderInternal$BinderProxyLimitListener;
import com.android.internal.os.BinderInternal;
import android.os.SystemProperties;
import android.os.Build;
import android.os.IBinder;
import android.content.Intent;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Handler;
import com.android.systemui.dump.DumpManager;
import android.app.Service;

public class SystemUIService extends Service
{
    private final DumpManager mDumpManager;
    private final Handler mMainHandler;
    
    public SystemUIService(final Handler mMainHandler, final DumpManager mDumpManager) {
        this.mMainHandler = mMainHandler;
        this.mDumpManager = mDumpManager;
    }
    
    protected void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        String[] array2 = array;
        if (array.length == 0) {
            array2 = new String[] { "--dump-priority", "CRITICAL" };
        }
        this.mDumpManager.dump(fileDescriptor, printWriter, array2);
    }
    
    public IBinder onBind(final Intent intent) {
        return null;
    }
    
    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication)this.getApplication()).startServicesIfNeeded();
        if (Build.IS_DEBUGGABLE && SystemProperties.getBoolean("debug.crash_sysui", false)) {
            throw new RuntimeException();
        }
        if (Build.IS_DEBUGGABLE) {
            BinderInternal.nSetBinderProxyCountEnabled(true);
            BinderInternal.nSetBinderProxyCountWatermarks(1000, 900);
            BinderInternal.setBinderProxyCountCallback((BinderInternal$BinderProxyLimitListener)new BinderInternal$BinderProxyLimitListener(this) {
                public void onLimitReached(final int i) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("uid ");
                    sb.append(i);
                    sb.append(" sent too many Binder proxies to uid ");
                    sb.append(Process.myUid());
                    Slog.w("SystemUIService", sb.toString());
                }
            }, this.mMainHandler);
        }
        this.startServiceAsUser(new Intent(this.getApplicationContext(), (Class)SystemUIAuxiliaryDumpService.class), UserHandle.SYSTEM);
    }
}
