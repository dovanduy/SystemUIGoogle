// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import android.view.View;
import android.view.View$OnAttachStateChangeListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import java.util.Objects;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.util.time.SystemClock;

public class ExpandableNotificationRowController
{
    private final ActivatableNotificationViewController mActivatableNotificationViewController;
    private final boolean mAllowLongPress;
    private final String mAppName;
    private final SystemClock mClock;
    private final ExpandableNotificationRow.ExpansionLogger mExpansionLogger;
    private final FalsingManager mFalsingManager;
    private final HeadsUpManager mHeadsUpManager;
    private final KeyguardBypassController mKeyguardBypassController;
    private final NotificationMediaManager mMediaManager;
    private final NotificationGroupManager mNotificationGroupManager;
    private final NotificationGutsManager mNotificationGutsManager;
    private final String mNotificationKey;
    private final NotificationLogger mNotificationLogger;
    private final ExpandableNotificationRow.OnAppOpsClickListener mOnAppOpsClickListener;
    private Runnable mOnDismissRunnable;
    private final ExpandableNotificationRow.OnExpandClickListener mOnExpandClickListener;
    private final PeopleNotificationIdentifier mPeopleNotificationIdentifier;
    private final PluginManager mPluginManager;
    private final RowContentBindStage mRowContentBindStage;
    private final StatusBarStateController mStatusBarStateController;
    private final ExpandableNotificationRow mView;
    
    public ExpandableNotificationRowController(final ExpandableNotificationRow mView, final ActivatableNotificationViewController mActivatableNotificationViewController, final NotificationMediaManager mMediaManager, final PluginManager mPluginManager, final SystemClock mClock, final String mAppName, final String mNotificationKey, final KeyguardBypassController mKeyguardBypassController, final NotificationGroupManager mNotificationGroupManager, final RowContentBindStage mRowContentBindStage, final NotificationLogger mNotificationLogger, final HeadsUpManager mHeadsUpManager, final ExpandableNotificationRow.OnExpandClickListener mOnExpandClickListener, final StatusBarStateController mStatusBarStateController, final NotificationRowContentBinder.InflationCallback inflationCallback, final NotificationGutsManager notificationGutsManager, final boolean mAllowLongPress, final Runnable mOnDismissRunnable, final FalsingManager mFalsingManager, final PeopleNotificationIdentifier mPeopleNotificationIdentifier) {
        this.mExpansionLogger = new _$$Lambda$ExpandableNotificationRowController$7PRoCj_f2CPB0eC3liBvfR80zWU(this);
        this.mView = mView;
        this.mActivatableNotificationViewController = mActivatableNotificationViewController;
        this.mMediaManager = mMediaManager;
        this.mPluginManager = mPluginManager;
        this.mClock = mClock;
        this.mAppName = mAppName;
        this.mNotificationKey = mNotificationKey;
        this.mKeyguardBypassController = mKeyguardBypassController;
        this.mNotificationGroupManager = mNotificationGroupManager;
        this.mRowContentBindStage = mRowContentBindStage;
        this.mNotificationLogger = mNotificationLogger;
        this.mHeadsUpManager = mHeadsUpManager;
        this.mOnExpandClickListener = mOnExpandClickListener;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mNotificationGutsManager = notificationGutsManager;
        this.mOnDismissRunnable = mOnDismissRunnable;
        Objects.requireNonNull(notificationGutsManager);
        this.mOnAppOpsClickListener = new _$$Lambda$oy9pBf4KjrW7ZRpgHkpOCIaDYlg(notificationGutsManager);
        this.mAllowLongPress = mAllowLongPress;
        this.mFalsingManager = mFalsingManager;
        this.mPeopleNotificationIdentifier = mPeopleNotificationIdentifier;
    }
    
    private void logNotificationExpansion(final String s, final boolean b, final boolean b2) {
        this.mNotificationLogger.onExpansionChanged(s, b, b2);
    }
    
    public void init() {
        this.mActivatableNotificationViewController.init();
        this.mView.initialize(this.mAppName, this.mNotificationKey, this.mExpansionLogger, this.mKeyguardBypassController, this.mNotificationGroupManager, this.mHeadsUpManager, this.mRowContentBindStage, this.mOnExpandClickListener, this.mMediaManager, this.mOnAppOpsClickListener, this.mFalsingManager, this.mStatusBarStateController, this.mPeopleNotificationIdentifier);
        this.mView.setOnDismissRunnable(this.mOnDismissRunnable);
        this.mView.setDescendantFocusability(393216);
        if (this.mAllowLongPress) {
            final ExpandableNotificationRow mView = this.mView;
            final NotificationGutsManager mNotificationGutsManager = this.mNotificationGutsManager;
            Objects.requireNonNull(mNotificationGutsManager);
            mView.setLongPressListener((ExpandableNotificationRow.LongPressListener)new _$$Lambda$0lGYUT66Z7cr4TZs4rdZ8M7DQkw(mNotificationGutsManager));
        }
        if (NotificationRemoteInputManager.ENABLE_REMOTE_INPUT) {
            this.mView.setDescendantFocusability(131072);
        }
        this.mView.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
                ExpandableNotificationRowController.this.mView.getEntry().setInitializationTime(ExpandableNotificationRowController.this.mClock.elapsedRealtime());
                ExpandableNotificationRowController.this.mPluginManager.addPluginListener((PluginListener<Plugin>)ExpandableNotificationRowController.this.mView, NotificationMenuRowPlugin.class, false);
            }
            
            public void onViewDetachedFromWindow(final View view) {
                ExpandableNotificationRowController.this.mPluginManager.removePluginListener(ExpandableNotificationRowController.this.mView);
            }
        });
    }
    
    public void setOnDismissRunnable(final Runnable runnable) {
        this.mOnDismissRunnable = runnable;
        this.mView.setOnDismissRunnable(runnable);
    }
}
