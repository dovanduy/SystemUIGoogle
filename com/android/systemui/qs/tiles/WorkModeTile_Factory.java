// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.qs.QSHost;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class WorkModeTile_Factory implements Factory<WorkModeTile>
{
    private final Provider<QSHost> hostProvider;
    private final Provider<ManagedProfileController> managedProfileControllerProvider;
    
    public WorkModeTile_Factory(final Provider<QSHost> hostProvider, final Provider<ManagedProfileController> managedProfileControllerProvider) {
        this.hostProvider = hostProvider;
        this.managedProfileControllerProvider = managedProfileControllerProvider;
    }
    
    public static WorkModeTile_Factory create(final Provider<QSHost> provider, final Provider<ManagedProfileController> provider2) {
        return new WorkModeTile_Factory(provider, provider2);
    }
    
    public static WorkModeTile provideInstance(final Provider<QSHost> provider, final Provider<ManagedProfileController> provider2) {
        return new WorkModeTile(provider.get(), provider2.get());
    }
    
    @Override
    public WorkModeTile get() {
        return provideInstance(this.hostProvider, this.managedProfileControllerProvider);
    }
}
