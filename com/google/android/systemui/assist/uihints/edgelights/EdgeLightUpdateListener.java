// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights;

import android.util.MathUtils;
import android.animation.ValueAnimator;
import com.android.systemui.assist.ui.EdgeLight;
import android.animation.ValueAnimator$AnimatorUpdateListener;

public final class EdgeLightUpdateListener implements ValueAnimator$AnimatorUpdateListener
{
    private EdgeLight[] mFinalLights;
    private EdgeLight[] mInitialLights;
    private EdgeLight[] mLights;
    private EdgeLightsView mView;
    
    public EdgeLightUpdateListener(final EdgeLight[] mInitialLights, final EdgeLight[] mFinalLights, final EdgeLight[] mLights, final EdgeLightsView mView) {
        if (mInitialLights.length == mFinalLights.length && mLights.length == mFinalLights.length) {
            this.mFinalLights = mFinalLights;
            this.mInitialLights = mInitialLights;
            this.mLights = mLights;
            this.mView = mView;
            return;
        }
        throw new IllegalArgumentException("Lights arrays must be the same length");
    }
    
    public void onAnimationUpdate(final ValueAnimator valueAnimator) {
        final float animatedFraction = valueAnimator.getAnimatedFraction();
        int n = 0;
        EdgeLight[] mLights;
        while (true) {
            mLights = this.mLights;
            if (n >= mLights.length) {
                break;
            }
            this.mLights[n].setLength(MathUtils.lerp(this.mInitialLights[n].getLength(), this.mFinalLights[n].getLength(), animatedFraction));
            this.mLights[n].setStart(MathUtils.lerp(this.mInitialLights[n].getStart(), this.mFinalLights[n].getStart(), animatedFraction));
            ++n;
        }
        this.mView.setAssistLights(mLights);
    }
}
