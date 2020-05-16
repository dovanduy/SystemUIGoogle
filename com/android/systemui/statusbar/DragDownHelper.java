// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.view.MotionEvent;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ValueAnimator;
import android.view.ViewConfiguration;
import com.android.systemui.R$dimen;
import android.content.Context;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import android.view.View;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.ExpandHelper;
import com.android.systemui.Gefingerpoken;

public class DragDownHelper implements Gefingerpoken
{
    private ExpandHelper.Callback mCallback;
    private DragDownCallback mDragDownCallback;
    private boolean mDraggedFarEnough;
    private boolean mDraggingDown;
    private FalsingManager mFalsingManager;
    private View mHost;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mLastHeight;
    private int mMinDragDistance;
    private final float mSlopMultiplier;
    private ExpandableView mStartingChild;
    private final int[] mTemp2;
    private final float mTouchSlop;
    
    public DragDownHelper(final Context context, final View mHost, final ExpandHelper.Callback mCallback, final DragDownCallback mDragDownCallback, final FalsingManager mFalsingManager) {
        this.mTemp2 = new int[2];
        this.mMinDragDistance = context.getResources().getDimensionPixelSize(R$dimen.keyguard_drag_down_min_distance);
        final ViewConfiguration value = ViewConfiguration.get(context);
        this.mTouchSlop = (float)value.getScaledTouchSlop();
        this.mSlopMultiplier = value.getScaledAmbiguousGestureMultiplier();
        this.mCallback = mCallback;
        this.mDragDownCallback = mDragDownCallback;
        this.mHost = mHost;
        this.mFalsingManager = mFalsingManager;
    }
    
