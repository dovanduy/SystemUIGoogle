// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.keyguard.KeyguardMediaPlayer;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapter;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.ActivityStarter;
import dagger.internal.Factory;

public final class NotificationSectionsManager_Factory implements Factory<NotificationSectionsManager>
{
    public static NotificationSectionsManager newNotificationSectionsManager(final ActivityStarter activityStarter, final StatusBarStateController statusBarStateController, final ConfigurationController configurationController, final PeopleHubViewAdapter peopleHubViewAdapter, final KeyguardMediaPlayer keyguardMediaPlayer, final NotificationSectionsFeatureManager notificationSectionsFeatureManager) {
        return new NotificationSectionsManager(activityStarter, statusBarStateController, configurationController, peopleHubViewAdapter, keyguardMediaPlayer, notificationSectionsFeatureManager);
    }
}
