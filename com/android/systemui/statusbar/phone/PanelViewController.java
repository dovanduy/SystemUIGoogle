// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.View$OnTouchListener;
import android.content.res.Configuration;
import com.android.systemui.DejankUtils;
import android.util.Log;
import com.android.systemui.R$dimen;
import android.view.ViewGroup;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.android.systemui.Interpolators;
import android.animation.TimeInterpolator;
import android.view.ViewConfiguration;
import android.os.SystemClock;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.MotionEvent;
import com.android.systemui.R$bool;
import android.view.View$OnLayoutChangeListener;
import android.view.View;
import android.view.View$OnAttachStateChangeListener;
import com.android.systemui.statusbar.VibratorHelper;
import android.view.VelocityTracker;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import android.content.res.Resources;
import android.animation.ObjectAnimator;
import com.android.internal.util.LatencyTracker;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.animation.ValueAnimator;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.plugins.FalsingManager;
import java.util.ArrayList;
import com.android.systemui.doze.DozeLog;
import android.view.animation.Interpolator;

public abstract class PanelViewController
{
    public static final String TAG;
    private boolean mAnimateAfterExpanding;
    private boolean mAnimatingOnDown;
    PanelBar mBar;
    private Interpolator mBounceInterpolator;
    private boolean mClosing;
    private boolean mCollapsedAndHeadsUpOnDown;
    protected long mDownTime;
    private final DozeLog mDozeLog;
    private boolean mExpandLatencyTracking;
    private float mExpandedFraction;
    protected float mExpandedHeight;
    protected boolean mExpanding;
    protected ArrayList<PanelExpansionListener> mExpansionListeners;
    private final FalsingManager mFalsingManager;
    private int mFixedDuration;
    private FlingAnimationUtils mFlingAnimationUtils;
    private FlingAnimationUtils mFlingAnimationUtilsClosing;
    private FlingAnimationUtils mFlingAnimationUtilsDismissing;
    private final Runnable mFlingCollapseRunnable;
    private boolean mGestureWaitForTouchSlop;
    private boolean mHasLayoutedSinceDown;
    protected HeadsUpManagerPhone mHeadsUpManager;
    private ValueAnimator mHeightAnimator;
    protected boolean mHintAnimationRunning;
    private float mHintDistance;
    private boolean mIgnoreXTouchSlop;
    private float mInitialOffsetOnTouch;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private boolean mInstantExpanding;
    private boolean mJustPeeked;
    protected KeyguardBottomAreaView mKeyguardBottomArea;
    protected final KeyguardStateController mKeyguardStateController;
    private final LatencyTracker mLatencyTracker;
    protected boolean mLaunchingNotification;
    private LockscreenGestureLogger mLockscreenGestureLogger;
    private float mMinExpandHeight;
    private boolean mMotionAborted;
    private float mNextCollapseSpeedUpFactor;
    private boolean mNotificationsDragEnabled;
    private boolean mOverExpandedBeforeFling;
    private boolean mPanelClosedOnDown;
    private boolean mPanelUpdateWhenAnimatorEnds;
    private ObjectAnimator mPeekAnimator;
    private float mPeekHeight;
    private boolean mPeekTouching;
    protected final Runnable mPostCollapseRunnable;
    protected final Resources mResources;
    private float mSlopMultiplier;
    protected StatusBar mStatusBar;
    protected final SysuiStatusBarStateController mStatusBarStateController;
    protected final StatusBarTouchableRegionManager mStatusBarTouchableRegionManager;
    private boolean mTouchAboveFalsingThreshold;
    private boolean mTouchDisabled;
    private int mTouchSlop;
    private boolean mTouchSlopExceeded;
    protected boolean mTouchSlopExceededBeforeDown;
    private boolean mTouchStartedInEmptyArea;
    protected boolean mTracking;
    private int mTrackingPointer;
    private int mUnlockFalsingThreshold;
    private boolean mUpdateFlingOnLayout;
    private float mUpdateFlingVelocity;
    private boolean mUpwardsWhenThresholdReached;
    private final VelocityTracker mVelocityTracker;
    private boolean mVibrateOnOpening;
    private final VibratorHelper mVibratorHelper;
    private final PanelView mView;
    private String mViewName;
    
    static {
        TAG = PanelView.class.getSimpleName();
    }
    
    public PanelViewController(final PanelView mView, final FalsingManager mFalsingManager, final DozeLog mDozeLog, final KeyguardStateController mKeyguardStateController, final SysuiStatusBarStateController mStatusBarStateController, final VibratorHelper mVibratorHelper, final LatencyTracker mLatencyTracker, final FlingAnimationUtils.Builder builder, final StatusBarTouchableRegionManager mStatusBarTouchableRegionManager) {
        this.mLockscreenGestureLogger = new LockscreenGestureLogger();
        this.mFixedDuration = -1;
        this.mExpansionListeners = new ArrayList<PanelExpansionListener>();
        this.mExpandedFraction = 0.0f;
        this.mExpandedHeight = 0.0f;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mNextCollapseSpeedUpFactor = 1.0f;
        this.mFlingCollapseRunnable = new Runnable() {
            @Override
            public void run() {
                final PanelViewController this$0 = PanelViewController.this;
                this$0.fling(0.0f, false, this$0.mNextCollapseSpeedUpFactor, false);
            }
        };
        this.mPostCollapseRunnable = new Runnable() {
            @Override
            public void run() {
                PanelViewController.this.collapse(false, 1.0f);
            }
        };
        (this.mView = mView).addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
                final PanelViewController this$0 = PanelViewController.this;
                this$0.mViewName = this$0.mResources.getResourceName(this$0.mView.getId());
            }
            
