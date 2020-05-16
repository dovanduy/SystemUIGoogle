// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ColorInversionTile_Factory implements Factory<ColorInversionTile>
{
    private final Provider<QSHost> hostProvider;
    
    public ColorInversionTile_Factory(final Provider<QSHost> hostProvider) {
        this.hostProvider = hostProvider;
    }
    
    public static ColorInversionTile_Factory create(final Provider<QSHost> provider) {
        return new ColorInversionTile_Factory(provider);
    }
    
    public static ColorInversionTile provideInstance(final Provider<QSHost> provider) {
        return new ColorInversionTile(provider.get());
    }
    
    @Override
    public ColorInversionTile get() {
        return provideInstance(this.hostProvider);
    }
}
