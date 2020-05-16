// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class VibratorHelper_Factory implements Factory<VibratorHelper>
{
    private final Provider<Context> contextProvider;
    
    public VibratorHelper_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static VibratorHelper_Factory create(final Provider<Context> provider) {
        return new VibratorHelper_Factory(provider);
    }
    
    public static VibratorHelper provideInstance(final Provider<Context> provider) {
        return new VibratorHelper(provider.get());
    }
    
    @Override
    public VibratorHelper get() {
        return provideInstance(this.contextProvider);
    }
}
