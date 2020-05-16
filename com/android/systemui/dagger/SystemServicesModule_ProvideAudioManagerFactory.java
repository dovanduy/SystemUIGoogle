// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.media.AudioManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideAudioManagerFactory implements Factory<AudioManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideAudioManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideAudioManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideAudioManagerFactory(provider);
    }
    
    public static AudioManager provideInstance(final Provider<Context> provider) {
        return proxyProvideAudioManager(provider.get());
    }
    
    public static AudioManager proxyProvideAudioManager(final Context context) {
        final AudioManager provideAudioManager = SystemServicesModule.provideAudioManager(context);
        Preconditions.checkNotNull(provideAudioManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideAudioManager;
    }
    
    @Override
    public AudioManager get() {
        return provideInstance(this.contextProvider);
    }
}