            public void onViewDetachedFromWindow(final View view) {
            }
        });
        this.mView.addOnLayoutChangeListener((View$OnLayoutChangeListener)this.createLayoutChangeListener());
        this.mView.setOnTouchListener(this.createTouchHandler());
        this.mView.setOnConfigurationChangedListener((PanelView.OnConfigurationChangedListener)this.createOnConfigurationChangedListener());
        this.mResources = this.mView.getResources();
        this.mKeyguardStateController = mKeyguardStateController;
        this.mStatusBarStateController = mStatusBarStateController;
        builder.reset();
        builder.setMaxLengthSeconds(0.6f);
        builder.setSpeedUpFactor(0.6f);
        this.mFlingAnimationUtils = builder.build();
        builder.reset();
        builder.setMaxLengthSeconds(0.5f);
        builder.setSpeedUpFactor(0.6f);
        this.mFlingAnimationUtilsClosing = builder.build();
        builder.reset();
        builder.setMaxLengthSeconds(0.5f);
        builder.setSpeedUpFactor(0.6f);
        builder.setX2(0.6f);
        builder.setY2(0.84f);
        this.mFlingAnimationUtilsDismissing = builder.build();
        this.mLatencyTracker = mLatencyTracker;
        this.mBounceInterpolator = (Interpolator)new BounceInterpolator();
        this.mFalsingManager = mFalsingManager;
        this.mDozeLog = mDozeLog;
        this.mNotificationsDragEnabled = this.mResources.getBoolean(R$bool.config_enableNotificationShadeDrag);
        this.mVibratorHelper = mVibratorHelper;
        this.mVibrateOnOpening = this.mResources.getBoolean(R$bool.config_vibrateOnIconAnimation);
        this.mStatusBarTouchableRegionManager = mStatusBarTouchableRegionManager;
    }
    
    private void abortAnimations() {
        this.cancelPeek();
        this.cancelHeightAnimator();
        this.mView.removeCallbacks(this.mPostCollapseRunnable);
        this.mView.removeCallbacks(this.mFlingCollapseRunnable);
    }
    
    private void addMovement(final MotionEvent motionEvent) {
        final float n = motionEvent.getRawX() - motionEvent.getX();
        final float n2 = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(n, n2);
        this.mVelocityTracker.addMovement(motionEvent);
        motionEvent.offsetLocation(-n, -n2);
    }
    
    private ValueAnimator createHeightAnimator(final float n) {
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mExpandedHeight, n });
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$PanelViewController$dSx0idVyG0MoiMqYY5GMAiz4jTg(this));
        return ofFloat;
    }
    
    private void endClosing() {
        if (this.mClosing) {
            this.mClosing = false;
            this.onClosingFinished();
        }
    }
    
    private void endMotionEvent(final MotionEvent motionEvent, final float n, final float n2, final boolean b) {
        this.mTrackingPointer = -1;
        final boolean mTracking = this.mTracking;
        final boolean b2 = true;
        if ((!mTracking || !this.mTouchSlopExceeded) && Math.abs(n - this.mInitialTouchX) <= this.mTouchSlop && Math.abs(n2 - this.mInitialTouchY) <= this.mTouchSlop && motionEvent.getActionMasked() != 3 && !b) {
            if (this.mPanelClosedOnDown && !this.mHeadsUpManager.hasPinnedHeadsUp() && !this.mTracking && !this.mStatusBar.isBouncerShowing() && !this.mKeyguardStateController.isKeyguardFadingAway()) {
                if (SystemClock.uptimeMillis() - this.mDownTime < ViewConfiguration.getLongPressTimeout()) {
                    this.runPeekAnimation(360L, this.getPeekHeight(), true);
                }
                else {
                    this.mView.postOnAnimation(this.mPostCollapseRunnable);
                }
            }
            else if (!this.mStatusBar.isBouncerShowing()) {
                this.onTrackingStopped(this.onEmptySpaceClick(this.mInitialTouchX));
            }
        }
        else {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            final float yVelocity = this.mVelocityTracker.getYVelocity();
            final boolean b3 = this.flingExpands(yVelocity, (float)Math.hypot(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity()), n, n2) || motionEvent.getActionMasked() == 3 || b;
            this.mDozeLog.traceFling(b3, this.mTouchAboveFalsingThreshold, this.mStatusBar.isFalsingThresholdNeeded(), this.mStatusBar.isWakeUpComingFromTouch());
            if (!b3 && this.mStatusBarStateController.getState() == 1) {
                final float displayDensity = this.mStatusBar.getDisplayDensity();
                this.mLockscreenGestureLogger.write(186, (int)Math.abs((n2 - this.mInitialTouchY) / displayDensity), (int)Math.abs(yVelocity / displayDensity));
            }
            this.fling(yVelocity, b3, this.isFalseTouch(n, n2));
            this.onTrackingStopped(b3);
            final boolean mUpdateFlingOnLayout = b3 && this.mPanelClosedOnDown && !this.mHasLayoutedSinceDown && b2;
            this.mUpdateFlingOnLayout = mUpdateFlingOnLayout;
            if (mUpdateFlingOnLayout) {
                this.mUpdateFlingVelocity = yVelocity;
            }
        }
        this.mVelocityTracker.clear();
        this.mPeekTouching = false;
    }
    
    private int getFalsingThreshold() {
        float n;
        if (this.mStatusBar.isWakeUpComingFromTouch()) {
            n = 1.5f;
        }
        else {
            n = 1.0f;
        }
        return (int)(this.mUnlockFalsingThreshold * n);
    }
    
    private boolean isDirectionUpwards(final float n, float a) {
        final float mInitialTouchX = this.mInitialTouchX;
        a -= this.mInitialTouchY;
        boolean b = false;
        if (a >= 0.0f) {
            return false;
        }
        if (Math.abs(a) >= Math.abs(n - mInitialTouchX)) {
            b = true;
        }
        return b;
    }
    
    private boolean isFalseTouch(final float n, final float n2) {
        if (!this.mStatusBar.isFalsingThresholdNeeded()) {
            return false;
        }
        if (this.mFalsingManager.isClassifierEnabled()) {
            return this.mFalsingManager.isFalseTouch();
        }
        return !this.mTouchAboveFalsingThreshold || (!this.mUpwardsWhenThresholdReached && (this.isDirectionUpwards(n, n2) ^ true));
    }
    
    private void notifyExpandingStarted() {
        if (!this.mExpanding) {
            this.mExpanding = true;
            this.onExpandingStarted();
        }
    }
    
    private void runPeekAnimation(final long duration, final float mPeekHeight, final boolean b) {
        this.mPeekHeight = mPeekHeight;
        if (this.mHeightAnimator != null) {
            return;
        }
        final ObjectAnimator mPeekAnimator = this.mPeekAnimator;
        if (mPeekAnimator != null) {
            mPeekAnimator.cancel();
        }
        (this.mPeekAnimator = ObjectAnimator.ofFloat((Object)this, "expandedHeight", new float[] { this.mPeekHeight }).setDuration(duration)).setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
        this.mPeekAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                PanelViewController.this.mPeekAnimator = null;
                if (!this.mCancelled && b) {
                    PanelViewController.this.mView.postOnAnimation(PanelViewController.this.mPostCollapseRunnable);
                }
            }
        });
        this.notifyExpandingStarted();
        this.mPeekAnimator.start();
        this.mJustPeeked = true;
    }
    
    private void setAnimator(final ValueAnimator mHeightAnimator) {
        this.mHeightAnimator = mHeightAnimator;
        if (mHeightAnimator == null && this.mPanelUpdateWhenAnimatorEnds) {
            this.mPanelUpdateWhenAnimatorEnds = false;
            this.requestPanelHeightUpdate();
        }
    }
    
    private void startOpening(final MotionEvent motionEvent) {
        this.runPeekAnimation(200L, this.getOpeningHeight(), false);
        this.notifyBarPanelExpansionChanged();
        this.maybeVibrateOnOpening();
        this.mLockscreenGestureLogger.writeAtFractionalPosition(1328, (int)(motionEvent.getX() / this.mStatusBar.getDisplayWidth() * 100.0f), (int)(motionEvent.getY() / this.mStatusBar.getDisplayHeight() * 100.0f), this.mStatusBar.getRotation());
    }
    
    private void startUnlockHintAnimationPhase1(final Runnable runnable) {
        final ValueAnimator heightAnimator = this.createHeightAnimator(Math.max(0.0f, this.getMaxPanelHeight() - this.mHintDistance));
        heightAnimator.setDuration(250L);
        heightAnimator.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        heightAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (this.mCancelled) {
                    PanelViewController.this.setAnimator(null);
                    runnable.run();
                }
                else {
                    PanelViewController.this.startUnlockHintAnimationPhase2(runnable);
                }
            }
        });
        heightAnimator.start();
        this.setAnimator(heightAnimator);
        final View indicationArea = this.mKeyguardBottomArea.getIndicationArea();
        int i = 0;
        final View ambientIndicationContainer = this.mStatusBar.getAmbientIndicationContainer();
        while (i < 2) {
            final View view = (new View[] { indicationArea, ambientIndicationContainer })[i];
            if (view != null) {
                view.animate().translationY(-this.mHintDistance).setDuration(250L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).withEndAction((Runnable)new _$$Lambda$PanelViewController$2WGoBUvxneCReDApmWjMb2yffws(this, view)).start();
            }
            ++i;
        }
    }
    
    private void startUnlockHintAnimationPhase2(final Runnable runnable) {
        final ValueAnimator heightAnimator = this.createHeightAnimator((float)this.getMaxPanelHeight());
        heightAnimator.setDuration(450L);
        heightAnimator.setInterpolator((TimeInterpolator)this.mBounceInterpolator);
        heightAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                PanelViewController.this.setAnimator(null);
                runnable.run();
                PanelViewController.this.notifyBarPanelExpansionChanged();
            }
        });
        heightAnimator.start();
        this.setAnimator(heightAnimator);
    }
    
    public void addExpansionListener(final PanelExpansionListener e) {
        this.mExpansionListeners.add(e);
    }
    
    public boolean canPanelBeCollapsed() {
        return !this.isFullyCollapsed() && !this.mTracking && !this.mClosing;
    }
    
    protected void cancelHeightAnimator() {
        final ValueAnimator mHeightAnimator = this.mHeightAnimator;
        if (mHeightAnimator != null) {
            if (mHeightAnimator.isRunning()) {
                this.mPanelUpdateWhenAnimatorEnds = false;
            }
            this.mHeightAnimator.cancel();
        }
        this.endClosing();
    }
    
    public void cancelPeek() {
        final ObjectAnimator mPeekAnimator = this.mPeekAnimator;
        boolean b;
        if (mPeekAnimator != null) {
            b = true;
            mPeekAnimator.cancel();
        }
        else {
            b = false;
        }
        if (b) {
            this.notifyBarPanelExpansionChanged();
        }
    }
    
    public void collapse(final boolean b, final float mNextCollapseSpeedUpFactor) {
        if (this.canPanelBeCollapsed()) {
            this.cancelHeightAnimator();
            this.notifyExpandingStarted();
            this.mClosing = true;
            if (b) {
                this.mNextCollapseSpeedUpFactor = mNextCollapseSpeedUpFactor;
                this.mView.postDelayed(this.mFlingCollapseRunnable, 120L);
            }
            else {
                this.fling(0.0f, false, mNextCollapseSpeedUpFactor, false);
            }
        }
    }
    
    public void collapseWithDuration(final int mFixedDuration) {
        this.mFixedDuration = mFixedDuration;
        this.collapse(false, 1.0f);
        this.mFixedDuration = -1;
    }
    
    public abstract OnLayoutChangeListener createLayoutChangeListener();
    
    protected abstract OnConfigurationChangedListener createOnConfigurationChangedListener();
    
    protected abstract TouchHandler createTouchHandler();
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final String simpleName = this.getClass().getSimpleName();
        final float expandedHeight = this.getExpandedHeight();
        final int maxPanelHeight = this.getMaxPanelHeight();
        final boolean mClosing = this.mClosing;
        String s = "T";
        String s2;
        if (mClosing) {
            s2 = "T";
        }
        else {
            s2 = "f";
        }
        String s3;
        if (this.mTracking) {
            s3 = "T";
        }
        else {
            s3 = "f";
        }
        String s4;
        if (this.mJustPeeked) {
            s4 = "T";
        }
        else {
            s4 = "f";
        }
        final ObjectAnimator mPeekAnimator = this.mPeekAnimator;
        String s5 = " (started)";
        String s6;
        if (mPeekAnimator != null && mPeekAnimator.isStarted()) {
            s6 = " (started)";
        }
        else {
            s6 = "";
        }
        final ValueAnimator mHeightAnimator = this.mHeightAnimator;
        if (mHeightAnimator == null || !mHeightAnimator.isStarted()) {
            s5 = "";
        }
        if (!this.mTouchDisabled) {
            s = "f";
        }
        printWriter.println(String.format("[PanelView(%s): expandedHeight=%f maxPanelHeight=%d closing=%s tracking=%s justPeeked=%s peekAnim=%s%s timeAnim=%s%s touchDisabled=%s]", simpleName, expandedHeight, maxPanelHeight, s2, s3, s4, mPeekAnimator, s6, mHeightAnimator, s5, s));
    }
    
    public void expand(final boolean mAnimateAfterExpanding) {
        if (!this.isFullyCollapsed() && !this.isCollapsing()) {
            return;
        }
        this.mInstantExpanding = true;
        this.mAnimateAfterExpanding = mAnimateAfterExpanding;
        this.mUpdateFlingOnLayout = false;
        this.abortAnimations();
        this.cancelPeek();
        if (this.mTracking) {
            this.onTrackingStopped(true);
        }
        if (this.mExpanding) {
            this.notifyExpandingFinished();
        }
        this.notifyBarPanelExpansionChanged();
        this.mView.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (!PanelViewController.this.mInstantExpanding) {
                    PanelViewController.this.mView.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                    return;
                }
                if (PanelViewController.this.mStatusBar.getNotificationShadeWindowView().isVisibleToUser()) {
                    PanelViewController.this.mView.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                    if (PanelViewController.this.mAnimateAfterExpanding) {
                        PanelViewController.this.notifyExpandingStarted();
                        PanelViewController.this.fling(0.0f, true);
                    }
                    else {
                        PanelViewController.this.setExpandedFraction(1.0f);
                    }
                    PanelViewController.this.mInstantExpanding = false;
                }
            }
        });
        this.mView.requestLayout();
    }
    
    protected void fling(final float n, final boolean b) {
        this.fling(n, b, 1.0f, false);
    }
    
    protected void fling(final float n, final boolean b, final float n2, final boolean b2) {
        this.cancelPeek();
        float n3;
        if (b) {
            n3 = (float)this.getMaxPanelHeight();
        }
        else {
            n3 = 0.0f;
        }
        if (!b) {
            this.mClosing = true;
        }
        this.flingToHeight(n, b, n3, n2, b2);
    }
    
    protected void fling(final float n, final boolean b, final boolean b2) {
        this.fling(n, b, 1.0f, b2);
    }
    
    protected boolean flingExpands(final float n, final float a, final float n2, final float n3) {
        final boolean unlockingDisabled = this.mFalsingManager.isUnlockingDisabled();
        boolean b = true;
        if (unlockingDisabled) {
            return true;
        }
        if (this.isFalseTouch(n2, n3)) {
            return true;
        }
        if (Math.abs(a) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
            return this.shouldExpandWhenNotFlinging();
        }
        if (n <= 0.0f) {
            b = false;
        }
        return b;
    }
    
    protected void flingToHeight(final float n, final boolean b, float n2, float n3, final boolean b2) {
        boolean mOverExpandedBeforeFling = true;
        final boolean b3 = b && this.fullyExpandedClearAllVisible() && this.mExpandedHeight < this.getMaxPanelHeight() - this.getClearAllHeight() && !this.isClearAllVisible();
        if (b3) {
            n2 = (float)(this.getMaxPanelHeight() - this.getClearAllHeight());
        }
        if (n2 != this.mExpandedHeight && (this.getOverExpansionAmount() <= 0.0f || !b)) {
            if (this.getOverExpansionAmount() <= 0.0f) {
                mOverExpandedBeforeFling = false;
            }
            this.mOverExpandedBeforeFling = mOverExpandedBeforeFling;
            final ValueAnimator heightAnimator = this.createHeightAnimator(n2);
            if (b) {
                n3 = n;
                if (b2) {
                    n3 = n;
                    if (n < 0.0f) {
                        n3 = 0.0f;
                    }
                }
                this.mFlingAnimationUtils.apply((Animator)heightAnimator, this.mExpandedHeight, n2, n3, (float)this.mView.getHeight());
                if (n3 == 0.0f) {
                    heightAnimator.setDuration(350L);
                }
            }
            else {
                if (this.shouldUseDismissingAnimation()) {
                    if (n == 0.0f) {
                        heightAnimator.setInterpolator((TimeInterpolator)Interpolators.PANEL_CLOSE_ACCELERATED);
                        heightAnimator.setDuration((long)(this.mExpandedHeight / this.mView.getHeight() * 100.0f + 200.0f));
                    }
                    else {
                        this.mFlingAnimationUtilsDismissing.apply((Animator)heightAnimator, this.mExpandedHeight, n2, n, (float)this.mView.getHeight());
                    }
                }
                else {
                    this.mFlingAnimationUtilsClosing.apply((Animator)heightAnimator, this.mExpandedHeight, n2, n, (float)this.mView.getHeight());
                }
                if (n == 0.0f) {
                    heightAnimator.setDuration((long)(heightAnimator.getDuration() / n3));
                }
                final int mFixedDuration = this.mFixedDuration;
                if (mFixedDuration != -1) {
                    heightAnimator.setDuration((long)mFixedDuration);
                }
            }
            heightAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                private boolean mCancelled;
                
                public void onAnimationCancel(final Animator animator) {
                    this.mCancelled = true;
                }
                
                public void onAnimationEnd(final Animator animator) {
                    if (b3 && !this.mCancelled) {
                        final PanelViewController this$0 = PanelViewController.this;
                        this$0.setExpandedHeightInternal((float)this$0.getMaxPanelHeight());
                    }
                    PanelViewController.this.setAnimator(null);
                    if (!this.mCancelled) {
                        PanelViewController.this.notifyExpandingFinished();
                    }
                    PanelViewController.this.notifyBarPanelExpansionChanged();
                }
            });
            this.setAnimator(heightAnimator);
            heightAnimator.start();
            return;
        }
        this.notifyExpandingFinished();
    }
    
    protected abstract boolean fullyExpandedClearAllVisible();
    
    protected abstract int getClearAllHeight();
    
    protected float getCurrentExpandVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        return this.mVelocityTracker.getYVelocity();
    }
    
    public float getExpandedFraction() {
        return this.mExpandedFraction;
    }
    
    public float getExpandedHeight() {
        return this.mExpandedHeight;
    }
    
    protected abstract int getMaxPanelHeight();
    
    protected abstract float getOpeningHeight();
    
    protected abstract float getOverExpansionAmount();
    
    protected abstract float getOverExpansionPixels();
    
    protected abstract float getPeekHeight();
    
    protected float getTouchSlop(final MotionEvent motionEvent) {
        float n;
        if (motionEvent.getClassification() == 1) {
            n = this.mTouchSlop * this.mSlopMultiplier;
        }
        else {
            n = (float)this.mTouchSlop;
        }
        return n;
    }
    
    public ViewGroup getView() {
        return (ViewGroup)this.mView;
    }
    
    public void instantCollapse() {
        this.abortAnimations();
        this.setExpandedFraction(0.0f);
        if (this.mExpanding) {
            this.notifyExpandingFinished();
        }
        if (this.mInstantExpanding) {
            this.mInstantExpanding = false;
            this.notifyBarPanelExpansionChanged();
        }
    }
    
    protected abstract boolean isClearAllVisible();
    
    public boolean isCollapsing() {
        return this.mClosing || this.mLaunchingNotification;
    }
    
    public boolean isEnabled() {
        return this.mView.isEnabled();
    }
    
    public boolean isFullyCollapsed() {
        return this.mExpandedFraction <= 0.0f;
    }
    
    public boolean isFullyExpanded() {
        return this.mExpandedHeight >= this.getMaxPanelHeight();
    }
    
    protected abstract boolean isInContentBounds(final float p0, final float p1);
    
    protected abstract boolean isPanelVisibleBecauseOfHeadsUp();
    
    protected abstract boolean isScrolledToBottom();
    
    public boolean isTracking() {
        return this.mTracking;
    }
    
    protected abstract boolean isTrackingBlocked();
    
    public boolean isUnlockHintRunning() {
        return this.mHintAnimationRunning;
    }
    
    protected void loadDimens() {
        final ViewConfiguration value = ViewConfiguration.get(this.mView.getContext());
        this.mTouchSlop = value.getScaledTouchSlop();
        this.mSlopMultiplier = value.getScaledAmbiguousGestureMultiplier();
        this.mHintDistance = this.mResources.getDimension(R$dimen.hint_move_distance);
        this.mUnlockFalsingThreshold = this.mResources.getDimensionPixelSize(R$dimen.unlock_falsing_threshold);
    }
    
    protected void maybeVibrateOnOpening() {
        if (this.mVibrateOnOpening) {
            this.mVibratorHelper.vibrate(2);
        }
    }
    
    protected void notifyBarPanelExpansionChanged() {
        final PanelBar mBar = this.mBar;
        int i;
        final int n = i = 0;
        if (mBar != null) {
            final float mExpandedFraction = this.mExpandedFraction;
            mBar.panelExpansionChanged(mExpandedFraction, mExpandedFraction > 0.0f || this.mPeekAnimator != null || this.mInstantExpanding || this.isPanelVisibleBecauseOfHeadsUp() || this.mTracking || this.mHeightAnimator != null);
            i = n;
        }
        while (i < this.mExpansionListeners.size()) {
            this.mExpansionListeners.get(i).onPanelExpansionChanged(this.mExpandedFraction, this.mTracking);
            ++i;
        }
    }
    
    protected final void notifyExpandingFinished() {
        this.endClosing();
        if (this.mExpanding) {
            this.mExpanding = false;
            this.onExpandingFinished();
        }
    }
    
    protected void onClosingFinished() {
        this.mBar.onClosingFinished();
    }
    
    protected boolean onEmptySpaceClick(final float n) {
        return this.mHintAnimationRunning || this.onMiddleClicked();
    }
    
    protected void onExpandingFinished() {
        this.mBar.onExpandingFinished();
    }
    
    protected void onExpandingStarted() {
    }
    
    protected abstract void onHeightUpdated(final float p0);
    
    protected abstract boolean onMiddleClicked();
    
    protected void onTrackingStarted() {
        this.endClosing();
        this.mTracking = true;
        this.mBar.onTrackingStarted();
        this.notifyExpandingStarted();
        this.notifyBarPanelExpansionChanged();
    }
    
    protected void onTrackingStopped(final boolean b) {
        this.mTracking = false;
        this.mBar.onTrackingStopped(b);
        this.notifyBarPanelExpansionChanged();
    }
    
    protected void onUnlockHintFinished() {
        this.mStatusBar.onHintFinished();
    }
    
    protected void onUnlockHintStarted() {
        this.mStatusBar.onUnlockHintStarted();
    }
    
    protected void requestPanelHeightUpdate() {
        final float expandedHeight = (float)this.getMaxPanelHeight();
        if (this.isFullyCollapsed()) {
            return;
        }
        if (expandedHeight == this.mExpandedHeight) {
            return;
        }
        if (this.mPeekAnimator == null) {
            if (!this.mPeekTouching) {
                if (this.mTracking && !this.isTrackingBlocked()) {
                    return;
                }
                if (this.mHeightAnimator != null) {
                    this.mPanelUpdateWhenAnimatorEnds = true;
                    return;
                }
                this.setExpandedHeight(expandedHeight);
            }
        }
    }
    
    public abstract void resetViews(final boolean p0);
    
    public void setBar(final PanelBar mBar) {
        this.mBar = mBar;
    }
    
    public void setExpandedFraction(final float n) {
        this.setExpandedHeight(this.getMaxPanelHeight() * n);
    }
    
    public void setExpandedHeight(final float n) {
        this.setExpandedHeightInternal(n + this.getOverExpansionPixels());
    }
    
    public void setExpandedHeightInternal(float mExpandedHeight) {
        if (Float.isNaN(mExpandedHeight)) {
            Log.wtf(PanelViewController.TAG, "ExpandedHeight set to NaN");
        }
        final boolean mExpandLatencyTracking = this.mExpandLatencyTracking;
        final float n = 0.0f;
        if (mExpandLatencyTracking && mExpandedHeight != 0.0f) {
            DejankUtils.postAfterTraversal(new _$$Lambda$PanelViewController$3_TJ0A2OT3Q4yelawe6rfaI8nnw(this));
            this.mExpandLatencyTracking = false;
        }
        final float b = this.getMaxPanelHeight() - this.getOverExpansionAmount();
        if (this.mHeightAnimator == null) {
            final float max = Math.max(0.0f, mExpandedHeight - b);
            if (this.getOverExpansionPixels() != max && this.mTracking) {
                this.setOverExpansion(max, true);
            }
            this.mExpandedHeight = Math.min(mExpandedHeight, b) + this.getOverExpansionAmount();
        }
        else {
            this.mExpandedHeight = mExpandedHeight;
            if (this.mOverExpandedBeforeFling) {
                this.setOverExpansion(Math.max(0.0f, mExpandedHeight - b), false);
            }
        }
        mExpandedHeight = this.mExpandedHeight;
        if (mExpandedHeight < 1.0f && mExpandedHeight != 0.0f && this.mClosing) {
            this.mExpandedHeight = 0.0f;
            final ValueAnimator mHeightAnimator = this.mHeightAnimator;
            if (mHeightAnimator != null) {
                mHeightAnimator.end();
            }
        }
        if (b == 0.0f) {
            mExpandedHeight = n;
        }
        else {
            mExpandedHeight = this.mExpandedHeight / b;
        }
        this.mExpandedFraction = Math.min(1.0f, mExpandedHeight);
        this.onHeightUpdated(this.mExpandedHeight);
        this.notifyBarPanelExpansionChanged();
    }
    
    public void setHeadsUpManager(final HeadsUpManagerPhone mHeadsUpManager) {
        this.mHeadsUpManager = mHeadsUpManager;
    }
    
    public void setLaunchingNotification(final boolean mLaunchingNotification) {
        this.mLaunchingNotification = mLaunchingNotification;
    }
    
    protected abstract void setOverExpansion(final float p0, final boolean p1);
    
    public void setTouchAndAnimationDisabled(final boolean mTouchDisabled) {
        this.mTouchDisabled = mTouchDisabled;
        if (mTouchDisabled) {
            this.cancelHeightAnimator();
            if (this.mTracking) {
                this.onTrackingStopped(true);
            }
            this.notifyExpandingFinished();
        }
    }
    
    protected boolean shouldExpandWhenNotFlinging() {
        return this.getExpandedFraction() > 0.5f;
    }
    
    protected abstract boolean shouldGestureIgnoreXTouchSlop(final float p0, final float p1);
    
    protected abstract boolean shouldGestureWaitForTouchSlop();
    
    protected abstract boolean shouldUseDismissingAnimation();
    
    public void startExpandLatencyTracking() {
        if (this.mLatencyTracker.isEnabled()) {
            this.mLatencyTracker.onActionStart(0);
            this.mExpandLatencyTracking = true;
        }
    }
    
    protected void startExpandMotion(final float mInitialTouchX, final float mInitialTouchY, final boolean b, final float n) {
        this.mInitialOffsetOnTouch = n;
        this.mInitialTouchY = mInitialTouchY;
        this.mInitialTouchX = mInitialTouchX;
        if (b) {
            this.mTouchSlopExceeded = true;
            this.setExpandedHeight(n);
            this.onTrackingStarted();
        }
    }
    
    protected void startExpandingFromPeek() {
        this.mStatusBar.handlePeekToExpandTransistion();
    }
    
    protected void startUnlockHintAnimation() {
        if (this.mHeightAnimator == null) {
            if (!this.mTracking) {
                this.cancelPeek();
                this.notifyExpandingStarted();
                this.startUnlockHintAnimationPhase1(new _$$Lambda$PanelViewController$GuYBMkURoVUrgoMW3L5UanjAhbw(this));
                this.onUnlockHintStarted();
                this.mHintAnimationRunning = true;
            }
        }
    }
    
    public class OnConfigurationChangedListener implements PanelView.OnConfigurationChangedListener
    {
        @Override
        public void onConfigurationChanged(final Configuration configuration) {
            PanelViewController.this.loadDimens();
        }
    }
    
    public class OnLayoutChangeListener implements View$OnLayoutChangeListener
    {
        public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
            PanelViewController.this.mStatusBar.onPanelLaidOut();
            PanelViewController.this.requestPanelHeightUpdate();
            PanelViewController.this.mHasLayoutedSinceDown = true;
            if (PanelViewController.this.mUpdateFlingOnLayout) {
                PanelViewController.this.abortAnimations();
                final PanelViewController this$0 = PanelViewController.this;
                this$0.fling(this$0.mUpdateFlingVelocity, true);
                PanelViewController.this.mUpdateFlingOnLayout = false;
            }
        }
    }
    
    public class TouchHandler implements View$OnTouchListener
    {
        public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
            if (!PanelViewController.this.mInstantExpanding && PanelViewController.this.mNotificationsDragEnabled && !PanelViewController.this.mTouchDisabled) {
                if (!PanelViewController.this.mMotionAborted || motionEvent.getActionMasked() == 0) {
                    int pointerIndex;
                    if ((pointerIndex = motionEvent.findPointerIndex(PanelViewController.this.mTrackingPointer)) < 0) {
                        PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(0);
                        pointerIndex = 0;
                    }
                    final float x = motionEvent.getX(pointerIndex);
                    final float y = motionEvent.getY(pointerIndex);
                    final boolean scrolledToBottom = PanelViewController.this.isScrolledToBottom();
                    final int actionMasked = motionEvent.getActionMasked();
                    int n = 1;
                    if (actionMasked != 0) {
                        if (actionMasked != 1) {
                            if (actionMasked != 2) {
                                if (actionMasked != 3) {
                                    if (actionMasked != 5) {
                                        if (actionMasked != 6) {
                                            return false;
                                        }
                                        final int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                                        if (PanelViewController.this.mTrackingPointer == pointerId) {
                                            if (motionEvent.getPointerId(0) != pointerId) {
                                                n = 0;
                                            }
                                            PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(n);
                                            PanelViewController.this.mInitialTouchX = motionEvent.getX(n);
                                            PanelViewController.this.mInitialTouchY = motionEvent.getY(n);
                                            return false;
                                        }
                                        return false;
                                    }
                                    else {
                                        if (PanelViewController.this.mStatusBarStateController.getState() == 1) {
                                            PanelViewController.this.mMotionAborted = true;
                                            PanelViewController.this.mVelocityTracker.clear();
                                            return false;
                                        }
                                        return false;
                                    }
                                }
                            }
                            else {
                                final float a = y - PanelViewController.this.mInitialTouchY;
                                PanelViewController.this.addMovement(motionEvent);
                                if (!scrolledToBottom && !PanelViewController.this.mTouchStartedInEmptyArea && !PanelViewController.this.mAnimatingOnDown) {
                                    return false;
                                }
                                final float abs = Math.abs(a);
                                final float touchSlop = PanelViewController.this.getTouchSlop(motionEvent);
                                if ((a < -touchSlop || (PanelViewController.this.mAnimatingOnDown && abs > touchSlop)) && abs > Math.abs(x - PanelViewController.this.mInitialTouchX)) {
                                    PanelViewController.this.cancelHeightAnimator();
                                    final PanelViewController this$0 = PanelViewController.this;
                                    this$0.startExpandMotion(x, y, true, this$0.mExpandedHeight);
                                    return true;
                                }
                                return false;
                            }
                        }
                        PanelViewController.this.mVelocityTracker.clear();
                    }
                    else {
                        PanelViewController.this.mStatusBar.userActivity();
                        final PanelViewController this$2 = PanelViewController.this;
                        this$2.mAnimatingOnDown = (this$2.mHeightAnimator != null);
                        PanelViewController.this.mMinExpandHeight = 0.0f;
                        PanelViewController.this.mDownTime = SystemClock.uptimeMillis();
                        if ((PanelViewController.this.mAnimatingOnDown && PanelViewController.this.mClosing && !PanelViewController.this.mHintAnimationRunning) || PanelViewController.this.mPeekAnimator != null) {
                            PanelViewController.this.cancelHeightAnimator();
                            PanelViewController.this.cancelPeek();
                            PanelViewController.this.mTouchSlopExceeded = true;
                            return true;
                        }
                        PanelViewController.this.mInitialTouchY = y;
                        PanelViewController.this.mInitialTouchX = x;
                        final PanelViewController this$3 = PanelViewController.this;
                        this$3.mTouchStartedInEmptyArea = (this$3.isInContentBounds(x, y) ^ true);
                        final PanelViewController this$4 = PanelViewController.this;
                        this$4.mTouchSlopExceeded = this$4.mTouchSlopExceededBeforeDown;
                        PanelViewController.this.mJustPeeked = false;
                        PanelViewController.this.mMotionAborted = false;
                        final PanelViewController this$5 = PanelViewController.this;
                        this$5.mPanelClosedOnDown = this$5.isFullyCollapsed();
                        PanelViewController.this.mCollapsedAndHeadsUpOnDown = false;
                        PanelViewController.this.mHasLayoutedSinceDown = false;
                        PanelViewController.this.mUpdateFlingOnLayout = false;
                        PanelViewController.this.mTouchAboveFalsingThreshold = false;
                        PanelViewController.this.addMovement(motionEvent);
                    }
                }
            }
            return false;
        }
        
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            final boolean access$500 = PanelViewController.this.mInstantExpanding;
            boolean b2;
            final boolean b = b2 = false;
            if (!access$500) {
                if (PanelViewController.this.mTouchDisabled) {
                    b2 = b;
                    if (motionEvent.getActionMasked() != 3) {
                        return b2;
                    }
                }
                if (PanelViewController.this.mMotionAborted && motionEvent.getActionMasked() != 0) {
                    b2 = b;
                }
                else {
                    if (!PanelViewController.this.mNotificationsDragEnabled) {
                        final PanelViewController this$0 = PanelViewController.this;
                        if (this$0.mTracking) {
                            this$0.onTrackingStopped(true);
                        }
                        return false;
                    }
                    if (PanelViewController.this.isFullyCollapsed() && motionEvent.isFromSource(8194)) {
                        if (motionEvent.getAction() == 1) {
                            PanelViewController.this.expand(true);
                        }
                        return true;
                    }
                    int pointerIndex;
                    if ((pointerIndex = motionEvent.findPointerIndex(PanelViewController.this.mTrackingPointer)) < 0) {
                        PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(0);
                        pointerIndex = 0;
                    }
                    final float x = motionEvent.getX(pointerIndex);
                    final float y = motionEvent.getY(pointerIndex);
                    if (motionEvent.getActionMasked() == 0) {
                        final PanelViewController this$2 = PanelViewController.this;
                        this$2.mGestureWaitForTouchSlop = this$2.shouldGestureWaitForTouchSlop();
                        final PanelViewController this$3 = PanelViewController.this;
                        this$3.mIgnoreXTouchSlop = (this$3.isFullyCollapsed() || PanelViewController.this.shouldGestureIgnoreXTouchSlop(x, y));
                    }
                    final int actionMasked = motionEvent.getActionMasked();
                    Label_1206: {
                        if (actionMasked != 0) {
                            if (actionMasked != 1) {
                                if (actionMasked != 2) {
                                    if (actionMasked != 3) {
                                        if (actionMasked != 5) {
                                            if (actionMasked != 6) {
                                                break Label_1206;
                                            }
                                            final int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                                            if (PanelViewController.this.mTrackingPointer == pointerId) {
                                                int n;
                                                if (motionEvent.getPointerId(0) != pointerId) {
                                                    n = 0;
                                                }
                                                else {
                                                    n = 1;
                                                }
                                                final float y2 = motionEvent.getY(n);
                                                final float x2 = motionEvent.getX(n);
                                                PanelViewController.this.mTrackingPointer = motionEvent.getPointerId(n);
                                                final PanelViewController this$4 = PanelViewController.this;
                                                this$4.startExpandMotion(x2, y2, true, this$4.mExpandedHeight);
                                            }
                                            break Label_1206;
                                        }
                                        else {
                                            if (PanelViewController.this.mStatusBarStateController.getState() == 1) {
                                                PanelViewController.this.mMotionAborted = true;
                                                PanelViewController.this.endMotionEvent(motionEvent, x, y, true);
                                                return false;
                                            }
                                            break Label_1206;
                                        }
                                    }
                                }
                                else {
                                    PanelViewController.this.addMovement(motionEvent);
                                    float n3;
                                    final float n2 = n3 = y - PanelViewController.this.mInitialTouchY;
                                    Label_0621: {
                                        if (Math.abs(n2) > PanelViewController.this.getTouchSlop(motionEvent)) {
                                            if (Math.abs(n2) <= Math.abs(x - PanelViewController.this.mInitialTouchX)) {
                                                n3 = n2;
                                                if (!PanelViewController.this.mIgnoreXTouchSlop) {
                                                    break Label_0621;
                                                }
                                            }
                                            PanelViewController.this.mTouchSlopExceeded = true;
                                            n3 = n2;
                                            if (PanelViewController.this.mGestureWaitForTouchSlop) {
                                                final PanelViewController this$5 = PanelViewController.this;
                                                n3 = n2;
                                                if (!this$5.mTracking) {
                                                    n3 = n2;
                                                    if (!this$5.mCollapsedAndHeadsUpOnDown) {
                                                        n3 = n2;
                                                        if (!PanelViewController.this.mJustPeeked) {
                                                            n3 = n2;
                                                            if (PanelViewController.this.mInitialOffsetOnTouch != 0.0f) {
                                                                final PanelViewController this$6 = PanelViewController.this;
                                                                this$6.startExpandMotion(x, y, false, this$6.mExpandedHeight);
                                                                n3 = 0.0f;
                                                            }
                                                        }
                                                        PanelViewController.this.cancelHeightAnimator();
                                                        PanelViewController.this.onTrackingStarted();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    final float max = Math.max(0.0f, PanelViewController.this.mInitialOffsetOnTouch + n3);
                                    if (max > PanelViewController.this.mPeekHeight) {
                                        if (PanelViewController.this.mPeekAnimator != null) {
                                            PanelViewController.this.mPeekAnimator.cancel();
                                        }
                                        PanelViewController.this.mJustPeeked = false;
                                    }
                                    else if (PanelViewController.this.mPeekAnimator == null && PanelViewController.this.mJustPeeked) {
                                        final PanelViewController this$7 = PanelViewController.this;
                                        this$7.mInitialOffsetOnTouch = this$7.mExpandedHeight;
                                        PanelViewController.this.mInitialTouchY = y;
                                        final PanelViewController this$8 = PanelViewController.this;
                                        this$8.mMinExpandHeight = this$8.mExpandedHeight;
                                        PanelViewController.this.mJustPeeked = false;
                                    }
                                    final float max2 = Math.max(max, PanelViewController.this.mMinExpandHeight);
                                    if (-n3 >= PanelViewController.this.getFalsingThreshold()) {
                                        PanelViewController.this.mTouchAboveFalsingThreshold = true;
                                        final PanelViewController this$9 = PanelViewController.this;
                                        this$9.mUpwardsWhenThresholdReached = this$9.isDirectionUpwards(x, y);
                                    }
                                    if (!PanelViewController.this.mJustPeeked && (!PanelViewController.this.mGestureWaitForTouchSlop || PanelViewController.this.mTracking) && !PanelViewController.this.isTrackingBlocked()) {
                                        PanelViewController.this.setExpandedHeightInternal(max2);
                                    }
                                    break Label_1206;
                                }
                            }
                            PanelViewController.this.addMovement(motionEvent);
                            PanelViewController.this.endMotionEvent(motionEvent, x, y, false);
                        }
                        else {
                            final PanelViewController this$10 = PanelViewController.this;
                            this$10.startExpandMotion(x, y, false, this$10.mExpandedHeight);
                            PanelViewController.this.mJustPeeked = false;
                            PanelViewController.this.mMinExpandHeight = 0.0f;
                            final PanelViewController this$11 = PanelViewController.this;
                            this$11.mPanelClosedOnDown = this$11.isFullyCollapsed();
                            PanelViewController.this.mHasLayoutedSinceDown = false;
                            PanelViewController.this.mUpdateFlingOnLayout = false;
                            PanelViewController.this.mMotionAborted = false;
                            final PanelViewController this$12 = PanelViewController.this;
                            this$12.mPeekTouching = this$12.mPanelClosedOnDown;
                            PanelViewController.this.mDownTime = SystemClock.uptimeMillis();
                            PanelViewController.this.mTouchAboveFalsingThreshold = false;
                            final PanelViewController this$13 = PanelViewController.this;
                            this$13.mCollapsedAndHeadsUpOnDown = (this$13.isFullyCollapsed() && PanelViewController.this.mHeadsUpManager.hasPinnedHeadsUp());
                            PanelViewController.this.addMovement(motionEvent);
                            if (!PanelViewController.this.mGestureWaitForTouchSlop || (PanelViewController.this.mHeightAnimator != null && !PanelViewController.this.mHintAnimationRunning) || PanelViewController.this.mPeekAnimator != null) {
                                final PanelViewController this$14 = PanelViewController.this;
                                this$14.mTouchSlopExceeded = ((this$14.mHeightAnimator != null && !PanelViewController.this.mHintAnimationRunning) || PanelViewController.this.mPeekAnimator != null || PanelViewController.this.mTouchSlopExceededBeforeDown);
                                PanelViewController.this.cancelHeightAnimator();
                                PanelViewController.this.cancelPeek();
                                PanelViewController.this.onTrackingStarted();
                            }
                            if (PanelViewController.this.isFullyCollapsed() && !PanelViewController.this.mHeadsUpManager.hasPinnedHeadsUp() && !PanelViewController.this.mStatusBar.isBouncerShowing()) {
                                PanelViewController.this.startOpening(motionEvent);
                            }
                        }
                    }
                    if (PanelViewController.this.mGestureWaitForTouchSlop) {
                        b2 = b;
                        if (!PanelViewController.this.mTracking) {
                            return b2;
                        }
                    }
                    b2 = true;
                }
            }
            return b2;
        }
    }
}
