// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.util.sensors.AsyncSensorManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardProximity_Factory implements Factory<KeyguardProximity>
{
    private final Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardVisibility> keyguardGateProvider;
    
    public KeyguardProximity_Factory(final Provider<Context> contextProvider, final Provider<AsyncSensorManager> asyncSensorManagerProvider, final Provider<KeyguardVisibility> keyguardGateProvider) {
        this.contextProvider = contextProvider;
        this.asyncSensorManagerProvider = asyncSensorManagerProvider;
        this.keyguardGateProvider = keyguardGateProvider;
    }
    
    public static KeyguardProximity_Factory create(final Provider<Context> provider, final Provider<AsyncSensorManager> provider2, final Provider<KeyguardVisibility> provider3) {
        return new KeyguardProximity_Factory(provider, provider2, provider3);
    }
    
    public static KeyguardProximity provideInstance(final Provider<Context> provider, final Provider<AsyncSensorManager> provider2, final Provider<KeyguardVisibility> provider3) {
        return new KeyguardProximity(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public KeyguardProximity get() {
        return provideInstance(this.contextProvider, this.asyncSensorManagerProvider, this.keyguardGateProvider);
    }
}
