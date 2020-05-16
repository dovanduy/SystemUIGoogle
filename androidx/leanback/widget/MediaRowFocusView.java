// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.Canvas;
import androidx.leanback.R$color;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.Paint;
import android.view.View;

class MediaRowFocusView extends View
{
    private final Paint mPaint;
    private final RectF mRoundRectF;
    private int mRoundRectRadius;
    
    public MediaRowFocusView(final Context context) {
        super(context);
        this.mRoundRectF = new RectF();
        this.mPaint = this.createPaint(context);
    }
    
    public MediaRowFocusView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mRoundRectF = new RectF();
        this.mPaint = this.createPaint(context);
    }
    
    public MediaRowFocusView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mRoundRectF = new RectF();
        this.mPaint = this.createPaint(context);
    }
    
    private Paint createPaint(final Context context) {
        final Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R$color.lb_playback_media_row_highlight_color));
        return paint;
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final int mRoundRectRadius = this.getHeight() / 2;
        this.mRoundRectRadius = mRoundRectRadius;
        final int n = (mRoundRectRadius * 2 - this.getHeight()) / 2;
        this.mRoundRectF.set(0.0f, (float)(-n), (float)this.getWidth(), (float)(this.getHeight() + n));
        final RectF mRoundRectF = this.mRoundRectF;
        final int mRoundRectRadius2 = this.mRoundRectRadius;
        canvas.drawRoundRect(mRoundRectF, (float)mRoundRectRadius2, (float)mRoundRectRadius2, this.mPaint);
    }
}
