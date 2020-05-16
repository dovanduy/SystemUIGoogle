// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.media;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class MediaControllerFactory_Factory implements Factory<MediaControllerFactory>
{
    private final Provider<Context> contextProvider;
    
    public MediaControllerFactory_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static MediaControllerFactory_Factory create(final Provider<Context> provider) {
        return new MediaControllerFactory_Factory(provider);
    }
    
    public static MediaControllerFactory provideInstance(final Provider<Context> provider) {
        return new MediaControllerFactory(provider.get());
    }
    
    @Override
    public MediaControllerFactory get() {
        return provideInstance(this.contextProvider);
    }
}
