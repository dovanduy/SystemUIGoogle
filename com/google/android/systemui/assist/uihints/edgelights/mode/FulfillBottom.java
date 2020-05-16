// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights.mode;

import android.content.res.Resources$Theme;
import com.android.systemui.R$color;
import android.util.Log;
import android.util.MathUtils;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightUpdateListener;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import java.util.Random;
import com.android.systemui.assist.ui.PerimeterPathGuide;
import android.animation.AnimatorSet;
import com.android.systemui.assist.ui.EdgeLight;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;

public final class FulfillBottom implements Mode
{
    private static final PathInterpolator CRADLE_INTERPOLATOR;
    private static final LinearInterpolator EXIT_FADE_INTERPOLATOR;
    private static final PathInterpolator EXIT_TO_CORNER_INTERPOLATOR;
    private EdgeLight mBlueLight;
    private AnimatorSet mCradleAnimations;
    private EdgeLightsView mEdgeLightsView;
    private AnimatorSet mExitAnimations;
    private EdgeLight mGreenLight;
    private PerimeterPathGuide mGuide;
    private final boolean mIsListening;
    private EdgeLight[] mLightsArray;
    private Mode mNextMode;
    private final Random mRandom;
    private EdgeLight mRedLight;
    private final Resources mResources;
    private boolean mSwingLeft;
    private EdgeLight mYellowLight;
    
    static {
        CRADLE_INTERPOLATOR = new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
        EXIT_TO_CORNER_INTERPOLATOR = new PathInterpolator(0.1f, 0.0f, 0.5f, 1.0f);
        EXIT_FADE_INTERPOLATOR = new LinearInterpolator();
    }
    
    public FulfillBottom(final Context context, final boolean mIsListening) {
        this.mRandom = new Random();
        this.mExitAnimations = new AnimatorSet();
        this.mCradleAnimations = new AnimatorSet();
        this.mEdgeLightsView = null;
        this.mGuide = null;
        this.mNextMode = null;
        this.mSwingLeft = false;
        this.mResources = context.getResources();
        this.mIsListening = mIsListening;
    }
    
