// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.util.leak.LeakDetector;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideLeakDetectorFactory implements Factory<LeakDetector>
{
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideLeakDetectorFactory(final DependencyProvider module) {
        this.module = module;
    }
    
    public static DependencyProvider_ProvideLeakDetectorFactory create(final DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideLeakDetectorFactory(dependencyProvider);
    }
    
    public static LeakDetector provideInstance(final DependencyProvider dependencyProvider) {
        return proxyProvideLeakDetector(dependencyProvider);
    }
    
    public static LeakDetector proxyProvideLeakDetector(final DependencyProvider dependencyProvider) {
        final LeakDetector provideLeakDetector = dependencyProvider.provideLeakDetector();
        Preconditions.checkNotNull(provideLeakDetector, "Cannot return null from a non-@Nullable @Provides method");
        return provideLeakDetector;
    }
    
    @Override
    public LeakDetector get() {
        return provideInstance(this.module);
    }
}
