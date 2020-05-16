// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.Drawable$Callback;
import com.android.systemui.R$drawable;
import com.android.systemui.Dependency;
import com.android.internal.annotations.VisibleForTesting;
import android.graphics.Canvas;
import android.animation.Animator;
import android.util.Log;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.view.View;
import android.view.ViewAnimationUtils;
import com.android.internal.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Handler;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;

public class NotificationGuts extends FrameLayout
{
    private int mActualHeight;
    private Drawable mBackground;
    private int mClipBottomAmount;
    private int mClipTopAmount;
    private OnGutsClosedListener mClosedListener;
    private boolean mExposed;
    private Runnable mFalsingCheck;
    private GutsContent mGutsContent;
    private Handler mHandler;
    private OnHeightChangedListener mHeightListener;
    private boolean mNeedsFalsingProtection;
    
    public NotificationGuts(final Context context) {
        this(context, null);
    }
    
    public NotificationGuts(final Context context, final AttributeSet set) {
        super(context, set);
        this.setWillNotDraw(false);
        this.mHandler = new Handler();
        this.mFalsingCheck = new Runnable() {
            @Override
            public void run() {
                if (NotificationGuts.this.mNeedsFalsingProtection && NotificationGuts.this.mExposed) {
                    NotificationGuts.this.closeControls(-1, -1, false, false);
                }
            }
        };
        context.obtainStyledAttributes(set, R$styleable.Theme, 0, 0).recycle();
    }
    
    private void animateOpen(final boolean b, final int b2, final int b3, final Runnable runnable) {
        if (this.isAttachedToWindow()) {
            if (b) {
                final float n = (float)Math.hypot(Math.max(this.getWidth() - b2, b2), Math.max(this.getHeight() - b3, b3));
                this.setAlpha(1.0f);
                final Animator circularReveal = ViewAnimationUtils.createCircularReveal((View)this, b2, b3, 0.0f, n);
                circularReveal.setDuration(360L);
                circularReveal.setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
                circularReveal.addListener((Animator$AnimatorListener)new AnimateOpenListener(runnable));
                circularReveal.start();
            }
            else {
                this.setAlpha(0.0f);
                this.animate().alpha(1.0f).setDuration(240L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN).setListener((Animator$AnimatorListener)new AnimateOpenListener(runnable)).start();
            }
        }
        else {
            Log.w("NotificationGuts", "Failed to animate guts open");
        }
    }
    
    private void draw(final Canvas canvas, final Drawable drawable) {
        final int mClipTopAmount = this.mClipTopAmount;
        final int n = this.mActualHeight - this.mClipBottomAmount;
        if (drawable != null && mClipTopAmount < n) {
            drawable.setBounds(0, mClipTopAmount, this.getWidth(), n);
            drawable.draw(canvas);
        }
    }
    
    private void drawableStateChanged(final Drawable drawable) {
        if (drawable != null && drawable.isStateful()) {
            drawable.setState(this.getDrawableState());
        }
    }
    
