// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import javax.inject.Provider;
import android.service.notification.StatusBarNotification;
import dagger.internal.Factory;

public final class ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory implements Factory<StatusBarNotification>
{
    private final Provider<NotificationEntry> notificationEntryProvider;
    
    public ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory(final Provider<NotificationEntry> notificationEntryProvider) {
        this.notificationEntryProvider = notificationEntryProvider;
    }
    
    public static ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory create(final Provider<NotificationEntry> provider) {
        return new ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory(provider);
    }
    
    public static StatusBarNotification provideInstance(final Provider<NotificationEntry> provider) {
        return proxyProvideStatusBarNotification(provider.get());
    }
    
    public static StatusBarNotification proxyProvideStatusBarNotification(final NotificationEntry notificationEntry) {
        final StatusBarNotification provideStatusBarNotification = ExpandableNotificationRowComponent.ExpandableNotificationRowModule.provideStatusBarNotification(notificationEntry);
        Preconditions.checkNotNull(provideStatusBarNotification, "Cannot return null from a non-@Nullable @Provides method");
        return provideStatusBarNotification;
    }
    
    @Override
    public StatusBarNotification get() {
        return provideInstance(this.notificationEntryProvider);
    }
}
