// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights.mode;

import android.animation.Animator$AnimatorListener;
import android.os.Handler;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import android.animation.AnimatorSet;
import com.android.systemui.assist.ui.PerimeterPathGuide;
import android.animation.ValueAnimator;
import android.content.res.Resources$Theme;
import com.android.systemui.R$color;
import android.content.Context;
import com.android.systemui.assist.ui.EdgeLight;
import android.view.animation.PathInterpolator;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;

public final class FulfillPerimeter implements Mode
{
    private static final PathInterpolator FULFILL_PERIMETER_INTERPOLATOR;
    private final EdgeLight mBlueLight;
    private boolean mDisappearing;
    private final EdgeLight mGreenLight;
    private final EdgeLight[] mLights;
    private Mode mNextMode;
    private final EdgeLight mRedLight;
    private final EdgeLight mYellowLight;
    
    static {
        FULFILL_PERIMETER_INTERPOLATOR = new PathInterpolator(0.2f, 0.0f, 0.2f, 1.0f);
    }
    
    public FulfillPerimeter(final Context context) {
        this.mDisappearing = false;
        this.mBlueLight = new EdgeLight(context.getResources().getColor(R$color.edge_light_blue, (Resources$Theme)null), 0.0f, 0.0f);
        this.mRedLight = new EdgeLight(context.getResources().getColor(R$color.edge_light_red, (Resources$Theme)null), 0.0f, 0.0f);
        this.mYellowLight = new EdgeLight(context.getResources().getColor(R$color.edge_light_yellow, (Resources$Theme)null), 0.0f, 0.0f);
        final EdgeLight mGreenLight = new EdgeLight(context.getResources().getColor(R$color.edge_light_green, (Resources$Theme)null), 0.0f, 0.0f);
        this.mGreenLight = mGreenLight;
        this.mLights = new EdgeLight[] { this.mBlueLight, this.mRedLight, mGreenLight, this.mYellowLight };
    }
    
    @Override
    public int getSubType() {
        return 4;
    }
    
    @Override
    public void onNewModeRequest(final EdgeLightsView edgeLightsView, final Mode mNextMode) {
        this.mNextMode = mNextMode;
    }
    
    @Override
    public void start(final EdgeLightsView edgeLightsView, final PerimeterPathGuide perimeterPathGuide, final Mode mode) {
        edgeLightsView.setVisibility(0);
        final AnimatorSet set = new AnimatorSet();
        for (final EdgeLight edgeLight : this.mLights) {
            final boolean b = edgeLight == this.mBlueLight || edgeLight == this.mRedLight;
            final boolean b2 = edgeLight == this.mRedLight || edgeLight == this.mYellowLight;
            final float regionCenter = perimeterPathGuide.getRegionCenter(PerimeterPathGuide.Region.BOTTOM);
            float clockwise;
            if (b) {
                clockwise = PerimeterPathGuide.makeClockwise(perimeterPathGuide.getRegionCenter(PerimeterPathGuide.Region.TOP));
            }
            else {
                clockwise = regionCenter;
            }
            final float n = perimeterPathGuide.getRegionCenter(PerimeterPathGuide.Region.TOP) - perimeterPathGuide.getRegionCenter(PerimeterPathGuide.Region.BOTTOM);
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
            long startDelay;
            if (b2) {
                startDelay = 100L;
            }
            else {
                startDelay = 0L;
            }
            ofFloat.setStartDelay(startDelay);
            ofFloat.setDuration(433L);
            ofFloat.setInterpolator((TimeInterpolator)FulfillPerimeter.FULFILL_PERIMETER_INTERPOLATOR);
            ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$FulfillPerimeter$MZtU_jbRyns2SZEYMcv6IQbgrRY(this, edgeLight, clockwise - regionCenter, regionCenter, n - 0.0f, 0.0f, edgeLightsView));
            if (!b2) {
                set.play((Animator)ofFloat);
            }
            else {
                final float interpolation = ofFloat.getInterpolator().getInterpolation(100.0f / ofFloat.getDuration());
                final ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
                ofFloat2.setStartDelay(ofFloat.getStartDelay() + 100L);
                ofFloat2.setDuration(733L);
                ofFloat2.setInterpolator((TimeInterpolator)FulfillPerimeter.FULFILL_PERIMETER_INTERPOLATOR);
                ofFloat2.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$FulfillPerimeter$4qfpqiVttSOidi4h0dCycMmHzTE(this, edgeLight, interpolation * n, perimeterPathGuide, edgeLightsView));
                set.play((Animator)ofFloat);
                set.play((Animator)ofFloat2);
            }
        }
        set.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                super.onAnimationEnd(animator);
                if (FulfillPerimeter.this.mNextMode == null) {
                    FulfillPerimeter.this.mDisappearing = false;
                    set.start();
                }
                else if (FulfillPerimeter.this.mNextMode != null) {
                    new Handler().postDelayed((Runnable)new _$$Lambda$FulfillPerimeter$1$uToVp7_HsUUglm_WlzavSyXNWCo(this, edgeLightsView), 500L);
                }
            }
        });
        set.start();
    }
}
