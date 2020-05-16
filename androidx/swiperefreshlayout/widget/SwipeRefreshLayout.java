// 
// Decompiled by Procyon v0.5.36
// 

package androidx.swiperefreshlayout.widget;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.view.View$BaseSavedState;
import android.view.ViewParent;
import androidx.core.view.ViewCompat;
import android.widget.AbsListView;
import android.os.Build$VERSION;
import android.os.Parcelable;
import android.view.View$MeasureSpec;
import android.util.Log;
import androidx.core.widget.ListViewCompat;
import android.widget.ListView;
import android.view.MotionEvent;
import android.graphics.drawable.Drawable;
import android.view.animation.Interpolator;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;
import android.view.animation.Transformation;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation$AnimationListener;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.NestedScrollingChildHelper;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParent3;
import android.view.ViewGroup;

public class SwipeRefreshLayout extends ViewGroup implements NestedScrollingParent3, NestedScrollingParent2, NestedScrollingChild, NestedScrollingParent
{
    static final int CIRCLE_DIAMETER = 40;
    static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final int[] LAYOUT_ATTRS;
    private static final String LOG_TAG;
    private int mActivePointerId;
    private Animation mAlphaMaxAnimation;
    private Animation mAlphaStartAnimation;
    private final Animation mAnimateToCorrectPosition;
    private final Animation mAnimateToStartPosition;
    private OnChildScrollUpCallback mChildScrollUpCallback;
    private int mCircleDiameter;
    CircleImageView mCircleView;
    private int mCircleViewIndex;
    int mCurrentTargetOffsetTop;
    int mCustomSlingshotDistance;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private boolean mEnableLegacyRequestDisallowInterceptTouch;
    protected int mFrom;
    private float mInitialDownY;
    private float mInitialMotionY;
    private boolean mIsBeingDragged;
    OnRefreshListener mListener;
    private int mMediumAnimationDuration;
    private boolean mNestedScrollInProgress;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final int[] mNestedScrollingV2ConsumedCompat;
    boolean mNotify;
    protected int mOriginalOffsetTop;
    private final int[] mParentOffsetInWindow;
    private final int[] mParentScrollConsumed;
    CircularProgressDrawable mProgress;
    private Animation$AnimationListener mRefreshListener;
    boolean mRefreshing;
    private boolean mReturningToStart;
    boolean mScale;
    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mScaleDownToStartAnimation;
    int mSpinnerOffsetEnd;
    float mStartingScale;
    private View mTarget;
    private float mTotalDragDistance;
    private float mTotalUnconsumed;
    private int mTouchSlop;
    boolean mUsingCustomStart;
    
    static {
        LOG_TAG = SwipeRefreshLayout.class.getSimpleName();
        LAYOUT_ATTRS = new int[] { 16842766 };
    }
    
