// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternChecker$OnCheckCallback;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternView$Cell;
import java.util.List;
import com.android.internal.widget.LockPatternView$DisplayMode;
import android.text.TextUtils;
import com.android.systemui.R$string;
import android.content.res.ColorStateList;
import android.view.View$OnClickListener;
import com.android.internal.widget.LockPatternView$OnPatternListener;
import com.android.systemui.R$id;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.systemui.R$plurals;
import android.os.SystemClock;
import com.android.systemui.R$dimen;
import android.view.animation.AnimationUtils;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import android.os.AsyncTask;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockPatternUtils;
import android.graphics.Rect;
import android.view.View;
import com.android.settingslib.animation.DisappearAnimationUtils;
import android.os.CountDownTimer;
import android.view.ViewGroup;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.internal.widget.LockPatternView$CellState;
import com.android.settingslib.animation.AppearAnimationCreator;
import android.widget.LinearLayout;

public class KeyguardPatternView extends LinearLayout implements KeyguardSecurityView, AppearAnimationCreator<LockPatternView$CellState>, EmergencyButtonCallback
{
    private final AppearAnimationUtils mAppearAnimationUtils;
    private KeyguardSecurityCallback mCallback;
    private Runnable mCancelPatternRunnable;
    private ViewGroup mContainer;
    private CountDownTimer mCountdownTimer;
    private final DisappearAnimationUtils mDisappearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
    private View mEcaView;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private long mLastPokeTime;
    private final Rect mLockPatternScreenBounds;
    private LockPatternUtils mLockPatternUtils;
    private LockPatternView mLockPatternView;
    private AsyncTask<?, ?, ?> mPendingLockCheck;
    @VisibleForTesting
    KeyguardMessageArea mSecurityMessageDisplay;
    private final Rect mTempRect;
    private final int[] mTmpPosition;
    
    public KeyguardPatternView(final Context context) {
        this(context, null);
    }
    
