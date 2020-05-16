// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationRankingManager_Factory implements Factory<NotificationRankingManager>
{
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<HighPriorityProvider> highPriorityProvider;
    private final Provider<NotificationEntryManagerLogger> loggerProvider;
    private final Provider<NotificationMediaManager> mediaManagerLazyProvider;
    private final Provider<NotificationFilter> notifFilterProvider;
    private final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider;
    private final Provider<NotificationSectionsFeatureManager> sectionsFeatureManagerProvider;
    
    public NotificationRankingManager_Factory(final Provider<NotificationMediaManager> mediaManagerLazyProvider, final Provider<NotificationGroupManager> groupManagerProvider, final Provider<HeadsUpManager> headsUpManagerProvider, final Provider<NotificationFilter> notifFilterProvider, final Provider<NotificationEntryManagerLogger> loggerProvider, final Provider<NotificationSectionsFeatureManager> sectionsFeatureManagerProvider, final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider, final Provider<HighPriorityProvider> highPriorityProvider) {
        this.mediaManagerLazyProvider = mediaManagerLazyProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.notifFilterProvider = notifFilterProvider;
        this.loggerProvider = loggerProvider;
        this.sectionsFeatureManagerProvider = sectionsFeatureManagerProvider;
        this.peopleNotificationIdentifierProvider = peopleNotificationIdentifierProvider;
        this.highPriorityProvider = highPriorityProvider;
    }
    
    public static NotificationRankingManager_Factory create(final Provider<NotificationMediaManager> provider, final Provider<NotificationGroupManager> provider2, final Provider<HeadsUpManager> provider3, final Provider<NotificationFilter> provider4, final Provider<NotificationEntryManagerLogger> provider5, final Provider<NotificationSectionsFeatureManager> provider6, final Provider<PeopleNotificationIdentifier> provider7, final Provider<HighPriorityProvider> provider8) {
        return new NotificationRankingManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static NotificationRankingManager provideInstance(final Provider<NotificationMediaManager> provider, final Provider<NotificationGroupManager> provider2, final Provider<HeadsUpManager> provider3, final Provider<NotificationFilter> provider4, final Provider<NotificationEntryManagerLogger> provider5, final Provider<NotificationSectionsFeatureManager> provider6, final Provider<PeopleNotificationIdentifier> provider7, final Provider<HighPriorityProvider> provider8) {
        return new NotificationRankingManager(DoubleCheck.lazy(provider), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    @Override
    public NotificationRankingManager get() {
        return provideInstance(this.mediaManagerLazyProvider, this.groupManagerProvider, this.headsUpManagerProvider, this.notifFilterProvider, this.loggerProvider, this.sectionsFeatureManagerProvider, this.peopleNotificationIdentifierProvider, this.highPriorityProvider);
    }
}
