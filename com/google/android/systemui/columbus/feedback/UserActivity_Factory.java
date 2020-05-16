// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UserActivity_Factory implements Factory<UserActivity>
{
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    
    public UserActivity_Factory(final Provider<Context> contextProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider) {
        this.contextProvider = contextProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
    }
    
    public static UserActivity_Factory create(final Provider<Context> provider, final Provider<KeyguardStateController> provider2) {
        return new UserActivity_Factory(provider, provider2);
    }
    
    public static UserActivity provideInstance(final Provider<Context> provider, final Provider<KeyguardStateController> provider2) {
        return new UserActivity(provider.get(), provider2.get());
    }
    
    @Override
    public UserActivity get() {
        return provideInstance(this.contextProvider, this.keyguardStateControllerProvider);
    }
}
