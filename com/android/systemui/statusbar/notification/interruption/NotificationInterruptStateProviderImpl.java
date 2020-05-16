// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.interruption;

import android.app.PendingIntent;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.Log;
import android.provider.Settings$Global;
import java.util.ArrayList;
import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
import java.util.List;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.os.PowerManager;
import com.android.systemui.statusbar.notification.NotificationFilter;
import android.database.ContentObserver;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import android.service.dreams.IDreamManager;
import android.content.ContentResolver;
import com.android.systemui.statusbar.policy.BatteryController;
import android.hardware.display.AmbientDisplayConfiguration;

public class NotificationInterruptStateProviderImpl implements NotificationInterruptStateProvider
{
    private final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    private final BatteryController mBatteryController;
    private final ContentResolver mContentResolver;
    private final IDreamManager mDreamManager;
    private HeadsUpManager mHeadsUpManager;
    private final ContentObserver mHeadsUpObserver;
    private final NotificationFilter mNotificationFilter;
    private final PowerManager mPowerManager;
    private final StatusBarStateController mStatusBarStateController;
    private final List<NotificationInterruptSuppressor> mSuppressors;
    @VisibleForTesting
    protected boolean mUseHeadsUp;
    
    public NotificationInterruptStateProviderImpl(final ContentResolver mContentResolver, final PowerManager mPowerManager, final IDreamManager mDreamManager, final AmbientDisplayConfiguration mAmbientDisplayConfiguration, final NotificationFilter mNotificationFilter, final BatteryController mBatteryController, final StatusBarStateController mStatusBarStateController, final HeadsUpManager mHeadsUpManager, final Handler handler) {
        this.mSuppressors = new ArrayList<NotificationInterruptSuppressor>();
        this.mUseHeadsUp = false;
        this.mContentResolver = mContentResolver;
        this.mPowerManager = mPowerManager;
        this.mDreamManager = mDreamManager;
        this.mBatteryController = mBatteryController;
        this.mAmbientDisplayConfiguration = mAmbientDisplayConfiguration;
        this.mNotificationFilter = mNotificationFilter;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mHeadsUpManager = mHeadsUpManager;
        this.mHeadsUpObserver = new ContentObserver(handler) {
            public void onChange(final boolean b) {
                final NotificationInterruptStateProviderImpl this$0 = NotificationInterruptStateProviderImpl.this;
                final boolean mUseHeadsUp = this$0.mUseHeadsUp;
                final ContentResolver access$000 = this$0.mContentResolver;
                boolean mUseHeadsUp2 = false;
                if (Settings$Global.getInt(access$000, "heads_up_notifications_enabled", 0) != 0) {
                    mUseHeadsUp2 = true;
                }
                this$0.mUseHeadsUp = mUseHeadsUp2;
                final StringBuilder sb = new StringBuilder();
                sb.append("heads up is ");
                String str;
                if (NotificationInterruptStateProviderImpl.this.mUseHeadsUp) {
                    str = "enabled";
                }
                else {
                    str = "disabled";
                }
                sb.append(str);
                Log.d("InterruptionStateProvider", sb.toString());
                final boolean mUseHeadsUp3 = NotificationInterruptStateProviderImpl.this.mUseHeadsUp;
                if (mUseHeadsUp != mUseHeadsUp3 && !mUseHeadsUp3) {
                    Log.d("InterruptionStateProvider", "dismissing any existing heads up notification on disable event");
                    NotificationInterruptStateProviderImpl.this.mHeadsUpManager.releaseAllImmediately();
                }
            }
        };
        this.mContentResolver.registerContentObserver(Settings$Global.getUriFor("heads_up_notifications_enabled"), true, this.mHeadsUpObserver);
        this.mContentResolver.registerContentObserver(Settings$Global.getUriFor("ticker_gets_heads_up"), true, this.mHeadsUpObserver);
        this.mHeadsUpObserver.onChange(true);
    }
    
