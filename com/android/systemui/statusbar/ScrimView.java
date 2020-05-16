// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.graphics.Canvas;
import com.android.internal.annotations.VisibleForTesting;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff$Mode;
import androidx.core.graphics.ColorUtils;
import android.graphics.Color;
import android.graphics.drawable.Drawable$Callback;
import com.android.internal.colorextraction.drawable.ScrimDrawable;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.android.internal.colorextraction.ColorExtractor$GradientColors;
import android.graphics.PorterDuffColorFilter;
import android.view.View;

public class ScrimView extends View
{
    private Runnable mChangeRunnable;
    private PorterDuffColorFilter mColorFilter;
    private final ColorExtractor$GradientColors mColors;
    private Drawable mDrawable;
    private int mTintColor;
    private float mViewAlpha;
    
    public ScrimView(final Context context) {
        this(context, null);
    }
    
    public ScrimView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ScrimView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ScrimView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mViewAlpha = 1.0f;
        (this.mDrawable = (Drawable)new ScrimDrawable()).setCallback((Drawable$Callback)this);
        this.mColors = new ColorExtractor$GradientColors();
        this.updateColorWithTint(false);
    }
    
    private void updateColorWithTint(final boolean b) {
        final Drawable mDrawable = this.mDrawable;
        if (mDrawable instanceof ScrimDrawable) {
            ((ScrimDrawable)mDrawable).setColor(ColorUtils.blendARGB(this.mColors.getMainColor(), this.mTintColor, Color.alpha(this.mTintColor) / 255.0f), b);
        }
        else {
            if (Color.alpha(this.mTintColor) != 0) {
                final PorterDuffColorFilter mColorFilter = this.mColorFilter;
                PorterDuff$Mode porterDuff$Mode;
                if (mColorFilter == null) {
                    porterDuff$Mode = PorterDuff$Mode.SRC_OVER;
                }
                else {
                    porterDuff$Mode = mColorFilter.getMode();
                }
                final PorterDuffColorFilter mColorFilter2 = this.mColorFilter;
                if (mColorFilter2 == null || mColorFilter2.getColor() != this.mTintColor) {
                    this.mColorFilter = new PorterDuffColorFilter(this.mTintColor, porterDuff$Mode);
                }
            }
            else {
                this.mColorFilter = null;
            }
            this.mDrawable.setColorFilter((ColorFilter)this.mColorFilter);
            this.mDrawable.invalidateSelf();
        }
        final Runnable mChangeRunnable = this.mChangeRunnable;
        if (mChangeRunnable != null) {
            mChangeRunnable.run();
        }
    }
    
    protected boolean canReceivePointerEvents() {
        return false;
    }
    
    public ColorExtractor$GradientColors getColors() {
        return this.mColors;
    }
    
    @VisibleForTesting
    Drawable getDrawable() {
        return this.mDrawable;
    }
    
    public int getTint() {
        return this.mTintColor;
    }
    
    public float getViewAlpha() {
        return this.mViewAlpha;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public void invalidateDrawable(final Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (drawable == this.mDrawable) {
            this.invalidate();
        }
    }
    
    protected void onDraw(final Canvas canvas) {
        if (this.mDrawable.getAlpha() > 0) {
            this.mDrawable.draw(canvas);
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        if (b) {
            this.mDrawable.setBounds(n, n2, n3, n4);
            this.invalidate();
        }
    }
    
    public void setChangeRunnable(final Runnable mChangeRunnable) {
        this.mChangeRunnable = mChangeRunnable;
    }
    
    public void setColors(final ColorExtractor$GradientColors colorExtractor$GradientColors, final boolean b) {
        if (colorExtractor$GradientColors == null) {
            throw new IllegalArgumentException("Colors cannot be null");
        }
        if (this.mColors.equals((Object)colorExtractor$GradientColors)) {
            return;
        }
        this.mColors.set(colorExtractor$GradientColors);
        this.updateColorWithTint(b);
    }
    
    public void setTint(final int n) {
        this.setTint(n, false);
    }
    
    public void setTint(final int mTintColor, final boolean b) {
        if (this.mTintColor == mTintColor) {
            return;
        }
        this.mTintColor = mTintColor;
        this.updateColorWithTint(b);
    }
    
    public void setViewAlpha(final float f) {
        if (!Float.isNaN(f)) {
            if (f != this.mViewAlpha) {
                this.mViewAlpha = f;
                this.mDrawable.setAlpha((int)(f * 255.0f));
                final Runnable mChangeRunnable = this.mChangeRunnable;
                if (mChangeRunnable != null) {
                    mChangeRunnable.run();
                }
            }
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("alpha cannot be NaN: ");
        sb.append(f);
        throw new IllegalArgumentException(sb.toString());
    }
}
