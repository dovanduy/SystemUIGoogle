// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.graph;

import android.graphics.ColorFilter;
import android.graphics.Path$Op;
import android.graphics.Path$Direction;
import android.graphics.Path$FillType;
import android.graphics.Canvas;
import android.content.res.TypedArray;
import android.content.res.Resources;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$color;
import android.graphics.Paint$Align;
import android.graphics.Typeface;
import android.graphics.Paint$Style;
import com.android.settingslib.R$fraction;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.R$array;
import android.graphics.Rect;
import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class BatteryMeterDrawableBase extends Drawable
{
    protected final Paint mBatteryPaint;
    private final RectF mBoltFrame;
    protected final Paint mBoltPaint;
    private final Path mBoltPath;
    private final float[] mBoltPoints;
    private final RectF mButtonFrame;
    protected float mButtonHeightFraction;
    private int mChargeColor;
    private boolean mCharging;
    private final int[] mColors;
    protected final Context mContext;
    private final int mCriticalLevel;
    private final RectF mFrame;
    protected final Paint mFramePaint;
    private int mHeight;
    private int mIconTint;
    private final int mIntrinsicHeight;
    private final int mIntrinsicWidth;
    private int mLevel;
    private final Path mOutlinePath;
    private final Rect mPadding;
    private final RectF mPlusFrame;
    protected final Paint mPlusPaint;
    private final Path mPlusPath;
    private final float[] mPlusPoints;
    protected boolean mPowerSaveAsColorError;
    private boolean mPowerSaveEnabled;
    protected final Paint mPowersavePaint;
    private final Path mShapePath;
    private boolean mShowPercent;
    private float mTextHeight;
    protected final Paint mTextPaint;
    private final Path mTextPath;
    private String mWarningString;
    private float mWarningTextHeight;
    protected final Paint mWarningTextPaint;
    private int mWidth;
    
    public BatteryMeterDrawableBase(final Context mContext, final int color) {
        this.mLevel = -1;
        this.mPowerSaveAsColorError = true;
        this.mIconTint = -1;
        this.mBoltPath = new Path();
        this.mPlusPath = new Path();
        this.mPadding = new Rect();
        this.mFrame = new RectF();
        this.mButtonFrame = new RectF();
        this.mBoltFrame = new RectF();
        this.mPlusFrame = new RectF();
        this.mShapePath = new Path();
        this.mOutlinePath = new Path();
        this.mTextPath = new Path();
        this.mContext = mContext;
        final Resources resources = mContext.getResources();
        final TypedArray obtainTypedArray = resources.obtainTypedArray(R$array.batterymeter_color_levels);
        final TypedArray obtainTypedArray2 = resources.obtainTypedArray(R$array.batterymeter_color_values);
        final int length = obtainTypedArray.length();
        this.mColors = new int[length * 2];
        for (int i = 0; i < length; ++i) {
            final int[] mColors = this.mColors;
            final int n = i * 2;
            mColors[n] = obtainTypedArray.getInt(i, 0);
            if (obtainTypedArray2.getType(i) == 2) {
                this.mColors[n + 1] = Utils.getColorAttrDefaultColor(mContext, obtainTypedArray2.getThemeAttributeId(i, 0));
            }
            else {
                this.mColors[n + 1] = obtainTypedArray2.getColor(i, 0);
            }
        }
        obtainTypedArray.recycle();
        obtainTypedArray2.recycle();
        this.mWarningString = mContext.getString(R$string.battery_meter_very_low_overlay_symbol);
        this.mCriticalLevel = this.mContext.getResources().getInteger(17694765);
        this.mButtonHeightFraction = mContext.getResources().getFraction(R$fraction.battery_button_height_fraction, 1, 1);
        mContext.getResources().getFraction(R$fraction.battery_subpixel_smoothing_left, 1, 1);
        mContext.getResources().getFraction(R$fraction.battery_subpixel_smoothing_right, 1, 1);
        (this.mFramePaint = new Paint(1)).setColor(color);
        this.mFramePaint.setDither(true);
        this.mFramePaint.setStrokeWidth(0.0f);
        this.mFramePaint.setStyle(Paint$Style.FILL_AND_STROKE);
        (this.mBatteryPaint = new Paint(1)).setDither(true);
        this.mBatteryPaint.setStrokeWidth(0.0f);
        this.mBatteryPaint.setStyle(Paint$Style.FILL_AND_STROKE);
        (this.mTextPaint = new Paint(1)).setTypeface(Typeface.create("sans-serif-condensed", 1));
        this.mTextPaint.setTextAlign(Paint$Align.CENTER);
        (this.mWarningTextPaint = new Paint(1)).setTypeface(Typeface.create("sans-serif", 1));
        this.mWarningTextPaint.setTextAlign(Paint$Align.CENTER);
        final int[] mColors2 = this.mColors;
        if (mColors2.length > 1) {
            this.mWarningTextPaint.setColor(mColors2[1]);
        }
        this.mChargeColor = Utils.getColorStateListDefaultColor(this.mContext, R$color.meter_consumed_color);
        (this.mBoltPaint = new Paint(1)).setColor(Utils.getColorStateListDefaultColor(this.mContext, R$color.batterymeter_bolt_color));
        this.mBoltPoints = loadPoints(resources, R$array.batterymeter_bolt_points);
        (this.mPlusPaint = new Paint(1)).setColor(Utils.getColorStateListDefaultColor(this.mContext, R$color.batterymeter_plus_color));
        this.mPlusPoints = loadPoints(resources, R$array.batterymeter_plus_points);
        (this.mPowersavePaint = new Paint(1)).setColor(this.mPlusPaint.getColor());
        this.mPowersavePaint.setStyle(Paint$Style.STROKE);
        this.mPowersavePaint.setStrokeWidth((float)mContext.getResources().getDimensionPixelSize(R$dimen.battery_powersave_outline_thickness));
        this.mIntrinsicWidth = mContext.getResources().getDimensionPixelSize(R$dimen.battery_width);
        this.mIntrinsicHeight = mContext.getResources().getDimensionPixelSize(R$dimen.battery_height);
    }
    
    private int getColorForLevel(final int n) {
        int n2 = 0;
        int n3 = 0;
        while (true) {
            final int[] mColors = this.mColors;
            if (n2 >= mColors.length) {
                return n3;
            }
            final int n4 = mColors[n2];
            n3 = mColors[n2 + 1];
            if (n <= n4) {
                if (n2 == mColors.length - 2) {
                    return this.mIconTint;
                }
                return n3;
            }
            else {
                n2 += 2;
            }
        }
    }
    
    private static float[] loadPoints(final Resources resources, int max) {
        final int[] intArray = resources.getIntArray(max);
        final int n = 0;
        int i = 0;
        int max2;
        max = (max2 = i);
        while (i < intArray.length) {
            max = Math.max(max, intArray[i]);
            max2 = Math.max(max2, intArray[i + 1]);
            i += 2;
        }
        final float[] array = new float[intArray.length];
        for (int j = n; j < intArray.length; j += 2) {
            array[j] = intArray[j] / (float)max;
            final int n2 = j + 1;
            array[n2] = intArray[n2] / (float)max2;
        }
        return array;
    }
    
    private void updateSize() {
        final Rect bounds = this.getBounds();
        final int bottom = bounds.bottom;
        final Rect mPadding = this.mPadding;
        final int mHeight = bottom - mPadding.bottom - (bounds.top + mPadding.top);
        this.mHeight = mHeight;
        this.mWidth = bounds.right - mPadding.right - (bounds.left + mPadding.left);
        this.mWarningTextPaint.setTextSize(mHeight * 0.75f);
        this.mWarningTextHeight = -this.mWarningTextPaint.getFontMetrics().ascent;
    }
    
    protected int batteryColorForLevel(int n) {
        if (!this.mCharging && (!this.mPowerSaveEnabled || !this.mPowerSaveAsColorError)) {
            n = this.getColorForLevel(n);
        }
        else {
            n = this.mChargeColor;
        }
        return n;
    }
    
    public void draw(final Canvas canvas) {
        final int mLevel = this.mLevel;
        final Rect bounds = this.getBounds();
        if (mLevel == -1) {
            return;
        }
        float n = mLevel / 100.0f;
        final int mHeight = this.mHeight;
        final int n2 = (int)(this.getAspectRatio() * this.mHeight);
        final int n3 = (this.mWidth - n2) / 2;
        final float n4 = (float)mHeight;
        final int round = Math.round(this.mButtonHeightFraction * n4);
        final Rect mPadding = this.mPadding;
        final int n5 = mPadding.left + bounds.left;
        final int n6 = bounds.bottom - mPadding.bottom - mHeight;
        final RectF mFrame = this.mFrame;
        final float n7 = (float)n5;
        final float n8 = (float)n6;
        mFrame.set(n7, n8, (float)(n5 + n2), (float)(mHeight + n6));
        this.mFrame.offset((float)n3, 0.0f);
        final RectF mButtonFrame = this.mButtonFrame;
        final float left = this.mFrame.left;
        final float n9 = n2 * 0.28f;
        final float n10 = (float)Math.round(n9);
        final RectF mFrame2 = this.mFrame;
        final float top = mFrame2.top;
        final float right = mFrame2.right;
        final float n11 = (float)Math.round(n9);
        final float top2 = this.mFrame.top;
        final float n12 = (float)round;
        mButtonFrame.set(left + n10, top, right - n11, top2 + n12);
        final RectF mFrame3 = this.mFrame;
        mFrame3.top += n12;
        this.mBatteryPaint.setColor(this.batteryColorForLevel(mLevel));
        if (mLevel >= 96) {
            n = 1.0f;
        }
        else if (mLevel <= this.mCriticalLevel) {
            n = 0.0f;
        }
        float top3;
        if (n == 1.0f) {
            top3 = this.mButtonFrame.top;
        }
        else {
            final RectF mFrame4 = this.mFrame;
            top3 = mFrame4.height() * (1.0f - n) + mFrame4.top;
        }
        this.mShapePath.reset();
        this.mOutlinePath.reset();
        final float n13 = this.getRadiusRatio() * (this.mFrame.height() + n12);
        this.mShapePath.setFillType(Path$FillType.WINDING);
        this.mShapePath.addRoundRect(this.mFrame, n13, n13, Path$Direction.CW);
        this.mShapePath.addRect(this.mButtonFrame, Path$Direction.CW);
        this.mOutlinePath.addRoundRect(this.mFrame, n13, n13, Path$Direction.CW);
        final Path path = new Path();
        path.addRect(this.mButtonFrame, Path$Direction.CW);
        this.mOutlinePath.op(path, Path$Op.XOR);
        final boolean mCharging = this.mCharging;
        final int n14 = 0;
        final int n15 = 0;
        if (mCharging) {
            final RectF mFrame5 = this.mFrame;
            final float n16 = mFrame5.left + mFrame5.width() / 4.0f + 1.0f;
            final RectF mFrame6 = this.mFrame;
            final float n17 = mFrame6.top + mFrame6.height() / 6.0f;
            final RectF mFrame7 = this.mFrame;
            final float n18 = mFrame7.right - mFrame7.width() / 4.0f + 1.0f;
            final RectF mFrame8 = this.mFrame;
            final float n19 = mFrame8.bottom - mFrame8.height() / 10.0f;
            final RectF mBoltFrame = this.mBoltFrame;
            if (mBoltFrame.left != n16 || mBoltFrame.top != n17 || mBoltFrame.right != n18 || mBoltFrame.bottom != n19) {
                this.mBoltFrame.set(n16, n17, n18, n19);
                this.mBoltPath.reset();
                final Path mBoltPath = this.mBoltPath;
                final RectF mBoltFrame2 = this.mBoltFrame;
                final float left2 = mBoltFrame2.left;
                final float n20 = this.mBoltPoints[0];
                final float width = mBoltFrame2.width();
                final RectF mBoltFrame3 = this.mBoltFrame;
                mBoltPath.moveTo(left2 + n20 * width, mBoltFrame3.top + this.mBoltPoints[1] * mBoltFrame3.height());
                int n21 = 2;
                float[] mBoltPoints;
                while (true) {
                    mBoltPoints = this.mBoltPoints;
                    if (n21 >= mBoltPoints.length) {
                        break;
                    }
                    final Path mBoltPath2 = this.mBoltPath;
                    final RectF mBoltFrame4 = this.mBoltFrame;
                    final float left3 = mBoltFrame4.left;
                    final float n22 = mBoltPoints[n21];
                    final float width2 = mBoltFrame4.width();
                    final RectF mBoltFrame5 = this.mBoltFrame;
                    mBoltPath2.lineTo(left3 + n22 * width2, mBoltFrame5.top + this.mBoltPoints[n21 + 1] * mBoltFrame5.height());
                    n21 += 2;
                }
                final Path mBoltPath3 = this.mBoltPath;
                final RectF mBoltFrame6 = this.mBoltFrame;
                final float left4 = mBoltFrame6.left;
                final float n23 = mBoltPoints[0];
                final float width3 = mBoltFrame6.width();
                final RectF mBoltFrame7 = this.mBoltFrame;
                mBoltPath3.lineTo(left4 + n23 * width3, mBoltFrame7.top + this.mBoltPoints[1] * mBoltFrame7.height());
            }
            final RectF mBoltFrame8 = this.mBoltFrame;
            final float bottom = mBoltFrame8.bottom;
            if (Math.min(Math.max((bottom - top3) / (bottom - mBoltFrame8.top), 0.0f), 1.0f) <= 0.3f) {
                canvas.drawPath(this.mBoltPath, this.mBoltPaint);
            }
            else {
                this.mShapePath.op(this.mBoltPath, Path$Op.DIFFERENCE);
            }
        }
        else if (this.mPowerSaveEnabled) {
            final float n24 = this.mFrame.width() * 2.0f / 3.0f;
            final RectF mFrame9 = this.mFrame;
            final float n25 = mFrame9.left + (mFrame9.width() - n24) / 2.0f;
            final RectF mFrame10 = this.mFrame;
            final float n26 = mFrame10.top + (mFrame10.height() - n24) / 2.0f;
            final RectF mFrame11 = this.mFrame;
            final float n27 = mFrame11.right - (mFrame11.width() - n24) / 2.0f;
            final RectF mFrame12 = this.mFrame;
            final float n28 = mFrame12.bottom - (mFrame12.height() - n24) / 2.0f;
            final RectF mPlusFrame = this.mPlusFrame;
            if (mPlusFrame.left != n25 || mPlusFrame.top != n26 || mPlusFrame.right != n27 || mPlusFrame.bottom != n28) {
                this.mPlusFrame.set(n25, n26, n27, n28);
                this.mPlusPath.reset();
                final Path mPlusPath = this.mPlusPath;
                final RectF mPlusFrame2 = this.mPlusFrame;
                final float left5 = mPlusFrame2.left;
                final float n29 = this.mPlusPoints[0];
                final float width4 = mPlusFrame2.width();
                final RectF mPlusFrame3 = this.mPlusFrame;
                mPlusPath.moveTo(left5 + n29 * width4, mPlusFrame3.top + this.mPlusPoints[1] * mPlusFrame3.height());
                int n30 = 2;
                float[] mPlusPoints;
                while (true) {
                    mPlusPoints = this.mPlusPoints;
                    if (n30 >= mPlusPoints.length) {
                        break;
                    }
                    final Path mPlusPath2 = this.mPlusPath;
                    final RectF mPlusFrame4 = this.mPlusFrame;
                    final float left6 = mPlusFrame4.left;
                    final float n31 = mPlusPoints[n30];
                    final float width5 = mPlusFrame4.width();
                    final RectF mPlusFrame5 = this.mPlusFrame;
                    mPlusPath2.lineTo(left6 + n31 * width5, mPlusFrame5.top + this.mPlusPoints[n30 + 1] * mPlusFrame5.height());
                    n30 += 2;
                }
                final Path mPlusPath3 = this.mPlusPath;
                final RectF mPlusFrame6 = this.mPlusFrame;
                final float left7 = mPlusFrame6.left;
                final float n32 = mPlusPoints[0];
                final float width6 = mPlusFrame6.width();
                final RectF mPlusFrame7 = this.mPlusFrame;
                mPlusPath3.lineTo(left7 + n32 * width6, mPlusFrame7.top + this.mPlusPoints[1] * mPlusFrame7.height());
            }
            this.mShapePath.op(this.mPlusPath, Path$Op.DIFFERENCE);
            if (this.mPowerSaveAsColorError) {
                canvas.drawPath(this.mPlusPath, this.mPlusPaint);
            }
        }
        String s = null;
        float n37;
        int n38;
        float n39;
        if (!this.mCharging && !this.mPowerSaveEnabled && mLevel > this.mCriticalLevel && this.mShowPercent) {
            this.mTextPaint.setColor(this.getColorForLevel(mLevel));
            final Paint mTextPaint = this.mTextPaint;
            float n33;
            if (this.mLevel == 100) {
                n33 = 0.38f;
            }
            else {
                n33 = 0.5f;
            }
            mTextPaint.setTextSize(n4 * n33);
            this.mTextHeight = -this.mTextPaint.getFontMetrics().ascent;
            final String value = String.valueOf(mLevel);
            final float n34 = this.mWidth * 0.5f + n7;
            final float n35 = (this.mHeight + this.mTextHeight) * 0.47f + n8;
            int n36 = n15;
            if (top3 > n35) {
                n36 = 1;
            }
            s = value;
            n37 = n34;
            n38 = n36;
            n39 = n35;
            if (n36 == 0) {
                this.mTextPath.reset();
                this.mTextPaint.getTextPath(value, 0, value.length(), n34, n35, this.mTextPath);
                this.mShapePath.op(this.mTextPath, Path$Op.DIFFERENCE);
                s = value;
                n37 = n34;
                n38 = n36;
                n39 = n35;
            }
        }
        else {
            n37 = 0.0f;
            n39 = 0.0f;
            n38 = n14;
        }
        canvas.drawPath(this.mShapePath, this.mFramePaint);
        this.mFrame.top = top3;
        canvas.save();
        canvas.clipRect(this.mFrame);
        canvas.drawPath(this.mShapePath, this.mBatteryPaint);
        canvas.restore();
        if (!this.mCharging && !this.mPowerSaveEnabled) {
            if (mLevel <= this.mCriticalLevel) {
                canvas.drawText(this.mWarningString, this.mWidth * 0.5f + n7, (this.mHeight + this.mWarningTextHeight) * 0.48f + n8, this.mWarningTextPaint);
            }
            else if (n38 != 0) {
                canvas.drawText(s, n37, n39, this.mTextPaint);
            }
        }
        if (!this.mCharging && this.mPowerSaveEnabled && this.mPowerSaveAsColorError) {
            canvas.drawPath(this.mOutlinePath, this.mPowersavePaint);
        }
    }
    
    protected float getAspectRatio() {
        return 0.58f;
    }
    
    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }
    
    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }
    
    public int getOpacity() {
        return 0;
    }
    
    public boolean getPadding(final Rect rect) {
        final Rect mPadding = this.mPadding;
        if (mPadding.left == 0 && mPadding.top == 0 && mPadding.right == 0 && mPadding.bottom == 0) {
            return super.getPadding(rect);
        }
        rect.set(this.mPadding);
        return true;
    }
    
    protected float getRadiusRatio() {
        return 0.05882353f;
    }
    
    protected void postInvalidate() {
        this.unscheduleSelf((Runnable)new _$$Lambda$ExJ0HHRzS2_LMtcBJqtFiovbn0w(this));
        this.scheduleSelf((Runnable)new _$$Lambda$ExJ0HHRzS2_LMtcBJqtFiovbn0w(this), 0L);
    }
    
    public void setAlpha(final int n) {
    }
    
    public void setBatteryLevel(final int mLevel) {
        this.mLevel = mLevel;
        this.postInvalidate();
    }
    
    public void setBounds(final int n, final int n2, final int n3, final int n4) {
        super.setBounds(n, n2, n3, n4);
        this.updateSize();
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mFramePaint.setColorFilter(colorFilter);
        this.mBatteryPaint.setColorFilter(colorFilter);
        this.mWarningTextPaint.setColorFilter(colorFilter);
        this.mBoltPaint.setColorFilter(colorFilter);
        this.mPlusPaint.setColorFilter(colorFilter);
    }
    
    public void setPadding(final int left, final int top, final int right, final int bottom) {
        final Rect mPadding = this.mPadding;
        mPadding.left = left;
        mPadding.top = top;
        mPadding.right = right;
        mPadding.bottom = bottom;
        this.updateSize();
    }
}
