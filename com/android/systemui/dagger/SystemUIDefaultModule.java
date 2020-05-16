// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.systemui.recents.Recents;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.content.Context;

public abstract class SystemUIDefaultModule
{
    static boolean provideAllowNotificationLongPress() {
        return true;
    }
    
    static HeadsUpManagerPhone provideHeadsUpManagerPhone(final Context context, final StatusBarStateController statusBarStateController, final KeyguardBypassController keyguardBypassController, final NotificationGroupManager notificationGroupManager, final ConfigurationController configurationController) {
        return new HeadsUpManagerPhone(context, statusBarStateController, keyguardBypassController, notificationGroupManager, configurationController);
    }
    
    static String provideLeakReportEmail() {
        return null;
    }
    
    static Recents provideRecents(final Context context, final RecentsImplementation recentsImplementation, final CommandQueue commandQueue) {
        return new Recents(context, recentsImplementation, commandQueue);
    }
}
