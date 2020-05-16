// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.qs.QSHost;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class RotationLockTile_Factory implements Factory<RotationLockTile>
{
    private final Provider<QSHost> hostProvider;
    private final Provider<RotationLockController> rotationLockControllerProvider;
    
    public RotationLockTile_Factory(final Provider<QSHost> hostProvider, final Provider<RotationLockController> rotationLockControllerProvider) {
        this.hostProvider = hostProvider;
        this.rotationLockControllerProvider = rotationLockControllerProvider;
    }
    
    public static RotationLockTile_Factory create(final Provider<QSHost> provider, final Provider<RotationLockController> provider2) {
        return new RotationLockTile_Factory(provider, provider2);
    }
    
    public static RotationLockTile provideInstance(final Provider<QSHost> provider, final Provider<RotationLockController> provider2) {
        return new RotationLockTile(provider.get(), provider2.get());
    }
    
    @Override
    public RotationLockTile get() {
        return provideInstance(this.hostProvider, this.rotationLockControllerProvider);
    }
}
