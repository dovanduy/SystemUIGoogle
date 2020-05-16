// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import com.android.systemui.media.MediaControllerFactory;
import android.content.Context;
import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardMediaPlayer_Factory implements Factory<KeyguardMediaPlayer>
{
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<MediaControllerFactory> factoryProvider;
    
    public KeyguardMediaPlayer_Factory(final Provider<Context> contextProvider, final Provider<MediaControllerFactory> factoryProvider, final Provider<Executor> backgroundExecutorProvider) {
        this.contextProvider = contextProvider;
        this.factoryProvider = factoryProvider;
        this.backgroundExecutorProvider = backgroundExecutorProvider;
    }
    
    public static KeyguardMediaPlayer_Factory create(final Provider<Context> provider, final Provider<MediaControllerFactory> provider2, final Provider<Executor> provider3) {
        return new KeyguardMediaPlayer_Factory(provider, provider2, provider3);
    }
    
    public static KeyguardMediaPlayer provideInstance(final Provider<Context> provider, final Provider<MediaControllerFactory> provider2, final Provider<Executor> provider3) {
        return new KeyguardMediaPlayer(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public KeyguardMediaPlayer get() {
        return provideInstance(this.contextProvider, this.factoryProvider, this.backgroundExecutorProvider);
    }
}
