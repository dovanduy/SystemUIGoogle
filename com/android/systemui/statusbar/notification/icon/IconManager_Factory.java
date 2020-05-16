// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.icon;

import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import android.content.pm.LauncherApps;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class IconManager_Factory implements Factory<IconManager>
{
    private final Provider<IconBuilder> iconBuilderProvider;
    private final Provider<LauncherApps> launcherAppsProvider;
    private final Provider<CommonNotifCollection> notifCollectionProvider;
    
    public IconManager_Factory(final Provider<CommonNotifCollection> notifCollectionProvider, final Provider<LauncherApps> launcherAppsProvider, final Provider<IconBuilder> iconBuilderProvider) {
        this.notifCollectionProvider = notifCollectionProvider;
        this.launcherAppsProvider = launcherAppsProvider;
        this.iconBuilderProvider = iconBuilderProvider;
    }
    
    public static IconManager_Factory create(final Provider<CommonNotifCollection> provider, final Provider<LauncherApps> provider2, final Provider<IconBuilder> provider3) {
        return new IconManager_Factory(provider, provider2, provider3);
    }
    
    public static IconManager provideInstance(final Provider<CommonNotifCollection> provider, final Provider<LauncherApps> provider2, final Provider<IconBuilder> provider3) {
        return new IconManager(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public IconManager get() {
        return provideInstance(this.notifCollectionProvider, this.launcherAppsProvider, this.iconBuilderProvider);
    }
}
