// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.content.res.TypedArray;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import android.graphics.Shader;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;

public class HorizontalGridView extends BaseGridView
{
    private boolean mFadingHighEdge;
    private boolean mFadingLowEdge;
    private LinearGradient mHighFadeShader;
    private int mHighFadeShaderLength;
    private int mHighFadeShaderOffset;
    private LinearGradient mLowFadeShader;
    private int mLowFadeShaderLength;
    private int mLowFadeShaderOffset;
    private Bitmap mTempBitmapHigh;
    private Bitmap mTempBitmapLow;
    private Paint mTempPaint;
    private Rect mTempRect;
    
    public HorizontalGridView(final Context context) {
        this(context, null);
    }
    
    public HorizontalGridView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public HorizontalGridView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mTempPaint = new Paint();
        this.mTempRect = new Rect();
        super.mLayoutManager.setOrientation(0);
        this.initAttributes(context, set);
    }
    
    private Bitmap getTempBitmapHigh() {
        final Bitmap mTempBitmapHigh = this.mTempBitmapHigh;
        if (mTempBitmapHigh == null || mTempBitmapHigh.getWidth() != this.mHighFadeShaderLength || this.mTempBitmapHigh.getHeight() != this.getHeight()) {
            this.mTempBitmapHigh = Bitmap.createBitmap(this.mHighFadeShaderLength, this.getHeight(), Bitmap$Config.ARGB_8888);
        }
        return this.mTempBitmapHigh;
    }
    
    private Bitmap getTempBitmapLow() {
        final Bitmap mTempBitmapLow = this.mTempBitmapLow;
        if (mTempBitmapLow == null || mTempBitmapLow.getWidth() != this.mLowFadeShaderLength || this.mTempBitmapLow.getHeight() != this.getHeight()) {
            this.mTempBitmapLow = Bitmap.createBitmap(this.mLowFadeShaderLength, this.getHeight(), Bitmap$Config.ARGB_8888);
        }
        return this.mTempBitmapLow;
    }
    
    private boolean needsFadingHighEdge() {
        if (!this.mFadingHighEdge) {
            return false;
        }
        for (int i = this.getChildCount() - 1; i >= 0; --i) {
            if (super.mLayoutManager.getOpticalRight(this.getChildAt(i)) > this.getWidth() - this.getPaddingRight() + this.mHighFadeShaderOffset) {
                return true;
            }
        }
        return false;
    }
    
    private boolean needsFadingLowEdge() {
        if (!this.mFadingLowEdge) {
            return false;
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            if (super.mLayoutManager.getOpticalLeft(this.getChildAt(i)) < this.getPaddingLeft() - this.mLowFadeShaderOffset) {
                return true;
            }
        }
        return false;
    }
    
    private void updateLayerType() {
        if (!this.mFadingLowEdge && !this.mFadingHighEdge) {
            this.setLayerType(0, (Paint)null);
            this.setWillNotDraw(true);
        }
        else {
            this.setLayerType(2, (Paint)null);
            this.setWillNotDraw(false);
        }
    }
    
    @Override
    public void draw(final Canvas canvas) {
        final boolean needsFadingLowEdge = this.needsFadingLowEdge();
        final boolean needsFadingHighEdge = this.needsFadingHighEdge();
        if (!needsFadingLowEdge) {
            this.mTempBitmapLow = null;
        }
        if (!needsFadingHighEdge) {
            this.mTempBitmapHigh = null;
        }
        if (!needsFadingLowEdge && !needsFadingHighEdge) {
            super.draw(canvas);
            return;
        }
        int n;
        if (this.mFadingLowEdge) {
            n = this.getPaddingLeft() - this.mLowFadeShaderOffset - this.mLowFadeShaderLength;
        }
        else {
            n = 0;
        }
        int width;
        if (this.mFadingHighEdge) {
            width = this.getWidth() - this.getPaddingRight() + this.mHighFadeShaderOffset + this.mHighFadeShaderLength;
        }
        else {
            width = this.getWidth();
        }
        final int save = canvas.save();
        int mLowFadeShaderLength;
        if (this.mFadingLowEdge) {
            mLowFadeShaderLength = this.mLowFadeShaderLength;
        }
        else {
            mLowFadeShaderLength = 0;
        }
        int mHighFadeShaderLength;
        if (this.mFadingHighEdge) {
            mHighFadeShaderLength = this.mHighFadeShaderLength;
        }
        else {
            mHighFadeShaderLength = 0;
        }
        canvas.clipRect(mLowFadeShaderLength + n, 0, width - mHighFadeShaderLength, this.getHeight());
        super.draw(canvas);
        canvas.restoreToCount(save);
        final Canvas canvas2 = new Canvas();
        final Rect mTempRect = this.mTempRect;
        mTempRect.top = 0;
        mTempRect.bottom = this.getHeight();
        if (needsFadingLowEdge && this.mLowFadeShaderLength > 0) {
            final Bitmap tempBitmapLow = this.getTempBitmapLow();
            tempBitmapLow.eraseColor(0);
            canvas2.setBitmap(tempBitmapLow);
            final int save2 = canvas2.save();
            canvas2.clipRect(0, 0, this.mLowFadeShaderLength, this.getHeight());
            final float n2 = (float)(-n);
            canvas2.translate(n2, 0.0f);
            super.draw(canvas2);
            canvas2.restoreToCount(save2);
            this.mTempPaint.setShader((Shader)this.mLowFadeShader);
            canvas2.drawRect(0.0f, 0.0f, (float)this.mLowFadeShaderLength, (float)this.getHeight(), this.mTempPaint);
            final Rect mTempRect2 = this.mTempRect;
            mTempRect2.left = 0;
            mTempRect2.right = this.mLowFadeShaderLength;
            canvas.translate((float)n, 0.0f);
            final Rect mTempRect3 = this.mTempRect;
            canvas.drawBitmap(tempBitmapLow, mTempRect3, mTempRect3, (Paint)null);
            canvas.translate(n2, 0.0f);
        }
        if (needsFadingHighEdge && this.mHighFadeShaderLength > 0) {
            final Bitmap tempBitmapHigh = this.getTempBitmapHigh();
            tempBitmapHigh.eraseColor(0);
            canvas2.setBitmap(tempBitmapHigh);
            final int save3 = canvas2.save();
            canvas2.clipRect(0, 0, this.mHighFadeShaderLength, this.getHeight());
            canvas2.translate((float)(-(width - this.mHighFadeShaderLength)), 0.0f);
            super.draw(canvas2);
            canvas2.restoreToCount(save3);
            this.mTempPaint.setShader((Shader)this.mHighFadeShader);
            canvas2.drawRect(0.0f, 0.0f, (float)this.mHighFadeShaderLength, (float)this.getHeight(), this.mTempPaint);
            final Rect mTempRect4 = this.mTempRect;
            mTempRect4.left = 0;
            final int mHighFadeShaderLength2 = this.mHighFadeShaderLength;
            mTempRect4.right = mHighFadeShaderLength2;
            canvas.translate((float)(width - mHighFadeShaderLength2), 0.0f);
            final Rect mTempRect5 = this.mTempRect;
            canvas.drawBitmap(tempBitmapHigh, mTempRect5, mTempRect5, (Paint)null);
            canvas.translate((float)(-(width - this.mHighFadeShaderLength)), 0.0f);
        }
    }
    
    protected void initAttributes(final Context context, final AttributeSet set) {
        this.initBaseGridViewAttributes(context, set);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbHorizontalGridView);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.lbHorizontalGridView, set, obtainStyledAttributes, 0, 0);
        this.setRowHeight(obtainStyledAttributes);
        this.setNumRows(obtainStyledAttributes.getInt(R$styleable.lbHorizontalGridView_numberOfRows, 1));
        obtainStyledAttributes.recycle();
        this.updateLayerType();
        (this.mTempPaint = new Paint()).setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.DST_IN));
    }
    
    public void setNumRows(final int numRows) {
        super.mLayoutManager.setNumRows(numRows);
        this.requestLayout();
    }
    
    public void setRowHeight(final int rowHeight) {
        super.mLayoutManager.setRowHeight(rowHeight);
        this.requestLayout();
    }
    
    void setRowHeight(final TypedArray typedArray) {
        if (typedArray.peekValue(R$styleable.lbHorizontalGridView_rowHeight) != null) {
            this.setRowHeight(typedArray.getLayoutDimension(R$styleable.lbHorizontalGridView_rowHeight, 0));
        }
    }
}
