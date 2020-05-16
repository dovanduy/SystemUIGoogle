// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.View$OnLongClickListener;
import android.view.View$OnClickListener;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo;
import android.hardware.biometrics.BiometricSourceType;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.systemui.R$attr;
import com.android.systemui.R$string;
import android.view.ViewGroup$LayoutParams;
import com.android.systemui.R$dimen;
import android.content.res.Configuration;
import java.util.function.Consumer;
import android.view.View;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.content.res.Resources;
import android.view.View$OnAttachStateChangeListener;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.KeyguardIndicationController;
import java.util.Optional;
import com.android.systemui.dock.DockManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import android.view.View$AccessibilityDelegate;
import com.android.systemui.statusbar.policy.AccessibilityController;

public class LockscreenLockIconController
{
    private final AccessibilityController mAccessibilityController;
    private final View$AccessibilityDelegate mAccessibilityDelegate;
    private boolean mBlockUpdates;
    private boolean mBouncerShowingScrimmed;
    private final ConfigurationController mConfigurationController;
    private final ConfigurationController.ConfigurationListener mConfigurationListener;
    private final DockManager.DockEventListener mDockEventListener;
    private final Optional<DockManager> mDockManager;
    private boolean mDocked;
    private boolean mDozing;
    private final HeadsUpManagerPhone mHeadsUpManagerPhone;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardIndicationController mKeyguardIndicationController;
    private boolean mKeyguardJustShown;
    private final KeyguardStateController.Callback mKeyguardMonitorCallback;
    private boolean mKeyguardShowing;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private int mLastState;
    private LockIcon mLockIcon;
    private final LockPatternUtils mLockPatternUtils;
    private final LockscreenGestureLogger mLockscreenGestureLogger;
    private final NotificationWakeUpCoordinator mNotificationWakeUpCoordinator;
    private View$OnAttachStateChangeListener mOnAttachStateChangeListener;
    private boolean mPulsing;
    private final Resources mResources;
    private final StatusBarStateController.StateListener mSBStateListener;
    private final ShadeController mShadeController;
    private boolean mShowingLaunchAffordance;
    private boolean mSimLocked;
    private int mStatusBarState;
    private final StatusBarStateController mStatusBarStateController;
    private boolean mTransientBiometricsError;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private boolean mWakeAndUnlockRunning;
    private final NotificationWakeUpCoordinator.WakeUpListener mWakeUpListener;
    
    public LockscreenLockIconController(final LockscreenGestureLogger mLockscreenGestureLogger, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final LockPatternUtils mLockPatternUtils, final ShadeController mShadeController, final AccessibilityController mAccessibilityController, final KeyguardIndicationController mKeyguardIndicationController, final StatusBarStateController mStatusBarStateController, final ConfigurationController mConfigurationController, final NotificationWakeUpCoordinator mNotificationWakeUpCoordinator, final KeyguardBypassController mKeyguardBypassController, final DockManager value, final KeyguardStateController mKeyguardStateController, final Resources mResources, final HeadsUpManagerPhone mHeadsUpManagerPhone) {
        this.mStatusBarState = 0;
        this.mOnAttachStateChangeListener = (View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
                LockscreenLockIconController.this.mStatusBarStateController.addCallback(LockscreenLockIconController.this.mSBStateListener);
                LockscreenLockIconController.this.mConfigurationController.addCallback(LockscreenLockIconController.this.mConfigurationListener);
                LockscreenLockIconController.this.mNotificationWakeUpCoordinator.addListener(LockscreenLockIconController.this.mWakeUpListener);
                LockscreenLockIconController.this.mKeyguardUpdateMonitor.registerCallback(LockscreenLockIconController.this.mUpdateMonitorCallback);
                LockscreenLockIconController.this.mKeyguardStateController.addCallback(LockscreenLockIconController.this.mKeyguardMonitorCallback);
                LockscreenLockIconController.this.mDockManager.ifPresent(new _$$Lambda$LockscreenLockIconController$1$lAN9mjl0bP11onyNXKoQAiuwbo4(this));
                final LockscreenLockIconController this$0 = LockscreenLockIconController.this;
                this$0.mSimLocked = this$0.mKeyguardUpdateMonitor.isSimPinSecure();
                LockscreenLockIconController.this.mConfigurationListener.onThemeChanged();
                LockscreenLockIconController.this.update();
            }
            
            public void onViewDetachedFromWindow(final View view) {
                LockscreenLockIconController.this.mStatusBarStateController.removeCallback(LockscreenLockIconController.this.mSBStateListener);
                LockscreenLockIconController.this.mConfigurationController.removeCallback(LockscreenLockIconController.this.mConfigurationListener);
                LockscreenLockIconController.this.mNotificationWakeUpCoordinator.removeListener(LockscreenLockIconController.this.mWakeUpListener);
                LockscreenLockIconController.this.mKeyguardUpdateMonitor.removeCallback(LockscreenLockIconController.this.mUpdateMonitorCallback);
                LockscreenLockIconController.this.mKeyguardStateController.removeCallback(LockscreenLockIconController.this.mKeyguardMonitorCallback);
                LockscreenLockIconController.this.mDockManager.ifPresent(new _$$Lambda$LockscreenLockIconController$1$33uhHOghx__czm01x2awmcBSkdM(this));
            }
        };
        this.mSBStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onDozeAmountChanged(final float n, final float dozeAmount) {
                if (LockscreenLockIconController.this.mLockIcon != null) {
                    LockscreenLockIconController.this.mLockIcon.setDozeAmount(dozeAmount);
                }
            }
            
