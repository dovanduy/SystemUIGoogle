// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.util.MathUtils;
import com.google.android.systemui.assist.uihints.edgelights.mode.FullListening;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;
import com.android.systemui.R$dimen;
import android.graphics.Rect;
import android.graphics.Region;
import java.util.Optional;
import android.view.SurfaceControl;
import android.graphics.drawable.Drawable;
import com.android.systemui.R$drawable;
import android.util.Log;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import android.view.View$OnTouchListener;
import android.view.View$OnClickListener;
import android.graphics.BlendMode;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import android.view.View;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import com.google.android.systemui.assist.uihints.input.TouchInsideRegion;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsListener;

public class ScrimController implements TranscriptionSpaceListener, CardInfoListener, EdgeLightsListener, TouchInsideRegion
{
    private static final LinearInterpolator ALPHA_INTERPOLATOR;
    private ValueAnimator mAlphaAnimator;
    private boolean mCardForcesScrimGone;
    private boolean mCardTransitionAnimated;
    private boolean mCardVisible;
    private boolean mHaveAccurateLightness;
    private boolean mInFullListening;
    private float mInvocationProgress;
    private boolean mIsDozing;
    private final LightnessProvider mLightnessProvider;
    private float mMedianLightness;
    private final OverlappedElementController mOverlappedElement;
    private final View mScrimView;
    private boolean mTranscriptionVisible;
    private VisibilityListener mVisibilityListener;
    
    static {
        ALPHA_INTERPOLATOR = new LinearInterpolator();
    }
    
    public ScrimController(final ViewGroup viewGroup, final OverlappedElementController mOverlappedElement, final LightnessProvider mLightnessProvider, final TouchInsideHandler touchInsideHandler) {
        this.mAlphaAnimator = new ValueAnimator();
        this.mInvocationProgress = 0.0f;
        this.mTranscriptionVisible = false;
        this.mCardVisible = false;
        this.mHaveAccurateLightness = false;
        this.mInFullListening = false;
        this.mCardTransitionAnimated = false;
        this.mCardForcesScrimGone = false;
        this.mIsDozing = false;
        (this.mScrimView = viewGroup.findViewById(R$id.scrim)).setBackgroundTintBlendMode(BlendMode.SRC_IN);
        this.mLightnessProvider = mLightnessProvider;
        this.mScrimView.setOnClickListener((View$OnClickListener)touchInsideHandler);
        this.mScrimView.setOnTouchListener((View$OnTouchListener)touchInsideHandler);
        this.mOverlappedElement = mOverlappedElement;
    }
    