    private void cancelExpansion() {
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mLastHeight, 0.0f });
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        ofFloat.setDuration(375L);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$DragDownHelper$q6x0oNk24uuvhTw3d_iOE5k6pV4(this));
        ofFloat.start();
    }
    
    private void cancelExpansion(final ExpandableView expandableView) {
        if (expandableView.getActualHeight() == expandableView.getCollapsedHeight()) {
            this.mCallback.setUserLockedChild((View)expandableView, false);
            return;
        }
        final ObjectAnimator ofInt = ObjectAnimator.ofInt((Object)expandableView, "actualHeight", new int[] { expandableView.getActualHeight(), expandableView.getCollapsedHeight() });
        ofInt.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        ofInt.setDuration(375L);
        ofInt.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                DragDownHelper.this.mCallback.setUserLockedChild((View)expandableView, false);
            }
        });
        ofInt.start();
    }
    
    private void captureStartingChild(final float n, final float n2) {
        if (this.mStartingChild == null) {
            final ExpandableView view = this.findView(n, n2);
            if ((this.mStartingChild = view) != null) {
                if (this.mDragDownCallback.isDragDownEnabledForView(view)) {
                    this.mCallback.setUserLockedChild((View)this.mStartingChild, true);
                }
                else {
                    this.mStartingChild = null;
                }
            }
        }
    }
    
    private ExpandableView findView(final float n, final float n2) {
        this.mHost.getLocationOnScreen(this.mTemp2);
        final int[] mTemp2 = this.mTemp2;
        return this.mCallback.getChildAtRawPosition(n + mTemp2[0], n2 + mTemp2[1]);
    }
    
    private void handleExpansion(float n, final ExpandableView expandableView) {
        float n2 = n;
        if (n < 0.0f) {
            n2 = 0.0f;
        }
        final boolean contentExpandable = expandableView.isContentExpandable();
        if (contentExpandable) {
            n = 0.5f;
        }
        else {
            n = 0.15f;
        }
        final float n3 = n *= n2;
        if (contentExpandable) {
            n = n3;
            if (expandableView.getCollapsedHeight() + n3 > expandableView.getMaxContentHeight()) {
                n = n3 - (expandableView.getCollapsedHeight() + n3 - expandableView.getMaxContentHeight()) * 0.85f;
            }
        }
        expandableView.setActualHeight((int)(expandableView.getCollapsedHeight() + n));
    }
    
    private boolean isFalseTouch() {
        final boolean falsingCheckNeeded = this.mDragDownCallback.isFalsingCheckNeeded();
        boolean b = false;
        if (!falsingCheckNeeded) {
            return false;
        }
        if (this.mFalsingManager.isFalseTouch() || !this.mDraggedFarEnough) {
            b = true;
        }
        return b;
    }
    
    private void stopDragging() {
        this.mFalsingManager.onNotificatonStopDraggingDown();
        final ExpandableView mStartingChild = this.mStartingChild;
        if (mStartingChild != null) {
            this.cancelExpansion(mStartingChild);
            this.mStartingChild = null;
        }
        else {
            this.cancelExpansion();
        }
        this.mDraggingDown = false;
        this.mDragDownCallback.onDragDownReset();
    }
    
    public boolean isDragDownEnabled() {
        return this.mDragDownCallback.isDragDownEnabledForView(null);
    }
    
    public boolean isDraggingDown() {
        return this.mDraggingDown;
    }
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        final int actionMasked = motionEvent.getActionMasked();
        boolean b = false;
        if (actionMasked != 0) {
            if (actionMasked == 2) {
                final float n = y - this.mInitialTouchY;
                float mTouchSlop;
                if (motionEvent.getClassification() == 1) {
                    mTouchSlop = this.mTouchSlop * this.mSlopMultiplier;
                }
                else {
                    mTouchSlop = this.mTouchSlop;
                }
                if (n > mTouchSlop && n > Math.abs(x - this.mInitialTouchX)) {
                    this.mFalsingManager.onNotificatonStartDraggingDown();
                    this.mDraggingDown = true;
                    this.captureStartingChild(this.mInitialTouchX, this.mInitialTouchY);
                    this.mInitialTouchY = y;
                    this.mInitialTouchX = x;
                    this.mDragDownCallback.onTouchSlopExceeded();
                    if (this.mStartingChild != null || this.mDragDownCallback.isDragDownAnywhereEnabled()) {
                        b = true;
                    }
                    return b;
                }
            }
        }
        else {
            this.mDraggedFarEnough = false;
            this.mDraggingDown = false;
            this.mStartingChild = null;
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
        }
        return false;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (!this.mDraggingDown) {
            return false;
        }
        motionEvent.getX();
        final float y = motionEvent.getY();
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                final float mInitialTouchY = this.mInitialTouchY;
                this.mLastHeight = y - mInitialTouchY;
                this.captureStartingChild(this.mInitialTouchX, mInitialTouchY);
                final ExpandableView mStartingChild = this.mStartingChild;
                if (mStartingChild != null) {
                    this.handleExpansion(this.mLastHeight, mStartingChild);
                }
                else {
                    this.mDragDownCallback.setEmptyDragAmount(this.mLastHeight);
                }
                if (this.mLastHeight > this.mMinDragDistance) {
                    if (!this.mDraggedFarEnough) {
                        this.mDraggedFarEnough = true;
                        this.mDragDownCallback.onCrossedThreshold(true);
                    }
                }
                else if (this.mDraggedFarEnough) {
                    this.mDraggedFarEnough = false;
                    this.mDragDownCallback.onCrossedThreshold(false);
                }
                return true;
            }
            if (actionMasked == 3) {
                this.stopDragging();
                return false;
            }
        }
        else {
            if (this.mFalsingManager.isUnlockingDisabled() || this.isFalseTouch() || !this.mDragDownCallback.onDraggedDown((View)this.mStartingChild, (int)(y - this.mInitialTouchY))) {
                this.stopDragging();
                return false;
            }
            final ExpandableView mStartingChild2 = this.mStartingChild;
            if (mStartingChild2 == null) {
                this.cancelExpansion();
            }
            else {
                this.mCallback.setUserLockedChild((View)mStartingChild2, false);
                this.mStartingChild = null;
            }
            this.mDraggingDown = false;
        }
        return false;
    }
    
    public interface DragDownCallback
    {
        boolean isDragDownAnywhereEnabled();
        
        boolean isDragDownEnabledForView(final ExpandableView p0);
        
        boolean isFalsingCheckNeeded();
        
        void onCrossedThreshold(final boolean p0);
        
        void onDragDownReset();
        
        boolean onDraggedDown(final View p0, final int p1);
        
        void onTouchSlopExceeded();
        
        void setEmptyDragAmount(final float p0);
    }
}
