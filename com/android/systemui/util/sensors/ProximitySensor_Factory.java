// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.sensors;

import android.content.res.Resources;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ProximitySensor_Factory implements Factory<ProximitySensor>
{
    private final Provider<Resources> resourcesProvider;
    private final Provider<AsyncSensorManager> sensorManagerProvider;
    
    public ProximitySensor_Factory(final Provider<Resources> resourcesProvider, final Provider<AsyncSensorManager> sensorManagerProvider) {
        this.resourcesProvider = resourcesProvider;
        this.sensorManagerProvider = sensorManagerProvider;
    }
    
    public static ProximitySensor_Factory create(final Provider<Resources> provider, final Provider<AsyncSensorManager> provider2) {
        return new ProximitySensor_Factory(provider, provider2);
    }
    
    public static ProximitySensor provideInstance(final Provider<Resources> provider, final Provider<AsyncSensorManager> provider2) {
        return new ProximitySensor(provider.get(), provider2.get());
    }
    
    @Override
    public ProximitySensor get() {
        return provideInstance(this.resourcesProvider, this.sensorManagerProvider);
    }
}
