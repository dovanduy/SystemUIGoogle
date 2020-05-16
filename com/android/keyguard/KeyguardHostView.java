// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.content.res.ColorStateList;
import com.android.settingslib.Utils;
import java.io.File;
import android.app.ActivityManager;
import com.android.systemui.R$bool;
import com.android.systemui.R$id;
import android.graphics.Canvas;
import android.view.KeyEvent;
import com.android.systemui.Dependency;
import android.util.Log;
import android.util.AttributeSet;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.plugins.ActivityStarter;
import android.media.AudioManager;
import android.widget.FrameLayout;

public class KeyguardHostView extends FrameLayout implements SecurityCallback
{
    private AudioManager mAudioManager;
    private Runnable mCancelAction;
    private ActivityStarter.OnDismissAction mDismissAction;
    protected LockPatternUtils mLockPatternUtils;
    private KeyguardSecurityContainer mSecurityContainer;
    private TelephonyManager mTelephonyManager;
    private final KeyguardUpdateMonitorCallback mUpdateCallback;
    protected ViewMediatorCallback mViewMediatorCallback;
    
    public KeyguardHostView(final Context context) {
        this(context, null);
    }
    
    public KeyguardHostView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTelephonyManager = null;
        this.mUpdateCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onTrustGrantedWithFlags(int n, final int n2) {
                if (n2 != KeyguardUpdateMonitor.getCurrentUser()) {
                    return;
                }
                if (!KeyguardHostView.this.isAttachedToWindow()) {
                    return;
                }
                final boolean visibleToUser = KeyguardHostView.this.isVisibleToUser();
                final int n3 = 1;
                final boolean b = (n & 0x1) != 0x0;
                if ((n & 0x2) != 0x0) {
                    n = n3;
                }
                else {
                    n = 0;
                }
                if (b || n != 0) {
                    if (KeyguardHostView.this.mViewMediatorCallback.isScreenOn() && (visibleToUser || n != 0)) {
                        if (!visibleToUser) {
                            Log.i("KeyguardViewBase", "TrustAgent dismissed Keyguard.");
                        }
                        KeyguardHostView.this.dismiss(false, n2);
                    }
                    else {
                        KeyguardHostView.this.mViewMediatorCallback.playTrustedSound();
                    }
                }
            }
            
