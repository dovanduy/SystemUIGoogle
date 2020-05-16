// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.util.Property;
import android.animation.ObjectAnimator;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.content.res.Resources;
import android.view.ViewConfiguration;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.content.Context;
import android.view.VelocityTracker;
import android.os.Handler;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.plugins.FalsingManager;
import android.animation.Animator;
import android.util.ArrayMap;
import android.view.View;

public class SwipeHelper implements Gefingerpoken
{
    private final Callback mCallback;
    private boolean mCanCurrViewBeDimissed;
    private View mCurrView;
    private float mDensityScale;
    private boolean mDisableHwLayers;
    private final ArrayMap<View, Animator> mDismissPendingMap;
    private final float[] mDownLocation;
    private boolean mDragging;
    private final boolean mFadeDependingOnAmountSwiped;
    private final FalsingManager mFalsingManager;
    private final int mFalsingThreshold;
    private final FlingAnimationUtils mFlingAnimationUtils;
    protected final Handler mHandler;
    private float mInitialTouchPos;
    private boolean mLongPressSent;
    private final long mLongPressTimeout;
    private float mMaxSwipeProgress;
    private boolean mMenuRowIntercepting;
    private float mMinSwipeProgress;
    private float mPagingTouchSlop;
    private final Runnable mPerformLongPress;
    private float mPerpendicularInitialTouchPos;
    private final float mSlopMultiplier;
    private boolean mSnappingChild;
    private final int mSwipeDirection;
    private boolean mTouchAboveFalsingThreshold;
    private float mTranslation;
    private final VelocityTracker mVelocityTracker;
    
    public SwipeHelper(final int mSwipeDirection, final Callback mCallback, final Context context, final FalsingManager mFalsingManager) {
        this.mMinSwipeProgress = 0.0f;
        this.mMaxSwipeProgress = 1.0f;
        this.mTranslation = 0.0f;
        this.mDownLocation = new float[2];
        this.mPerformLongPress = new Runnable() {
            private final int[] mViewOffset = new int[2];
            
            @Override
            public void run() {
                if (SwipeHelper.this.mCurrView != null && !SwipeHelper.this.mLongPressSent) {
                    SwipeHelper.this.mLongPressSent = true;
                    if (SwipeHelper.this.mCurrView instanceof ExpandableNotificationRow) {
                        SwipeHelper.this.mCurrView.getLocationOnScreen(this.mViewOffset);
                        final int n = (int)SwipeHelper.this.mDownLocation[0];
                        final int n2 = this.mViewOffset[0];
                        final int n3 = (int)SwipeHelper.this.mDownLocation[1];
                        final int n4 = this.mViewOffset[1];
                        SwipeHelper.this.mCurrView.sendAccessibilityEvent(2);
                        ((ExpandableNotificationRow)SwipeHelper.this.mCurrView).doLongClickCallback(n - n2, n3 - n4);
                    }
                }
            }
        };
        this.mDismissPendingMap = (ArrayMap<View, Animator>)new ArrayMap();
        this.mCallback = mCallback;
        this.mHandler = new Handler();
        this.mSwipeDirection = mSwipeDirection;
        this.mVelocityTracker = VelocityTracker.obtain();
        final ViewConfiguration value = ViewConfiguration.get(context);
        this.mPagingTouchSlop = (float)value.getScaledPagingTouchSlop();
        this.mSlopMultiplier = value.getScaledAmbiguousGestureMultiplier();
        this.mLongPressTimeout = (long)(ViewConfiguration.getLongPressTimeout() * 1.5f);
        final Resources resources = context.getResources();
        this.mDensityScale = resources.getDisplayMetrics().density;
        this.mFalsingThreshold = resources.getDimensionPixelSize(R$dimen.swipe_helper_falsing_threshold);
        this.mFadeDependingOnAmountSwiped = resources.getBoolean(R$bool.config_fadeDependingOnAmountSwiped);
        this.mFalsingManager = mFalsingManager;
        this.mFlingAnimationUtils = new FlingAnimationUtils(resources.getDisplayMetrics(), this.getMaxEscapeAnimDuration() / 1000.0f);
    }
    
