// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.accessibility;

import android.os.Handler;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class WindowMagnification_Factory implements Factory<WindowMagnification>
{
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    
    public WindowMagnification_Factory(final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }
    
    public static WindowMagnification_Factory create(final Provider<Context> provider, final Provider<Handler> provider2) {
        return new WindowMagnification_Factory(provider, provider2);
    }
    
    public static WindowMagnification provideInstance(final Provider<Context> provider, final Provider<Handler> provider2) {
        return new WindowMagnification(provider.get(), provider2.get());
    }
    
    @Override
    public WindowMagnification get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider);
    }
}
