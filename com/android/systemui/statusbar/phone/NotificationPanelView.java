// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Canvas;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Paint;

public class NotificationPanelView extends PanelView
{
    private final Paint mAlphaPaint;
    private int mCurrentPanelAlpha;
    private boolean mDozing;
    private RtlChangeListener mRtlChangeListener;
    
    public NotificationPanelView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mAlphaPaint = new Paint();
        this.setWillNotDraw(true);
        this.mAlphaPaint.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.MULTIPLY));
        this.setBackgroundColor(0);
    }
    
    protected void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mCurrentPanelAlpha != 255) {
            canvas.drawRect(0.0f, 0.0f, (float)canvas.getWidth(), (float)canvas.getHeight(), this.mAlphaPaint);
        }
    }
    
    float getCurrentPanelAlpha() {
        return (float)this.mCurrentPanelAlpha;
    }
    
    public boolean hasOverlappingRendering() {
        return this.mDozing ^ true;
    }
    
    public void onRtlPropertiesChanged(final int n) {
        final RtlChangeListener mRtlChangeListener = this.mRtlChangeListener;
        if (mRtlChangeListener != null) {
            mRtlChangeListener.onRtlPropertielsChanged(n);
        }
    }
    
    public void setDozing(final boolean mDozing) {
        this.mDozing = mDozing;
    }
    
    void setPanelAlphaInternal(final float n) {
        final int mCurrentPanelAlpha = (int)n;
        this.mCurrentPanelAlpha = mCurrentPanelAlpha;
        this.mAlphaPaint.setARGB(mCurrentPanelAlpha, 255, 255, 255);
        this.invalidate();
    }
    
    void setRtlChangeListener(final RtlChangeListener mRtlChangeListener) {
        this.mRtlChangeListener = mRtlChangeListener;
    }
    
    public boolean shouldDelayChildPressedState() {
        return true;
    }
    
    interface RtlChangeListener
    {
        void onRtlPropertielsChanged(final int p0);
    }
}
