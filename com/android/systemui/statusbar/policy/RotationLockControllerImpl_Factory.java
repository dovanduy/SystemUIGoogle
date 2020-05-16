// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class RotationLockControllerImpl_Factory implements Factory<RotationLockControllerImpl>
{
    private final Provider<Context> contextProvider;
    
    public RotationLockControllerImpl_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static RotationLockControllerImpl_Factory create(final Provider<Context> provider) {
        return new RotationLockControllerImpl_Factory(provider);
    }
    
    public static RotationLockControllerImpl provideInstance(final Provider<Context> provider) {
        return new RotationLockControllerImpl(provider.get());
    }
    
    @Override
    public RotationLockControllerImpl get() {
        return provideInstance(this.contextProvider);
    }
}
