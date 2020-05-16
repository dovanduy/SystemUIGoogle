// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Canvas;
import com.android.systemui.R$dimen;
import android.content.Context;

public class VerticalNavigationHandle extends NavigationHandle
{
    private final int mWidth;
    
    public VerticalNavigationHandle(final Context context) {
        super(context);
        this.mWidth = context.getResources().getDimensionPixelSize(R$dimen.navigation_home_handle_width);
    }
    
    @Override
    protected void onDraw(final Canvas canvas) {
        final int mRadius = super.mRadius;
        final int width = this.getWidth();
        final int mBottom = super.mBottom;
        final int n = this.getHeight() / 2;
        final int n2 = this.mWidth / 2;
        final int width2 = this.getWidth();
        final int mBottom2 = super.mBottom;
        final int n3 = this.getHeight() / 2;
        final int n4 = this.mWidth / 2;
        final float n5 = (float)(width2 - mBottom2 - mRadius * 2);
        final float n6 = (float)(n - n2);
        final float n7 = (float)(width - mBottom);
        final float n8 = (float)(n3 + n4);
        final int mRadius2 = super.mRadius;
        canvas.drawRoundRect(n5, n6, n7, n8, (float)mRadius2, (float)mRadius2, super.mPaint);
    }
}
