// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LocationTile_Factory implements Factory<LocationTile>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<LocationController> locationControllerProvider;
    
    public LocationTile_Factory(final Provider<QSHost> hostProvider, final Provider<LocationController> locationControllerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.locationControllerProvider = locationControllerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }
    
    public static LocationTile_Factory create(final Provider<QSHost> provider, final Provider<LocationController> provider2, final Provider<KeyguardStateController> provider3, final Provider<ActivityStarter> provider4) {
        return new LocationTile_Factory(provider, provider2, provider3, provider4);
    }
    
    public static LocationTile provideInstance(final Provider<QSHost> provider, final Provider<LocationController> provider2, final Provider<KeyguardStateController> provider3, final Provider<ActivityStarter> provider4) {
        return new LocationTile(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public LocationTile get() {
        return provideInstance(this.hostProvider, this.locationControllerProvider, this.keyguardStateControllerProvider, this.activityStarterProvider);
    }
}
