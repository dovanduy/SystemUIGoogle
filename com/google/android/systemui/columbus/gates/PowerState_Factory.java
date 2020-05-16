// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import dagger.internal.DoubleCheck;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PowerState_Factory implements Factory<PowerState>
{
    private final Provider<Context> contextProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    
    public PowerState_Factory(final Provider<Context> contextProvider, final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider) {
        this.contextProvider = contextProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
    }
    
    public static PowerState_Factory create(final Provider<Context> provider, final Provider<WakefulnessLifecycle> provider2) {
        return new PowerState_Factory(provider, provider2);
    }
    
    public static PowerState provideInstance(final Provider<Context> provider, final Provider<WakefulnessLifecycle> provider2) {
        return new PowerState(provider.get(), DoubleCheck.lazy(provider2));
    }
    
    @Override
    public PowerState get() {
        return provideInstance(this.contextProvider, this.wakefulnessLifecycleProvider);
    }
}
