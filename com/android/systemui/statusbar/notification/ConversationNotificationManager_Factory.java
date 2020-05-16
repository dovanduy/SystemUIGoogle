// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.phone.NotificationGroupManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ConversationNotificationManager_Factory implements Factory<ConversationNotificationManager>
{
    private final Provider<Context> contextProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationGroupManager> notificationGroupManagerProvider;
    
    public ConversationNotificationManager_Factory(final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<NotificationGroupManager> notificationGroupManagerProvider, final Provider<Context> contextProvider) {
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.notificationGroupManagerProvider = notificationGroupManagerProvider;
        this.contextProvider = contextProvider;
    }
    
    public static ConversationNotificationManager_Factory create(final Provider<NotificationEntryManager> provider, final Provider<NotificationGroupManager> provider2, final Provider<Context> provider3) {
        return new ConversationNotificationManager_Factory(provider, provider2, provider3);
    }
    
    public static ConversationNotificationManager provideInstance(final Provider<NotificationEntryManager> provider, final Provider<NotificationGroupManager> provider2, final Provider<Context> provider3) {
        return new ConversationNotificationManager(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ConversationNotificationManager get() {
        return provideInstance(this.notificationEntryManagerProvider, this.notificationGroupManagerProvider, this.contextProvider);
    }
}
