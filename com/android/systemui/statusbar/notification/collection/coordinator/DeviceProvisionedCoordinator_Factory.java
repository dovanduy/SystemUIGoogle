// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import android.content.pm.IPackageManager;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DeviceProvisionedCoordinator_Factory implements Factory<DeviceProvisionedCoordinator>
{
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<IPackageManager> packageManagerProvider;
    
    public DeviceProvisionedCoordinator_Factory(final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, final Provider<IPackageManager> packageManagerProvider) {
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.packageManagerProvider = packageManagerProvider;
    }
    
    public static DeviceProvisionedCoordinator_Factory create(final Provider<DeviceProvisionedController> provider, final Provider<IPackageManager> provider2) {
        return new DeviceProvisionedCoordinator_Factory(provider, provider2);
    }
    
    public static DeviceProvisionedCoordinator provideInstance(final Provider<DeviceProvisionedController> provider, final Provider<IPackageManager> provider2) {
        return new DeviceProvisionedCoordinator(provider.get(), provider2.get());
    }
    
    @Override
    public DeviceProvisionedCoordinator get() {
        return provideInstance(this.deviceProvisionedControllerProvider, this.packageManagerProvider);
    }
}
