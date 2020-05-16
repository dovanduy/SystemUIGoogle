// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard.dagger;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.keyguard.KeyguardUpdateMonitor;
import java.util.concurrent.Executor;
import android.app.trust.TrustManager;
import com.android.keyguard.KeyguardViewController;
import android.os.PowerManager;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import com.android.systemui.keyguard.KeyguardViewMediator;
import dagger.internal.Factory;

public final class KeyguardModule_NewKeyguardViewMediatorFactory implements Factory<KeyguardViewMediator>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> deviceConfigProvider;
    private final Provider<DismissCallbackRegistry> dismissCallbackRegistryProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<KeyguardViewController> statusBarKeyguardViewManagerLazyProvider;
    private final Provider<TrustManager> trustManagerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    private final Provider<KeyguardUpdateMonitor> updateMonitorProvider;
    
    public KeyguardModule_NewKeyguardViewMediatorFactory(final Provider<Context> contextProvider, final Provider<FalsingManager> falsingManagerProvider, final Provider<LockPatternUtils> lockPatternUtilsProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<KeyguardViewController> statusBarKeyguardViewManagerLazyProvider, final Provider<DismissCallbackRegistry> dismissCallbackRegistryProvider, final Provider<KeyguardUpdateMonitor> updateMonitorProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<PowerManager> powerManagerProvider, final Provider<TrustManager> trustManagerProvider, final Provider<Executor> uiBgExecutorProvider, final Provider<DeviceConfigProxy> deviceConfigProvider, final Provider<NavigationModeController> navigationModeControllerProvider) {
        this.contextProvider = contextProvider;
        this.falsingManagerProvider = falsingManagerProvider;
        this.lockPatternUtilsProvider = lockPatternUtilsProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.statusBarKeyguardViewManagerLazyProvider = statusBarKeyguardViewManagerLazyProvider;
        this.dismissCallbackRegistryProvider = dismissCallbackRegistryProvider;
        this.updateMonitorProvider = updateMonitorProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.trustManagerProvider = trustManagerProvider;
        this.uiBgExecutorProvider = uiBgExecutorProvider;
        this.deviceConfigProvider = deviceConfigProvider;
        this.navigationModeControllerProvider = navigationModeControllerProvider;
    }
    
    public static KeyguardModule_NewKeyguardViewMediatorFactory create(final Provider<Context> provider, final Provider<FalsingManager> provider2, final Provider<LockPatternUtils> provider3, final Provider<BroadcastDispatcher> provider4, final Provider<NotificationShadeWindowController> provider5, final Provider<KeyguardViewController> provider6, final Provider<DismissCallbackRegistry> provider7, final Provider<KeyguardUpdateMonitor> provider8, final Provider<DumpManager> provider9, final Provider<PowerManager> provider10, final Provider<TrustManager> provider11, final Provider<Executor> provider12, final Provider<DeviceConfigProxy> provider13, final Provider<NavigationModeController> provider14) {
        return new KeyguardModule_NewKeyguardViewMediatorFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14);
    }
    
    public static KeyguardViewMediator provideInstance(final Provider<Context> provider, final Provider<FalsingManager> provider2, final Provider<LockPatternUtils> provider3, final Provider<BroadcastDispatcher> provider4, final Provider<NotificationShadeWindowController> provider5, final Provider<KeyguardViewController> provider6, final Provider<DismissCallbackRegistry> provider7, final Provider<KeyguardUpdateMonitor> provider8, final Provider<DumpManager> provider9, final Provider<PowerManager> provider10, final Provider<TrustManager> provider11, final Provider<Executor> provider12, final Provider<DeviceConfigProxy> provider13, final Provider<NavigationModeController> provider14) {
        return proxyNewKeyguardViewMediator(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), DoubleCheck.lazy(provider6), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get());
    }
    
    public static KeyguardViewMediator proxyNewKeyguardViewMediator(final Context context, final FalsingManager falsingManager, final LockPatternUtils lockPatternUtils, final BroadcastDispatcher broadcastDispatcher, final NotificationShadeWindowController notificationShadeWindowController, final Lazy<KeyguardViewController> lazy, final DismissCallbackRegistry dismissCallbackRegistry, final KeyguardUpdateMonitor keyguardUpdateMonitor, final DumpManager dumpManager, final PowerManager powerManager, final TrustManager trustManager, final Executor executor, final DeviceConfigProxy deviceConfigProxy, final NavigationModeController navigationModeController) {
        final KeyguardViewMediator keyguardViewMediator = KeyguardModule.newKeyguardViewMediator(context, falsingManager, lockPatternUtils, broadcastDispatcher, notificationShadeWindowController, lazy, dismissCallbackRegistry, keyguardUpdateMonitor, dumpManager, powerManager, trustManager, executor, deviceConfigProxy, navigationModeController);
        Preconditions.checkNotNull(keyguardViewMediator, "Cannot return null from a non-@Nullable @Provides method");
        return keyguardViewMediator;
    }
    
    @Override
    public KeyguardViewMediator get() {
        return provideInstance(this.contextProvider, this.falsingManagerProvider, this.lockPatternUtilsProvider, this.broadcastDispatcherProvider, this.notificationShadeWindowControllerProvider, this.statusBarKeyguardViewManagerLazyProvider, this.dismissCallbackRegistryProvider, this.updateMonitorProvider, this.dumpManagerProvider, this.powerManagerProvider, this.trustManagerProvider, this.uiBgExecutorProvider, this.deviceConfigProvider, this.navigationModeControllerProvider);
    }
}
