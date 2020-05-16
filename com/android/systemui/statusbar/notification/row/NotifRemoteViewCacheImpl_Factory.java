// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifRemoteViewCacheImpl_Factory implements Factory<NotifRemoteViewCacheImpl>
{
    private final Provider<CommonNotifCollection> collectionProvider;
    
    public NotifRemoteViewCacheImpl_Factory(final Provider<CommonNotifCollection> collectionProvider) {
        this.collectionProvider = collectionProvider;
    }
    
    public static NotifRemoteViewCacheImpl_Factory create(final Provider<CommonNotifCollection> provider) {
        return new NotifRemoteViewCacheImpl_Factory(provider);
    }
    
    public static NotifRemoteViewCacheImpl provideInstance(final Provider<CommonNotifCollection> provider) {
        return new NotifRemoteViewCacheImpl(provider.get());
    }
    
    @Override
    public NotifRemoteViewCacheImpl get() {
        return provideInstance(this.collectionProvider);
    }
}
