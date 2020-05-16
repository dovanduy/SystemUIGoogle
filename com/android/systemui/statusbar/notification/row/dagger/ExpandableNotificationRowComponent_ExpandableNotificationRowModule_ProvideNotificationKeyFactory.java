// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.dagger;

import dagger.internal.Preconditions;
import android.service.notification.StatusBarNotification;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory implements Factory<String>
{
    private final Provider<StatusBarNotification> statusBarNotificationProvider;
    
    public ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory(final Provider<StatusBarNotification> statusBarNotificationProvider) {
        this.statusBarNotificationProvider = statusBarNotificationProvider;
    }
    
    public static ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory create(final Provider<StatusBarNotification> provider) {
        return new ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory(provider);
    }
    
    public static String provideInstance(final Provider<StatusBarNotification> provider) {
        return proxyProvideNotificationKey(provider.get());
    }
    
    public static String proxyProvideNotificationKey(final StatusBarNotification statusBarNotification) {
        final String provideNotificationKey = ExpandableNotificationRowComponent.ExpandableNotificationRowModule.provideNotificationKey(statusBarNotification);
        Preconditions.checkNotNull(provideNotificationKey, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationKey;
    }
    
    @Override
    public String get() {
        return provideInstance(this.statusBarNotificationProvider);
    }
}
