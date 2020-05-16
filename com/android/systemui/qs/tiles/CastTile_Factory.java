// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class CastTile_Factory implements Factory<CastTile>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<CastController> castControllerProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<NetworkController> networkControllerProvider;
    
    public CastTile_Factory(final Provider<QSHost> hostProvider, final Provider<CastController> castControllerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<NetworkController> networkControllerProvider, final Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.castControllerProvider = castControllerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.networkControllerProvider = networkControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }
    
    public static CastTile_Factory create(final Provider<QSHost> provider, final Provider<CastController> provider2, final Provider<KeyguardStateController> provider3, final Provider<NetworkController> provider4, final Provider<ActivityStarter> provider5) {
        return new CastTile_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static CastTile provideInstance(final Provider<QSHost> provider, final Provider<CastController> provider2, final Provider<KeyguardStateController> provider3, final Provider<NetworkController> provider4, final Provider<ActivityStarter> provider5) {
        return new CastTile(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public CastTile get() {
        return provideInstance(this.hostProvider, this.castControllerProvider, this.keyguardStateControllerProvider, this.networkControllerProvider, this.activityStarterProvider);
    }
}
