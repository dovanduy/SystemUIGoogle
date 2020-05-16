// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NightDisplayTile_Factory implements Factory<NightDisplayTile>
{
    private final Provider<QSHost> hostProvider;
    
    public NightDisplayTile_Factory(final Provider<QSHost> hostProvider) {
        this.hostProvider = hostProvider;
    }
    
    public static NightDisplayTile_Factory create(final Provider<QSHost> provider) {
        return new NightDisplayTile_Factory(provider);
    }
    
    public static NightDisplayTile provideInstance(final Provider<QSHost> provider) {
        return new NightDisplayTile(provider.get());
    }
    
    @Override
    public NightDisplayTile get() {
        return provideInstance(this.hostProvider);
    }
}
