// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.content.res.TypedArray;
import android.view.RemotableViewMethod;
import android.view.View;
import android.graphics.drawable.Drawable;
import com.android.systemui.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.RemoteViews$RemoteView;
import android.widget.ImageView;

@RemoteViews$RemoteView
public class AnimatedImageView extends ImageView
{
    private boolean mAllowAnimation;
    AnimationDrawable mAnim;
    boolean mAttached;
    int mDrawableId;
    private final boolean mHasOverlappingRendering;
    
    public AnimatedImageView(final Context context) {
        this(context, null);
    }
    
    public AnimatedImageView(final Context context, AttributeSet obtainStyledAttributes) {
        super(context, obtainStyledAttributes);
        this.mAllowAnimation = true;
        obtainStyledAttributes = (AttributeSet)context.getTheme().obtainStyledAttributes(obtainStyledAttributes, R$styleable.AnimatedImageView, 0, 0);
        try {
            this.mHasOverlappingRendering = ((TypedArray)obtainStyledAttributes).getBoolean(R$styleable.AnimatedImageView_hasOverlappingRendering, true);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    private void updateAnim() {
        final Drawable drawable = this.getDrawable();
        if (this.mAttached) {
            final AnimationDrawable mAnim = this.mAnim;
            if (mAnim != null) {
                mAnim.stop();
            }
        }
        if (drawable instanceof AnimationDrawable) {
            this.mAnim = (AnimationDrawable)drawable;
            if (this.isShown() && this.mAllowAnimation) {
                this.mAnim.start();
            }
        }
        else {
            this.mAnim = null;
        }
    }
    
    public boolean hasOverlappingRendering() {
        return this.mHasOverlappingRendering;
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttached = true;
        this.updateAnim();
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final AnimationDrawable mAnim = this.mAnim;
        if (mAnim != null) {
            mAnim.stop();
        }
        this.mAttached = false;
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        if (this.mAnim != null) {
            if (this.isShown() && this.mAllowAnimation) {
                this.mAnim.start();
            }
            else {
                this.mAnim.stop();
            }
        }
    }
    
    public void setAllowAnimation(final boolean mAllowAnimation) {
        if (this.mAllowAnimation != mAllowAnimation) {
            this.mAllowAnimation = mAllowAnimation;
            this.updateAnim();
            if (!this.mAllowAnimation) {
                final AnimationDrawable mAnim = this.mAnim;
                if (mAnim != null) {
                    mAnim.setVisible(this.getVisibility() == 0, true);
                }
            }
        }
    }
    
    public void setImageDrawable(final Drawable imageDrawable) {
        if (imageDrawable != null) {
            if (this.mDrawableId == imageDrawable.hashCode()) {
                return;
            }
            this.mDrawableId = imageDrawable.hashCode();
        }
        else {
            this.mDrawableId = 0;
        }
        super.setImageDrawable(imageDrawable);
        this.updateAnim();
    }
    
    @RemotableViewMethod
    public void setImageResource(final int mDrawableId) {
        if (this.mDrawableId == mDrawableId) {
            return;
        }
        super.setImageResource(this.mDrawableId = mDrawableId);
        this.updateAnim();
    }
}