    public SwipeRefreshLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mRefreshing = false;
        this.mTotalDragDistance = -1.0f;
        this.mParentScrollConsumed = new int[2];
        this.mParentOffsetInWindow = new int[2];
        this.mNestedScrollingV2ConsumedCompat = new int[2];
        this.mActivePointerId = -1;
        this.mCircleViewIndex = -1;
        this.mRefreshListener = (Animation$AnimationListener)new Animation$AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                final SwipeRefreshLayout this$0 = SwipeRefreshLayout.this;
                if (this$0.mRefreshing) {
                    this$0.mProgress.setAlpha(255);
                    SwipeRefreshLayout.this.mProgress.start();
                    final SwipeRefreshLayout this$2 = SwipeRefreshLayout.this;
                    if (this$2.mNotify) {
                        final OnRefreshListener mListener = this$2.mListener;
                        if (mListener != null) {
                            mListener.onRefresh();
                        }
                    }
                    final SwipeRefreshLayout this$3 = SwipeRefreshLayout.this;
                    this$3.mCurrentTargetOffsetTop = this$3.mCircleView.getTop();
                }
                else {
                    this$0.reset();
                }
            }
            
            public void onAnimationRepeat(final Animation animation) {
            }
            
            public void onAnimationStart(final Animation animation) {
            }
        };
        this.mAnimateToCorrectPosition = new Animation() {
            public void applyTransformation(final float n, final Transformation transformation) {
                final SwipeRefreshLayout this$0 = SwipeRefreshLayout.this;
                int mSpinnerOffsetEnd;
                if (!this$0.mUsingCustomStart) {
                    mSpinnerOffsetEnd = this$0.mSpinnerOffsetEnd - Math.abs(this$0.mOriginalOffsetTop);
                }
                else {
                    mSpinnerOffsetEnd = this$0.mSpinnerOffsetEnd;
                }
                final SwipeRefreshLayout this$2 = SwipeRefreshLayout.this;
                final int mFrom = this$2.mFrom;
                SwipeRefreshLayout.this.setTargetOffsetTopAndBottom(mFrom + (int)((mSpinnerOffsetEnd - mFrom) * n) - this$2.mCircleView.getTop());
                SwipeRefreshLayout.this.mProgress.setArrowScale(1.0f - n);
            }
        };
        this.mAnimateToStartPosition = new Animation() {
            public void applyTransformation(final float n, final Transformation transformation) {
                SwipeRefreshLayout.this.moveToStart(n);
            }
        };
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMediumAnimationDuration = this.getResources().getInteger(17694721);
        this.setWillNotDraw(false);
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.0f);
        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        this.mCircleDiameter = (int)(displayMetrics.density * 40.0f);
        this.createProgressView();
        this.setChildrenDrawingOrderEnabled(true);
        final int mSpinnerOffsetEnd = (int)(displayMetrics.density * 64.0f);
        this.mSpinnerOffsetEnd = mSpinnerOffsetEnd;
        this.mTotalDragDistance = (float)mSpinnerOffsetEnd;
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.mNestedScrollingChildHelper = new NestedScrollingChildHelper((View)this);
        this.setNestedScrollingEnabled(true);
        final int n = -this.mCircleDiameter;
        this.mCurrentTargetOffsetTop = n;
        this.mOriginalOffsetTop = n;
        this.moveToStart(1.0f);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, SwipeRefreshLayout.LAYOUT_ATTRS);
        this.setEnabled(obtainStyledAttributes.getBoolean(0, true));
        obtainStyledAttributes.recycle();
    }
    
    private void animateOffsetToCorrectPosition(final int mFrom, final Animation$AnimationListener animationListener) {
        this.mFrom = mFrom;
        this.mAnimateToCorrectPosition.reset();
        this.mAnimateToCorrectPosition.setDuration(200L);
        this.mAnimateToCorrectPosition.setInterpolator((Interpolator)this.mDecelerateInterpolator);
        if (animationListener != null) {
            this.mCircleView.setAnimationListener(animationListener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mAnimateToCorrectPosition);
    }
    
    private void animateOffsetToStartPosition(final int mFrom, final Animation$AnimationListener animationListener) {
        if (this.mScale) {
            this.startScaleDownReturnToStartAnimation(mFrom, animationListener);
        }
        else {
            this.mFrom = mFrom;
            this.mAnimateToStartPosition.reset();
            this.mAnimateToStartPosition.setDuration(200L);
            this.mAnimateToStartPosition.setInterpolator((Interpolator)this.mDecelerateInterpolator);
            if (animationListener != null) {
                this.mCircleView.setAnimationListener(animationListener);
            }
            this.mCircleView.clearAnimation();
            this.mCircleView.startAnimation(this.mAnimateToStartPosition);
        }
    }
    
    private void createProgressView() {
        this.mCircleView = new CircleImageView(this.getContext());
        (this.mProgress = new CircularProgressDrawable(this.getContext())).setStyle(1);
        this.mCircleView.setImageDrawable((Drawable)this.mProgress);
        this.mCircleView.setVisibility(8);
        this.addView((View)this.mCircleView);
    }
    
    private void ensureTarget() {
        if (this.mTarget == null) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                final View child = this.getChildAt(i);
                if (!child.equals(this.mCircleView)) {
                    this.mTarget = child;
                    break;
                }
            }
        }
    }
    
    private void finishSpinner(final float n) {
        if (n > this.mTotalDragDistance) {
            this.setRefreshing(true, true);
        }
        else {
            this.mRefreshing = false;
            this.mProgress.setStartEndTrim(0.0f, 0.0f);
            Object o = null;
            if (!this.mScale) {
                o = new Animation$AnimationListener() {
                    public void onAnimationEnd(final Animation animation) {
                        final SwipeRefreshLayout this$0 = SwipeRefreshLayout.this;
                        if (!this$0.mScale) {
                            this$0.startScaleDownAnimation(null);
                        }
                    }
                    
                    public void onAnimationRepeat(final Animation animation) {
                    }
                    
                    public void onAnimationStart(final Animation animation) {
                    }
                };
            }
            this.animateOffsetToStartPosition(this.mCurrentTargetOffsetTop, (Animation$AnimationListener)o);
            this.mProgress.setArrowEnabled(false);
        }
    }
    
    private boolean isAnimationRunning(final Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }
    
    private void moveSpinner(final float a) {
        this.mProgress.setArrowEnabled(true);
        final float min = Math.min(1.0f, Math.abs(a / this.mTotalDragDistance));
        final float b = (float)Math.max(min - 0.4, 0.0) * 5.0f / 3.0f;
        final float abs = Math.abs(a);
        final float mTotalDragDistance = this.mTotalDragDistance;
        int n = this.mCustomSlingshotDistance;
        if (n <= 0) {
            if (this.mUsingCustomStart) {
                n = this.mSpinnerOffsetEnd - this.mOriginalOffsetTop;
            }
            else {
                n = this.mSpinnerOffsetEnd;
            }
        }
        final float n2 = (float)n;
        final double a2 = Math.max(0.0f, Math.min(abs - mTotalDragDistance, n2 * 2.0f) / n2) / 4.0f;
        final float n3 = (float)(a2 - Math.pow(a2, 2.0)) * 2.0f;
        final int mOriginalOffsetTop = this.mOriginalOffsetTop;
        final int n4 = (int)(n2 * min + n2 * n3 * 2.0f);
        if (this.mCircleView.getVisibility() != 0) {
            this.mCircleView.setVisibility(0);
        }
        if (!this.mScale) {
            this.mCircleView.setScaleX(1.0f);
            this.mCircleView.setScaleY(1.0f);
        }
        if (this.mScale) {
            this.setAnimationProgress(Math.min(1.0f, a / this.mTotalDragDistance));
        }
        if (a < this.mTotalDragDistance) {
            if (this.mProgress.getAlpha() > 76 && !this.isAnimationRunning(this.mAlphaStartAnimation)) {
                this.startProgressAlphaStartAnimation();
            }
        }
        else if (this.mProgress.getAlpha() < 255 && !this.isAnimationRunning(this.mAlphaMaxAnimation)) {
            this.startProgressAlphaMaxAnimation();
        }
        this.mProgress.setStartEndTrim(0.0f, Math.min(0.8f, b * 0.8f));
        this.mProgress.setArrowScale(Math.min(1.0f, b));
        this.mProgress.setProgressRotation((b * 0.4f - 0.25f + n3 * 2.0f) * 0.5f);
        this.setTargetOffsetTopAndBottom(mOriginalOffsetTop + n4 - this.mCurrentTargetOffsetTop);
    }
    
    private void onSecondaryPointerUp(final MotionEvent motionEvent) {
        final int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            int n;
            if (actionIndex == 0) {
                n = 1;
            }
            else {
                n = 0;
            }
            this.mActivePointerId = motionEvent.getPointerId(n);
        }
    }
    
    private void setColorViewAlpha(final int n) {
        this.mCircleView.getBackground().setAlpha(n);
        this.mProgress.setAlpha(n);
    }
    
    private void setRefreshing(final boolean mRefreshing, final boolean mNotify) {
        if (this.mRefreshing != mRefreshing) {
            this.mNotify = mNotify;
            this.ensureTarget();
            this.mRefreshing = mRefreshing;
            if (mRefreshing) {
                this.animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
            }
            else {
                this.startScaleDownAnimation(this.mRefreshListener);
            }
        }
    }
    
    private Animation startAlphaAnimation(final int n, final int n2) {
        final Animation animation = new Animation() {
            public void applyTransformation(final float n, final Transformation transformation) {
                final CircularProgressDrawable mProgress = SwipeRefreshLayout.this.mProgress;
                final int val$startingAlpha = n;
                mProgress.setAlpha((int)(val$startingAlpha + (n2 - val$startingAlpha) * n));
            }
        };
        animation.setDuration(300L);
        this.mCircleView.setAnimationListener(null);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation((Animation)animation);
        return animation;
    }
    
    private void startDragging(final float n) {
        final float mInitialDownY = this.mInitialDownY;
        final int mTouchSlop = this.mTouchSlop;
        if (n - mInitialDownY > mTouchSlop && !this.mIsBeingDragged) {
            this.mInitialMotionY = mInitialDownY + mTouchSlop;
            this.mIsBeingDragged = true;
            this.mProgress.setAlpha(76);
        }
    }
    
    private void startProgressAlphaMaxAnimation() {
        this.mAlphaMaxAnimation = this.startAlphaAnimation(this.mProgress.getAlpha(), 255);
    }
    
    private void startProgressAlphaStartAnimation() {
        this.mAlphaStartAnimation = this.startAlphaAnimation(this.mProgress.getAlpha(), 76);
    }
    
    private void startScaleDownReturnToStartAnimation(final int mFrom, final Animation$AnimationListener animationListener) {
        this.mFrom = mFrom;
        this.mStartingScale = this.mCircleView.getScaleX();
        (this.mScaleDownToStartAnimation = new Animation() {
            public void applyTransformation(final float n, final Transformation transformation) {
                final SwipeRefreshLayout this$0 = SwipeRefreshLayout.this;
                final float mStartingScale = this$0.mStartingScale;
                this$0.setAnimationProgress(mStartingScale + -mStartingScale * n);
                SwipeRefreshLayout.this.moveToStart(n);
            }
        }).setDuration(150L);
        if (animationListener != null) {
            this.mCircleView.setAnimationListener(animationListener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownToStartAnimation);
    }
    
    private void startScaleUpAnimation(final Animation$AnimationListener animationListener) {
        this.mCircleView.setVisibility(0);
        this.mProgress.setAlpha(255);
        (this.mScaleAnimation = new Animation() {
            public void applyTransformation(final float animationProgress, final Transformation transformation) {
                SwipeRefreshLayout.this.setAnimationProgress(animationProgress);
            }
        }).setDuration((long)this.mMediumAnimationDuration);
        if (animationListener != null) {
            this.mCircleView.setAnimationListener(animationListener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleAnimation);
    }
    
    public boolean canChildScrollUp() {
        final OnChildScrollUpCallback mChildScrollUpCallback = this.mChildScrollUpCallback;
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, this.mTarget);
        }
        final View mTarget = this.mTarget;
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView)mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }
    
    public boolean dispatchNestedFling(final float n, final float n2, final boolean b) {
        return this.mNestedScrollingChildHelper.dispatchNestedFling(n, n2, b);
    }
    
    public boolean dispatchNestedPreFling(final float n, final float n2) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreFling(n, n2);
    }
    
    public boolean dispatchNestedPreScroll(final int n, final int n2, final int[] array, final int[] array2) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreScroll(n, n2, array, array2);
    }
    
    public void dispatchNestedScroll(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5, final int[] array2) {
        if (n5 == 0) {
            this.mNestedScrollingChildHelper.dispatchNestedScroll(n, n2, n3, n4, array, n5, array2);
        }
    }
    
    public boolean dispatchNestedScroll(final int n, final int n2, final int n3, final int n4, final int[] array) {
        return this.mNestedScrollingChildHelper.dispatchNestedScroll(n, n2, n3, n4, array);
    }
    
    protected int getChildDrawingOrder(int n, final int n2) {
        final int mCircleViewIndex = this.mCircleViewIndex;
        if (mCircleViewIndex < 0) {
            return n2;
        }
        if (n2 == n - 1) {
            return mCircleViewIndex;
        }
        if ((n = n2) >= mCircleViewIndex) {
            n = n2 + 1;
        }
        return n;
    }
    
    public int getNestedScrollAxes() {
        return this.mNestedScrollingParentHelper.getNestedScrollAxes();
    }
    
    public boolean hasNestedScrollingParent() {
        return this.mNestedScrollingChildHelper.hasNestedScrollingParent();
    }
    
    public boolean isNestedScrollingEnabled() {
        return this.mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }
    
    void moveToStart(final float n) {
        final int mFrom = this.mFrom;
        this.setTargetOffsetTopAndBottom(mFrom + (int)((this.mOriginalOffsetTop - mFrom) * n) - this.mCircleView.getTop());
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.reset();
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        this.ensureTarget();
        final int actionMasked = motionEvent.getActionMasked();
        if (this.mReturningToStart && actionMasked == 0) {
            this.mReturningToStart = false;
        }
        if (this.isEnabled() && !this.mReturningToStart && !this.canChildScrollUp() && !this.mRefreshing && !this.mNestedScrollInProgress) {
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked != 2) {
                        if (actionMasked != 3) {
                            if (actionMasked != 6) {
                                return this.mIsBeingDragged;
                            }
                            this.onSecondaryPointerUp(motionEvent);
                            return this.mIsBeingDragged;
                        }
                    }
                    else {
                        final int mActivePointerId = this.mActivePointerId;
                        if (mActivePointerId == -1) {
                            Log.e(SwipeRefreshLayout.LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                            return false;
                        }
                        final int pointerIndex = motionEvent.findPointerIndex(mActivePointerId);
                        if (pointerIndex < 0) {
                            return false;
                        }
                        this.startDragging(motionEvent.getY(pointerIndex));
                        return this.mIsBeingDragged;
                    }
                }
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
            }
            else {
                this.setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCircleView.getTop());
                final int pointerId = motionEvent.getPointerId(0);
                this.mActivePointerId = pointerId;
                this.mIsBeingDragged = false;
                final int pointerIndex2 = motionEvent.findPointerIndex(pointerId);
                if (pointerIndex2 < 0) {
                    return false;
                }
                this.mInitialDownY = motionEvent.getY(pointerIndex2);
            }
            return this.mIsBeingDragged;
        }
        return false;
    }
    
    protected void onLayout(final boolean b, int measuredWidth, int n, int n2, int measuredHeight) {
        measuredWidth = this.getMeasuredWidth();
        measuredHeight = this.getMeasuredHeight();
        if (this.getChildCount() == 0) {
            return;
        }
        if (this.mTarget == null) {
            this.ensureTarget();
        }
        final View mTarget = this.mTarget;
        if (mTarget == null) {
            return;
        }
        n2 = this.getPaddingLeft();
        n = this.getPaddingTop();
        mTarget.layout(n2, n, measuredWidth - this.getPaddingLeft() - this.getPaddingRight() + n2, measuredHeight - this.getPaddingTop() - this.getPaddingBottom() + n);
        n2 = this.mCircleView.getMeasuredWidth();
        n = this.mCircleView.getMeasuredHeight();
        final CircleImageView mCircleView = this.mCircleView;
        measuredWidth /= 2;
        measuredHeight = n2 / 2;
        n2 = this.mCurrentTargetOffsetTop;
        mCircleView.layout(measuredWidth - measuredHeight, n2, measuredWidth + measuredHeight, n + n2);
    }
    
    public void onMeasure(int i, final int n) {
        super.onMeasure(i, n);
        if (this.mTarget == null) {
            this.ensureTarget();
        }
        final View mTarget = this.mTarget;
        if (mTarget == null) {
            return;
        }
        mTarget.measure(View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), 1073741824), View$MeasureSpec.makeMeasureSpec(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), 1073741824));
        this.mCircleView.measure(View$MeasureSpec.makeMeasureSpec(this.mCircleDiameter, 1073741824), View$MeasureSpec.makeMeasureSpec(this.mCircleDiameter, 1073741824));
        this.mCircleViewIndex = -1;
        for (i = 0; i < this.getChildCount(); ++i) {
            if (this.getChildAt(i) == this.mCircleView) {
                this.mCircleViewIndex = i;
                break;
            }
        }
    }
    
    public boolean onNestedFling(final View view, final float n, final float n2, final boolean b) {
        return this.dispatchNestedFling(n, n2, b);
    }
    
    public boolean onNestedPreFling(final View view, final float n, final float n2) {
        return this.dispatchNestedPreFling(n, n2);
    }
    
    public void onNestedPreScroll(final View view, final int n, final int n2, final int[] array) {
        if (n2 > 0) {
            final float mTotalUnconsumed = this.mTotalUnconsumed;
            if (mTotalUnconsumed > 0.0f) {
                final float n3 = (float)n2;
                if (n3 > mTotalUnconsumed) {
                    array[1] = (int)mTotalUnconsumed;
                    this.mTotalUnconsumed = 0.0f;
                }
                else {
                    this.mTotalUnconsumed = mTotalUnconsumed - n3;
                    array[1] = n2;
                }
                this.moveSpinner(this.mTotalUnconsumed);
            }
        }
        if (this.mUsingCustomStart && n2 > 0 && this.mTotalUnconsumed == 0.0f && Math.abs(n2 - array[1]) > 0) {
            this.mCircleView.setVisibility(8);
        }
        final int[] mParentScrollConsumed = this.mParentScrollConsumed;
        if (this.dispatchNestedPreScroll(n - array[0], n2 - array[1], mParentScrollConsumed, null)) {
            array[0] += mParentScrollConsumed[0];
            array[1] += mParentScrollConsumed[1];
        }
    }
    
    public void onNestedPreScroll(final View view, final int n, final int n2, final int[] array, final int n3) {
        if (n3 == 0) {
            this.onNestedPreScroll(view, n, n2, array);
        }
    }
    
    public void onNestedScroll(final View view, final int n, final int n2, final int n3, final int n4) {
        this.onNestedScroll(view, n, n2, n3, n4, 0, this.mNestedScrollingV2ConsumedCompat);
    }
    
    public void onNestedScroll(final View view, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.onNestedScroll(view, n, n2, n3, n4, n5, this.mNestedScrollingV2ConsumedCompat);
    }
    
    public void onNestedScroll(final View view, int a, int n, final int n2, final int n3, final int n4, final int[] array) {
        if (n4 != 0) {
            return;
        }
        final int n5 = array[1];
        this.dispatchNestedScroll(a, n, n2, n3, this.mParentOffsetInWindow, n4, array);
        n = n3 - (array[1] - n5);
        if (n == 0) {
            a = n3 + this.mParentOffsetInWindow[1];
        }
        else {
            a = n;
        }
        if (a < 0 && !this.canChildScrollUp()) {
            this.moveSpinner(this.mTotalUnconsumed += Math.abs(a));
            array[1] += n;
        }
    }
    
    public void onNestedScrollAccepted(final View view, final View view2, final int n) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(view, view2, n);
        this.startNestedScroll(n & 0x2);
        this.mTotalUnconsumed = 0.0f;
        this.mNestedScrollInProgress = true;
    }
    
    public void onNestedScrollAccepted(final View view, final View view2, final int n, final int n2) {
        if (n2 == 0) {
            this.onNestedScrollAccepted(view, view2, n);
        }
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.setRefreshing(savedState.mRefreshing);
    }
    
    protected Parcelable onSaveInstanceState() {
        return (Parcelable)new SavedState(super.onSaveInstanceState(), this.mRefreshing);
    }
    
    public boolean onStartNestedScroll(final View view, final View view2, final int n) {
        return this.isEnabled() && !this.mReturningToStart && !this.mRefreshing && (n & 0x2) != 0x0;
    }
    
    public boolean onStartNestedScroll(final View view, final View view2, final int n, final int n2) {
        return n2 == 0 && this.onStartNestedScroll(view, view2, n);
    }
    
    public void onStopNestedScroll(final View view) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(view);
        this.mNestedScrollInProgress = false;
        final float mTotalUnconsumed = this.mTotalUnconsumed;
        if (mTotalUnconsumed > 0.0f) {
            this.finishSpinner(mTotalUnconsumed);
            this.mTotalUnconsumed = 0.0f;
        }
        this.stopNestedScroll();
    }
    
    public void onStopNestedScroll(final View view, final int n) {
        if (n == 0) {
            this.onStopNestedScroll(view);
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final String log_TAG = SwipeRefreshLayout.LOG_TAG;
        final int actionMasked = motionEvent.getActionMasked();
        if (this.mReturningToStart && actionMasked == 0) {
            this.mReturningToStart = false;
        }
        if (this.isEnabled() && !this.mReturningToStart && !this.canChildScrollUp() && !this.mRefreshing && !this.mNestedScrollInProgress) {
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked != 2) {
                        if (actionMasked == 3) {
                            return false;
                        }
                        if (actionMasked != 5) {
                            if (actionMasked == 6) {
                                this.onSecondaryPointerUp(motionEvent);
                            }
                        }
                        else {
                            final int actionIndex = motionEvent.getActionIndex();
                            if (actionIndex < 0) {
                                Log.e(log_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                                return false;
                            }
                            this.mActivePointerId = motionEvent.getPointerId(actionIndex);
                        }
                    }
                    else {
                        final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (pointerIndex < 0) {
                            Log.e(log_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                            return false;
                        }
                        final float y = motionEvent.getY(pointerIndex);
                        this.startDragging(y);
                        if (this.mIsBeingDragged) {
                            final float n = (y - this.mInitialMotionY) * 0.5f;
                            if (n <= 0.0f) {
                                return false;
                            }
                            this.getParent().requestDisallowInterceptTouchEvent(true);
                            this.moveSpinner(n);
                        }
                    }
                }
                else {
                    final int pointerIndex2 = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex2 < 0) {
                        Log.e(log_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                        return false;
                    }
                    if (this.mIsBeingDragged) {
                        final float y2 = motionEvent.getY(pointerIndex2);
                        final float mInitialMotionY = this.mInitialMotionY;
                        this.mIsBeingDragged = false;
                        this.finishSpinner((y2 - mInitialMotionY) * 0.5f);
                    }
                    this.mActivePointerId = -1;
                    return false;
                }
            }
            else {
                this.mActivePointerId = motionEvent.getPointerId(0);
                this.mIsBeingDragged = false;
            }
            return true;
        }
        return false;
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean b) {
        if (Build$VERSION.SDK_INT >= 21 || !(this.mTarget instanceof AbsListView)) {
            final View mTarget = this.mTarget;
            if (mTarget == null || ViewCompat.isNestedScrollingEnabled(mTarget)) {
                super.requestDisallowInterceptTouchEvent(b);
                return;
            }
        }
        if (!this.mEnableLegacyRequestDisallowInterceptTouch) {
            final ViewParent parent = this.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(b);
            }
        }
    }
    
    void reset() {
        this.mCircleView.clearAnimation();
        this.mProgress.stop();
        this.mCircleView.setVisibility(8);
        this.setColorViewAlpha(255);
        if (this.mScale) {
            this.setAnimationProgress(0.0f);
        }
        else {
            this.setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCurrentTargetOffsetTop);
        }
        this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
    }
    
    void setAnimationProgress(final float n) {
        this.mCircleView.setScaleX(n);
        this.mCircleView.setScaleY(n);
    }
    
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            this.reset();
        }
    }
    
    public void setNestedScrollingEnabled(final boolean nestedScrollingEnabled) {
        this.mNestedScrollingChildHelper.setNestedScrollingEnabled(nestedScrollingEnabled);
    }
    
    public void setRefreshing(final boolean mRefreshing) {
        if (mRefreshing && this.mRefreshing != mRefreshing) {
            this.mRefreshing = mRefreshing;
            int mSpinnerOffsetEnd;
            if (!this.mUsingCustomStart) {
                mSpinnerOffsetEnd = this.mSpinnerOffsetEnd + this.mOriginalOffsetTop;
            }
            else {
                mSpinnerOffsetEnd = this.mSpinnerOffsetEnd;
            }
            this.setTargetOffsetTopAndBottom(mSpinnerOffsetEnd - this.mCurrentTargetOffsetTop);
            this.mNotify = false;
            this.startScaleUpAnimation(this.mRefreshListener);
        }
        else {
            this.setRefreshing(mRefreshing, false);
        }
    }
    
    void setTargetOffsetTopAndBottom(final int n) {
        this.mCircleView.bringToFront();
        ViewCompat.offsetTopAndBottom((View)this.mCircleView, n);
        this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
    }
    
    public boolean startNestedScroll(final int n) {
        return this.mNestedScrollingChildHelper.startNestedScroll(n);
    }
    
    void startScaleDownAnimation(final Animation$AnimationListener animationListener) {
        (this.mScaleDownAnimation = new Animation() {
            public void applyTransformation(final float n, final Transformation transformation) {
                SwipeRefreshLayout.this.setAnimationProgress(1.0f - n);
            }
        }).setDuration(150L);
        this.mCircleView.setAnimationListener(animationListener);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownAnimation);
    }
    
    public void stopNestedScroll() {
        this.mNestedScrollingChildHelper.stopNestedScroll();
    }
    
    public interface OnChildScrollUpCallback
    {
        boolean canChildScrollUp(final SwipeRefreshLayout p0, final View p1);
    }
    
    public interface OnRefreshListener
    {
        void onRefresh();
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        final boolean mRefreshing;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel) {
            super(parcel);
            this.mRefreshing = (parcel.readByte() != 0);
        }
        
        SavedState(final Parcelable parcelable, final boolean mRefreshing) {
            super(parcelable);
            this.mRefreshing = mRefreshing;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeByte((byte)(byte)(this.mRefreshing ? 1 : 0));
        }
    }
}
