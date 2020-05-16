// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UserInfoControllerImpl_Factory implements Factory<UserInfoControllerImpl>
{
    private final Provider<Context> contextProvider;
    
    public UserInfoControllerImpl_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static UserInfoControllerImpl_Factory create(final Provider<Context> provider) {
        return new UserInfoControllerImpl_Factory(provider);
    }
    
    public static UserInfoControllerImpl provideInstance(final Provider<Context> provider) {
        return new UserInfoControllerImpl(provider.get());
    }
    
    @Override
    public UserInfoControllerImpl get() {
        return provideInstance(this.contextProvider);
    }
}
