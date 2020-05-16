// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dagger;

import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import com.android.systemui.R$bool;
import com.google.android.systemui.reversecharging.ReverseWirelessCharger;
import java.util.Optional;
import com.android.systemui.recents.Recents;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.google.android.systemui.dreamliner.DockObserver;
import com.google.android.systemui.dreamliner.DreamlinerUtils;
import com.android.systemui.dock.DockManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.content.Context;

abstract class SystemUIGoogleModule
{
    static boolean provideAllowNotificationLongPress() {
        return true;
    }
    
    static DockManager provideDockManager(final Context context, final BroadcastDispatcher broadcastDispatcher, final StatusBarStateController statusBarStateController, final NotificationInterruptStateProvider notificationInterruptStateProvider) {
        return new DockObserver(context, DreamlinerUtils.getInstance(context), broadcastDispatcher, statusBarStateController, notificationInterruptStateProvider);
    }
    
    static HeadsUpManagerPhone provideHeadsUpManagerPhone(final Context context, final StatusBarStateController statusBarStateController, final KeyguardBypassController keyguardBypassController, final NotificationGroupManager notificationGroupManager, final ConfigurationController configurationController) {
        return new HeadsUpManagerPhone(context, statusBarStateController, keyguardBypassController, notificationGroupManager, configurationController);
    }
    
    static String provideLeakReportEmail() {
        return "buganizer-system+187317@google.com";
    }
    
    static Recents provideRecents(final Context context, final RecentsImplementation recentsImplementation, final CommandQueue commandQueue) {
        return new Recents(context, recentsImplementation, commandQueue);
    }
    
    static Optional<ReverseWirelessCharger> provideReverseWirelessCharger(final Context context) {
        Optional<ReverseWirelessCharger> optional;
        if (context.getResources().getBoolean(R$bool.config_wlc_support_enabled)) {
            optional = Optional.of(new ReverseWirelessCharger(context));
        }
        else {
            optional = Optional.empty();
        }
        return optional;
    }
    
    static SharedPreferences provideSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
