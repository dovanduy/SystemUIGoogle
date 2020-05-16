// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.qs.QSHost;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UserTile_Factory implements Factory<UserTile>
{
    private final Provider<QSHost> hostProvider;
    private final Provider<UserInfoController> userInfoControllerProvider;
    private final Provider<UserSwitcherController> userSwitcherControllerProvider;
    
    public UserTile_Factory(final Provider<QSHost> hostProvider, final Provider<UserSwitcherController> userSwitcherControllerProvider, final Provider<UserInfoController> userInfoControllerProvider) {
        this.hostProvider = hostProvider;
        this.userSwitcherControllerProvider = userSwitcherControllerProvider;
        this.userInfoControllerProvider = userInfoControllerProvider;
    }
    
    public static UserTile_Factory create(final Provider<QSHost> provider, final Provider<UserSwitcherController> provider2, final Provider<UserInfoController> provider3) {
        return new UserTile_Factory(provider, provider2, provider3);
    }
    
    public static UserTile provideInstance(final Provider<QSHost> provider, final Provider<UserSwitcherController> provider2, final Provider<UserInfoController> provider3) {
        return new UserTile(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public UserTile get() {
        return provideInstance(this.hostProvider, this.userSwitcherControllerProvider, this.userInfoControllerProvider);
    }
}
