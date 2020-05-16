// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class VolumeUI_Factory implements Factory<VolumeUI>
{
    private final Provider<Context> contextProvider;
    private final Provider<VolumeDialogComponent> volumeDialogComponentProvider;
    
    public VolumeUI_Factory(final Provider<Context> contextProvider, final Provider<VolumeDialogComponent> volumeDialogComponentProvider) {
        this.contextProvider = contextProvider;
        this.volumeDialogComponentProvider = volumeDialogComponentProvider;
    }
    
    public static VolumeUI_Factory create(final Provider<Context> provider, final Provider<VolumeDialogComponent> provider2) {
        return new VolumeUI_Factory(provider, provider2);
    }
    
    public static VolumeUI provideInstance(final Provider<Context> provider, final Provider<VolumeDialogComponent> provider2) {
        return new VolumeUI(provider.get(), provider2.get());
    }
    
    @Override
    public VolumeUI get() {
        return provideInstance(this.contextProvider, this.volumeDialogComponentProvider);
    }
}
