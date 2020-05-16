// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.view.Choreographer;
import dagger.internal.Factory;

public final class DependencyProvider_ProvidesChoreographerFactory implements Factory<Choreographer>
{
    private final DependencyProvider module;
    
    public DependencyProvider_ProvidesChoreographerFactory(final DependencyProvider module) {
        this.module = module;
    }
    
    public static DependencyProvider_ProvidesChoreographerFactory create(final DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvidesChoreographerFactory(dependencyProvider);
    }
    
    public static Choreographer provideInstance(final DependencyProvider dependencyProvider) {
        return proxyProvidesChoreographer(dependencyProvider);
    }
    
    public static Choreographer proxyProvidesChoreographer(final DependencyProvider dependencyProvider) {
        final Choreographer providesChoreographer = dependencyProvider.providesChoreographer();
        Preconditions.checkNotNull(providesChoreographer, "Cannot return null from a non-@Nullable @Provides method");
        return providesChoreographer;
    }
    
    @Override
    public Choreographer get() {
        return provideInstance(this.module);
    }
}
