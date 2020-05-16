// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights.mode;

import android.os.SystemClock;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightUpdateListener;
import android.animation.TimeInterpolator;
import android.animation.Animator$AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.AnimatorListenerAdapter;
import android.util.MathUtils;
import android.content.res.Resources$Theme;
import com.android.systemui.R$color;
import android.content.Context;
import com.google.android.systemui.assist.uihints.RollingAverage;
import com.android.systemui.assist.ui.EdgeLight;
import com.android.systemui.assist.ui.PerimeterPathGuide;
import android.animation.Animator;
import android.view.animation.PathInterpolator;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;

public final class FullListening implements Mode
{
    private static final PathInterpolator INTERPOLATOR;
    private Animator mAnimator;
    private EdgeLightsView mEdgeLightsView;
    private final boolean mFakeForHalfListening;
    private PerimeterPathGuide mGuide;
    private boolean mLastPerturbationWasEven;
    private long mLastSpeechTimestampMs;
    private final EdgeLight[] mLights;
    private RollingAverage mRollingConfidence;
    private State mState;
    
    static {
        INTERPOLATOR = new PathInterpolator(0.33f, 0.0f, 0.67f, 1.0f);
    }
    
    public FullListening(final Context context, final boolean mFakeForHalfListening) {
        this.mLastPerturbationWasEven = false;
        this.mLastSpeechTimestampMs = 0L;
        this.mRollingConfidence = new RollingAverage(3);
        this.mState = State.NOT_STARTED;
        this.mFakeForHalfListening = mFakeForHalfListening;
        this.mLights = new EdgeLight[] { new EdgeLight(context.getResources().getColor(R$color.edge_light_blue, (Resources$Theme)null), 0.0f, 0.0f), new EdgeLight(context.getResources().getColor(R$color.edge_light_red, (Resources$Theme)null), 0.0f, 0.0f), new EdgeLight(context.getResources().getColor(R$color.edge_light_yellow, (Resources$Theme)null), 0.0f, 0.0f), new EdgeLight(context.getResources().getColor(R$color.edge_light_green, (Resources$Theme)null), 0.0f, 0.0f) };
    }
    
