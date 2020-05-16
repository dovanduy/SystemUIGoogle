// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist;

import dagger.internal.DoubleCheck;
import android.os.Handler;
import com.android.systemui.model.SysUiState;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.recents.OverviewProxyService;
import com.google.android.systemui.assist.uihints.NgaUiController;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler;
import com.android.internal.app.AssistUtils;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistManagerGoogle_Factory implements Factory<AssistManagerGoogle>
{
    private final Provider<AssistUtils> assistUtilsProvider;
    private final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> controllerProvider;
    private final Provider<AssistHandleBehaviorController> handleControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    private final Provider<NgaMessageHandler> ngaMessageHandlerProvider;
    private final Provider<NgaUiController> ngaUiControllerProvider;
    private final Provider<OpaEnabledDispatcher> opaEnabledDispatcherProvider;
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;
    private final Provider<PhoneStateMonitor> phoneStateMonitorProvider;
    private final Provider<SysUiState> sysUiStateProvider;
    private final Provider<Handler> uiHandlerProvider;
    
    public AssistManagerGoogle_Factory(final Provider<DeviceProvisionedController> controllerProvider, final Provider<Context> contextProvider, final Provider<AssistUtils> assistUtilsProvider, final Provider<AssistHandleBehaviorController> handleControllerProvider, final Provider<NgaUiController> ngaUiControllerProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<PhoneStateMonitor> phoneStateMonitorProvider, final Provider<OverviewProxyService> overviewProxyServiceProvider, final Provider<OpaEnabledDispatcher> opaEnabledDispatcherProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<NavigationModeController> navigationModeControllerProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider, final Provider<NgaMessageHandler> ngaMessageHandlerProvider, final Provider<SysUiState> sysUiStateProvider, final Provider<Handler> uiHandlerProvider) {
        this.controllerProvider = controllerProvider;
        this.contextProvider = contextProvider;
        this.assistUtilsProvider = assistUtilsProvider;
        this.handleControllerProvider = handleControllerProvider;
        this.ngaUiControllerProvider = ngaUiControllerProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.phoneStateMonitorProvider = phoneStateMonitorProvider;
        this.overviewProxyServiceProvider = overviewProxyServiceProvider;
        this.opaEnabledDispatcherProvider = opaEnabledDispatcherProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.navigationModeControllerProvider = navigationModeControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.assistantPresenceHandlerProvider = assistantPresenceHandlerProvider;
        this.ngaMessageHandlerProvider = ngaMessageHandlerProvider;
        this.sysUiStateProvider = sysUiStateProvider;
        this.uiHandlerProvider = uiHandlerProvider;
    }
    
    public static AssistManagerGoogle_Factory create(final Provider<DeviceProvisionedController> provider, final Provider<Context> provider2, final Provider<AssistUtils> provider3, final Provider<AssistHandleBehaviorController> provider4, final Provider<NgaUiController> provider5, final Provider<CommandQueue> provider6, final Provider<BroadcastDispatcher> provider7, final Provider<PhoneStateMonitor> provider8, final Provider<OverviewProxyService> provider9, final Provider<OpaEnabledDispatcher> provider10, final Provider<KeyguardUpdateMonitor> provider11, final Provider<NavigationModeController> provider12, final Provider<ConfigurationController> provider13, final Provider<AssistantPresenceHandler> provider14, final Provider<NgaMessageHandler> provider15, final Provider<SysUiState> provider16, final Provider<Handler> provider17) {
        return new AssistManagerGoogle_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17);
    }
    
    public static AssistManagerGoogle provideInstance(final Provider<DeviceProvisionedController> provider, final Provider<Context> provider2, final Provider<AssistUtils> provider3, final Provider<AssistHandleBehaviorController> provider4, final Provider<NgaUiController> provider5, final Provider<CommandQueue> provider6, final Provider<BroadcastDispatcher> provider7, final Provider<PhoneStateMonitor> provider8, final Provider<OverviewProxyService> provider9, final Provider<OpaEnabledDispatcher> provider10, final Provider<KeyguardUpdateMonitor> provider11, final Provider<NavigationModeController> provider12, final Provider<ConfigurationController> provider13, final Provider<AssistantPresenceHandler> provider14, final Provider<NgaMessageHandler> provider15, final Provider<SysUiState> provider16, final Provider<Handler> provider17) {
        return new AssistManagerGoogle(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), DoubleCheck.lazy(provider16), provider17.get());
    }
    
    @Override
    public AssistManagerGoogle get() {
        return provideInstance(this.controllerProvider, this.contextProvider, this.assistUtilsProvider, this.handleControllerProvider, this.ngaUiControllerProvider, this.commandQueueProvider, this.broadcastDispatcherProvider, this.phoneStateMonitorProvider, this.overviewProxyServiceProvider, this.opaEnabledDispatcherProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, this.configurationControllerProvider, this.assistantPresenceHandlerProvider, this.ngaMessageHandlerProvider, this.sysUiStateProvider, this.uiHandlerProvider);
    }
}
