// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors.config;

import com.google.android.systemui.columbus.ColumbusContentObserver;
import android.content.Context;
import java.util.Set;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GestureConfiguration_Factory implements Factory<GestureConfiguration>
{
    private final Provider<Set<Adjustment>> adjustmentsProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    
    public GestureConfiguration_Factory(final Provider<Context> contextProvider, final Provider<Set<Adjustment>> adjustmentsProvider, final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider) {
        this.contextProvider = contextProvider;
        this.adjustmentsProvider = adjustmentsProvider;
        this.settingsObserverFactoryProvider = settingsObserverFactoryProvider;
    }
    
    public static GestureConfiguration_Factory create(final Provider<Context> provider, final Provider<Set<Adjustment>> provider2, final Provider<ColumbusContentObserver.Factory> provider3) {
        return new GestureConfiguration_Factory(provider, provider2, provider3);
    }
    
    public static GestureConfiguration provideInstance(final Provider<Context> provider, final Provider<Set<Adjustment>> provider2, final Provider<ColumbusContentObserver.Factory> provider3) {
        return new GestureConfiguration(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public GestureConfiguration get() {
        return provideInstance(this.contextProvider, this.adjustmentsProvider, this.settingsObserverFactoryProvider);
    }
}
