// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.os.Bundle;
import android.os.Parcelable;
import android.content.Intent;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import java.util.Objects;
import android.content.IntentSender;
import android.app.PendingIntent;
import android.view.ViewParent;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import android.os.RemoteException;
import android.app.ActivityManager;
import android.content.IntentFilter;
import android.os.UserHandle;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import android.view.View;
import android.os.Handler;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.BroadcastReceiver;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationRemoteInputManager;

public class StatusBarRemoteInputCallback implements Callback, Callbacks, StateListener
{
    private final ActivityIntentHelper mActivityIntentHelper;
    private final ActivityStarter mActivityStarter;
    protected BroadcastReceiver mChallengeReceiver;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private int mDisabled2;
    private final NotificationGroupManager mGroupManager;
    private KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private Handler mMainHandler;
    private View mPendingRemoteInputView;
    private View mPendingWorkRemoteInputView;
    private final ShadeController mShadeController;
    private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final SysuiStatusBarStateController mStatusBarStateController;
    
    public StatusBarRemoteInputCallback(final Context mContext, final NotificationGroupManager mGroupManager, final NotificationLockscreenUserManager mLockscreenUserManager, final KeyguardStateController mKeyguardStateController, final StatusBarStateController statusBarStateController, final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager, final ActivityStarter mActivityStarter, final ShadeController mShadeController, final CommandQueue mCommandQueue) {
        this.mChallengeReceiver = new ChallengeReceiver();
        this.mMainHandler = new Handler();
        this.mContext = mContext;
        this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
        this.mShadeController = mShadeController;
        mContext.registerReceiverAsUser(this.mChallengeReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.DEVICE_LOCKED_CHANGED"), (String)null, (Handler)null);
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mKeyguardStateController = mKeyguardStateController;
        final SysuiStatusBarStateController mStatusBarStateController = (SysuiStatusBarStateController)statusBarStateController;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mActivityStarter = mActivityStarter;
        mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.mKeyguardManager = (KeyguardManager)mContext.getSystemService((Class)KeyguardManager.class);
        (this.mCommandQueue = mCommandQueue).addCallback((CommandQueue.Callbacks)this);
        this.mActivityIntentHelper = new ActivityIntentHelper(this.mContext);
        this.mGroupManager = mGroupManager;
    }
    
    @Override
    public void disable(final int n, final int n2, final int mDisabled2, final boolean b) {
        if (n == this.mContext.getDisplayId()) {
            this.mDisabled2 = mDisabled2;
        }
    }
    
    @Override
    public boolean handleRemoteViewClick(final View view, final PendingIntent pendingIntent, final ClickHandler clickHandler) {
        if (pendingIntent.isActivity()) {
            this.mActivityStarter.dismissKeyguardThenExecute((ActivityStarter.OnDismissAction)new _$$Lambda$StatusBarRemoteInputCallback$8d3SjU56C80S4rq_vR5b0crRuYY(this, clickHandler), null, this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId()));
            return true;
        }
        return clickHandler.handleClick();
    }
    
    @Override
    public void onLockedRemoteInput(final ExpandableNotificationRow expandableNotificationRow, final View mPendingRemoteInputView) {
        if (!expandableNotificationRow.isPinned()) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        }
        this.mStatusBarKeyguardViewManager.showBouncer(true);
        this.mPendingRemoteInputView = mPendingRemoteInputView;
    }
    
    @Override
    public void onLockedWorkRemoteInput(final int n, final ExpandableNotificationRow expandableNotificationRow, final View mPendingWorkRemoteInputView) {
        this.mCommandQueue.animateCollapsePanels();
        this.startWorkChallengeIfNecessary(n, null, null);
        this.mPendingWorkRemoteInputView = mPendingWorkRemoteInputView;
    }
    
    @Override
    public void onMakeExpandedVisibleForRemoteInput(final ExpandableNotificationRow expandableNotificationRow, final View obj) {
        if (this.mKeyguardStateController.isShowing()) {
            this.onLockedRemoteInput(expandableNotificationRow, obj);
        }
        else {
            if (expandableNotificationRow.isChildInGroup() && !expandableNotificationRow.areChildrenExpanded()) {
                this.mGroupManager.toggleGroupExpansion(expandableNotificationRow.getEntry().getSbn());
            }
            expandableNotificationRow.setUserExpanded(true);
            final NotificationContentView privateLayout = expandableNotificationRow.getPrivateLayout();
            Objects.requireNonNull(obj);
            privateLayout.setOnExpandedVisibleListener(new _$$Lambda$MVkYf3B_uVxXy7rxrXvHR4SUXEU(obj));
        }
    }
    
    @Override
    public void onStateChanged(final int n) {
        final boolean b = this.mPendingRemoteInputView != null;
        if (n == 0 && (this.mStatusBarStateController.leaveOpenOnKeyguardHide() || b) && !this.mStatusBarStateController.isKeyguardRequested()) {
            if (b) {
                final Handler mMainHandler = this.mMainHandler;
                final View mPendingRemoteInputView = this.mPendingRemoteInputView;
                Objects.requireNonNull(mPendingRemoteInputView);
                mMainHandler.post((Runnable)new _$$Lambda$au9TYywfgPbmO65RQz_jg3_3Qz0(mPendingRemoteInputView));
            }
            this.mPendingRemoteInputView = null;
        }
    }
    
    protected void onWorkChallengeChanged() {
        this.mLockscreenUserManager.updatePublicMode();
        if (this.mPendingWorkRemoteInputView != null && !this.mLockscreenUserManager.isAnyProfilePublicMode()) {
            this.mShadeController.postOnShadeExpanded(new _$$Lambda$StatusBarRemoteInputCallback$R1k7Wh1xlx_jAMn9HjU1lr6mXXE(this));
            this.mShadeController.instantExpandNotificationsPanel();
        }
    }
    
    @Override
    public boolean shouldHandleRemoteInput(final View view, final PendingIntent pendingIntent) {
        return (this.mDisabled2 & 0x4) != 0x0;
    }
    
    boolean startWorkChallengeIfNecessary(final int n, final IntentSender intentSender, final String s) {
        this.mPendingWorkRemoteInputView = null;
        final Intent confirmDeviceCredentialIntent = this.mKeyguardManager.createConfirmDeviceCredentialIntent((CharSequence)null, (CharSequence)null, n);
        if (confirmDeviceCredentialIntent == null) {
            return false;
        }
        final Intent intent = new Intent("com.android.systemui.statusbar.work_challenge_unlocked_notification_action");
        intent.putExtra("android.intent.extra.INTENT", (Parcelable)intentSender);
        intent.putExtra("android.intent.extra.INDEX", s);
        intent.setPackage(this.mContext.getPackageName());
        confirmDeviceCredentialIntent.putExtra("android.intent.extra.INTENT", (Parcelable)PendingIntent.getBroadcast(this.mContext, 0, intent, 1409286144).getIntentSender());
        try {
            ActivityManager.getService().startConfirmDeviceCredentialIntent(confirmDeviceCredentialIntent, (Bundle)null);
            return true;
        }
        catch (RemoteException ex) {
            return true;
        }
    }
    
    protected class ChallengeReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            final int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            if ("android.intent.action.DEVICE_LOCKED_CHANGED".equals(action) && intExtra != StatusBarRemoteInputCallback.this.mLockscreenUserManager.getCurrentUserId() && StatusBarRemoteInputCallback.this.mLockscreenUserManager.isCurrentProfile(intExtra)) {
                StatusBarRemoteInputCallback.this.onWorkChallengeChanged();
            }
        }
    }
}
