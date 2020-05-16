// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.content.SharedPreferences;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideSharePreferencesFactory implements Factory<SharedPreferences>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideSharePreferencesFactory(final DependencyProvider module, final Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }
    
    public static DependencyProvider_ProvideSharePreferencesFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return new DependencyProvider_ProvideSharePreferencesFactory(dependencyProvider, provider);
    }
    
    public static SharedPreferences provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return proxyProvideSharePreferences(dependencyProvider, provider.get());
    }
    
    public static SharedPreferences proxyProvideSharePreferences(final DependencyProvider dependencyProvider, final Context context) {
        final SharedPreferences provideSharePreferences = dependencyProvider.provideSharePreferences(context);
        Preconditions.checkNotNull(provideSharePreferences, "Cannot return null from a non-@Nullable @Provides method");
        return provideSharePreferences;
    }
    
    @Override
    public SharedPreferences get() {
        return provideInstance(this.module, this.contextProvider);
    }
}
