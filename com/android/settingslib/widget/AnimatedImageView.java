// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.view.View;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.AnimatedRotateDrawable;
import android.widget.ImageView;

public class AnimatedImageView extends ImageView
{
    private boolean mAnimating;
    private AnimatedRotateDrawable mDrawable;
    
    public AnimatedImageView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void updateAnimating() {
        if (this.mDrawable != null) {
            if (this.getVisibility() == 0 && this.mAnimating) {
                this.mDrawable.start();
            }
            else {
                this.mDrawable.stop();
            }
        }
    }
    
    private void updateDrawable() {
        if (this.isShown()) {
            final AnimatedRotateDrawable mDrawable = this.mDrawable;
            if (mDrawable != null) {
                mDrawable.stop();
            }
        }
        final Drawable drawable = this.getDrawable();
        if (drawable instanceof AnimatedRotateDrawable) {
            (this.mDrawable = (AnimatedRotateDrawable)drawable).setFramesCount(56);
            this.mDrawable.setFramesDuration(32);
            if (this.isShown() && this.mAnimating) {
                this.mDrawable.start();
            }
        }
        else {
            this.mDrawable = null;
        }
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.updateAnimating();
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.updateAnimating();
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        this.updateAnimating();
    }
    
    public void setImageDrawable(final Drawable imageDrawable) {
        super.setImageDrawable(imageDrawable);
        this.updateDrawable();
    }
    
    public void setImageResource(final int imageResource) {
        super.setImageResource(imageResource);
        this.updateDrawable();
    }
}
