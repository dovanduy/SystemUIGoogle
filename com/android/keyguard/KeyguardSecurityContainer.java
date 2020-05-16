// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.WindowInsets$Type;
import android.view.ViewRootImpl;
import android.view.WindowInsets;
import com.android.systemui.R$layout;
import android.util.Log;
import android.app.Activity;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.android.systemui.R$string;
import android.app.admin.DevicePolicyManager;
import android.util.Slog;
import android.content.ComponentName;
import com.android.systemui.R$id;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Handler;
import android.os.Looper;
import com.android.systemui.SystemUIFactory;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import android.metrics.LogMaker;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewConfiguration;
import android.view.VelocityTracker;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.InjectionInflationController;
import android.app.AlertDialog;
import android.widget.FrameLayout;

public class KeyguardSecurityContainer extends FrameLayout implements KeyguardSecurityView
{
    private int mActivePointerId;
    private AlertDialog mAlertDialog;
    private KeyguardSecurityCallback mCallback;
    private KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection;
    private KeyguardSecurityView mCurrentSecurityView;
    private InjectionInflationController mInjectionInflationController;
    private boolean mIsDragging;
    private final KeyguardStateController mKeyguardStateController;
    private float mLastTouchY;
    private LockPatternUtils mLockPatternUtils;
    private final MetricsLogger mMetricsLogger;
    private KeyguardSecurityCallback mNullCallback;
    private AdminSecondaryLockScreenController mSecondaryLockScreenController;
    private SecurityCallback mSecurityCallback;
    private KeyguardSecurityModel mSecurityModel;
    private KeyguardSecurityViewFlipper mSecurityViewFlipper;
    private final SpringAnimation mSpringAnimation;
    private float mStartTouchY;
    private boolean mSwipeUpToRetry;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private final VelocityTracker mVelocityTracker;
    private final ViewConfiguration mViewConfiguration;
    
    public KeyguardSecurityContainer(final Context context) {
        this(context, null, 0);
    }
    
    public KeyguardSecurityContainer(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public KeyguardSecurityContainer(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mCurrentSecuritySelection = KeyguardSecurityModel.SecurityMode.Invalid;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mLastTouchY = -1.0f;
        this.mActivePointerId = -1;
        this.mStartTouchY = -1.0f;
        this.mCallback = new KeyguardSecurityCallback() {
            @Override
            public void dismiss(final boolean b, final int n) {
                KeyguardSecurityContainer.this.mSecurityCallback.dismiss(b, n);
            }
            
            @Override
            public void onCancelClicked() {
                KeyguardSecurityContainer.this.mSecurityCallback.onCancelClicked();
            }
            
            @Override
            public void onUserInput() {
                KeyguardSecurityContainer.this.mUpdateMonitor.cancelFaceAuth();
            }
            
            @Override
            public void reportUnlockAttempt(int type, final boolean b, final int n) {
                if (b) {
                    SysUiStatsLog.write(64, 2);
                    KeyguardSecurityContainer.this.mLockPatternUtils.reportSuccessfulPasswordAttempt(type);
                    ThreadUtils.postOnBackgroundThread((Runnable)_$$Lambda$KeyguardSecurityContainer$1$ZmZG61mJJm4DEtN57wo5kJoWZGk.INSTANCE);
                }
                else {
                    SysUiStatsLog.write(64, 1);
                    KeyguardSecurityContainer.this.reportFailedUnlockAttempt(type, n);
                }
                final MetricsLogger access$500 = KeyguardSecurityContainer.this.mMetricsLogger;
                final LogMaker logMaker = new LogMaker(197);
                if (b) {
                    type = 10;
                }
                else {
                    type = 11;
                }
                access$500.write(logMaker.setType(type));
            }
            
            @Override
            public void reset() {
                KeyguardSecurityContainer.this.mSecurityCallback.reset();
            }
            
            @Override
            public void userActivity() {
                if (KeyguardSecurityContainer.this.mSecurityCallback != null) {
                    KeyguardSecurityContainer.this.mSecurityCallback.userActivity();
                }
            }
        };
        this.mNullCallback = new KeyguardSecurityCallback() {
            @Override
            public void dismiss(final boolean b, final int n) {
            }
            
            @Override
            public void onUserInput() {
            }
            
            @Override
            public void reportUnlockAttempt(final int n, final boolean b, final int n2) {
            }
            
            @Override
            public void reset() {
            }
            
            @Override
            public void userActivity() {
            }
        };
        this.mSecurityModel = Dependency.get(KeyguardSecurityModel.class);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class);
        this.mSpringAnimation = new SpringAnimation((K)this, (FloatPropertyCompat<K>)DynamicAnimation.Y);
        this.mInjectionInflationController = new InjectionInflationController(SystemUIFactory.getInstance().getRootComponent());
        this.mViewConfiguration = ViewConfiguration.get(context);
        this.mKeyguardStateController = Dependency.get(KeyguardStateController.class);
        this.mSecondaryLockScreenController = new AdminSecondaryLockScreenController(context, (ViewGroup)this, this.mUpdateMonitor, this.mCallback, new Handler(Looper.myLooper()));
    }
    
