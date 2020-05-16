// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.View;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.internal.widget.MessagingGroup;
import com.android.internal.widget.MessagingMessage;
import java.util.function.BooleanSupplier;
import java.util.Objects;
import com.android.systemui.statusbar.notification.AboveShelfChangedListener;
import android.content.pm.PackageManager;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.R$string;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.ForegroundServiceNotificationListener;
import java.util.List;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import android.service.vr.IVrManager;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import android.util.Slog;
import android.service.vr.IVrManager$Stub;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import com.android.systemui.R$integer;
import android.app.KeyguardManager;
import com.android.systemui.R$id;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.os.RemoteException;
import android.service.vr.IVrStateCallbacks$Stub;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.InitController;
import android.view.ViewGroup;
import android.content.Context;
import android.service.vr.IVrStateCallbacks;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptSuppressor;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.row.NotificationInfo;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.statusbar.notification.AboveShelfObserver;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.NotificationPresenter;

public class StatusBarNotificationPresenter implements NotificationPresenter, ConfigurationListener, BindRowCallback, Callbacks
{
    private final AboveShelfObserver mAboveShelfObserver;
    private final AccessibilityManager mAccessibilityManager;
    private final ActivityLaunchAnimator mActivityLaunchAnimator;
    private final ActivityStarter mActivityStarter;
    private final IStatusBarService mBarService;
    private final NotificationInfo.CheckSaveListener mCheckSaveListener;
    private final CommandQueue mCommandQueue;
    private boolean mDispatchUiModeChangeOnUserSwitched;
    private final DozeScrimController mDozeScrimController;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private final NotificationEntryManager mEntryManager;
    private final NotificationGutsManager mGutsManager;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private final NotificationInterruptSuppressor mInterruptSuppressor;
    private final KeyguardIndicationController mKeyguardIndicationController;
    private final KeyguardStateController mKeyguardStateController;
    private final LockscreenGestureLogger mLockscreenGestureLogger;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final int mMaxAllowedKeyguardNotifications;
    private int mMaxKeyguardNotifications;
    private final NotificationMediaManager mMediaManager;
    private final NotificationPanelViewController mNotificationPanel;
    private final NotificationGutsManager.OnSettingsClickListener mOnSettingsClickListener;
    private boolean mReinflateNotificationsOnUserSwitched;
    private final ScrimController mScrimController;
    private final ShadeController mShadeController;
    private final StatusBar mStatusBar;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private final NotificationViewHierarchyManager mViewHierarchyManager;
    private final VisualStabilityManager mVisualStabilityManager;
    protected boolean mVrMode;
    private final IVrStateCallbacks mVrStateCallbacks;
    
