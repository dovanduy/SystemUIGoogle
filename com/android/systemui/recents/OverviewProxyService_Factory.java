// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import com.android.systemui.model.SysUiState;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.pip.PipUI;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.stackdivider.Divider;
import java.util.Optional;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class OverviewProxyService_Factory implements Factory<OverviewProxyService>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Optional<Divider>> dividerOptionalProvider;
    private final Provider<NavigationBarController> navBarControllerProvider;
    private final Provider<NavigationModeController> navModeControllerProvider;
    private final Provider<PipUI> pipUIProvider;
    private final Provider<DeviceProvisionedController> provisionControllerProvider;
    private final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider;
    private final Provider<NotificationShadeWindowController> statusBarWinControllerProvider;
    private final Provider<SysUiState> sysUiStateProvider;
    
    public OverviewProxyService_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<DeviceProvisionedController> provisionControllerProvider, final Provider<NavigationBarController> navBarControllerProvider, final Provider<NavigationModeController> navModeControllerProvider, final Provider<NotificationShadeWindowController> statusBarWinControllerProvider, final Provider<SysUiState> sysUiStateProvider, final Provider<PipUI> pipUIProvider, final Provider<Optional<Divider>> dividerOptionalProvider, final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.provisionControllerProvider = provisionControllerProvider;
        this.navBarControllerProvider = navBarControllerProvider;
        this.navModeControllerProvider = navModeControllerProvider;
        this.statusBarWinControllerProvider = statusBarWinControllerProvider;
        this.sysUiStateProvider = sysUiStateProvider;
        this.pipUIProvider = pipUIProvider;
        this.dividerOptionalProvider = dividerOptionalProvider;
        this.statusBarOptionalLazyProvider = statusBarOptionalLazyProvider;
    }
    
    public static OverviewProxyService_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<DeviceProvisionedController> provider3, final Provider<NavigationBarController> provider4, final Provider<NavigationModeController> provider5, final Provider<NotificationShadeWindowController> provider6, final Provider<SysUiState> provider7, final Provider<PipUI> provider8, final Provider<Optional<Divider>> provider9, final Provider<Optional<Lazy<StatusBar>>> provider10) {
        return new OverviewProxyService_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
    }
    
    public static OverviewProxyService provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<DeviceProvisionedController> provider3, final Provider<NavigationBarController> provider4, final Provider<NavigationModeController> provider5, final Provider<NotificationShadeWindowController> provider6, final Provider<SysUiState> provider7, final Provider<PipUI> provider8, final Provider<Optional<Divider>> provider9, final Provider<Optional<Lazy<StatusBar>>> provider10) {
        return new OverviewProxyService(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get());
    }
    
    @Override
    public OverviewProxyService get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.provisionControllerProvider, this.navBarControllerProvider, this.navModeControllerProvider, this.statusBarWinControllerProvider, this.sysUiStateProvider, this.pipUIProvider, this.dividerOptionalProvider, this.statusBarOptionalLazyProvider);
    }
}
