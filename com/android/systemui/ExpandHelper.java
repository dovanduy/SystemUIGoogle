// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.internal.annotations.VisibleForTesting;
import android.view.MotionEvent;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.ViewConfiguration;
import android.view.ScaleGestureDetector$SimpleOnScaleGestureListener;
import android.view.VelocityTracker;
import com.android.systemui.statusbar.policy.ScrollAdapter;
import android.view.ScaleGestureDetector$OnScaleGestureListener;
import android.animation.ObjectAnimator;
import android.view.ScaleGestureDetector;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.FlingAnimationUtils;
import android.view.View;
import android.content.Context;

public class ExpandHelper implements Gefingerpoken
{
    private Callback mCallback;
    private Context mContext;
    private float mCurrentHeight;
    private boolean mEnabled;
    private View mEventSource;
    private boolean mExpanding;
    private int mExpansionStyle;
    private FlingAnimationUtils mFlingAnimationUtils;
    private int mGravity;
    private boolean mHasPopped;
    private float mInitialTouchFocusY;
    private float mInitialTouchSpan;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mLastFocusY;
    private float mLastMotionY;
    private float mLastSpanY;
    private float mNaturalHeight;
    private float mOldHeight;
    private boolean mOnlyMovements;
    private float mPullGestureMinXSpan;
    private ExpandableView mResizedView;
    private ScaleGestureDetector mSGD;
    private ObjectAnimator mScaleAnimation;
    private ScaleGestureDetector$OnScaleGestureListener mScaleGestureListener;
    private ViewScaler mScaler;
    private ScrollAdapter mScrollAdapter;
    private final float mSlopMultiplier;
    private int mSmallSize;
    private final int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private boolean mWatchingForPull;
    
    public ExpandHelper(final Context mContext, final Callback mCallback, final int mSmallSize, final int n) {
        this.mExpansionStyle = 0;
        this.mEnabled = true;
        this.mScaleGestureListener = (ScaleGestureDetector$OnScaleGestureListener)new ScaleGestureDetector$SimpleOnScaleGestureListener() {
            public boolean onScale(final ScaleGestureDetector scaleGestureDetector) {
                return true;
            }
            
            public boolean onScaleBegin(final ScaleGestureDetector scaleGestureDetector) {
                if (!ExpandHelper.this.mOnlyMovements) {
                    final ExpandHelper this$0 = ExpandHelper.this;
                    this$0.startExpanding(this$0.mResizedView, 4);
                }
                return ExpandHelper.this.mExpanding;
            }
            
            public void onScaleEnd(final ScaleGestureDetector scaleGestureDetector) {
            }
        };
        this.mSmallSize = mSmallSize;
        this.mContext = mContext;
        this.mCallback = mCallback;
        final ViewScaler mScaler = new ViewScaler();
        this.mScaler = mScaler;
        this.mGravity = 48;
        this.mScaleAnimation = ObjectAnimator.ofFloat((Object)mScaler, "height", new float[] { 0.0f });
        this.mPullGestureMinXSpan = this.mContext.getResources().getDimension(R$dimen.pull_span_min);
        this.mTouchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
        this.mSlopMultiplier = ViewConfiguration.getAmbiguousGestureMultiplier();
        this.mSGD = new ScaleGestureDetector(mContext, this.mScaleGestureListener);
        this.mFlingAnimationUtils = new FlingAnimationUtils(this.mContext.getResources().getDisplayMetrics(), 0.3f);
    }
    
    private void cancel(final boolean b) {
        this.finishExpanding(true, 0.0f, b);
        this.clearView();
        this.mSGD = new ScaleGestureDetector(this.mContext, this.mScaleGestureListener);
    }
    
    private float clamp(float n) {
        final int mSmallSize = this.mSmallSize;
        float n2 = n;
        if (n < mSmallSize) {
            n2 = (float)mSmallSize;
        }
        final float mNaturalHeight = this.mNaturalHeight;
        n = n2;
        if (n2 > mNaturalHeight) {
            n = mNaturalHeight;
        }
        return n;
    }
    
    private void clearView() {
        this.mResizedView = null;
    }
    
