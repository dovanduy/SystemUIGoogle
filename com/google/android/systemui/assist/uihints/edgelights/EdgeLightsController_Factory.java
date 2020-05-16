// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights;

import android.view.ViewGroup;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class EdgeLightsController_Factory implements Factory<EdgeLightsController>
{
    private final Provider<Context> contextProvider;
    private final Provider<ViewGroup> parentProvider;
    
    public EdgeLightsController_Factory(final Provider<Context> contextProvider, final Provider<ViewGroup> parentProvider) {
        this.contextProvider = contextProvider;
        this.parentProvider = parentProvider;
    }
    
    public static EdgeLightsController_Factory create(final Provider<Context> provider, final Provider<ViewGroup> provider2) {
        return new EdgeLightsController_Factory(provider, provider2);
    }
    
    public static EdgeLightsController provideInstance(final Provider<Context> provider, final Provider<ViewGroup> provider2) {
        return new EdgeLightsController(provider.get(), provider2.get());
    }
    
    @Override
    public EdgeLightsController get() {
        return provideInstance(this.contextProvider, this.parentProvider);
    }
}
