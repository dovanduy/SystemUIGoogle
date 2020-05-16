// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import android.os.Handler;
import javax.inject.Provider;
import android.hardware.display.NightDisplayListener;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideNightDisplayListenerFactory implements Factory<NightDisplayListener>
{
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideNightDisplayListenerFactory(final DependencyProvider module, final Provider<Context> contextProvider, final Provider<Handler> bgHandlerProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
        this.bgHandlerProvider = bgHandlerProvider;
    }
    
    public static DependencyProvider_ProvideNightDisplayListenerFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider, final Provider<Handler> provider2) {
        return new DependencyProvider_ProvideNightDisplayListenerFactory(dependencyProvider, provider, provider2);
    }
    
    public static NightDisplayListener provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider, final Provider<Handler> provider2) {
        return proxyProvideNightDisplayListener(dependencyProvider, provider.get(), provider2.get());
    }
    
    public static NightDisplayListener proxyProvideNightDisplayListener(final DependencyProvider dependencyProvider, final Context context, final Handler handler) {
        final NightDisplayListener provideNightDisplayListener = dependencyProvider.provideNightDisplayListener(context, handler);
        Preconditions.checkNotNull(provideNightDisplayListener, "Cannot return null from a non-@Nullable @Provides method");
        return provideNightDisplayListener;
    }
    
    @Override
    public NightDisplayListener get() {
        return provideInstance(this.module, this.contextProvider, this.bgHandlerProvider);
    }
}
