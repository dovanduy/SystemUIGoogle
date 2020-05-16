// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.os.Bundle;
import android.graphics.Rect;
import android.graphics.Canvas;
import androidx.leanback.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.Paint;
import android.view.View;

public final class SeekBar extends View
{
    private AccessibilitySeekListener mAccessibilitySeekListener;
    private int mActiveBarHeight;
    private int mActiveRadius;
    private final Paint mBackgroundPaint;
    private final RectF mBackgroundRect;
    private int mBarHeight;
    private final Paint mKnobPaint;
    private int mKnobx;
    private int mMax;
    private int mProgress;
    private final Paint mProgressPaint;
    private final RectF mProgressRect;
    private int mSecondProgress;
    private final Paint mSecondProgressPaint;
    private final RectF mSecondProgressRect;
    
    public SeekBar(final Context context, final AttributeSet set) {
        super(context, set);
        this.mProgressRect = new RectF();
        this.mSecondProgressRect = new RectF();
        this.mBackgroundRect = new RectF();
        this.mSecondProgressPaint = new Paint(1);
        this.mProgressPaint = new Paint(1);
        this.mBackgroundPaint = new Paint(1);
        this.mKnobPaint = new Paint(1);
        this.setWillNotDraw(false);
        this.mBackgroundPaint.setColor(-7829368);
        this.mSecondProgressPaint.setColor(-3355444);
        this.mProgressPaint.setColor(-65536);
        this.mKnobPaint.setColor(-1);
        this.mBarHeight = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_progressbar_bar_height);
        this.mActiveBarHeight = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_progressbar_active_bar_height);
        this.mActiveRadius = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_progressbar_active_radius);
    }
    
    private void calculate() {
        int n;
        if (this.isFocused()) {
            n = this.mActiveBarHeight;
        }
        else {
            n = this.mBarHeight;
        }
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int n2 = (height - n) / 2;
        final RectF mBackgroundRect = this.mBackgroundRect;
        final int mBarHeight = this.mBarHeight;
        final float n3 = (float)(mBarHeight / 2);
        final float n4 = (float)n2;
        final float n5 = (float)(width - mBarHeight / 2);
        final float n6 = (float)(height - n2);
        mBackgroundRect.set(n3, n4, n5, n6);
        int mActiveRadius;
        if (this.isFocused()) {
            mActiveRadius = this.mActiveRadius;
        }
        else {
            mActiveRadius = this.mBarHeight / 2;
        }
        final float n7 = this.mProgress / (float)this.mMax;
        final float n8 = (float)(width - mActiveRadius * 2);
        final float n9 = n7 * n8;
        final RectF mProgressRect = this.mProgressRect;
        final int mBarHeight2 = this.mBarHeight;
        mProgressRect.set((float)(mBarHeight2 / 2), n4, mBarHeight2 / 2 + n9, n6);
        this.mSecondProgressRect.set(this.mProgressRect.right, n4, this.mBarHeight / 2 + this.mSecondProgress / (float)this.mMax * n8, n6);
        this.mKnobx = mActiveRadius + (int)n9;
        this.invalidate();
    }
    
    public CharSequence getAccessibilityClassName() {
        return android.widget.SeekBar.class.getName();
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        int mActiveRadius;
        if (this.isFocused()) {
            mActiveRadius = this.mActiveRadius;
        }
        else {
            mActiveRadius = this.mBarHeight / 2;
        }
        final RectF mBackgroundRect = this.mBackgroundRect;
        final float n = (float)mActiveRadius;
        canvas.drawRoundRect(mBackgroundRect, n, n, this.mBackgroundPaint);
        final RectF mSecondProgressRect = this.mSecondProgressRect;
        if (mSecondProgressRect.right > mSecondProgressRect.left) {
            canvas.drawRoundRect(mSecondProgressRect, n, n, this.mSecondProgressPaint);
        }
        canvas.drawRoundRect(this.mProgressRect, n, n, this.mProgressPaint);
        canvas.drawCircle((float)this.mKnobx, (float)(this.getHeight() / 2), n, this.mKnobPaint);
    }
    
    protected void onFocusChanged(final boolean b, final int n, final Rect rect) {
        super.onFocusChanged(b, n, rect);
        this.calculate();
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        this.calculate();
    }
    
    public boolean performAccessibilityAction(final int n, final Bundle bundle) {
        final AccessibilitySeekListener mAccessibilitySeekListener = this.mAccessibilitySeekListener;
        if (mAccessibilitySeekListener != null) {
            if (n == 4096) {
                return mAccessibilitySeekListener.onAccessibilitySeekForward();
            }
            if (n == 8192) {
                return mAccessibilitySeekListener.onAccessibilitySeekBackward();
            }
        }
        return super.performAccessibilityAction(n, bundle);
    }
    
    public abstract static class AccessibilitySeekListener
    {
        public abstract boolean onAccessibilitySeekBackward();
        
        public abstract boolean onAccessibilitySeekForward();
    }
}
