// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.graphics.Matrix$ScaleToFit;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.SurfaceControl$Transaction;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import com.android.systemui.R$bool;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;

public class PipSurfaceTransactionHelper
{
    private final int mCornerRadius;
    private final boolean mEnableCornerRadius;
    private final RectF mTmpDestinationRectF;
    private final float[] mTmpFloat9;
    private final RectF mTmpSourceRectF;
    private final Matrix mTmpTransform;
    
    public PipSurfaceTransactionHelper(final Context context) {
        this.mTmpTransform = new Matrix();
        this.mTmpFloat9 = new float[9];
        this.mTmpSourceRectF = new RectF();
        this.mTmpDestinationRectF = new RectF();
        final Resources resources = context.getResources();
        this.mEnableCornerRadius = resources.getBoolean(R$bool.config_pipEnableRoundCorner);
        this.mCornerRadius = resources.getDimensionPixelSize(R$dimen.pip_corner_radius);
    }
    
    PipSurfaceTransactionHelper alpha(final SurfaceControl$Transaction surfaceControl$Transaction, final SurfaceControl surfaceControl, final float n) {
        surfaceControl$Transaction.setAlpha(surfaceControl, n);
        return this;
    }
    
    PipSurfaceTransactionHelper crop(final SurfaceControl$Transaction surfaceControl$Transaction, final SurfaceControl surfaceControl, final Rect rect) {
        surfaceControl$Transaction.setWindowCrop(surfaceControl, rect.width(), rect.height()).setPosition(surfaceControl, (float)rect.left, (float)rect.top);
        return this;
    }
    
    PipSurfaceTransactionHelper resetScale(final SurfaceControl$Transaction surfaceControl$Transaction, final SurfaceControl surfaceControl, final Rect rect) {
        surfaceControl$Transaction.setMatrix(surfaceControl, Matrix.IDENTITY_MATRIX, this.mTmpFloat9).setPosition(surfaceControl, (float)rect.left, (float)rect.top);
        return this;
    }
    
    PipSurfaceTransactionHelper round(final SurfaceControl$Transaction surfaceControl$Transaction, final SurfaceControl surfaceControl, final boolean b) {
        if (this.mEnableCornerRadius) {
            float n;
            if (b) {
                n = (float)this.mCornerRadius;
            }
            else {
                n = 0.0f;
            }
            surfaceControl$Transaction.setCornerRadius(surfaceControl, n);
        }
        return this;
    }
    
    PipSurfaceTransactionHelper scale(final SurfaceControl$Transaction surfaceControl$Transaction, final SurfaceControl surfaceControl, final Rect rect, final Rect rect2) {
        this.mTmpSourceRectF.set(rect);
        this.mTmpDestinationRectF.set(rect2);
        this.mTmpTransform.setRectToRect(this.mTmpSourceRectF, this.mTmpDestinationRectF, Matrix$ScaleToFit.FILL);
        surfaceControl$Transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setPosition(surfaceControl, (float)rect2.left, (float)rect2.top);
        return this;
    }
    
    interface SurfaceControlTransactionFactory
    {
        SurfaceControl$Transaction getTransaction();
    }
}
