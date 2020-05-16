// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GlowController_Factory implements Factory<GlowController>
{
    private final Provider<Context> contextProvider;
    private final Provider<ViewGroup> parentProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;
    
    public GlowController_Factory(final Provider<Context> contextProvider, final Provider<ViewGroup> parentProvider, final Provider<TouchInsideHandler> touchInsideHandlerProvider) {
        this.contextProvider = contextProvider;
        this.parentProvider = parentProvider;
        this.touchInsideHandlerProvider = touchInsideHandlerProvider;
    }
    
    public static GlowController_Factory create(final Provider<Context> provider, final Provider<ViewGroup> provider2, final Provider<TouchInsideHandler> provider3) {
        return new GlowController_Factory(provider, provider2, provider3);
    }
    
    public static GlowController provideInstance(final Provider<Context> provider, final Provider<ViewGroup> provider2, final Provider<TouchInsideHandler> provider3) {
        return new GlowController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public GlowController get() {
        return provideInstance(this.contextProvider, this.parentProvider, this.touchInsideHandlerProvider);
    }
}
