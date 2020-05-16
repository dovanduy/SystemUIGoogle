// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class OpaLockscreen_Factory implements Factory<OpaLockscreen>
{
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<StatusBar> statusBarProvider;
    
    public OpaLockscreen_Factory(final Provider<StatusBar> statusBarProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider) {
        this.statusBarProvider = statusBarProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
    }
    
    public static OpaLockscreen_Factory create(final Provider<StatusBar> provider, final Provider<KeyguardStateController> provider2) {
        return new OpaLockscreen_Factory(provider, provider2);
    }
    
    public static OpaLockscreen provideInstance(final Provider<StatusBar> provider, final Provider<KeyguardStateController> provider2) {
        return new OpaLockscreen(provider.get(), provider2.get());
    }
    
    @Override
    public OpaLockscreen get() {
        return provideInstance(this.statusBarProvider, this.keyguardStateControllerProvider);
    }
}
