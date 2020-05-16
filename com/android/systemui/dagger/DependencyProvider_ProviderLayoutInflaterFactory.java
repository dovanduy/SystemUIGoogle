// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.view.LayoutInflater;
import dagger.internal.Factory;

public final class DependencyProvider_ProviderLayoutInflaterFactory implements Factory<LayoutInflater>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProviderLayoutInflaterFactory(final DependencyProvider module, final Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }
    
    public static DependencyProvider_ProviderLayoutInflaterFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return new DependencyProvider_ProviderLayoutInflaterFactory(dependencyProvider, provider);
    }
    
    public static LayoutInflater provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return proxyProviderLayoutInflater(dependencyProvider, provider.get());
    }
    
    public static LayoutInflater proxyProviderLayoutInflater(final DependencyProvider dependencyProvider, final Context context) {
        final LayoutInflater providerLayoutInflater = dependencyProvider.providerLayoutInflater(context);
        Preconditions.checkNotNull(providerLayoutInflater, "Cannot return null from a non-@Nullable @Provides method");
        return providerLayoutInflater;
    }
    
    @Override
    public LayoutInflater get() {
        return provideInstance(this.module, this.contextProvider);
    }
}