    public StatusBarNotificationPresenter(final Context context, final NotificationPanelViewController mNotificationPanel, final HeadsUpManagerPhone mHeadsUpManager, final NotificationShadeWindowView notificationShadeWindowView, final ViewGroup viewGroup, final DozeScrimController mDozeScrimController, final ScrimController mScrimController, final ActivityLaunchAnimator mActivityLaunchAnimator, final DynamicPrivacyController mDynamicPrivacyController, final KeyguardStateController mKeyguardStateController, final KeyguardIndicationController mKeyguardIndicationController, final StatusBar mStatusBar, final ShadeController mShadeController, final CommandQueue mCommandQueue, final InitController initController, final NotificationInterruptStateProvider notificationInterruptStateProvider) {
        this.mLockscreenGestureLogger = Dependency.get(LockscreenGestureLogger.class);
        this.mActivityStarter = Dependency.get(ActivityStarter.class);
        this.mViewHierarchyManager = Dependency.get(NotificationViewHierarchyManager.class);
        this.mLockscreenUserManager = Dependency.get(NotificationLockscreenUserManager.class);
        this.mStatusBarStateController = Dependency.get((Class<SysuiStatusBarStateController>)StatusBarStateController.class);
        this.mEntryManager = Dependency.get(NotificationEntryManager.class);
        this.mMediaManager = Dependency.get(NotificationMediaManager.class);
        this.mVisualStabilityManager = Dependency.get(VisualStabilityManager.class);
        this.mGutsManager = Dependency.get(NotificationGutsManager.class);
        this.mVrStateCallbacks = (IVrStateCallbacks)new IVrStateCallbacks$Stub() {
            public void onVrStateChanged(final boolean mVrMode) {
                StatusBarNotificationPresenter.this.mVrMode = mVrMode;
            }
        };
        this.mCheckSaveListener = new NotificationInfo.CheckSaveListener() {};
        this.mOnSettingsClickListener = new NotificationGutsManager.OnSettingsClickListener() {
            @Override
            public void onSettingsClick(final String s) {
                try {
                    StatusBarNotificationPresenter.this.mBarService.onNotificationSettingsViewed(s);
                }
                catch (RemoteException ex) {}
            }
        };
        this.mInterruptSuppressor = new NotificationInterruptSuppressor() {
            @Override
            public String getName() {
                return "StatusBarNotificationPresenter";
            }
            
            @Override
            public boolean suppressAwakeHeadsUp(final NotificationEntry notificationEntry) {
                final StatusBarNotification sbn = notificationEntry.getSbn();
                if (StatusBarNotificationPresenter.this.mStatusBar.isOccluded()) {
                    final boolean b = StatusBarNotificationPresenter.this.mLockscreenUserManager.isLockscreenPublicMode(StatusBarNotificationPresenter.this.mLockscreenUserManager.getCurrentUserId()) || StatusBarNotificationPresenter.this.mLockscreenUserManager.isLockscreenPublicMode(sbn.getUserId());
                    final boolean needsRedaction = StatusBarNotificationPresenter.this.mLockscreenUserManager.needsRedaction(notificationEntry);
                    if (b && needsRedaction) {
                        return true;
                    }
                }
                if (!StatusBarNotificationPresenter.this.mCommandQueue.panelsEnabled()) {
                    return true;
                }
                if (sbn.getNotification().fullScreenIntent != null) {
                    if (StatusBarNotificationPresenter.this.mKeyguardStateController.isShowing() && !StatusBarNotificationPresenter.this.mStatusBar.isOccluded()) {
                        return true;
                    }
                    if (StatusBarNotificationPresenter.this.mAccessibilityManager.isTouchExplorationEnabled()) {
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public boolean suppressAwakeInterruptions(final NotificationEntry notificationEntry) {
                return StatusBarNotificationPresenter.this.isDeviceInVrMode();
            }
            
            @Override
            public boolean suppressInterruptions(final NotificationEntry notificationEntry) {
                return StatusBarNotificationPresenter.this.mStatusBar.areNotificationAlertsDisabled();
            }
        };
        this.mKeyguardStateController = mKeyguardStateController;
        this.mNotificationPanel = mNotificationPanel;
        this.mHeadsUpManager = mHeadsUpManager;
        this.mDynamicPrivacyController = mDynamicPrivacyController;
        this.mKeyguardIndicationController = mKeyguardIndicationController;
        this.mStatusBar = mStatusBar;
        this.mShadeController = mShadeController;
        this.mCommandQueue = mCommandQueue;
        final AboveShelfObserver mAboveShelfObserver = new AboveShelfObserver(viewGroup);
        this.mAboveShelfObserver = mAboveShelfObserver;
        this.mActivityLaunchAnimator = mActivityLaunchAnimator;
        mAboveShelfObserver.setListener((AboveShelfObserver.HasViewAboveShelfChangedListener)notificationShadeWindowView.findViewById(R$id.notification_container_parent));
        this.mAccessibilityManager = (AccessibilityManager)context.getSystemService((Class)AccessibilityManager.class);
        this.mDozeScrimController = mDozeScrimController;
        this.mScrimController = mScrimController;
        final KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService((Class)KeyguardManager.class);
        this.mMaxAllowedKeyguardNotifications = context.getResources().getInteger(R$integer.keyguard_max_notification_count);
        this.mBarService = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        final IVrManager interface1 = IVrManager$Stub.asInterface(ServiceManager.getService("vrmanager"));
        if (interface1 != null) {
            try {
                interface1.registerListener(this.mVrStateCallbacks);
            }
            catch (RemoteException obj) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Failed to register VR mode state listener: ");
                sb.append(obj);
                Slog.e("StatusBarNotificationPresenter", sb.toString());
            }
        }
        final NotificationRemoteInputManager notificationRemoteInputManager = Dependency.get(NotificationRemoteInputManager.class);
        notificationRemoteInputManager.setUpWithCallback((NotificationRemoteInputManager.Callback)Dependency.get(NotificationRemoteInputManager.Callback.class), this.mNotificationPanel.createRemoteInputDelegate());
        notificationRemoteInputManager.getController().addCallback((RemoteInputController.Callback)Dependency.get(NotificationShadeWindowController.class));
        initController.addPostInitTask(new _$$Lambda$StatusBarNotificationPresenter$RZolY06L4AtX2ZrvmGw_j2EoYcA(this, (NotificationListContainer)viewGroup, notificationRemoteInputManager, notificationInterruptStateProvider));
        Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    private void maybeEndAmbientPulse() {
        if (this.mNotificationPanel.hasPulsingNotifications() && !this.mHeadsUpManager.hasNotifications()) {
            this.mDozeScrimController.pulseOutNow();
        }
    }
    
    private void updateNotificationOnUiModeChanged() {
        final List<NotificationEntry> activeNotificationsForCurrentUser = this.mEntryManager.getActiveNotificationsForCurrentUser();
        for (int i = 0; i < activeNotificationsForCurrentUser.size(); ++i) {
            final ExpandableNotificationRow row = activeNotificationsForCurrentUser.get(i).getRow();
            if (row != null) {
                row.onUiModeChanged();
            }
        }
    }
    
    private void updateNotificationsOnDensityOrFontScaleChanged() {
        final List<NotificationEntry> activeNotificationsForCurrentUser = this.mEntryManager.getActiveNotificationsForCurrentUser();
        for (int i = 0; i < activeNotificationsForCurrentUser.size(); ++i) {
            final NotificationEntry notificationEntry = activeNotificationsForCurrentUser.get(i);
            notificationEntry.onDensityOrFontScaleChanged();
            if (notificationEntry.areGutsExposed()) {
                this.mGutsManager.onDensityOrFontScaleChanged(notificationEntry);
            }
        }
    }
    
    @Override
    public int getMaxNotificationsWhileLocked(final boolean b) {
        if (b) {
            return this.mMaxKeyguardNotifications = Math.max(1, this.mNotificationPanel.computeMaxKeyguardNotifications(this.mMaxAllowedKeyguardNotifications));
        }
        return this.mMaxKeyguardNotifications;
    }
    
    public boolean hasActiveNotifications() {
        return this.mEntryManager.hasActiveNotifications();
    }
    
    @Override
    public boolean isCollapsing() {
        return this.mNotificationPanel.isCollapsing() || this.mActivityLaunchAnimator.isAnimationPending() || this.mActivityLaunchAnimator.isAnimationRunning();
    }
    
    @Override
    public boolean isDeviceInVrMode() {
        return this.mVrMode;
    }
    
    @Override
    public boolean isPresenterFullyCollapsed() {
        return this.mNotificationPanel.isFullyCollapsed();
    }
    
    public void onActivated() {
        this.mLockscreenGestureLogger.write(192, 0, 0);
        this.mNotificationPanel.showTransientIndication(R$string.notification_tap_again);
        final ActivatableNotificationView activatedChild = this.mNotificationPanel.getActivatedChild();
        if (activatedChild != null) {
            activatedChild.makeInactive(true);
        }
    }
    
    @Override
    public void onActivated(final ActivatableNotificationView activatedChild) {
        this.onActivated();
        if (activatedChild != null) {
            this.mNotificationPanel.setActivatedChild(activatedChild);
        }
    }
    
    @Override
    public void onActivationReset(final ActivatableNotificationView activatableNotificationView) {
        if (activatableNotificationView == this.mNotificationPanel.getActivatedChild()) {
            this.mNotificationPanel.setActivatedChild(null);
            this.mKeyguardIndicationController.hideTransientIndication();
        }
    }
    
    @Override
    public void onBindRow(final NotificationEntry notificationEntry, final PackageManager packageManager, final StatusBarNotification statusBarNotification, final ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setAboveShelfChangedListener(this.mAboveShelfObserver);
        final KeyguardStateController mKeyguardStateController = this.mKeyguardStateController;
        Objects.requireNonNull(mKeyguardStateController);
        expandableNotificationRow.setSecureStateProvider(new _$$Lambda$1SZvPIdbqVv78ZfFe1GTnc0G8WM(mKeyguardStateController));
    }
    
    @Override
    public void onDensityOrFontScaleChanged() {
        MessagingMessage.dropCache();
        MessagingGroup.dropCache();
        if (!Dependency.get(KeyguardUpdateMonitor.class).isSwitchingUser()) {
            this.updateNotificationsOnDensityOrFontScaleChanged();
        }
        else {
            this.mReinflateNotificationsOnUserSwitched = true;
        }
    }
    
    @Override
    public void onExpandClicked(final NotificationEntry notificationEntry, final boolean b) {
        this.mHeadsUpManager.setExpanded(notificationEntry, b);
        if (b) {
            if (this.mStatusBarStateController.getState() == 1) {
                this.mShadeController.goToLockedShade((View)notificationEntry.getRow());
            }
            else if (notificationEntry.isSensitive() && this.mDynamicPrivacyController.isInLockedDownShade()) {
                this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
                this.mActivityStarter.dismissKeyguardThenExecute((ActivityStarter.OnDismissAction)_$$Lambda$StatusBarNotificationPresenter$kVrBvDo577RHxcwdetzp8ypANEY.INSTANCE, null, false);
            }
        }
    }
    
    public void onNotificationRemoved(final String s, final StatusBarNotification statusBarNotification) {
        if (statusBarNotification != null && !this.hasActiveNotifications() && !this.mNotificationPanel.isTracking() && !this.mNotificationPanel.isQsExpanded()) {
            if (this.mStatusBarStateController.getState() == 0) {
                this.mCommandQueue.animateCollapsePanels();
            }
            else if (this.mStatusBarStateController.getState() == 2 && !this.isCollapsing()) {
                this.mStatusBarStateController.setState(1);
            }
        }
    }
    
    @Override
    public void onOverlayChanged() {
        this.onDensityOrFontScaleChanged();
    }
    
    @Override
    public void onUiModeChanged() {
        if (!Dependency.get(KeyguardUpdateMonitor.class).isSwitchingUser()) {
            this.updateNotificationOnUiModeChanged();
        }
        else {
            this.mDispatchUiModeChangeOnUserSwitched = true;
        }
    }
    
    @Override
    public void onUpdateRowStates() {
        this.mNotificationPanel.onUpdateRowStates();
    }
    
    @Override
    public void onUserSwitched(final int n) {
        this.mHeadsUpManager.setUser(n);
        this.mCommandQueue.animateCollapsePanels();
        if (this.mReinflateNotificationsOnUserSwitched) {
            this.updateNotificationsOnDensityOrFontScaleChanged();
            this.mReinflateNotificationsOnUserSwitched = false;
        }
        if (this.mDispatchUiModeChangeOnUserSwitched) {
            this.updateNotificationOnUiModeChanged();
            this.mDispatchUiModeChangeOnUserSwitched = false;
        }
        this.updateNotificationViews();
        this.mMediaManager.clearCurrentMediaNotification();
        this.mStatusBar.setLockscreenUser(n);
        this.updateMediaMetaData(true, false);
    }
    
    @Override
    public void updateMediaMetaData(final boolean b, final boolean b2) {
        this.mMediaManager.updateMediaMetaData(b, b2);
    }
    
    @Override
    public void updateNotificationViews() {
        if (this.mScrimController == null) {
            return;
        }
        if (this.isCollapsing()) {
            this.mShadeController.addPostCollapseAction(new _$$Lambda$gi2ID0w_NOAgPDK3Aiflyq0eAFU(this));
            return;
        }
        this.mViewHierarchyManager.updateNotificationViews();
        this.mNotificationPanel.updateNotificationViews();
    }
}
