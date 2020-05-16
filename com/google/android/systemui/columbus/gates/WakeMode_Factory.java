// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import dagger.internal.DoubleCheck;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class WakeMode_Factory implements Factory<WakeMode>
{
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    
    public WakeMode_Factory(final Provider<Context> contextProvider, final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider) {
        this.contextProvider = contextProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
        this.settingsObserverFactoryProvider = settingsObserverFactoryProvider;
    }
    
    public static WakeMode_Factory create(final Provider<Context> provider, final Provider<WakefulnessLifecycle> provider2, final Provider<ColumbusContentObserver.Factory> provider3) {
        return new WakeMode_Factory(provider, provider2, provider3);
    }
    
    public static WakeMode provideInstance(final Provider<Context> provider, final Provider<WakefulnessLifecycle> provider2, final Provider<ColumbusContentObserver.Factory> provider3) {
        return new WakeMode(provider.get(), DoubleCheck.lazy(provider2), provider3.get());
    }
    
    @Override
    public WakeMode get() {
        return provideInstance(this.contextProvider, this.wakefulnessLifecycleProvider, this.settingsObserverFactoryProvider);
    }
}
