// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.plugins;

import android.os.SystemProperties;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.graphics.drawable.Icon;
import android.app.Notification$Action$Builder;
import android.app.PendingIntent;
import android.net.Uri;
import android.app.Notification$Builder;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.res.Resources;
import android.app.NotificationManager;
import android.content.Intent;
import dalvik.system.PathClassLoader;
import android.text.TextUtils;
import java.io.File;
import java.util.List;
import android.app.ActivityThread;
import android.app.LoadedApk;
import java.util.ArrayList;
import android.util.Log;
import android.content.pm.ApplicationInfo;
import com.android.systemui.plugins.Plugin;
import android.content.IntentFilter;
import java.util.Iterator;
import android.content.ComponentName;
import com.android.internal.annotations.VisibleForTesting;
import android.os.Handler;
import java.util.Collection;
import java.util.Arrays;
import android.os.Build;
import com.android.systemui.plugins.PluginListener;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.os.Looper;
import android.content.Context;
import java.util.Map;
import android.content.BroadcastReceiver;

public class PluginManagerImpl extends BroadcastReceiver implements PluginManager
{
    private static final String TAG;
    private final boolean isDebuggable;
    private final Map<String, ClassLoader> mClassLoaders;
    private final Context mContext;
    private final PluginInstanceManagerFactory mFactory;
    private boolean mHasOneShot;
    private boolean mListening;
    private Looper mLooper;
    private final ArraySet<String> mOneShotPackages;
    private ClassLoaderFilter mParentClassLoader;
    private final PluginEnabler mPluginEnabler;
    private final PluginInitializer mPluginInitializer;
    private final ArrayMap<PluginListener<?>, PluginInstanceManager> mPluginMap;
    private final PluginPrefs mPluginPrefs;
    private final ArraySet<String> mWhitelistedPlugins;
    
    static {
        TAG = PluginManagerImpl.class.getSimpleName();
    }
    
    public PluginManagerImpl(final Context context, final PluginInitializer pluginInitializer) {
        this(context, new PluginInstanceManagerFactory(), Build.IS_DEBUGGABLE, Thread.getUncaughtExceptionPreHandler(), pluginInitializer);
    }
    
    @VisibleForTesting
    PluginManagerImpl(final Context mContext, final PluginInstanceManagerFactory mFactory, final boolean isDebuggable, final Thread.UncaughtExceptionHandler uncaughtExceptionHandler, final PluginInitializer mPluginInitializer) {
        this.mPluginMap = (ArrayMap<PluginListener<?>, PluginInstanceManager>)new ArrayMap();
        this.mClassLoaders = (Map<String, ClassLoader>)new ArrayMap();
        this.mOneShotPackages = (ArraySet<String>)new ArraySet();
        this.mWhitelistedPlugins = (ArraySet<String>)new ArraySet();
        this.mContext = mContext;
        this.mFactory = mFactory;
        this.mLooper = mPluginInitializer.getBgLooper();
        this.isDebuggable = isDebuggable;
        this.mWhitelistedPlugins.addAll((Collection)Arrays.asList(mPluginInitializer.getWhitelistedPlugins(this.mContext)));
        this.mPluginPrefs = new PluginPrefs(this.mContext);
        this.mPluginEnabler = mPluginInitializer.getPluginEnabler(this.mContext);
        this.mPluginInitializer = mPluginInitializer;
        Thread.setUncaughtExceptionPreHandler((Thread.UncaughtExceptionHandler)new PluginExceptionHandler(uncaughtExceptionHandler));
        new Handler(this.mLooper).post((Runnable)new Runnable(this) {
            @Override
            public void run() {
                mPluginInitializer.onPluginManagerInit();
            }
        });
    }
    
    private boolean clearClassLoader(final String s) {
        return this.mClassLoaders.remove(s) != null;
    }
    
