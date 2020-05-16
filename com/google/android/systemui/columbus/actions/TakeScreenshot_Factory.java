// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import android.os.Handler;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TakeScreenshot_Factory implements Factory<TakeScreenshot>
{
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    
    public TakeScreenshot_Factory(final Provider<Context> contextProvider, final Provider<Handler> handlerProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
    }
    
    public static TakeScreenshot_Factory create(final Provider<Context> provider, final Provider<Handler> provider2) {
        return new TakeScreenshot_Factory(provider, provider2);
    }
    
    public static TakeScreenshot provideInstance(final Provider<Context> provider, final Provider<Handler> provider2) {
        return new TakeScreenshot(provider.get(), provider2.get());
    }
    
    @Override
    public TakeScreenshot get() {
        return provideInstance(this.contextProvider, this.handlerProvider);
    }
}
