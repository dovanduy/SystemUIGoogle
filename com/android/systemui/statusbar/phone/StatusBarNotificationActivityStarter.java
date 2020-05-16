// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.app.Notification;
import android.text.TextUtils;
import android.view.RemoteAnimationAdapter;
import android.app.PendingIntent$OnFinished;
import android.app.ActivityTaskManager;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.RemoteException;
import android.view.View;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import android.app.PendingIntent;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.service.notification.StatusBarNotification;
import android.app.PendingIntent$CanceledException;
import android.util.EventLog;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import java.util.Objects;
import android.os.Looper;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import android.util.Log;
import java.util.concurrent.Executor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.app.KeyguardManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.service.dreams.IDreamManager;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.bubbles.BubbleController;
import com.android.internal.statusbar.IStatusBarService;
import android.os.Handler;
import com.android.systemui.assist.AssistManager;
import dagger.Lazy;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;

public class StatusBarNotificationActivityStarter implements NotificationActivityStarter
{
    protected static final boolean DEBUG;
    private final ActivityIntentHelper mActivityIntentHelper;
    private final ActivityLaunchAnimator mActivityLaunchAnimator;
    private final ActivityStarter mActivityStarter;
    private final Lazy<AssistManager> mAssistManagerLazy;
    private final Handler mBackgroundHandler;
    private final IStatusBarService mBarService;
    private final BubbleController mBubbleController;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private final IDreamManager mDreamManager;
    private final NotificationEntryManager mEntryManager;
    private final FeatureFlags mFeatureFlags;
    private final NotificationGroupManager mGroupManager;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private boolean mIsCollapsingToShowActivityOverLockscreen;
    private final KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    private final LockPatternUtils mLockPatternUtils;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final Handler mMainThreadHandler;
    private final MetricsLogger mMetricsLogger;
    private final NotifCollection mNotifCollection;
    private final NotifPipeline mNotifPipeline;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationPanelViewController mNotificationPanel;
    private final NotificationPresenter mPresenter;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final ShadeController mShadeController;
    private final StatusBar mStatusBar;
    private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarRemoteInputCallback mStatusBarRemoteInputCallback;
    private final StatusBarStateController mStatusBarStateController;
    private final Executor mUiBgExecutor;
    
    static {
        DEBUG = Log.isLoggable("NotifActivityStarter", 3);
    }
    
