// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.keyguard.KeyguardViewMediator;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class OpaHomeButton_Factory implements Factory<OpaHomeButton>
{
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<StatusBar> statusBarProvider;
    
    public OpaHomeButton_Factory(final Provider<KeyguardViewMediator> keyguardViewMediatorProvider, final Provider<StatusBar> statusBarProvider) {
        this.keyguardViewMediatorProvider = keyguardViewMediatorProvider;
        this.statusBarProvider = statusBarProvider;
    }
    
    public static OpaHomeButton_Factory create(final Provider<KeyguardViewMediator> provider, final Provider<StatusBar> provider2) {
        return new OpaHomeButton_Factory(provider, provider2);
    }
    
    public static OpaHomeButton provideInstance(final Provider<KeyguardViewMediator> provider, final Provider<StatusBar> provider2) {
        return new OpaHomeButton(provider.get(), provider2.get());
    }
    
    @Override
    public OpaHomeButton get() {
        return provideInstance(this.keyguardViewMediatorProvider, this.statusBarProvider);
    }
}
