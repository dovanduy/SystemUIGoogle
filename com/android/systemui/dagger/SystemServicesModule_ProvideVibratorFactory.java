// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import android.content.Context;
import javax.inject.Provider;
import android.os.Vibrator;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideVibratorFactory implements Factory<Vibrator>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideVibratorFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideVibratorFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideVibratorFactory(provider);
    }
    
    public static Vibrator provideInstance(final Provider<Context> provider) {
        return proxyProvideVibrator(provider.get());
    }
    
    public static Vibrator proxyProvideVibrator(final Context context) {
        return SystemServicesModule.provideVibrator(context);
    }
    
    @Override
    public Vibrator get() {
        return provideInstance(this.contextProvider);
    }
}
