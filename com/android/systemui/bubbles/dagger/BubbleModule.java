// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles.dagger;

import com.android.systemui.bubbles.BubbleStackView;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.model.SysUiState;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.bubbles.BubbleData;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import android.content.Context;

public interface BubbleModule
{
    default BubbleController newBubbleController(final Context context, final NotificationShadeWindowController notificationShadeWindowController, final StatusBarStateController statusBarStateController, final ShadeController shadeController, final BubbleData bubbleData, final ConfigurationController configurationController, final NotificationInterruptStateProvider notificationInterruptStateProvider, final ZenModeController zenModeController, final NotificationLockscreenUserManager notificationLockscreenUserManager, final NotificationGroupManager notificationGroupManager, final NotificationEntryManager notificationEntryManager, final NotifPipeline notifPipeline, final FeatureFlags featureFlags, final DumpManager dumpManager, final FloatingContentCoordinator floatingContentCoordinator, final SysUiState sysUiState) {
        return new BubbleController(context, notificationShadeWindowController, statusBarStateController, shadeController, bubbleData, null, configurationController, notificationInterruptStateProvider, zenModeController, notificationLockscreenUserManager, notificationGroupManager, notificationEntryManager, notifPipeline, featureFlags, dumpManager, floatingContentCoordinator, sysUiState);
    }
}
