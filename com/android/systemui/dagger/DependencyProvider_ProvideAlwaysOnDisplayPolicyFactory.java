// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory implements Factory<AlwaysOnDisplayPolicy>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory(final DependencyProvider module, final Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }
    
    public static DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return new DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory(dependencyProvider, provider);
    }
    
    public static AlwaysOnDisplayPolicy provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return proxyProvideAlwaysOnDisplayPolicy(dependencyProvider, provider.get());
    }
    
    public static AlwaysOnDisplayPolicy proxyProvideAlwaysOnDisplayPolicy(final DependencyProvider dependencyProvider, final Context context) {
        final AlwaysOnDisplayPolicy provideAlwaysOnDisplayPolicy = dependencyProvider.provideAlwaysOnDisplayPolicy(context);
        Preconditions.checkNotNull(provideAlwaysOnDisplayPolicy, "Cannot return null from a non-@Nullable @Provides method");
        return provideAlwaysOnDisplayPolicy;
    }
    
    @Override
    public AlwaysOnDisplayPolicy get() {
        return provideInstance(this.module, this.contextProvider);
    }
}
