// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardLifecyclesDispatcher_Factory implements Factory<KeyguardLifecyclesDispatcher>
{
    private final Provider<ScreenLifecycle> screenLifecycleProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    
    public KeyguardLifecyclesDispatcher_Factory(final Provider<ScreenLifecycle> screenLifecycleProvider, final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider) {
        this.screenLifecycleProvider = screenLifecycleProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
    }
    
    public static KeyguardLifecyclesDispatcher_Factory create(final Provider<ScreenLifecycle> provider, final Provider<WakefulnessLifecycle> provider2) {
        return new KeyguardLifecyclesDispatcher_Factory(provider, provider2);
    }
    
    public static KeyguardLifecyclesDispatcher provideInstance(final Provider<ScreenLifecycle> provider, final Provider<WakefulnessLifecycle> provider2) {
        return new KeyguardLifecyclesDispatcher(provider.get(), provider2.get());
    }
    
    @Override
    public KeyguardLifecyclesDispatcher get() {
        return provideInstance(this.screenLifecycleProvider, this.wakefulnessLifecycleProvider);
    }
}
