// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import java.util.Objects;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Set;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.settingslib.notification.ConversationIconFactory;
import com.android.systemui.R$dimen;
import android.util.IconDrawableFactory;
import android.os.UserHandle;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.Log;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.app.NotificationChannel;
import android.util.ArraySet;
import android.view.View;
import android.content.pm.PackageManager;
import android.service.notification.StatusBarNotification;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import android.content.pm.ShortcutManager;
import com.android.systemui.statusbar.NotificationPresenter;
import android.app.INotificationManager;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.internal.logging.MetricsLogger;
import android.os.Handler;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import android.content.pm.LauncherApps;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.Dumpable;

public class NotificationGutsManager implements Dumpable, NotificationLifetimeExtender
{
    private final AccessibilityManager mAccessibilityManager;
    private final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private NotificationMenuRowPlugin.MenuItem mGutsMenuItem;
    private final HighPriorityProvider mHighPriorityProvider;
    @VisibleForTesting
    protected String mKeyToRemoveOnGutsClosed;
    private final LauncherApps mLauncherApps;
    private NotificationListContainer mListContainer;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final Handler mMainHandler;
    private final MetricsLogger mMetricsLogger;
    private NotificationActivityStarter mNotificationActivityStarter;
    private NotificationGuts mNotificationGutsExposed;
    private NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    private final INotificationManager mNotificationManager;
    private OnSettingsClickListener mOnSettingsClickListener;
    private Runnable mOpenRunnable;
    private NotificationPresenter mPresenter;
    private final ShortcutManager mShortcutManager;
    private final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    private final VisualStabilityManager mVisualStabilityManager;
    
    public NotificationGutsManager(final Context mContext, final VisualStabilityManager mVisualStabilityManager, final Lazy<StatusBar> mStatusBarLazy, final Handler mMainHandler, final AccessibilityManager mAccessibilityManager, final HighPriorityProvider mHighPriorityProvider, final INotificationManager mNotificationManager, final LauncherApps mLauncherApps, final ShortcutManager mShortcutManager) {
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mLockscreenUserManager = Dependency.get(NotificationLockscreenUserManager.class);
        this.mStatusBarStateController = Dependency.get(StatusBarStateController.class);
        this.mDeviceProvisionedController = Dependency.get(DeviceProvisionedController.class);
        this.mContext = mContext;
        this.mVisualStabilityManager = mVisualStabilityManager;
        this.mStatusBarLazy = mStatusBarLazy;
        this.mMainHandler = mMainHandler;
        this.mAccessibilityManager = mAccessibilityManager;
        this.mHighPriorityProvider = mHighPriorityProvider;
        this.mNotificationManager = mNotificationManager;
        this.mLauncherApps = mLauncherApps;
        this.mShortcutManager = mShortcutManager;
    }
    
    private boolean bindGuts(final ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.ensureGutsInflated();
        return this.bindGuts(expandableNotificationRow, this.mGutsMenuItem);
    }
    
    private void initializeAppOpsInfo(final ExpandableNotificationRow expandableNotificationRow, final AppOpsInfo appOpsInfo) {
        final NotificationGuts guts = expandableNotificationRow.getGuts();
        final StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        final PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(this.mContext, sbn.getUser().getIdentifier());
        final _$$Lambda$NotificationGutsManager$QUX76CVRNteGCzCinyuNeuYX3tU $$Lambda$NotificationGutsManager$QUX76CVRNteGCzCinyuNeuYX3tU = new _$$Lambda$NotificationGutsManager$QUX76CVRNteGCzCinyuNeuYX3tU(this, guts, expandableNotificationRow);
        if (!expandableNotificationRow.getEntry().mActiveAppOps.isEmpty()) {
            appOpsInfo.bindGuts(packageManagerForUser, (AppOpsInfo.OnSettingsClickListener)$$Lambda$NotificationGutsManager$QUX76CVRNteGCzCinyuNeuYX3tU, sbn, expandableNotificationRow.getEntry().mActiveAppOps);
        }
    }
    
    private void initializeSnoozeView(final ExpandableNotificationRow expandableNotificationRow, final NotificationSnooze notificationSnooze) {
        final NotificationGuts guts = expandableNotificationRow.getGuts();
        final StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        notificationSnooze.setSnoozeListener(this.mListContainer.getSwipeActionHelper());
        notificationSnooze.setStatusBarNotification(sbn);
        notificationSnooze.setSnoozeOptions(expandableNotificationRow.getEntry().getSnoozeCriteria());
        guts.setHeightChangedListener((NotificationGuts.OnHeightChangedListener)new _$$Lambda$NotificationGutsManager$xtHxMW6jrIgJGugFgxSSg6aT080(this, expandableNotificationRow));
    }
    
