// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.graphics.Canvas;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint;
import android.view.View;

public class ScreenshotSelectorView extends View
{
    private final Paint mPaintBackground;
    private final Paint mPaintSelection;
    private Rect mSelectionRect;
    private Point mStartPoint;
    
    public ScreenshotSelectorView(final Context context) {
        this(context, null);
    }
    
    public ScreenshotSelectorView(final Context context, final AttributeSet set) {
        super(context, set);
        (this.mPaintBackground = new Paint(-16777216)).setAlpha(160);
        (this.mPaintSelection = new Paint(0)).setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.CLEAR));
    }
    
    public void draw(final Canvas canvas) {
        canvas.drawRect((float)super.mLeft, (float)super.mTop, (float)super.mRight, (float)super.mBottom, this.mPaintBackground);
        final Rect mSelectionRect = this.mSelectionRect;
        if (mSelectionRect != null) {
            canvas.drawRect(mSelectionRect, this.mPaintSelection);
        }
    }
    
    public Rect getSelectionRect() {
        return this.mSelectionRect;
    }
    
    public void startSelection(final int n, final int n2) {
        this.mStartPoint = new Point(n, n2);
        this.mSelectionRect = new Rect(n, n2, n, n2);
    }
    
    public void stopSelection() {
        this.mStartPoint = null;
        this.mSelectionRect = null;
    }
    
    public void updateSelection(final int n, final int n2) {
        final Rect mSelectionRect = this.mSelectionRect;
        if (mSelectionRect != null) {
            mSelectionRect.left = Math.min(this.mStartPoint.x, n);
            this.mSelectionRect.right = Math.max(this.mStartPoint.x, n);
            this.mSelectionRect.top = Math.min(this.mStartPoint.y, n2);
            this.mSelectionRect.bottom = Math.max(this.mStartPoint.y, n2);
            this.invalidate();
        }
    }
}
