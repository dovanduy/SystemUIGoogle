// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.animation.AnimatorListenerAdapter;
import android.view.ViewOutlineProvider;
import com.android.internal.annotations.VisibleForTesting;
import android.view.animation.Interpolator;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;

public abstract class StackScrollerDecorView extends ExpandableView
{
    protected View mContent;
    private boolean mContentAnimating;
    private final Runnable mContentVisibilityEndRunnable;
    private boolean mContentVisible;
    private int mDuration;
    private boolean mIsSecondaryVisible;
    private boolean mIsVisible;
    private boolean mSecondaryAnimating;
    protected View mSecondaryView;
    private final Runnable mSecondaryVisibilityEndRunnable;
    
    public StackScrollerDecorView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mIsVisible = true;
        this.mContentVisible = true;
        this.mIsSecondaryVisible = true;
        this.mDuration = 260;
        this.mContentVisibilityEndRunnable = new _$$Lambda$StackScrollerDecorView$GE_2dwloJkJho6ozN7VXOOo7f2I(this);
        this.mSecondaryAnimating = false;
        this.mSecondaryVisibilityEndRunnable = new _$$Lambda$StackScrollerDecorView$2MZ2DZW5S75DgdV6pIZbLhsQuUs(this);
        this.setClipChildren(false);
    }
    
    private void setContentVisible(final boolean mContentVisible, final boolean mContentAnimating) {
        if (this.mContentVisible != mContentVisible) {
            this.mContentAnimating = mContentAnimating;
            this.mContentVisible = mContentVisible;
            this.setViewVisible(this.mContent, mContentVisible, mContentAnimating, this.mContentVisibilityEndRunnable);
        }
        if (!this.mContentAnimating) {
            this.mContentVisibilityEndRunnable.run();
        }
    }
    
    private void setViewVisible(final View view, final boolean b, final boolean b2, final Runnable runnable) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != 0) {
            view.setVisibility(0);
        }
        view.animate().cancel();
        float alpha;
        if (b) {
            alpha = 1.0f;
        }
        else {
            alpha = 0.0f;
        }
        if (!b2) {
            view.setAlpha(alpha);
            if (runnable != null) {
                runnable.run();
            }
            return;
        }
        Interpolator interpolator;
        if (b) {
            interpolator = Interpolators.ALPHA_IN;
        }
        else {
            interpolator = Interpolators.ALPHA_OUT;
        }
        view.animate().alpha(alpha).setInterpolator((TimeInterpolator)interpolator).setDuration((long)this.mDuration).withEndAction(runnable);
    }
    
    protected abstract View findContentView();
    
    protected abstract View findSecondaryView();
    
    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public boolean isContentVisible() {
        return this.mContentVisible;
    }
    
    @VisibleForTesting
    boolean isSecondaryVisible() {
        return this.mIsSecondaryVisible;
    }
    
    @Override
    public boolean isTransparent() {
        return true;
    }
    
    public boolean isVisible() {
        return this.mIsVisible;
    }
    
    @Override
    public boolean needsClippingToShelf() {
        return false;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = this.findContentView();
        this.mSecondaryView = this.findSecondaryView();
        this.setVisible(false, false);
        this.setSecondaryVisible(false, false);
    }
    
    @Override
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.setOutlineProvider((ViewOutlineProvider)null);
    }
    
    @Override
    public void performAddAnimation(final long n, final long n2, final boolean b) {
        this.setContentVisible(true);
    }
    
    @Override
    public long performRemoveAnimation(final long n, final long n2, final float n3, final boolean b, final float n4, final Runnable runnable, final AnimatorListenerAdapter animatorListenerAdapter) {
        this.setContentVisible(false);
        return 0L;
    }
    
    public void setContentVisible(final boolean b) {
        this.setContentVisible(b, true);
    }
    
    public void setSecondaryVisible(final boolean mIsSecondaryVisible, final boolean mSecondaryAnimating) {
        if (this.mIsSecondaryVisible != mIsSecondaryVisible) {
            this.mSecondaryAnimating = mSecondaryAnimating;
            this.mIsSecondaryVisible = mIsSecondaryVisible;
            this.setViewVisible(this.mSecondaryView, mIsSecondaryVisible, mSecondaryAnimating, this.mSecondaryVisibilityEndRunnable);
        }
        if (!this.mSecondaryAnimating) {
            this.mSecondaryVisibilityEndRunnable.run();
        }
    }
    
    public void setVisible(final boolean mIsVisible, final boolean b) {
        if (this.mIsVisible != mIsVisible) {
            this.mIsVisible = mIsVisible;
            if (b) {
                if (mIsVisible) {
                    this.setVisibility(0);
                    this.setWillBeGone(false);
                    this.notifyHeightChanged(false);
                }
                else {
                    this.setWillBeGone(true);
                }
                this.setContentVisible(mIsVisible, true);
            }
            else {
                int visibility;
                if (mIsVisible) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                this.setVisibility(visibility);
                this.setContentVisible(mIsVisible, false);
                this.setWillBeGone(false);
                this.notifyHeightChanged(false);
            }
        }
    }
}
