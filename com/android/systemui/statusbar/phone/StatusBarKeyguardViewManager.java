// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.res.ColorStateList;
import android.os.Bundle;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import android.view.WindowManagerGlobal;
import android.view.KeyEvent;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.notification.ViewGroupFadeHelper;
import java.io.PrintWriter;
import com.android.systemui.DejankUtils;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.internal.util.LatencyTracker;
import android.view.WindowInsets$Type;
import android.view.ViewRootImpl;
import android.os.SystemClock;
import com.android.keyguard.ViewMediatorCallback;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import android.view.View;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.dock.DockManager;
import android.content.Context;
import android.view.ViewGroup;
import java.util.ArrayList;
import com.android.systemui.plugins.ActivityStarter;
import com.android.keyguard.KeyguardViewController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.RemoteInputController;

public class StatusBarKeyguardViewManager implements Callback, StateListener, ConfigurationListener, PanelExpansionListener, ModeChangedListener, KeyguardViewController
{
    private ActivityStarter.OnDismissAction mAfterKeyguardGoneAction;
    private final ArrayList<Runnable> mAfterKeyguardGoneRunnables;
    private BiometricUnlockController mBiometricUnlockController;
    protected KeyguardBouncer mBouncer;
    private KeyguardBypassController mBypassController;
    private final ConfigurationController mConfigurationController;
    private ViewGroup mContainer;
    protected final Context mContext;
    private final DockManager.DockEventListener mDockEventListener;
    private final DockManager mDockManager;
    private boolean mDozing;
    private final KeyguardBouncer.BouncerExpansionCallback mExpansionCallback;
    protected boolean mFirstUpdate;
    private boolean mGesturalNav;
    private boolean mIsDocked;
    private Runnable mKeyguardGoneCancelAction;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateManager;
    private int mLastBiometricMode;
    private boolean mLastBouncerDismissible;
    private boolean mLastBouncerShowing;
    private boolean mLastDozing;
    private boolean mLastGesturalNav;
    private boolean mLastIsDocked;
    private boolean mLastLockVisible;
    protected boolean mLastOccluded;
    private boolean mLastPulsing;
    protected boolean mLastRemoteInputActive;
    protected boolean mLastShowing;
    private ViewGroup mLockIconContainer;
    protected LockPatternUtils mLockPatternUtils;
    private Runnable mMakeNavigationBarVisibleRunnable;
    private final NotificationMediaManager mMediaManager;
    private final NavigationModeController mNavigationModeController;
    private View mNotificationContainer;
    private NotificationPanelViewController mNotificationPanelViewController;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    protected boolean mOccluded;
    private DismissWithActionRequest mPendingWakeupAction;
    private boolean mPulsing;
    protected boolean mRemoteInputActive;
    protected boolean mShowing;
    protected StatusBar mStatusBar;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    protected ViewMediatorCallback mViewMediatorCallback;
    
    public StatusBarKeyguardViewManager(final Context mContext, final ViewMediatorCallback mViewMediatorCallback, final LockPatternUtils mLockPatternUtils, final SysuiStatusBarStateController mStatusBarStateController, final ConfigurationController mConfigurationController, final KeyguardUpdateMonitor mKeyguardUpdateManager, final NavigationModeController mNavigationModeController, final DockManager mDockManager, final NotificationShadeWindowController mNotificationShadeWindowController, final KeyguardStateController mKeyguardStateController, final NotificationMediaManager mMediaManager) {
        this.mExpansionCallback = new KeyguardBouncer.BouncerExpansionCallback() {
            @Override
            public void onFullyHidden() {
                StatusBarKeyguardViewManager.this.updateStates();
                StatusBarKeyguardViewManager.this.updateLockIcon();
            }
            
            @Override
            public void onFullyShown() {
                StatusBarKeyguardViewManager.this.updateStates();
                StatusBarKeyguardViewManager.this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), (View)StatusBarKeyguardViewManager.this.mContainer, "BOUNCER_VISIBLE");
                StatusBarKeyguardViewManager.this.updateLockIcon();
            }
            
            @Override
            public void onStartingToHide() {
                StatusBarKeyguardViewManager.this.updateStates();
            }
            
