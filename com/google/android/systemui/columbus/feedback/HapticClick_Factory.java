// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class HapticClick_Factory implements Factory<HapticClick>
{
    private final Provider<Context> contextProvider;
    
    public HapticClick_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static HapticClick_Factory create(final Provider<Context> provider) {
        return new HapticClick_Factory(provider);
    }
    
    public static HapticClick provideInstance(final Provider<Context> provider) {
        return new HapticClick(provider.get());
    }
    
    @Override
    public HapticClick get() {
        return provideInstance(this.contextProvider);
    }
}
