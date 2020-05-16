// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TelephonyActivity_Factory implements Factory<TelephonyActivity>
{
    private final Provider<Context> contextProvider;
    
    public TelephonyActivity_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static TelephonyActivity_Factory create(final Provider<Context> provider) {
        return new TelephonyActivity_Factory(provider);
    }
    
    public static TelephonyActivity provideInstance(final Provider<Context> provider) {
        return new TelephonyActivity(provider.get());
    }
    
    @Override
    public TelephonyActivity get() {
        return provideInstance(this.contextProvider);
    }
}