    private void animateCradle() {
        final float regionWidth = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM);
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$FulfillBottom$Hxdn1aAzrZEP6ECJzzCZ4dM70Ts(this, this.mBlueLight.getEnd() / regionWidth, this.mRedLight.getEnd() / regionWidth, this.mYellowLight.getEnd() / regionWidth));
        ofFloat.setDuration(1000L);
        final ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat2.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$FulfillBottom$uU5qRyXHyVD8Cbr6AGTsLp6utxA(this));
        ofFloat2.setDuration(1300L);
        ofFloat2.setInterpolator((TimeInterpolator)FulfillBottom.CRADLE_INTERPOLATOR);
        ofFloat2.setRepeatMode(2);
        ofFloat2.setRepeatCount(-1);
        (this.mCradleAnimations = new AnimatorSet()).playSequentially(new Animator[] { (Animator)ofFloat, (Animator)ofFloat2 });
        this.mCradleAnimations.start();
    }
    
    private void animateExit() {
        final ValueAnimator toCornersAnimator = this.createToCornersAnimator();
        final ValueAnimator fadeOutAnimator = this.createFadeOutAnimator();
        final AnimatorSet mExitAnimations = new AnimatorSet();
        mExitAnimations.play((Animator)toCornersAnimator);
        mExitAnimations.play((Animator)fadeOutAnimator);
        mExitAnimations.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mCancelled = false;
            
            public void onAnimationCancel(final Animator animator) {
                super.onAnimationCancel(animator);
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                super.onAnimationEnd(animator);
                FulfillBottom.this.mEdgeLightsView.setVisibility(8);
                if (FulfillBottom.this.mNextMode != null && !this.mCancelled) {
                    FulfillBottom.this.mEdgeLightsView.commitModeTransition(FulfillBottom.this.mNextMode);
                }
            }
        });
        (this.mExitAnimations = mExitAnimations).start();
    }
    
    private ValueAnimator createFadeOutAnimator() {
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 1.0f, 0.0f });
        ofFloat.setInterpolator((TimeInterpolator)FulfillBottom.EXIT_FADE_INTERPOLATOR);
        ofFloat.setDuration(350L);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$FulfillBottom$7TXpqxeUJqI3ZokTLSOiTWetkw0(this));
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                FulfillBottom.this.mEdgeLightsView.setAssistLights(new EdgeLight[0]);
                FulfillBottom.this.mEdgeLightsView.setAlpha(1.0f);
            }
        });
        return ofFloat;
    }
    
    private ValueAnimator createToCornersAnimator() {
        final EdgeLight[] copy = EdgeLight.copy(this.mLightsArray);
        final EdgeLight[] copy2 = EdgeLight.copy(this.mLightsArray);
        final float n = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM_LEFT) * 0.8f;
        final float n2 = -1.0f * n;
        final float regionWidth = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM);
        copy2[0].setEndpoints(n2, n2);
        copy2[1].setEndpoints(n2, n2);
        final EdgeLight edgeLight = copy2[2];
        final float n3 = regionWidth + n;
        edgeLight.setEndpoints(n3, n3);
        copy2[3].setEndpoints(n3, n3);
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.setInterpolator((TimeInterpolator)FulfillBottom.EXIT_TO_CORNER_INTERPOLATOR);
        ofFloat.setDuration(350L);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new EdgeLightUpdateListener(copy, copy2, this.mLightsArray, this.mEdgeLightsView));
        return ofFloat;
    }
    
    private void setRelativePoints(float n, float n2, final float n3) {
        final float regionWidth = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM);
        final EdgeLight mBlueLight = this.mBlueLight;
        final float n4 = n * regionWidth;
        mBlueLight.setEndpoints(0.0f, n4);
        final EdgeLight mRedLight = this.mRedLight;
        n = n2 * regionWidth;
        mRedLight.setEndpoints(n4, n);
        final EdgeLight mYellowLight = this.mYellowLight;
        n2 = n3 * regionWidth;
        mYellowLight.setEndpoints(n, n2);
        this.mGreenLight.setEndpoints(n2, regionWidth);
        this.mEdgeLightsView.setAssistLights(this.mLightsArray);
    }
    
    private boolean swingingToLeft() {
        return this.mSwingLeft;
    }
    
    @Override
    public int getSubType() {
        return 3;
    }
    
    public boolean isListening() {
        return this.mIsListening;
    }
    
    @Override
    public void onConfigurationChanged() {
        if (this.mNextMode == null) {
            this.start(this.mEdgeLightsView, this.mGuide, this);
        }
        else {
            if (this.mExitAnimations.isRunning()) {
                this.mExitAnimations.cancel();
            }
            this.onNewModeRequest(this.mEdgeLightsView, this.mNextMode);
        }
    }
    
    @Override
    public void onNewModeRequest(final EdgeLightsView edgeLightsView, final Mode mNextMode) {
        this.mNextMode = mNextMode;
        if (this.mCradleAnimations.isRunning()) {
            this.mCradleAnimations.cancel();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("got mode ");
        sb.append(mNextMode.getClass().getSimpleName());
        Log.v("FulfillBottom", sb.toString());
        if (!(mNextMode instanceof Gone)) {
            if (this.mExitAnimations.isRunning()) {
                this.mExitAnimations.cancel();
            }
            this.mEdgeLightsView.commitModeTransition(this.mNextMode);
        }
        else if (!this.mExitAnimations.isRunning()) {
            this.animateExit();
        }
    }
    
    @Override
    public void start(final EdgeLightsView mEdgeLightsView, final PerimeterPathGuide mGuide, final Mode mode) {
        this.mEdgeLightsView = mEdgeLightsView;
        this.mGuide = mGuide;
        mEdgeLightsView.setVisibility(0);
        final EdgeLight[] assistLights = mEdgeLightsView.getAssistLights();
        if ((mode instanceof FullListening || mode instanceof FulfillBottom) && assistLights.length == 4) {
            this.mBlueLight = assistLights[0];
            this.mRedLight = assistLights[1];
            this.mYellowLight = assistLights[2];
            this.mGreenLight = assistLights[3];
        }
        else {
            this.mBlueLight = new EdgeLight(this.mResources.getColor(R$color.edge_light_blue, (Resources$Theme)null), 0.0f, 0.0f);
            this.mRedLight = new EdgeLight(this.mResources.getColor(R$color.edge_light_red, (Resources$Theme)null), 0.0f, 0.0f);
            this.mYellowLight = new EdgeLight(this.mResources.getColor(R$color.edge_light_yellow, (Resources$Theme)null), 0.0f, 0.0f);
            this.mGreenLight = new EdgeLight(this.mResources.getColor(R$color.edge_light_green, (Resources$Theme)null), 0.0f, 0.0f);
        }
        this.mLightsArray = new EdgeLight[] { this.mBlueLight, this.mRedLight, this.mYellowLight, this.mGreenLight };
        boolean mSwingLeft;
        if (mode instanceof FulfillBottom) {
            mSwingLeft = ((FulfillBottom)mode).swingingToLeft();
        }
        else {
            mSwingLeft = this.mRandom.nextBoolean();
        }
        this.mSwingLeft = mSwingLeft;
        this.animateCradle();
    }
}
