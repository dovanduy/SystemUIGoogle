// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardService_Factory implements Factory<KeyguardService>
{
    private final Provider<KeyguardLifecyclesDispatcher> keyguardLifecyclesDispatcherProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    
    public KeyguardService_Factory(final Provider<KeyguardViewMediator> keyguardViewMediatorProvider, final Provider<KeyguardLifecyclesDispatcher> keyguardLifecyclesDispatcherProvider) {
        this.keyguardViewMediatorProvider = keyguardViewMediatorProvider;
        this.keyguardLifecyclesDispatcherProvider = keyguardLifecyclesDispatcherProvider;
    }
    
    public static KeyguardService_Factory create(final Provider<KeyguardViewMediator> provider, final Provider<KeyguardLifecyclesDispatcher> provider2) {
        return new KeyguardService_Factory(provider, provider2);
    }
    
    public static KeyguardService provideInstance(final Provider<KeyguardViewMediator> provider, final Provider<KeyguardLifecyclesDispatcher> provider2) {
        return new KeyguardService(provider.get(), provider2.get());
    }
    
    @Override
    public KeyguardService get() {
        return provideInstance(this.keyguardViewMediatorProvider, this.keyguardLifecyclesDispatcherProvider);
    }
}
