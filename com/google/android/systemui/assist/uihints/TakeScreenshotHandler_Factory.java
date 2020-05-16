// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TakeScreenshotHandler_Factory implements Factory<TakeScreenshotHandler>
{
    private final Provider<Context> contextProvider;
    
    public TakeScreenshotHandler_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static TakeScreenshotHandler_Factory create(final Provider<Context> provider) {
        return new TakeScreenshotHandler_Factory(provider);
    }
    
    public static TakeScreenshotHandler provideInstance(final Provider<Context> provider) {
        return new TakeScreenshotHandler(provider.get());
    }
    
    @Override
    public TakeScreenshotHandler get() {
        return provideInstance(this.contextProvider);
    }
}
