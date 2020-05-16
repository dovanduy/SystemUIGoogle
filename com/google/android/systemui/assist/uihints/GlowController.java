// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.graphics.PorterDuff$Mode;
import android.util.Log;
import android.graphics.Rect;
import android.graphics.Region;
import java.util.Optional;
import com.google.android.systemui.assist.uihints.edgelights.mode.FullListening;
import com.google.android.systemui.assist.uihints.edgelights.mode.Gone;
import com.google.android.systemui.assist.uihints.edgelights.mode.FulfillBottom;
import android.util.MathUtils;
import com.android.systemui.R$dimen;
import android.animation.TimeInterpolator;
import android.view.animation.LinearInterpolator;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.View$OnTouchListener;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.NavigationModeController;
import android.view.ViewGroup;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;
import com.android.systemui.assist.ui.EdgeLight;
import android.content.Context;
import android.animation.ValueAnimator;
import com.google.android.systemui.assist.uihints.input.TouchInsideRegion;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsListener;

public final class GlowController implements AudioInfoListener, CardInfoListener, EdgeLightsListener, TouchInsideRegion
{
    private ValueAnimator mAnimator;
    private boolean mCardVisible;
    private final Context mContext;
    private EdgeLight[] mEdgeLights;
    private EdgeLightsView.Mode mEdgeLightsMode;
    private final GlowView mGlowView;
    private int mGlowsY;
    private int mGlowsYDestination;
    private boolean mInvocationCompleting;
    private float mMedianLightness;
    private RollingAverage mSpeechRolling;
    private VisibilityListener mVisibilityListener;
    
    GlowController(final Context mContext, final ViewGroup viewGroup, final TouchInsideHandler touchInsideHandler) {
        this.mEdgeLights = null;
        this.mEdgeLightsMode = null;
        this.mAnimator = null;
        this.mGlowsY = 0;
        this.mGlowsYDestination = 0;
        this.mSpeechRolling = new RollingAverage(3);
        this.mInvocationCompleting = false;
        this.mCardVisible = false;
        this.mContext = mContext;
        Dependency.get(NavigationModeController.class).addListener((NavigationModeController.ModeChangedListener)new _$$Lambda$GlowController$p_RUOKgBpKNbCOCs2BtGayinrRI(this));
        final GlowView mGlowView = (GlowView)viewGroup.findViewById(R$id.glow);
        this.mGlowView = mGlowView;
        final int mGlowsY = this.mGlowsY;
        mGlowView.setGlowsY(mGlowsY, mGlowsY, null);
        this.mGlowView.setOnClickListener((View$OnClickListener)touchInsideHandler);
        this.mGlowView.setOnTouchListener((View$OnTouchListener)touchInsideHandler);
        this.mGlowView.setGlowsY(this.getMinTranslationY(), this.getMinTranslationY(), null);
        this.mGlowView.setGlowWidthRatio(this.getGlowWidthToViewWidth());
    }
    
    private void animateGlowTranslationY(final int n) {
        this.animateGlowTranslationY(n, this.getYAnimationDuration((float)(n - this.mGlowsY)));
    }
    
