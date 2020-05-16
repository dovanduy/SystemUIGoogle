// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist.ui;

import android.content.Context;
import android.graphics.Path;

public final class CircularCornerPathRenderer extends CornerPathRenderer
{
    private final int mCornerRadiusBottom;
    private final int mCornerRadiusTop;
    private final int mHeight;
    private final Path mPath;
    private final int mWidth;
    
    public CircularCornerPathRenderer(final Context context) {
        this.mPath = new Path();
        this.mCornerRadiusBottom = DisplayUtils.getCornerRadiusBottom(context);
        this.mCornerRadiusTop = DisplayUtils.getCornerRadiusTop(context);
        this.mHeight = DisplayUtils.getHeight(context);
        this.mWidth = DisplayUtils.getWidth(context);
    }
    
    @Override
    public Path getCornerPath(final Corner corner) {
        this.mPath.reset();
        final int n = CircularCornerPathRenderer$1.$SwitchMap$com$android$systemui$assist$ui$CornerPathRenderer$Corner[corner.ordinal()];
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    if (n == 4) {
                        this.mPath.moveTo((float)this.mCornerRadiusTop, 0.0f);
                        final Path mPath = this.mPath;
                        final int mCornerRadiusTop = this.mCornerRadiusTop;
                        mPath.arcTo(0.0f, 0.0f, (float)(mCornerRadiusTop * 2), (float)(mCornerRadiusTop * 2), 270.0f, -90.0f, true);
                    }
                }
                else {
                    this.mPath.moveTo((float)this.mWidth, (float)this.mCornerRadiusTop);
                    final Path mPath2 = this.mPath;
                    final int mWidth = this.mWidth;
                    final int mCornerRadiusTop2 = this.mCornerRadiusTop;
                    mPath2.arcTo((float)(mWidth - mCornerRadiusTop2 * 2), 0.0f, (float)mWidth, (float)(mCornerRadiusTop2 * 2), 0.0f, -90.0f, true);
                }
            }
            else {
                this.mPath.moveTo((float)(this.mWidth - this.mCornerRadiusBottom), (float)this.mHeight);
                final Path mPath3 = this.mPath;
                final int mWidth2 = this.mWidth;
                final int mCornerRadiusBottom = this.mCornerRadiusBottom;
                final float n2 = (float)(mWidth2 - mCornerRadiusBottom * 2);
                final int mHeight = this.mHeight;
                mPath3.arcTo(n2, (float)(mHeight - mCornerRadiusBottom * 2), (float)mWidth2, (float)mHeight, 90.0f, -90.0f, true);
            }
        }
        else {
            this.mPath.moveTo(0.0f, (float)(this.mHeight - this.mCornerRadiusBottom));
            final Path mPath4 = this.mPath;
            final int mHeight2 = this.mHeight;
            final int mCornerRadiusBottom2 = this.mCornerRadiusBottom;
            mPath4.arcTo(0.0f, (float)(mHeight2 - mCornerRadiusBottom2 * 2), (float)(mCornerRadiusBottom2 * 2), (float)mHeight2, 180.0f, -90.0f, true);
        }
        return this.mPath;
    }
}