    private KeyguardSecurityView getSecurityView(final KeyguardSecurityModel.SecurityMode securityMode) {
        final int securityViewIdForMode = this.getSecurityViewIdForMode(securityMode);
        while (true) {
            for (int childCount = this.mSecurityViewFlipper.getChildCount(), i = 0; i < childCount; ++i) {
                if (this.mSecurityViewFlipper.getChildAt(i).getId() == securityViewIdForMode) {
                    final KeyguardSecurityView keyguardSecurityView = (KeyguardSecurityView)this.mSecurityViewFlipper.getChildAt(i);
                    final int layoutId = this.getLayoutIdFor(securityMode);
                    KeyguardSecurityView keyguardSecurityView2 = keyguardSecurityView;
                    if (keyguardSecurityView == null) {
                        keyguardSecurityView2 = keyguardSecurityView;
                        if (layoutId != 0) {
                            final View inflate = this.mInjectionInflationController.injectable(LayoutInflater.from(super.mContext)).inflate(layoutId, (ViewGroup)this.mSecurityViewFlipper, false);
                            this.mSecurityViewFlipper.addView(inflate);
                            this.updateSecurityView(inflate);
                            keyguardSecurityView2 = (KeyguardSecurityView)inflate;
                            keyguardSecurityView2.reset();
                        }
                    }
                    return keyguardSecurityView2;
                }
            }
            final KeyguardSecurityView keyguardSecurityView = null;
            continue;
        }
    }
    
    private int getSecurityViewIdForMode(final KeyguardSecurityModel.SecurityMode securityMode) {
        final int n = KeyguardSecurityContainer$3.$SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[securityMode.ordinal()];
        if (n == 1) {
            return R$id.keyguard_pattern_view;
        }
        if (n == 2) {
            return R$id.keyguard_pin_view;
        }
        if (n == 3) {
            return R$id.keyguard_password_view;
        }
        if (n == 6) {
            return R$id.keyguard_sim_pin_view;
        }
        if (n != 7) {
            return 0;
        }
        return R$id.keyguard_sim_puk_view;
    }
    