    private ValueAnimator createRelativeAlphaAnimator(final float n) {
        final ValueAnimator setDuration = ValueAnimator.ofFloat(new float[] { this.mScrimView.getAlpha(), n }).setDuration((long)(Math.abs(n - this.mScrimView.getAlpha()) * 300.0f));
        setDuration.setInterpolator((TimeInterpolator)ScrimController.ALPHA_INTERPOLATOR);
        setDuration.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ScrimController$V4Rx0mUN7UWA_Ls8nihlMw8zVgY(this));
        return setDuration;
    }
    
    private void refresh() {
        if (this.mHaveAccurateLightness && !this.mIsDozing) {
            if (this.mCardVisible && this.mCardForcesScrimGone) {
                this.setRelativeAlpha(0.0f, this.mCardTransitionAnimated);
            }
            else if (!this.mInFullListening && !this.mTranscriptionVisible) {
                if (this.mCardVisible) {
                    this.setRelativeAlpha(0.0f, this.mCardTransitionAnimated);
                }
                else {
                    final float mInvocationProgress = this.mInvocationProgress;
                    if (mInvocationProgress > 0.0f) {
                        this.setRelativeAlpha(Math.min(1.0f, mInvocationProgress), false);
                    }
                    else {
                        this.setRelativeAlpha(0.0f, true);
                    }
                }
            }
            else if (!this.mCardVisible || this.isVisible()) {
                this.setRelativeAlpha(1.0f, false);
            }
        }
        else {
            this.setRelativeAlpha(0.0f, false);
        }
    }
    
    private void setAlpha(final float alpha) {
        this.mScrimView.setAlpha(alpha);
        this.mOverlappedElement.setAlpha(1.0f - alpha);
    }
    
    private void setRelativeAlpha(final float n, final boolean b) {
        if (!this.mHaveAccurateLightness && n > 0.0f) {
            return;
        }
        if (n >= 0.0f && n <= 1.0f) {
            if (this.mAlphaAnimator.isRunning()) {
                this.mAlphaAnimator.cancel();
            }
            if (n > 0.0f) {
                if (this.mScrimView.getVisibility() != 0) {
                    final View mScrimView = this.mScrimView;
                    int n2;
                    if (this.mMedianLightness <= 0.4f) {
                        n2 = -16777216;
                    }
                    else {
                        n2 = -1;
                    }
                    mScrimView.setBackgroundTintList(ColorStateList.valueOf(n2));
                    this.setVisibility(0);
                }
                if (b) {
                    (this.mAlphaAnimator = this.createRelativeAlphaAnimator(n)).start();
                }
                else {
                    this.setAlpha(n);
                }
            }
            else if (b) {
                (this.mAlphaAnimator = this.createRelativeAlphaAnimator(n)).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    private boolean mCancelled = false;
                    
                    public void onAnimationCancel(final Animator animator) {
                        super.onAnimationCancel(animator);
                        this.mCancelled = true;
                    }
                    
                    public void onAnimationEnd(final Animator animator) {
                        super.onAnimationEnd(animator);
                        if (this.mCancelled) {
                            return;
                        }
                        ScrimController.this.setVisibility(8);
                    }
                });
                this.mAlphaAnimator.start();
            }
            else {
                this.setAlpha(0.0f);
                this.setVisibility(8);
            }
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Got unexpected alpha: ");
        sb.append(n);
        sb.append(", ignoring");
        Log.e("ScrimController", sb.toString());
    }
    
    private void setVisibility(final int visibility) {
        if (visibility == this.mScrimView.getVisibility()) {
            return;
        }
        this.mScrimView.setVisibility(visibility);
        final VisibilityListener mVisibilityListener = this.mVisibilityListener;
        if (mVisibilityListener != null) {
            mVisibilityListener.onVisibilityChanged(visibility);
        }
        this.mLightnessProvider.setMuted(visibility == 0);
        final View mScrimView = this.mScrimView;
        Drawable drawable;
        if (visibility == 0) {
            drawable = mScrimView.getContext().getDrawable(R$drawable.scrim_strip);
        }
        else {
            drawable = null;
        }
        mScrimView.setBackground(drawable);
        if (visibility != 0) {
            this.mOverlappedElement.setAlpha(1.0f);
            this.refresh();
        }
    }
    
    SurfaceControl getSurfaceControllerHandle() {
        if (this.mScrimView.getViewRootImpl() == null) {
            return null;
        }
        return this.mScrimView.getViewRootImpl().getSurfaceControl();
    }
    
    @Override
    public Optional<Region> getTouchInsideRegion() {
        if (!this.isVisible()) {
            return Optional.empty();
        }
        final Rect rect = new Rect();
        this.mScrimView.getHitRect(rect);
        rect.top = rect.bottom - this.mScrimView.getResources().getDimensionPixelSize(R$dimen.scrim_touchable_height);
        return Optional.of(new Region(rect));
    }
    
    boolean isVisible() {
        return this.mScrimView.getVisibility() == 0;
    }
    
    @Override
    public void onCardInfo(final boolean mCardVisible, final int n, final boolean mCardTransitionAnimated, final boolean mCardForcesScrimGone) {
        this.mCardVisible = mCardVisible;
        this.mCardTransitionAnimated = mCardTransitionAnimated;
        this.mCardForcesScrimGone = mCardForcesScrimGone;
        this.refresh();
    }
    
    void onLightnessInvalidated() {
        this.mHaveAccurateLightness = false;
        this.refresh();
    }
    
    @Override
    public void onModeStarted(final EdgeLightsView.Mode mode) {
        this.mInFullListening = (mode instanceof FullListening);
        this.refresh();
    }
    
    @Override
    public void onStateChanged(final State state, final State state2) {
        final boolean mTranscriptionVisible = state2 != State.NONE;
        if (this.mTranscriptionVisible == mTranscriptionVisible) {
            return;
        }
        this.mTranscriptionVisible = mTranscriptionVisible;
        this.refresh();
    }
    
    void setHasMedianLightness(final float mMedianLightness) {
        this.mHaveAccurateLightness = true;
        this.mMedianLightness = mMedianLightness;
        this.refresh();
    }
    
    void setInvocationProgress(float constrain) {
        constrain = MathUtils.constrain(constrain, 0.0f, 1.0f);
        if (this.mInvocationProgress == constrain) {
            return;
        }
        this.mInvocationProgress = constrain;
        this.refresh();
    }
    
    void setIsDozing(final boolean mIsDozing) {
        this.mIsDozing = mIsDozing;
        this.refresh();
    }
    
    void setVisibilityListener(final VisibilityListener mVisibilityListener) {
        this.mVisibilityListener = mVisibilityListener;
    }
}