    private void startAppDetailsSettingsActivity(final String s, final int n, final NotificationChannel notificationChannel, final ExpandableNotificationRow expandableNotificationRow) {
        final Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", s, (String)null));
        intent.putExtra("android.provider.extra.APP_PACKAGE", s);
        intent.putExtra("app_uid", n);
        if (notificationChannel != null) {
            intent.putExtra(":settings:fragment_args_key", notificationChannel.getId());
        }
        this.mNotificationActivityStarter.startNotificationGutsIntent(intent, n, expandableNotificationRow);
    }
    
    private void startAppNotificationSettingsActivity(final String s, final int n, final NotificationChannel notificationChannel, final ExpandableNotificationRow expandableNotificationRow) {
        final Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", s);
        intent.putExtra("app_uid", n);
        if (notificationChannel != null) {
            final Bundle bundle = new Bundle();
            intent.putExtra(":settings:fragment_args_key", notificationChannel.getId());
            bundle.putString(":settings:fragment_args_key", notificationChannel.getId());
            intent.putExtra(":settings:show_fragment_args", bundle);
        }
        this.mNotificationActivityStarter.startNotificationGutsIntent(intent, n, expandableNotificationRow);
    }
    
    @VisibleForTesting
    protected boolean bindGuts(final ExpandableNotificationRow expandableNotificationRow, final NotificationMenuRowPlugin.MenuItem gutsView) {
        final StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        expandableNotificationRow.setGutsView(gutsView);
        expandableNotificationRow.setTag((Object)sbn.getPackageName());
        expandableNotificationRow.getGuts().setClosedListener((NotificationGuts.OnGutsClosedListener)new _$$Lambda$NotificationGutsManager$lbHSFb83h5SRmJTPUlzactX7_1Q(this, expandableNotificationRow, sbn));
        final View gutsView2 = gutsView.getGutsView();
        try {
            if (gutsView2 instanceof NotificationSnooze) {
                this.initializeSnoozeView(expandableNotificationRow, (NotificationSnooze)gutsView2);
            }
            else if (gutsView2 instanceof AppOpsInfo) {
                this.initializeAppOpsInfo(expandableNotificationRow, (AppOpsInfo)gutsView2);
            }
            else if (gutsView2 instanceof NotificationInfo) {
                this.initializeNotificationInfo(expandableNotificationRow, (NotificationInfo)gutsView2);
            }
            else if (gutsView2 instanceof NotificationConversationInfo) {
                this.initializeConversationNotificationInfo(expandableNotificationRow, (NotificationConversationInfo)gutsView2);
            }
            return true;
        }
        catch (Exception ex) {
            Log.e("NotificationGutsManager", "error binding guts", (Throwable)ex);
            return false;
        }
    }
    