            @Override
            public void onUserSwitchComplete(final int n) {
                KeyguardHostView.this.getSecurityContainer().showPrimarySecurityScreen(false);
            }
        };
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mUpdateCallback);
    }
    
    private void handleMediaKeyEvent(final KeyEvent keyEvent) {
        synchronized (this) {
            if (this.mAudioManager == null) {
                this.mAudioManager = (AudioManager)this.getContext().getSystemService("audio");
            }
            // monitorexit(this)
            this.mAudioManager.dispatchMediaKeyEvent(keyEvent);
        }
    }
    
    public void cancelDismissAction() {
        this.setOnDismissAction(null, null);
    }
    
    public void cleanUp() {
        this.getSecurityContainer().onPause();
    }
    
    public boolean dismiss(final int n) {
        return this.dismiss(false, n);
    }
    
    public boolean dismiss(final boolean b, final int n) {
        return this.mSecurityContainer.showNextSecurityScreenOrFinish(b, n);
    }
    
    protected void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
        final ViewMediatorCallback mViewMediatorCallback = this.mViewMediatorCallback;
        if (mViewMediatorCallback != null) {
            mViewMediatorCallback.keyguardDoneDrawing();
        }
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        return this.interceptMediaKey(keyEvent) || super.dispatchKeyEvent(keyEvent);
    }
    
    public void finish(final boolean b, final int n) {
        final ActivityStarter.OnDismissAction mDismissAction = this.mDismissAction;
        boolean onDismiss;
        if (mDismissAction != null) {
            onDismiss = mDismissAction.onDismiss();
            this.mDismissAction = null;
            this.mCancelAction = null;
        }
        else {
            onDismiss = false;
        }
        final ViewMediatorCallback mViewMediatorCallback = this.mViewMediatorCallback;
        if (mViewMediatorCallback != null) {
            if (onDismiss) {
                mViewMediatorCallback.keyguardDonePending(b, n);
            }
            else {
                mViewMediatorCallback.keyguardDone(b, n);
            }
        }
    }
    
    public CharSequence getAccessibilityTitleForCurrentMode() {
        return this.mSecurityContainer.getTitle();
    }
    
    public KeyguardSecurityModel.SecurityMode getCurrentSecurityMode() {
        return this.mSecurityContainer.getCurrentSecurityMode();
    }
    
    protected KeyguardSecurityContainer getSecurityContainer() {
        return this.mSecurityContainer;
    }
    
    public KeyguardSecurityModel.SecurityMode getSecurityMode() {
        return this.mSecurityContainer.getSecurityMode();
    }
    
    public boolean hasDismissActions() {
        return this.mDismissAction != null || this.mCancelAction != null;
    }
    
    public boolean interceptMediaKey(final KeyEvent keyEvent) {
        final int keyCode = keyEvent.getKeyCode();
        if (keyEvent.getAction() == 0) {
            Label_0132: {
                if (keyCode != 79 && keyCode != 130 && keyCode != 222) {
                    if (keyCode != 126 && keyCode != 127) {
                        switch (keyCode) {
                            default: {
                                return false;
                            }
                            case 85: {
                                break;
                            }
                            case 86:
                            case 87:
                            case 88:
                            case 89:
                            case 90:
                            case 91: {
                                break Label_0132;
                            }
                        }
                    }
                    if (this.mTelephonyManager == null) {
                        this.mTelephonyManager = (TelephonyManager)this.getContext().getSystemService("phone");
                    }
                    final TelephonyManager mTelephonyManager = this.mTelephonyManager;
                    if (mTelephonyManager != null && mTelephonyManager.getCallState() != 0) {
                        return true;
                    }
                }
            }
            this.handleMediaKeyEvent(keyEvent);
            return true;
        }
        if (keyEvent.getAction() == 1) {
            if (keyCode != 79 && keyCode != 130 && keyCode != 222 && keyCode != 126 && keyCode != 127) {
                switch (keyCode) {
                    default: {
                        return false;
                    }
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                    case 90:
                    case 91: {
                        break;
                    }
                }
            }
            this.handleMediaKeyEvent(keyEvent);
            return true;
        }
        return false;
    }
    
    public void onCancelClicked() {
        this.mViewMediatorCallback.onCancelClicked();
    }
    
    protected void onFinishInflate() {
        this.mSecurityContainer = (KeyguardSecurityContainer)this.findViewById(R$id.keyguard_security_container);
        final LockPatternUtils lockPatternUtils = new LockPatternUtils(super.mContext);
        this.mLockPatternUtils = lockPatternUtils;
        this.mSecurityContainer.setLockPatternUtils(lockPatternUtils);
        this.mSecurityContainer.setSecurityCallback((KeyguardSecurityContainer.SecurityCallback)this);
        this.mSecurityContainer.showPrimarySecurityScreen(false);
    }
    
    public void onPause() {
        this.mSecurityContainer.showPrimarySecurityScreen(true);
        this.mSecurityContainer.onPause();
        this.clearFocus();
    }
    
    public void onResume() {
        this.mSecurityContainer.onResume(1);
        this.requestFocus();
    }
    
    public void onSecurityModeChanged(final KeyguardSecurityModel.SecurityMode securityMode, final boolean needsInput) {
        final ViewMediatorCallback mViewMediatorCallback = this.mViewMediatorCallback;
        if (mViewMediatorCallback != null) {
            mViewMediatorCallback.setNeedsInput(needsInput);
        }
    }
    
    public void reset() {
        this.mViewMediatorCallback.resetKeyguard();
    }
    
    public void resetSecurityContainer() {
        this.mSecurityContainer.reset();
    }
    
    public void setLockPatternUtils(final LockPatternUtils lockPatternUtils) {
        this.mLockPatternUtils = lockPatternUtils;
        this.mSecurityContainer.setLockPatternUtils(lockPatternUtils);
    }
    
    public void setOnDismissAction(final ActivityStarter.OnDismissAction mDismissAction, final Runnable mCancelAction) {
        final Runnable mCancelAction2 = this.mCancelAction;
        if (mCancelAction2 != null) {
            mCancelAction2.run();
            this.mCancelAction = null;
        }
        this.mDismissAction = mDismissAction;
        this.mCancelAction = mCancelAction;
    }
    
    public void setViewMediatorCallback(final ViewMediatorCallback mViewMediatorCallback) {
        (this.mViewMediatorCallback = mViewMediatorCallback).setNeedsInput(this.mSecurityContainer.needsInput());
    }
    
    public boolean shouldEnableMenuKey() {
        final boolean boolean1 = this.getResources().getBoolean(R$bool.config_disableMenuKeyInLockScreen);
        final boolean runningInTestHarness = ActivityManager.isRunningInTestHarness();
        final boolean exists = new File("/data/local/enable_menu_key").exists();
        return !boolean1 || runningInTestHarness || exists;
    }
    
    public void showErrorMessage(final CharSequence charSequence) {
        this.showMessage(charSequence, Utils.getColorError(super.mContext));
    }
    
    public void showMessage(final CharSequence charSequence, final ColorStateList list) {
        this.mSecurityContainer.showMessage(charSequence, list);
    }
    
    public void showPrimarySecurityScreen() {
        this.mSecurityContainer.showPrimarySecurityScreen(false);
    }
    
    public void showPromptReason(final int n) {
        this.mSecurityContainer.showPromptReason(n);
    }
    
    public void startAppearAnimation() {
        this.mSecurityContainer.startAppearAnimation();
    }
    
    public void startDisappearAnimation(final Runnable runnable) {
        if (!this.mSecurityContainer.startDisappearAnimation(runnable) && runnable != null) {
            runnable.run();
        }
    }
    
    public void userActivity() {
        final ViewMediatorCallback mViewMediatorCallback = this.mViewMediatorCallback;
        if (mViewMediatorCallback != null) {
            mViewMediatorCallback.userActivity();
        }
    }
}