    private boolean isPluginPackageWhitelisted(final String s) {
        for (final String s2 : this.mWhitelistedPlugins) {
            final ComponentName unflattenFromString = ComponentName.unflattenFromString(s2);
            if (unflattenFromString != null) {
                if (unflattenFromString.getPackageName().equals(s)) {
                    return true;
                }
                continue;
            }
            else {
                if (s2.equals(s)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private boolean isPluginWhitelisted(final ComponentName componentName) {
        for (final String s : this.mWhitelistedPlugins) {
            final ComponentName unflattenFromString = ComponentName.unflattenFromString(s);
            if (unflattenFromString != null) {
                if (unflattenFromString.equals((Object)componentName)) {
                    return true;
                }
                continue;
            }
            else {
                if (s.equals(componentName.getPackageName())) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private void startListening() {
        if (this.mListening) {
            return;
        }
        this.mListening = true;
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("com.android.systemui.action.PLUGIN_CHANGED");
        intentFilter.addAction("com.android.systemui.action.DISABLE_PLUGIN");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver((BroadcastReceiver)this, intentFilter);
        this.mContext.registerReceiver((BroadcastReceiver)this, new IntentFilter("android.intent.action.USER_UNLOCKED"));
    }
    
    private void stopListening() {
        if (this.mListening) {
            if (!this.mHasOneShot) {
                this.mListening = false;
                this.mContext.unregisterReceiver((BroadcastReceiver)this);
            }
        }
    }
    
    public <T extends Plugin> void addPluginListener(final PluginListener<T> pluginListener, final Class<?> clazz) {
        this.addPluginListener(pluginListener, clazz, false);
    }
    
    public <T extends Plugin> void addPluginListener(final PluginListener<T> pluginListener, final Class<?> clazz, final boolean b) {
        this.addPluginListener(Helper.getAction(clazz), pluginListener, clazz, b);
    }
    
    public <T extends Plugin> void addPluginListener(final String s, final PluginListener<T> pluginListener, final Class<?> clazz) {
        this.addPluginListener(s, pluginListener, clazz, false);
    }
    
    public <T extends Plugin> void addPluginListener(final String s, final PluginListener<T> pluginListener, final Class clazz, final boolean b) {
        this.mPluginPrefs.addAction(s);
        final PluginInstanceManager pluginInstanceManager = this.mFactory.createPluginInstanceManager(this.mContext, s, pluginListener, b, this.mLooper, clazz, this);
        pluginInstanceManager.loadAll();
        synchronized (this) {
            this.mPluginMap.put((Object)pluginListener, (Object)pluginInstanceManager);
            // monitorexit(this)
            this.startListening();
        }
    }
    
    public <T> boolean dependsOn(final Plugin plugin, final Class<T> clazz) {
        // monitorenter(this)
        int i = 0;
        try {
            while (i < this.mPluginMap.size()) {
                if (((PluginInstanceManager)this.mPluginMap.valueAt(i)).dependsOn(plugin, clazz)) {
                    return true;
                }
                ++i;
            }
            return false;
        }
        finally {
        }
        // monitorexit(this)
    }
    
    public ClassLoader getClassLoader(final ApplicationInfo applicationInfo) {
        if (!this.isDebuggable && !this.isPluginPackageWhitelisted(applicationInfo.packageName)) {
            final String tag = PluginManagerImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot get class loader for non-whitelisted plugin. Src:");
            sb.append(applicationInfo.sourceDir);
            sb.append(", pkg: ");
            sb.append(applicationInfo.packageName);
            Log.w(tag, sb.toString());
            return null;
        }
        if (this.mClassLoaders.containsKey(applicationInfo.packageName)) {
            return this.mClassLoaders.get(applicationInfo.packageName);
        }
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        LoadedApk.makePaths((ActivityThread)null, true, applicationInfo, (List)list, (List)list2);
        final PathClassLoader pathClassLoader = new PathClassLoader(TextUtils.join((CharSequence)File.pathSeparator, (Iterable)list), TextUtils.join((CharSequence)File.pathSeparator, (Iterable)list2), this.getParentClassLoader());
        this.mClassLoaders.put(applicationInfo.packageName, (ClassLoader)pathClassLoader);
        return (ClassLoader)pathClassLoader;
    }
    
    ClassLoader getParentClassLoader() {
        if (this.mParentClassLoader == null) {
            this.mParentClassLoader = new ClassLoaderFilter(PluginManagerImpl.class.getClassLoader(), "com.android.systemui.plugin");
        }
        return this.mParentClassLoader;
    }
    
    public PluginEnabler getPluginEnabler() {
        return this.mPluginEnabler;
    }
    
    public String[] getWhitelistedPlugins() {
        return (String[])this.mWhitelistedPlugins.toArray((Object[])new String[0]);
    }
    
    public void handleWtfs() {
        this.mPluginInitializer.handleWtfs();
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final String tag = PluginManagerImpl.TAG;
        if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
            synchronized (this) {
                final Iterator<PluginInstanceManager> iterator = (Iterator<PluginInstanceManager>)this.mPluginMap.values().iterator();
                while (iterator.hasNext()) {
                    iterator.next().loadAll();
                }
                return;
            }
        }
        String encodedSchemeSpecificPart;
        if ("com.android.systemui.action.DISABLE_PLUGIN".equals(intent.getAction())) {
            final ComponentName unflattenFromString = ComponentName.unflattenFromString(intent.getData().toString().substring(10));
            if (this.isPluginWhitelisted(unflattenFromString)) {
                return;
            }
            this.getPluginEnabler().setDisabled(unflattenFromString, 1);
            ((NotificationManager)this.mContext.getSystemService((Class)NotificationManager.class)).cancel(unflattenFromString.getClassName(), 6);
            return;
        }
        else {
            encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
            final ComponentName unflattenFromString2 = ComponentName.unflattenFromString(encodedSchemeSpecificPart);
            if (this.mOneShotPackages.contains((Object)encodedSchemeSpecificPart)) {
                final int identifier = Resources.getSystem().getIdentifier("stat_sys_warning", "drawable", "android");
                final int identifier2 = Resources.getSystem().getIdentifier("system_notification_accent_color", "color", "android");
                String string;
                try {
                    final PackageManager packageManager = this.mContext.getPackageManager();
                    string = packageManager.getApplicationInfo(encodedSchemeSpecificPart, 0).loadLabel(packageManager).toString();
                }
                catch (PackageManager$NameNotFoundException ex) {
                    string = encodedSchemeSpecificPart;
                }
                final Notification$Builder setColor = new Notification$Builder(this.mContext, "ALR").setSmallIcon(identifier).setWhen(0L).setShowWhen(false).setPriority(2).setVisibility(1).setColor(this.mContext.getColor(identifier2));
                final StringBuilder sb = new StringBuilder();
                sb.append("Plugin \"");
                sb.append(string);
                sb.append("\" has updated");
                final Notification$Builder setContentText = setColor.setContentTitle((CharSequence)sb.toString()).setContentText((CharSequence)"Restart SysUI for changes to take effect.");
                final Intent intent2 = new Intent("com.android.systemui.action.RESTART");
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("package://");
                sb2.append(encodedSchemeSpecificPart);
                setContentText.addAction(new Notification$Action$Builder((Icon)null, (CharSequence)"Restart SysUI", PendingIntent.getBroadcast(this.mContext, 0, intent2.setData(Uri.parse(sb2.toString())), 0)).build());
                ((NotificationManager)this.mContext.getSystemService((Class)NotificationManager.class)).notify(6, setContentText.build());
            }
            if (this.clearClassLoader(encodedSchemeSpecificPart)) {
                if (Build.IS_ENG) {
                    final Context mContext = this.mContext;
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("Reloading ");
                    sb3.append(encodedSchemeSpecificPart);
                    Toast.makeText(mContext, (CharSequence)sb3.toString(), 1).show();
                }
                else {
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append("Reloading ");
                    sb4.append(encodedSchemeSpecificPart);
                    Log.v(tag, sb4.toString());
                }
            }
            if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction()) && unflattenFromString2 != null) {
                final int disableReason = this.getPluginEnabler().getDisableReason(unflattenFromString2);
                if (disableReason == 2 || disableReason == 3 || disableReason == 1) {
                    final StringBuilder sb5 = new StringBuilder();
                    sb5.append("Re-enabling previously disabled plugin that has been updated: ");
                    sb5.append(unflattenFromString2.flattenToShortString());
                    Log.i(tag, sb5.toString());
                    this.getPluginEnabler().setEnabled(unflattenFromString2);
                }
            }
        }
        synchronized (this) {
            if (!"android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
                final Iterator<PluginInstanceManager> iterator2 = (Iterator<PluginInstanceManager>)this.mPluginMap.values().iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().onPackageChange(encodedSchemeSpecificPart);
                }
            }
            else {
                final Iterator<PluginInstanceManager> iterator3 = (Iterator<PluginInstanceManager>)this.mPluginMap.values().iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().onPackageRemoved(encodedSchemeSpecificPart);
                }
            }
        }
    }
    
    public void removePluginListener(final PluginListener<?> pluginListener) {
        synchronized (this) {
            if (!this.mPluginMap.containsKey((Object)pluginListener)) {
                return;
            }
            ((PluginInstanceManager)this.mPluginMap.remove((Object)pluginListener)).destroy();
            if (this.mPluginMap.size() == 0) {
                this.stopListening();
            }
        }
    }
    
    private static class ClassLoaderFilter extends ClassLoader
    {
        private final ClassLoader mBase;
        private final String mPackage;
        
        public ClassLoaderFilter(final ClassLoader mBase, final String mPackage) {
            super(ClassLoader.getSystemClassLoader());
            this.mBase = mBase;
            this.mPackage = mPackage;
        }
        
        @Override
        protected Class<?> loadClass(final String s, final boolean resolve) throws ClassNotFoundException {
            if (!s.startsWith(this.mPackage)) {
                super.loadClass(s, resolve);
            }
            return this.mBase.loadClass(s);
        }
    }
    
    public static class CrashWhilePluginActiveException extends RuntimeException
    {
        public CrashWhilePluginActiveException(final Throwable cause) {
            super(cause);
        }
    }
    
    private class PluginExceptionHandler implements UncaughtExceptionHandler
    {
        private final UncaughtExceptionHandler mHandler;
        
        private PluginExceptionHandler(final UncaughtExceptionHandler mHandler) {
            this.mHandler = mHandler;
        }
        
        private boolean checkStack(final Throwable t) {
            int i = 0;
            if (t == null) {
                return false;
            }
            synchronized (this) {
                final StackTraceElement[] stackTrace = t.getStackTrace();
                final int length = stackTrace.length;
                boolean b = false;
                while (i < length) {
                    final StackTraceElement stackTraceElement = stackTrace[i];
                    final Iterator<PluginInstanceManager> iterator = (Iterator<PluginInstanceManager>)PluginManagerImpl.this.mPluginMap.values().iterator();
                    while (iterator.hasNext()) {
                        b |= iterator.next().checkAndDisable(stackTraceElement.getClassName());
                    }
                    ++i;
                }
                return this.checkStack(t.getCause()) | b;
            }
        }
        
        @Override
        public void uncaughtException(final Thread thread, final Throwable t) {
            if (SystemProperties.getBoolean("plugin.debugging", false)) {
                this.mHandler.uncaughtException(thread, t);
                return;
            }
            boolean checkStack;
            boolean b = checkStack = this.checkStack(t);
            if (!b) {
                synchronized (this) {
                    final Iterator<PluginInstanceManager> iterator = (Iterator<PluginInstanceManager>)PluginManagerImpl.this.mPluginMap.values().iterator();
                    while (iterator.hasNext()) {
                        b |= iterator.next().disableAll();
                    }
                    // monitorexit(this)
                    checkStack = b;
                }
            }
            Throwable t2 = t;
            if (checkStack) {
                t2 = new CrashWhilePluginActiveException(t);
            }
            this.mHandler.uncaughtException(thread, t2);
        }
    }
    
    @VisibleForTesting
    public static class PluginInstanceManagerFactory
    {
        public <T extends Plugin> PluginInstanceManager createPluginInstanceManager(final Context context, final String s, final PluginListener<T> pluginListener, final boolean b, final Looper looper, final Class<?> clazz, final PluginManagerImpl pluginManagerImpl) {
            final VersionInfo versionInfo = new VersionInfo();
            versionInfo.addClass(clazz);
            return new PluginInstanceManager(context, s, (PluginListener<Plugin>)pluginListener, b, looper, versionInfo, pluginManagerImpl);
        }
    }
}
