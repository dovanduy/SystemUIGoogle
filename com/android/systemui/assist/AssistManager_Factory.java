// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.DoubleCheck;
import com.android.systemui.model.SysUiState;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.internal.app.AssistUtils;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistManager_Factory implements Factory<AssistManager>
{
    private final Provider<AssistUtils> assistUtilsProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> controllerProvider;
    private final Provider<AssistHandleBehaviorController> handleControllerProvider;
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;
    private final Provider<PhoneStateMonitor> phoneStateMonitorProvider;
    private final Provider<SysUiState> sysUiStateProvider;
    
    public AssistManager_Factory(final Provider<DeviceProvisionedController> controllerProvider, final Provider<Context> contextProvider, final Provider<AssistUtils> assistUtilsProvider, final Provider<AssistHandleBehaviorController> handleControllerProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<PhoneStateMonitor> phoneStateMonitorProvider, final Provider<OverviewProxyService> overviewProxyServiceProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<SysUiState> sysUiStateProvider) {
        this.controllerProvider = controllerProvider;
        this.contextProvider = contextProvider;
        this.assistUtilsProvider = assistUtilsProvider;
        this.handleControllerProvider = handleControllerProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.phoneStateMonitorProvider = phoneStateMonitorProvider;
        this.overviewProxyServiceProvider = overviewProxyServiceProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.sysUiStateProvider = sysUiStateProvider;
    }
    
    public static AssistManager_Factory create(final Provider<DeviceProvisionedController> provider, final Provider<Context> provider2, final Provider<AssistUtils> provider3, final Provider<AssistHandleBehaviorController> provider4, final Provider<CommandQueue> provider5, final Provider<PhoneStateMonitor> provider6, final Provider<OverviewProxyService> provider7, final Provider<ConfigurationController> provider8, final Provider<SysUiState> provider9) {
        return new AssistManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static AssistManager provideInstance(final Provider<DeviceProvisionedController> provider, final Provider<Context> provider2, final Provider<AssistUtils> provider3, final Provider<AssistHandleBehaviorController> provider4, final Provider<CommandQueue> provider5, final Provider<PhoneStateMonitor> provider6, final Provider<OverviewProxyService> provider7, final Provider<ConfigurationController> provider8, final Provider<SysUiState> provider9) {
        return new AssistManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), DoubleCheck.lazy(provider9));
    }
    
    @Override
    public AssistManager get() {
        return provideInstance(this.controllerProvider, this.contextProvider, this.assistUtilsProvider, this.handleControllerProvider, this.commandQueueProvider, this.phoneStateMonitorProvider, this.overviewProxyServiceProvider, this.configurationControllerProvider, this.sysUiStateProvider);
    }
}
