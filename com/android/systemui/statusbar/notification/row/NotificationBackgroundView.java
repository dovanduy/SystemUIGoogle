// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.internal.util.ArrayUtils;
import android.content.res.ColorStateList;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.Drawable$Callback;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.Canvas;
import com.android.systemui.R$bool;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

public class NotificationBackgroundView extends View
{
    private int mActualHeight;
    private float mActualWidth;
    private Drawable mBackground;
    private int mBackgroundTop;
    private boolean mBottomAmountClips;
    private boolean mBottomIsRounded;
    private int mClipBottomAmount;
    private int mClipTopAmount;
    private float[] mCornerRadii;
    private float mDistanceToTopRoundness;
    private final boolean mDontModifyCorners;
    private int mDrawableAlpha;
    private boolean mExpandAnimationRunning;
    private boolean mFirstInSection;
    private boolean mIsPressedAllowed;
    private boolean mLastInSection;
    private int mTintColor;
    private boolean mTopAmountRounded;
    
    public NotificationBackgroundView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mCornerRadii = new float[8];
        this.mBottomAmountClips = true;
        this.mDrawableAlpha = 255;
        this.mDontModifyCorners = this.getResources().getBoolean(R$bool.config_clipNotificationsToOutline);
    }
    
    private void draw(final Canvas canvas, final Drawable drawable) {
        if (drawable != null) {
            final int mBackgroundTop = this.mBackgroundTop;
            int mActualHeight;
            final int n = mActualHeight = this.mActualHeight;
            if (this.mBottomIsRounded) {
                mActualHeight = n;
                if (this.mBottomAmountClips) {
                    mActualHeight = n;
                    if (!this.mExpandAnimationRunning) {
                        mActualHeight = n;
                        if (!this.mLastInSection) {
                            mActualHeight = n - this.mClipBottomAmount;
                        }
                    }
                }
            }
            int n2 = 0;
            int width = this.getWidth();
            if (this.mExpandAnimationRunning) {
                final float n3 = (float)this.getWidth();
                final float mActualWidth = this.mActualWidth;
                n2 = (int)((n3 - mActualWidth) / 2.0f);
                width = (int)(n2 + mActualWidth);
            }
            int n4 = mBackgroundTop;
            int n5 = mActualHeight;
            if (this.mTopAmountRounded) {
                final int n6 = (int)(this.mClipTopAmount - this.mDistanceToTopRoundness);
                int n7 = 0;
                Label_0164: {
                    if (n6 < 0) {
                        n7 = mBackgroundTop;
                        if (this.mFirstInSection) {
                            break Label_0164;
                        }
                    }
                    n7 = mBackgroundTop + n6;
                }
                n4 = n7;
                n5 = mActualHeight;
                if (n6 >= 0) {
                    n4 = n7;
                    n5 = mActualHeight;
                    if (!this.mLastInSection) {
                        n5 = mActualHeight + n6;
                        n4 = n7;
                    }
                }
            }
            drawable.setBounds(n2, n4, width, n5);
            drawable.draw(canvas);
        }
    }
    
    private void updateBackgroundRadii() {
        if (this.mDontModifyCorners) {
            return;
        }
        final Drawable mBackground = this.mBackground;
        if (mBackground instanceof LayerDrawable) {
            ((GradientDrawable)((LayerDrawable)mBackground).getDrawable(0)).setCornerRadii(this.mCornerRadii);
        }
    }
    
    public void drawableHotspotChanged(final float n, final float n2) {
        final Drawable mBackground = this.mBackground;
        if (mBackground != null) {
            mBackground.setHotspot(n, n2);
        }
    }
    
    protected void drawableStateChanged() {
        this.setState(this.getDrawableState());
    }
    
    public int getActualHeight() {
        return this.mActualHeight;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onDraw(final Canvas canvas) {
        if (this.mClipTopAmount + this.mClipBottomAmount < this.mActualHeight - this.mBackgroundTop || this.mExpandAnimationRunning) {
            canvas.save();
            if (!this.mExpandAnimationRunning) {
                canvas.clipRect(0, this.mClipTopAmount, this.getWidth(), this.mActualHeight - this.mClipBottomAmount);
            }
            this.draw(canvas, this.mBackground);
            canvas.restore();
        }
    }
    
    public void setActualHeight(final int mActualHeight) {
        if (this.mExpandAnimationRunning) {
            return;
        }
        this.mActualHeight = mActualHeight;
        this.invalidate();
    }
    
    public void setBackgroundTop(final int mBackgroundTop) {
        this.mBackgroundTop = mBackgroundTop;
        this.invalidate();
    }
    
    public void setBottomAmountClips(final boolean mBottomAmountClips) {
        if (mBottomAmountClips != this.mBottomAmountClips) {
            this.mBottomAmountClips = mBottomAmountClips;
            this.invalidate();
        }
    }
    
    public void setClipBottomAmount(final int mClipBottomAmount) {
        this.mClipBottomAmount = mClipBottomAmount;
        this.invalidate();
    }
    
    public void setClipTopAmount(final int mClipTopAmount) {
        this.mClipTopAmount = mClipTopAmount;
        this.invalidate();
    }
    
    public void setCustomBackground(final int n) {
        this.setCustomBackground(super.mContext.getDrawable(n));
    }
    
    public void setCustomBackground(Drawable mBackground) {
        final Drawable mBackground2 = this.mBackground;
        if (mBackground2 != null) {
            mBackground2.setCallback((Drawable$Callback)null);
            this.unscheduleDrawable(this.mBackground);
        }
        (this.mBackground = mBackground).mutate();
        mBackground = this.mBackground;
        if (mBackground != null) {
            mBackground.setCallback((Drawable$Callback)this);
            this.setTint(this.mTintColor);
        }
        mBackground = this.mBackground;
        if (mBackground instanceof RippleDrawable) {
            ((RippleDrawable)mBackground).setForceSoftware(true);
        }
        this.updateBackgroundRadii();
        this.invalidate();
    }
    
    public void setDistanceToTopRoundness(final float mDistanceToTopRoundness) {
        if (mDistanceToTopRoundness != this.mDistanceToTopRoundness) {
            this.mTopAmountRounded = (mDistanceToTopRoundness >= 0.0f);
            this.mDistanceToTopRoundness = mDistanceToTopRoundness;
            this.invalidate();
        }
    }
    
    public void setDrawableAlpha(final int n) {
        this.mDrawableAlpha = n;
        if (this.mExpandAnimationRunning) {
            return;
        }
        this.mBackground.setAlpha(n);
    }
    
    public void setExpandAnimationParams(final ActivityLaunchAnimator.ExpandAnimationParameters expandAnimationParameters) {
        this.mActualHeight = expandAnimationParameters.getHeight();
        this.mActualWidth = (float)expandAnimationParameters.getWidth();
        this.mBackground.setAlpha((int)(this.mDrawableAlpha * (1.0f - Interpolators.ALPHA_IN.getInterpolation(expandAnimationParameters.getProgress(67L, 200L)))));
        this.invalidate();
    }
    
    public void setExpandAnimationRunning(final boolean mExpandAnimationRunning) {
        this.mExpandAnimationRunning = mExpandAnimationRunning;
        final Drawable mBackground = this.mBackground;
        if (mBackground instanceof LayerDrawable) {
            final GradientDrawable gradientDrawable = (GradientDrawable)((LayerDrawable)mBackground).getDrawable(0);
            Object xfermode;
            if (mExpandAnimationRunning) {
                xfermode = new PorterDuffXfermode(PorterDuff$Mode.SRC);
            }
            else {
                xfermode = null;
            }
            gradientDrawable.setXfermode((Xfermode)xfermode);
            gradientDrawable.setAntiAlias(mExpandAnimationRunning ^ true);
        }
        if (!this.mExpandAnimationRunning) {
            this.setDrawableAlpha(this.mDrawableAlpha);
        }
        this.invalidate();
    }
    
    public void setFirstInSection(final boolean mFirstInSection) {
        this.mFirstInSection = mFirstInSection;
        this.invalidate();
    }
    
    public void setLastInSection(final boolean mLastInSection) {
        this.mLastInSection = mLastInSection;
        this.invalidate();
    }
    
    public void setPressedAllowed(final boolean mIsPressedAllowed) {
        this.mIsPressedAllowed = mIsPressedAllowed;
    }
    
    public void setRippleColor(final int n) {
        final Drawable mBackground = this.mBackground;
        if (mBackground instanceof RippleDrawable) {
            ((RippleDrawable)mBackground).setColor(ColorStateList.valueOf(n));
        }
    }
    
    public void setRoundness(final float n, final float n2) {
        final float[] mCornerRadii = this.mCornerRadii;
        if (n == mCornerRadii[0] && n2 == mCornerRadii[4]) {
            return;
        }
        this.mBottomIsRounded = (n2 != 0.0f);
        final float[] mCornerRadii2 = this.mCornerRadii;
        mCornerRadii2[1] = (mCornerRadii2[0] = n);
        mCornerRadii2[3] = (mCornerRadii2[2] = n);
        mCornerRadii2[5] = (mCornerRadii2[4] = n2);
        mCornerRadii2[7] = (mCornerRadii2[6] = n2);
        this.updateBackgroundRadii();
    }
    
    public void setState(final int[] array) {
        final Drawable mBackground = this.mBackground;
        if (mBackground != null && mBackground.isStateful()) {
            int[] removeInt = array;
            if (!this.mIsPressedAllowed) {
                removeInt = ArrayUtils.removeInt(array, 16842919);
            }
            this.mBackground.setState(removeInt);
        }
    }
    
    public void setTint(final int mTintColor) {
        if (mTintColor != 0) {
            this.mBackground.setColorFilter(mTintColor, PorterDuff$Mode.SRC_ATOP);
        }
        else {
            this.mBackground.clearColorFilter();
        }
        this.mTintColor = mTintColor;
        this.invalidate();
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mBackground;
    }
}