            @Override
            public void onDozingChanged(final boolean b) {
                LockscreenLockIconController.this.setDozing(b);
            }
            
            @Override
            public void onStateChanged(final int n) {
                LockscreenLockIconController.this.setStatusBarState(n);
            }
        };
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            private int mDensity;
            
            @Override
            public void onConfigChanged(final Configuration configuration) {
                final int densityDpi = configuration.densityDpi;
                if (densityDpi != this.mDensity) {
                    this.mDensity = densityDpi;
                    LockscreenLockIconController.this.update();
                }
            }
            
            @Override
            public void onDensityOrFontScaleChanged() {
                if (LockscreenLockIconController.this.mLockIcon == null) {
                    return;
                }
                final ViewGroup$LayoutParams layoutParams = LockscreenLockIconController.this.mLockIcon.getLayoutParams();
                if (layoutParams == null) {
                    return;
                }
                layoutParams.width = LockscreenLockIconController.this.mLockIcon.getResources().getDimensionPixelSize(R$dimen.keyguard_lock_width);
                layoutParams.height = LockscreenLockIconController.this.mLockIcon.getResources().getDimensionPixelSize(R$dimen.keyguard_lock_height);
                LockscreenLockIconController.this.mLockIcon.setLayoutParams(layoutParams);
                LockscreenLockIconController.this.update(true);
            }
            
            @Override
            public void onLocaleListChanged() {
                if (LockscreenLockIconController.this.mLockIcon == null) {
                    return;
                }
                LockscreenLockIconController.this.mLockIcon.setContentDescription(LockscreenLockIconController.this.mLockIcon.getResources().getText(R$string.accessibility_unlock_button));
                LockscreenLockIconController.this.update(true);
            }
            
            @Override
            public void onThemeChanged() {
                if (LockscreenLockIconController.this.mLockIcon == null) {
                    return;
                }
                final TypedArray obtainStyledAttributes = LockscreenLockIconController.this.mLockIcon.getContext().getTheme().obtainStyledAttributes((AttributeSet)null, new int[] { R$attr.wallpaperTextColor }, 0, 0);
                final int color = obtainStyledAttributes.getColor(0, -1);
                obtainStyledAttributes.recycle();
                LockscreenLockIconController.this.mLockIcon.onThemeChange(color);
            }
        };
        this.mWakeUpListener = new NotificationWakeUpCoordinator.WakeUpListener() {
            @Override
            public void onFullyHiddenChanged(final boolean b) {
                if (LockscreenLockIconController.this.mKeyguardBypassController.getBypassEnabled() && LockscreenLockIconController.this.updateIconVisibility()) {
                    LockscreenLockIconController.this.update();
                }
            }
            
            @Override
            public void onPulseExpansionChanged(final boolean b) {
            }
        };
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onBiometricRunningStateChanged(final boolean b, final BiometricSourceType biometricSourceType) {
                LockscreenLockIconController.this.update();
            }
            
            @Override
            public void onKeyguardVisibilityChanged(final boolean b) {
                LockscreenLockIconController.this.update();
            }
            
            @Override
            public void onSimStateChanged(final int n, final int n2, final int n3) {
                final LockscreenLockIconController this$0 = LockscreenLockIconController.this;
                this$0.mSimLocked = this$0.mKeyguardUpdateMonitor.isSimPinSecure();
                LockscreenLockIconController.this.update();
            }
            
            @Override
            public void onStrongAuthStateChanged(final int n) {
                LockscreenLockIconController.this.update();
            }
        };
        this.mDockEventListener = new _$$Lambda$LockscreenLockIconController$YwLkB4yDF5Gwcj5NX5hNSw8eA7E(this);
        this.mKeyguardMonitorCallback = new KeyguardStateController.Callback() {
            @Override
            public void onKeyguardFadingAwayChanged() {
                if (!LockscreenLockIconController.this.mKeyguardStateController.isKeyguardFadingAway() && LockscreenLockIconController.this.mBlockUpdates) {
                    LockscreenLockIconController.this.mBlockUpdates = false;
                    LockscreenLockIconController.this.update(true);
                }
            }
            
            @Override
            public void onKeyguardShowingChanged() {
                final boolean access$2000 = LockscreenLockIconController.this.mKeyguardShowing;
                final LockscreenLockIconController this$0 = LockscreenLockIconController.this;
                this$0.mKeyguardShowing = this$0.mKeyguardStateController.isShowing();
                boolean b2;
                final boolean b = b2 = false;
                if (!access$2000) {
                    b2 = b;
                    if (LockscreenLockIconController.this.mKeyguardShowing) {
                        b2 = b;
                        if (LockscreenLockIconController.this.mBlockUpdates) {
                            LockscreenLockIconController.this.mBlockUpdates = false;
                            b2 = true;
                        }
                    }
                }
                if (!access$2000 && LockscreenLockIconController.this.mKeyguardShowing) {
                    LockscreenLockIconController.this.mKeyguardJustShown = true;
                }
                LockscreenLockIconController.this.update(b2);
            }
            
            @Override
            public void onUnlockedChanged() {
                LockscreenLockIconController.this.update();
            }
        };
        this.mAccessibilityDelegate = new View$AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                final boolean fingerprintDetectionRunning = LockscreenLockIconController.this.mKeyguardUpdateMonitor.isFingerprintDetectionRunning();
                final boolean unlockingWithBiometricAllowed = LockscreenLockIconController.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true);
                if (fingerprintDetectionRunning && unlockingWithBiometricAllowed) {
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(16, (CharSequence)LockscreenLockIconController.this.mResources.getString(R$string.accessibility_unlock_without_fingerprint)));
                    accessibilityNodeInfo.setHintText((CharSequence)LockscreenLockIconController.this.mResources.getString(R$string.accessibility_waiting_for_fingerprint));
                }
                else if (LockscreenLockIconController.this.getState() == 2) {
                    accessibilityNodeInfo.setClassName((CharSequence)LockIcon.class.getName());
                    accessibilityNodeInfo.setContentDescription((CharSequence)LockscreenLockIconController.this.mResources.getString(R$string.accessibility_scanning_face));
                }
            }
        };
        this.mLockscreenGestureLogger = mLockscreenGestureLogger;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mLockPatternUtils = mLockPatternUtils;
        this.mShadeController = mShadeController;
        this.mAccessibilityController = mAccessibilityController;
        this.mKeyguardIndicationController = mKeyguardIndicationController;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mConfigurationController = mConfigurationController;
        this.mNotificationWakeUpCoordinator = mNotificationWakeUpCoordinator;
        this.mKeyguardBypassController = mKeyguardBypassController;
        Optional<DockManager> mDockManager;
        if (value == null) {
            mDockManager = Optional.empty();
        }
        else {
            mDockManager = Optional.of(value);
        }
        this.mDockManager = mDockManager;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mResources = mResources;
        this.mHeadsUpManagerPhone = mHeadsUpManagerPhone;
        this.mKeyguardIndicationController.setLockIconController(this);
    }
    
    private boolean canBlockUpdates() {
        return this.mKeyguardShowing || this.mKeyguardStateController.isKeyguardFadingAway();
    }
    
    private int getState() {
        if ((this.mKeyguardStateController.canDismissLockScreen() || !this.mKeyguardShowing || this.mKeyguardStateController.isKeyguardGoingAway()) && !this.mSimLocked) {
            return 1;
        }
        if (this.mTransientBiometricsError) {
            return 3;
        }
        if (this.mKeyguardUpdateMonitor.isFaceDetectionRunning() && !this.mPulsing) {
            return 2;
        }
        return 0;
    }
    
    private void handleClick(final View view) {
        if (!this.mAccessibilityController.isAccessibilityEnabled()) {
            return;
        }
        this.mShadeController.animateCollapsePanels(0, true);
    }
    
    private boolean handleLongClick(final View view) {
        this.mLockscreenGestureLogger.write(191, 0, 0);
        this.mKeyguardIndicationController.showTransientIndication(R$string.keyguard_indication_trust_disabled);
        this.mKeyguardUpdateMonitor.onLockIconPressed();
        this.mLockPatternUtils.requireCredentialEntry(KeyguardUpdateMonitor.getCurrentUser());
        return true;
    }
    
    private void setDozing(final boolean mDozing) {
        this.mDozing = mDozing;
        this.update();
    }
    
    private void setStatusBarState(final int mStatusBarState) {
        this.mStatusBarState = mStatusBarState;
        this.updateIconVisibility();
    }
    
    private void update() {
        this.update(false);
    }
    
    private void update(final boolean b) {
        final int state = this.getState();
        boolean b2 = this.mLastState != state || b;
        if (this.mBlockUpdates) {
            b2 = b2;
            if (this.canBlockUpdates()) {
                b2 = false;
            }
        }
        if (b2) {
            final LockIcon mLockIcon = this.mLockIcon;
            if (mLockIcon != null) {
                mLockIcon.update(state, this.mPulsing, this.mDozing, this.mKeyguardJustShown);
            }
        }
        this.mLastState = state;
        this.mKeyguardJustShown = false;
        this.updateIconVisibility();
        this.updateClickability();
    }
    
    private void updateClickability() {
        if (this.mAccessibilityController == null) {
            return;
        }
        final boolean methodSecure = this.mKeyguardStateController.isMethodSecure();
        boolean longClickable = true;
        final boolean b = methodSecure && this.mKeyguardStateController.canDismissLockScreen();
        final boolean accessibilityEnabled = this.mAccessibilityController.isAccessibilityEnabled();
        final LockIcon mLockIcon = this.mLockIcon;
        if (mLockIcon != null) {
            mLockIcon.setClickable(accessibilityEnabled);
            final LockIcon mLockIcon2 = this.mLockIcon;
            if (!b || accessibilityEnabled) {
                longClickable = false;
            }
            mLockIcon2.setLongClickable(longClickable);
            this.mLockIcon.setFocusable(this.mAccessibilityController.isAccessibilityEnabled());
        }
    }
    
    private boolean updateIconVisibility() {
        int n;
        final boolean b = (n = (((this.mDozing && (!this.mPulsing || this.mDocked)) || this.mWakeAndUnlockRunning || this.mShowingLaunchAffordance) ? 1 : 0)) != 0;
        Label_0121: {
            if (this.mKeyguardBypassController.getBypassEnabled()) {
                n = (b ? 1 : 0);
                if (!this.mBouncerShowingScrimmed) {
                    if (!this.mHeadsUpManagerPhone.isHeadsUpGoingAway() && !this.mHeadsUpManagerPhone.hasPinnedHeadsUp()) {
                        n = (b ? 1 : 0);
                        if (this.mStatusBarState != 1) {
                            break Label_0121;
                        }
                    }
                    n = (b ? 1 : 0);
                    if (!this.mNotificationWakeUpCoordinator.getNotificationsFullyHidden()) {
                        n = 1;
                    }
                }
            }
        }
        final LockIcon mLockIcon = this.mLockIcon;
        return mLockIcon != null && mLockIcon.updateIconVisibility((boolean)((n ^ 0x1) != 0x0));
    }
    
    public void attach(final LockIcon mLockIcon) {
        (this.mLockIcon = mLockIcon).setOnClickListener((View$OnClickListener)new _$$Lambda$LockscreenLockIconController$w6uFCwNQV4Mtc7oy2_mEXXG52_I(this));
        this.mLockIcon.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$LockscreenLockIconController$LslFmHw3JlLgJluLcqL2mxJusEk(this));
        this.mLockIcon.setAccessibilityDelegate(this.mAccessibilityDelegate);
        if (this.mLockIcon.isAttachedToWindow()) {
            this.mOnAttachStateChangeListener.onViewAttachedToWindow((View)this.mLockIcon);
        }
        this.mLockIcon.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        this.setStatusBarState(this.mStatusBarStateController.getState());
    }
    
    public void onBiometricAuthModeChanged(final boolean b, final boolean b2) {
        if (b) {
            this.mWakeAndUnlockRunning = true;
        }
        if (b2 && this.mKeyguardBypassController.getBypassEnabled() && this.canBlockUpdates()) {
            this.mBlockUpdates = true;
        }
        this.update();
    }
    
    public void onBouncerPreHideAnimation() {
        this.update();
    }
    
    public void onScrimVisibilityChanged(final Integer n) {
        if (this.mWakeAndUnlockRunning && n == 0) {
            this.mWakeAndUnlockRunning = false;
            this.update();
        }
    }
    
    public void onShowingLaunchAffordanceChanged(final Boolean b) {
        this.mShowingLaunchAffordance = b;
        this.update();
    }
    
    public void setBouncerShowingScrimmed(final boolean mBouncerShowingScrimmed) {
        this.mBouncerShowingScrimmed = mBouncerShowingScrimmed;
        if (this.mKeyguardBypassController.getBypassEnabled()) {
            this.update();
        }
    }
    
    public void setPulsing(final boolean mPulsing) {
        this.mPulsing = mPulsing;
        this.update();
    }
    
    public void setTransientBiometricsError(final boolean mTransientBiometricsError) {
        this.mTransientBiometricsError = mTransientBiometricsError;
        this.update();
    }
}
