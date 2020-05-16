// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.annotation.TargetApi;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.graphics.Paint$Style;
import android.graphics.Bitmap$Config;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.RectF;

public class IconNormalizer
{
    private final RectF mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Rect mBounds;
    private final Canvas mCanvas;
    private boolean mEnableShapeDetection;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final float[] mRightBorder;
    private final Path mShapePath;
    
    IconNormalizer(final Context context, int mMaxSize, final boolean mEnableShapeDetection) {
        mMaxSize *= 2;
        this.mMaxSize = mMaxSize;
        this.mBitmap = Bitmap.createBitmap(mMaxSize, mMaxSize, Bitmap$Config.ALPHA_8);
        this.mCanvas = new Canvas(this.mBitmap);
        mMaxSize = this.mMaxSize;
        this.mPixels = new byte[mMaxSize * mMaxSize];
        this.mLeftBorder = new float[mMaxSize];
        this.mRightBorder = new float[mMaxSize];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new RectF();
        (this.mPaintMaskShape = new Paint()).setColor(-65536);
        this.mPaintMaskShape.setStyle(Paint$Style.FILL);
        this.mPaintMaskShape.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.XOR));
        (this.mPaintMaskShapeOutline = new Paint()).setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        this.mPaintMaskShapeOutline.setStyle(Paint$Style.STROKE);
        this.mPaintMaskShapeOutline.setColor(-16777216);
        this.mPaintMaskShapeOutline.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mEnableShapeDetection = mEnableShapeDetection;
    }
    
    private static void convertToConvexArray(final float[] array, final int n, final int n2, final int n3) {
        final float[] array2 = new float[array.length - 1];
        int i = n2 + 1;
        int n4 = -1;
        float n5 = Float.MAX_VALUE;
        while (i <= n3) {
            if (array[i] > -1.0f) {
                int n6;
                if (n5 == Float.MAX_VALUE) {
                    n6 = n2;
                }
                else {
                    final float n7 = (array[i] - array[n4]) / (i - n4);
                    final float n8 = (float)n;
                    n6 = n4;
                    if ((n7 - n5) * n8 < 0.0f) {
                        while ((n6 = n4) > n2) {
                            n6 = --n4;
                            if (((array[i] - array[n6]) / (i - n6) - array2[n6]) * n8 >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                n5 = (array[i] - array[n6]) / (i - n6);
                for (int j = n6; j < i; ++j) {
                    array2[j] = n5;
                    array[j] = array[n6] + (j - n6) * n5;
                }
                n4 = i;
            }
            ++i;
        }
    }
    
    private static float getScale(float n, float n2, float n3) {
        n2 = n / n2;
        final float n4 = 1.0f;
        if (n2 < 0.7853982f) {
            n2 = 0.6597222f;
        }
        else {
            n2 = (1.0f - n2) * 0.040449437f + 0.6510417f;
        }
        n3 = n / n3;
        n = n4;
        if (n3 > n2) {
            n = (float)Math.sqrt(n2 / n3);
        }
        return n;
    }
    
    private boolean isShape(final Path path) {
        if (Math.abs(this.mBounds.width() / (float)this.mBounds.height() - 1.0f) > 0.05f) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale((float)this.mBounds.width(), (float)this.mBounds.height());
        final Matrix mMatrix = this.mMatrix;
        final Rect mBounds = this.mBounds;
        mMatrix.postTranslate((float)mBounds.left, (float)mBounds.top);
        path.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return this.isTransparentBitmap();
    }
    
    private boolean isTransparentBitmap() {
        final ByteBuffer wrap = ByteBuffer.wrap(this.mPixels);
        wrap.rewind();
        this.mBitmap.copyPixelsToBuffer((Buffer)wrap);
        final Rect mBounds = this.mBounds;
        int top = mBounds.top;
        final int mMaxSize = this.mMaxSize;
        int n = top * mMaxSize;
        final int right = mBounds.right;
        boolean b = false;
        int n2 = 0;
        Rect mBounds2;
        while (true) {
            mBounds2 = this.mBounds;
            if (top >= mBounds2.bottom) {
                break;
            }
            final int left = mBounds2.left;
            int n3 = n + left;
            int n4;
            for (int i = left; i < this.mBounds.right; ++i, n2 = n4) {
                n4 = n2;
                if ((this.mPixels[n3] & 0xFF) > 40) {
                    n4 = n2 + 1;
                }
                ++n3;
            }
            n = n3 + (mMaxSize - right);
            ++top;
        }
        if (n2 / (float)(mBounds2.width() * this.mBounds.height()) < 0.005f) {
            b = true;
        }
        return b;
    }
    
    @TargetApi(26)
    public static float normalizeAdaptiveIcon(final Drawable drawable, final int n, final RectF rectF) {
        final Rect bounds = new Rect(drawable.getBounds());
        drawable.setBounds(0, 0, n, n);
        final Path iconMask = ((AdaptiveIconDrawable)drawable).getIconMask();
        final Region region = new Region();
        region.setPath(iconMask, new Region(0, 0, n, n));
        final Rect bounds2 = region.getBounds();
        final int area = GraphicsUtils.getArea(region);
        if (rectF != null) {
            final float n2 = (float)n;
            rectF.set(bounds2.left / n2, bounds2.top / n2, 1.0f - bounds2.right / n2, 1.0f - bounds2.bottom / n2);
        }
        drawable.setBounds(bounds);
        final float n3 = (float)area;
        return getScale(n3, n3, (float)(n * n));
    }
    
    public float getScale(final Drawable drawable, final RectF rectF, final Path path, final boolean[] array) {
        synchronized (this) {
            if (BaseIconFactory.ATLEAST_OREO && drawable instanceof AdaptiveIconDrawable) {
                if (this.mAdaptiveIconScale == 0.0f) {
                    this.mAdaptiveIconScale = normalizeAdaptiveIcon(drawable, this.mMaxSize, this.mAdaptiveIconBounds);
                }
                if (rectF != null) {
                    rectF.set(this.mAdaptiveIconBounds);
                }
                return this.mAdaptiveIconScale;
            }
            final int intrinsicWidth = drawable.getIntrinsicWidth();
            final int intrinsicHeight = drawable.getIntrinsicHeight();
            int n = 0;
            int mMaxSize = 0;
            Label_0206: {
                if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                    if (intrinsicWidth <= this.mMaxSize) {
                        n = intrinsicWidth;
                        if ((mMaxSize = intrinsicHeight) <= this.mMaxSize) {
                            break Label_0206;
                        }
                    }
                    final int max = Math.max(intrinsicWidth, intrinsicHeight);
                    n = this.mMaxSize * intrinsicWidth / max;
                    mMaxSize = this.mMaxSize * intrinsicHeight / max;
                }
                else {
                    int mMaxSize2;
                    if (intrinsicWidth <= 0 || (mMaxSize2 = intrinsicWidth) > this.mMaxSize) {
                        mMaxSize2 = this.mMaxSize;
                    }
                    if (intrinsicHeight > 0) {
                        n = mMaxSize2;
                        if ((mMaxSize = intrinsicHeight) <= this.mMaxSize) {
                            break Label_0206;
                        }
                    }
                    mMaxSize = this.mMaxSize;
                    n = mMaxSize2;
                }
            }
            this.mBitmap.eraseColor(0);
            drawable.setBounds(0, 0, n, mMaxSize);
            drawable.draw(this.mCanvas);
            final ByteBuffer wrap = ByteBuffer.wrap(this.mPixels);
            wrap.rewind();
            this.mBitmap.copyPixelsToBuffer((Buffer)wrap);
            int n2 = this.mMaxSize + 1;
            final int mMaxSize3 = this.mMaxSize;
            int n3;
            int i = n3 = 0;
            int top = -1;
            int n4 = -1;
            int bottom = -1;
            while (i < mMaxSize) {
                final int n5 = 0;
                int b = -1;
                int b2 = -1;
                int n6 = n3;
                int n7;
                for (int j = n5; j < n; ++j, b2 = n7) {
                    n7 = b2;
                    if ((this.mPixels[n6] & 0xFF) > 40) {
                        int n8;
                        if ((n8 = b2) == -1) {
                            n8 = j;
                        }
                        final int n9 = j;
                        n7 = n8;
                        b = n9;
                    }
                    ++n6;
                }
                final int n10 = n6 + (mMaxSize3 - n);
                this.mLeftBorder[i] = (float)b2;
                this.mRightBorder[i] = (float)b;
                int min = n2;
                int n11 = top;
                int max2 = n4;
                if (b2 != -1) {
                    if ((n11 = top) == -1) {
                        n11 = i;
                    }
                    min = Math.min(n2, b2);
                    max2 = Math.max(n4, b);
                    bottom = i;
                }
                ++i;
                n2 = min;
                top = n11;
                n4 = max2;
                n3 = n10;
            }
            if (top != -1 && n4 != -1) {
                convertToConvexArray(this.mLeftBorder, 1, top, bottom);
                convertToConvexArray(this.mRightBorder, -1, top, bottom);
                float n12 = 0.0f;
                for (int k = 0; k < mMaxSize; ++k) {
                    if (this.mLeftBorder[k] > -1.0f) {
                        n12 += this.mRightBorder[k] - this.mLeftBorder[k] + 1.0f;
                    }
                }
                this.mBounds.left = n2;
                this.mBounds.right = n4;
                this.mBounds.top = top;
                this.mBounds.bottom = bottom;
                if (rectF != null) {
                    final float n13 = (float)this.mBounds.left;
                    final float n14 = (float)n;
                    final float n15 = n13 / n14;
                    final float n16 = (float)this.mBounds.top;
                    final float n17 = (float)mMaxSize;
                    rectF.set(n15, n16 / n17, 1.0f - this.mBounds.right / n14, 1.0f - this.mBounds.bottom / n17);
                }
                if (array != null && this.mEnableShapeDetection && array.length > 0) {
                    array[0] = this.isShape(path);
                }
                return getScale(n12, (float)((bottom + 1 - top) * (n4 + 1 - n2)), (float)(n * mMaxSize));
            }
            return 1.0f;
        }
    }
}
