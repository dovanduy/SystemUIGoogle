// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.row.ExpandableView;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import android.graphics.Rect;
import android.animation.ObjectAnimator;

class NotificationSection
{
    private ObjectAnimator mBottomAnimator;
    private Rect mBounds;
    private int mBucket;
    private Rect mCurrentBounds;
    private Rect mEndAnimationRect;
    private ActivatableNotificationView mFirstVisibleChild;
    private ActivatableNotificationView mLastVisibleChild;
    private View mOwningView;
    private Rect mStartAnimationRect;
    private ObjectAnimator mTopAnimator;
    
    NotificationSection(final View mOwningView, final int mBucket) {
        this.mBounds = new Rect();
        this.mCurrentBounds = new Rect(-1, -1, -1, -1);
        this.mStartAnimationRect = new Rect();
        this.mEndAnimationRect = new Rect();
        this.mTopAnimator = null;
        this.mBottomAnimator = null;
        this.mOwningView = mOwningView;
        this.mBucket = mBucket;
    }
    
    private void setBackgroundBottom(final int bottom) {
        this.mCurrentBounds.bottom = bottom;
        this.mOwningView.invalidate();
    }
    
    private void setBackgroundTop(final int top) {
        this.mCurrentBounds.top = top;
        this.mOwningView.invalidate();
    }
    