    private void animateGlowTranslationY(int mGlowsYDestination, final long duration) {
        if (mGlowsYDestination == this.mGlowsYDestination) {
            final GlowView mGlowView = this.mGlowView;
            final int mGlowsY = this.mGlowsY;
            mGlowsYDestination = this.getMinTranslationY();
            EdgeLight[] mEdgeLights;
            if (this.getTranslationYProportionalToEdgeLights()) {
                mEdgeLights = this.mEdgeLights;
            }
            else {
                mEdgeLights = null;
            }
            mGlowView.setGlowsY(mGlowsY, mGlowsYDestination, mEdgeLights);
            return;
        }
        this.mGlowsYDestination = mGlowsYDestination;
        final ValueAnimator mAnimator = this.mAnimator;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        (this.mAnimator = ValueAnimator.ofInt(new int[] { this.mGlowsY, mGlowsYDestination })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GlowController$ddVtB0V_XlJzUh86At1jXulvciQ(this));
        this.mAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                GlowController.this.mAnimator = null;
                if (GlowState.GONE.equals(GlowController.this.getState())) {
                    GlowController.this.setVisibility(8);
                    return;
                }
                GlowController.this.maybeAnimateForSpeechConfidence();
            }
        });
        this.mAnimator.setInterpolator((TimeInterpolator)new LinearInterpolator());
        this.mAnimator.setDuration(duration);
        mGlowsYDestination = this.mGlowView.getBlurRadius();
        this.mAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GlowController$LnsJsfqQkxOAPCYtkIs6g3HuvG4(this, mGlowsYDestination, this.getBlurRadius()));
        final float glowWidthRatio = this.mGlowView.getGlowWidthRatio();
        this.mGlowView.setGlowWidthRatio(glowWidthRatio + (this.getGlowWidthToViewWidth() - glowWidthRatio) * 1.0f);
        if (this.mGlowView.getVisibility() != 0) {
            this.setVisibility(0);
        }
        this.mAnimator.start();
    }
    
    private int getBlurRadius() {
        if (this.getState() == GlowState.GONE) {
            return this.mGlowView.getBlurRadius();
        }
        if (this.getState() == GlowState.SHORT_DARK_BACKGROUND || this.getState() == GlowState.SHORT_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_short_blur);
        }
        if (this.getState() != GlowState.TALL_DARK_BACKGROUND && this.getState() != GlowState.TALL_LIGHT_BACKGROUND) {
            return 0;
        }
        return this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_tall_blur);
    }
    
    private float getGlowWidthToViewWidth() {
        return 0.55f;
    }
    
    private int getInvocationBlurRadius(final float n) {
        return (int)MathUtils.lerp((float)this.getBlurRadius(), (float)this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_tall_blur), Math.min(1.0f, n * 5.0f));
    }
    
    private int getInvocationTranslationY(final float n) {
        return (int)MathUtils.min((int)MathUtils.lerp((float)this.getMinTranslationY(), (float)this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_tall_min_y), n), this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_invocation_max));
    }
    
    private int getMaxTranslationY() {
        if (this.getState() == GlowState.SHORT_DARK_BACKGROUND || this.getState() == GlowState.SHORT_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_short_max_y);
        }
        if (this.getState() != GlowState.TALL_DARK_BACKGROUND && this.getState() != GlowState.TALL_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_gone_max_y);
        }
        return this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_tall_max_y);
    }
    
    private long getMaxYAnimationDuration() {
        return 400L;
    }
    
    private int getMinTranslationY() {
        if (this.getState() == GlowState.SHORT_DARK_BACKGROUND || this.getState() == GlowState.SHORT_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_short_min_y);
        }
        if (this.getState() != GlowState.TALL_DARK_BACKGROUND && this.getState() != GlowState.TALL_LIGHT_BACKGROUND) {
            return this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_gone_min_y);
        }
        return this.mContext.getResources().getDimensionPixelSize(R$dimen.glow_tall_min_y);
    }
    
    private GlowState getState() {
        final EdgeLightsView.Mode mEdgeLightsMode = this.mEdgeLightsMode;
        final boolean b = mEdgeLightsMode instanceof FulfillBottom;
        final int n = 1;
        final boolean b2 = b && !((FulfillBottom)mEdgeLightsMode).isListening();
        final EdgeLightsView.Mode mEdgeLightsMode2 = this.mEdgeLightsMode;
        if (!(mEdgeLightsMode2 instanceof Gone) && mEdgeLightsMode2 != null && !b2) {
            final boolean mCardVisible = this.mCardVisible;
            int n2;
            if (this.mMedianLightness < 0.4f) {
                n2 = n;
            }
            else {
                n2 = 0;
            }
            GlowState glowState;
            if (n2 != 0) {
                glowState = GlowState.TALL_DARK_BACKGROUND;
            }
            else {
                glowState = GlowState.TALL_LIGHT_BACKGROUND;
            }
            return glowState;
        }
        return GlowState.GONE;
    }
    
    private boolean getTranslationYProportionalToEdgeLights() {
        return this.mEdgeLightsMode instanceof FullListening;
    }
    
    private long getYAnimationDuration(float n) {
        n = Math.abs(n) / (Math.abs(this.getMaxTranslationY() - this.getMinTranslationY()) / this.getMaxYAnimationDuration());
        return (long)Math.min((float)this.getMaxYAnimationDuration(), n);
    }
    
    private void maybeAnimateForSpeechConfidence() {
        if (this.shouldAnimateForSpeechConfidence()) {
            this.animateGlowTranslationY((int)MathUtils.lerp((float)this.getMinTranslationY(), (float)this.getMaxTranslationY(), (float)this.mSpeechRolling.getAverage()));
        }
    }
    
    private void setVisibility(final int visibility) {
        this.mGlowView.setVisibility(visibility);
        if (visibility == 0 == this.isVisible()) {
            return;
        }
        final VisibilityListener mVisibilityListener = this.mVisibilityListener;
        if (mVisibilityListener != null) {
            mVisibilityListener.onVisibilityChanged(visibility);
        }
        if (!this.isVisible()) {
            this.mGlowView.clearCaches();
        }
    }
    
    private boolean shouldAnimateForSpeechConfidence() {
        final EdgeLightsView.Mode mEdgeLightsMode = this.mEdgeLightsMode;
        final boolean b = mEdgeLightsMode instanceof FullListening;
        boolean b2 = false;
        if (!b && !(mEdgeLightsMode instanceof FulfillBottom)) {
            return false;
        }
        if (this.mSpeechRolling.getAverage() >= 0.30000001192092896 || this.mGlowsYDestination > this.getMinTranslationY()) {
            b2 = true;
        }
        return b2;
    }
    
    @Override
    public Optional<Region> getTouchInsideRegion() {
        if (this.mGlowView.getVisibility() == 0) {
            final Rect rect = new Rect();
            this.mGlowView.getBoundsOnScreen(rect);
            rect.top = rect.bottom - this.getMaxTranslationY();
            return Optional.of(new Region(rect));
        }
        return Optional.empty();
    }
    
    public boolean isVisible() {
        return this.mGlowView.getVisibility() == 0;
    }
    
    @Override
    public void onAssistLightsUpdated(final EdgeLightsView.Mode mode, final EdgeLight[] mEdgeLights) {
        if (!this.getTranslationYProportionalToEdgeLights()) {
            this.mEdgeLights = null;
            this.mGlowView.distributeEvenly();
            return;
        }
        this.mEdgeLights = mEdgeLights;
        if (this.mInvocationCompleting && mode instanceof Gone) {
            return;
        }
        if (mode instanceof FullListening) {
            if (mEdgeLights != null && mEdgeLights.length == 4) {
                this.maybeAnimateForSpeechConfidence();
            }
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Expected 4 lights, have ");
                int length;
                if (mEdgeLights == null) {
                    length = 0;
                }
                else {
                    length = mEdgeLights.length;
                }
                sb.append(length);
                Log.e("GlowController", sb.toString());
            }
        }
    }
    
    @Override
    public void onAudioInfo(final float n, final float n2) {
        this.mSpeechRolling.add(n2);
        this.maybeAnimateForSpeechConfidence();
    }
    
    @Override
    public void onCardInfo(final boolean mCardVisible, final int n, final boolean b, final boolean b2) {
        this.mCardVisible = mCardVisible;
    }
    
    @Override
    public void onModeStarted(final EdgeLightsView.Mode mode) {
        final boolean b = mode instanceof Gone;
        if (b && this.mEdgeLightsMode == null) {
            this.mEdgeLightsMode = mode;
            return;
        }
        this.mInvocationCompleting = (b ^ true);
        this.mEdgeLightsMode = mode;
        if (b) {
            this.mSpeechRolling = new RollingAverage(3);
        }
        this.animateGlowTranslationY(this.getMinTranslationY());
    }
    
    void setInvocationProgress(final float n) {
        if (!(this.mEdgeLightsMode instanceof Gone)) {
            return;
        }
        int visibility;
        if (n > 0.0f) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        this.setVisibility(visibility);
        this.mGlowView.setBlurRadius(this.getInvocationBlurRadius(n));
        final int invocationTranslationY = this.getInvocationTranslationY(n);
        this.mGlowsY = invocationTranslationY;
        this.mGlowsYDestination = invocationTranslationY;
        this.mGlowView.setGlowsY(invocationTranslationY, invocationTranslationY, null);
        this.mGlowView.distributeEvenly();
    }
    
    void setMedianLightness(final float mMedianLightness) {
        final GlowView mGlowView = this.mGlowView;
        PorterDuff$Mode glowsBlendMode;
        if (mMedianLightness <= 0.4f) {
            glowsBlendMode = PorterDuff$Mode.LIGHTEN;
        }
        else {
            glowsBlendMode = PorterDuff$Mode.SRC_OVER;
        }
        mGlowView.setGlowsBlendMode(glowsBlendMode);
        this.mMedianLightness = mMedianLightness;
    }
    
    void setVisibilityListener(final VisibilityListener mVisibilityListener) {
        this.mVisibilityListener = mVisibilityListener;
    }
    
    private enum GlowState
    {
        GONE, 
        SHORT_DARK_BACKGROUND, 
        SHORT_LIGHT_BACKGROUND, 
        TALL_DARK_BACKGROUND, 
        TALL_LIGHT_BACKGROUND;
    }
}
