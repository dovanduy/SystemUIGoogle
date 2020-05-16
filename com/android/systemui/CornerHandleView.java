// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.animation.ArgbEvaluator;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import android.graphics.Paint$Cap;
import android.graphics.Paint$Style;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Paint;
import android.view.View;

public class CornerHandleView extends View
{
    private int mDarkColor;
    private int mLightColor;
    private Paint mPaint;
    private Path mPath;
    private boolean mRequiresInvalidate;
    
    public CornerHandleView(final Context context, final AttributeSet set) {
        super(context, set);
        (this.mPaint = new Paint()).setAntiAlias(true);
        this.mPaint.setStyle(Paint$Style.STROKE);
        this.mPaint.setStrokeCap(Paint$Cap.ROUND);
        this.mPaint.setStrokeWidth(this.getStrokePx());
        final int themeAttr = Utils.getThemeAttr(super.mContext, R$attr.darkIconTheme);
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(super.mContext, Utils.getThemeAttr(super.mContext, R$attr.lightIconTheme));
        final ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(super.mContext, themeAttr);
        this.mLightColor = Utils.getColorAttrDefaultColor((Context)contextThemeWrapper, R$attr.singleToneColor);
        this.mDarkColor = Utils.getColorAttrDefaultColor((Context)contextThemeWrapper2, R$attr.singleToneColor);
        this.updatePath();
    }
    
    private static float convertDpToPixel(final float n, final Context context) {
        return n * (context.getResources().getDisplayMetrics().densityDpi / 160.0f);
    }
    
    private static float convertPixelToDp(final float n, final Context context) {
        return n * 160.0f / context.getResources().getDisplayMetrics().densityDpi;
    }
    
    private float getAngle() {
        float n;
        if ((n = 31.0f / convertPixelToDp(this.getOuterRadiusPx() * 2.0f * 3.1415927f, super.mContext) * 360.0f) > 90.0f) {
            n = 90.0f;
        }
        return n;
    }
    
    private float getInnerRadiusPx() {
        return this.getOuterRadiusPx() - this.getMarginPx();
    }
    
    private float getMarginPx() {
        return convertDpToPixel(8.0f, super.mContext);
    }
    
    private float getOuterRadiusPx() {
        int n;
        if ((n = this.getResources().getDimensionPixelSize(17105447)) == 0) {
            n = this.getResources().getDimensionPixelSize(17105445);
        }
        int dimensionPixelSize;
        if ((dimensionPixelSize = n) == 0) {
            dimensionPixelSize = this.getResources().getDimensionPixelSize(17105449);
        }
        int n2;
        if ((n2 = dimensionPixelSize) == 0) {
            n2 = (int)convertDpToPixel(15.0f, super.mContext);
        }
        return (float)n2;
    }
    
    private float getStrokePx() {
        float n;
        if (this.getAngle() < 90.0f) {
            n = 2.0f;
        }
        else {
            n = 1.95f;
        }
        return convertDpToPixel(n, this.getContext());
    }
    
    private void updatePath() {
        this.mPath = new Path();
        final float marginPx = this.getMarginPx();
        final float innerRadiusPx = this.getInnerRadiusPx();
        final float n = this.getStrokePx() / 2.0f;
        final float angle = this.getAngle();
        final float n2 = (90.0f - angle) / 2.0f + 180.0f;
        final float n3 = marginPx + n;
        final float n4 = innerRadiusPx * 2.0f;
        final float n5 = marginPx + n4 - n;
        final RectF rectF = new RectF(n3, n3, n5, n5);
        if (angle >= 90.0f) {
            final float convertDpToPixel = convertDpToPixel((31.0f - convertPixelToDp(n4 * 3.1415927f, super.mContext) * this.getAngle() / 360.0f - 8.0f) / 2.0f, super.mContext);
            final Path mPath = this.mPath;
            final float n6 = marginPx + innerRadiusPx;
            final float n7 = convertDpToPixel + n6;
            mPath.moveTo(n3, n7);
            this.mPath.lineTo(n3, n6);
            this.mPath.arcTo(rectF, n2, angle);
            this.mPath.moveTo(n6, n3);
            this.mPath.lineTo(n7, n3);
        }
        else {
            this.mPath.arcTo(rectF, n2, angle);
        }
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(this.mPath, this.mPaint);
    }
    
    public void setAlpha(final float alpha) {
        super.setAlpha(alpha);
        if (alpha > 0.0f && this.mRequiresInvalidate) {
            this.mRequiresInvalidate = false;
            this.invalidate();
        }
    }
    
    public void updateDarkness(final float n) {
        final int intValue = (int)ArgbEvaluator.getInstance().evaluate(n, (Object)this.mLightColor, (Object)this.mDarkColor);
        if (this.mPaint.getColor() != intValue) {
            this.mPaint.setColor(intValue);
            if (this.getVisibility() == 0 && this.getAlpha() > 0.0f) {
                this.invalidate();
            }
            else {
                this.mRequiresInvalidate = true;
            }
        }
    }
}
