// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SensorPrivacyControllerImpl_Factory implements Factory<SensorPrivacyControllerImpl>
{
    private final Provider<Context> contextProvider;
    
    public SensorPrivacyControllerImpl_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SensorPrivacyControllerImpl_Factory create(final Provider<Context> provider) {
        return new SensorPrivacyControllerImpl_Factory(provider);
    }
    
    public static SensorPrivacyControllerImpl provideInstance(final Provider<Context> provider) {
        return new SensorPrivacyControllerImpl(provider.get());
    }
    
    @Override
    public SensorPrivacyControllerImpl get() {
        return provideInstance(this.contextProvider);
    }
}