    @VisibleForTesting
    void animateClose(int b, final int n, final boolean b2) {
        if (this.isAttachedToWindow()) {
            if (b2) {
                int b3 = 0;
                Label_0051: {
                    if (b != -1) {
                        b3 = b;
                        if ((b = n) != -1) {
                            break Label_0051;
                        }
                    }
                    b3 = (this.getLeft() + this.getRight()) / 2;
                    b = this.getTop() + this.getHeight() / 2;
                }
                final Animator circularReveal = ViewAnimationUtils.createCircularReveal((View)this, b3, b, (float)Math.hypot(Math.max(this.getWidth() - b3, b3), Math.max(this.getHeight() - b, b)), 0.0f);
                circularReveal.setDuration(360L);
                circularReveal.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_LINEAR_IN);
                circularReveal.addListener((Animator$AnimatorListener)new AnimateCloseListener((View)this, this.mGutsContent));
                circularReveal.start();
            }
            else {
                this.animate().alpha(0.0f).setDuration(240L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT).setListener((Animator$AnimatorListener)new AnimateCloseListener((View)this, this.mGutsContent)).start();
            }
        }
        else {
            Log.w("NotificationGuts", "Failed to animate guts close");
            this.mGutsContent.onFinishedClosing();
        }
    }
    
    public void closeControls(final int n, final int n2, final boolean b, final boolean b2) {
        final boolean dismissCurrentBlockingHelper = Dependency.get(NotificationBlockingHelperManager.class).dismissCurrentBlockingHelper();
        if (this.getWindowToken() == null) {
            final OnGutsClosedListener mClosedListener = this.mClosedListener;
            if (mClosedListener != null) {
                mClosedListener.onGutsClosed(this);
            }
            return;
        }
        final GutsContent mGutsContent = this.mGutsContent;
        if (mGutsContent == null || !mGutsContent.handleCloseControls(b, b2) || dismissCurrentBlockingHelper) {
            this.animateClose(n, n2, dismissCurrentBlockingHelper ^ true);
            this.setExposed(false, this.mNeedsFalsingProtection);
            final OnGutsClosedListener mClosedListener2 = this.mClosedListener;
            if (mClosedListener2 != null) {
                mClosedListener2.onGutsClosed(this);
            }
        }
    }
    
    public void closeControls(final boolean b, final boolean b2, final int n, final int n2, final boolean b3) {
        final GutsContent mGutsContent = this.mGutsContent;
        if (mGutsContent != null && ((mGutsContent.isLeavebehind() && b) || (!this.mGutsContent.isLeavebehind() && b2))) {
            this.closeControls(n, n2, this.mGutsContent.shouldBeSaved(), b3);
        }
    }
    
    public void drawableHotspotChanged(final float n, final float n2) {
        final Drawable mBackground = this.mBackground;
        if (mBackground != null) {
            mBackground.setHotspot(n, n2);
        }
    }
    
    protected void drawableStateChanged() {
        this.drawableStateChanged(this.mBackground);
    }
    
    public GutsContent getGutsContent() {
        return this.mGutsContent;
    }
    
    public int getIntrinsicHeight() {
        final GutsContent mGutsContent = this.mGutsContent;
        int n;
        if (mGutsContent != null && this.mExposed) {
            n = mGutsContent.getActualHeight();
        }
        else {
            n = this.getHeight();
        }
        return n;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public boolean isExposed() {
        return this.mExposed;
    }
    
    public boolean isLeavebehind() {
        final GutsContent mGutsContent = this.mGutsContent;
        return mGutsContent != null && mGutsContent.isLeavebehind();
    }
    
    protected void onDraw(final Canvas canvas) {
        this.draw(canvas, this.mBackground);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        final Drawable drawable = super.mContext.getDrawable(R$drawable.notification_guts_bg);
        this.mBackground = drawable;
        if (drawable != null) {
            drawable.setCallback((Drawable$Callback)this);
        }
    }
    
    protected void onHeightChanged() {
        final OnHeightChangedListener mHeightListener = this.mHeightListener;
        if (mHeightListener != null) {
            mHeightListener.onHeightChanged(this);
        }
    }
    
    public void openControls(final boolean b, final int n, final int n2, final boolean b2, final Runnable runnable) {
        this.animateOpen(b, n, n2, runnable);
        this.setExposed(true, b2);
    }
    
    public void resetFalsingCheck() {
        this.mHandler.removeCallbacks(this.mFalsingCheck);
        if (this.mNeedsFalsingProtection && this.mExposed) {
            this.mHandler.postDelayed(this.mFalsingCheck, 8000L);
        }
    }
    
    public void setActualHeight(final int mActualHeight) {
        this.mActualHeight = mActualHeight;
        this.invalidate();
    }
    
    public void setClipBottomAmount(final int mClipBottomAmount) {
        this.mClipBottomAmount = mClipBottomAmount;
        this.invalidate();
    }
    
    public void setClipTopAmount(final int mClipTopAmount) {
        this.mClipTopAmount = mClipTopAmount;
        this.invalidate();
    }
    
    public void setClosedListener(final OnGutsClosedListener mClosedListener) {
        this.mClosedListener = mClosedListener;
    }
    
    @VisibleForTesting
    void setExposed(final boolean mExposed, final boolean mNeedsFalsingProtection) {
        final boolean mExposed2 = this.mExposed;
        this.mExposed = mExposed;
        this.mNeedsFalsingProtection = mNeedsFalsingProtection;
        if (mExposed && mNeedsFalsingProtection) {
            this.resetFalsingCheck();
        }
        else {
            this.mHandler.removeCallbacks(this.mFalsingCheck);
        }
        if (mExposed2 != this.mExposed) {
            final GutsContent mGutsContent = this.mGutsContent;
            if (mGutsContent != null) {
                final View contentView = mGutsContent.getContentView();
                contentView.sendAccessibilityEvent(32);
                if (this.mExposed) {
                    contentView.requestAccessibilityFocus();
                }
            }
        }
    }
    
    public void setGutsContent(final GutsContent mGutsContent) {
        this.mGutsContent = mGutsContent;
        this.removeAllViews();
        this.addView(this.mGutsContent.getContentView());
    }
    
    public void setHeightChangedListener(final OnHeightChangedListener mHeightListener) {
        this.mHeightListener = mHeightListener;
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mBackground;
    }
    
    public boolean willBeRemoved() {
        final GutsContent mGutsContent = this.mGutsContent;
        return mGutsContent != null && mGutsContent.willBeRemoved();
    }
    
    private class AnimateCloseListener extends AnimatorListenerAdapter
    {
        private final GutsContent mGutsContent;
        final View mView;
        
        private AnimateCloseListener(final View mView, final GutsContent mGutsContent) {
            this.mView = mView;
            this.mGutsContent = mGutsContent;
        }
        
        public void onAnimationEnd(final Animator animator) {
            super.onAnimationEnd(animator);
            if (!NotificationGuts.this.isExposed()) {
                this.mView.setVisibility(8);
                this.mGutsContent.onFinishedClosing();
            }
        }
    }
    
    private static class AnimateOpenListener extends AnimatorListenerAdapter
    {
        final Runnable mOnAnimationEnd;
        
        private AnimateOpenListener(final Runnable mOnAnimationEnd) {
            this.mOnAnimationEnd = mOnAnimationEnd;
        }
        
        public void onAnimationEnd(final Animator animator) {
            super.onAnimationEnd(animator);
            final Runnable mOnAnimationEnd = this.mOnAnimationEnd;
            if (mOnAnimationEnd != null) {
                mOnAnimationEnd.run();
            }
        }
    }
    
    public interface GutsContent
    {
        int getActualHeight();
        
        View getContentView();
        
        boolean handleCloseControls(final boolean p0, final boolean p1);
        
        default boolean isLeavebehind() {
            return false;
        }
        
        default void onFinishedClosing() {
        }
        
        void setGutsParent(final NotificationGuts p0);
        
        boolean shouldBeSaved();
        
        boolean willBeRemoved();
    }
    
    public interface OnGutsClosedListener
    {
        void onGutsClosed(final NotificationGuts p0);
    }
    
    public interface OnHeightChangedListener
    {
        void onHeightChanged(final NotificationGuts p0);
    }
}
