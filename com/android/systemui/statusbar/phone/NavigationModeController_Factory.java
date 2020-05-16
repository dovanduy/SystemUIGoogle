// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.concurrent.Executor;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NavigationModeController_Factory implements Factory<NavigationModeController>
{
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    
    public NavigationModeController_Factory(final Provider<Context> contextProvider, final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, final Provider<Executor> uiBgExecutorProvider) {
        this.contextProvider = contextProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.uiBgExecutorProvider = uiBgExecutorProvider;
    }
    
    public static NavigationModeController_Factory create(final Provider<Context> provider, final Provider<DeviceProvisionedController> provider2, final Provider<Executor> provider3) {
        return new NavigationModeController_Factory(provider, provider2, provider3);
    }
    
    public static NavigationModeController provideInstance(final Provider<Context> provider, final Provider<DeviceProvisionedController> provider2, final Provider<Executor> provider3) {
        return new NavigationModeController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public NavigationModeController get() {
        return provideInstance(this.contextProvider, this.deviceProvisionedControllerProvider, this.uiBgExecutorProvider);
    }
}
