// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ColorChangeHandler_Factory implements Factory<ColorChangeHandler>
{
    private final Provider<Context> contextProvider;
    
    public ColorChangeHandler_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static ColorChangeHandler_Factory create(final Provider<Context> provider) {
        return new ColorChangeHandler_Factory(provider);
    }
    
    public static ColorChangeHandler provideInstance(final Provider<Context> provider) {
        return new ColorChangeHandler(provider.get());
    }
    
    @Override
    public ColorChangeHandler get() {
        return provideInstance(this.contextProvider);
    }
}