    private int getFalsingThreshold() {
        return (int)(this.mFalsingThreshold * this.mCallback.getFalsingThresholdFactor());
    }
    
    private float getMaxVelocity() {
        return this.mDensityScale * 4000.0f;
    }
    
    private float getPerpendicularPos(final MotionEvent motionEvent) {
        float n;
        if (this.mSwipeDirection == 0) {
            n = motionEvent.getY();
        }
        else {
            n = motionEvent.getX();
        }
        return n;
    }
    
    private float getPos(final MotionEvent motionEvent) {
        float n;
        if (this.mSwipeDirection == 0) {
            n = motionEvent.getX();
        }
        else {
            n = motionEvent.getY();
        }
        return n;
    }
    
    private float getSwipeAlpha(final float n) {
        if (this.mFadeDependingOnAmountSwiped) {
            return Math.max(1.0f - n, 0.0f);
        }
        return 1.0f - Math.max(0.0f, Math.min(1.0f, n / 0.5f));
    }
    
    private float getSwipeProgressForOffset(final View view, float abs) {
        abs = Math.abs(abs / this.getSize(view));
        return Math.min(Math.max(this.mMinSwipeProgress, abs), this.mMaxSwipeProgress);
    }
    
    private float getVelocity(final VelocityTracker velocityTracker) {
        float n;
        if (this.mSwipeDirection == 0) {
            n = velocityTracker.getXVelocity();
        }
        else {
            n = velocityTracker.getYVelocity();
        }
        return n;
    }
    
    public static void invalidateGlobalRegion(final View view) {
        invalidateGlobalRegion(view, new RectF((float)view.getLeft(), (float)view.getTop(), (float)view.getRight(), (float)view.getBottom()));
    }
    
    public static void invalidateGlobalRegion(View view, final RectF rectF) {
        while (view.getParent() != null && view.getParent() instanceof View) {
            view = (View)view.getParent();
            view.getMatrix().mapRect(rectF);
            view.invalidate((int)Math.floor(rectF.left), (int)Math.floor(rectF.top), (int)Math.ceil(rectF.right), (int)Math.ceil(rectF.bottom));
        }
    }
    
    private void snapChildInstantly(final View view) {
        final boolean canChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        this.setTranslation(view, 0.0f);
        this.updateSwipeProgressFromOffset(view, canChildBeDismissed);
    }
    
    private void updateSwipeProgressFromOffset(final View view, final boolean b) {
        this.updateSwipeProgressFromOffset(view, b, this.getTranslation(view));
    }
    
    private void updateSwipeProgressFromOffset(final View view, final boolean b, float swipeProgressForOffset) {
        swipeProgressForOffset = this.getSwipeProgressForOffset(view, swipeProgressForOffset);
        if (!this.mCallback.updateSwipeProgress(view, b, swipeProgressForOffset) && b) {
            if (!this.mDisableHwLayers) {
                if (swipeProgressForOffset != 0.0f && swipeProgressForOffset != 1.0f) {
                    view.setLayerType(2, (Paint)null);
                }
                else {
                    view.setLayerType(0, (Paint)null);
                }
            }
            view.setAlpha(this.getSwipeAlpha(swipeProgressForOffset));
        }
        invalidateGlobalRegion(view);
    }
    
    public void cancelLongPress() {
        this.mHandler.removeCallbacks(this.mPerformLongPress);
    }
    
    protected ObjectAnimator createTranslationAnimation(final View view, final float n) {
        Property property;
        if (this.mSwipeDirection == 0) {
            property = View.TRANSLATION_X;
        }
        else {
            property = View.TRANSLATION_Y;
        }
        return ObjectAnimator.ofFloat((Object)view, property, new float[] { n });
    }
    
