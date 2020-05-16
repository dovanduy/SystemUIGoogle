// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.util.wakelock.WakeLock;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.internal.app.IBatteryStats;
import com.android.systemui.dock.DockManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardIndicationController_Factory implements Factory<KeyguardIndicationController>
{
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<IBatteryStats> iBatteryStatsProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<WakeLock.Builder> wakeLockBuilderProvider;
    
    public KeyguardIndicationController_Factory(final Provider<Context> contextProvider, final Provider<WakeLock.Builder> wakeLockBuilderProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<DockManager> dockManagerProvider, final Provider<IBatteryStats> iBatteryStatsProvider) {
        this.contextProvider = contextProvider;
        this.wakeLockBuilderProvider = wakeLockBuilderProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.iBatteryStatsProvider = iBatteryStatsProvider;
    }
    
    public static KeyguardIndicationController_Factory create(final Provider<Context> provider, final Provider<WakeLock.Builder> provider2, final Provider<KeyguardStateController> provider3, final Provider<StatusBarStateController> provider4, final Provider<KeyguardUpdateMonitor> provider5, final Provider<DockManager> provider6, final Provider<IBatteryStats> provider7) {
        return new KeyguardIndicationController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
    }
    
    public static KeyguardIndicationController provideInstance(final Provider<Context> provider, final Provider<WakeLock.Builder> provider2, final Provider<KeyguardStateController> provider3, final Provider<StatusBarStateController> provider4, final Provider<KeyguardUpdateMonitor> provider5, final Provider<DockManager> provider6, final Provider<IBatteryStats> provider7) {
        return new KeyguardIndicationController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get());
    }
    
    @Override
    public KeyguardIndicationController get() {
        return provideInstance(this.contextProvider, this.wakeLockBuilderProvider, this.keyguardStateControllerProvider, this.statusBarStateControllerProvider, this.keyguardUpdateMonitorProvider, this.dockManagerProvider, this.iBatteryStatsProvider);
    }
}
