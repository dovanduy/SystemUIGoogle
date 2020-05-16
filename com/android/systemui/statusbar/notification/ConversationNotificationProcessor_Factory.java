// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.content.pm.LauncherApps;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ConversationNotificationProcessor_Factory implements Factory<ConversationNotificationProcessor>
{
    private final Provider<ConversationNotificationManager> conversationNotificationManagerProvider;
    private final Provider<LauncherApps> launcherAppsProvider;
    
    public ConversationNotificationProcessor_Factory(final Provider<LauncherApps> launcherAppsProvider, final Provider<ConversationNotificationManager> conversationNotificationManagerProvider) {
        this.launcherAppsProvider = launcherAppsProvider;
        this.conversationNotificationManagerProvider = conversationNotificationManagerProvider;
    }
    
    public static ConversationNotificationProcessor_Factory create(final Provider<LauncherApps> provider, final Provider<ConversationNotificationManager> provider2) {
        return new ConversationNotificationProcessor_Factory(provider, provider2);
    }
    
    public static ConversationNotificationProcessor provideInstance(final Provider<LauncherApps> provider, final Provider<ConversationNotificationManager> provider2) {
        return new ConversationNotificationProcessor(provider.get(), provider2.get());
    }
    
    @Override
    public ConversationNotificationProcessor get() {
        return provideInstance(this.launcherAppsProvider, this.conversationNotificationManagerProvider);
    }
}
