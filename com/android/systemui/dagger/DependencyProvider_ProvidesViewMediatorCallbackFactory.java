// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.keyguard.KeyguardViewMediator;
import javax.inject.Provider;
import com.android.keyguard.ViewMediatorCallback;
import dagger.internal.Factory;

public final class DependencyProvider_ProvidesViewMediatorCallbackFactory implements Factory<ViewMediatorCallback>
{
    private final DependencyProvider module;
    private final Provider<KeyguardViewMediator> viewMediatorProvider;
    
    public DependencyProvider_ProvidesViewMediatorCallbackFactory(final DependencyProvider module, final Provider<KeyguardViewMediator> viewMediatorProvider) {
        this.module = module;
        this.viewMediatorProvider = viewMediatorProvider;
    }
    
    public static DependencyProvider_ProvidesViewMediatorCallbackFactory create(final DependencyProvider dependencyProvider, final Provider<KeyguardViewMediator> provider) {
        return new DependencyProvider_ProvidesViewMediatorCallbackFactory(dependencyProvider, provider);
    }
    
    public static ViewMediatorCallback provideInstance(final DependencyProvider dependencyProvider, final Provider<KeyguardViewMediator> provider) {
        return proxyProvidesViewMediatorCallback(dependencyProvider, provider.get());
    }
    
    public static ViewMediatorCallback proxyProvidesViewMediatorCallback(final DependencyProvider dependencyProvider, final KeyguardViewMediator keyguardViewMediator) {
        final ViewMediatorCallback providesViewMediatorCallback = dependencyProvider.providesViewMediatorCallback(keyguardViewMediator);
        Preconditions.checkNotNull(providesViewMediatorCallback, "Cannot return null from a non-@Nullable @Provides method");
        return providesViewMediatorCallback;
    }
    
    @Override
    public ViewMediatorCallback get() {
        return provideInstance(this.module, this.viewMediatorProvider);
    }
}