    private ExpandableView findView(final float n, final float n2) {
        final View mEventSource = this.mEventSource;
        ExpandableView expandableView;
        if (mEventSource != null) {
            final int[] array = new int[2];
            mEventSource.getLocationOnScreen(array);
            expandableView = this.mCallback.getChildAtRawPosition(n + array[0], n2 + array[1]);
        }
        else {
            expandableView = this.mCallback.getChildAtPosition(n, n2);
        }
        return expandableView;
    }
    
    private void finishExpanding(final boolean b, float n, final boolean b2) {
        if (!this.mExpanding) {
            return;
        }
        final float height = this.mScaler.getHeight();
        final float mOldHeight = this.mOldHeight;
        final float n2 = (float)this.mSmallSize;
        final boolean b3 = true;
        final boolean b4 = mOldHeight == n2;
        boolean b6;
        if (!b) {
            boolean b5 = false;
            Label_0106: {
                Label_0081: {
                    if (b4) {
                        if (height <= this.mOldHeight || n < 0.0f) {
                            break Label_0081;
                        }
                    }
                    else if (height < this.mOldHeight) {
                        if (n <= 0.0f) {
                            break Label_0081;
                        }
                    }
                    b5 = true;
                    break Label_0106;
                }
                b5 = false;
            }
            b6 = (b5 | this.mNaturalHeight == this.mSmallSize);
        }
        else {
            b6 = (b4 ^ true);
        }
        if (this.mScaleAnimation.isRunning()) {
            this.mScaleAnimation.cancel();
        }
        this.mCallback.expansionStateChanged(false);
        int n3 = this.mScaler.getNaturalHeight();
        if (!b6) {
            n3 = this.mSmallSize;
        }
        final float height2 = (float)n3;
        final float n4 = fcmpl(height2, height);
        if (n4 != 0 && this.mEnabled && b2) {
            this.mScaleAnimation.setFloatValues(new float[] { height2 });
            this.mScaleAnimation.setupStartValues();
            this.mScaleAnimation.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public boolean mCancelled;
                final /* synthetic */ View val$scaledView = ExpandHelper.this.mResizedView;
                
                public void onAnimationCancel(final Animator animator) {
                    this.mCancelled = true;
                }
                
                public void onAnimationEnd(final Animator animator) {
                    if (!this.mCancelled) {
                        ExpandHelper.this.mCallback.setUserExpandedChild(this.val$scaledView, b6);
                        if (!ExpandHelper.this.mExpanding) {
                            ExpandHelper.this.mScaler.setView(null);
                        }
                    }
                    else {
                        ExpandHelper.this.mCallback.setExpansionCancelled(this.val$scaledView);
                    }
                    ExpandHelper.this.mCallback.setUserLockedChild(this.val$scaledView, false);
                    ExpandHelper.this.mScaleAnimation.removeListener((Animator$AnimatorListener)this);
                }
            });
            if (b6 != (n >= 0.0f && b3)) {
                n = 0.0f;
            }
            this.mFlingAnimationUtils.apply((Animator)this.mScaleAnimation, height, height2, n);
            this.mScaleAnimation.start();
        }
        else {
            if (n4 != 0) {
                this.mScaler.setHeight(height2);
            }
            this.mCallback.setUserExpandedChild((View)this.mResizedView, b6);
            this.mCallback.setUserLockedChild((View)this.mResizedView, false);
            this.mScaler.setView(null);
        }
        this.mExpanding = false;
        this.mExpansionStyle = 0;
    }
    
    private float getCurrentVelocity() {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.computeCurrentVelocity(1000);
            return this.mVelocityTracker.getYVelocity();
        }
        return 0.0f;
    }
    
    private float getTouchSlop(final MotionEvent motionEvent) {
        float n;
        if (motionEvent.getClassification() == 1) {
            n = this.mTouchSlop * this.mSlopMultiplier;
        }
        else {
            n = (float)this.mTouchSlop;
        }
        return n;
    }
    
    private boolean isEnabled() {
        return this.mEnabled;
    }
    
    private boolean isFullyExpanded(final ExpandableView expandableView) {
        return expandableView.getIntrinsicHeight() == expandableView.getMaxContentHeight() && (!expandableView.isSummaryWithChildren() || expandableView.areChildrenExpanded());
    }
    
    private boolean isInside(final View view, float n, float n2) {
        final boolean b = false;
        if (view == null) {
            return false;
        }
        final View mEventSource = this.mEventSource;
        float n3 = n;
        float n4 = n2;
        if (mEventSource != null) {
            final int[] array = new int[2];
            mEventSource.getLocationOnScreen(array);
            n3 = n + array[0];
            n4 = n2 + array[1];
        }
        final int[] array2 = new int[2];
        view.getLocationOnScreen(array2);
        n = n3 - array2[0];
        n2 = n4 - array2[1];
        boolean b2 = b;
        if (n > 0.0f) {
            b2 = b;
            if (n2 > 0.0f) {
                final boolean b3 = n < view.getWidth();
                final boolean b4 = n2 < view.getHeight();
                b2 = b;
                if (b3 & b4) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    private void maybeRecycleVelocityTracker(final MotionEvent motionEvent) {
        if (this.mVelocityTracker != null && (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1)) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    private void trackVelocity(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked == 2) {
                if (this.mVelocityTracker == null) {
                    this.mVelocityTracker = VelocityTracker.obtain();
                }
                this.mVelocityTracker.addMovement(motionEvent);
            }
        }
        else {
            final VelocityTracker mVelocityTracker = this.mVelocityTracker;
            if (mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            else {
                mVelocityTracker.clear();
            }
            this.mVelocityTracker.addMovement(motionEvent);
        }
    }
    
    public void cancel() {
        this.cancel(true);
    }
    
    public void cancelImmediately() {
        this.cancel(false);
    }
    
    @VisibleForTesting
    void finishExpanding(final boolean b, final float n) {
        this.finishExpanding(b, n, true);
    }
    
    @VisibleForTesting
    ObjectAnimator getScaleAnimation() {
        return this.mScaleAnimation;
    }
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final boolean enabled = this.isEnabled();
        boolean b = false;
        if (!enabled) {
            return false;
        }
        this.trackVelocity(motionEvent);
        final int action = motionEvent.getAction();
        this.mSGD.onTouchEvent(motionEvent);
        final int n = (int)this.mSGD.getFocusX();
        final float mInitialTouchFocusY = (float)(int)this.mSGD.getFocusY();
        this.mInitialTouchFocusY = mInitialTouchFocusY;
        final float currentSpan = this.mSGD.getCurrentSpan();
        this.mInitialTouchSpan = currentSpan;
        this.mLastFocusY = this.mInitialTouchFocusY;
        this.mLastSpanY = currentSpan;
        final boolean mExpanding = this.mExpanding;
        final boolean b2 = true;
        if (mExpanding) {
            this.mLastMotionY = motionEvent.getRawY();
            this.maybeRecycleVelocityTracker(motionEvent);
            return true;
        }
        if (action == 2 && (this.mExpansionStyle & 0x1) != 0x0) {
            return true;
        }
        final int n2 = action & 0xFF;
        Label_0480: {
            if (n2 != 0) {
                if (n2 != 1) {
                    if (n2 != 2) {
                        if (n2 != 3) {
                            break Label_0480;
                        }
                    }
                    else {
                        final float currentSpanX = this.mSGD.getCurrentSpanX();
                        if (currentSpanX > this.mPullGestureMinXSpan && currentSpanX > this.mSGD.getCurrentSpanY() && !this.mExpanding) {
                            this.startExpanding(this.mResizedView, 2);
                            this.mWatchingForPull = false;
                        }
                        if (!this.mWatchingForPull) {
                            break Label_0480;
                        }
                        final float n3 = motionEvent.getRawY() - this.mInitialTouchY;
                        final float rawX = motionEvent.getRawX();
                        final float mInitialTouchX = this.mInitialTouchX;
                        if (n3 <= this.getTouchSlop(motionEvent) || n3 <= Math.abs(rawX - mInitialTouchX)) {
                            break Label_0480;
                        }
                        this.mWatchingForPull = false;
                        final ExpandableView mResizedView = this.mResizedView;
                        if (mResizedView != null && !this.isFullyExpanded(mResizedView) && this.startExpanding(this.mResizedView, 1)) {
                            this.mLastMotionY = motionEvent.getRawY();
                            this.mInitialTouchY = motionEvent.getRawY();
                            this.mHasPopped = false;
                        }
                        break Label_0480;
                    }
                }
                if (motionEvent.getActionMasked() == 3) {
                    b = true;
                }
                this.finishExpanding(b, this.getCurrentVelocity());
                this.clearView();
            }
            else {
                final ScrollAdapter mScrollAdapter = this.mScrollAdapter;
                this.mWatchingForPull = (mScrollAdapter != null && this.isInside(mScrollAdapter.getHostView(), (float)n, mInitialTouchFocusY) && this.mScrollAdapter.isScrolledToTop() && b2);
                final ExpandableView view = this.findView((float)n, mInitialTouchFocusY);
                this.mResizedView = view;
                if (view != null && !this.mCallback.canChildBeExpanded((View)view)) {
                    this.mResizedView = null;
                    this.mWatchingForPull = false;
                }
                this.mInitialTouchY = motionEvent.getRawY();
                this.mInitialTouchX = motionEvent.getRawX();
            }
        }
        this.mLastMotionY = motionEvent.getRawY();
        this.maybeRecycleVelocityTracker(motionEvent);
        return this.mExpanding;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final boolean enabled = this.isEnabled();
        final boolean b = false;
        if (!enabled && !this.mExpanding) {
            return false;
        }
        this.trackVelocity(motionEvent);
        final int actionMasked = motionEvent.getActionMasked();
        this.mSGD.onTouchEvent(motionEvent);
        final int n = (int)this.mSGD.getFocusX();
        final int n2 = (int)this.mSGD.getFocusY();
        if (this.mOnlyMovements) {
            this.mLastMotionY = motionEvent.getRawY();
            return false;
        }
        Label_0559: {
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked != 2) {
                        if (actionMasked != 3) {
                            if (actionMasked != 5 && actionMasked != 6) {
                                break Label_0559;
                            }
                            this.mInitialTouchY += this.mSGD.getFocusY() - this.mLastFocusY;
                            this.mInitialTouchSpan += this.mSGD.getCurrentSpan() - this.mLastSpanY;
                            break Label_0559;
                        }
                    }
                    else {
                        if (this.mWatchingForPull) {
                            final float n3 = motionEvent.getRawY() - this.mInitialTouchY;
                            final float rawX = motionEvent.getRawX();
                            final float mInitialTouchX = this.mInitialTouchX;
                            if (n3 > this.getTouchSlop(motionEvent) && n3 > Math.abs(rawX - mInitialTouchX)) {
                                this.mWatchingForPull = false;
                                final ExpandableView mResizedView = this.mResizedView;
                                if (mResizedView != null && !this.isFullyExpanded(mResizedView) && this.startExpanding(this.mResizedView, 1)) {
                                    this.mInitialTouchY = motionEvent.getRawY();
                                    this.mLastMotionY = motionEvent.getRawY();
                                    this.mHasPopped = false;
                                }
                            }
                        }
                        if (this.mExpanding && (this.mExpansionStyle & 0x1) != 0x0) {
                            final float n4 = motionEvent.getRawY() - this.mLastMotionY + this.mCurrentHeight;
                            final float clamp = this.clamp(n4);
                            boolean b2 = n4 > this.mNaturalHeight;
                            if (n4 < this.mSmallSize) {
                                b2 = true;
                            }
                            if (!this.mHasPopped) {
                                final View mEventSource = this.mEventSource;
                                if (mEventSource != null) {
                                    mEventSource.performHapticFeedback(1);
                                }
                                this.mHasPopped = true;
                            }
                            this.mScaler.setHeight(clamp);
                            this.mLastMotionY = motionEvent.getRawY();
                            if (b2) {
                                this.mCallback.expansionStateChanged(false);
                            }
                            else {
                                this.mCallback.expansionStateChanged(true);
                            }
                            return true;
                        }
                        if (this.mExpanding) {
                            this.updateExpansion();
                            this.mLastMotionY = motionEvent.getRawY();
                            return true;
                        }
                        break Label_0559;
                    }
                }
                this.finishExpanding(!this.isEnabled() || motionEvent.getActionMasked() == 3, this.getCurrentVelocity());
                this.clearView();
            }
            else {
                final ScrollAdapter mScrollAdapter = this.mScrollAdapter;
                this.mWatchingForPull = (mScrollAdapter != null && this.isInside(mScrollAdapter.getHostView(), (float)n, (float)n2));
                this.mResizedView = this.findView((float)n, (float)n2);
                this.mInitialTouchX = motionEvent.getRawX();
                this.mInitialTouchY = motionEvent.getRawY();
            }
        }
        this.mLastMotionY = motionEvent.getRawY();
        this.maybeRecycleVelocityTracker(motionEvent);
        boolean b3 = b;
        if (this.mResizedView != null) {
            b3 = true;
        }
        return b3;
    }
    
    public void onlyObserveMovements(final boolean mOnlyMovements) {
        this.mOnlyMovements = mOnlyMovements;
    }
    
    public void setEnabled(final boolean mEnabled) {
        this.mEnabled = mEnabled;
    }
    
    public void setEventSource(final View mEventSource) {
        this.mEventSource = mEventSource;
    }
    
    public void setScrollAdapter(final ScrollAdapter mScrollAdapter) {
        this.mScrollAdapter = mScrollAdapter;
    }
    
    @VisibleForTesting
    boolean startExpanding(final ExpandableView view, final int mExpansionStyle) {
        if (!(view instanceof ExpandableNotificationRow)) {
            return false;
        }
        this.mExpansionStyle = mExpansionStyle;
        if (this.mExpanding && view == this.mResizedView) {
            return true;
        }
        this.mExpanding = true;
        this.mCallback.expansionStateChanged(true);
        this.mCallback.setUserLockedChild((View)view, true);
        this.mScaler.setView(view);
        final float height = this.mScaler.getHeight();
        this.mOldHeight = height;
        this.mCurrentHeight = height;
        if (this.mCallback.canChildBeExpanded((View)view)) {
            this.mNaturalHeight = (float)this.mScaler.getNaturalHeight();
            this.mSmallSize = view.getCollapsedHeight();
        }
        else {
            this.mNaturalHeight = this.mOldHeight;
        }
        return true;
    }
    
    @VisibleForTesting
    void updateExpansion() {
        final float n = (this.mSGD.getCurrentSpan() - this.mInitialTouchSpan) * 1.0f;
        final float focusY = this.mSGD.getFocusY();
        final float mInitialTouchFocusY = this.mInitialTouchFocusY;
        float n2;
        if (this.mGravity == 80) {
            n2 = -1.0f;
        }
        else {
            n2 = 1.0f;
        }
        final float n3 = (focusY - mInitialTouchFocusY) * 1.0f * n2;
        final float n4 = Math.abs(n3) + Math.abs(n) + 1.0f;
        this.mScaler.setHeight(this.clamp(n3 * Math.abs(n3) / n4 + n * Math.abs(n) / n4 + this.mOldHeight));
        this.mLastFocusY = this.mSGD.getFocusY();
        this.mLastSpanY = this.mSGD.getCurrentSpan();
    }
    
    public interface Callback
    {
        boolean canChildBeExpanded(final View p0);
        
        void expansionStateChanged(final boolean p0);
        
        ExpandableView getChildAtPosition(final float p0, final float p1);
        
        ExpandableView getChildAtRawPosition(final float p0, final float p1);
        
        int getMaxExpandHeight(final ExpandableView p0);
        
        void setExpansionCancelled(final View p0);
        
        void setUserExpandedChild(final View p0, final boolean p1);
        
        void setUserLockedChild(final View p0, final boolean p1);
    }
    
    private class ViewScaler
    {
        ExpandableView mView;
        
        public ViewScaler() {
        }
        
        public float getHeight() {
            return (float)this.mView.getActualHeight();
        }
        
        public int getNaturalHeight() {
            return ExpandHelper.this.mCallback.getMaxExpandHeight(this.mView);
        }
        
        public void setHeight(final float n) {
            this.mView.setActualHeight((int)n);
            ExpandHelper.this.mCurrentHeight = n;
        }
        
        public void setView(final ExpandableView mView) {
            this.mView = mView;
        }
    }
}
