// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideActivityManagerWrapperFactory implements Factory<ActivityManagerWrapper>
{
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideActivityManagerWrapperFactory(final DependencyProvider module) {
        this.module = module;
    }
    
    public static DependencyProvider_ProvideActivityManagerWrapperFactory create(final DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideActivityManagerWrapperFactory(dependencyProvider);
    }
    
    public static ActivityManagerWrapper provideInstance(final DependencyProvider dependencyProvider) {
        return proxyProvideActivityManagerWrapper(dependencyProvider);
    }
    
    public static ActivityManagerWrapper proxyProvideActivityManagerWrapper(final DependencyProvider dependencyProvider) {
        final ActivityManagerWrapper provideActivityManagerWrapper = dependencyProvider.provideActivityManagerWrapper();
        Preconditions.checkNotNull(provideActivityManagerWrapper, "Cannot return null from a non-@Nullable @Provides method");
        return provideActivityManagerWrapper;
    }
    
    @Override
    public ActivityManagerWrapper get() {
        return provideInstance(this.module);
    }
}
