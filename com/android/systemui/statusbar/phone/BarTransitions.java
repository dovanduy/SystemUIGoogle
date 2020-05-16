// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.PorterDuff$Mode;
import android.graphics.ColorFilter;
import android.graphics.Color;
import com.android.systemui.Interpolators;
import android.graphics.Canvas;
import android.os.SystemClock;
import com.android.settingslib.Utils;
import com.android.systemui.R$color;
import android.content.Context;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

public class BarTransitions
{
    private boolean mAlwaysOpaque;
    protected final BarBackgroundDrawable mBarBackground;
    private int mMode;
    private final View mView;
    
    public BarTransitions(final View mView, final int n) {
        this.mAlwaysOpaque = false;
        final StringBuilder sb = new StringBuilder();
        sb.append("BarTransitions.");
        sb.append(mView.getClass().getSimpleName());
        sb.toString();
        this.mView = mView;
        final BarBackgroundDrawable barBackgroundDrawable = new BarBackgroundDrawable(this.mView.getContext(), n);
        this.mBarBackground = barBackgroundDrawable;
        this.mView.setBackground((Drawable)barBackgroundDrawable);
    }
    
    public static String modeToString(final int i) {
        if (i == 4) {
            return "MODE_OPAQUE";
        }
        if (i == 1) {
            return "MODE_SEMI_TRANSPARENT";
        }
        if (i == 2) {
            return "MODE_TRANSLUCENT";
        }
        if (i == 3) {
            return "MODE_LIGHTS_OUT";
        }
        if (i == 0) {
            return "MODE_TRANSPARENT";
        }
        if (i == 5) {
            return "MODE_WARNING";
        }
        if (i == 6) {
            return "MODE_LIGHTS_OUT_TRANSPARENT";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unknown mode ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    protected void applyModeBackground(final int n, final int n2, final boolean b) {
        this.mBarBackground.applyModeBackground(n, n2, b);
    }
    
    public void finishAnimations() {
        this.mBarBackground.finishAnimation();
    }
    
    public int getMode() {
        return this.mMode;
    }
    
    public boolean isAlwaysOpaque() {
        return this.mAlwaysOpaque;
    }
    
    protected boolean isLightsOut(final int n) {
        return n == 3 || n == 6;
    }
    
    protected void onTransition(final int n, final int n2, final boolean b) {
        this.applyModeBackground(n, n2, b);
    }
    
    public void transitionTo(int mMode, final boolean b) {
        int n = mMode;
        if (this.isAlwaysOpaque() && (mMode == 1 || mMode == 2 || (n = mMode) == 0)) {
            n = 4;
        }
        mMode = n;
        if (this.isAlwaysOpaque() && (mMode = n) == 6) {
            mMode = 3;
        }
        final int mMode2 = this.mMode;
        if (mMode2 == mMode) {
            return;
        }
        this.onTransition(mMode2, this.mMode = mMode, b);
    }
    
    protected static class BarBackgroundDrawable extends Drawable
    {
        private boolean mAnimating;
        private int mColor;
        private int mColorStart;
        private long mEndTime;
        private Rect mFrame;
        private final Drawable mGradient;
        private int mGradientAlpha;
        private int mGradientAlphaStart;
        private int mMode;
        private final int mOpaque;
        private Paint mPaint;
        private final int mSemiTransparent;
        private long mStartTime;
        private PorterDuffColorFilter mTintFilter;
        private final int mTransparent;
        private final int mWarning;
        
        public BarBackgroundDrawable(final Context context, final int n) {
            this.mMode = -1;
            this.mPaint = new Paint();
            context.getResources();
            this.mOpaque = context.getColor(R$color.system_bar_background_opaque);
            this.mSemiTransparent = context.getColor(17170988);
            this.mTransparent = context.getColor(R$color.system_bar_background_transparent);
            this.mWarning = Utils.getColorAttrDefaultColor(context, 16844099);
            this.mGradient = context.getDrawable(n);
        }
        
        public void applyModeBackground(final int n, final int mMode, final boolean mAnimating) {
            if (this.mMode == mMode) {
                return;
            }
            this.mMode = mMode;
            this.mAnimating = mAnimating;
            if (mAnimating) {
                final long elapsedRealtime = SystemClock.elapsedRealtime();
                this.mStartTime = elapsedRealtime;
                this.mEndTime = elapsedRealtime + 200L;
                this.mGradientAlphaStart = this.mGradientAlpha;
                this.mColorStart = this.mColor;
            }
            this.invalidateSelf();
        }
        
        public void draw(final Canvas canvas) {
            final int mMode = this.mMode;
            int n;
            if (mMode == 5) {
                n = this.mWarning;
            }
            else if (mMode == 2) {
                n = this.mSemiTransparent;
            }
            else if (mMode == 1) {
                n = this.mSemiTransparent;
            }
            else if (mMode != 0 && mMode != 6) {
                n = this.mOpaque;
            }
            else {
                n = this.mTransparent;
            }
            if (!this.mAnimating) {
                this.mColor = n;
                this.mGradientAlpha = 0;
            }
            else {
                final long elapsedRealtime = SystemClock.elapsedRealtime();
                final long mEndTime = this.mEndTime;
                if (elapsedRealtime >= mEndTime) {
                    this.mAnimating = false;
                    this.mColor = n;
                    this.mGradientAlpha = 0;
                }
                else {
                    final long mStartTime = this.mStartTime;
                    final float max = Math.max(0.0f, Math.min(Interpolators.LINEAR.getInterpolation((elapsedRealtime - mStartTime) / (float)(mEndTime - mStartTime)), 1.0f));
                    final float n2 = 0;
                    final float n3 = (float)this.mGradientAlphaStart;
                    final float n4 = 1.0f - max;
                    this.mGradientAlpha = (int)(n2 * max + n3 * n4);
                    this.mColor = Color.argb((int)(Color.alpha(n) * max + Color.alpha(this.mColorStart) * n4), (int)(Color.red(n) * max + Color.red(this.mColorStart) * n4), (int)(Color.green(n) * max + Color.green(this.mColorStart) * n4), (int)(max * Color.blue(n) + Color.blue(this.mColorStart) * n4));
                }
            }
            final int mGradientAlpha = this.mGradientAlpha;
            if (mGradientAlpha > 0) {
                this.mGradient.setAlpha(mGradientAlpha);
                this.mGradient.draw(canvas);
            }
            if (Color.alpha(this.mColor) > 0) {
                this.mPaint.setColor(this.mColor);
                final PorterDuffColorFilter mTintFilter = this.mTintFilter;
                if (mTintFilter != null) {
                    this.mPaint.setColorFilter((ColorFilter)mTintFilter);
                }
                final Rect mFrame = this.mFrame;
                if (mFrame != null) {
                    canvas.drawRect(mFrame, this.mPaint);
                }
                else {
                    canvas.drawPaint(this.mPaint);
                }
            }
            if (this.mAnimating) {
                this.invalidateSelf();
            }
        }
        
        public void finishAnimation() {
            if (this.mAnimating) {
                this.mAnimating = false;
                this.invalidateSelf();
            }
        }
        
        public int getOpacity() {
            return -3;
        }
        
        protected void onBoundsChange(final Rect bounds) {
            super.onBoundsChange(bounds);
            this.mGradient.setBounds(bounds);
        }
        
        public void setAlpha(final int n) {
        }
        
        public void setColorFilter(final ColorFilter colorFilter) {
        }
        
        public void setFrame(final Rect mFrame) {
            this.mFrame = mFrame;
        }
        
        public void setTint(final int n) {
            final PorterDuffColorFilter mTintFilter = this.mTintFilter;
            PorterDuff$Mode porterDuff$Mode;
            if (mTintFilter == null) {
                porterDuff$Mode = PorterDuff$Mode.SRC_IN;
            }
            else {
                porterDuff$Mode = mTintFilter.getMode();
            }
            final PorterDuffColorFilter mTintFilter2 = this.mTintFilter;
            if (mTintFilter2 == null || mTintFilter2.getColor() != n) {
                this.mTintFilter = new PorterDuffColorFilter(n, porterDuff$Mode);
            }
            this.invalidateSelf();
        }
        
        public void setTintMode(final PorterDuff$Mode porterDuff$Mode) {
            final PorterDuffColorFilter mTintFilter = this.mTintFilter;
            int color;
            if (mTintFilter == null) {
                color = 0;
            }
            else {
                color = mTintFilter.getColor();
            }
            final PorterDuffColorFilter mTintFilter2 = this.mTintFilter;
            if (mTintFilter2 == null || mTintFilter2.getMode() != porterDuff$Mode) {
                this.mTintFilter = new PorterDuffColorFilter(color, porterDuff$Mode);
            }
            this.invalidateSelf();
        }
    }
}
