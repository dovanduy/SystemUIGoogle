// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import android.content.res.Resources;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import android.util.MathUtils;
import com.android.systemui.doze.util.BurnInHelperKt;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class ClockLayout extends FrameLayout
{
    private View mAnalogClock;
    private int mBurnInPreventionOffsetX;
    private int mBurnInPreventionOffsetY;
    private float mDarkAmount;
    
    public ClockLayout(final Context context) {
        this(context, null);
    }
    
    public ClockLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ClockLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    private void positionChildren() {
        final float lerp = MathUtils.lerp(0.0f, (float)(BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffsetX * 2, true) - this.mBurnInPreventionOffsetX), this.mDarkAmount);
        final float lerp2 = MathUtils.lerp(0.0f, BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffsetY * 2, false) - this.mBurnInPreventionOffsetY * 0.5f, this.mDarkAmount);
        final View mAnalogClock = this.mAnalogClock;
        if (mAnalogClock != null) {
            mAnalogClock.setX(Math.max(0.0f, (this.getWidth() - this.mAnalogClock.getWidth()) * 0.5f) + lerp * 3.0f);
            this.mAnalogClock.setY(Math.max(0.0f, (this.getHeight() - this.mAnalogClock.getHeight()) * 0.5f) + lerp2 * 3.0f);
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mAnalogClock = this.findViewById(R$id.analog_clock);
        final Resources resources = this.getResources();
        this.mBurnInPreventionOffsetX = resources.getDimensionPixelSize(R$dimen.burn_in_prevention_offset_x);
        this.mBurnInPreventionOffsetY = resources.getDimensionPixelSize(R$dimen.burn_in_prevention_offset_y);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.positionChildren();
    }
}