            @Override
            public void onStartingToShow() {
                StatusBarKeyguardViewManager.this.updateLockIcon();
            }
        };
        this.mDockEventListener = new DockManager.DockEventListener() {
            @Override
            public void onEvent(final int n) {
                final boolean docked = StatusBarKeyguardViewManager.this.mDockManager.isDocked();
                if (docked == StatusBarKeyguardViewManager.this.mIsDocked) {
                    return;
                }
                StatusBarKeyguardViewManager.this.mIsDocked = docked;
                StatusBarKeyguardViewManager.this.updateStates();
            }
        };
        this.mFirstUpdate = true;
        this.mAfterKeyguardGoneRunnables = new ArrayList<Runnable>();
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onEmergencyCallAction() {
                final StatusBarKeyguardViewManager this$0 = StatusBarKeyguardViewManager.this;
                if (this$0.mOccluded) {
                    this$0.reset(true);
                }
            }
        };
        this.mMakeNavigationBarVisibleRunnable = new Runnable() {
            @Override
            public void run() {
                if (ViewRootImpl.sNewInsetsMode == 2) {
                    StatusBarKeyguardViewManager.this.mStatusBar.getNotificationShadeWindowView().getWindowInsetsController().show(WindowInsets$Type.navigationBars());
                }
                else {
                    StatusBarKeyguardViewManager.this.mStatusBar.getNavigationBarView().getRootView().setVisibility(0);
                }
            }
        };
        this.mContext = mContext;
        this.mViewMediatorCallback = mViewMediatorCallback;
        this.mLockPatternUtils = mLockPatternUtils;
        this.mConfigurationController = mConfigurationController;
        this.mNavigationModeController = mNavigationModeController;
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mMediaManager = mMediaManager;
        this.mKeyguardUpdateManager = mKeyguardUpdateManager;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mDockManager = mDockManager;
    }
    
    private void executeAfterKeyguardGoneAction() {
        final ActivityStarter.OnDismissAction mAfterKeyguardGoneAction = this.mAfterKeyguardGoneAction;
        if (mAfterKeyguardGoneAction != null) {
            mAfterKeyguardGoneAction.onDismiss();
            this.mAfterKeyguardGoneAction = null;
        }
        this.mKeyguardGoneCancelAction = null;
        for (int i = 0; i < this.mAfterKeyguardGoneRunnables.size(); ++i) {
            this.mAfterKeyguardGoneRunnables.get(i).run();
        }
        this.mAfterKeyguardGoneRunnables.clear();
    }
    
    private long getNavBarShowDelay() {
        if (this.mKeyguardStateController.isKeyguardFadingAway()) {
            return this.mKeyguardStateController.getKeyguardFadingAwayDelay();
        }
        if (this.mBouncer.isShowing()) {
            return 320L;
        }
        return 0L;
    }
    
    private boolean isWakeAndUnlocking() {
        final int mode = this.mBiometricUnlockController.getMode();
        boolean b = true;
        if (mode != 1) {
            b = (mode == 2 && b);
        }
        return b;
    }
    
    private boolean needsBypassFading() {
        final int mode = this.mBiometricUnlockController.getMode();
        boolean b = true;
        if ((mode != 7 && this.mBiometricUnlockController.getMode() != 2 && this.mBiometricUnlockController.getMode() != 1) || !this.mBypassController.getBypassEnabled()) {
            b = false;
        }
        return b;
    }
    
    private void registerListeners() {
        this.mKeyguardUpdateManager.registerCallback(this.mUpdateMonitorCallback);
        this.mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.mConfigurationController.addCallback((ConfigurationController.ConfigurationListener)this);
        this.mGesturalNav = QuickStepContract.isGesturalMode(this.mNavigationModeController.addListener((NavigationModeController.ModeChangedListener)this));
        final DockManager mDockManager = this.mDockManager;
        if (mDockManager != null) {
            mDockManager.addListener(this.mDockEventListener);
            this.mIsDocked = this.mDockManager.isDocked();
        }
    }
    
    private void setDozing(final boolean mDozing) {
        if (this.mDozing != mDozing) {
            this.mDozing = mDozing;
            if (mDozing || this.mBouncer.needsFullscreenBouncer() || this.mOccluded) {
                this.reset(mDozing);
            }
            this.updateStates();
            if (!mDozing) {
                this.launchPendingWakeupAction();
            }
        }
    }
    
    private void updateLockIcon() {
        if (this.mLockIconContainer == null) {
            return;
        }
        final int state = this.mStatusBarStateController.getState();
        boolean mLastLockVisible = true;
        final int n = 0;
        final boolean b = state == 1 && !this.mNotificationPanelViewController.isQsExpanded();
        if ((!this.mBouncer.isShowing() && !b) || this.mBouncer.isAnimatingAway() || this.mKeyguardStateController.isKeyguardFadingAway()) {
            mLastLockVisible = false;
        }
        if (this.mLastLockVisible != mLastLockVisible) {
            this.mLastLockVisible = mLastLockVisible;
            if (mLastLockVisible) {
                CrossFadeHelper.fadeIn((View)this.mLockIconContainer, 220L, 0);
            }
            else {
                long n2;
                int n3;
                if (this.needsBypassFading()) {
                    n2 = 67L;
                    n3 = n;
                }
                else {
                    n2 = 110L;
                    n3 = 120;
                }
                CrossFadeHelper.fadeOut((View)this.mLockIconContainer, n2, n3, null);
            }
        }
    }
    
    private void wakeAndUnlockDejank() {
        if (this.mBiometricUnlockController.getMode() == 1 && LatencyTracker.isEnabled(this.mContext)) {
            DejankUtils.postAfterTraversal(new _$$Lambda$StatusBarKeyguardViewManager$WtAkg4w14mbTRLi3kx_TWboxp_s(this));
        }
    }
    
    public void addAfterKeyguardGoneRunnable(final Runnable e) {
        this.mAfterKeyguardGoneRunnables.add(e);
    }
    
    public boolean bouncerIsOrWillBeShowing() {
        return this.mBouncer.isShowing() || this.mBouncer.inTransit();
    }
    
    public boolean bouncerNeedsScrimming() {
        return this.mOccluded || this.mBouncer.willDismissWithAction() || this.mStatusBar.isFullScreenUserSwitcherState() || (this.mBouncer.isShowing() && this.mBouncer.isScrimmed()) || this.mBouncer.isFullscreenBouncer();
    }
    
    public void cancelPendingWakeupAction() {
        final DismissWithActionRequest mPendingWakeupAction = this.mPendingWakeupAction;
        this.mPendingWakeupAction = null;
        if (mPendingWakeupAction != null) {
            final Runnable cancelAction = mPendingWakeupAction.cancelAction;
            if (cancelAction != null) {
                cancelAction.run();
            }
        }
    }
    
    @Override
    public void dismissAndCollapse() {
        this.mStatusBar.executeRunnableDismissingKeyguard(null, null, true, false, true);
    }
    
    public void dismissWithAction(final ActivityStarter.OnDismissAction onDismissAction, final Runnable runnable, final boolean b) {
        this.dismissWithAction(onDismissAction, runnable, b, null);
    }
    
    public void dismissWithAction(final ActivityStarter.OnDismissAction mAfterKeyguardGoneAction, final Runnable mKeyguardGoneCancelAction, final boolean b, final String s) {
        if (this.mShowing) {
            this.cancelPendingWakeupAction();
            if (this.mDozing && !this.isWakeAndUnlocking()) {
                this.mPendingWakeupAction = new DismissWithActionRequest(mAfterKeyguardGoneAction, mKeyguardGoneCancelAction, b, s);
                return;
            }
            if (!b) {
                this.mBouncer.showWithDismissAction(mAfterKeyguardGoneAction, mKeyguardGoneCancelAction);
            }
            else {
                this.mAfterKeyguardGoneAction = mAfterKeyguardGoneAction;
                this.mKeyguardGoneCancelAction = mKeyguardGoneCancelAction;
                this.mBouncer.show(false);
            }
        }
        this.updateStates();
    }
    
    public void dump(final PrintWriter printWriter) {
        printWriter.println("StatusBarKeyguardViewManager:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mShowing: ");
        sb.append(this.mShowing);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mOccluded: ");
        sb2.append(this.mOccluded);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mRemoteInputActive: ");
        sb3.append(this.mRemoteInputActive);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mDozing: ");
        sb4.append(this.mDozing);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mAfterKeyguardGoneAction: ");
        sb5.append(this.mAfterKeyguardGoneAction);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mAfterKeyguardGoneRunnables: ");
        sb6.append(this.mAfterKeyguardGoneRunnables);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  mPendingWakeupAction: ");
        sb7.append(this.mPendingWakeupAction);
        printWriter.println(sb7.toString());
        final KeyguardBouncer mBouncer = this.mBouncer;
        if (mBouncer != null) {
            mBouncer.dump(printWriter);
        }
    }
    
    protected boolean getLastNavBarVisible() {
        final boolean mLastShowing = this.mLastShowing;
        final boolean b = true;
        final boolean b2 = mLastShowing && !this.mLastOccluded;
        final boolean b3 = this.mLastDozing && this.mLastBiometricMode != 2;
        final boolean b4 = ((b2 && !this.mLastDozing) || (this.mLastPulsing && !this.mLastIsDocked)) && this.mLastGesturalNav;
        if (!b2) {
            final boolean b5 = b;
            if (!b3) {
                return b5;
            }
        }
        boolean b5 = b;
        if (!this.mLastBouncerShowing) {
            b5 = b;
            if (!this.mLastRemoteInputActive) {
                b5 = (b4 && b);
            }
        }
        return b5;
    }
    
    @Override
    public ViewRootImpl getViewRootImpl() {
        return this.mStatusBar.getStatusBarView().getViewRootImpl();
    }
    
    @Override
    public void hide(final long n, long n2) {
        this.mShowing = false;
        final KeyguardStateController mKeyguardStateController = this.mKeyguardStateController;
        mKeyguardStateController.notifyKeyguardState(false, mKeyguardStateController.isOccluded());
        this.launchPendingWakeupAction();
        if (this.mKeyguardUpdateManager.needsSlowUnlockTransition()) {
            n2 = 2000L;
        }
        long max = Math.max(0L, n - 48L - SystemClock.uptimeMillis());
        if (this.mStatusBar.isInLaunchTransition()) {
            this.mStatusBar.fadeKeyguardAfterLaunchTransition(new Runnable() {
                @Override
                public void run() {
                    StatusBarKeyguardViewManager.this.mNotificationShadeWindowController.setKeyguardShowing(false);
                    StatusBarKeyguardViewManager.this.mNotificationShadeWindowController.setKeyguardFadingAway(true);
                    StatusBarKeyguardViewManager.this.hideBouncer(true);
                    StatusBarKeyguardViewManager.this.updateStates();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    StatusBarKeyguardViewManager.this.mStatusBar.hideKeyguard();
                    StatusBarKeyguardViewManager.this.mNotificationShadeWindowController.setKeyguardFadingAway(false);
                    StatusBarKeyguardViewManager.this.mViewMediatorCallback.keyguardGone();
                    StatusBarKeyguardViewManager.this.executeAfterKeyguardGoneAction();
                }
            });
        }
        else {
            this.executeAfterKeyguardGoneAction();
            final boolean b = this.mBiometricUnlockController.getMode() == 2;
            final boolean needsBypassFading = this.needsBypassFading();
            Label_0155: {
                if (needsBypassFading) {
                    n2 = 67L;
                }
                else {
                    if (!b) {
                        break Label_0155;
                    }
                    n2 = 240L;
                }
                max = 0L;
            }
            final StatusBar mStatusBar = this.mStatusBar;
            final boolean keyguardFadingAway = true;
            mStatusBar.setKeyguardFadingAway(n, max, n2, needsBypassFading);
            this.mBiometricUnlockController.startKeyguardFadingAway();
            this.hideBouncer(keyguardFadingAway);
            if (b) {
                if (needsBypassFading) {
                    ViewGroupFadeHelper.fadeOutAllChildrenExcept(this.mNotificationPanelViewController.getView(), this.mNotificationContainer, n2, new _$$Lambda$StatusBarKeyguardViewManager$aIusP5sgaSr59XXK3nFh48FBNI4(this));
                }
                else {
                    this.mStatusBar.fadeKeyguardWhilePulsing();
                }
                this.wakeAndUnlockDejank();
            }
            else if (!this.mStatusBarStateController.leaveOpenOnKeyguardHide()) {
                this.mNotificationShadeWindowController.setKeyguardFadingAway(keyguardFadingAway);
                if (needsBypassFading) {
                    ViewGroupFadeHelper.fadeOutAllChildrenExcept(this.mNotificationPanelViewController.getView(), this.mNotificationContainer, n2, new _$$Lambda$StatusBarKeyguardViewManager$EJI38cHcIk60L5eHmdpMvFRistw(this));
                }
                else {
                    this.mStatusBar.hideKeyguard();
                }
                this.mStatusBar.updateScrimController();
                this.wakeAndUnlockDejank();
            }
            else {
                this.mStatusBar.hideKeyguard();
                this.mStatusBar.finishKeyguardFadingAway();
                this.mBiometricUnlockController.finishKeyguardFadingAway();
            }
            this.updateLockIcon();
            this.updateStates();
            this.mNotificationShadeWindowController.setKeyguardShowing(false);
            this.mViewMediatorCallback.keyguardGone();
        }
        SysUiStatsLog.write(62, 1);
    }
    
    void hideBouncer(final boolean b) {
        if (this.mBouncer == null) {
            return;
        }
        if (this.mShowing) {
            this.mAfterKeyguardGoneAction = null;
            final Runnable mKeyguardGoneCancelAction = this.mKeyguardGoneCancelAction;
            if (mKeyguardGoneCancelAction != null) {
                mKeyguardGoneCancelAction.run();
                this.mKeyguardGoneCancelAction = null;
            }
        }
        this.mBouncer.hide(b);
        this.cancelPendingWakeupAction();
    }
    
    public boolean interceptMediaKey(final KeyEvent keyEvent) {
        return this.mBouncer.interceptMediaKey(keyEvent);
    }
    
    public boolean isBouncerShowing() {
        return this.mBouncer.isShowing();
    }
    
    @Override
    public boolean isGoingToNotificationShade() {
        return this.mStatusBarStateController.leaveOpenOnKeyguardHide();
    }
    
    protected boolean isNavBarVisible() {
        final int mode = this.mBiometricUnlockController.getMode();
        final boolean mShowing = this.mShowing;
        final boolean b = true;
        final boolean b2 = mShowing && !this.mOccluded;
        final boolean b3 = this.mDozing && mode != 2;
        final boolean b4 = ((b2 && !this.mDozing) || (this.mPulsing && !this.mIsDocked)) && this.mGesturalNav;
        if (!b2) {
            final boolean b5 = b;
            if (!b3) {
                return b5;
            }
        }
        boolean b5 = b;
        if (!this.mBouncer.isShowing()) {
            b5 = b;
            if (!this.mRemoteInputActive) {
                b5 = (b4 && b);
            }
        }
        return b5;
    }
    
    public boolean isOccluded() {
        return this.mOccluded;
    }
    
    public boolean isSecure() {
        return this.mBouncer.isSecure();
    }
    
    @Override
    public boolean isShowing() {
        return this.mShowing;
    }
    
    @Override
    public boolean isUnlockWithWallpaper() {
        return this.mNotificationShadeWindowController.isShowingWallpaper();
    }
    
    @Override
    public void keyguardGoingAway() {
        this.mStatusBar.keyguardGoingAway();
    }
    
    public void launchPendingWakeupAction() {
        final DismissWithActionRequest mPendingWakeupAction = this.mPendingWakeupAction;
        this.mPendingWakeupAction = null;
        if (mPendingWakeupAction != null) {
            if (this.mShowing) {
                this.dismissWithAction(mPendingWakeupAction.dismissAction, mPendingWakeupAction.cancelAction, mPendingWakeupAction.afterKeyguardGone, mPendingWakeupAction.message);
            }
            else {
                final ActivityStarter.OnDismissAction dismissAction = mPendingWakeupAction.dismissAction;
                if (dismissAction != null) {
                    dismissAction.onDismiss();
                }
            }
        }
    }
    
    public void notifyKeyguardAuthenticated(final boolean b) {
        this.mBouncer.notifyKeyguardAuthenticated(b);
    }
    
    public boolean onBackPressed(final boolean b) {
        if (this.mBouncer.isShowing()) {
            this.mStatusBar.endAffordanceLaunch();
            if (this.mBouncer.isScrimmed() && !this.mBouncer.needsFullscreenBouncer()) {
                this.hideBouncer(false);
                this.updateStates();
            }
            else {
                this.reset(b);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void onCancelClicked() {
    }
    
    @Override
    public void onDensityOrFontScaleChanged() {
        this.hideBouncer(true);
    }
    
    @Override
    public void onDozingChanged(final boolean dozing) {
        this.setDozing(dozing);
    }
    
    @Override
    public void onFinishedGoingToSleep() {
        this.mBouncer.onScreenTurnedOff();
    }
    
    public void onKeyguardFadedAway() {
        this.mContainer.postDelayed((Runnable)new _$$Lambda$StatusBarKeyguardViewManager$nb9yQRGKq0kAyQz17NqvixIA7LU(this), 100L);
        ViewGroupFadeHelper.reset(this.mNotificationPanelViewController.getView());
        this.mStatusBar.finishKeyguardFadingAway();
        this.mBiometricUnlockController.finishKeyguardFadingAway();
        WindowManagerGlobal.getInstance().trimMemory(20);
    }
    
    @Override
    public void onNavigationModeChanged(final int n) {
        final boolean gesturalMode = QuickStepContract.isGesturalMode(n);
        if (gesturalMode != this.mGesturalNav) {
            this.mGesturalNav = gesturalMode;
            this.updateStates();
        }
    }
    
    @Override
    public void onPanelExpansionChanged(final float expansion, final boolean b) {
        if (this.mNotificationPanelViewController.isUnlockHintRunning()) {
            this.mBouncer.setExpansion(1.0f);
        }
        else if (this.bouncerNeedsScrimming()) {
            this.mBouncer.setExpansion(0.0f);
        }
        else if (this.mShowing) {
            if (!this.isWakeAndUnlocking() && !this.mStatusBar.isInLaunchTransition()) {
                this.mBouncer.setExpansion(expansion);
            }
            if (expansion != 1.0f && b && !this.mKeyguardStateController.canDismissLockScreen() && !this.mBouncer.isShowing() && !this.mBouncer.isAnimatingAway()) {
                this.mBouncer.show(false, false);
            }
        }
        else if (this.mPulsing && expansion == 0.0f) {
            this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), (View)this.mContainer, "BOUNCER_VISIBLE");
        }
    }
    
    @Override
    public void onQsExpansionChanged(final float n) {
        this.updateLockIcon();
    }
    
    @Override
    public void onRemoteInputActive(final boolean mRemoteInputActive) {
        this.mRemoteInputActive = mRemoteInputActive;
        this.updateStates();
    }
    
    @Override
    public void onStateChanged(final int n) {
        this.updateLockIcon();
    }
    
    @Override
    public void onThemeChanged() {
        this.hideBouncer(true);
        this.mBouncer.prepare();
    }
    
    public void readyForKeyguardDone() {
        this.mViewMediatorCallback.readyForKeyguardDone();
    }
    
    public void registerStatusBar(final StatusBar mStatusBar, final ViewGroup mContainer, final NotificationPanelViewController mNotificationPanelViewController, final BiometricUnlockController mBiometricUnlockController, final DismissCallbackRegistry dismissCallbackRegistry, final ViewGroup mLockIconContainer, final View mNotificationContainer, final KeyguardBypassController mBypassController, final FalsingManager falsingManager) {
        this.mStatusBar = mStatusBar;
        this.mContainer = mContainer;
        this.mLockIconContainer = mLockIconContainer;
        if (mLockIconContainer != null) {
            this.mLastLockVisible = (mLockIconContainer.getVisibility() == 0);
        }
        this.mBiometricUnlockController = mBiometricUnlockController;
        this.mBouncer = SystemUIFactory.getInstance().createKeyguardBouncer(this.mContext, this.mViewMediatorCallback, this.mLockPatternUtils, mContainer, dismissCallbackRegistry, this.mExpansionCallback, this.mKeyguardStateController, falsingManager, mBypassController);
        (this.mNotificationPanelViewController = mNotificationPanelViewController).addExpansionListener(this);
        this.mBypassController = mBypassController;
        this.mNotificationContainer = mNotificationContainer;
        this.registerListeners();
    }
    
    @Override
    public void reset(final boolean b) {
        if (this.mShowing) {
            if (this.mOccluded && !this.mDozing) {
                this.mStatusBar.hideKeyguard();
                if (b || this.mBouncer.needsFullscreenBouncer()) {
                    this.hideBouncer(false);
                }
            }
            else {
                this.showBouncerOrKeyguard(b);
            }
            this.mKeyguardUpdateManager.sendKeyguardReset();
            this.updateStates();
        }
    }
    
    @Override
    public void setNeedsInput(final boolean keyguardNeedsInput) {
        this.mNotificationShadeWindowController.setKeyguardNeedsInput(keyguardNeedsInput);
    }
    
    @Override
    public void setOccluded(final boolean keyguardOccluded, final boolean b) {
        this.mStatusBar.setOccluded(keyguardOccluded);
        boolean b2 = true;
        if (keyguardOccluded && !this.mOccluded && this.mShowing) {
            SysUiStatsLog.write(62, 3);
            if (this.mStatusBar.isInLaunchTransition()) {
                this.mOccluded = true;
                this.mStatusBar.fadeKeyguardAfterLaunchTransition(null, new Runnable() {
                    @Override
                    public void run() {
                        StatusBarKeyguardViewManager.this.mNotificationShadeWindowController.setKeyguardOccluded(StatusBarKeyguardViewManager.this.mOccluded);
                        StatusBarKeyguardViewManager.this.reset(true);
                    }
                });
                return;
            }
        }
        else if (!keyguardOccluded && this.mOccluded && this.mShowing) {
            SysUiStatsLog.write(62, 2);
        }
        final boolean b3 = !this.mOccluded && keyguardOccluded;
        this.mOccluded = keyguardOccluded;
        if (this.mShowing) {
            final NotificationMediaManager mMediaManager = this.mMediaManager;
            if (!b || keyguardOccluded) {
                b2 = false;
            }
            mMediaManager.updateMediaMetaData(false, b2);
        }
        this.mNotificationShadeWindowController.setKeyguardOccluded(keyguardOccluded);
        if (!this.mDozing) {
            this.reset(b3);
        }
        if (b && !keyguardOccluded && this.mShowing && !this.mBouncer.isShowing()) {
            this.mStatusBar.animateKeyguardUnoccluding();
        }
    }
    
    public void setPulsing(final boolean mPulsing) {
        if (this.mPulsing != mPulsing) {
            this.mPulsing = mPulsing;
            this.updateStates();
        }
    }
    
    protected boolean shouldDestroyViewOnReset() {
        return false;
    }
    
    @Override
    public boolean shouldDisableWindowAnimationsForUnlock() {
        return this.mStatusBar.isInLaunchTransition();
    }
    
    public boolean shouldDismissOnMenuPressed() {
        return this.mBouncer.shouldDismissOnMenuPressed();
    }
    
    @Override
    public boolean shouldSubtleWindowAnimationsForUnlock() {
        return this.needsBypassFading();
    }
    
    @Override
    public void show(final Bundle bundle) {
        this.mShowing = true;
        this.mNotificationShadeWindowController.setKeyguardShowing(true);
        final KeyguardStateController mKeyguardStateController = this.mKeyguardStateController;
        mKeyguardStateController.notifyKeyguardState(this.mShowing, mKeyguardStateController.isOccluded());
        this.reset(true);
        SysUiStatsLog.write(62, 2);
    }
    
    public void showBouncer(final boolean b) {
        if (this.mShowing && !this.mBouncer.isShowing()) {
            this.mBouncer.show(false, b);
        }
        this.updateStates();
    }
    
    public void showBouncerMessage(final String s, final ColorStateList list) {
        this.mBouncer.showMessage(s, list);
    }
    
    protected void showBouncerOrKeyguard(final boolean b) {
        if (this.mBouncer.needsFullscreenBouncer() && !this.mDozing) {
            this.mStatusBar.hideKeyguard();
            this.mBouncer.show(true);
        }
        else {
            this.mStatusBar.showKeyguard();
            if (b) {
                this.hideBouncer(this.shouldDestroyViewOnReset());
                this.mBouncer.prepare();
            }
        }
        this.updateStates();
    }
    
    @Override
    public void startPreHideAnimation(final Runnable runnable) {
        if (this.mBouncer.isShowing()) {
            this.mBouncer.startPreHideAnimation(runnable);
            this.mStatusBar.onBouncerPreHideAnimation();
        }
        else if (runnable != null) {
            runnable.run();
        }
        this.mNotificationPanelViewController.blockExpansionForCurrentTouch();
        this.updateLockIcon();
    }
    
    protected void updateNavigationBarVisibility(final boolean b) {
        if (this.mStatusBar.getNavigationBarView() != null) {
            if (b) {
                final long navBarShowDelay = this.getNavBarShowDelay();
                if (navBarShowDelay == 0L) {
                    this.mMakeNavigationBarVisibleRunnable.run();
                }
                else {
                    this.mContainer.postOnAnimationDelayed(this.mMakeNavigationBarVisibleRunnable, navBarShowDelay);
                }
            }
            else {
                this.mContainer.removeCallbacks(this.mMakeNavigationBarVisibleRunnable);
                if (ViewRootImpl.sNewInsetsMode == 2) {
                    this.mStatusBar.getNotificationShadeWindowView().getWindowInsetsController().hide(WindowInsets$Type.navigationBars());
                }
                else {
                    this.mStatusBar.getNavigationBarView().getRootView().setVisibility(8);
                }
            }
        }
    }
    
    protected void updateStates() {
        final int systemUiVisibility = this.mContainer.getSystemUiVisibility();
        final boolean mShowing = this.mShowing;
        final boolean mOccluded = this.mOccluded;
        final boolean showing = this.mBouncer.isShowing();
        final boolean fullscreenBouncer = this.mBouncer.isFullscreenBouncer();
        boolean b = true;
        final boolean mLastBouncerDismissible = fullscreenBouncer ^ true;
        final boolean mRemoteInputActive = this.mRemoteInputActive;
        if ((mLastBouncerDismissible || !mShowing || mRemoteInputActive) != (this.mLastBouncerDismissible || !this.mLastShowing || this.mLastRemoteInputActive) || this.mFirstUpdate) {
            if (!mLastBouncerDismissible && mShowing && !mRemoteInputActive) {
                this.mContainer.setSystemUiVisibility(systemUiVisibility | 0x400000);
            }
            else {
                this.mContainer.setSystemUiVisibility(systemUiVisibility & 0xFFBFFFFF);
            }
        }
        final boolean navBarVisible = this.isNavBarVisible();
        if (navBarVisible != this.getLastNavBarVisible() || this.mFirstUpdate) {
            this.updateNavigationBarVisibility(navBarVisible);
        }
        if (showing != this.mLastBouncerShowing || this.mFirstUpdate) {
            this.mNotificationShadeWindowController.setBouncerShowing(showing);
            this.mStatusBar.setBouncerShowing(showing);
            this.updateLockIcon();
        }
        if ((mShowing && !mOccluded) != (this.mLastShowing && !this.mLastOccluded) || this.mFirstUpdate) {
            final KeyguardUpdateMonitor mKeyguardUpdateManager = this.mKeyguardUpdateManager;
            if (!mShowing || mOccluded) {
                b = false;
            }
            mKeyguardUpdateManager.onKeyguardVisibilityChanged(b);
        }
        if (showing != this.mLastBouncerShowing || this.mFirstUpdate) {
            this.mKeyguardUpdateManager.sendKeyguardBouncerChanged(showing);
        }
        this.mFirstUpdate = false;
        this.mLastShowing = mShowing;
        this.mLastOccluded = mOccluded;
        this.mLastBouncerShowing = showing;
        this.mLastBouncerDismissible = mLastBouncerDismissible;
        this.mLastRemoteInputActive = mRemoteInputActive;
        this.mLastDozing = this.mDozing;
        this.mLastPulsing = this.mPulsing;
        this.mLastBiometricMode = this.mBiometricUnlockController.getMode();
        this.mLastGesturalNav = this.mGesturalNav;
        this.mLastIsDocked = this.mIsDocked;
        this.mStatusBar.onKeyguardViewManagerStatesUpdated();
    }
    
    private static class DismissWithActionRequest
    {
        final boolean afterKeyguardGone;
        final Runnable cancelAction;
        final ActivityStarter.OnDismissAction dismissAction;
        final String message;
        
        DismissWithActionRequest(final ActivityStarter.OnDismissAction dismissAction, final Runnable cancelAction, final boolean afterKeyguardGone, final String message) {
            this.dismissAction = dismissAction;
            this.cancelAction = cancelAction;
            this.afterKeyguardGone = afterKeyguardGone;
            this.message = message;
        }
    }
}
