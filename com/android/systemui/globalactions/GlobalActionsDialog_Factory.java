// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import com.android.systemui.plugins.GlobalActions;
import android.os.Vibrator;
import android.os.UserManager;
import com.android.internal.logging.UiEventLogger;
import android.app.trust.TrustManager;
import android.telephony.TelephonyManager;
import android.telecom.TelecomManager;
import com.android.internal.statusbar.IStatusBarService;
import android.content.res.Resources;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.view.IWindowManager;
import android.service.dreams.IDreamManager;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.controls.ui.ControlsUiController;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.controller.ControlsController;
import android.content.Context;
import android.content.ContentResolver;
import android.net.ConnectivityManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.BlurUtils;
import java.util.concurrent.Executor;
import android.media.AudioManager;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GlobalActionsDialog_Factory implements Factory<GlobalActionsDialog>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<AudioManager> audioManagerProvider;
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<BlurUtils> blurUtilsProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<ConnectivityManager> connectivityManagerProvider;
    private final Provider<ContentResolver> contentResolverProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ControlsController> controlsControllerProvider;
    private final Provider<ControlsListingController> controlsListingControllerProvider;
    private final Provider<ControlsUiController> controlsUiControllerProvider;
    private final Provider<NotificationShadeDepthController> depthControllerProvider;
    private final Provider<DevicePolicyManager> devicePolicyManagerProvider;
    private final Provider<IActivityManager> iActivityManagerProvider;
    private final Provider<IDreamManager> iDreamManagerProvider;
    private final Provider<IWindowManager> iWindowManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;
    private final Provider<TelecomManager> telecomManagerProvider;
    private final Provider<TelephonyManager> telephonyManagerProvider;
    private final Provider<TrustManager> trustManagerProvider;
    private final Provider<UiEventLogger> uiEventLoggerProvider;
    private final Provider<UserManager> userManagerProvider;
    private final Provider<Vibrator> vibratorProvider;
    private final Provider<GlobalActions.GlobalActionsManager> windowManagerFuncsProvider;
    
    public GlobalActionsDialog_Factory(final Provider<Context> contextProvider, final Provider<GlobalActions.GlobalActionsManager> windowManagerFuncsProvider, final Provider<AudioManager> audioManagerProvider, final Provider<IDreamManager> iDreamManagerProvider, final Provider<DevicePolicyManager> devicePolicyManagerProvider, final Provider<LockPatternUtils> lockPatternUtilsProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<ConnectivityManager> connectivityManagerProvider, final Provider<TelephonyManager> telephonyManagerProvider, final Provider<ContentResolver> contentResolverProvider, final Provider<Vibrator> vibratorProvider, final Provider<Resources> resourcesProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<ActivityStarter> activityStarterProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<UserManager> userManagerProvider, final Provider<TrustManager> trustManagerProvider, final Provider<IActivityManager> iActivityManagerProvider, final Provider<TelecomManager> telecomManagerProvider, final Provider<MetricsLogger> metricsLoggerProvider, final Provider<NotificationShadeDepthController> depthControllerProvider, final Provider<SysuiColorExtractor> colorExtractorProvider, final Provider<IStatusBarService> statusBarServiceProvider, final Provider<BlurUtils> blurUtilsProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<ControlsUiController> controlsUiControllerProvider, final Provider<IWindowManager> iWindowManagerProvider, final Provider<Executor> backgroundExecutorProvider, final Provider<ControlsListingController> controlsListingControllerProvider, final Provider<ControlsController> controlsControllerProvider, final Provider<UiEventLogger> uiEventLoggerProvider) {
        this.contextProvider = contextProvider;
        this.windowManagerFuncsProvider = windowManagerFuncsProvider;
        this.audioManagerProvider = audioManagerProvider;
        this.iDreamManagerProvider = iDreamManagerProvider;
        this.devicePolicyManagerProvider = devicePolicyManagerProvider;
        this.lockPatternUtilsProvider = lockPatternUtilsProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.connectivityManagerProvider = connectivityManagerProvider;
        this.telephonyManagerProvider = telephonyManagerProvider;
        this.contentResolverProvider = contentResolverProvider;
        this.vibratorProvider = vibratorProvider;
        this.resourcesProvider = resourcesProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.userManagerProvider = userManagerProvider;
        this.trustManagerProvider = trustManagerProvider;
        this.iActivityManagerProvider = iActivityManagerProvider;
        this.telecomManagerProvider = telecomManagerProvider;
        this.metricsLoggerProvider = metricsLoggerProvider;
        this.depthControllerProvider = depthControllerProvider;
        this.colorExtractorProvider = colorExtractorProvider;
        this.statusBarServiceProvider = statusBarServiceProvider;
        this.blurUtilsProvider = blurUtilsProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.controlsUiControllerProvider = controlsUiControllerProvider;
        this.iWindowManagerProvider = iWindowManagerProvider;
        this.backgroundExecutorProvider = backgroundExecutorProvider;
        this.controlsListingControllerProvider = controlsListingControllerProvider;
        this.controlsControllerProvider = controlsControllerProvider;
        this.uiEventLoggerProvider = uiEventLoggerProvider;
    }
    
    public static GlobalActionsDialog_Factory create(final Provider<Context> provider, final Provider<GlobalActions.GlobalActionsManager> provider2, final Provider<AudioManager> provider3, final Provider<IDreamManager> provider4, final Provider<DevicePolicyManager> provider5, final Provider<LockPatternUtils> provider6, final Provider<BroadcastDispatcher> provider7, final Provider<ConnectivityManager> provider8, final Provider<TelephonyManager> provider9, final Provider<ContentResolver> provider10, final Provider<Vibrator> provider11, final Provider<Resources> provider12, final Provider<ConfigurationController> provider13, final Provider<ActivityStarter> provider14, final Provider<KeyguardStateController> provider15, final Provider<UserManager> provider16, final Provider<TrustManager> provider17, final Provider<IActivityManager> provider18, final Provider<TelecomManager> provider19, final Provider<MetricsLogger> provider20, final Provider<NotificationShadeDepthController> provider21, final Provider<SysuiColorExtractor> provider22, final Provider<IStatusBarService> provider23, final Provider<BlurUtils> provider24, final Provider<NotificationShadeWindowController> provider25, final Provider<ControlsUiController> provider26, final Provider<IWindowManager> provider27, final Provider<Executor> provider28, final Provider<ControlsListingController> provider29, final Provider<ControlsController> provider30, final Provider<UiEventLogger> provider31) {
        return new GlobalActionsDialog_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26, provider27, provider28, provider29, provider30, provider31);
    }
    
    public static GlobalActionsDialog provideInstance(final Provider<Context> provider, final Provider<GlobalActions.GlobalActionsManager> provider2, final Provider<AudioManager> provider3, final Provider<IDreamManager> provider4, final Provider<DevicePolicyManager> provider5, final Provider<LockPatternUtils> provider6, final Provider<BroadcastDispatcher> provider7, final Provider<ConnectivityManager> provider8, final Provider<TelephonyManager> provider9, final Provider<ContentResolver> provider10, final Provider<Vibrator> provider11, final Provider<Resources> provider12, final Provider<ConfigurationController> provider13, final Provider<ActivityStarter> provider14, final Provider<KeyguardStateController> provider15, final Provider<UserManager> provider16, final Provider<TrustManager> provider17, final Provider<IActivityManager> provider18, final Provider<TelecomManager> provider19, final Provider<MetricsLogger> provider20, final Provider<NotificationShadeDepthController> provider21, final Provider<SysuiColorExtractor> provider22, final Provider<IStatusBarService> provider23, final Provider<BlurUtils> provider24, final Provider<NotificationShadeWindowController> provider25, final Provider<ControlsUiController> provider26, final Provider<IWindowManager> provider27, final Provider<Executor> provider28, final Provider<ControlsListingController> provider29, final Provider<ControlsController> provider30, final Provider<UiEventLogger> provider31) {
        return new GlobalActionsDialog(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get(), provider17.get(), provider18.get(), provider19.get(), provider20.get(), provider21.get(), provider22.get(), provider23.get(), provider24.get(), provider25.get(), provider26.get(), provider27.get(), provider28.get(), provider29.get(), provider30.get(), provider31.get());
    }
    
    @Override
    public GlobalActionsDialog get() {
        return provideInstance(this.contextProvider, this.windowManagerFuncsProvider, this.audioManagerProvider, this.iDreamManagerProvider, this.devicePolicyManagerProvider, this.lockPatternUtilsProvider, this.broadcastDispatcherProvider, this.connectivityManagerProvider, this.telephonyManagerProvider, this.contentResolverProvider, this.vibratorProvider, this.resourcesProvider, this.configurationControllerProvider, this.activityStarterProvider, this.keyguardStateControllerProvider, this.userManagerProvider, this.trustManagerProvider, this.iActivityManagerProvider, this.telecomManagerProvider, this.metricsLoggerProvider, this.depthControllerProvider, this.colorExtractorProvider, this.statusBarServiceProvider, this.blurUtilsProvider, this.notificationShadeWindowControllerProvider, this.controlsUiControllerProvider, this.iWindowManagerProvider, this.backgroundExecutorProvider, this.controlsListingControllerProvider, this.controlsControllerProvider, this.uiEventLoggerProvider);
    }
}
