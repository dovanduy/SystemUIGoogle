// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.plugins;

import android.content.pm.ServiceInfo;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.view.LayoutInflater;
import android.content.ContextWrapper;
import com.android.systemui.plugins.PluginFragment;
import android.os.Message;
import android.os.Handler;
import java.util.ArrayList;
import java.util.Iterator;
import android.util.Log;
import android.content.ComponentName;
import android.os.Build;
import java.util.Collection;
import java.util.Arrays;
import android.os.Looper;
import android.util.ArraySet;
import android.content.pm.PackageManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.PluginListener;
import android.content.Context;
import com.android.systemui.plugins.Plugin;

public class PluginInstanceManager<T extends Plugin>
{
    private final boolean isDebuggable;
    private final String mAction;
    private final boolean mAllowMultiple;
    private final Context mContext;
    private final PluginListener<T> mListener;
    @VisibleForTesting
    final MainHandler mMainHandler;
    private final PluginManagerImpl mManager;
    @VisibleForTesting
    final PluginHandler mPluginHandler;
    private final PackageManager mPm;
    private final VersionInfo mVersion;
    private final ArraySet<String> mWhitelistedPlugins;
    
    @VisibleForTesting
    PluginInstanceManager(final Context mContext, final PackageManager mPm, final String mAction, final PluginListener<T> mListener, final boolean mAllowMultiple, final Looper looper, final VersionInfo mVersion, final PluginManagerImpl mManager, final boolean isDebuggable, final String[] a) {
        this.mWhitelistedPlugins = (ArraySet<String>)new ArraySet();
        this.mMainHandler = new MainHandler(Looper.getMainLooper());
        this.mPluginHandler = new PluginHandler(looper);
        this.mManager = mManager;
        this.mContext = mContext;
        this.mPm = mPm;
        this.mAction = mAction;
        this.mListener = mListener;
        this.mAllowMultiple = mAllowMultiple;
        this.mVersion = mVersion;
        this.mWhitelistedPlugins.addAll((Collection)Arrays.asList(a));
        this.isDebuggable = isDebuggable;
    }
    
    PluginInstanceManager(final Context context, final String s, final PluginListener<T> pluginListener, final boolean b, final Looper looper, final VersionInfo versionInfo, final PluginManagerImpl pluginManagerImpl) {
        this(context, context.getPackageManager(), s, pluginListener, b, looper, versionInfo, pluginManagerImpl, Build.IS_DEBUGGABLE, pluginManagerImpl.getWhitelistedPlugins());
    }
    