    public KeyguardPatternView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTmpPosition = new int[2];
        this.mTempRect = new Rect();
        this.mLockPatternScreenBounds = new Rect();
        this.mCountdownTimer = null;
        this.mLastPokeTime = -7000L;
        this.mCancelPatternRunnable = new Runnable() {
            @Override
            public void run() {
                KeyguardPatternView.this.mLockPatternView.clearPattern();
            }
        };
        this.mKeyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class);
        this.mAppearAnimationUtils = new AppearAnimationUtils(context, 220L, 1.5f, 2.0f, AnimationUtils.loadInterpolator(super.mContext, 17563662));
        this.mDisappearAnimationUtils = new DisappearAnimationUtils(context, 125L, 1.2f, 0.6f, AnimationUtils.loadInterpolator(super.mContext, 17563663));
        this.mDisappearAnimationUtilsLocked = new DisappearAnimationUtils(context, 187L, 1.2f, 0.6f, AnimationUtils.loadInterpolator(super.mContext, 17563663));
        this.getResources().getDimensionPixelSize(R$dimen.disappear_y_translation);
    }
    
    private void displayDefaultSecurityMessage() {
        final KeyguardMessageArea mSecurityMessageDisplay = this.mSecurityMessageDisplay;
        if (mSecurityMessageDisplay != null) {
            mSecurityMessageDisplay.setMessage("");
        }
    }
    
    private void enableClipping(final boolean clipChildren) {
        this.setClipChildren(clipChildren);
        this.mContainer.setClipToPadding(clipChildren);
        this.mContainer.setClipChildren(clipChildren);
    }
    
    private void handleAttemptLockout(final long n) {
        this.mLockPatternView.clearPattern();
        this.mLockPatternView.setEnabled(false);
        this.mCountdownTimer = new CountDownTimer((long)Math.ceil((n - SystemClock.elapsedRealtime()) / 1000.0) * 1000L, 1000L) {
            public void onFinish() {
                KeyguardPatternView.this.mLockPatternView.setEnabled(true);
                KeyguardPatternView.this.displayDefaultSecurityMessage();
            }
            
            public void onTick(final long n) {
                final int i = (int)Math.round(n / 1000.0);
                final KeyguardPatternView this$0 = KeyguardPatternView.this;
                this$0.mSecurityMessageDisplay.setMessage(this$0.mContext.getResources().getQuantityString(R$plurals.kg_too_many_failed_attempts_countdown, i, new Object[] { i }));
            }
        }.start();
    }
    
    public void createAnimation(final LockPatternView$CellState lockPatternView$CellState, final long n, final long n2, final float n3, final boolean b, final Interpolator interpolator, final Runnable runnable) {
        final LockPatternView mLockPatternView = this.mLockPatternView;
        float n4;
        if (b) {
            n4 = 1.0f;
        }
        else {
            n4 = 0.0f;
        }
        float n5;
        if (b) {
            n5 = n3;
        }
        else {
            n5 = 0.0f;
        }
        float n6;
        if (b) {
            n6 = 0.0f;
        }
        else {
            n6 = n3;
        }
        float n7;
        if (b) {
            n7 = 0.0f;
        }
        else {
            n7 = 1.0f;
        }
        mLockPatternView.startCellStateAnimation(lockPatternView$CellState, 1.0f, n4, n5, n6, n7, 1.0f, n, n2, interpolator, runnable);
        if (runnable != null) {
            this.mAppearAnimationUtils.createAnimation(this.mEcaView, n, n2, n3, b, interpolator, (Runnable)null);
        }
    }
    
    public boolean disallowInterceptTouch(final MotionEvent motionEvent) {
        return !this.mLockPatternView.isEmpty() || this.mLockPatternScreenBounds.contains((int)motionEvent.getRawX(), (int)motionEvent.getRawY());
    }
    
    public CharSequence getTitle() {
        return this.getContext().getString(17040357);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public boolean needsInput() {
        return false;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay((View)this);
    }
    
    public void onEmergencyButtonClickedWhenInCall() {
        this.mCallback.reset();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        LockPatternUtils mLockPatternUtils;
        if ((mLockPatternUtils = this.mLockPatternUtils) == null) {
            mLockPatternUtils = new LockPatternUtils(super.mContext);
        }
        this.mLockPatternUtils = mLockPatternUtils;
        (this.mLockPatternView = (LockPatternView)this.findViewById(R$id.lockPatternView)).setSaveEnabled(false);
        this.mLockPatternView.setOnPatternListener((LockPatternView$OnPatternListener)new UnlockPatternListener());
        this.mLockPatternView.setInStealthMode(this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()) ^ true);
        this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
        this.mEcaView = this.findViewById(R$id.keyguard_selector_fade_container);
        this.mContainer = (ViewGroup)this.findViewById(R$id.container);
        final EmergencyButton emergencyButton = (EmergencyButton)this.findViewById(R$id.emergency_call_button);
        if (emergencyButton != null) {
            emergencyButton.setCallback((EmergencyButton.EmergencyButtonCallback)this);
        }
        final View viewById = this.findViewById(R$id.cancel_button);
        if (viewById != null) {
            viewById.setOnClickListener((View$OnClickListener)new _$$Lambda$KeyguardPatternView$N_2kmt4uZ3ZvQBB4SmVDuZJ_Wqw(this));
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.mLockPatternView.getLocationOnScreen(this.mTmpPosition);
        final Rect mLockPatternScreenBounds = this.mLockPatternScreenBounds;
        final int[] mTmpPosition = this.mTmpPosition;
        mLockPatternScreenBounds.set(mTmpPosition[0] - 40, mTmpPosition[1] - 40, mTmpPosition[0] + this.mLockPatternView.getWidth() + 40, this.mTmpPosition[1] + this.mLockPatternView.getHeight() + 40);
    }
    
    public void onPause() {
        final CountDownTimer mCountdownTimer = this.mCountdownTimer;
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
            this.mCountdownTimer = null;
        }
        final AsyncTask<?, ?, ?> mPendingLockCheck = this.mPendingLockCheck;
        if (mPendingLockCheck != null) {
            mPendingLockCheck.cancel(false);
            this.mPendingLockCheck = null;
        }
        this.displayDefaultSecurityMessage();
    }
    
    public void onResume(final int n) {
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final boolean onTouchEvent = super.onTouchEvent(motionEvent);
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        final long mLastPokeTime = this.mLastPokeTime;
        if (onTouchEvent && elapsedRealtime - mLastPokeTime > 6900L) {
            this.mLastPokeTime = SystemClock.elapsedRealtime();
        }
        final Rect mTempRect = this.mTempRect;
        boolean b = false;
        mTempRect.set(0, 0, 0, 0);
        this.offsetRectIntoDescendantCoords((View)this.mLockPatternView, this.mTempRect);
        final Rect mTempRect2 = this.mTempRect;
        motionEvent.offsetLocation((float)mTempRect2.left, (float)mTempRect2.top);
        if (this.mLockPatternView.dispatchTouchEvent(motionEvent) || onTouchEvent) {
            b = true;
        }
        final Rect mTempRect3 = this.mTempRect;
        motionEvent.offsetLocation((float)(-mTempRect3.left), (float)(-mTempRect3.top));
        return b;
    }
    
    public void reset() {
        this.mLockPatternView.setInStealthMode(this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()) ^ true);
        this.mLockPatternView.enableInput();
        this.mLockPatternView.setEnabled(true);
        this.mLockPatternView.clearPattern();
        if (this.mSecurityMessageDisplay == null) {
            return;
        }
        final long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
        if (lockoutAttemptDeadline != 0L) {
            this.handleAttemptLockout(lockoutAttemptDeadline);
        }
        else {
            this.displayDefaultSecurityMessage();
        }
    }
    
    public void setKeyguardCallback(final KeyguardSecurityCallback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void setLockPatternUtils(final LockPatternUtils mLockPatternUtils) {
        this.mLockPatternUtils = mLockPatternUtils;
    }
    
    public void showMessage(final CharSequence message, final ColorStateList nextMessageColor) {
        if (nextMessageColor != null) {
            this.mSecurityMessageDisplay.setNextMessageColor(nextMessageColor);
        }
        this.mSecurityMessageDisplay.setMessage(message);
    }
    
    public void showPromptReason(final int n) {
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        if (n != 4) {
                            if (n != 6) {
                                this.mSecurityMessageDisplay.setMessage(R$string.kg_prompt_reason_timeout_pattern);
                            }
                            else {
                                this.mSecurityMessageDisplay.setMessage(R$string.kg_prompt_reason_prepare_for_update_pattern);
                            }
                        }
                        else {
                            this.mSecurityMessageDisplay.setMessage(R$string.kg_prompt_reason_user_request);
                        }
                    }
                    else {
                        this.mSecurityMessageDisplay.setMessage(R$string.kg_prompt_reason_device_admin);
                    }
                }
                else {
                    this.mSecurityMessageDisplay.setMessage(R$string.kg_prompt_reason_timeout_pattern);
                }
            }
            else {
                this.mSecurityMessageDisplay.setMessage(R$string.kg_prompt_reason_restart_pattern);
            }
        }
    }
    
    public void startAppearAnimation() {
        this.enableClipping(false);
        this.setAlpha(1.0f);
        this.setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
        AppearAnimationUtils.startTranslationYAnimation((View)this, 0L, 500L, 0.0f, this.mAppearAnimationUtils.getInterpolator());
        this.mAppearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new Runnable() {
            @Override
            public void run() {
                KeyguardPatternView.this.enableClipping(true);
            }
        }, this);
        if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
            final AppearAnimationUtils mAppearAnimationUtils = this.mAppearAnimationUtils;
            mAppearAnimationUtils.createAnimation((View)this.mSecurityMessageDisplay, 0L, 220L, mAppearAnimationUtils.getStartTranslation(), true, this.mAppearAnimationUtils.getInterpolator(), (Runnable)null);
        }
    }
    
    public boolean startDisappearAnimation(final Runnable runnable) {
        float n;
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
            n = 1.5f;
        }
        else {
            n = 1.0f;
        }
        this.mLockPatternView.clearPattern();
        this.enableClipping(false);
        this.setTranslationY(0.0f);
        AppearAnimationUtils.startTranslationYAnimation((View)this, 0L, (long)(300.0f * n), -this.mDisappearAnimationUtils.getStartTranslation(), this.mDisappearAnimationUtils.getInterpolator());
        DisappearAnimationUtils disappearAnimationUtils;
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
            disappearAnimationUtils = this.mDisappearAnimationUtilsLocked;
        }
        else {
            disappearAnimationUtils = this.mDisappearAnimationUtils;
        }
        disappearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new _$$Lambda$KeyguardPatternView$i51b4f44m8j5rvWUlLMM4eRNauI(this, runnable), this);
        if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
            final DisappearAnimationUtils mDisappearAnimationUtils = this.mDisappearAnimationUtils;
            mDisappearAnimationUtils.createAnimation((View)this.mSecurityMessageDisplay, 0L, (long)(n * 200.0f), -mDisappearAnimationUtils.getStartTranslation() * 3.0f, false, this.mDisappearAnimationUtils.getInterpolator(), (Runnable)null);
        }
        return true;
    }
    
    private class UnlockPatternListener implements LockPatternView$OnPatternListener
    {
        private void onPatternChecked(final int n, final boolean b, final int n2, final boolean b2) {
            final boolean b3 = KeyguardUpdateMonitor.getCurrentUser() == n;
            if (b) {
                KeyguardPatternView.this.mCallback.reportUnlockAttempt(n, true, 0);
                if (b3) {
                    KeyguardPatternView.this.mLockPatternView.setDisplayMode(LockPatternView$DisplayMode.Correct);
                    KeyguardPatternView.this.mCallback.dismiss(true, n);
                }
            }
            else {
                KeyguardPatternView.this.mLockPatternView.setDisplayMode(LockPatternView$DisplayMode.Wrong);
                if (b2) {
                    KeyguardPatternView.this.mCallback.reportUnlockAttempt(n, false, n2);
                    if (n2 > 0) {
                        KeyguardPatternView.this.handleAttemptLockout(KeyguardPatternView.this.mLockPatternUtils.setLockoutAttemptDeadline(n, n2));
                    }
                }
                if (n2 == 0) {
                    KeyguardPatternView.this.mSecurityMessageDisplay.setMessage(R$string.kg_wrong_pattern);
                    KeyguardPatternView.this.mLockPatternView.postDelayed(KeyguardPatternView.this.mCancelPatternRunnable, 2000L);
                }
            }
        }
        
        public void onPatternCellAdded(final List<LockPatternView$Cell> list) {
            KeyguardPatternView.this.mCallback.userActivity();
            KeyguardPatternView.this.mCallback.onUserInput();
        }
        
        public void onPatternCleared() {
        }
        
        public void onPatternDetected(final List<LockPatternView$Cell> list) {
            KeyguardPatternView.this.mKeyguardUpdateMonitor.setCredentialAttempted();
            KeyguardPatternView.this.mLockPatternView.disableInput();
            if (KeyguardPatternView.this.mPendingLockCheck != null) {
                KeyguardPatternView.this.mPendingLockCheck.cancel(false);
            }
            final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            if (list.size() < 4) {
                KeyguardPatternView.this.mLockPatternView.enableInput();
                this.onPatternChecked(currentUser, false, 0, false);
                return;
            }
            if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionStart(3);
                LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionStart(4);
            }
            final KeyguardPatternView this$0 = KeyguardPatternView.this;
            this$0.mPendingLockCheck = (AsyncTask<?, ?, ?>)LockPatternChecker.checkCredential(this$0.mLockPatternUtils, LockscreenCredential.createPattern((List)list), currentUser, (LockPatternChecker$OnCheckCallback)new LockPatternChecker$OnCheckCallback() {
                public void onCancelled() {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(4);
                    }
                }
                
                public void onChecked(final boolean b, final int n) {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(4);
                    }
                    KeyguardPatternView.this.mLockPatternView.enableInput();
                    KeyguardPatternView.this.mPendingLockCheck = null;
                    if (!b) {
                        UnlockPatternListener.this.onPatternChecked(currentUser, false, n, true);
                    }
                }
                
                public void onEarlyMatched() {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(3);
                    }
                    UnlockPatternListener.this.onPatternChecked(currentUser, true, 0, true);
                }
            });
            if (list.size() > 2) {
                KeyguardPatternView.this.mCallback.userActivity();
                KeyguardPatternView.this.mCallback.onUserInput();
            }
        }
        
        public void onPatternStart() {
            KeyguardPatternView.this.mLockPatternView.removeCallbacks(KeyguardPatternView.this.mCancelPatternRunnable);
            KeyguardPatternView.this.mSecurityMessageDisplay.setMessage("");
        }
    }
}
