// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.FlashlightController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class FlashlightTile_Factory implements Factory<FlashlightTile>
{
    private final Provider<FlashlightController> flashlightControllerProvider;
    private final Provider<QSHost> hostProvider;
    
    public FlashlightTile_Factory(final Provider<QSHost> hostProvider, final Provider<FlashlightController> flashlightControllerProvider) {
        this.hostProvider = hostProvider;
        this.flashlightControllerProvider = flashlightControllerProvider;
    }
    
    public static FlashlightTile_Factory create(final Provider<QSHost> provider, final Provider<FlashlightController> provider2) {
        return new FlashlightTile_Factory(provider, provider2);
    }
    
    public static FlashlightTile provideInstance(final Provider<QSHost> provider, final Provider<FlashlightController> provider2) {
        return new FlashlightTile(provider.get(), provider2.get());
    }
    
    @Override
    public FlashlightTile get() {
        return provideInstance(this.hostProvider, this.flashlightControllerProvider);
    }
}
