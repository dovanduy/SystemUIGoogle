// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.graphics.Canvas;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import com.android.settingslib.Utils;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.Paint;
import android.view.View;

public class DataUsageGraph extends View
{
    private long mLimitLevel;
    private final int mMarkerWidth;
    private long mMaxLevel;
    private final int mOverlimitColor;
    private final Paint mTmpPaint;
    private final RectF mTmpRect;
    private final int mTrackColor;
    private final int mUsageColor;
    private long mUsageLevel;
    private final int mWarningColor;
    private long mWarningLevel;
    
    public DataUsageGraph(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTmpRect = new RectF();
        this.mTmpPaint = new Paint();
        final Resources resources = context.getResources();
        this.mTrackColor = Utils.getColorStateListDefaultColor(context, R$color.data_usage_graph_track);
        this.mWarningColor = Utils.getColorStateListDefaultColor(context, R$color.data_usage_graph_warning);
        this.mUsageColor = Utils.getColorAccentDefaultColor(context);
        this.mOverlimitColor = Utils.getColorErrorDefaultColor(context);
        this.mMarkerWidth = resources.getDimensionPixelSize(R$dimen.data_usage_graph_marker_width);
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final RectF mTmpRect = this.mTmpRect;
        final Paint mTmpPaint = this.mTmpPaint;
        final int width = this.getWidth();
        final int height = this.getHeight();
        final long mLimitLevel = this.mLimitLevel;
        final boolean b = mLimitLevel > 0L && this.mUsageLevel > mLimitLevel;
        final float n = (float)width;
        final float n2 = (float)this.mUsageLevel;
        final long mMaxLevel = this.mMaxLevel;
        float min = n2 / mMaxLevel * n;
        if (b) {
            final float n3 = this.mLimitLevel / (float)mMaxLevel;
            final int mMarkerWidth = this.mMarkerWidth;
            min = Math.min(Math.max(n3 * n - mMarkerWidth / 2, (float)mMarkerWidth), (float)(width - this.mMarkerWidth * 2));
            mTmpRect.set(this.mMarkerWidth + min, 0.0f, n, (float)height);
            mTmpPaint.setColor(this.mOverlimitColor);
            canvas.drawRect(mTmpRect, mTmpPaint);
        }
        else {
            mTmpRect.set(0.0f, 0.0f, n, (float)height);
            mTmpPaint.setColor(this.mTrackColor);
            canvas.drawRect(mTmpRect, mTmpPaint);
        }
        final float n4 = (float)height;
        mTmpRect.set(0.0f, 0.0f, min, n4);
        mTmpPaint.setColor(this.mUsageColor);
        canvas.drawRect(mTmpRect, mTmpPaint);
        final float min2 = Math.min(Math.max(n * (this.mWarningLevel / (float)this.mMaxLevel) - this.mMarkerWidth / 2, 0.0f), (float)(width - this.mMarkerWidth));
        mTmpRect.set(min2, 0.0f, this.mMarkerWidth + min2, n4);
        mTmpPaint.setColor(this.mWarningColor);
        canvas.drawRect(mTmpRect, mTmpPaint);
    }
    
    public void setLevels(final long b, final long b2, final long b3) {
        this.mLimitLevel = Math.max(0L, b);
        this.mWarningLevel = Math.max(0L, b2);
        this.mUsageLevel = Math.max(0L, b3);
        this.mMaxLevel = Math.max(Math.max(Math.max(this.mLimitLevel, this.mWarningLevel), this.mUsageLevel), 1L);
        this.postInvalidate();
    }
}
