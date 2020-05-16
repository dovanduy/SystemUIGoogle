// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.provider.Settings$Global;
import android.net.Uri;
import android.database.ContentObserver;
import com.android.systemui.statusbar.notification.NotificationUtils;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import android.os.Handler;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;

public class KeyguardCoordinator implements Coordinator
{
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private final HighPriorityProvider mHighPriorityProvider;
    private final KeyguardStateController.Callback mKeyguardCallback;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final Handler mMainHandler;
    private final NotifFilter mNotifFilter;
    private final StatusBarStateController mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStateListener;
    
    public KeyguardCoordinator(final Context mContext, final Handler mMainHandler, final KeyguardStateController mKeyguardStateController, final NotificationLockscreenUserManager mLockscreenUserManager, final BroadcastDispatcher mBroadcastDispatcher, final StatusBarStateController mStatusBarStateController, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final HighPriorityProvider mHighPriorityProvider) {
        this.mNotifFilter = new NotifFilter("KeyguardCoordinator") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                final StatusBarNotification sbn = notificationEntry.getSbn();
                if (!KeyguardCoordinator.this.mKeyguardStateController.isShowing()) {
                    return false;
                }
                if (!KeyguardCoordinator.this.mLockscreenUserManager.shouldShowLockscreenNotifications()) {
                    return true;
                }
                final int currentUserId = KeyguardCoordinator.this.mLockscreenUserManager.getCurrentUserId();
                int identifier;
                if (sbn.getUser().getIdentifier() == -1) {
                    identifier = currentUserId;
                }
                else {
                    identifier = sbn.getUser().getIdentifier();
                }
                if (!KeyguardCoordinator.this.mKeyguardUpdateMonitor.isUserInLockdown(currentUserId)) {
                    if (!KeyguardCoordinator.this.mKeyguardUpdateMonitor.isUserInLockdown(identifier)) {
                        if (KeyguardCoordinator.this.mLockscreenUserManager.isLockscreenPublicMode(currentUserId) || KeyguardCoordinator.this.mLockscreenUserManager.isLockscreenPublicMode(identifier)) {
                            if (notificationEntry.getRanking().getVisibilityOverride() == -1) {
                                return true;
                            }
                            if (!KeyguardCoordinator.this.mLockscreenUserManager.userAllowsNotificationsInPublic(currentUserId)) {
                                return true;
                            }
                            if (!KeyguardCoordinator.this.mLockscreenUserManager.userAllowsNotificationsInPublic(identifier)) {
                                return true;
                            }
                        }
                        return (notificationEntry.getParent() == null || !KeyguardCoordinator.this.priorityExceedsLockscreenShowingThreshold(notificationEntry.getParent())) && (KeyguardCoordinator.this.priorityExceedsLockscreenShowingThreshold(notificationEntry) ^ true);
                    }
                }
                return true;
            }
        };
        this.mKeyguardCallback = new KeyguardStateController.Callback() {
            @Override
            public void onKeyguardShowingChanged() {
                KeyguardCoordinator.this.invalidateListFromFilter("onKeyguardShowingChanged");
            }
            
            @Override
            public void onUnlockedChanged() {
                KeyguardCoordinator.this.invalidateListFromFilter("onUnlockedChanged");
            }
        };
        this.mStatusBarStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onStateChanged(final int n) {
                KeyguardCoordinator.this.invalidateListFromFilter("onStatusBarStateChanged");
            }
        };
        this.mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onStrongAuthStateChanged(final int n) {
                KeyguardCoordinator.this.invalidateListFromFilter("onStrongAuthStateChanged");
            }
        };
        this.mContext = mContext;
        this.mMainHandler = mMainHandler;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mHighPriorityProvider = mHighPriorityProvider;
    }
    
    private boolean hideSilentNotificationsOnLockscreen() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean b = true;
        if (Settings$Secure.getInt(contentResolver, "lock_screen_show_silent_notifications", 1) != 0) {
            b = false;
        }
        return b;
    }
    
    private void invalidateListFromFilter(final String s) {
        this.mNotifFilter.invalidateList();
    }
    
    private boolean priorityExceedsLockscreenShowingThreshold(final ListEntry listEntry) {
        final boolean b = false;
        if (listEntry == null) {
            return false;
        }
        if (NotificationUtils.useNewInterruptionModel(this.mContext) && this.hideSilentNotificationsOnLockscreen()) {
            return this.mHighPriorityProvider.isHighPriority(listEntry);
        }
        boolean b2 = b;
        if (listEntry.getRepresentativeEntry() != null) {
            b2 = b;
            if (!listEntry.getRepresentativeEntry().getRanking().isAmbient()) {
                b2 = true;
            }
        }
        return b2;
    }
    
    private void setupInvalidateNotifListCallbacks() {
        this.mKeyguardStateController.addCallback(this.mKeyguardCallback);
        this.mKeyguardUpdateMonitor.registerCallback(this.mKeyguardUpdateCallback);
        final ContentObserver contentObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(final boolean b, final Uri obj) {
                if (KeyguardCoordinator.this.mKeyguardStateController.isShowing()) {
                    final KeyguardCoordinator this$0 = KeyguardCoordinator.this;
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Settings ");
                    sb.append(obj);
                    sb.append(" changed");
                    this$0.invalidateListFromFilter(sb.toString());
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("lock_screen_show_notifications"), false, (ContentObserver)contentObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("lock_screen_allow_private_notifications"), true, (ContentObserver)contentObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("zen_mode"), false, (ContentObserver)contentObserver);
        this.mStatusBarStateController.addCallback(this.mStatusBarStateListener);
        this.mBroadcastDispatcher.registerReceiver(new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (KeyguardCoordinator.this.mKeyguardStateController.isShowing()) {
                    KeyguardCoordinator.this.invalidateListFromFilter(intent.getAction());
                }
            }
        }, new IntentFilter("android.intent.action.USER_SWITCHED"));
    }
    
    @Override
    public void attach(final NotifPipeline notifPipeline) {
        this.setupInvalidateNotifListCallbacks();
        notifPipeline.addFinalizeFilter(this.mNotifFilter);
    }
}
