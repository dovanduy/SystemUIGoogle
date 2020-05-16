// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardVisibility_Factory implements Factory<KeyguardVisibility>
{
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    
    public KeyguardVisibility_Factory(final Provider<Context> contextProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider) {
        this.contextProvider = contextProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
    }
    
    public static KeyguardVisibility_Factory create(final Provider<Context> provider, final Provider<KeyguardStateController> provider2) {
        return new KeyguardVisibility_Factory(provider, provider2);
    }
    
    public static KeyguardVisibility provideInstance(final Provider<Context> provider, final Provider<KeyguardStateController> provider2) {
        return new KeyguardVisibility(provider.get(), provider2.get());
    }
    
    @Override
    public KeyguardVisibility get() {
        return provideInstance(this.contextProvider, this.keyguardStateControllerProvider);
    }
}