    public void dismissChild(final View view, final float n, final Runnable runnable, final long startDelay, final boolean b, long min, final boolean b2) {
        final boolean canChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        final int layoutDirection = view.getLayoutDirection();
        final int n2 = 0;
        final boolean b3 = layoutDirection == 1;
        final float n3 = fcmpl(n, 0.0f);
        final boolean b4 = n3 == 0 && (this.getTranslation(view) == 0.0f || b2) && this.mSwipeDirection == 1;
        final boolean b5 = n3 == 0 && (this.getTranslation(view) == 0.0f || b2) && b3;
        int n4 = 0;
        Label_0156: {
            if (Math.abs(n) <= this.getEscapeVelocity() || n >= 0.0f) {
                n4 = n2;
                if (this.getTranslation(view) >= 0.0f) {
                    break Label_0156;
                }
                n4 = n2;
                if (b2) {
                    break Label_0156;
                }
            }
            n4 = 1;
        }
        float size;
        if (n4 == 0 && !b5 && !b4) {
            size = this.getSize(view);
        }
        else {
            size = -this.getSize(view);
        }
        if (min == 0L) {
            if (n3 != 0) {
                min = Math.min(400L, (int)(Math.abs(size - this.getTranslation(view)) * 1000.0f / Math.abs(n)));
            }
            else {
                min = 200L;
            }
        }
        if (!this.mDisableHwLayers) {
            view.setLayerType(2, (Paint)null);
        }
        final Animator viewTranslationAnimator = this.getViewTranslationAnimator(view, size, (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                SwipeHelper.this.onTranslationUpdate(view, (float)valueAnimator.getAnimatedValue(), canChildBeDismissed);
            }
        });
        if (viewTranslationAnimator == null) {
            return;
        }
        if (b) {
            viewTranslationAnimator.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_LINEAR_IN);
            viewTranslationAnimator.setDuration(min);
        }
        else {
            this.mFlingAnimationUtils.applyDismissing(viewTranslationAnimator, this.getTranslation(view), size, n, this.getSize(view));
        }
        if (startDelay > 0L) {
            viewTranslationAnimator.setStartDelay(startDelay);
        }
        viewTranslationAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                SwipeHelper.this.updateSwipeProgressFromOffset(view, canChildBeDismissed);
                SwipeHelper.this.mDismissPendingMap.remove((Object)view);
                final View val$animView = view;
                final boolean b = val$animView instanceof ExpandableNotificationRow && ((ExpandableNotificationRow)val$animView).isRemoved();
                if (!this.mCancelled || b) {
                    SwipeHelper.this.mCallback.onChildDismissed(view);
                }
                final Runnable val$endAction = runnable;
                if (val$endAction != null) {
                    val$endAction.run();
                }
                if (!SwipeHelper.this.mDisableHwLayers) {
                    view.setLayerType(0, (Paint)null);
                }
            }
        });
        this.prepareDismissAnimation(view, viewTranslationAnimator);
        this.mDismissPendingMap.put((Object)view, (Object)viewTranslationAnimator);
        viewTranslationAnimator.start();
    }
    
    public void dismissChild(final View view, final float n, final boolean b) {
        this.dismissChild(view, n, null, 0L, b, 0L, false);
    }
    
    protected float getEscapeVelocity() {
        return this.getUnscaledEscapeVelocity() * this.mDensityScale;
    }
    
    protected long getMaxEscapeAnimDuration() {
        return 400L;
    }
    
    protected float getSize(final View view) {
        int n;
        if (this.mSwipeDirection == 0) {
            n = view.getMeasuredWidth();
        }
        else {
            n = view.getMeasuredHeight();
        }
        return (float)n;
    }
    
    protected abstract float getTranslation(final View p0);
    
    protected float getUnscaledEscapeVelocity() {
        return 500.0f;
    }
    
    protected Animator getViewTranslationAnimator(final View view, final float n, final ValueAnimator$AnimatorUpdateListener valueAnimator$AnimatorUpdateListener) {
        final ObjectAnimator translationAnimation = this.createTranslationAnimation(view, n);
        if (valueAnimator$AnimatorUpdateListener != null) {
            translationAnimation.addUpdateListener(valueAnimator$AnimatorUpdateListener);
        }
        return (Animator)translationAnimation;
    }
    
    protected abstract boolean handleUpEvent(final MotionEvent p0, final View p1, final float p2, final float p3);
    
    public boolean isDismissGesture(final MotionEvent motionEvent) {
        final float translation = this.getTranslation(this.mCurrView);
        final int actionMasked = motionEvent.getActionMasked();
        boolean b2;
        final boolean b = b2 = false;
        if (actionMasked == 1) {
            b2 = b;
            if (!this.mFalsingManager.isUnlockingDisabled()) {
                b2 = b;
                if (!this.isFalseGesture(motionEvent)) {
                    if (!this.swipedFastEnough()) {
                        b2 = b;
                        if (!this.swipedFarEnough()) {
                            return b2;
                        }
                    }
                    final Callback mCallback = this.mCallback;
                    final View mCurrView = this.mCurrView;
                    final boolean b3 = translation > 0.0f;
                    b2 = b;
                    if (mCallback.canChildBeDismissedInDirection(mCurrView, b3)) {
                        b2 = true;
                    }
                }
            }
        }
        return b2;
    }
    
    public boolean isFalseGesture(final MotionEvent motionEvent) {
        final boolean antiFalsingNeeded = this.mCallback.isAntiFalsingNeeded();
        final boolean classifierEnabled = this.mFalsingManager.isClassifierEnabled();
        boolean b = true;
        if (classifierEnabled) {
            if (antiFalsingNeeded && this.mFalsingManager.isFalseTouch()) {
                return b;
            }
        }
        else if (antiFalsingNeeded && !this.mTouchAboveFalsingThreshold) {
            return b;
        }
        b = false;
        return b;
    }
    
    protected abstract void onChildSnappedBack(final View p0, final float p1);
    
    public abstract void onDownUpdate(final View p0, final MotionEvent p1);
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final View mCurrView = this.mCurrView;
        if (mCurrView instanceof ExpandableNotificationRow) {
            final NotificationMenuRowPlugin provider = ((ExpandableNotificationRow)mCurrView).getProvider();
            if (provider != null) {
                this.mMenuRowIntercepting = provider.onInterceptTouchEvent(this.mCurrView, motionEvent);
            }
        }
        final int action = motionEvent.getAction();
        final boolean b = true;
        Label_0481: {
            if (action != 0) {
                if (action != 1) {
                    if (action != 2) {
                        if (action != 3) {
                            break Label_0481;
                        }
                    }
                    else {
                        if (this.mCurrView == null || this.mLongPressSent) {
                            break Label_0481;
                        }
                        this.mVelocityTracker.addMovement(motionEvent);
                        final float pos = this.getPos(motionEvent);
                        final float perpendicularPos = this.getPerpendicularPos(motionEvent);
                        final float n = pos - this.mInitialTouchPos;
                        final float mPerpendicularInitialTouchPos = this.mPerpendicularInitialTouchPos;
                        float mPagingTouchSlop;
                        if (motionEvent.getClassification() == 1) {
                            mPagingTouchSlop = this.mPagingTouchSlop * this.mSlopMultiplier;
                        }
                        else {
                            mPagingTouchSlop = this.mPagingTouchSlop;
                        }
                        if (Math.abs(n) > mPagingTouchSlop && Math.abs(n) > Math.abs(perpendicularPos - mPerpendicularInitialTouchPos)) {
                            if (this.mCallback.canChildBeDragged(this.mCurrView)) {
                                this.mCallback.onBeginDrag(this.mCurrView);
                                this.mDragging = true;
                                this.mInitialTouchPos = this.getPos(motionEvent);
                                this.mTranslation = this.getTranslation(this.mCurrView);
                            }
                            this.cancelLongPress();
                            break Label_0481;
                        }
                        if (motionEvent.getClassification() == 2 && this.mHandler.hasCallbacks(this.mPerformLongPress)) {
                            this.cancelLongPress();
                            this.mPerformLongPress.run();
                        }
                        break Label_0481;
                    }
                }
                final boolean b2 = this.mDragging || this.mLongPressSent || this.mMenuRowIntercepting;
                this.mDragging = false;
                this.mCurrView = null;
                this.mLongPressSent = false;
                this.mMenuRowIntercepting = false;
                this.cancelLongPress();
                if (b2) {
                    return true;
                }
            }
            else {
                this.mTouchAboveFalsingThreshold = false;
                this.mDragging = false;
                this.mSnappingChild = false;
                this.mLongPressSent = false;
                this.mVelocityTracker.clear();
                final View childAtPosition = this.mCallback.getChildAtPosition(motionEvent);
                if ((this.mCurrView = childAtPosition) != null) {
                    this.onDownUpdate(childAtPosition, motionEvent);
                    this.mCanCurrViewBeDimissed = this.mCallback.canChildBeDismissed(this.mCurrView);
                    this.mVelocityTracker.addMovement(motionEvent);
                    this.mInitialTouchPos = this.getPos(motionEvent);
                    this.mPerpendicularInitialTouchPos = this.getPerpendicularPos(motionEvent);
                    this.mTranslation = this.getTranslation(this.mCurrView);
                    this.mDownLocation[0] = motionEvent.getRawX();
                    this.mDownLocation[1] = motionEvent.getRawY();
                    this.mHandler.postDelayed(this.mPerformLongPress, this.mLongPressTimeout);
                }
            }
        }
        boolean b3 = b;
        if (!this.mDragging) {
            b3 = b;
            if (!this.mLongPressSent) {
                b3 = (this.mMenuRowIntercepting && b);
            }
        }
        return b3;
    }
    
    protected abstract void onMoveUpdate(final View p0, final MotionEvent p1, final float p2, final float p3);
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.mLongPressSent && !this.mMenuRowIntercepting) {
            return true;
        }
        final boolean mDragging = this.mDragging;
        boolean b = false;
        if (mDragging || this.mMenuRowIntercepting) {
            this.mVelocityTracker.addMovement(motionEvent);
            final int action = motionEvent.getAction();
            Label_0341: {
                if (action != 1) {
                    if (action != 2) {
                        if (action == 3) {
                            break Label_0341;
                        }
                        if (action != 4) {
                            return true;
                        }
                    }
                    if (this.mCurrView != null) {
                        final float n = this.getPos(motionEvent) - this.mInitialTouchPos;
                        final float abs = Math.abs(n);
                        if (abs >= this.getFalsingThreshold()) {
                            this.mTouchAboveFalsingThreshold = true;
                        }
                        final Callback mCallback = this.mCallback;
                        final View mCurrView = this.mCurrView;
                        final float n2 = fcmpl(n, 0.0f);
                        if (n2 > 0) {
                            b = true;
                        }
                        float n3 = n;
                        if (!mCallback.canChildBeDismissedInDirection(mCurrView, b)) {
                            final float size = this.getSize(this.mCurrView);
                            final float n4 = 0.3f * size;
                            if (abs >= size) {
                                if (n2 > 0) {
                                    n3 = n4;
                                }
                                else {
                                    n3 = -n4;
                                }
                            }
                            else {
                                final float n5 = (float)this.mCallback.getConstrainSwipeStartPosition();
                                n3 = n;
                                if (abs > n5) {
                                    final float n6 = (float)(int)(n5 * Math.signum(n));
                                    n3 = n6 + n4 * (float)Math.sin((n - n6) / size * 1.5707963267948966);
                                }
                            }
                        }
                        this.setTranslation(this.mCurrView, this.mTranslation + n3);
                        this.updateSwipeProgressFromOffset(this.mCurrView, this.mCanCurrViewBeDimissed);
                        this.onMoveUpdate(this.mCurrView, motionEvent, this.mTranslation + n3, n3);
                        return true;
                    }
                    return true;
                }
            }
            if (this.mCurrView != null) {
                this.mVelocityTracker.computeCurrentVelocity(1000, this.getMaxVelocity());
                final float velocity = this.getVelocity(this.mVelocityTracker);
                final View mCurrView2 = this.mCurrView;
                if (!this.handleUpEvent(motionEvent, mCurrView2, velocity, this.getTranslation(mCurrView2))) {
                    if (this.isDismissGesture(motionEvent)) {
                        this.dismissChild(this.mCurrView, velocity, this.swipedFastEnough() ^ true);
                    }
                    else {
                        this.mCallback.onDragCancelled(this.mCurrView);
                        this.snapChild(this.mCurrView, 0.0f, velocity);
                    }
                    this.mCurrView = null;
                }
                this.mDragging = false;
            }
            return true;
        }
        if (this.mCallback.getChildAtPosition(motionEvent) != null) {
            this.onInterceptTouchEvent(motionEvent);
            return true;
        }
        this.cancelLongPress();
        return false;
    }
    
    public void onTranslationUpdate(final View view, final float n, final boolean b) {
        this.updateSwipeProgressFromOffset(view, b, n);
    }
    
    protected void prepareDismissAnimation(final View view, final Animator animator) {
    }
    
    protected void prepareSnapBackAnimation(final View view, final Animator animator) {
    }
    
    public void setDensityScale(final float mDensityScale) {
        this.mDensityScale = mDensityScale;
    }
    
    public void setPagingTouchSlop(final float mPagingTouchSlop) {
        this.mPagingTouchSlop = mPagingTouchSlop;
    }
    
    protected abstract void setTranslation(final View p0, final float p1);
    
    public void snapChild(final View view, final float n, final float n2) {
        final boolean canChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        final Animator viewTranslationAnimator = this.getViewTranslationAnimator(view, n, (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                SwipeHelper.this.onTranslationUpdate(view, (float)valueAnimator.getAnimatedValue(), canChildBeDismissed);
            }
        });
        if (viewTranslationAnimator == null) {
            return;
        }
        viewTranslationAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            boolean wasCancelled = false;
            
            public void onAnimationCancel(final Animator animator) {
                this.wasCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                SwipeHelper.this.mSnappingChild = false;
                if (!this.wasCancelled) {
                    SwipeHelper.this.updateSwipeProgressFromOffset(view, canChildBeDismissed);
                    SwipeHelper.this.onChildSnappedBack(view, n);
                    SwipeHelper.this.mCallback.onChildSnappedBack(view, n);
                }
            }
        });
        this.prepareSnapBackAnimation(view, viewTranslationAnimator);
        this.mSnappingChild = true;
        this.mFlingAnimationUtils.apply(viewTranslationAnimator, this.getTranslation(view), n, n2, Math.abs(n - this.getTranslation(view)));
        viewTranslationAnimator.start();
    }
    
    public void snapChildIfNeeded(final View view, final boolean b, final float n) {
        if ((this.mDragging && this.mCurrView == view) || this.mSnappingChild) {
            return;
        }
        boolean b2 = false;
        final Animator animator = (Animator)this.mDismissPendingMap.get((Object)view);
        Label_0068: {
            if (animator != null) {
                animator.cancel();
            }
            else if (this.getTranslation(view) == 0.0f) {
                break Label_0068;
            }
            b2 = true;
        }
        if (b2) {
            if (b) {
                this.snapChild(view, n, 0.0f);
            }
            else {
                this.snapChildInstantly(view);
            }
        }
    }
    
    protected boolean swipedFarEnough() {
        return Math.abs(this.getTranslation(this.mCurrView)) > this.getSize(this.mCurrView) * 0.6f;
    }
    
    protected boolean swipedFastEnough() {
        final float velocity = this.getVelocity(this.mVelocityTracker);
        final float translation = this.getTranslation(this.mCurrView);
        final float abs = Math.abs(velocity);
        final float escapeVelocity = this.getEscapeVelocity();
        boolean b = true;
        if (abs <= escapeVelocity || velocity > 0.0f != translation > 0.0f) {
            b = false;
        }
        return b;
    }
    
    public interface Callback
    {
        boolean canChildBeDismissed(final View p0);
        
        default boolean canChildBeDismissedInDirection(final View view, final boolean b) {
            return this.canChildBeDismissed(view);
        }
        
        default boolean canChildBeDragged(final View view) {
            return true;
        }
        
        View getChildAtPosition(final MotionEvent p0);
        
        default int getConstrainSwipeStartPosition() {
            return 0;
        }
        
        float getFalsingThresholdFactor();
        
        boolean isAntiFalsingNeeded();
        
        void onBeginDrag(final View p0);
        
        void onChildDismissed(final View p0);
        
        void onChildSnappedBack(final View p0, final float p1);
        
        void onDragCancelled(final View p0);
        
        boolean updateSwipeProgress(final View p0, final boolean p1, final float p2);
    }
}
