// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import com.google.android.systemui.columbus.sensors.config.GestureConfiguration;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GestureSensorImpl_Factory implements Factory<GestureSensorImpl>
{
    private final Provider<Context> contextProvider;
    private final Provider<GestureConfiguration> gestureConfigurationProvider;
    
    public GestureSensorImpl_Factory(final Provider<Context> contextProvider, final Provider<GestureConfiguration> gestureConfigurationProvider) {
        this.contextProvider = contextProvider;
        this.gestureConfigurationProvider = gestureConfigurationProvider;
    }
    
    public static GestureSensorImpl_Factory create(final Provider<Context> provider, final Provider<GestureConfiguration> provider2) {
        return new GestureSensorImpl_Factory(provider, provider2);
    }
    
    public static GestureSensorImpl provideInstance(final Provider<Context> provider, final Provider<GestureConfiguration> provider2) {
        return new GestureSensorImpl(provider.get(), provider2.get());
    }
    
    @Override
    public GestureSensorImpl get() {
        return provideInstance(this.contextProvider, this.gestureConfigurationProvider);
    }
}