    public void closeAndSaveGuts(final boolean b, final boolean b2, final boolean b3, final int n, final int n2, final boolean b4) {
        final NotificationGuts mNotificationGutsExposed = this.mNotificationGutsExposed;
        if (mNotificationGutsExposed != null) {
            mNotificationGutsExposed.removeCallbacks(this.mOpenRunnable);
            this.mNotificationGutsExposed.closeControls(b, b3, n, n2, b2);
        }
        if (b4) {
            this.mListContainer.resetExposedMenuView(false, true);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("NotificationGutsManager state:");
        printWriter.print("  mKeyToRemoveOnGutsClosed: ");
        printWriter.println(this.mKeyToRemoveOnGutsClosed);
    }
    
    public NotificationGuts getExposedGuts() {
        return this.mNotificationGutsExposed;
    }
    
    @VisibleForTesting
    void initializeConversationNotificationInfo(final ExpandableNotificationRow expandableNotificationRow, final NotificationConversationInfo notificationConversationInfo) throws Exception {
        final NotificationGuts guts = expandableNotificationRow.getGuts();
        final StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        final String packageName = sbn.getPackageName();
        final UserHandle user = sbn.getUser();
        final PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(this.mContext, user.getIdentifier());
        final _$$Lambda$NotificationGutsManager$9FsF_zUJ5zlFxqpy3aSUEhBYXvI $$Lambda$NotificationGutsManager$9FsF_zUJ5zlFxqpy3aSUEhBYXvI = new _$$Lambda$NotificationGutsManager$9FsF_zUJ5zlFxqpy3aSUEhBYXvI(this, sbn);
        Object o;
        if (user.equals((Object)UserHandle.ALL) && this.mLockscreenUserManager.getCurrentUserId() != 0) {
            o = null;
        }
        else {
            o = new _$$Lambda$NotificationGutsManager$FTSuXAqt9_sMxBGLPWZSSAYCzbM(this, guts, sbn, packageName, expandableNotificationRow, notificationConversationInfo);
        }
        final Context mContext = this.mContext;
        notificationConversationInfo.bindNotification(this.mShortcutManager, packageManagerForUser, this.mNotificationManager, this.mVisualStabilityManager, packageName, expandableNotificationRow.getEntry().getChannel(), expandableNotificationRow.getEntry(), (NotificationConversationInfo.OnSettingsClickListener)o, (NotificationConversationInfo.OnSnoozeClickListener)$$Lambda$NotificationGutsManager$9FsF_zUJ5zlFxqpy3aSUEhBYXvI, new ConversationIconFactory(mContext, this.mLauncherApps, packageManagerForUser, IconDrawableFactory.newInstance(mContext, false), this.mContext.getResources().getDimensionPixelSize(R$dimen.notification_guts_conversation_icon_size)), this.mDeviceProvisionedController.isDeviceProvisioned());
    }
    
    @VisibleForTesting
    void initializeNotificationInfo(final ExpandableNotificationRow expandableNotificationRow, final NotificationInfo notificationInfo) throws Exception {
        final NotificationGuts guts = expandableNotificationRow.getGuts();
        final StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        final String packageName = sbn.getPackageName();
        final UserHandle user = sbn.getUser();
        final PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(this.mContext, user.getIdentifier());
        final _$$Lambda$NotificationGutsManager$5sbilrrQIt_lf__8k9ZdwNLn_js $$Lambda$NotificationGutsManager$5sbilrrQIt_lf__8k9ZdwNLn_js = new _$$Lambda$NotificationGutsManager$5sbilrrQIt_lf__8k9ZdwNLn_js(this, guts, sbn, expandableNotificationRow);
        Object o;
        if (user.equals((Object)UserHandle.ALL) && this.mLockscreenUserManager.getCurrentUserId() != 0) {
            o = null;
        }
        else {
            o = new _$$Lambda$NotificationGutsManager$Q50_8sHdIRaYdx4NmoW9bex_4_o(this, guts, sbn, packageName, expandableNotificationRow);
        }
        notificationInfo.bindNotification(packageManagerForUser, this.mNotificationManager, this.mVisualStabilityManager, packageName, expandableNotificationRow.getEntry().getChannel(), (Set<NotificationChannel>)expandableNotificationRow.getUniqueChannels(), expandableNotificationRow.getEntry(), (NotificationInfo.OnSettingsClickListener)o, (NotificationInfo.OnAppSettingsClickListener)$$Lambda$NotificationGutsManager$5sbilrrQIt_lf__8k9ZdwNLn_js, this.mDeviceProvisionedController.isDeviceProvisioned(), expandableNotificationRow.getIsNonblockable(), this.mHighPriorityProvider.isHighPriority(expandableNotificationRow.getEntry()));
    }
    
    public void onDensityOrFontScaleChanged(final NotificationEntry notificationEntry) {
        this.setExposedGuts(notificationEntry.getGuts());
        this.bindGuts(notificationEntry.getRow());
    }
    
    public boolean openGuts(final View view, final int n, final int n2, final NotificationMenuRowPlugin.MenuItem menuItem) {
        if (menuItem.getGutsView() instanceof NotificationInfo) {
            final StatusBarStateController mStatusBarStateController = this.mStatusBarStateController;
            if (mStatusBarStateController instanceof StatusBarStateControllerImpl) {
                ((StatusBarStateControllerImpl)mStatusBarStateController).setLeaveOpenOnKeyguardHide(true);
            }
            this.mStatusBarLazy.get().executeRunnableDismissingKeyguard(new _$$Lambda$NotificationGutsManager$8gGDBkjiNygwZVxEnvaniT49x6g(this, view, n, n2, menuItem), null, false, true, true);
            return true;
        }
        return this.openGutsInternal(view, n, n2, menuItem);
    }
    
    @VisibleForTesting
    boolean openGutsInternal(final View view, final int n, final int n2, final NotificationMenuRowPlugin.MenuItem menuItem) {
        if (!(view instanceof ExpandableNotificationRow)) {
            return false;
        }
        if (view.getWindowToken() == null) {
            Log.e("NotificationGutsManager", "Trying to show notification guts, but not attached to window");
            return false;
        }
        final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
        view.performHapticFeedback(0);
        if (expandableNotificationRow.areGutsExposed()) {
            this.closeAndSaveGuts(false, false, true, -1, -1, true);
            return false;
        }
        expandableNotificationRow.ensureGutsInflated();
        final NotificationGuts guts = expandableNotificationRow.getGuts();
        this.mNotificationGutsExposed = guts;
        if (!this.bindGuts(expandableNotificationRow, menuItem)) {
            return false;
        }
        if (guts == null) {
            return false;
        }
        guts.setVisibility(4);
        guts.post(this.mOpenRunnable = new Runnable() {
            @Override
            public void run() {
                if (expandableNotificationRow.getWindowToken() == null) {
                    Log.e("NotificationGutsManager", "Trying to show notification guts in post(), but not attached to window");
                    return;
                }
                guts.setVisibility(0);
                final boolean b = NotificationGutsManager.this.mStatusBarStateController.getState() == 1 && !NotificationGutsManager.this.mAccessibilityManager.isTouchExplorationEnabled();
                final NotificationGuts val$guts = guts;
                final boolean blockingHelperShowing = expandableNotificationRow.isBlockingHelperShowing();
                final int val$x = n;
                final int val$y = n2;
                final ExpandableNotificationRow val$row = expandableNotificationRow;
                Objects.requireNonNull(val$row);
                val$guts.openControls(blockingHelperShowing ^ true, val$x, val$y, b, new _$$Lambda$IONSGD9gxXDD_zwBcDGw5yfu2Rc(val$row));
                expandableNotificationRow.closeRemoteInput();
                ((ExpandableView.OnHeightChangedListener)NotificationGutsManager.this.mListContainer).onHeightChanged(expandableNotificationRow, true);
                NotificationGutsManager.this.mGutsMenuItem = menuItem;
            }
        });
        return true;
    }
    
    @Override
    public void setCallback(final NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback) {
        this.mNotificationLifetimeFinishedCallback = mNotificationLifetimeFinishedCallback;
    }
    
    public void setExposedGuts(final NotificationGuts mNotificationGutsExposed) {
        this.mNotificationGutsExposed = mNotificationGutsExposed;
    }
    
    public void setNotificationActivityStarter(final NotificationActivityStarter mNotificationActivityStarter) {
        this.mNotificationActivityStarter = mNotificationActivityStarter;
    }
    
    @Override
    public void setShouldManageLifetime(final NotificationEntry notificationEntry, final boolean b) {
        if (b) {
            this.mKeyToRemoveOnGutsClosed = notificationEntry.getKey();
            if (Log.isLoggable("NotificationGutsManager", 3)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Keeping notification because it's showing guts. ");
                sb.append(notificationEntry.getKey());
                Log.d("NotificationGutsManager", sb.toString());
            }
        }
        else {
            final String mKeyToRemoveOnGutsClosed = this.mKeyToRemoveOnGutsClosed;
            if (mKeyToRemoveOnGutsClosed != null && mKeyToRemoveOnGutsClosed.equals(notificationEntry.getKey())) {
                this.mKeyToRemoveOnGutsClosed = null;
                if (Log.isLoggable("NotificationGutsManager", 3)) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Notification that was kept for guts was updated. ");
                    sb2.append(notificationEntry.getKey());
                    Log.d("NotificationGutsManager", sb2.toString());
                }
            }
        }
    }
    
