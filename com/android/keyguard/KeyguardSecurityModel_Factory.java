// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardSecurityModel_Factory implements Factory<KeyguardSecurityModel>
{
    private final Provider<Context> contextProvider;
    
    public KeyguardSecurityModel_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static KeyguardSecurityModel_Factory create(final Provider<Context> provider) {
        return new KeyguardSecurityModel_Factory(provider);
    }
    
    public static KeyguardSecurityModel provideInstance(final Provider<Context> provider) {
        return new KeyguardSecurityModel(provider.get());
    }
    
    @Override
    public KeyguardSecurityModel get() {
        return provideInstance(this.contextProvider);
    }
}
