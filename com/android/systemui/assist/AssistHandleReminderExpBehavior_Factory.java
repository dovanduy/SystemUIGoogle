// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.DoubleCheck;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.model.SysUiState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.recents.OverviewProxyService;
import android.os.Handler;
import androidx.slice.Clock;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.BootCompleteCache;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistHandleReminderExpBehavior_Factory implements Factory<AssistHandleReminderExpBehavior>
{
    private final Provider<ActivityManagerWrapper> activityManagerWrapperProvider;
    private final Provider<BootCompleteCache> bootCompleteCacheProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Clock> clockProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;
    private final Provider<PackageManagerWrapper> packageManagerWrapperProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<SysUiState> sysUiFlagContainerProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    
    public AssistHandleReminderExpBehavior_Factory(final Provider<Clock> clockProvider, final Provider<Handler> handlerProvider, final Provider<DeviceConfigHelper> deviceConfigHelperProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<ActivityManagerWrapper> activityManagerWrapperProvider, final Provider<OverviewProxyService> overviewProxyServiceProvider, final Provider<SysUiState> sysUiFlagContainerProvider, final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, final Provider<PackageManagerWrapper> packageManagerWrapperProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<BootCompleteCache> bootCompleteCacheProvider) {
        this.clockProvider = clockProvider;
        this.handlerProvider = handlerProvider;
        this.deviceConfigHelperProvider = deviceConfigHelperProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.activityManagerWrapperProvider = activityManagerWrapperProvider;
        this.overviewProxyServiceProvider = overviewProxyServiceProvider;
        this.sysUiFlagContainerProvider = sysUiFlagContainerProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
        this.packageManagerWrapperProvider = packageManagerWrapperProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.bootCompleteCacheProvider = bootCompleteCacheProvider;
    }
    
    public static AssistHandleReminderExpBehavior_Factory create(final Provider<Clock> provider, final Provider<Handler> provider2, final Provider<DeviceConfigHelper> provider3, final Provider<StatusBarStateController> provider4, final Provider<ActivityManagerWrapper> provider5, final Provider<OverviewProxyService> provider6, final Provider<SysUiState> provider7, final Provider<WakefulnessLifecycle> provider8, final Provider<PackageManagerWrapper> provider9, final Provider<BroadcastDispatcher> provider10, final Provider<BootCompleteCache> provider11) {
        return new AssistHandleReminderExpBehavior_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
    }
    
    public static AssistHandleReminderExpBehavior provideInstance(final Provider<Clock> provider, final Provider<Handler> provider2, final Provider<DeviceConfigHelper> provider3, final Provider<StatusBarStateController> provider4, final Provider<ActivityManagerWrapper> provider5, final Provider<OverviewProxyService> provider6, final Provider<SysUiState> provider7, final Provider<WakefulnessLifecycle> provider8, final Provider<PackageManagerWrapper> provider9, final Provider<BroadcastDispatcher> provider10, final Provider<BootCompleteCache> provider11) {
        return new AssistHandleReminderExpBehavior(provider.get(), provider2.get(), provider3.get(), DoubleCheck.lazy(provider4), DoubleCheck.lazy(provider5), DoubleCheck.lazy(provider6), DoubleCheck.lazy(provider7), DoubleCheck.lazy(provider8), DoubleCheck.lazy(provider9), DoubleCheck.lazy(provider10), DoubleCheck.lazy(provider11));
    }
    
    @Override
    public AssistHandleReminderExpBehavior get() {
        return provideInstance(this.clockProvider, this.handlerProvider, this.deviceConfigHelperProvider, this.statusBarStateControllerProvider, this.activityManagerWrapperProvider, this.overviewProxyServiceProvider, this.sysUiFlagContainerProvider, this.wakefulnessLifecycleProvider, this.packageManagerWrapperProvider, this.broadcastDispatcherProvider, this.bootCompleteCacheProvider);
    }
}
