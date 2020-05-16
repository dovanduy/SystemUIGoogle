// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class FeatureFlags_Factory implements Factory<FeatureFlags>
{
    private final Provider<Executor> executorProvider;
    
    public FeatureFlags_Factory(final Provider<Executor> executorProvider) {
        this.executorProvider = executorProvider;
    }
    
    public static FeatureFlags_Factory create(final Provider<Executor> provider) {
        return new FeatureFlags_Factory(provider);
    }
    
    public static FeatureFlags provideInstance(final Provider<Executor> provider) {
        return new FeatureFlags(provider.get());
    }
    
    @Override
    public FeatureFlags get() {
        return provideInstance(this.executorProvider);
    }
}
