// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.keyguard.KeyguardViewMediator;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.os.HandlerThread;
import com.android.systemui.Prefs;
import android.content.SharedPreferences;
import com.android.systemui.shared.plugins.PluginInitializer;
import com.android.systemui.shared.plugins.PluginManagerImpl;
import com.android.systemui.plugins.PluginInitializerImpl;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.internal.util.NotificationMessagingUtil;
import android.hardware.display.NightDisplayListener;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.util.leak.LeakDetector;
import android.app.INotificationManager$Stub;
import android.os.ServiceManager;
import android.app.INotificationManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.phone.ConfigurationControllerImpl;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.phone.AutoHideController;
import android.view.IWindowManager;
import android.os.Handler;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import android.content.Context;
import com.android.systemui.shared.system.ActivityManagerWrapper;

public class DependencyProvider
{
    public ActivityManagerWrapper provideActivityManagerWrapper() {
        return ActivityManagerWrapper.getInstance();
    }
    
    public AlwaysOnDisplayPolicy provideAlwaysOnDisplayPolicy(final Context context) {
        return new AlwaysOnDisplayPolicy(context);
    }
    
    public AmbientDisplayConfiguration provideAmbientDisplayConfiguration(final Context context) {
        return new AmbientDisplayConfiguration(context);
    }
    
    public AutoHideController provideAutoHideController(final Context context, final Handler handler, final IWindowManager windowManager) {
        return new AutoHideController(context, handler, windowManager);
    }
    
    public ConfigurationController provideConfigurationController(final Context context) {
        return new ConfigurationControllerImpl(context);
    }
    
    public DataSaverController provideDataSaverController(final NetworkController networkController) {
        return networkController.getDataSaverController();
    }
    
    public DevicePolicyManagerWrapper provideDevicePolicyManagerWrapper() {
        return DevicePolicyManagerWrapper.getInstance();
    }
    
    public DisplayMetrics provideDisplayMetrics(final Context context, final WindowManager windowManager) {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
    
    public Handler provideHandler() {
        return new Handler();
    }
    
    public INotificationManager provideINotificationManager() {
        return INotificationManager$Stub.asInterface(ServiceManager.getService("notification"));
    }
    
    public LeakDetector provideLeakDetector() {
        return LeakDetector.create();
    }
    
    public LockPatternUtils provideLockPatternUtils(final Context context) {
        return new LockPatternUtils(context);
    }
    
    public MetricsLogger provideMetricsLogger() {
        return new MetricsLogger();
    }
    
    public NavigationBarController provideNavigationBarController(final Context context, final Handler handler, final CommandQueue commandQueue) {
        return new NavigationBarController(context, handler, commandQueue);
    }
    
    public NightDisplayListener provideNightDisplayListener(final Context context, final Handler handler) {
        return new NightDisplayListener(context, handler);
    }
    
    public NotificationMessagingUtil provideNotificationMessagingUtil(final Context context) {
        return new NotificationMessagingUtil(context);
    }
    
    public PluginManager providePluginManager(final Context context) {
        return new PluginManagerImpl(context, new PluginInitializerImpl());
    }
    
    public SharedPreferences provideSharePreferences(final Context context) {
        return Prefs.get(context);
    }
    
    public Handler provideTimeTickHandler() {
        final HandlerThread handlerThread = new HandlerThread("TimeTick");
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }
    
    public LayoutInflater providerLayoutInflater(final Context context) {
        return LayoutInflater.from(context);
    }
    
    public Choreographer providesChoreographer() {
        return Choreographer.getInstance();
    }
    
    public ViewMediatorCallback providesViewMediatorCallback(final KeyguardViewMediator keyguardViewMediator) {
        return keyguardViewMediator.getViewMediatorCallback();
    }
}
