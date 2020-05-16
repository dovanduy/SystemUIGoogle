// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import com.android.systemui.shared.system.WindowManagerWrapper;
import android.view.ViewPropertyAnimator;
import android.view.PointerIcon;
import android.view.ViewConfiguration;
import com.android.systemui.R$bool;
import com.android.systemui.R$integer;
import com.android.systemui.R$dimen;
import android.content.res.Configuration;
import android.graphics.Region$Op;
import android.view.ViewTreeObserver$InternalInsetsInfo;
import android.util.SparseIntArray;
import android.view.InsetsState;
import android.view.ViewRootImpl;
import android.view.WindowInsets;
import com.android.internal.logging.MetricsLogger;
import android.view.SurfaceControl;
import android.animation.Animator$AnimatorListener;
import java.util.function.Consumer;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import android.animation.Animator;
import android.view.MotionEvent;
import android.view.SurfaceControl$Transaction;
import com.android.internal.policy.DockedDividerUtils;
import android.animation.AnimationHandler$AnimationFrameCallbackProvider;
import com.android.internal.graphics.SfVsyncFrameCallbackProvider;
import android.hardware.display.DisplayManager;
import com.android.systemui.Interpolators;
import android.os.Bundle;
import com.android.internal.policy.DividerSnapAlgorithm;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import com.android.systemui.R$string;
import com.android.systemui.R$id;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.AttributeSet;
import android.content.Context;
import android.view.VelocityTracker;
import com.android.internal.policy.DividerSnapAlgorithm$SnapTarget;
import android.os.Handler;
import android.view.View$AccessibilityDelegate;
import com.android.systemui.statusbar.FlingAnimationUtils;
import android.graphics.Rect;
import android.view.Display;
import android.animation.ValueAnimator;
import android.view.View;
import android.animation.AnimationHandler;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.view.ViewTreeObserver$OnComputeInternalInsetsListener;
import android.view.View$OnTouchListener;
import android.widget.FrameLayout;

public class DividerView extends FrameLayout implements View$OnTouchListener, ViewTreeObserver$OnComputeInternalInsetsListener
{
    private static final PathInterpolator DIM_INTERPOLATOR;
    private static final Interpolator IME_ADJUST_INTERPOLATOR;
    private static final PathInterpolator SLOWDOWN_INTERPOLATOR;
    private boolean mAdjustedForIme;
    private final AnimationHandler mAnimationHandler;
    private View mBackground;
    private boolean mBackgroundLifted;
    private DividerCallbacks mCallback;
    private ValueAnimator mCurrentAnimator;
    private final Display mDefaultDisplay;
    private int mDividerInsets;
    int mDividerPositionX;
    int mDividerPositionY;
    private int mDividerSize;
    private int mDockSide;
    private final Rect mDockedInsetRect;
    private final Rect mDockedRect;
    private boolean mDockedStackMinimized;
    private final Rect mDockedTaskRect;
    private boolean mEntranceAnimationRunning;
    private boolean mExitAnimationRunning;
    private int mExitStartPosition;
    boolean mFirstLayout;
    private FlingAnimationUtils mFlingAnimationUtils;
    private DividerHandleView mHandle;
    private final View$AccessibilityDelegate mHandleDelegate;
    private final Handler mHandler;
    private boolean mHomeStackResizable;
    private boolean mIsInMinimizeInteraction;
    private final Rect mLastResizeRect;
    private int mLongPressEntraceAnimDuration;
    private MinimizedDockShadow mMinimizedShadow;
    private boolean mMoving;
    private final Rect mOtherInsetRect;
    private final Rect mOtherRect;
    private final Rect mOtherTaskRect;
    private boolean mRemoved;
    private final Runnable mResetBackgroundRunnable;
    DividerSnapAlgorithm$SnapTarget mSnapTargetBeforeMinimized;
    private SplitDisplayLayout mSplitLayout;
    private final Rect mStableInsets;
    private int mStartPosition;
    private int mStartX;
    private int mStartY;
    private DividerState mState;
    private boolean mSurfaceHidden;
    private SplitScreenTaskOrganizer mTiles;
    private final Rect mTmpRect;
    private int mTouchElevation;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private DividerWindowManager mWindowManager;
    private final WindowManagerProxy mWindowManagerProxy;
    
    static {
        SLOWDOWN_INTERPOLATOR = new PathInterpolator(0.5f, 1.0f, 0.5f, 1.0f);
        DIM_INTERPOLATOR = new PathInterpolator(0.23f, 0.87f, 0.52f, -0.11f);
        IME_ADJUST_INTERPOLATOR = (Interpolator)new PathInterpolator(0.2f, 0.0f, 0.1f, 1.0f);
    }
    
    public DividerView(final Context context) {
        this(context, null);
    }
    