    private void reportFailedUnlockAttempt(final int n, final int n2) {
        final int currentFailedPasswordAttempts = this.mLockPatternUtils.getCurrentFailedPasswordAttempts(n);
        int n3 = 1;
        final int n4 = currentFailedPasswordAttempts + 1;
        final DevicePolicyManager devicePolicyManager = this.mLockPatternUtils.getDevicePolicyManager();
        final int maximumFailedPasswordsForWipe = devicePolicyManager.getMaximumFailedPasswordsForWipe((ComponentName)null, n);
        int n5;
        if (maximumFailedPasswordsForWipe > 0) {
            n5 = maximumFailedPasswordsForWipe - n4;
        }
        else {
            n5 = Integer.MAX_VALUE;
        }
        if (n5 < 5) {
            final int profileWithMinimumFailedPasswordsForWipe = devicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(n);
            if (profileWithMinimumFailedPasswordsForWipe == n) {
                if (profileWithMinimumFailedPasswordsForWipe != 0) {
                    n3 = 3;
                }
            }
            else if (profileWithMinimumFailedPasswordsForWipe != -10000) {
                n3 = 2;
            }
            if (n5 > 0) {
                this.showAlmostAtWipeDialog(n4, n5, n3);
            }
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Too many unlock attempts; user ");
                sb.append(profileWithMinimumFailedPasswordsForWipe);
                sb.append(" will be wiped!");
                Slog.i("KeyguardSecurityView", sb.toString());
                this.showWipeDialog(n4, n3);
            }
        }
        this.mLockPatternUtils.reportFailedPasswordAttempt(n);
        if (n2 > 0) {
            this.mLockPatternUtils.reportPasswordLockout(n2, n);
            this.showTimeoutDialog(n, n2);
        }
    }
    
    private void showAlmostAtWipeDialog(final int i, final int j, final int n) {
        String s;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    s = null;
                }
                else {
                    s = super.mContext.getString(R$string.kg_failed_attempts_almost_at_erase_user, new Object[] { i, j });
                }
            }
            else {
                s = super.mContext.getString(R$string.kg_failed_attempts_almost_at_erase_profile, new Object[] { i, j });
            }
        }
        else {
            s = super.mContext.getString(R$string.kg_failed_attempts_almost_at_wipe, new Object[] { i, j });
        }
        this.showDialog(null, s);
    }
    
    private void showDialog(final String title, final String message) {
        final AlertDialog mAlertDialog = this.mAlertDialog;
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        final AlertDialog create = new AlertDialog$Builder(super.mContext).setTitle((CharSequence)title).setMessage((CharSequence)message).setCancelable(false).setNeutralButton(R$string.ok, (DialogInterface$OnClickListener)null).create();
        this.mAlertDialog = create;
        if (!(super.mContext instanceof Activity)) {
            create.getWindow().setType(2009);
        }
        this.mAlertDialog.show();
    }
    
    private void showSecurityScreen(final KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection) {
        final KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection2 = this.mCurrentSecuritySelection;
        if (mCurrentSecuritySelection == mCurrentSecuritySelection2) {
            return;
        }
        final KeyguardSecurityView securityView = this.getSecurityView(mCurrentSecuritySelection2);
        final KeyguardSecurityView securityView2 = this.getSecurityView(mCurrentSecuritySelection);
        if (securityView != null) {
            securityView.onPause();
            securityView.setKeyguardCallback(this.mNullCallback);
        }
        if (mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            securityView2.onResume(2);
            securityView2.setKeyguardCallback(this.mCallback);
        }
        final int childCount = this.mSecurityViewFlipper.getChildCount();
        final int securityViewIdForMode = this.getSecurityViewIdForMode(mCurrentSecuritySelection);
        final boolean b = false;
        for (int i = 0; i < childCount; ++i) {
            if (this.mSecurityViewFlipper.getChildAt(i).getId() == securityViewIdForMode) {
                this.mSecurityViewFlipper.setDisplayedChild(i);
                break;
            }
        }
        this.mCurrentSecuritySelection = mCurrentSecuritySelection;
        this.mCurrentSecurityView = securityView2;
        final SecurityCallback mSecurityCallback = this.mSecurityCallback;
        boolean b2 = b;
        if (mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            b2 = b;
            if (securityView2.needsInput()) {
                b2 = true;
            }
        }
        mSecurityCallback.onSecurityModeChanged(mCurrentSecuritySelection, b2);
    }
    
    private void showTimeoutDialog(final int n, int n2) {
        final int i = n2 / 1000;
        n2 = KeyguardSecurityContainer$3.$SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[this.mSecurityModel.getSecurityMode(n).ordinal()];
        if (n2 != 1) {
            if (n2 != 2) {
                if (n2 != 3) {
                    n2 = 0;
                }
                else {
                    n2 = R$string.kg_too_many_failed_password_attempts_dialog_message;
                }
            }
            else {
                n2 = R$string.kg_too_many_failed_pin_attempts_dialog_message;
            }
        }
        else {
            n2 = R$string.kg_too_many_failed_pattern_attempts_dialog_message;
        }
        if (n2 != 0) {
            this.showDialog(null, super.mContext.getString(n2, new Object[] { this.mLockPatternUtils.getCurrentFailedPasswordAttempts(n), i }));
        }
    }
    
    private void showWipeDialog(final int i, final int n) {
        String s;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    s = null;
                }
                else {
                    s = super.mContext.getString(R$string.kg_failed_attempts_now_erasing_user, new Object[] { i });
                }
            }
            else {
                s = super.mContext.getString(R$string.kg_failed_attempts_now_erasing_profile, new Object[] { i });
            }
        }
        else {
            s = super.mContext.getString(R$string.kg_failed_attempts_now_wiping, new Object[] { i });
        }
        this.showDialog(null, s);
    }
    
    private void startSpringAnimation(final float startVelocity) {
        final SpringAnimation mSpringAnimation = this.mSpringAnimation;
        mSpringAnimation.setStartVelocity(startVelocity);
        mSpringAnimation.animateToFinalPosition(0.0f);
    }
    
    private void updateBiometricRetry() {
        final KeyguardSecurityModel.SecurityMode securityMode = this.getSecurityMode();
        this.mSwipeUpToRetry = (this.mKeyguardStateController.isFaceAuthEnabled() && securityMode != KeyguardSecurityModel.SecurityMode.SimPin && securityMode != KeyguardSecurityModel.SecurityMode.SimPuk && securityMode != KeyguardSecurityModel.SecurityMode.None);
    }
    
    private void updateSecurityView(final View obj) {
        if (obj instanceof KeyguardSecurityView) {
            final KeyguardSecurityView keyguardSecurityView = (KeyguardSecurityView)obj;
            keyguardSecurityView.setKeyguardCallback(this.mCallback);
            keyguardSecurityView.setLockPatternUtils(this.mLockPatternUtils);
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append("View ");
            sb.append(obj);
            sb.append(" is not a KeyguardSecurityView");
            Log.w("KeyguardSecurityView", sb.toString());
        }
    }
    
    public KeyguardSecurityModel.SecurityMode getCurrentSecurityMode() {
        return this.mCurrentSecuritySelection;
    }
    
    public int getLayoutIdFor(final KeyguardSecurityModel.SecurityMode securityMode) {
        final int n = KeyguardSecurityContainer$3.$SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[securityMode.ordinal()];
        if (n == 1) {
            return R$layout.keyguard_pattern_view;
        }
        if (n == 2) {
            return R$layout.keyguard_pin_view;
        }
        if (n == 3) {
            return R$layout.keyguard_password_view;
        }
        if (n == 6) {
            return R$layout.keyguard_sim_pin_view;
        }
        if (n != 7) {
            return 0;
        }
        return R$layout.keyguard_sim_puk_view;
    }
    
    public KeyguardSecurityModel.SecurityMode getSecurityMode() {
        return this.mSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
    }
    
    public CharSequence getTitle() {
        return this.mSecurityViewFlipper.getTitle();
    }
    
    public boolean needsInput() {
        return this.mSecurityViewFlipper.needsInput();
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        int n;
        if (ViewRootImpl.sNewInsetsMode == 2) {
            n = Integer.max(windowInsets.getInsetsIgnoringVisibility(WindowInsets$Type.systemBars()).bottom, windowInsets.getInsets(WindowInsets$Type.ime()).bottom);
        }
        else {
            n = windowInsets.getSystemWindowInsetBottom();
        }
        this.setPadding(this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight(), n);
        return windowInsets.inset(0, 0, 0, n);
    }
    
    protected void onFinishInflate() {
        (this.mSecurityViewFlipper = (KeyguardSecurityViewFlipper)this.findViewById(R$id.view_flipper)).setLockPatternUtils(this.mLockPatternUtils);
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        return false;
                    }
                }
                else {
                    if (this.mIsDragging) {
                        return true;
                    }
                    if (!this.mSwipeUpToRetry) {
                        return false;
                    }
                    if (this.mCurrentSecurityView.disallowInterceptTouch(motionEvent)) {
                        return false;
                    }
                    final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    final float n = (float)this.mViewConfiguration.getScaledTouchSlop();
                    if (this.mCurrentSecurityView != null && pointerIndex != -1 && this.mStartTouchY - motionEvent.getY(pointerIndex) > n * 4.0f) {
                        return this.mIsDragging = true;
                    }
                    return false;
                }
            }
            this.mIsDragging = false;
        }
        else {
            final int actionIndex = motionEvent.getActionIndex();
            this.mStartTouchY = motionEvent.getY(actionIndex);
            this.mActivePointerId = motionEvent.getPointerId(actionIndex);
            this.mVelocityTracker.clear();
        }
        return false;
    }
    
    public void onPause() {
        final AlertDialog mAlertDialog = this.mAlertDialog;
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            this.mAlertDialog = null;
        }
        this.mSecondaryLockScreenController.hide();
        final KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection = this.mCurrentSecuritySelection;
        if (mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            this.getSecurityView(mCurrentSecuritySelection).onPause();
        }
    }
    
    public void onResume(final int n) {
        final KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection = this.mCurrentSecuritySelection;
        if (mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            this.getSecurityView(mCurrentSecuritySelection).onResume(n);
        }
        this.updateBiometricRetry();
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        int n = 0;
        Label_0168: {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    this.mVelocityTracker.addMovement(motionEvent);
                    final float y = motionEvent.getY(motionEvent.findPointerIndex(this.mActivePointerId));
                    final float mLastTouchY = this.mLastTouchY;
                    if (mLastTouchY != -1.0f) {
                        this.setTranslationY(this.getTranslationY() + (y - mLastTouchY) * 0.25f);
                    }
                    this.mLastTouchY = y;
                    break Label_0168;
                }
                if (actionMasked != 3) {
                    if (actionMasked != 6) {
                        break Label_0168;
                    }
                    final int actionIndex = motionEvent.getActionIndex();
                    if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                        if (actionIndex == 0) {
                            n = 1;
                        }
                        this.mLastTouchY = motionEvent.getY(n);
                        this.mActivePointerId = motionEvent.getPointerId(n);
                    }
                    break Label_0168;
                }
            }
            this.mActivePointerId = -1;
            this.mLastTouchY = -1.0f;
            this.mIsDragging = false;
            this.startSpringAnimation(this.mVelocityTracker.getYVelocity());
        }
        if (actionMasked == 1 && -this.getTranslationY() > TypedValue.applyDimension(1, 10.0f, this.getResources().getDisplayMetrics()) && !this.mUpdateMonitor.isFaceDetectionRunning()) {
            this.mUpdateMonitor.requestFaceAuth();
            this.mCallback.userActivity();
            this.showMessage(null, null);
        }
        return true;
    }
    
    public void reset() {
        this.mSecurityViewFlipper.reset();
    }
    
    public void setKeyguardCallback(final KeyguardSecurityCallback keyguardCallback) {
        this.mSecurityViewFlipper.setKeyguardCallback(keyguardCallback);
    }
    
    public void setLockPatternUtils(final LockPatternUtils lockPatternUtils) {
        this.mLockPatternUtils = lockPatternUtils;
        this.mSecurityModel.setLockPatternUtils(lockPatternUtils);
        this.mSecurityViewFlipper.setLockPatternUtils(this.mLockPatternUtils);
    }
    
    public void setSecurityCallback(final SecurityCallback mSecurityCallback) {
        this.mSecurityCallback = mSecurityCallback;
    }
    
    public boolean shouldDelayChildPressedState() {
        return true;
    }
    
    public void showMessage(final CharSequence charSequence, final ColorStateList list) {
        final KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection = this.mCurrentSecuritySelection;
        if (mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            this.getSecurityView(mCurrentSecuritySelection).showMessage(charSequence, list);
        }
    }
    
    boolean showNextSecurityScreenOrFinish(final boolean b, final int n) {
        final boolean userHasTrust = this.mUpdateMonitor.getUserHasTrust(n);
        int subtype = 2;
        final boolean b2 = true;
        boolean b3 = true;
        int n3 = 0;
        Label_0276: {
            Label_0025: {
                if (userHasTrust) {
                    subtype = 3;
                }
                else if (!this.mUpdateMonitor.getUserUnlockedWithBiometric(n)) {
                    final KeyguardSecurityModel.SecurityMode none = KeyguardSecurityModel.SecurityMode.None;
                    final KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection = this.mCurrentSecuritySelection;
                    if (none != mCurrentSecuritySelection) {
                        if (b) {
                            final int n2 = KeyguardSecurityContainer$3.$SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[mCurrentSecuritySelection.ordinal()];
                            if (n2 == 1 || n2 == 2 || n2 == 3) {
                                n3 = (subtype = 1);
                                b3 = b2;
                                break Label_0276;
                            }
                            if (n2 != 6 && n2 != 7) {
                                final StringBuilder sb = new StringBuilder();
                                sb.append("Bad security screen ");
                                sb.append(this.mCurrentSecuritySelection);
                                sb.append(", fail safe");
                                Log.v("KeyguardSecurityView", sb.toString());
                                this.showPrimarySecurityScreen(false);
                            }
                            else {
                                final KeyguardSecurityModel.SecurityMode securityMode = this.mSecurityModel.getSecurityMode(n);
                                if (securityMode == KeyguardSecurityModel.SecurityMode.None && this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
                                    subtype = 4;
                                    break Label_0025;
                                }
                                this.showSecurityScreen(securityMode);
                            }
                        }
                        subtype = -1;
                        n3 = ((b3 = false) ? 1 : 0);
                        break Label_0276;
                    }
                    final KeyguardSecurityModel.SecurityMode securityMode2 = this.mSecurityModel.getSecurityMode(n);
                    if (KeyguardSecurityModel.SecurityMode.None == securityMode2) {
                        subtype = 0;
                    }
                    else {
                        this.showSecurityScreen(securityMode2);
                        subtype = -1;
                        b3 = false;
                    }
                }
            }
            n3 = 0;
        }
        if (b3) {
            final Intent secondaryLockscreenRequirement = this.mUpdateMonitor.getSecondaryLockscreenRequirement(n);
            if (secondaryLockscreenRequirement != null) {
                this.mSecondaryLockScreenController.show(secondaryLockscreenRequirement);
                return false;
            }
        }
        if (subtype != -1) {
            this.mMetricsLogger.write(new LogMaker(197).setType(5).setSubtype(subtype));
        }
        if (b3) {
            this.mSecurityCallback.finish((boolean)(n3 != 0), n);
        }
        return b3;
    }
    
    void showPrimarySecurityScreen(final boolean b) {
        this.showSecurityScreen(DejankUtils.whitelistIpcs((Supplier<KeyguardSecurityModel.SecurityMode>)new _$$Lambda$KeyguardSecurityContainer$2pPkYsoLI01tKHny_UaXkNxV_qo(this)));
    }
    
    public void showPromptReason(final int i) {
        if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            if (i != 0) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Strong auth required, reason: ");
                sb.append(i);
                Log.i("KeyguardSecurityView", sb.toString());
            }
            this.getSecurityView(this.mCurrentSecuritySelection).showPromptReason(i);
        }
    }
    
    public void startAppearAnimation() {
        final KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection = this.mCurrentSecuritySelection;
        if (mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            this.getSecurityView(mCurrentSecuritySelection).startAppearAnimation();
        }
    }
    
    public boolean startDisappearAnimation(final Runnable runnable) {
        final KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection = this.mCurrentSecuritySelection;
        return mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None && this.getSecurityView(mCurrentSecuritySelection).startDisappearAnimation(runnable);
    }
    
    public interface SecurityCallback
    {
        boolean dismiss(final boolean p0, final int p1);
        
        void finish(final boolean p0, final int p1);
        
        void onCancelClicked();
        
        void onSecurityModeChanged(final KeyguardSecurityModel.SecurityMode p0, final boolean p1);
        
        void reset();
        
        void userActivity();
    }
}
