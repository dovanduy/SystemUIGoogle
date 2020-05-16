// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist.ui;

import android.graphics.RectF;
import android.util.Log;
import android.util.PathParser;
import com.android.systemui.R$string;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Matrix;

public final class PathSpecCornerPathRenderer extends CornerPathRenderer
{
    private final int mBottomCornerRadius;
    private final int mHeight;
    private final Matrix mMatrix;
    private final Path mPath;
    private final float mPathScale;
    private final Path mRoundedPath;
    private final int mTopCornerRadius;
    private final int mWidth;
    
    public PathSpecCornerPathRenderer(final Context context) {
        this.mPath = new Path();
        this.mMatrix = new Matrix();
        this.mWidth = DisplayUtils.getWidth(context);
        this.mHeight = DisplayUtils.getHeight(context);
        this.mBottomCornerRadius = DisplayUtils.getCornerRadiusBottom(context);
        this.mTopCornerRadius = DisplayUtils.getCornerRadiusTop(context);
        final Path pathFromPathData = PathParser.createPathFromPathData(context.getResources().getString(R$string.config_rounded_mask));
        if (pathFromPathData == null) {
            Log.e("PathSpecCornerPathRenderer", "No rounded corner path found!");
            this.mRoundedPath = new Path();
        }
        else {
            this.mRoundedPath = pathFromPathData;
        }
        final RectF rectF = new RectF();
        this.mRoundedPath.computeBounds(rectF, true);
        this.mPathScale = Math.min(Math.abs(rectF.right - rectF.left), Math.abs(rectF.top - rectF.bottom));
    }
    
    @Override
    public Path getCornerPath(final Corner corner) {
        if (this.mRoundedPath.isEmpty()) {
            return this.mRoundedPath;
        }
        final int n = PathSpecCornerPathRenderer$1.$SwitchMap$com$android$systemui$assist$ui$CornerPathRenderer$Corner[corner.ordinal()];
        int n2 = 0;
        int n3;
        int n4;
        int n5;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    n3 = this.mBottomCornerRadius;
                    n4 = this.mHeight;
                    n2 = 270;
                    n5 = 0;
                }
                else {
                    n3 = this.mBottomCornerRadius;
                    n2 = 180;
                    n5 = this.mWidth;
                    n4 = this.mHeight;
                }
            }
            else {
                n3 = this.mTopCornerRadius;
                n5 = this.mWidth;
                n2 = 90;
                n4 = 0;
            }
        }
        else {
            n3 = this.mTopCornerRadius;
            n5 = (n4 = 0);
        }
        this.mPath.reset();
        this.mMatrix.reset();
        this.mPath.addPath(this.mRoundedPath);
        final Matrix mMatrix = this.mMatrix;
        final float n6 = (float)n3;
        final float mPathScale = this.mPathScale;
        mMatrix.preScale(n6 / mPathScale, n6 / mPathScale);
        this.mMatrix.postRotate((float)n2);
        this.mMatrix.postTranslate((float)n5, (float)n4);
        this.mPath.transform(this.mMatrix);
        return this.mPath;
    }
}
