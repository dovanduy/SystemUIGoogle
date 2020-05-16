// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wm;

import android.view.IWindowManager;
import android.os.Handler;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DisplayController_Factory implements Factory<DisplayController>
{
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<IWindowManager> wmServiceProvider;
    
    public DisplayController_Factory(final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider, final Provider<IWindowManager> wmServiceProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.wmServiceProvider = wmServiceProvider;
    }
    
    public static DisplayController_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<IWindowManager> provider3) {
        return new DisplayController_Factory(provider, provider2, provider3);
    }
    
    public static DisplayController provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<IWindowManager> provider3) {
        return new DisplayController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public DisplayController get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.wmServiceProvider);
    }
}