    private StatusBarNotificationActivityStarter(final Context mContext, final CommandQueue mCommandQueue, final Lazy<AssistManager> mAssistManagerLazy, final NotificationPanelViewController mNotificationPanel, final NotificationPresenter mPresenter, final NotificationEntryManager mEntryManager, final HeadsUpManagerPhone mHeadsUpManager, final ActivityStarter mActivityStarter, final ActivityLaunchAnimator mActivityLaunchAnimator, final IStatusBarService mBarService, final StatusBarStateController mStatusBarStateController, final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager, final KeyguardManager mKeyguardManager, final IDreamManager mDreamManager, final NotificationRemoteInputManager mRemoteInputManager, final StatusBarRemoteInputCallback mStatusBarRemoteInputCallback, final NotificationGroupManager mGroupManager, final NotificationLockscreenUserManager mLockscreenUserManager, final ShadeController mShadeController, final StatusBar mStatusBar, final KeyguardStateController mKeyguardStateController, final NotificationInterruptStateProvider mNotificationInterruptStateProvider, final MetricsLogger mMetricsLogger, final LockPatternUtils mLockPatternUtils, final Handler mMainThreadHandler, final Handler mBackgroundHandler, final Executor mUiBgExecutor, final ActivityIntentHelper mActivityIntentHelper, final BubbleController mBubbleController, final FeatureFlags mFeatureFlags, final NotifPipeline mNotifPipeline, final NotifCollection mNotifCollection) {
        this.mContext = mContext;
        this.mNotificationPanel = mNotificationPanel;
        this.mPresenter = mPresenter;
        this.mHeadsUpManager = mHeadsUpManager;
        this.mActivityLaunchAnimator = mActivityLaunchAnimator;
        this.mBarService = mBarService;
        this.mCommandQueue = mCommandQueue;
        this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
        this.mKeyguardManager = mKeyguardManager;
        this.mDreamManager = mDreamManager;
        this.mRemoteInputManager = mRemoteInputManager;
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mShadeController = mShadeController;
        this.mStatusBar = mStatusBar;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mActivityStarter = mActivityStarter;
        this.mEntryManager = mEntryManager;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mNotificationInterruptStateProvider = mNotificationInterruptStateProvider;
        this.mMetricsLogger = mMetricsLogger;
        this.mAssistManagerLazy = mAssistManagerLazy;
        this.mGroupManager = mGroupManager;
        this.mLockPatternUtils = mLockPatternUtils;
        this.mBackgroundHandler = mBackgroundHandler;
        this.mUiBgExecutor = mUiBgExecutor;
        this.mFeatureFlags = mFeatureFlags;
        this.mNotifPipeline = mNotifPipeline;
        this.mNotifCollection = mNotifCollection;
        if (!mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
                @Override
                public void onPendingEntryAdded(final NotificationEntry notificationEntry) {
                    StatusBarNotificationActivityStarter.this.handleFullScreenIntent(notificationEntry);
                }
            });
        }
        else {
            this.mNotifPipeline.addCollectionListener(new NotifCollectionListener() {
                @Override
                public void onEntryAdded(final NotificationEntry notificationEntry) {
                    StatusBarNotificationActivityStarter.this.handleFullScreenIntent(notificationEntry);
                }
            });
        }
        this.mStatusBarRemoteInputCallback = mStatusBarRemoteInputCallback;
        this.mMainThreadHandler = mMainThreadHandler;
        this.mActivityIntentHelper = mActivityIntentHelper;
        this.mBubbleController = mBubbleController;
    }
    
    private void collapseOnMainThread() {
        if (Looper.getMainLooper().isCurrentThread()) {
            this.mShadeController.collapsePanel();
        }
        else {
            final Handler mMainThreadHandler = this.mMainThreadHandler;
            final ShadeController mShadeController = this.mShadeController;
            Objects.requireNonNull(mShadeController);
            mMainThreadHandler.post((Runnable)new _$$Lambda$XDmf1V0qHGBRkx_V63RRNIpOXuQ(mShadeController));
        }
    }
    
    private Runnable createRemoveRunnable(final NotificationEntry notificationEntry) {
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            return new Runnable() {
                @Override
                public void run() {
                    int n;
                    if (StatusBarNotificationActivityStarter.this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                        n = 1;
                    }
                    else if (StatusBarNotificationActivityStarter.this.mNotificationPanel.hasPulsingNotifications()) {
                        n = 2;
                    }
                    else {
                        n = 3;
                    }
                    final NotifCollection access$400 = StatusBarNotificationActivityStarter.this.mNotifCollection;
                    final NotificationEntry val$entry = notificationEntry;
                    access$400.dismissNotification(val$entry, new DismissedByUserStats(n, 1, NotificationVisibility.obtain(val$entry.getKey(), notificationEntry.getRanking().getRank(), StatusBarNotificationActivityStarter.this.mNotifPipeline.getShadeListCount(), true, NotificationLogger.getNotificationLocation(notificationEntry))));
                }
            };
        }
        return new Runnable() {
            @Override
            public void run() {
                StatusBarNotificationActivityStarter.this.mEntryManager.performRemoveNotification(notificationEntry.getSbn(), 1);
            }
        };
    }
    
    private void expandBubbleStackOnMainThread(final String s) {
        if (Looper.getMainLooper().isCurrentThread()) {
            this.mBubbleController.expandStackAndSelectBubble(s);
        }
        else {
            this.mMainThreadHandler.post((Runnable)new _$$Lambda$StatusBarNotificationActivityStarter$SAG_ctHvOhll_OxtSg_OBbXZGGw(this, s));
        }
    }
    
    private int getVisibleNotificationsCount() {
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            return this.mNotifPipeline.getShadeListCount();
        }
        return this.mEntryManager.getActiveNotificationsCount();
    }
    
    private void handleFullScreenIntent(final NotificationEntry notificationEntry) {
        if (!this.mNotificationInterruptStateProvider.shouldLaunchFullScreenIntentWhenAdded(notificationEntry)) {
            return;
        }
        if (this.shouldSuppressFullScreenIntent(notificationEntry)) {
            if (StatusBarNotificationActivityStarter.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("No Fullscreen intent: suppressed by DND: ");
                sb.append(notificationEntry.getKey());
                Log.d("NotifActivityStarter", sb.toString());
            }
            return;
        }
        else if (notificationEntry.getImportance() < 4) {
            if (StatusBarNotificationActivityStarter.DEBUG) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("No Fullscreen intent: not important enough: ");
                sb2.append(notificationEntry.getKey());
                Log.d("NotifActivityStarter", sb2.toString());
            }
            return;
        }
        else {
            this.mUiBgExecutor.execute(new _$$Lambda$StatusBarNotificationActivityStarter$r9RsnGtfcZuVJem_yK82SlR0x7o(this));
            if (StatusBarNotificationActivityStarter.DEBUG) {
                Log.d("NotifActivityStarter", "Notification has fullScreenIntent; sending fullScreenIntent");
            }
        }
        try {
            EventLog.writeEvent(36002, notificationEntry.getKey());
            notificationEntry.getSbn().getNotification().fullScreenIntent.send();
            notificationEntry.notifyFullScreenIntentLaunched();
            this.mMetricsLogger.count("note_fullscreen", 1);
        }
        catch (PendingIntent$CanceledException ex) {}
    }
    
    private boolean handleNotificationClickAfterKeyguardDismissed(final StatusBarNotification statusBarNotification, final ExpandableNotificationRow expandableNotificationRow, final RemoteInputController remoteInputController, final PendingIntent pendingIntent, final boolean b, final boolean b2, final boolean b3) {
        final HeadsUpManagerPhone mHeadsUpManager = this.mHeadsUpManager;
        if (mHeadsUpManager != null && mHeadsUpManager.isAlerting(statusBarNotification.getKey())) {
            if (this.mPresenter.isPresenterFullyCollapsed()) {
                HeadsUpUtil.setIsClickedHeadsUpNotification((View)expandableNotificationRow, true);
            }
            this.mHeadsUpManager.removeNotification(statusBarNotification.getKey(), true);
        }
        NotificationEntry logicalGroupSummary = null;
        Label_0101: {
            if (shouldAutoCancel(statusBarNotification) && this.mGroupManager.isOnlyChildInGroup(statusBarNotification)) {
                logicalGroupSummary = this.mGroupManager.getLogicalGroupSummary(statusBarNotification);
                if (shouldAutoCancel(logicalGroupSummary.getSbn())) {
                    break Label_0101;
                }
            }
            logicalGroupSummary = null;
        }
        final _$$Lambda$StatusBarNotificationActivityStarter$7mfSGy2G6exE_3cGRoA3iww8GIU $$Lambda$StatusBarNotificationActivityStarter$7mfSGy2G6exE_3cGRoA3iww8GIU = new _$$Lambda$StatusBarNotificationActivityStarter$7mfSGy2G6exE_3cGRoA3iww8GIU(this, statusBarNotification, expandableNotificationRow, remoteInputController, pendingIntent, b, b2, logicalGroupSummary);
        if (b3) {
            this.mShadeController.addPostCollapseAction($$Lambda$StatusBarNotificationActivityStarter$7mfSGy2G6exE_3cGRoA3iww8GIU);
            this.mShadeController.collapsePanel(true);
        }
        else if (this.mKeyguardStateController.isShowing() && this.mStatusBar.isOccluded()) {
            this.mStatusBarKeyguardViewManager.addAfterKeyguardGoneRunnable($$Lambda$StatusBarNotificationActivityStarter$7mfSGy2G6exE_3cGRoA3iww8GIU);
            this.mShadeController.collapsePanel();
        }
        else {
            this.mBackgroundHandler.postAtFrontOfQueue((Runnable)$$Lambda$StatusBarNotificationActivityStarter$7mfSGy2G6exE_3cGRoA3iww8GIU);
        }
        return this.mNotificationPanel.isFullyCollapsed() ^ true;
    }
    
    private void handleNotificationClickAfterPanelCollapsed(final StatusBarNotification p0, final ExpandableNotificationRow p1, final RemoteInputController p2, final PendingIntent p3, final boolean p4, final boolean p5, final NotificationEntry p6) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   android/service/notification/StatusBarNotification.getKey:()Ljava/lang/String;
        //     4: astore          8
        //     6: invokestatic    android/app/ActivityManager.getService:()Landroid/app/IActivityManager;
        //     9: invokeinterface android/app/IActivityManager.resumeAppSwitches:()V
        //    14: iload           5
        //    16: ifeq            77
        //    19: aload           4
        //    21: invokevirtual   android/app/PendingIntent.getCreatorUserHandle:()Landroid/os/UserHandle;
        //    24: invokevirtual   android/os/UserHandle.getIdentifier:()I
        //    27: istore          9
        //    29: aload_0        
        //    30: getfield        com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.mLockPatternUtils:Lcom/android/internal/widget/LockPatternUtils;
        //    33: iload           9
        //    35: invokevirtual   com/android/internal/widget/LockPatternUtils.isSeparateProfileChallengeEnabled:(I)Z
        //    38: ifeq            77
        //    41: aload_0        
        //    42: getfield        com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.mKeyguardManager:Landroid/app/KeyguardManager;
        //    45: iload           9
        //    47: invokevirtual   android/app/KeyguardManager.isDeviceLocked:(I)Z
        //    50: ifeq            77
        //    53: aload_0        
        //    54: getfield        com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.mStatusBarRemoteInputCallback:Lcom/android/systemui/statusbar/phone/StatusBarRemoteInputCallback;
        //    57: iload           9
        //    59: aload           4
        //    61: invokevirtual   android/app/PendingIntent.getIntentSender:()Landroid/content/IntentSender;
        //    64: aload           8
        //    66: invokevirtual   com/android/systemui/statusbar/phone/StatusBarRemoteInputCallback.startWorkChallengeIfNecessary:(ILandroid/content/IntentSender;Ljava/lang/String;)Z
        //    69: ifeq            77
        //    72: aload_0        
        //    73: invokespecial   com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.collapseOnMainThread:()V
        //    76: return         
        //    77: aload_2        
        //    78: invokevirtual   com/android/systemui/statusbar/notification/row/ExpandableNotificationRow.getEntry:()Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;
        //    81: astore          10
        //    83: aload           10
        //    85: invokevirtual   com/android/systemui/statusbar/notification/collection/NotificationEntry.isBubble:()Z
        //    88: istore          11
        //    90: aload           10
        //    92: getfield        com/android/systemui/statusbar/notification/collection/NotificationEntry.remoteInputText:Ljava/lang/CharSequence;
        //    95: invokestatic    android/text/TextUtils.isEmpty:(Ljava/lang/CharSequence;)Z
        //    98: ifne            111
        //   101: aload           10
        //   103: getfield        com/android/systemui/statusbar/notification/collection/NotificationEntry.remoteInputText:Ljava/lang/CharSequence;
        //   106: astore          12
        //   108: goto            114
        //   111: aconst_null    
        //   112: astore          12
        //   114: aload           12
        //   116: invokestatic    android/text/TextUtils.isEmpty:(Ljava/lang/CharSequence;)Z
        //   119: ifne            155
        //   122: aload_3        
        //   123: aload           8
        //   125: invokevirtual   com/android/systemui/statusbar/RemoteInputController.isSpinning:(Ljava/lang/String;)Z
        //   128: ifne            155
        //   131: new             Landroid/content/Intent;
        //   134: dup            
        //   135: invokespecial   android/content/Intent.<init>:()V
        //   138: ldc_w           "android.remoteInputDraft"
        //   141: aload           12
        //   143: invokeinterface java/lang/CharSequence.toString:()Ljava/lang/String;
        //   148: invokevirtual   android/content/Intent.putExtra:(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
        //   151: astore_3       
        //   152: goto            157
        //   155: aconst_null    
        //   156: astore_3       
        //   157: iload           11
        //   159: ifeq            171
        //   162: aload_0        
        //   163: aload           8
        //   165: invokespecial   com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.expandBubbleStackOnMainThread:(Ljava/lang/String;)V
        //   168: goto            183
        //   171: aload_0        
        //   172: aload           4
        //   174: aload_3        
        //   175: aload_2        
        //   176: iload           6
        //   178: iload           5
        //   180: invokespecial   com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.startNotificationIntent:(Landroid/app/PendingIntent;Landroid/content/Intent;Landroid/view/View;ZZ)V
        //   183: iload           5
        //   185: ifne            193
        //   188: iload           11
        //   190: ifeq            208
        //   193: aload_0        
        //   194: getfield        com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.mAssistManagerLazy:Ldagger/Lazy;
        //   197: invokeinterface dagger/Lazy.get:()Ljava/lang/Object;
        //   202: checkcast       Lcom/android/systemui/assist/AssistManager;
        //   205: invokevirtual   com/android/systemui/assist/AssistManager.hideAssist:()V
        //   208: aload_0        
        //   209: invokespecial   com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.shouldCollapse:()Z
        //   212: ifeq            219
        //   215: aload_0        
        //   216: invokespecial   com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.collapseOnMainThread:()V
        //   219: aload_0        
        //   220: invokespecial   com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.getVisibleNotificationsCount:()I
        //   223: istore          9
        //   225: aload           8
        //   227: aload           10
        //   229: invokevirtual   com/android/systemui/statusbar/notification/collection/NotificationEntry.getRanking:()Landroid/service/notification/NotificationListenerService$Ranking;
        //   232: invokevirtual   android/service/notification/NotificationListenerService$Ranking.getRank:()I
        //   235: iload           9
        //   237: iconst_1       
        //   238: aload           10
        //   240: invokestatic    com/android/systemui/statusbar/notification/logging/NotificationLogger.getNotificationLocation:(Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;)Lcom/android/internal/statusbar/NotificationVisibility$NotificationLocation;
        //   243: invokestatic    com/android/internal/statusbar/NotificationVisibility.obtain:(Ljava/lang/String;IIZLcom/android/internal/statusbar/NotificationVisibility$NotificationLocation;)Lcom/android/internal/statusbar/NotificationVisibility;
        //   246: astore_3       
        //   247: aload_0        
        //   248: getfield        com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.mBarService:Lcom/android/internal/statusbar/IStatusBarService;
        //   251: aload           8
        //   253: aload_3        
        //   254: invokeinterface com/android/internal/statusbar/IStatusBarService.onNotificationClick:(Ljava/lang/String;Lcom/android/internal/statusbar/NotificationVisibility;)V
        //   259: iload           11
        //   261: ifne            302
        //   264: aload           7
        //   266: ifnull          275
        //   269: aload_0        
        //   270: aload           7
        //   272: invokespecial   com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.removeNotification:(Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;)V
        //   275: aload_1        
        //   276: invokestatic    com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.shouldAutoCancel:(Landroid/service/notification/StatusBarNotification;)Z
        //   279: ifne            294
        //   282: aload_0        
        //   283: getfield        com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.mRemoteInputManager:Lcom/android/systemui/statusbar/NotificationRemoteInputManager;
        //   286: aload           8
        //   288: invokevirtual   com/android/systemui/statusbar/NotificationRemoteInputManager.isNotificationKeptForRemoteInputHistory:(Ljava/lang/String;)Z
        //   291: ifeq            302
        //   294: aload_0        
        //   295: aload_2        
        //   296: invokevirtual   com/android/systemui/statusbar/notification/row/ExpandableNotificationRow.getEntry:()Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;
        //   299: invokespecial   com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.removeNotification:(Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;)V
        //   302: aload_0        
        //   303: iconst_0       
        //   304: putfield        com/android/systemui/statusbar/phone/StatusBarNotificationActivityStarter.mIsCollapsingToShowActivityOverLockscreen:Z
        //   307: return         
        //   308: astore          12
        //   310: goto            14
        //   313: astore_3       
        //   314: goto            259
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  6      14     308    313    Landroid/os/RemoteException;
        //  247    259    313    317    Landroid/os/RemoteException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0259:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void removeNotification(final NotificationEntry notificationEntry) {
        this.mMainThreadHandler.post((Runnable)new _$$Lambda$StatusBarNotificationActivityStarter$Onf3rO_LBHBE1T4g9IhbNgDeFyg(this, notificationEntry));
    }
    
    private static boolean shouldAutoCancel(final StatusBarNotification statusBarNotification) {
        final int flags = statusBarNotification.getNotification().flags;
        return (flags & 0x10) == 0x10 && (flags & 0x40) == 0x0;
    }
    
    private boolean shouldCollapse() {
        return this.mStatusBarStateController.getState() != 0 || !this.mActivityLaunchAnimator.isAnimationPending();
    }
    
    private boolean shouldSuppressFullScreenIntent(final NotificationEntry notificationEntry) {
        return this.mPresenter.isDeviceInVrMode() || notificationEntry.shouldSuppressFullScreenIntent();
    }
    
    private void startNotificationIntent(final PendingIntent pendingIntent, final Intent intent, final View view, final boolean b, final boolean b2) {
        final RemoteAnimationAdapter launchAnimation = this.mActivityLaunchAnimator.getLaunchAnimation(view, b);
        while (true) {
            if (launchAnimation != null) {
                try {
                    ActivityTaskManager.getService().registerRemoteAnimationForNextActivityStart(pendingIntent.getCreatorPackage(), launchAnimation);
                    this.mActivityLaunchAnimator.setLaunchResult(pendingIntent.sendAndReturnResult(this.mContext, 0, intent, (PendingIntent$OnFinished)null, (Handler)null, (String)null, StatusBar.getActivityOptions(launchAnimation)), b2);
                }
                catch (RemoteException | PendingIntent$CanceledException ex) {
                    final Object o;
                    final Object obj = o;
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Sending contentIntent failed: ");
                    sb.append(obj);
                    Log.w("NotifActivityStarter", sb.toString());
                }
                return;
            }
            continue;
        }
    }
    
    @Override
    public boolean isCollapsingToShowActivityOverLockscreen() {
        return this.mIsCollapsingToShowActivityOverLockscreen;
    }
    
    @Override
    public void onNotificationClicked(final StatusBarNotification statusBarNotification, final ExpandableNotificationRow expandableNotificationRow) {
        final RemoteInputController controller = this.mRemoteInputManager.getController();
        if (controller.isRemoteInputActive(expandableNotificationRow.getEntry()) && !TextUtils.isEmpty(expandableNotificationRow.getActiveRemoteInputText())) {
            controller.closeRemoteInputs();
            return;
        }
        final Notification notification = statusBarNotification.getNotification();
        PendingIntent pendingIntent = notification.contentIntent;
        if (pendingIntent == null) {
            pendingIntent = notification.fullScreenIntent;
        }
        final boolean bubble = expandableNotificationRow.getEntry().isBubble();
        if (pendingIntent == null && !bubble) {
            Log.e("NotifActivityStarter", "onNotificationClicked called for non-clickable notification!");
            return;
        }
        final boolean b = pendingIntent != null && pendingIntent.isActivity() && !bubble;
        final boolean b2 = b && this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
        final boolean occluded = this.mStatusBar.isOccluded();
        final boolean b3 = this.mKeyguardStateController.isShowing() && pendingIntent != null && this.mActivityIntentHelper.wouldShowOverLockscreen(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
        final _$$Lambda$StatusBarNotificationActivityStarter$Pyeef5xkti2nTtS5zKZgWAnZicA $$Lambda$StatusBarNotificationActivityStarter$Pyeef5xkti2nTtS5zKZgWAnZicA = new _$$Lambda$StatusBarNotificationActivityStarter$Pyeef5xkti2nTtS5zKZgWAnZicA(this, statusBarNotification, expandableNotificationRow, controller, pendingIntent, b, occluded, b3);
        if (b3) {
            this.mIsCollapsingToShowActivityOverLockscreen = true;
            ((ActivityStarter.OnDismissAction)$$Lambda$StatusBarNotificationActivityStarter$Pyeef5xkti2nTtS5zKZgWAnZicA).onDismiss();
        }
        else {
            this.mActivityStarter.dismissKeyguardThenExecute((ActivityStarter.OnDismissAction)$$Lambda$StatusBarNotificationActivityStarter$Pyeef5xkti2nTtS5zKZgWAnZicA, null, b2);
        }
    }
    
    @Override
    public void startNotificationGutsIntent(final Intent intent, final int n, final ExpandableNotificationRow expandableNotificationRow) {
        this.mActivityStarter.dismissKeyguardThenExecute((ActivityStarter.OnDismissAction)new _$$Lambda$StatusBarNotificationActivityStarter$cyhnCXwOFANppGr5Crfg0gR112k(this, intent, expandableNotificationRow, n), null, false);
    }
    
    public static class Builder
    {
        private final ActivityIntentHelper mActivityIntentHelper;
        private ActivityLaunchAnimator mActivityLaunchAnimator;
        private final ActivityStarter mActivityStarter;
        private final Lazy<AssistManager> mAssistManagerLazy;
        private final Handler mBackgroundHandler;
        private final BubbleController mBubbleController;
        private final CommandQueue mCommandQueue;
        private final Context mContext;
        private final IDreamManager mDreamManager;
        private final NotificationEntryManager mEntryManager;
        private final FeatureFlags mFeatureFlags;
        private final NotificationGroupManager mGroupManager;
        private final HeadsUpManagerPhone mHeadsUpManager;
        private final KeyguardManager mKeyguardManager;
        private final KeyguardStateController mKeyguardStateController;
        private final LockPatternUtils mLockPatternUtils;
        private final NotificationLockscreenUserManager mLockscreenUserManager;
        private final Handler mMainThreadHandler;
        private final MetricsLogger mMetricsLogger;
        private final NotifCollection mNotifCollection;
        private final NotifPipeline mNotifPipeline;
        private NotificationInterruptStateProvider mNotificationInterruptStateProvider;
        private NotificationPanelViewController mNotificationPanelViewController;
        private NotificationPresenter mNotificationPresenter;
        private final StatusBarRemoteInputCallback mRemoteInputCallback;
        private final NotificationRemoteInputManager mRemoteInputManager;
        private final ShadeController mShadeController;
        private StatusBar mStatusBar;
        private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
        private final IStatusBarService mStatusBarService;
        private final StatusBarStateController mStatusBarStateController;
        private final Executor mUiBgExecutor;
        
        public Builder(final Context mContext, final CommandQueue mCommandQueue, final Lazy<AssistManager> mAssistManagerLazy, final NotificationEntryManager mEntryManager, final HeadsUpManagerPhone mHeadsUpManager, final ActivityStarter mActivityStarter, final IStatusBarService mStatusBarService, final StatusBarStateController mStatusBarStateController, final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager, final KeyguardManager mKeyguardManager, final IDreamManager mDreamManager, final NotificationRemoteInputManager mRemoteInputManager, final StatusBarRemoteInputCallback mRemoteInputCallback, final NotificationGroupManager mGroupManager, final NotificationLockscreenUserManager mLockscreenUserManager, final KeyguardStateController mKeyguardStateController, final NotificationInterruptStateProvider mNotificationInterruptStateProvider, final MetricsLogger mMetricsLogger, final LockPatternUtils mLockPatternUtils, final Handler mMainThreadHandler, final Handler mBackgroundHandler, final Executor mUiBgExecutor, final ActivityIntentHelper mActivityIntentHelper, final BubbleController mBubbleController, final ShadeController mShadeController, final FeatureFlags mFeatureFlags, final NotifPipeline mNotifPipeline, final NotifCollection mNotifCollection) {
            this.mContext = mContext;
            this.mCommandQueue = mCommandQueue;
            this.mAssistManagerLazy = mAssistManagerLazy;
            this.mEntryManager = mEntryManager;
            this.mHeadsUpManager = mHeadsUpManager;
            this.mActivityStarter = mActivityStarter;
            this.mStatusBarService = mStatusBarService;
            this.mStatusBarStateController = mStatusBarStateController;
            this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
            this.mKeyguardManager = mKeyguardManager;
            this.mDreamManager = mDreamManager;
            this.mRemoteInputManager = mRemoteInputManager;
            this.mRemoteInputCallback = mRemoteInputCallback;
            this.mGroupManager = mGroupManager;
            this.mLockscreenUserManager = mLockscreenUserManager;
            this.mKeyguardStateController = mKeyguardStateController;
            this.mNotificationInterruptStateProvider = mNotificationInterruptStateProvider;
            this.mMetricsLogger = mMetricsLogger;
            this.mLockPatternUtils = mLockPatternUtils;
            this.mMainThreadHandler = mMainThreadHandler;
            this.mBackgroundHandler = mBackgroundHandler;
            this.mUiBgExecutor = mUiBgExecutor;
            this.mActivityIntentHelper = mActivityIntentHelper;
            this.mBubbleController = mBubbleController;
            this.mShadeController = mShadeController;
            this.mFeatureFlags = mFeatureFlags;
            this.mNotifPipeline = mNotifPipeline;
            this.mNotifCollection = mNotifCollection;
        }
        
        public StatusBarNotificationActivityStarter build() {
            return new StatusBarNotificationActivityStarter(this.mContext, this.mCommandQueue, this.mAssistManagerLazy, this.mNotificationPanelViewController, this.mNotificationPresenter, this.mEntryManager, this.mHeadsUpManager, this.mActivityStarter, this.mActivityLaunchAnimator, this.mStatusBarService, this.mStatusBarStateController, this.mStatusBarKeyguardViewManager, this.mKeyguardManager, this.mDreamManager, this.mRemoteInputManager, this.mRemoteInputCallback, this.mGroupManager, this.mLockscreenUserManager, this.mShadeController, this.mStatusBar, this.mKeyguardStateController, this.mNotificationInterruptStateProvider, this.mMetricsLogger, this.mLockPatternUtils, this.mMainThreadHandler, this.mBackgroundHandler, this.mUiBgExecutor, this.mActivityIntentHelper, this.mBubbleController, this.mFeatureFlags, this.mNotifPipeline, this.mNotifCollection, null);
        }
        
        public Builder setActivityLaunchAnimator(final ActivityLaunchAnimator mActivityLaunchAnimator) {
            this.mActivityLaunchAnimator = mActivityLaunchAnimator;
            return this;
        }
        
        public Builder setNotificationPanelViewController(final NotificationPanelViewController mNotificationPanelViewController) {
            this.mNotificationPanelViewController = mNotificationPanelViewController;
            return this;
        }
        
        public Builder setNotificationPresenter(final NotificationPresenter mNotificationPresenter) {
            this.mNotificationPresenter = mNotificationPresenter;
            return this;
        }
        
        public Builder setStatusBar(final StatusBar mStatusBar) {
            this.mStatusBar = mStatusBar;
            return this;
        }
    }
}