    private boolean canAlertAwakeCommon(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        for (int i = 0; i < this.mSuppressors.size(); ++i) {
            if (this.mSuppressors.get(i).suppressAwakeInterruptions(notificationEntry)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("No alerting: aborted by suppressor: ");
                sb.append(this.mSuppressors.get(i).getName());
                sb.append(" sbnKey=");
                sb.append(sbn.getKey());
                Log.d("InterruptionStateProvider", sb.toString());
                return false;
            }
        }
        if (this.isSnoozedPackage(sbn)) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("No alerting: snoozed package: ");
            sb2.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb2.toString());
            return false;
        }
        if (notificationEntry.hasJustLaunchedFullScreenIntent()) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("No alerting: recent fullscreen: ");
            sb3.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb3.toString());
            return false;
        }
        return true;
    }
    
    private boolean canAlertCommon(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        if (this.mNotificationFilter.shouldFilterOut(notificationEntry)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No alerting: filtered notification: ");
            sb.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb.toString());
            return false;
        }
        if (sbn.isGroup() && sbn.getNotification().suppressAlertingDueToGrouping()) {
            Log.d("InterruptionStateProvider", "No alerting: suppressed due to group alert behavior");
            return false;
        }
        for (int i = 0; i < this.mSuppressors.size(); ++i) {
            if (this.mSuppressors.get(i).suppressInterruptions(notificationEntry)) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("No alerting: aborted by suppressor: ");
                sb2.append(this.mSuppressors.get(i).getName());
                sb2.append(" sbnKey=");
                sb2.append(sbn.getKey());
                Log.d("InterruptionStateProvider", sb2.toString());
                return false;
            }
        }
        return true;
    }
    
    private boolean isSnoozedPackage(final StatusBarNotification statusBarNotification) {
        return this.mHeadsUpManager.isSnoozed(statusBarNotification.getPackageName());
    }
    
    private boolean shouldHeadsUpWhenAwake(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        if (!this.mUseHeadsUp) {
            Log.d("InterruptionStateProvider", "No heads up: no huns");
            return false;
        }
        if (!this.canAlertCommon(notificationEntry)) {
            return false;
        }
        if (!this.canAlertAwakeCommon(notificationEntry)) {
            return false;
        }
        final boolean b = this.mStatusBarStateController.getState() == 0;
        if (notificationEntry.isBubble() && b) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No heads up: in unlocked shade where notification is shown as a bubble: ");
            sb.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb.toString());
            return false;
        }
        if (notificationEntry.shouldSuppressPeek()) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("No heads up: suppressed by DND: ");
            sb2.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb2.toString());
            return false;
        }
        if (notificationEntry.getImportance() < 4) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("No heads up: unimportant notification: ");
            sb3.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb3.toString());
            return false;
        }
        boolean dreaming;
        try {
            dreaming = this.mDreamManager.isDreaming();
        }
        catch (RemoteException ex) {
            Log.e("InterruptionStateProvider", "Failed to query dream manager.", (Throwable)ex);
            dreaming = false;
        }
        if (!this.mPowerManager.isScreenOn() || dreaming) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("No heads up: not in use: ");
            sb4.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb4.toString());
            return false;
        }
        for (int i = 0; i < this.mSuppressors.size(); ++i) {
            if (this.mSuppressors.get(i).suppressAwakeHeadsUp(notificationEntry)) {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append("No heads up: aborted by suppressor: ");
                sb5.append(this.mSuppressors.get(i).getName());
                sb5.append(" sbnKey=");
                sb5.append(sbn.getKey());
                Log.d("InterruptionStateProvider", sb5.toString());
                return false;
            }
        }
        return true;
    }
    
    private boolean shouldHeadsUpWhenDozing(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        if (!this.mAmbientDisplayConfiguration.pulseOnNotificationEnabled(-2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No pulsing: disabled by setting: ");
            sb.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb.toString());
            return false;
        }
        if (this.mBatteryController.isAodPowerSave()) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("No pulsing: disabled by battery saver: ");
            sb2.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb2.toString());
            return false;
        }
        if (!this.canAlertCommon(notificationEntry)) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("No pulsing: notification shouldn't alert: ");
            sb3.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb3.toString());
            return false;
        }
        if (notificationEntry.shouldSuppressAmbient()) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("No pulsing: ambient effect suppressed: ");
            sb4.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb4.toString());
            return false;
        }
        if (notificationEntry.getImportance() < 3) {
            final StringBuilder sb5 = new StringBuilder();
            sb5.append("No pulsing: not important enough: ");
            sb5.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb5.toString());
            return false;
        }
        return true;
    }
    
    @Override
    public void addSuppressor(final NotificationInterruptSuppressor notificationInterruptSuppressor) {
        this.mSuppressors.add(notificationInterruptSuppressor);
    }
    
    @Override
    public boolean shouldBubbleUp(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        if (!this.canAlertCommon(notificationEntry)) {
            return false;
        }
        if (!this.canAlertAwakeCommon(notificationEntry)) {
            return false;
        }
        if (!notificationEntry.canBubble()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No bubble up: not allowed to bubble: ");
            sb.append(sbn.getKey());
            Log.d("InterruptionStateProvider", sb.toString());
            return false;
        }
        if (!notificationEntry.isBubble()) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("No bubble up: notification ");
            sb2.append(sbn.getKey());
            sb2.append(" is bubble? ");
            sb2.append(notificationEntry.isBubble());
            Log.d("InterruptionStateProvider", sb2.toString());
            return false;
        }
        if (notificationEntry.getBubbleMetadata() != null && (notificationEntry.getBubbleMetadata().getShortcutId() != null || notificationEntry.getBubbleMetadata().getIntent() != null)) {
            return true;
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("No bubble up: notification: ");
        sb3.append(sbn.getKey());
        sb3.append(" doesn't have valid metadata");
        Log.d("InterruptionStateProvider", sb3.toString());
        return false;
    }
    
    @Override
    public boolean shouldHeadsUp(final NotificationEntry notificationEntry) {
        if (this.mStatusBarStateController.isDozing()) {
            return this.shouldHeadsUpWhenDozing(notificationEntry);
        }
        return this.shouldHeadsUpWhenAwake(notificationEntry);
    }
    
    @Override
    public boolean shouldLaunchFullScreenIntentWhenAdded(final NotificationEntry notificationEntry) {
        final PendingIntent fullScreenIntent = notificationEntry.getSbn().getNotification().fullScreenIntent;
        final boolean b = true;
        if (fullScreenIntent != null) {
            boolean b2 = b;
            if (!this.shouldHeadsUp(notificationEntry)) {
                return b2;
            }
            if (this.mStatusBarStateController.getState() == 1) {
                b2 = b;
                return b2;
            }
        }
        return false;
    }
}