    private EdgeLight[] createPerturbedLights() {
        final float regionWidth = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM);
        float n;
        if (this.mState == State.LISTENING_TO_SPEECH) {
            if (this.mLastPerturbationWasEven) {
                n = 0.39999998f;
            }
            else {
                n = 0.6f;
            }
        }
        else if (this.mLastPerturbationWasEven) {
            n = 0.49f;
        }
        else {
            n = 0.51f;
        }
        final float n2 = n * regionWidth;
        final float n3 = regionWidth / 2.0f;
        final float lerp = MathUtils.lerp(Math.min(n3, n2), Math.max(n3, n2), (float)this.mRollingConfidence.getAverage());
        final float n4 = regionWidth - lerp;
        this.mLastPerturbationWasEven ^= true;
        double n5;
        if (this.mState == State.LISTENING_TO_SPEECH) {
            n5 = 0.6;
        }
        else {
            n5 = 0.52;
        }
        double n6;
        if (this.mState == State.LISTENING_TO_SPEECH) {
            n6 = 0.4;
        }
        else {
            n6 = 0.48;
        }
        final double n7 = n5 - n6;
        final float n8 = (float)(Math.random() * n7 + n6);
        final float n9 = (float)(Math.random() * n7 + n6);
        final float n10 = n8 * lerp;
        final float length = n9 * n4;
        final float length2 = n4 - length;
        final EdgeLight[] copy = EdgeLight.copy(this.mLights);
        copy[0].setLength(n10);
        copy[1].setLength(length);
        copy[2].setLength(length2);
        copy[3].setLength(lerp - n10);
        copy[0].setStart(0.0f);
        copy[1].setStart(n10);
        final EdgeLight edgeLight = copy[2];
        final float start = n10 + length;
        edgeLight.setStart(start);
        copy[3].setStart(start + length2);
        return copy;
    }
    
    private AnimatorListenerAdapter createUpdateStateOnEndAnimatorListener() {
        return new AnimatorListenerAdapter() {
            private boolean mCancelled = false;
            
            public void onAnimationCancel(final Animator animator) {
                super.onAnimationCancel(animator);
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                super.onAnimationEnd(animator);
                if (FullListening.this.mAnimator == animator) {
                    FullListening.this.mAnimator = null;
                }
                if (this.mCancelled) {
                    return;
                }
                FullListening.this.updateStateAndAnimation();
            }
        };
    }
    
    private long getExpandToWidthDuration(final EdgeLightsView edgeLightsView, final Mode mode) {
        if (mode instanceof FullListening) {
            return 0L;
        }
        if (mode instanceof FulfillBottom) {
            return 300L;
        }
        if (!edgeLightsView.getAssistInvocationLights().isEmpty()) {
            return 0L;
        }
        return 500L;
    }
    
    private EdgeLight[] getFinalLights() {
        final EdgeLight[] copy = EdgeLight.copy(this.mLights);
        final float length = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM) / 4.0f;
        for (int i = 0; i < copy.length; ++i) {
            copy[i].setStart(i * length);
            copy[i].setLength(length);
        }
        return copy;
    }
    
    private EdgeLight[] getInitialLights(final EdgeLightsView edgeLightsView, final PerimeterPathGuide perimeterPathGuide, final Mode mode) {
        final EdgeLight[] copy = EdgeLight.copy(this.mLights);
        EdgeLight[] copy2;
        if (edgeLightsView.getAssistLights() != null) {
            copy2 = EdgeLight.copy(edgeLightsView.getAssistLights());
        }
        else {
            copy2 = null;
        }
        final boolean b = mode instanceof FulfillBottom;
        int i = 0;
        final boolean b2 = b && copy2 != null && copy.length == copy2.length;
        while (i < copy.length) {
            final EdgeLight edgeLight = copy[i];
            float start;
            if (b2) {
                start = copy2[i].getStart();
            }
            else {
                start = perimeterPathGuide.getRegionCenter(PerimeterPathGuide.Region.BOTTOM);
            }
            edgeLight.setStart(start);
            float length;
            if (b2) {
                length = copy2[i].getLength();
            }
            else {
                length = 0.0f;
            }
            edgeLight.setLength(length);
            ++i;
        }
        return copy;
    }
    
    private void setAnimator(final Animator mAnimator) {
        final Animator mAnimator2 = this.mAnimator;
        if (mAnimator2 != null) {
            mAnimator2.cancel();
        }
        if ((this.mAnimator = mAnimator) != null) {
            mAnimator.start();
        }
    }
    
    private void updateStateAndAnimation() {
        EdgeLight[] array;
        int n;
        if (this.mRollingConfidence.getAverage() > 0.10000000149011612) {
            if (this.mState == State.LISTENING_TO_SPEECH && this.mAnimator != null) {
                return;
            }
            this.mState = State.LISTENING_TO_SPEECH;
            array = this.createPerturbedLights();
            n = (int)MathUtils.lerp(400.0f, 150.0f, (float)this.mRollingConfidence.getAverage());
        }
        else {
            final State mState = this.mState;
            if (mState != State.LISTENING_TO_SPEECH && mState != State.WAITING_FOR_ENDPOINTER) {
                if (mState == State.WAITING_FOR_SPEECH && this.mAnimator != null) {
                    return;
                }
                this.mState = State.WAITING_FOR_SPEECH;
                array = this.createPerturbedLights();
                n = 1200;
            }
            else {
                if (this.mAnimator != null) {
                    final State mState2 = this.mState;
                    if (mState2 == State.WAITING_FOR_ENDPOINTER || mState2 == State.LISTENING_TO_SPEECH) {
                        return;
                    }
                }
                this.mState = State.WAITING_FOR_ENDPOINTER;
                array = this.getFinalLights();
                n = 2000;
            }
        }
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.addListener((Animator$AnimatorListener)this.createUpdateStateOnEndAnimatorListener());
        ofFloat.setDuration((long)n);
        ofFloat.setInterpolator((TimeInterpolator)FullListening.INTERPOLATOR);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new EdgeLightUpdateListener(EdgeLight.copy(this.mLights), array, this.mLights, this.mEdgeLightsView));
        this.setAnimator((Animator)ofFloat);
    }
    
    @Override
    public int getSubType() {
        return 1;
    }
    
    public boolean isFakeForHalfListening() {
        return this.mFakeForHalfListening;
    }
    
    @Override
    public void onAudioLevelUpdate(final float n, final float n2) {
        this.mRollingConfidence.add(n);
        long mLastSpeechTimestampMs;
        if (n > 0.1f) {
            mLastSpeechTimestampMs = SystemClock.uptimeMillis();
        }
        else {
            mLastSpeechTimestampMs = this.mLastSpeechTimestampMs;
        }
        this.mLastSpeechTimestampMs = mLastSpeechTimestampMs;
        if (this.mState != State.EXPANDING_TO_WIDTH) {
            this.updateStateAndAnimation();
        }
    }
    
    @Override
    public void onConfigurationChanged() {
        this.setAnimator(null);
        final EdgeLight[] mLights = this.mLights;
        final int length = mLights.length;
        float n = 0.0f;
        for (int i = 0; i < length; ++i) {
            n += mLights[i].getLength();
        }
        final float regionWidth = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM);
        this.mLights[0].setStart(0.0f);
        final EdgeLight[] mLights2 = this.mLights;
        mLights2[0].setLength(mLights2[0].getLength() / n * regionWidth);
        final EdgeLight[] mLights3 = this.mLights;
        mLights3[1].setStart(mLights3[0].getStart() + this.mLights[0].getLength());
        final EdgeLight[] mLights4 = this.mLights;
        mLights4[1].setLength(mLights4[1].getLength() / n * regionWidth);
        final EdgeLight[] mLights5 = this.mLights;
        mLights5[2].setStart(mLights5[1].getStart() + this.mLights[1].getLength());
        final EdgeLight[] mLights6 = this.mLights;
        mLights6[2].setLength(mLights6[2].getLength() / n * regionWidth);
        final EdgeLight[] mLights7 = this.mLights;
        mLights7[3].setStart(mLights7[2].getStart() + this.mLights[2].getLength());
        final EdgeLight[] mLights8 = this.mLights;
        mLights8[3].setLength(mLights8[3].getLength() / n * regionWidth);
        this.updateStateAndAnimation();
    }
    
    @Override
    public void onNewModeRequest(final EdgeLightsView edgeLightsView, final Mode mode) {
        if (mode instanceof FullListening && ((FullListening)mode).mFakeForHalfListening == this.mFakeForHalfListening) {
            return;
        }
        this.setAnimator(null);
        edgeLightsView.commitModeTransition(mode);
    }
    
    @Override
    public boolean preventsInvocations() {
        return true;
    }
    
    @Override
    public void start(final EdgeLightsView mEdgeLightsView, final PerimeterPathGuide mGuide, final Mode mode) {
        this.mEdgeLightsView = mEdgeLightsView;
        this.mGuide = mGuide;
        this.mState = State.EXPANDING_TO_WIDTH;
        mEdgeLightsView.setVisibility(0);
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.setDuration(this.getExpandToWidthDuration(mEdgeLightsView, mode));
        ofFloat.setInterpolator((TimeInterpolator)FullListening.INTERPOLATOR);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new EdgeLightUpdateListener(this.getInitialLights(mEdgeLightsView, mGuide, mode), this.getFinalLights(), this.mLights, mEdgeLightsView));
        ofFloat.addListener((Animator$AnimatorListener)this.createUpdateStateOnEndAnimatorListener());
        this.setAnimator((Animator)ofFloat);
    }
    
    private enum State
    {
        EXPANDING_TO_WIDTH, 
        LISTENING_TO_SPEECH, 
        NOT_STARTED, 
        WAITING_FOR_ENDPOINTER, 
        WAITING_FOR_SPEECH;
    }
}
