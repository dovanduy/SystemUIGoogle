// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.keyguard.KeyguardViewMediator;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SquishyNavigationButtons_Factory implements Factory<SquishyNavigationButtons>
{
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<StatusBar> statusBarProvider;
    
    public SquishyNavigationButtons_Factory(final Provider<Context> contextProvider, final Provider<KeyguardViewMediator> keyguardViewMediatorProvider, final Provider<StatusBar> statusBarProvider) {
        this.contextProvider = contextProvider;
        this.keyguardViewMediatorProvider = keyguardViewMediatorProvider;
        this.statusBarProvider = statusBarProvider;
    }
    
    public static SquishyNavigationButtons_Factory create(final Provider<Context> provider, final Provider<KeyguardViewMediator> provider2, final Provider<StatusBar> provider3) {
        return new SquishyNavigationButtons_Factory(provider, provider2, provider3);
    }
    
    public static SquishyNavigationButtons provideInstance(final Provider<Context> provider, final Provider<KeyguardViewMediator> provider2, final Provider<StatusBar> provider3) {
        return new SquishyNavigationButtons(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public SquishyNavigationButtons get() {
        return provideInstance(this.contextProvider, this.keyguardViewMediatorProvider, this.statusBarProvider);
    }
}