    public void setUpWithPresenter(final NotificationPresenter mPresenter, final NotificationListContainer mListContainer, final NotificationInfo.CheckSaveListener checkSaveListener, final OnSettingsClickListener mOnSettingsClickListener) {
        this.mPresenter = mPresenter;
        this.mListContainer = mListContainer;
        this.mOnSettingsClickListener = mOnSettingsClickListener;
    }
    
    @Override
    public boolean shouldExtendLifetime(final NotificationEntry notificationEntry) {
        return notificationEntry != null && this.mNotificationGutsExposed != null && notificationEntry.getGuts() != null && this.mNotificationGutsExposed == notificationEntry.getGuts() && !this.mNotificationGutsExposed.isLeavebehind();
    }
    
    protected void startAppOpsSettingsActivity(final String s, final int n, final ArraySet<Integer> set, final ExpandableNotificationRow expandableNotificationRow) {
        final boolean contains = set.contains((Object)24);
        final Integer value = 27;
        final Integer value2 = 26;
        if (contains) {
            if (!set.contains((Object)value2) && !set.contains((Object)value)) {
                final Intent intent = new Intent("android.settings.MANAGE_APP_OVERLAY_PERMISSION");
                intent.setData(Uri.fromParts("package", s, (String)null));
                this.mNotificationActivityStarter.startNotificationGutsIntent(intent, n, expandableNotificationRow);
            }
            else {
                this.startAppDetailsSettingsActivity(s, n, null, expandableNotificationRow);
            }
        }
        else if (set.contains((Object)value2) || set.contains((Object)value)) {
            final Intent intent2 = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
            intent2.putExtra("android.intent.extra.PACKAGE_NAME", s);
            this.mNotificationActivityStarter.startNotificationGutsIntent(intent2, n, expandableNotificationRow);
        }
    }
    
    public interface OnSettingsClickListener
    {
        void onSettingsClick(final String p0);
    }
}