    private void startBottomAnimation(final boolean b) {
        final int bottom = this.mStartAnimationRect.bottom;
        final int bottom2 = this.mEndAnimationRect.bottom;
        final int bottom3 = this.mBounds.bottom;
        final ObjectAnimator mBottomAnimator = this.mBottomAnimator;
        if (mBottomAnimator != null && bottom2 == bottom3) {
            return;
        }
        if (b) {
            if (mBottomAnimator != null) {
                mBottomAnimator.cancel();
            }
            final ObjectAnimator ofInt = ObjectAnimator.ofInt((Object)this, "backgroundBottom", new int[] { this.mCurrentBounds.bottom, bottom3 });
            ofInt.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
            ofInt.setDuration(360L);
            ofInt.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    NotificationSection.this.mStartAnimationRect.bottom = -1;
                    NotificationSection.this.mEndAnimationRect.bottom = -1;
                    NotificationSection.this.mBottomAnimator = null;
                }
            });
            ofInt.start();
            this.mStartAnimationRect.bottom = this.mCurrentBounds.bottom;
            this.mEndAnimationRect.bottom = bottom3;
            this.mBottomAnimator = ofInt;
            return;
        }
        if (mBottomAnimator != null) {
            mBottomAnimator.getValues()[0].setIntValues(new int[] { bottom, bottom3 });
            this.mStartAnimationRect.bottom = bottom;
            this.mEndAnimationRect.bottom = bottom3;
            mBottomAnimator.setCurrentPlayTime(mBottomAnimator.getCurrentPlayTime());
            return;
        }
        this.setBackgroundBottom(bottom3);
    }
    
    private void startTopAnimation(final boolean b) {
        final int top = this.mEndAnimationRect.top;
        final int top2 = this.mBounds.top;
        final ObjectAnimator mTopAnimator = this.mTopAnimator;
        if (mTopAnimator != null && top == top2) {
            return;
        }
        if (b) {
            if (mTopAnimator != null) {
                mTopAnimator.cancel();
            }
            final ObjectAnimator ofInt = ObjectAnimator.ofInt((Object)this, "backgroundTop", new int[] { this.mCurrentBounds.top, top2 });
            ofInt.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
            ofInt.setDuration(360L);
            ofInt.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    NotificationSection.this.mStartAnimationRect.top = -1;
                    NotificationSection.this.mEndAnimationRect.top = -1;
                    NotificationSection.this.mTopAnimator = null;
                }
            });
            ofInt.start();
            this.mStartAnimationRect.top = this.mCurrentBounds.top;
            this.mEndAnimationRect.top = top2;
            this.mTopAnimator = ofInt;
            return;
        }
        if (mTopAnimator != null) {
            final int top3 = this.mStartAnimationRect.top;
            mTopAnimator.getValues()[0].setIntValues(new int[] { top3, top2 });
            this.mStartAnimationRect.top = top3;
            this.mEndAnimationRect.top = top2;
            mTopAnimator.setCurrentPlayTime(mTopAnimator.getCurrentPlayTime());
            return;
        }
        this.setBackgroundTop(top2);
    }
    
    public boolean areBoundsAnimating() {
        return this.mBottomAnimator != null || this.mTopAnimator != null;
    }
    
    public void cancelAnimators() {
        final ObjectAnimator mBottomAnimator = this.mBottomAnimator;
        if (mBottomAnimator != null) {
            mBottomAnimator.cancel();
        }
        final ObjectAnimator mTopAnimator = this.mTopAnimator;
        if (mTopAnimator != null) {
            mTopAnimator.cancel();
        }
    }
    
    public boolean didBoundsChange() {
        return this.mCurrentBounds.equals((Object)this.mBounds) ^ true;
    }
    
    public Rect getBounds() {
        return this.mBounds;
    }
    
    public int getBucket() {
        return this.mBucket;
    }
    
    public Rect getCurrentBounds() {
        return this.mCurrentBounds;
    }
    
    public ActivatableNotificationView getFirstVisibleChild() {
        return this.mFirstVisibleChild;
    }
    
    public ActivatableNotificationView getLastVisibleChild() {
        return this.mLastVisibleChild;
    }
    
    public boolean isTargetBottom(final int n) {
        return (this.mBottomAnimator == null && this.mCurrentBounds.bottom == n) || (this.mBottomAnimator != null && this.mEndAnimationRect.bottom == n);
    }
    
    public boolean isTargetTop(final int n) {
        return (this.mTopAnimator == null && this.mCurrentBounds.top == n) || (this.mTopAnimator != null && this.mEndAnimationRect.top == n);
    }
    
    public void resetCurrentBounds() {
        this.mCurrentBounds.set(this.mBounds);
    }
    
    public boolean setFirstVisibleChild(final ActivatableNotificationView mFirstVisibleChild) {
        final boolean b = this.mFirstVisibleChild != mFirstVisibleChild;
        this.mFirstVisibleChild = mFirstVisibleChild;
        return b;
    }
    
    public boolean setLastVisibleChild(final ActivatableNotificationView mLastVisibleChild) {
        final boolean b = this.mLastVisibleChild != mLastVisibleChild;
        this.mLastVisibleChild = mLastVisibleChild;
        return b;
    }
    
    public void startBackgroundAnimation(final boolean b, final boolean b2) {
        final Rect mCurrentBounds = this.mCurrentBounds;
        final Rect mBounds = this.mBounds;
        mCurrentBounds.left = mBounds.left;
        mCurrentBounds.right = mBounds.right;
        this.startBottomAnimation(b2);
        this.startTopAnimation(b);
    }
    
    public int updateBounds(int n, int b, final boolean b2) {
        final ActivatableNotificationView firstVisibleChild = this.getFirstVisibleChild();
        int max2;
        int b3;
        if (firstVisibleChild != null) {
            final int n2 = (int)Math.ceil(ViewState.getFinalTranslationY((View)firstVisibleChild));
            int a;
            if (this.isTargetTop(n2)) {
                a = n2;
            }
            else {
                a = (int)Math.ceil(firstVisibleChild.getTranslationY());
            }
            final int max = Math.max(a, n);
            if (firstVisibleChild.showingPulsing()) {
                final int n3 = max2 = Math.max(n, n2 + ExpandableViewState.getFinalActualHeight(firstVisibleChild));
                b3 = max;
                if (b2) {
                    final Rect mBounds = this.mBounds;
                    mBounds.left += (int)Math.max(firstVisibleChild.getTranslation(), 0.0f);
                    final Rect mBounds2 = this.mBounds;
                    mBounds2.right += (int)Math.min(firstVisibleChild.getTranslation(), 0.0f);
                    max2 = n3;
                    b3 = max;
                }
            }
            else {
                max2 = n;
                b3 = max;
            }
        }
        else {
            max2 = (b3 = n);
        }
        final int max3 = Math.max(n, b3);
        final ActivatableNotificationView lastVisibleChild = this.getLastVisibleChild();
        n = max2;
        if (lastVisibleChild != null) {
            n = (int)Math.floor(ViewState.getFinalTranslationY((View)lastVisibleChild) + ExpandableViewState.getFinalActualHeight(lastVisibleChild) - lastVisibleChild.getClipBottomAmount());
            if (!this.isTargetBottom(n)) {
                n = (int)(lastVisibleChild.getTranslationY() + lastVisibleChild.getActualHeight() - lastVisibleChild.getClipBottomAmount());
                b = (int)Math.min(lastVisibleChild.getTranslationY() + lastVisibleChild.getActualHeight(), (float)b);
            }
            n = Math.max(max2, Math.max(n, b));
        }
        n = Math.max(max3, n);
        final Rect mBounds3 = this.mBounds;
        mBounds3.top = max3;
        return mBounds3.bottom = n;
    }
}
