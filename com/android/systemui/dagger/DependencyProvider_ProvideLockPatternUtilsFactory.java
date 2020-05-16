// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import com.android.internal.widget.LockPatternUtils;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideLockPatternUtilsFactory implements Factory<LockPatternUtils>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideLockPatternUtilsFactory(final DependencyProvider module, final Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }
    
    public static DependencyProvider_ProvideLockPatternUtilsFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return new DependencyProvider_ProvideLockPatternUtilsFactory(dependencyProvider, provider);
    }
    
    public static LockPatternUtils provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return proxyProvideLockPatternUtils(dependencyProvider, provider.get());
    }
    
    public static LockPatternUtils proxyProvideLockPatternUtils(final DependencyProvider dependencyProvider, final Context context) {
        final LockPatternUtils provideLockPatternUtils = dependencyProvider.provideLockPatternUtils(context);
        Preconditions.checkNotNull(provideLockPatternUtils, "Cannot return null from a non-@Nullable @Provides method");
        return provideLockPatternUtils;
    }
    
    @Override
    public LockPatternUtils get() {
        return provideInstance(this.module, this.contextProvider);
    }
}
