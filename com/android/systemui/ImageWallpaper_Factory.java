// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.statusbar.phone.DozeParameters;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ImageWallpaper_Factory implements Factory<ImageWallpaper>
{
    private final Provider<DozeParameters> dozeParametersProvider;
    
    public ImageWallpaper_Factory(final Provider<DozeParameters> dozeParametersProvider) {
        this.dozeParametersProvider = dozeParametersProvider;
    }
    
    public static ImageWallpaper_Factory create(final Provider<DozeParameters> provider) {
        return new ImageWallpaper_Factory(provider);
    }
    
    public static ImageWallpaper provideInstance(final Provider<DozeParameters> provider) {
        return new ImageWallpaper(provider.get());
    }
    
    @Override
    public ImageWallpaper get() {
        return provideInstance(this.dozeParametersProvider);
    }
}
