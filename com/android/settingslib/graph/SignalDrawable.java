// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.graph;

import android.graphics.ColorFilter;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.Path$FillType;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.telephony.CellSignalStrength;
import android.animation.ArgbEvaluator;
import android.graphics.Path$Direction;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import com.android.settingslib.R$dimen;
import com.android.settingslib.Utils;
import com.android.settingslib.R$color;
import android.util.PathParser;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Handler;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.DrawableWrapper;

public class SignalDrawable extends DrawableWrapper
{
    private boolean mAnimating;
    private final Runnable mChangeDot;
    private int mCurrentDot;
    private final float mCutoutHeightFraction;
    private final Path mCutoutPath;
    private final float mCutoutWidthFraction;
    private float mDarkIntensity;
    private final int mDarkModeFillColor;
    private final Paint mForegroundPaint;
    private final Path mForegroundPath;
    private final Handler mHandler;
    private final int mIntrinsicSize;
    private final int mLightModeFillColor;
    private final Path mScaledXPath;
    private final Paint mTransparentPaint;
    private final Path mXPath;
    private final Matrix mXScaleMatrix;
    
    public SignalDrawable(final Context context) {
        super(context.getDrawable(17302817));
        this.mForegroundPaint = new Paint(1);
        this.mTransparentPaint = new Paint(1);
        this.mCutoutPath = new Path();
        this.mForegroundPath = new Path();
        this.mXPath = new Path();
        this.mXScaleMatrix = new Matrix();
        this.mScaledXPath = new Path();
        this.mDarkIntensity = -1.0f;
        this.mChangeDot = new Runnable() {
            @Override
            public void run() {
                if (++SignalDrawable.this.mCurrentDot == 3) {
                    SignalDrawable.this.mCurrentDot = 0;
                }
                SignalDrawable.this.invalidateSelf();
                SignalDrawable.this.mHandler.postDelayed(SignalDrawable.this.mChangeDot, 1000L);
            }
        };
        this.mXPath.set(PathParser.createPathFromPathData(context.getString(17039950)));
        this.updateScaledXPath();
        this.mCutoutWidthFraction = context.getResources().getFloat(17105090);
        this.mCutoutHeightFraction = context.getResources().getFloat(17105089);
        this.mDarkModeFillColor = Utils.getColorStateListDefaultColor(context, R$color.dark_mode_icon_color_single_tone);
        this.mLightModeFillColor = Utils.getColorStateListDefaultColor(context, R$color.light_mode_icon_color_single_tone);
        this.mIntrinsicSize = context.getResources().getDimensionPixelSize(R$dimen.signal_icon_size);
        this.mTransparentPaint.setColor(context.getColor(17170445));
        this.mTransparentPaint.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.SRC_IN));
        this.mHandler = new Handler();
        this.setDarkIntensity(0.0f);
    }
    
    private void drawDotAndPadding(final float n, final float n2, final float n3, float n4, final int n5) {
        if (n5 == this.mCurrentDot) {
            final Path mForegroundPath = this.mForegroundPath;
            final float n6 = n + n4;
            n4 += n2;
            mForegroundPath.addRect(n, n2, n6, n4, Path$Direction.CW);
            this.mCutoutPath.addRect(n - n3, n2 - n3, n6 + n3, n4 + n3, Path$Direction.CW);
        }
    }
    
    public static int getCarrierChangeState(final int n) {
        return n << 8 | 0x30000;
    }
    
    private int getColorForDarkIntensity(final float n, final int i, final int j) {
        return (int)ArgbEvaluator.getInstance().evaluate(n, (Object)i, (Object)j);
    }
    
    public static int getEmptyState(final int n) {
        return getState(0, n, true);
    }
    
    private int getFillColor(final float n) {
        return this.getColorForDarkIntensity(n, this.mLightModeFillColor, this.mDarkModeFillColor);
    }
    
    public static int getState(final int n) {
        return (n & 0xFF0000) >> 16;
    }
    
    public static int getState(final int n, final int n2, final boolean b) {
        int n3;
        if (b) {
            n3 = 2;
        }
        else {
            n3 = 0;
        }
        return n | (n2 << 8 | n3 << 16);
    }
    
    private boolean isInState(final int n) {
        return getState(this.getLevel()) == n;
    }
    
    private int unpackLevel(final int n) {
        int n2;
        if ((0xFF00 & n) >> 8 == CellSignalStrength.getNumSignalStrengthLevels() + 1) {
            n2 = 10;
        }
        else {
            n2 = 0;
        }
        return (n & 0xFF) + n2;
    }
    
    private void updateAnimation() {
        final boolean mAnimating = this.isInState(3) && this.isVisible();
        if (mAnimating == this.mAnimating) {
            return;
        }
        this.mAnimating = mAnimating;
        if (mAnimating) {
            this.mChangeDot.run();
        }
        else {
            this.mHandler.removeCallbacks(this.mChangeDot);
        }
    }
    
    private void updateScaledXPath() {
        if (this.getBounds().isEmpty()) {
            this.mXScaleMatrix.setScale(1.0f, 1.0f);
        }
        else {
            this.mXScaleMatrix.setScale(this.getBounds().width() / 24.0f, this.getBounds().height() / 24.0f);
        }
        this.mXPath.transform(this.mXScaleMatrix, this.mScaledXPath);
    }
    
    public void draw(final Canvas canvas) {
        canvas.saveLayer((RectF)null, (Paint)null);
        final float n = (float)this.getBounds().width();
        final float n2 = (float)this.getBounds().height();
        final int layoutDirection = this.getLayoutDirection();
        boolean b = true;
        if (layoutDirection != 1) {
            b = false;
        }
        if (b) {
            canvas.save();
            canvas.translate(n, 0.0f);
            canvas.scale(-1.0f, 1.0f);
        }
        super.draw(canvas);
        this.mCutoutPath.reset();
        this.mCutoutPath.setFillType(Path$FillType.WINDING);
        final float n3 = (float)Math.round(0.083333336f * n);
        if (this.isInState(3)) {
            final float n4 = 0.125f * n2;
            final float n5 = n2 * 0.0625f;
            final float n6 = n5 + n4;
            final float n7 = n - n3 - n4;
            final float n8 = n2 - n3 - n4;
            this.mForegroundPath.reset();
            this.drawDotAndPadding(n7, n8, n5, n4, 2);
            this.drawDotAndPadding(n7 - n6, n8, n5, n4, 1);
            this.drawDotAndPadding(n7 - n6 * 2.0f, n8, n5, n4, 0);
            canvas.drawPath(this.mCutoutPath, this.mTransparentPaint);
            canvas.drawPath(this.mForegroundPath, this.mForegroundPaint);
        }
        else if (this.isInState(2)) {
            final float n9 = this.mCutoutWidthFraction * n / 24.0f;
            final float n10 = this.mCutoutHeightFraction * n2 / 24.0f;
            this.mCutoutPath.moveTo(n, n2);
            this.mCutoutPath.rLineTo(-n9, 0.0f);
            this.mCutoutPath.rLineTo(0.0f, -n10);
            this.mCutoutPath.rLineTo(n9, 0.0f);
            this.mCutoutPath.rLineTo(0.0f, n10);
            canvas.drawPath(this.mCutoutPath, this.mTransparentPaint);
            canvas.drawPath(this.mScaledXPath, this.mForegroundPaint);
        }
        if (b) {
            canvas.restore();
        }
        canvas.restore();
    }
    
    public int getIntrinsicHeight() {
        return this.mIntrinsicSize;
    }
    
    public int getIntrinsicWidth() {
        return this.mIntrinsicSize;
    }
    
    protected void onBoundsChange(final Rect rect) {
        super.onBoundsChange(rect);
        this.updateScaledXPath();
        this.invalidateSelf();
    }
    
    protected boolean onLevelChange(final int n) {
        super.onLevelChange(this.unpackLevel(n));
        this.updateAnimation();
        this.setTintList(ColorStateList.valueOf(this.mForegroundPaint.getColor()));
        this.invalidateSelf();
        return true;
    }
    
    public void setAlpha(final int n) {
        super.setAlpha(n);
        this.mForegroundPaint.setAlpha(n);
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        this.mForegroundPaint.setColorFilter(colorFilter);
    }
    
    public void setDarkIntensity(final float n) {
        if (n == this.mDarkIntensity) {
            return;
        }
        this.setTintList(ColorStateList.valueOf(this.getFillColor(n)));
    }
    
    public void setTintList(final ColorStateList tintList) {
        super.setTintList(tintList);
        final int color = this.mForegroundPaint.getColor();
        this.mForegroundPaint.setColor(tintList.getDefaultColor());
        if (color != this.mForegroundPaint.getColor()) {
            this.invalidateSelf();
        }
    }
    
    public boolean setVisible(final boolean b, final boolean b2) {
        final boolean setVisible = super.setVisible(b, b2);
        this.updateAnimation();
        return setVisible;
    }
}
