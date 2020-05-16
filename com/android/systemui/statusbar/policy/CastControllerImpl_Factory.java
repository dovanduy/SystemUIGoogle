// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class CastControllerImpl_Factory implements Factory<CastControllerImpl>
{
    private final Provider<Context> contextProvider;
    
    public CastControllerImpl_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static CastControllerImpl_Factory create(final Provider<Context> provider) {
        return new CastControllerImpl_Factory(provider);
    }
    
    public static CastControllerImpl provideInstance(final Provider<Context> provider) {
        return new CastControllerImpl(provider.get());
    }
    
    @Override
    public CastControllerImpl get() {
        return provideInstance(this.contextProvider);
    }
}
