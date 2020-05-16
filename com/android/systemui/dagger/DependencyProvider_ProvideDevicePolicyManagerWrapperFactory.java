// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideDevicePolicyManagerWrapperFactory implements Factory<DevicePolicyManagerWrapper>
{
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideDevicePolicyManagerWrapperFactory(final DependencyProvider module) {
        this.module = module;
    }
    
    public static DependencyProvider_ProvideDevicePolicyManagerWrapperFactory create(final DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideDevicePolicyManagerWrapperFactory(dependencyProvider);
    }
    
    public static DevicePolicyManagerWrapper provideInstance(final DependencyProvider dependencyProvider) {
        return proxyProvideDevicePolicyManagerWrapper(dependencyProvider);
    }
    
    public static DevicePolicyManagerWrapper proxyProvideDevicePolicyManagerWrapper(final DependencyProvider dependencyProvider) {
        final DevicePolicyManagerWrapper provideDevicePolicyManagerWrapper = dependencyProvider.provideDevicePolicyManagerWrapper();
        Preconditions.checkNotNull(provideDevicePolicyManagerWrapper, "Cannot return null from a non-@Nullable @Provides method");
        return provideDevicePolicyManagerWrapper;
    }
    
    @Override
    public DevicePolicyManagerWrapper get() {
        return provideInstance(this.module);
    }
}