    private boolean disable(final PluginInfo pluginInfo, final int n) {
        final ComponentName componentName = new ComponentName(pluginInfo.mPackage, pluginInfo.mClass);
        if (this.isPluginWhitelisted(componentName)) {
            return false;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Disabling plugin ");
        sb.append(componentName.flattenToShortString());
        Log.w("PluginInstanceManager", sb.toString());
        this.mManager.getPluginEnabler().setDisabled(componentName, n);
        return true;
    }
    
    private boolean isPluginWhitelisted(final ComponentName componentName) {
        for (final String s : this.mWhitelistedPlugins) {
            final ComponentName unflattenFromString = ComponentName.unflattenFromString(s);
            if (unflattenFromString == null) {
                if (s.equals(componentName.getPackageName())) {
                    return true;
                }
                continue;
            }
            else {
                if (unflattenFromString.equals((Object)componentName)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public boolean checkAndDisable(final String s) {
        final Iterator<PluginInfo> iterator = new ArrayList<PluginInfo>(this.mPluginHandler.mPlugins).iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final PluginInfo pluginInfo = iterator.next();
            if (s.startsWith(pluginInfo.mPackage)) {
                b |= this.disable(pluginInfo, 2);
            }
        }
        return b;
    }
    
    public <T> boolean dependsOn(final Plugin plugin, final Class<T> clazz) {
        final Iterator<PluginInfo<Object>> iterator = new ArrayList<PluginInfo<Object>>(this.mPluginHandler.mPlugins).iterator();
        PluginInfo<Object> pluginInfo;
        boolean b;
        do {
            final boolean hasNext = iterator.hasNext();
            final boolean b2;
            b = (b2 = false);
            if (!hasNext) {
                return b2;
            }
            pluginInfo = iterator.next();
        } while (!pluginInfo.mPlugin.getClass().getName().equals(plugin.getClass().getName()));
        boolean b2 = b;
        if (pluginInfo.mVersion == null) {
            return b2;
        }
        b2 = b;
        if (pluginInfo.mVersion.hasClass(clazz)) {
            b2 = true;
            return b2;
        }
        return b2;
    }
    
    public void destroy() {
        final Iterator<PluginInfo> iterator = new ArrayList<PluginInfo>(this.mPluginHandler.mPlugins).iterator();
        while (iterator.hasNext()) {
            this.mMainHandler.obtainMessage(2, (Object)iterator.next().mPlugin).sendToTarget();
        }
    }
    
    public boolean disableAll() {
        final ArrayList<PluginInfo> list = new ArrayList<PluginInfo>(this.mPluginHandler.mPlugins);
        int i = 0;
        boolean b = false;
        while (i < list.size()) {
            b |= this.disable(list.get(i), 3);
            ++i;
        }
        return b;
    }
    
    public void loadAll() {
        this.mPluginHandler.sendEmptyMessage(1);
    }
    
    public void onPackageChange(final String s) {
        this.mPluginHandler.obtainMessage(3, (Object)s).sendToTarget();
        this.mPluginHandler.obtainMessage(2, (Object)s).sendToTarget();
    }
    
    public void onPackageRemoved(final String s) {
        this.mPluginHandler.obtainMessage(3, (Object)s).sendToTarget();
    }
    
    @Override
    public String toString() {
        return String.format("%s@%s (action=%s)", PluginInstanceManager.class.getSimpleName(), this.hashCode(), this.mAction);
    }
    
    private class MainHandler extends Handler
    {
        public MainHandler(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 1) {
                if (what != 2) {
                    super.handleMessage(message);
                }
                else {
                    PluginInstanceManager.this.mListener.onPluginDisconnected((Plugin)message.obj);
                    final Object obj = message.obj;
                    if (!(obj instanceof PluginFragment)) {
                        ((Plugin)obj).onDestroy();
                    }
                }
            }
            else {
                PluginPrefs.setHasPlugins(PluginInstanceManager.this.mContext);
                final PluginInfo pluginInfo = (PluginInfo)message.obj;
                PluginInstanceManager.this.mManager.handleWtfs();
                if (!(message.obj instanceof PluginFragment)) {
                    ((Plugin)pluginInfo.mPlugin).onCreate(PluginInstanceManager.this.mContext, pluginInfo.mPluginContext);
                }
                PluginInstanceManager.this.mListener.onPluginConnected((Plugin)pluginInfo.mPlugin, pluginInfo.mPluginContext);
            }
        }
    }
    
    public static class PluginContextWrapper extends ContextWrapper
    {
        private final ClassLoader mClassLoader;
        private LayoutInflater mInflater;
        
        public PluginContextWrapper(final Context context, final ClassLoader mClassLoader) {
            super(context);
            this.mClassLoader = mClassLoader;
        }
        
        public ClassLoader getClassLoader() {
            return this.mClassLoader;
        }
        
        public Object getSystemService(final String anObject) {
            if ("layout_inflater".equals(anObject)) {
                if (this.mInflater == null) {
                    this.mInflater = LayoutInflater.from(this.getBaseContext()).cloneInContext((Context)this);
                }
                return this.mInflater;
            }
            return this.getBaseContext().getSystemService(anObject);
        }
    }
    
    private class PluginHandler extends Handler
    {
        private final ArrayList<PluginInfo<T>> mPlugins;
        
        public PluginHandler(final Looper looper) {
            super(looper);
            this.mPlugins = new ArrayList<PluginInfo<T>>();
        }
        
        private VersionInfo checkVersion(final Class<?> clazz, final T t, final VersionInfo versionInfo) throws VersionInfo.InvalidVersionException {
            final VersionInfo versionInfo2 = new VersionInfo();
            versionInfo2.addClass(clazz);
            if (versionInfo2.hasVersionInfo()) {
                versionInfo.checkVersion(versionInfo2);
                return versionInfo2;
            }
            if (t.getVersion() == versionInfo.getDefaultVersion()) {
                return null;
            }
            throw new VersionInfo.InvalidVersionException("Invalid legacy version", false);
        }
        
        private void handleQueryPlugins(final String package1) {
            final Intent intent = new Intent(PluginInstanceManager.this.mAction);
            if (package1 != null) {
                intent.setPackage(package1);
            }
            final List queryIntentServices = PluginInstanceManager.this.mPm.queryIntentServices(intent, 0);
            if (queryIntentServices.size() > 1 && !PluginInstanceManager.this.mAllowMultiple) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Multiple plugins found for ");
                sb.append(PluginInstanceManager.this.mAction);
                Log.w("PluginInstanceManager", sb.toString());
                return;
            }
            final Iterator<ResolveInfo> iterator = queryIntentServices.iterator();
            while (iterator.hasNext()) {
                final ServiceInfo serviceInfo = iterator.next().serviceInfo;
                final PluginInfo<T> handleLoadPlugin = this.handleLoadPlugin(new ComponentName(serviceInfo.packageName, serviceInfo.name));
                if (handleLoadPlugin == null) {
                    continue;
                }
                this.mPlugins.add(handleLoadPlugin);
                PluginInstanceManager.this.mMainHandler.obtainMessage(1, (Object)handleLoadPlugin).sendToTarget();
            }
        }
        
        protected PluginInfo<T> handleLoadPlugin(final ComponentName p0) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //     4: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$1100:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Z
            //     7: ifne            54
            //    10: aload_0        
            //    11: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //    14: aload_1        
            //    15: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$1200:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;Landroid/content/ComponentName;)Z
            //    18: ifne            54
            //    21: new             Ljava/lang/StringBuilder;
            //    24: dup            
            //    25: invokespecial   java/lang/StringBuilder.<init>:()V
            //    28: astore_2       
            //    29: aload_2        
            //    30: ldc             "Plugin cannot be loaded on production build: "
            //    32: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    35: pop            
            //    36: aload_2        
            //    37: aload_1        
            //    38: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //    41: pop            
            //    42: ldc             "PluginInstanceManager"
            //    44: aload_2        
            //    45: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //    48: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
            //    51: pop            
            //    52: aconst_null    
            //    53: areturn        
            //    54: aload_0        
            //    55: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //    58: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$600:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Lcom/android/systemui/shared/plugins/PluginManagerImpl;
            //    61: invokevirtual   com/android/systemui/shared/plugins/PluginManagerImpl.getPluginEnabler:()Lcom/android/systemui/shared/plugins/PluginEnabler;
            //    64: aload_1        
            //    65: invokeinterface com/android/systemui/shared/plugins/PluginEnabler.isEnabled:(Landroid/content/ComponentName;)Z
            //    70: ifne            75
            //    73: aconst_null    
            //    74: areturn        
            //    75: aload_1        
            //    76: invokevirtual   android/content/ComponentName.getPackageName:()Ljava/lang/String;
            //    79: astore_3       
            //    80: aload_1        
            //    81: invokevirtual   android/content/ComponentName.getClassName:()Ljava/lang/String;
            //    84: astore          4
            //    86: aload_0        
            //    87: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //    90: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$1000:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/pm/PackageManager;
            //    93: aload_3        
            //    94: iconst_0       
            //    95: invokevirtual   android/content/pm/PackageManager.getApplicationInfo:(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
            //    98: astore          5
            //   100: aload_0        
            //   101: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   104: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$1000:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/pm/PackageManager;
            //   107: ldc             "com.android.systemui.permission.PLUGIN"
            //   109: aload_3        
            //   110: invokevirtual   android/content/pm/PackageManager.checkPermission:(Ljava/lang/String;Ljava/lang/String;)I
            //   113: ifeq            149
            //   116: new             Ljava/lang/StringBuilder;
            //   119: astore_1       
            //   120: aload_1        
            //   121: invokespecial   java/lang/StringBuilder.<init>:()V
            //   124: aload_1        
            //   125: ldc             "Plugin doesn't have permission: "
            //   127: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   130: pop            
            //   131: aload_1        
            //   132: aload_3        
            //   133: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   136: pop            
            //   137: ldc             "PluginInstanceManager"
            //   139: aload_1        
            //   140: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   143: invokestatic    android/util/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
            //   146: pop            
            //   147: aconst_null    
            //   148: areturn        
            //   149: aload_0        
            //   150: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   153: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$600:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Lcom/android/systemui/shared/plugins/PluginManagerImpl;
            //   156: aload           5
            //   158: invokevirtual   com/android/systemui/shared/plugins/PluginManagerImpl.getClassLoader:(Landroid/content/pm/ApplicationInfo;)Ljava/lang/ClassLoader;
            //   161: astore          6
            //   163: new             Lcom/android/systemui/shared/plugins/PluginInstanceManager$PluginContextWrapper;
            //   166: astore_2       
            //   167: aload_2        
            //   168: aload_0        
            //   169: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   172: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$500:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/Context;
            //   175: aload           5
            //   177: iconst_0       
            //   178: invokevirtual   android/content/Context.createApplicationContext:(Landroid/content/pm/ApplicationInfo;I)Landroid/content/Context;
            //   181: aload           6
            //   183: invokespecial   com/android/systemui/shared/plugins/PluginInstanceManager$PluginContextWrapper.<init>:(Landroid/content/Context;Ljava/lang/ClassLoader;)V
            //   186: aload           4
            //   188: iconst_1       
            //   189: aload           6
            //   191: invokestatic    java/lang/Class.forName:(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;
            //   194: astore          6
            //   196: aload           6
            //   198: invokevirtual   java/lang/Class.newInstance:()Ljava/lang/Object;
            //   201: checkcast       Lcom/android/systemui/plugins/Plugin;
            //   204: astore          5
            //   206: aload_0        
            //   207: aload           6
            //   209: aload           5
            //   211: aload_0        
            //   212: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   215: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$1300:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Lcom/android/systemui/shared/plugins/VersionInfo;
            //   218: invokespecial   com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.checkVersion:(Ljava/lang/Class;Lcom/android/systemui/plugins/Plugin;Lcom/android/systemui/shared/plugins/VersionInfo;)Lcom/android/systemui/shared/plugins/VersionInfo;
            //   221: astore          6
            //   223: new             Lcom/android/systemui/shared/plugins/PluginInstanceManager$PluginInfo;
            //   226: dup            
            //   227: aload_3        
            //   228: aload           4
            //   230: aload           5
            //   232: aload_2        
            //   233: aload           6
            //   235: invokespecial   com/android/systemui/shared/plugins/PluginInstanceManager$PluginInfo.<init>:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Landroid/content/Context;Lcom/android/systemui/shared/plugins/VersionInfo;)V
            //   238: astore_2       
            //   239: aload_2        
            //   240: areturn        
            //   241: astore_2       
            //   242: goto            246
            //   245: astore_2       
            //   246: invokestatic    android/content/res/Resources.getSystem:()Landroid/content/res/Resources;
            //   249: ldc_w           "stat_sys_warning"
            //   252: ldc_w           "drawable"
            //   255: ldc_w           "android"
            //   258: invokevirtual   android/content/res/Resources.getIdentifier:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
            //   261: istore          7
            //   263: invokestatic    android/content/res/Resources.getSystem:()Landroid/content/res/Resources;
            //   266: ldc_w           "system_notification_accent_color"
            //   269: ldc_w           "color"
            //   272: ldc_w           "android"
            //   275: invokevirtual   android/content/res/Resources.getIdentifier:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
            //   278: istore          8
            //   280: new             Landroid/app/Notification$Builder;
            //   283: astore          6
            //   285: aload           6
            //   287: aload_0        
            //   288: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   291: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$500:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/Context;
            //   294: ldc_w           "ALR"
            //   297: invokespecial   android/app/Notification$Builder.<init>:(Landroid/content/Context;Ljava/lang/String;)V
            //   300: new             Landroid/app/Notification$BigTextStyle;
            //   303: astore          9
            //   305: aload           9
            //   307: invokespecial   android/app/Notification$BigTextStyle.<init>:()V
            //   310: aload           6
            //   312: aload           9
            //   314: invokevirtual   android/app/Notification$Builder.setStyle:(Landroid/app/Notification$Style;)Landroid/app/Notification$Builder;
            //   317: iload           7
            //   319: invokevirtual   android/app/Notification$Builder.setSmallIcon:(I)Landroid/app/Notification$Builder;
            //   322: lconst_0       
            //   323: invokevirtual   android/app/Notification$Builder.setWhen:(J)Landroid/app/Notification$Builder;
            //   326: iconst_0       
            //   327: invokevirtual   android/app/Notification$Builder.setShowWhen:(Z)Landroid/app/Notification$Builder;
            //   330: iconst_1       
            //   331: invokevirtual   android/app/Notification$Builder.setVisibility:(I)Landroid/app/Notification$Builder;
            //   334: aload_0        
            //   335: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   338: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$500:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/Context;
            //   341: iload           8
            //   343: invokevirtual   android/content/Context.getColor:(I)I
            //   346: invokevirtual   android/app/Notification$Builder.setColor:(I)Landroid/app/Notification$Builder;
            //   349: astore          9
            //   351: aload_0        
            //   352: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   355: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$1000:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/pm/PackageManager;
            //   358: aload_1        
            //   359: iconst_0       
            //   360: invokevirtual   android/content/pm/PackageManager.getServiceInfo:(Landroid/content/ComponentName;I)Landroid/content/pm/ServiceInfo;
            //   363: aload_0        
            //   364: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   367: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$1000:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/pm/PackageManager;
            //   370: invokevirtual   android/content/pm/ServiceInfo.loadLabel:(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
            //   373: invokeinterface java/lang/CharSequence.toString:()Ljava/lang/String;
            //   378: astore          6
            //   380: aload           6
            //   382: astore          4
            //   384: aload_2        
            //   385: invokevirtual   com/android/systemui/shared/plugins/VersionInfo$InvalidVersionException.isTooNew:()Z
            //   388: istore          10
            //   390: iload           10
            //   392: ifne            486
            //   395: new             Ljava/lang/StringBuilder;
            //   398: astore          6
            //   400: aload           6
            //   402: invokespecial   java/lang/StringBuilder.<init>:()V
            //   405: aload           6
            //   407: ldc_w           "Plugin \""
            //   410: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   413: pop            
            //   414: aload           6
            //   416: aload           4
            //   418: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   421: pop            
            //   422: aload           6
            //   424: ldc_w           "\" is too old"
            //   427: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   430: pop            
            //   431: aload           9
            //   433: aload           6
            //   435: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   438: invokevirtual   android/app/Notification$Builder.setContentTitle:(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
            //   441: astore          6
            //   443: new             Ljava/lang/StringBuilder;
            //   446: astore          4
            //   448: aload           4
            //   450: invokespecial   java/lang/StringBuilder.<init>:()V
            //   453: aload           4
            //   455: ldc_w           "Contact plugin developer to get an updated version.\n"
            //   458: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   461: pop            
            //   462: aload           4
            //   464: aload_2        
            //   465: invokevirtual   java/lang/RuntimeException.getMessage:()Ljava/lang/String;
            //   468: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   471: pop            
            //   472: aload           6
            //   474: aload           4
            //   476: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   479: invokevirtual   android/app/Notification$Builder.setContentText:(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
            //   482: pop            
            //   483: goto            574
            //   486: new             Ljava/lang/StringBuilder;
            //   489: astore          6
            //   491: aload           6
            //   493: invokespecial   java/lang/StringBuilder.<init>:()V
            //   496: aload           6
            //   498: ldc_w           "Plugin \""
            //   501: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   504: pop            
            //   505: aload           6
            //   507: aload           4
            //   509: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   512: pop            
            //   513: aload           6
            //   515: ldc_w           "\" is too new"
            //   518: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   521: pop            
            //   522: aload           9
            //   524: aload           6
            //   526: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   529: invokevirtual   android/app/Notification$Builder.setContentTitle:(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
            //   532: astore          6
            //   534: new             Ljava/lang/StringBuilder;
            //   537: astore          4
            //   539: aload           4
            //   541: invokespecial   java/lang/StringBuilder.<init>:()V
            //   544: aload           4
            //   546: ldc_w           "Check to see if an OTA is available.\n"
            //   549: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   552: pop            
            //   553: aload           4
            //   555: aload_2        
            //   556: invokevirtual   java/lang/RuntimeException.getMessage:()Ljava/lang/String;
            //   559: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   562: pop            
            //   563: aload           6
            //   565: aload           4
            //   567: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   570: invokevirtual   android/app/Notification$Builder.setContentText:(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
            //   573: pop            
            //   574: new             Landroid/content/Intent;
            //   577: astore_2       
            //   578: aload_2        
            //   579: ldc_w           "com.android.systemui.action.DISABLE_PLUGIN"
            //   582: invokespecial   android/content/Intent.<init>:(Ljava/lang/String;)V
            //   585: new             Ljava/lang/StringBuilder;
            //   588: astore          4
            //   590: aload           4
            //   592: invokespecial   java/lang/StringBuilder.<init>:()V
            //   595: aload           4
            //   597: ldc_w           "package://"
            //   600: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   603: pop            
            //   604: aload           4
            //   606: aload_1        
            //   607: invokevirtual   android/content/ComponentName.flattenToString:()Ljava/lang/String;
            //   610: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   613: pop            
            //   614: aload_2        
            //   615: aload           4
            //   617: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   620: invokestatic    android/net/Uri.parse:(Ljava/lang/String;)Landroid/net/Uri;
            //   623: invokevirtual   android/content/Intent.setData:(Landroid/net/Uri;)Landroid/content/Intent;
            //   626: astore_1       
            //   627: aload_0        
            //   628: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   631: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$500:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/Context;
            //   634: iconst_0       
            //   635: aload_1        
            //   636: iconst_0       
            //   637: invokestatic    android/app/PendingIntent.getBroadcast:(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;
            //   640: astore_2       
            //   641: new             Landroid/app/Notification$Action$Builder;
            //   644: astore_1       
            //   645: aload_1        
            //   646: aconst_null    
            //   647: ldc_w           "Disable plugin"
            //   650: aload_2        
            //   651: invokespecial   android/app/Notification$Action$Builder.<init>:(Landroid/graphics/drawable/Icon;Ljava/lang/CharSequence;Landroid/app/PendingIntent;)V
            //   654: aload           9
            //   656: aload_1        
            //   657: invokevirtual   android/app/Notification$Action$Builder.build:()Landroid/app/Notification$Action;
            //   660: invokevirtual   android/app/Notification$Builder.addAction:(Landroid/app/Notification$Action;)Landroid/app/Notification$Builder;
            //   663: pop            
            //   664: aload_0        
            //   665: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   668: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$500:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Landroid/content/Context;
            //   671: ldc_w           Landroid/app/NotificationManager;.class
            //   674: invokevirtual   android/content/Context.getSystemService:(Ljava/lang/Class;)Ljava/lang/Object;
            //   677: checkcast       Landroid/app/NotificationManager;
            //   680: bipush          6
            //   682: aload           9
            //   684: invokevirtual   android/app/Notification$Builder.build:()Landroid/app/Notification;
            //   687: invokevirtual   android/app/NotificationManager.notify:(ILandroid/app/Notification;)V
            //   690: new             Ljava/lang/StringBuilder;
            //   693: astore_1       
            //   694: aload_1        
            //   695: invokespecial   java/lang/StringBuilder.<init>:()V
            //   698: aload_1        
            //   699: ldc_w           "Plugin has invalid interface version "
            //   702: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   705: pop            
            //   706: aload_1        
            //   707: aload           5
            //   709: invokeinterface com/android/systemui/plugins/Plugin.getVersion:()I
            //   714: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
            //   717: pop            
            //   718: aload_1        
            //   719: ldc_w           ", expected "
            //   722: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   725: pop            
            //   726: aload_1        
            //   727: aload_0        
            //   728: getfield        com/android/systemui/shared/plugins/PluginInstanceManager$PluginHandler.this$0:Lcom/android/systemui/shared/plugins/PluginInstanceManager;
            //   731: invokestatic    com/android/systemui/shared/plugins/PluginInstanceManager.access$1300:(Lcom/android/systemui/shared/plugins/PluginInstanceManager;)Lcom/android/systemui/shared/plugins/VersionInfo;
            //   734: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   737: pop            
            //   738: ldc             "PluginInstanceManager"
            //   740: aload_1        
            //   741: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   744: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
            //   747: pop            
            //   748: aconst_null    
            //   749: areturn        
            //   750: astore_2       
            //   751: new             Ljava/lang/StringBuilder;
            //   754: dup            
            //   755: invokespecial   java/lang/StringBuilder.<init>:()V
            //   758: astore_1       
            //   759: aload_1        
            //   760: ldc_w           "Couldn't load plugin: "
            //   763: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   766: pop            
            //   767: aload_1        
            //   768: aload_3        
            //   769: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   772: pop            
            //   773: ldc             "PluginInstanceManager"
            //   775: aload_1        
            //   776: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   779: aload_2        
            //   780: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   783: pop            
            //   784: aconst_null    
            //   785: areturn        
            //   786: astore          6
            //   788: goto            384
            //    Signature:
            //  (Landroid/content/ComponentName;)Lcom/android/systemui/shared/plugins/PluginInstanceManager$PluginInfo<TT;>;
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                                                     
            //  -----  -----  -----  -----  -------------------------------------------------------------------------
            //  86     147    750    786    Any
            //  149    206    750    786    Any
            //  206    223    245    246    Lcom/android/systemui/shared/plugins/VersionInfo$InvalidVersionException;
            //  206    223    750    786    Any
            //  223    239    241    245    Lcom/android/systemui/shared/plugins/VersionInfo$InvalidVersionException;
            //  223    239    750    786    Any
            //  246    351    750    786    Any
            //  351    380    786    791    Landroid/content/pm/PackageManager$NameNotFoundException;
            //  351    380    750    786    Any
            //  384    390    750    786    Any
            //  395    483    750    786    Any
            //  486    574    750    786    Any
            //  574    748    750    786    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0384:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 1) {
                if (what != 2) {
                    if (what != 3) {
                        super.handleMessage(message);
                    }
                    else {
                        final String anObject = (String)message.obj;
                        for (int i = this.mPlugins.size() - 1; i >= 0; --i) {
                            final PluginInfo pluginInfo = this.mPlugins.get(i);
                            if (pluginInfo.mPackage.equals(anObject)) {
                                PluginInstanceManager.this.mMainHandler.obtainMessage(2, (Object)pluginInfo.mPlugin).sendToTarget();
                                this.mPlugins.remove(i);
                            }
                        }
                    }
                }
                else {
                    final String s = (String)message.obj;
                    if (PluginInstanceManager.this.mAllowMultiple || this.mPlugins.size() == 0) {
                        this.handleQueryPlugins(s);
                    }
                }
            }
            else {
                for (int j = this.mPlugins.size() - 1; j >= 0; --j) {
                    final PluginInfo pluginInfo2 = this.mPlugins.get(j);
                    PluginInstanceManager.this.mListener.onPluginDisconnected((Plugin)pluginInfo2.mPlugin);
                    final T mPlugin = pluginInfo2.mPlugin;
                    if (!(mPlugin instanceof PluginFragment)) {
                        ((Plugin)mPlugin).onDestroy();
                    }
                }
                this.mPlugins.clear();
                this.handleQueryPlugins(null);
            }
        }
    }
    
    static class PluginInfo<T>
    {
        private String mClass;
        String mPackage;
        T mPlugin;
        private final Context mPluginContext;
        private final VersionInfo mVersion;
        
        public PluginInfo(final String mPackage, final String mClass, final T mPlugin, final Context mPluginContext, final VersionInfo mVersion) {
            this.mPlugin = mPlugin;
            this.mClass = mClass;
            this.mPackage = mPackage;
            this.mPluginContext = mPluginContext;
            this.mVersion = mVersion;
        }
    }
}
