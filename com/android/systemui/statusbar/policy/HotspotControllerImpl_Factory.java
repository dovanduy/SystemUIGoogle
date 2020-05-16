// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class HotspotControllerImpl_Factory implements Factory<HotspotControllerImpl>
{
    private final Provider<Handler> backgroundHandlerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    
    public HotspotControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider, final Provider<Handler> backgroundHandlerProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.backgroundHandlerProvider = backgroundHandlerProvider;
    }
    
    public static HotspotControllerImpl_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<Handler> provider3) {
        return new HotspotControllerImpl_Factory(provider, provider2, provider3);
    }
    
    public static HotspotControllerImpl provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<Handler> provider3) {
        return new HotspotControllerImpl(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public HotspotControllerImpl get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.backgroundHandlerProvider);
    }
}