    public DividerView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public DividerView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public DividerView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mDockedRect = new Rect();
        this.mDockedTaskRect = new Rect();
        this.mOtherTaskRect = new Rect();
        this.mOtherRect = new Rect();
        this.mDockedInsetRect = new Rect();
        this.mOtherInsetRect = new Rect();
        this.mLastResizeRect = new Rect();
        this.mTmpRect = new Rect();
        this.mWindowManagerProxy = WindowManagerProxy.getInstance();
        this.mStableInsets = new Rect();
        this.mAnimationHandler = new AnimationHandler();
        this.mFirstLayout = true;
        this.mSurfaceHidden = false;
        this.mHandler = new Handler();
        this.mHandleDelegate = new View$AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                final DividerSnapAlgorithm snapAlgorithm = DividerView.this.getSnapAlgorithm();
                if (DividerView.this.isHorizontalDivision()) {
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_tl_full, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_top_full)));
                    if (snapAlgorithm.isFirstSplitTargetAvailable()) {
                        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_tl_70, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_top_70)));
                    }
                    if (snapAlgorithm.showMiddleSplitTargetForAccessibility()) {
                        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_tl_50, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_top_50)));
                    }
                    if (snapAlgorithm.isLastSplitTargetAvailable()) {
                        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_tl_30, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_top_30)));
                    }
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_rb_full, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_bottom_full)));
                }
                else {
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_tl_full, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_left_full)));
                    if (snapAlgorithm.isFirstSplitTargetAvailable()) {
                        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_tl_70, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_left_70)));
                    }
                    if (snapAlgorithm.showMiddleSplitTargetForAccessibility()) {
                        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_tl_50, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_left_50)));
                    }
                    if (snapAlgorithm.isLastSplitTargetAvailable()) {
                        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_tl_30, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_left_30)));
                    }
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_move_rb_full, (CharSequence)DividerView.this.mContext.getString(R$string.accessibility_action_divider_right_full)));
                }
            }
            
            public boolean performAccessibilityAction(final View view, final int n, final Bundle bundle) {
                final int currentPosition = DividerView.this.getCurrentPosition();
                final DividerSnapAlgorithm snapAlgorithm = DividerView.this.mSplitLayout.getSnapAlgorithm();
                DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget;
                if (n == R$id.action_move_tl_full) {
                    dividerSnapAlgorithm$SnapTarget = snapAlgorithm.getDismissEndTarget();
                }
                else if (n == R$id.action_move_tl_70) {
                    dividerSnapAlgorithm$SnapTarget = snapAlgorithm.getLastSplitTarget();
                }
                else if (n == R$id.action_move_tl_50) {
                    dividerSnapAlgorithm$SnapTarget = snapAlgorithm.getMiddleTarget();
                }
                else if (n == R$id.action_move_tl_30) {
                    dividerSnapAlgorithm$SnapTarget = snapAlgorithm.getFirstSplitTarget();
                }
                else if (n == R$id.action_move_rb_full) {
                    dividerSnapAlgorithm$SnapTarget = snapAlgorithm.getDismissStartTarget();
                }
                else {
                    dividerSnapAlgorithm$SnapTarget = null;
                }
                if (dividerSnapAlgorithm$SnapTarget != null) {
                    DividerView.this.startDragging(true, false);
                    DividerView.this.stopDragging(currentPosition, dividerSnapAlgorithm$SnapTarget, 250L, Interpolators.FAST_OUT_SLOW_IN);
                    return true;
                }
                return super.performAccessibilityAction(view, n, bundle);
            }
        };
        this.mResetBackgroundRunnable = new Runnable() {
            @Override
            public void run() {
                DividerView.this.resetBackground();
            }
        };
        this.mDefaultDisplay = ((DisplayManager)super.mContext.getSystemService("display")).getDisplay(0);
        this.mAnimationHandler.setProvider((AnimationHandler$AnimationFrameCallbackProvider)new SfVsyncFrameCallbackProvider());
    }
    
    private void alignBottomRight(final Rect rect, final Rect rect2) {
        final int width = rect2.width();
        final int height = rect2.height();
        final int right = rect.right;
        final int bottom = rect.bottom;
        rect2.set(right - width, bottom - height, right, bottom);
    }
    
    private void alignTopLeft(final Rect rect, final Rect rect2) {
        final int width = rect2.width();
        final int height = rect2.height();
        final int left = rect.left;
        final int top = rect.top;
        rect2.set(left, top, width + left, height + top);
    }
    
    private void applyDismissingParallax(final Rect rect, int n, DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget, int n2, int n3) {
        final float min = Math.min(1.0f, Math.max(0.0f, this.mSplitLayout.getSnapAlgorithm().calculateDismissingFraction(n2)));
        final int position = this.mSplitLayout.getSnapAlgorithm().getLastSplitTarget().position;
        DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget2 = null;
        if (n2 <= position && dockSideTopLeft(n)) {
            dividerSnapAlgorithm$SnapTarget2 = this.mSplitLayout.getSnapAlgorithm().getDismissStartTarget();
            dividerSnapAlgorithm$SnapTarget = this.mSplitLayout.getSnapAlgorithm().getFirstSplitTarget();
        }
        else if (n2 >= this.mSplitLayout.getSnapAlgorithm().getLastSplitTarget().position && dockSideBottomRight(n)) {
            dividerSnapAlgorithm$SnapTarget2 = this.mSplitLayout.getSnapAlgorithm().getDismissEndTarget();
            dividerSnapAlgorithm$SnapTarget = this.mSplitLayout.getSnapAlgorithm().getLastSplitTarget();
            n3 = dividerSnapAlgorithm$SnapTarget.position;
        }
        else {
            n3 = 0;
            dividerSnapAlgorithm$SnapTarget = null;
        }
        if (dividerSnapAlgorithm$SnapTarget2 != null && min > 0.0f && isDismissing(dividerSnapAlgorithm$SnapTarget, n2, n)) {
            n2 = (int)(n3 + calculateParallaxDismissingFraction(min, n) * (dividerSnapAlgorithm$SnapTarget2.position - dividerSnapAlgorithm$SnapTarget.position));
            final int width = rect.width();
            n3 = rect.height();
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        if (n == 4) {
                            n = this.mDividerSize;
                            rect.top = n2 + n;
                            rect.bottom = n2 + n3 + n;
                        }
                    }
                    else {
                        n = this.mDividerSize;
                        rect.left = n2 + n;
                        rect.right = n2 + width + n;
                    }
                }
                else {
                    rect.top = n2 - n3;
                    rect.bottom = n2;
                }
            }
            else {
                rect.left = n2 - width;
                rect.right = n2;
            }
        }
    }
    
    private void applyExitAnimationParallax(final Rect rect, final int n) {
        final int mDockSide = this.mDockSide;
        if (mDockSide == 2) {
            rect.offset(0, (int)((n - this.mExitStartPosition) * 0.25f));
        }
        else if (mDockSide == 1) {
            rect.offset((int)((n - this.mExitStartPosition) * 0.25f), 0);
        }
        else if (mDockSide == 3) {
            rect.offset((int)((this.mExitStartPosition - n) * 0.25f), 0);
        }
    }
    
    private static float calculateParallaxDismissingFraction(float n, final int n2) {
        final float n3 = n = DividerView.SLOWDOWN_INTERPOLATOR.getInterpolation(n) / 3.5f;
        if (n2 == 2) {
            n = n3 / 2.0f;
        }
        return n;
    }
    
    private int calculatePosition(int n, final int n2) {
        if (this.isHorizontalDivision()) {
            n = this.calculateYPosition(n2);
        }
        else {
            n = this.calculateXPosition(n);
        }
        return n;
    }
    
    private int calculatePositionForInsetBounds() {
        this.mSplitLayout.mDisplayLayout.getStableBounds(this.mTmpRect);
        return DockedDividerUtils.calculatePositionForBounds(this.mTmpRect, this.mDockSide, this.mDividerSize);
    }
    
    private int calculateXPosition(final int n) {
        return this.mStartPosition + n - this.mStartX;
    }
    
    private int calculateYPosition(final int n) {
        return this.mStartPosition + n - this.mStartY;
    }
    
    private void cancelFlingAnimation() {
        final ValueAnimator mCurrentAnimator = this.mCurrentAnimator;
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
    }
    
    private boolean commitSnapFlags(final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget) {
        final int flag = dividerSnapAlgorithm$SnapTarget.flag;
        if (flag == 0) {
            return false;
        }
        boolean b = false;
        Label_0062: {
            Label_0039: {
                if (flag == 1) {
                    final int mDockSide = this.mDockSide;
                    if (mDockSide == 1) {
                        break Label_0039;
                    }
                    if (mDockSide == 2) {
                        break Label_0039;
                    }
                }
                else {
                    final int mDockSide2 = this.mDockSide;
                    if (mDockSide2 == 3) {
                        break Label_0039;
                    }
                    if (mDockSide2 == 4) {
                        break Label_0039;
                    }
                }
                b = false;
                break Label_0062;
            }
            b = true;
        }
        this.mWindowManagerProxy.dismissOrMaximizeDocked(this.mTiles, b);
        final SurfaceControl$Transaction transaction = this.mTiles.getTransaction();
        this.setResizeDimLayer(transaction, true, 0.0f);
        this.setResizeDimLayer(transaction, false, 0.0f);
        transaction.apply();
        this.mTiles.releaseTransaction(transaction);
        return true;
    }
    
    private void convertToScreenCoordinates(final MotionEvent motionEvent) {
        motionEvent.setLocation(motionEvent.getRawX(), motionEvent.getRawY());
    }
    
    private static boolean dockSideBottomRight(final int n) {
        return n == 4 || n == 3;
    }
    
    private static boolean dockSideTopLeft(final int n) {
        boolean b = true;
        if (n != 2) {
            b = (n == 1 && b);
        }
        return b;
    }
    
    private void fling(final int n, final float n2, final boolean b, final boolean b2) {
        final DividerSnapAlgorithm snapAlgorithm = this.getSnapAlgorithm();
        DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget2;
        final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget = dividerSnapAlgorithm$SnapTarget2 = snapAlgorithm.calculateSnapTarget(n, n2);
        if (b && (dividerSnapAlgorithm$SnapTarget2 = dividerSnapAlgorithm$SnapTarget) == snapAlgorithm.getDismissStartTarget()) {
            dividerSnapAlgorithm$SnapTarget2 = snapAlgorithm.getFirstSplitTarget();
        }
        if (b2) {
            this.logResizeEvent(dividerSnapAlgorithm$SnapTarget2);
        }
        final ValueAnimator flingAnimator = this.getFlingAnimator(n, dividerSnapAlgorithm$SnapTarget2, 0L);
        this.mFlingAnimationUtils.apply((Animator)flingAnimator, (float)n, (float)dividerSnapAlgorithm$SnapTarget2.position, n2);
        flingAnimator.start();
    }
    
    private void flingTo(final int n, final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget, final long duration, final long startDelay, final long n2, final Interpolator interpolator) {
        final ValueAnimator flingAnimator = this.getFlingAnimator(n, dividerSnapAlgorithm$SnapTarget, n2);
        flingAnimator.setDuration(duration);
        flingAnimator.setStartDelay(startDelay);
        flingAnimator.setInterpolator((TimeInterpolator)interpolator);
        flingAnimator.start();
    }
    
    private float getDimFraction(final int n, final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget) {
        if (this.mEntranceAnimationRunning) {
            return 0.0f;
        }
        float interpolation = DividerView.DIM_INTERPOLATOR.getInterpolation(Math.max(0.0f, Math.min(this.getSnapAlgorithm().calculateDismissingFraction(n), 1.0f)));
        if (this.hasInsetsAtDismissTarget(dividerSnapAlgorithm$SnapTarget)) {
            interpolation *= 0.8f;
        }
        return interpolation;
    }
    
    private ValueAnimator getFlingAnimator(final int n, final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget, final long n2) {
        if (this.mCurrentAnimator != null) {
            this.cancelFlingAnimation();
            this.updateDockSide();
        }
        final boolean b = dividerSnapAlgorithm$SnapTarget.flag == 0;
        final ValueAnimator ofInt = ValueAnimator.ofInt(new int[] { n, dividerSnapAlgorithm$SnapTarget.position });
        ofInt.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$DividerView$x6ptymtmG16alGF_IrKxQCHEKzc(this, b, dividerSnapAlgorithm$SnapTarget));
        ofInt.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mCancelled;
            final /* synthetic */ Consumer val$endAction = new _$$Lambda$DividerView$S31_LrddXUPvkXhtCmCT64_Mzgs(this, dividerSnapAlgorithm$SnapTarget);
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                long val$endDelay = n2;
                if (val$endDelay == 0L) {
                    final boolean mCancelled = this.mCancelled;
                    val$endDelay = 0L;
                }
                if (val$endDelay == 0L) {
                    this.val$endAction.accept(this.mCancelled);
                }
                else {
                    DividerView.this.mHandler.postDelayed((Runnable)new _$$Lambda$DividerView$3$46R0aXdd2QIwTYyQi54oXkaDuIU(this.val$endAction, this.mCancelled), val$endDelay);
                }
            }
        });
        ofInt.setAnimationHandler(this.mAnimationHandler);
        return this.mCurrentAnimator = ofInt;
    }
    
    private SurfaceControl getWindowSurfaceControl() {
        if (this.getViewRootImpl() == null) {
            return null;
        }
        final SurfaceControl surfaceControl = this.getViewRootImpl().getSurfaceControl();
        if (surfaceControl != null && surfaceControl.isValid()) {
            return surfaceControl;
        }
        return this.mWindowManager.mSystemWindows.getViewSurface((View)this);
    }
    
    private boolean hasInsetsAtDismissTarget(final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget) {
        final boolean horizontalDivision = this.isHorizontalDivision();
        final boolean b = true;
        final boolean b2 = true;
        final boolean b3 = true;
        boolean b4 = true;
        if (horizontalDivision) {
            if (dividerSnapAlgorithm$SnapTarget == this.getSnapAlgorithm().getDismissStartTarget()) {
                if (this.mStableInsets.top == 0) {
                    b4 = false;
                }
                return b4;
            }
            return this.mStableInsets.bottom != 0 && b;
        }
        else {
            if (dividerSnapAlgorithm$SnapTarget == this.getSnapAlgorithm().getDismissStartTarget()) {
                return this.mStableInsets.left != 0 && b2;
            }
            return this.mStableInsets.right != 0 && b3;
        }
    }
    
    private boolean inSplitMode() {
        return this.getVisibility() == 0;
    }
    
    private void initializeSurfaceState() {
        this.mSplitLayout.resizeSplits(this.mSplitLayout.getSnapAlgorithm().getMiddleTarget().position);
        final SurfaceControl$Transaction transaction = this.mTiles.getTransaction();
        if (this.mDockedStackMinimized) {
            final int position = this.mSplitLayout.getMinimizedSnapAlgorithm().getMiddleTarget().position;
            this.calculateBoundsForPosition(position, this.mDockSide, this.mDockedRect);
            this.calculateBoundsForPosition(position, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherRect);
            this.mDividerPositionY = position;
            this.mDividerPositionX = position;
            final Rect mDockedRect = this.mDockedRect;
            final SplitDisplayLayout mSplitLayout = this.mSplitLayout;
            this.resizeSplitSurfaces(transaction, mDockedRect, mSplitLayout.mPrimary, this.mOtherRect, mSplitLayout.mSecondary);
        }
        else {
            final SplitDisplayLayout mSplitLayout2 = this.mSplitLayout;
            this.resizeSplitSurfaces(transaction, mSplitLayout2.mPrimary, null, mSplitLayout2.mSecondary, null);
        }
        this.setResizeDimLayer(transaction, true, 0.0f);
        this.setResizeDimLayer(transaction, false, 0.0f);
        transaction.apply();
        this.mTiles.releaseTransaction(transaction);
    }
    
    private boolean isDismissTargetPrimary(final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget) {
        final int flag = dividerSnapAlgorithm$SnapTarget.flag;
        final boolean b = true;
        if (flag == 1) {
            final boolean b2 = b;
            if (dockSideTopLeft(this.mDockSide)) {
                return b2;
            }
        }
        return dividerSnapAlgorithm$SnapTarget.flag == 2 && dockSideBottomRight(this.mDockSide) && b;
    }
    
    private static boolean isDismissing(final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget, final int n, final int n2) {
        final boolean b = false;
        boolean b2 = false;
        if (n2 != 2 && n2 != 1) {
            if (n > dividerSnapAlgorithm$SnapTarget.position) {
                b2 = true;
            }
            return b2;
        }
        boolean b3 = b;
        if (n < dividerSnapAlgorithm$SnapTarget.position) {
            b3 = true;
        }
        return b3;
    }
    
    private void liftBackground() {
        if (this.mBackgroundLifted) {
            return;
        }
        if (this.isHorizontalDivision()) {
            this.mBackground.animate().scaleY(1.4f);
        }
        else {
            this.mBackground.animate().scaleX(1.4f);
        }
        this.mBackground.animate().setInterpolator((TimeInterpolator)Interpolators.TOUCH_RESPONSE).setDuration(150L).translationZ((float)this.mTouchElevation).start();
        this.mHandle.animate().setInterpolator((TimeInterpolator)Interpolators.TOUCH_RESPONSE).setDuration(150L).translationZ((float)this.mTouchElevation).start();
        this.mBackgroundLifted = true;
    }
    
    private void logResizeEvent(final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget) {
        if (dividerSnapAlgorithm$SnapTarget == this.mSplitLayout.getSnapAlgorithm().getDismissStartTarget()) {
            MetricsLogger.action(super.mContext, 390, (int)(dockSideTopLeft(this.mDockSide) ? 1 : 0));
        }
        else if (dividerSnapAlgorithm$SnapTarget == this.mSplitLayout.getSnapAlgorithm().getDismissEndTarget()) {
            MetricsLogger.action(super.mContext, 390, (int)(dockSideBottomRight(this.mDockSide) ? 1 : 0));
        }
        else if (dividerSnapAlgorithm$SnapTarget == this.mSplitLayout.getSnapAlgorithm().getMiddleTarget()) {
            MetricsLogger.action(super.mContext, 389, 0);
        }
        else {
            final DividerSnapAlgorithm$SnapTarget firstSplitTarget = this.mSplitLayout.getSnapAlgorithm().getFirstSplitTarget();
            final int n = 1;
            int n2 = 1;
            if (dividerSnapAlgorithm$SnapTarget == firstSplitTarget) {
                final Context mContext = super.mContext;
                if (!dockSideTopLeft(this.mDockSide)) {
                    n2 = 2;
                }
                MetricsLogger.action(mContext, 389, n2);
            }
            else if (dividerSnapAlgorithm$SnapTarget == this.mSplitLayout.getSnapAlgorithm().getLastSplitTarget()) {
                final Context mContext2 = super.mContext;
                int n3 = n;
                if (dockSideTopLeft(this.mDockSide)) {
                    n3 = 2;
                }
                MetricsLogger.action(mContext2, 389, n3);
            }
        }
    }
    
    private void releaseBackground() {
        if (!this.mBackgroundLifted) {
            return;
        }
        this.mBackground.animate().setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).setDuration(200L).translationZ(0.0f).scaleX(1.0f).scaleY(1.0f).start();
        this.mHandle.animate().setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).setDuration(200L).translationZ(0.0f).start();
        this.mBackgroundLifted = false;
    }
    
    private void repositionSnapTargetBeforeMinimized() {
        final float mRatioPositionBeforeMinimized = this.mState.mRatioPositionBeforeMinimized;
        int n;
        if (this.isHorizontalDivision()) {
            n = this.mSplitLayout.mDisplayLayout.height();
        }
        else {
            n = this.mSplitLayout.mDisplayLayout.width();
        }
        this.mSnapTargetBeforeMinimized = this.mSplitLayout.getSnapAlgorithm().calculateNonDismissingSnapTarget((int)(mRatioPositionBeforeMinimized * n));
    }
    
    private void resetBackground() {
        final View mBackground = this.mBackground;
        mBackground.setPivotX((float)(mBackground.getWidth() / 2));
        final View mBackground2 = this.mBackground;
        mBackground2.setPivotY((float)(mBackground2.getHeight() / 2));
        this.mBackground.setScaleX(1.0f);
        this.mBackground.setScaleY(1.0f);
        this.mMinimizedShadow.setAlpha(0.0f);
    }
    
    private void resizeSplitSurfaces(final SurfaceControl$Transaction surfaceControl$Transaction, final Rect rect, Rect rect2, final Rect rect3, final Rect rect4) {
        Rect rect5 = rect2;
        if (rect2 == null) {
            rect5 = rect;
        }
        if ((rect2 = rect4) == null) {
            rect2 = rect3;
        }
        this.mDividerPositionX = rect.right;
        this.mDividerPositionY = rect.bottom;
        surfaceControl$Transaction.setPosition(this.mTiles.mPrimarySurface, (float)rect5.left, (float)rect5.top);
        final Rect rect6 = new Rect(rect);
        rect6.offsetTo(-Math.min(rect5.left - rect.left, 0), -Math.min(rect5.top - rect.top, 0));
        surfaceControl$Transaction.setWindowCrop(this.mTiles.mPrimarySurface, rect6);
        surfaceControl$Transaction.setPosition(this.mTiles.mSecondarySurface, (float)rect2.left, (float)rect2.top);
        rect6.set(rect3);
        rect6.offsetTo(-(rect2.left - rect3.left), -(rect2.top - rect3.top));
        surfaceControl$Transaction.setWindowCrop(this.mTiles.mSecondarySurface, rect6);
        final SurfaceControl windowSurfaceControl = this.getWindowSurfaceControl();
        if (windowSurfaceControl != null) {
            if (this.isHorizontalDivision()) {
                surfaceControl$Transaction.setPosition(windowSurfaceControl, 0.0f, (float)(this.mDividerPositionY - this.mDividerInsets));
            }
            else {
                surfaceControl$Transaction.setPosition(windowSurfaceControl, (float)(this.mDividerPositionX - this.mDividerInsets), 0.0f);
            }
        }
    }
    
    private void resizeStackSurfaces(final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget) {
        final int position = dividerSnapAlgorithm$SnapTarget.position;
        this.resizeStackSurfaces(position, position, dividerSnapAlgorithm$SnapTarget);
    }
    
    private int restrictDismissingTaskPosition(final int n, final int n2, final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget) {
        if (dividerSnapAlgorithm$SnapTarget.flag == 1 && dockSideTopLeft(n2)) {
            return Math.max(this.mSplitLayout.getSnapAlgorithm().getFirstSplitTarget().position, this.mStartPosition);
        }
        if (dividerSnapAlgorithm$SnapTarget.flag == 2 && dockSideBottomRight(n2)) {
            return Math.min(this.mSplitLayout.getSnapAlgorithm().getLastSplitTarget().position, this.mStartPosition);
        }
        return n;
    }
    
    private void saveSnapTargetBeforeMinimized(final DividerSnapAlgorithm$SnapTarget mSnapTargetBeforeMinimized) {
        this.mSnapTargetBeforeMinimized = mSnapTargetBeforeMinimized;
        final DividerState mState = this.mState;
        final float n = (float)mSnapTargetBeforeMinimized.position;
        int n2;
        if (this.isHorizontalDivision()) {
            n2 = this.mSplitLayout.mDisplayLayout.height();
        }
        else {
            n2 = this.mSplitLayout.mDisplayLayout.width();
        }
        mState.mRatioPositionBeforeMinimized = n / n2;
    }
    
    private void stopDragging() {
        this.mHandle.setTouching(false, true);
        this.mWindowManager.setSlippery(true);
        this.releaseBackground();
    }
    
    private void updateDockSide() {
        final int primarySplitSide = this.mSplitLayout.getPrimarySplitSide();
        this.mDockSide = primarySplitSide;
        this.mMinimizedShadow.setDockSide(primarySplitSide);
    }
    
    public void calculateBoundsForPosition(final int n, final int n2, final Rect rect) {
        DockedDividerUtils.calculateBoundsForPosition(n, n2, rect, this.mSplitLayout.mDisplayLayout.width(), this.mSplitLayout.mDisplayLayout.height(), this.mDividerSize);
    }
    
    void enterSplitMode(final boolean b) {
        this.post((Runnable)new _$$Lambda$DividerView$_bnLr00UXtK_DynRLhejMeaxzqY(this));
        if (b) {
            final DividerSnapAlgorithm$SnapTarget middleTarget = this.mSplitLayout.getMinimizedSnapAlgorithm().getMiddleTarget();
            if (this.mDockedStackMinimized) {
                final int position = middleTarget.position;
                this.mDividerPositionX = position;
                this.mDividerPositionY = position;
            }
        }
    }
    
    void exitSplitMode() {
        this.post((Runnable)new _$$Lambda$DividerView$6_QMyrCv8U30qUtVPtEbRLknnHI(this));
        WindowManagerProxy.applyResizeSplits(this.mSplitLayout.getSnapAlgorithm().getMiddleTarget().position, this.mSplitLayout);
    }
    
    void finishAnimations() {
        final ValueAnimator mCurrentAnimator = this.mCurrentAnimator;
        if (mCurrentAnimator != null) {
            mCurrentAnimator.end();
        }
    }
    
    public int getCurrentPosition() {
        int n;
        if (this.isHorizontalDivision()) {
            n = this.mDividerPositionY;
        }
        else {
            n = this.mDividerPositionX;
        }
        return n;
    }
    
    public Rect getNonMinimizedSplitScreenSecondaryBounds() {
        this.calculateBoundsForPosition(this.mSnapTargetBeforeMinimized.position, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
        final Rect mOtherTaskRect = this.mOtherTaskRect;
        final int bottom = mOtherTaskRect.bottom;
        final Rect mStableInsets = this.mStableInsets;
        mOtherTaskRect.bottom = bottom - mStableInsets.bottom;
        final int mDockSide = this.mDockSide;
        if (mDockSide != 1) {
            if (mDockSide == 3) {
                mOtherTaskRect.top += mStableInsets.top;
                mOtherTaskRect.left += mStableInsets.left;
            }
        }
        else {
            mOtherTaskRect.top += mStableInsets.top;
            mOtherTaskRect.right -= mStableInsets.right;
        }
        return this.mOtherTaskRect;
    }
    
    public DividerSnapAlgorithm getSnapAlgorithm() {
        DividerSnapAlgorithm dividerSnapAlgorithm;
        if (this.mDockedStackMinimized && this.mHomeStackResizable) {
            dividerSnapAlgorithm = this.mSplitLayout.getMinimizedSnapAlgorithm();
        }
        else {
            dividerSnapAlgorithm = this.mSplitLayout.getSnapAlgorithm();
        }
        return dividerSnapAlgorithm;
    }
    
    public void injectDependencies(final DividerWindowManager mWindowManager, final DividerState mState, final DividerCallbacks mCallback, final SplitScreenTaskOrganizer mTiles, final SplitDisplayLayout mSplitLayout) {
        this.mWindowManager = mWindowManager;
        this.mState = mState;
        this.mCallback = mCallback;
        this.mTiles = mTiles;
        this.mSplitLayout = mSplitLayout;
        if (mState.mRatioPositionBeforeMinimized == 0.0f) {
            this.mSnapTargetBeforeMinimized = mSplitLayout.getSnapAlgorithm().getMiddleTarget();
        }
        else {
            this.repositionSnapTargetBeforeMinimized();
        }
    }
    
    boolean isHidden() {
        return this.mSurfaceHidden;
    }
    
    public boolean isHorizontalDivision() {
        final int orientation = this.getResources().getConfiguration().orientation;
        boolean b = true;
        if (orientation != 1) {
            b = false;
        }
        return b;
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        WindowInsets calculateInsets = windowInsets;
        if (this.isAttachedToWindow()) {
            calculateInsets = windowInsets;
            if (ViewRootImpl.sNewInsetsMode == 2) {
                final InsetsState state = this.getWindowInsetsController().getState();
                calculateInsets = state.calculateInsets(state.getDisplayFrame(), (InsetsState)null, windowInsets.isRound(), windowInsets.shouldAlwaysConsumeSystemBars(), windowInsets.getDisplayCutout(), 0, 48, (SparseIntArray)null);
            }
        }
        if (this.mStableInsets.left != calculateInsets.getStableInsetLeft() || this.mStableInsets.top != calculateInsets.getStableInsetTop() || this.mStableInsets.right != calculateInsets.getStableInsetRight() || this.mStableInsets.bottom != calculateInsets.getStableInsetBottom()) {
            this.mStableInsets.set(calculateInsets.getStableInsetLeft(), calculateInsets.getStableInsetTop(), calculateInsets.getStableInsetRight(), calculateInsets.getStableInsetBottom());
        }
        return super.onApplyWindowInsets(calculateInsets);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mHomeStackResizable && this.mDockSide != -1 && !this.mIsInMinimizeInteraction) {
            this.saveSnapTargetBeforeMinimized(this.mSnapTargetBeforeMinimized);
        }
        this.mFirstLayout = true;
    }
    
    public void onComputeInternalInsets(final ViewTreeObserver$InternalInsetsInfo viewTreeObserver$InternalInsetsInfo) {
        viewTreeObserver$InternalInsetsInfo.setTouchableInsets(3);
        viewTreeObserver$InternalInsetsInfo.touchableRegion.set(this.mHandle.getLeft(), this.mHandle.getTop(), this.mHandle.getRight(), this.mHandle.getBottom());
        viewTreeObserver$InternalInsetsInfo.touchableRegion.op(this.mBackground.getLeft(), this.mBackground.getTop(), this.mBackground.getRight(), this.mBackground.getBottom(), Region$Op.UNION);
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }
    
    void onDividerRemoved() {
        this.mRemoved = true;
        this.mCallback = null;
    }
    
    void onDockedFirstAnimationFrame() {
        this.saveSnapTargetBeforeMinimized(this.mSplitLayout.getSnapAlgorithm().getMiddleTarget());
    }
    
    void onDockedTopTask() {
        final DividerState mState = this.mState;
        mState.growAfterRecentsDrawn = false;
        mState.animateAfterRecentsDrawn = true;
        this.startDragging(false, false);
        this.updateDockSide();
        this.mEntranceAnimationRunning = true;
        this.resizeStackSurfaces(this.calculatePositionForInsetBounds(), this.mSplitLayout.getSnapAlgorithm().getMiddleTarget().position, this.mSplitLayout.getSnapAlgorithm().getMiddleTarget());
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHandle = (DividerHandleView)this.findViewById(R$id.docked_divider_handle);
        this.mBackground = this.findViewById(R$id.docked_divider_background);
        this.mMinimizedShadow = (MinimizedDockShadow)this.findViewById(R$id.minimized_dock_shadow);
        this.mHandle.setOnTouchListener((View$OnTouchListener)this);
        final int dimensionPixelSize = this.getResources().getDimensionPixelSize(17105175);
        final int dimensionPixelSize2 = this.getResources().getDimensionPixelSize(17105174);
        this.mDividerInsets = dimensionPixelSize2;
        this.mDividerSize = dimensionPixelSize - dimensionPixelSize2 * 2;
        this.mTouchElevation = this.getResources().getDimensionPixelSize(R$dimen.docked_stack_divider_lift_elevation);
        this.mLongPressEntraceAnimDuration = this.getResources().getInteger(R$integer.long_press_dock_anim_duration);
        this.getResources().getBoolean(R$bool.recents_grow_in_multiwindow);
        this.mTouchSlop = ViewConfiguration.get(super.mContext).getScaledTouchSlop();
        this.mFlingAnimationUtils = new FlingAnimationUtils(this.getResources().getDisplayMetrics(), 0.3f);
        final boolean b = this.getResources().getConfiguration().orientation == 2;
        final DividerHandleView mHandle = this.mHandle;
        final Context context = this.getContext();
        int n;
        if (b) {
            n = 1014;
        }
        else {
            n = 1015;
        }
        mHandle.setPointerIcon(PointerIcon.getSystemIcon(context, n));
        this.getViewTreeObserver().addOnComputeInternalInsetsListener((ViewTreeObserver$OnComputeInternalInsetsListener)this);
        this.mHandle.setAccessibilityDelegate(this.mHandleDelegate);
    }
    
    protected void onLayout(final boolean b, int n, int n2, int n3, final int n4) {
        final boolean mFirstLayout = this.mFirstLayout;
        final int n5 = 0;
        if (mFirstLayout) {
            this.initializeSurfaceState();
            this.mFirstLayout = false;
        }
        super.onLayout(b, n, n2, n3, n4);
        n = this.mDockSide;
        Label_0111: {
            if (n == 2) {
                n = this.mBackground.getTop();
                n2 = n5;
            }
            else {
                if (n == 1) {
                    n = this.mBackground.getLeft();
                }
                else {
                    if (n != 3) {
                        n = 0;
                        n2 = n5;
                        break Label_0111;
                    }
                    n = this.mBackground.getRight() - this.mMinimizedShadow.getWidth();
                }
                n3 = 0;
                n2 = n;
                n = n3;
            }
        }
        final MinimizedDockShadow mMinimizedShadow = this.mMinimizedShadow;
        mMinimizedShadow.layout(n2, n, mMinimizedShadow.getMeasuredWidth() + n2, this.mMinimizedShadow.getMeasuredHeight() + n);
        if (b) {
            this.mWindowManagerProxy.setTouchRegion(new Rect(this.mHandle.getLeft(), this.mHandle.getTop(), this.mHandle.getRight(), this.mHandle.getBottom()));
        }
    }
    
    void onRecentsDrawn() {
        this.updateDockSide();
        final int calculatePositionForInsetBounds = this.calculatePositionForInsetBounds();
        final DividerState mState = this.mState;
        if (mState.animateAfterRecentsDrawn) {
            mState.animateAfterRecentsDrawn = false;
            this.mHandler.post((Runnable)new _$$Lambda$DividerView$sxEYNP7KxoTbmEvg7gYGZZnnOWQ(this, calculatePositionForInsetBounds));
        }
        final DividerState mState2 = this.mState;
        if (mState2.growAfterRecentsDrawn) {
            mState2.growAfterRecentsDrawn = false;
            this.updateDockSide();
            final DividerCallbacks mCallback = this.mCallback;
            if (mCallback != null) {
                mCallback.growRecents();
            }
            this.stopDragging(calculatePositionForInsetBounds, this.getSnapAlgorithm().getMiddleTarget(), 336L, Interpolators.FAST_OUT_SLOW_IN);
        }
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        this.convertToScreenCoordinates(motionEvent);
        final int n = motionEvent.getAction() & 0xFF;
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        return true;
                    }
                }
                else {
                    this.mVelocityTracker.addMovement(motionEvent);
                    final int mStartX = (int)motionEvent.getX();
                    final int mStartY = (int)motionEvent.getY();
                    final boolean b = (this.isHorizontalDivision() && Math.abs(mStartY - this.mStartY) > this.mTouchSlop) || (!this.isHorizontalDivision() && Math.abs(mStartX - this.mStartX) > this.mTouchSlop);
                    if (!this.mMoving && b) {
                        this.mStartX = mStartX;
                        this.mStartY = mStartY;
                        this.mMoving = true;
                    }
                    if (this.mMoving && this.mDockSide != -1) {
                        this.resizeStackSurfaces(this.calculatePosition(mStartX, mStartY), this.mStartPosition, this.getSnapAlgorithm().calculateSnapTarget(this.mStartPosition, 0.0f, false));
                        return true;
                    }
                    return true;
                }
            }
            this.mVelocityTracker.addMovement(motionEvent);
            final int n2 = (int)motionEvent.getRawX();
            final int n3 = (int)motionEvent.getRawY();
            this.mVelocityTracker.computeCurrentVelocity(1000);
            final int calculatePosition = this.calculatePosition(n2, n3);
            float n4;
            if (this.isHorizontalDivision()) {
                n4 = this.mVelocityTracker.getYVelocity();
            }
            else {
                n4 = this.mVelocityTracker.getXVelocity();
            }
            this.stopDragging(calculatePosition, n4, false, true);
            this.mMoving = false;
            return true;
        }
        (this.mVelocityTracker = VelocityTracker.obtain()).addMovement(motionEvent);
        this.mStartX = (int)motionEvent.getX();
        this.mStartY = (int)motionEvent.getY();
        final boolean startDragging = this.startDragging(true, true);
        if (!startDragging) {
            this.stopDragging();
        }
        this.mStartPosition = this.getCurrentPosition();
        this.mMoving = false;
        return startDragging;
    }
    
    void onUndockingTask() {
        final int primarySplitSide = this.mSplitLayout.getPrimarySplitSide();
        if (this.inSplitMode() && (this.mHomeStackResizable || !this.mDockedStackMinimized)) {
            this.startDragging(false, false);
            DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget;
            if (dockSideTopLeft(primarySplitSide)) {
                dividerSnapAlgorithm$SnapTarget = this.mSplitLayout.getSnapAlgorithm().getDismissEndTarget();
            }
            else {
                dividerSnapAlgorithm$SnapTarget = this.mSplitLayout.getSnapAlgorithm().getDismissStartTarget();
            }
            this.mExitAnimationRunning = true;
            this.stopDragging(this.mExitStartPosition = this.getCurrentPosition(), dividerSnapAlgorithm$SnapTarget, 336L, 100L, 0L, Interpolators.FAST_OUT_SLOW_IN);
        }
    }
    
    void resizeSplitSurfaces(final SurfaceControl$Transaction surfaceControl$Transaction, final Rect rect, final Rect rect2) {
        this.resizeSplitSurfaces(surfaceControl$Transaction, rect, null, rect2, null);
    }
    
    void resizeStackSurfaces(final int n, int restrictDismissingTaskPosition, DividerSnapAlgorithm$SnapTarget closestDismissTarget) {
        if (this.mRemoved) {
            return;
        }
        this.calculateBoundsForPosition(n, this.mDockSide, this.mDockedRect);
        this.calculateBoundsForPosition(n, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherRect);
        if (this.mDockedRect.equals((Object)this.mLastResizeRect) && !this.mEntranceAnimationRunning) {
            return;
        }
        if (this.mBackground.getZ() > 0.0f) {
            this.mBackground.invalidate();
        }
        final SurfaceControl$Transaction transaction = this.mTiles.getTransaction();
        this.mLastResizeRect.set(this.mDockedRect);
        if (this.mHomeStackResizable && this.mIsInMinimizeInteraction) {
            this.calculateBoundsForPosition(this.mSnapTargetBeforeMinimized.position, this.mDockSide, this.mDockedTaskRect);
            this.calculateBoundsForPosition(this.mSnapTargetBeforeMinimized.position, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
            if (this.mDockSide == 3) {
                this.mDockedTaskRect.offset(Math.max(n, this.mStableInsets.left - this.mDividerSize) - this.mDockedTaskRect.left + this.mDividerSize, 0);
            }
            this.resizeSplitSurfaces(transaction, this.mDockedRect, this.mDockedTaskRect, this.mOtherRect, this.mOtherTaskRect);
            transaction.apply();
            this.mTiles.releaseTransaction(transaction);
            return;
        }
        if (this.mEntranceAnimationRunning && restrictDismissingTaskPosition != Integer.MAX_VALUE) {
            this.calculateBoundsForPosition(restrictDismissingTaskPosition, this.mDockSide, this.mDockedTaskRect);
            if (this.mDockSide == 3) {
                this.mDockedTaskRect.offset(Math.max(n, this.mStableInsets.left - this.mDividerSize) - this.mDockedTaskRect.left + this.mDividerSize, 0);
            }
            this.calculateBoundsForPosition(restrictDismissingTaskPosition, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
            this.resizeSplitSurfaces(transaction, this.mDockedRect, this.mDockedTaskRect, this.mOtherRect, this.mOtherTaskRect);
        }
        else if (this.mExitAnimationRunning && restrictDismissingTaskPosition != Integer.MAX_VALUE) {
            this.calculateBoundsForPosition(restrictDismissingTaskPosition, this.mDockSide, this.mDockedTaskRect);
            this.mDockedInsetRect.set(this.mDockedTaskRect);
            this.calculateBoundsForPosition(this.mExitStartPosition, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
            this.mOtherInsetRect.set(this.mOtherTaskRect);
            this.applyExitAnimationParallax(this.mOtherTaskRect, n);
            if (this.mDockSide == 3) {
                this.mDockedTaskRect.offset(n - this.mStableInsets.left + this.mDividerSize, 0);
            }
            this.resizeSplitSurfaces(transaction, this.mDockedRect, this.mDockedTaskRect, this.mOtherRect, this.mOtherTaskRect);
        }
        else if (restrictDismissingTaskPosition != Integer.MAX_VALUE) {
            this.calculateBoundsForPosition(n, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherRect);
            final int invertDockSide = DockedDividerUtils.invertDockSide(this.mDockSide);
            final int restrictDismissingTaskPosition2 = this.restrictDismissingTaskPosition(restrictDismissingTaskPosition, this.mDockSide, closestDismissTarget);
            restrictDismissingTaskPosition = this.restrictDismissingTaskPosition(restrictDismissingTaskPosition, invertDockSide, closestDismissTarget);
            this.calculateBoundsForPosition(restrictDismissingTaskPosition2, this.mDockSide, this.mDockedTaskRect);
            this.calculateBoundsForPosition(restrictDismissingTaskPosition, invertDockSide, this.mOtherTaskRect);
            this.mTmpRect.set(0, 0, this.mSplitLayout.mDisplayLayout.width(), this.mSplitLayout.mDisplayLayout.height());
            this.alignTopLeft(this.mDockedRect, this.mDockedTaskRect);
            this.alignTopLeft(this.mOtherRect, this.mOtherTaskRect);
            this.mDockedInsetRect.set(this.mDockedTaskRect);
            this.mOtherInsetRect.set(this.mOtherTaskRect);
            if (dockSideTopLeft(this.mDockSide)) {
                this.alignTopLeft(this.mTmpRect, this.mDockedInsetRect);
                this.alignBottomRight(this.mTmpRect, this.mOtherInsetRect);
            }
            else {
                this.alignBottomRight(this.mTmpRect, this.mDockedInsetRect);
                this.alignTopLeft(this.mTmpRect, this.mOtherInsetRect);
            }
            this.applyDismissingParallax(this.mDockedTaskRect, this.mDockSide, closestDismissTarget, n, restrictDismissingTaskPosition2);
            this.applyDismissingParallax(this.mOtherTaskRect, invertDockSide, closestDismissTarget, n, restrictDismissingTaskPosition);
            this.resizeSplitSurfaces(transaction, this.mDockedRect, this.mDockedTaskRect, this.mOtherRect, this.mOtherTaskRect);
        }
        else {
            this.resizeSplitSurfaces(transaction, this.mDockedRect, null, this.mOtherRect, null);
        }
        closestDismissTarget = this.getSnapAlgorithm().getClosestDismissTarget(n);
        this.setResizeDimLayer(transaction, this.isDismissTargetPrimary(closestDismissTarget), this.getDimFraction(n, closestDismissTarget));
        transaction.apply();
        this.mTiles.releaseTransaction(transaction);
    }
    
    public void setAdjustedForIme(final boolean mAdjustedForIme, final long n) {
        if (this.mAdjustedForIme == mAdjustedForIme) {
            return;
        }
        this.updateDockSide();
        final ViewPropertyAnimator setDuration = this.mHandle.animate().setInterpolator((TimeInterpolator)DividerView.IME_ADJUST_INTERPOLATOR).setDuration(n);
        final float n2 = 1.0f;
        float n3;
        if (mAdjustedForIme) {
            n3 = 0.0f;
        }
        else {
            n3 = 1.0f;
        }
        setDuration.alpha(n3).start();
        if (this.mDockSide == 2) {
            this.mBackground.setPivotY(0.0f);
            final ViewPropertyAnimator animate = this.mBackground.animate();
            float n4 = n2;
            if (mAdjustedForIme) {
                n4 = 0.5f;
            }
            animate.scaleY(n4);
        }
        if (!mAdjustedForIme) {
            this.mBackground.animate().withEndAction(this.mResetBackgroundRunnable);
        }
        this.mBackground.animate().setInterpolator((TimeInterpolator)DividerView.IME_ADJUST_INTERPOLATOR).setDuration(n).start();
        this.mAdjustedForIme = mAdjustedForIme;
    }
    
    void setHidden(final boolean mSurfaceHidden) {
        if (this.mSurfaceHidden == mSurfaceHidden) {
            return;
        }
        this.mSurfaceHidden = mSurfaceHidden;
        this.post((Runnable)new _$$Lambda$DividerView$c7dLKqbfWIqUSNmjfFAmTnyOe6A(this, mSurfaceHidden));
    }
    
    public void setMinimizedDockStack(final boolean b, final long duration, final boolean mHomeStackResizable) {
        this.mHomeStackResizable = mHomeStackResizable;
        this.updateDockSide();
        if (!mHomeStackResizable) {
            final ViewPropertyAnimator animate = this.mMinimizedShadow.animate();
            float n = 1.0f;
            float n2;
            if (b) {
                n2 = 1.0f;
            }
            else {
                n2 = 0.0f;
            }
            animate.alpha(n2).setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN).setDuration(duration).start();
            final ViewPropertyAnimator setDuration = this.mHandle.animate().setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).setDuration(duration);
            float n3;
            if (b) {
                n3 = 0.0f;
            }
            else {
                n3 = 1.0f;
            }
            setDuration.alpha(n3).start();
            final int mDockSide = this.mDockSide;
            if (mDockSide == 2) {
                this.mBackground.setPivotY(0.0f);
                final ViewPropertyAnimator animate2 = this.mBackground.animate();
                if (b) {
                    n = 0.0f;
                }
                animate2.scaleY(n);
            }
            else if (mDockSide == 1 || mDockSide == 3) {
                final View mBackground = this.mBackground;
                float pivotX;
                if (this.mDockSide == 1) {
                    pivotX = 0.0f;
                }
                else {
                    pivotX = (float)mBackground.getWidth();
                }
                mBackground.setPivotX(pivotX);
                final ViewPropertyAnimator animate3 = this.mBackground.animate();
                if (b) {
                    n = 0.0f;
                }
                animate3.scaleX(n);
            }
            this.mDockedStackMinimized = b;
        }
        else if (this.mDockedStackMinimized != b) {
            this.mIsInMinimizeInteraction = true;
            this.mDockedStackMinimized = b;
            int n4;
            if (b) {
                n4 = this.mSnapTargetBeforeMinimized.position;
            }
            else {
                n4 = this.getCurrentPosition();
            }
            DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget;
            if (b) {
                dividerSnapAlgorithm$SnapTarget = this.mSplitLayout.getMinimizedSnapAlgorithm().getMiddleTarget();
            }
            else {
                dividerSnapAlgorithm$SnapTarget = this.mSnapTargetBeforeMinimized;
            }
            this.stopDragging(n4, dividerSnapAlgorithm$SnapTarget, duration, Interpolators.FAST_OUT_SLOW_IN, 0L);
            this.setAdjustedForIme(false, duration);
        }
        if (!b) {
            this.mBackground.animate().withEndAction(this.mResetBackgroundRunnable);
        }
        this.mBackground.animate().setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).setDuration(duration).start();
    }
    
    public void setMinimizedDockStack(final boolean b, final boolean mHomeStackResizable) {
        this.mHomeStackResizable = mHomeStackResizable;
        this.updateDockSide();
        final float n = 0.0f;
        if (!b) {
            this.resetBackground();
        }
        else if (!mHomeStackResizable) {
            final int mDockSide = this.mDockSide;
            if (mDockSide == 2) {
                this.mBackground.setPivotY(0.0f);
                this.mBackground.setScaleY(0.0f);
            }
            else if (mDockSide == 1 || mDockSide == 3) {
                final View mBackground = this.mBackground;
                float pivotX;
                if (this.mDockSide == 1) {
                    pivotX = 0.0f;
                }
                else {
                    pivotX = (float)mBackground.getWidth();
                }
                mBackground.setPivotX(pivotX);
                this.mBackground.setScaleX(0.0f);
            }
        }
        final MinimizedDockShadow mMinimizedShadow = this.mMinimizedShadow;
        float alpha;
        if (b) {
            alpha = 1.0f;
        }
        else {
            alpha = 0.0f;
        }
        mMinimizedShadow.setAlpha(alpha);
        if (!mHomeStackResizable) {
            final DividerHandleView mHandle = this.mHandle;
            float alpha2;
            if (b) {
                alpha2 = n;
            }
            else {
                alpha2 = 1.0f;
            }
            mHandle.setAlpha(alpha2);
            this.mDockedStackMinimized = b;
        }
        else if (this.mDockedStackMinimized != b) {
            this.mDockedStackMinimized = b;
            if (this.mSplitLayout.mDisplayLayout.rotation() != this.mDefaultDisplay.getRotation()) {
                WindowManagerWrapper.getInstance().getStableInsets(this.mStableInsets);
                this.repositionSnapTargetBeforeMinimized();
            }
            if (this.mIsInMinimizeInteraction != b || this.mCurrentAnimator != null) {
                this.cancelFlingAnimation();
                if (b) {
                    this.requestLayout();
                    this.mIsInMinimizeInteraction = true;
                    this.resizeStackSurfaces(this.mSplitLayout.getMinimizedSnapAlgorithm().getMiddleTarget());
                }
                else {
                    this.resizeStackSurfaces(this.mSnapTargetBeforeMinimized);
                    this.mIsInMinimizeInteraction = false;
                }
            }
        }
    }
    
    void setResizeDimLayer(final SurfaceControl$Transaction surfaceControl$Transaction, final boolean b, final float n) {
        final SplitScreenTaskOrganizer mTiles = this.mTiles;
        SurfaceControl surfaceControl;
        if (b) {
            surfaceControl = mTiles.mPrimaryDim;
        }
        else {
            surfaceControl = mTiles.mSecondaryDim;
        }
        if (n <= 0.001f) {
            surfaceControl$Transaction.hide(surfaceControl);
        }
        else {
            surfaceControl$Transaction.setAlpha(surfaceControl, n);
            surfaceControl$Transaction.show(surfaceControl);
        }
    }
    
    public boolean startDragging(final boolean b, final boolean b2) {
        this.cancelFlingAnimation();
        if (b2) {
            this.mHandle.setTouching(true, b);
        }
        this.mDockSide = this.mSplitLayout.getPrimarySplitSide();
        this.mWindowManagerProxy.setResizing(true);
        if (b2) {
            this.mWindowManager.setSlippery(false);
            this.liftBackground();
        }
        final DividerCallbacks mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onDraggingStart();
        }
        return this.inSplitMode();
    }
    
    public void stopDragging(final int n, final float n2, final boolean b, final boolean b2) {
        this.mHandle.setTouching(false, true);
        this.fling(n, n2, b, b2);
        this.mWindowManager.setSlippery(true);
        this.releaseBackground();
    }
    
    public void stopDragging(final int n, final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget, final long n2, final long n3, final long n4, final Interpolator interpolator) {
        this.mHandle.setTouching(false, true);
        this.flingTo(n, dividerSnapAlgorithm$SnapTarget, n2, n3, n4, interpolator);
        this.mWindowManager.setSlippery(true);
        this.releaseBackground();
    }
    
    public void stopDragging(final int n, final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget, final long n2, final Interpolator interpolator) {
        this.stopDragging(n, dividerSnapAlgorithm$SnapTarget, n2, 0L, 0L, interpolator);
    }
    
    public void stopDragging(final int n, final DividerSnapAlgorithm$SnapTarget dividerSnapAlgorithm$SnapTarget, final long n2, final Interpolator interpolator, final long n3) {
        this.stopDragging(n, dividerSnapAlgorithm$SnapTarget, n2, 0L, n3, interpolator);
    }
    
    public interface DividerCallbacks
    {
        void growRecents();
        
        void onDraggingEnd();
        
        void onDraggingStart();
    }
}
