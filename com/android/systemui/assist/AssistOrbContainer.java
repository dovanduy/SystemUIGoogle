// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import com.android.systemui.R$id;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class AssistOrbContainer extends FrameLayout
{
    private boolean mAnimatingOut;
    private View mNavbarScrim;
    private AssistOrbView mOrb;
    private View mScrim;
    
    public AssistOrbContainer(final Context context) {
        this(context, null);
    }
    
    public AssistOrbContainer(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public AssistOrbContainer(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    private void reset() {
        this.mAnimatingOut = false;
        this.mOrb.reset();
        this.mScrim.setAlpha(1.0f);
        this.mNavbarScrim.setAlpha(1.0f);
    }
    
    private void startEnterAnimation() {
        if (this.mAnimatingOut) {
            return;
        }
        this.mOrb.startEnterAnimation();
        this.mScrim.setAlpha(0.0f);
        this.mNavbarScrim.setAlpha(0.0f);
        this.post((Runnable)new Runnable() {
            @Override
            public void run() {
                AssistOrbContainer.this.mScrim.animate().alpha(1.0f).setDuration(300L).setStartDelay(0L).setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
                AssistOrbContainer.this.mNavbarScrim.animate().alpha(1.0f).setDuration(300L).setStartDelay(0L).setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
            }
        });
    }
    
    private void startExitAnimation(final Runnable runnable) {
        if (this.mAnimatingOut) {
            if (runnable != null) {
                runnable.run();
            }
            return;
        }
        this.mAnimatingOut = true;
        this.mOrb.startExitAnimation(150L);
        this.mScrim.animate().alpha(0.0f).setDuration(250L).setStartDelay(150L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        this.mNavbarScrim.animate().alpha(0.0f).setDuration(250L).setStartDelay(150L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).withEndAction(runnable);
    }
    
    public AssistOrbView getOrb() {
        return this.mOrb;
    }
    
    public boolean isShowing() {
        return this.getVisibility() == 0 && !this.mAnimatingOut;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mScrim = this.findViewById(R$id.assist_orb_scrim);
        this.mNavbarScrim = this.findViewById(R$id.assist_orb_navbar_scrim);
        this.mOrb = (AssistOrbView)this.findViewById(R$id.assist_orb);
    }
    
    public void show(final boolean b, final boolean b2) {
        if (b) {
            if (this.getVisibility() != 0) {
                this.setVisibility(0);
                if (b2) {
                    this.startEnterAnimation();
                }
                else {
                    this.reset();
                }
            }
        }
        else if (b2) {
            this.startExitAnimation(new Runnable() {
                @Override
                public void run() {
                    AssistOrbContainer.this.mAnimatingOut = false;
                    AssistOrbContainer.this.setVisibility(8);
                }
            });
        }
        else {
            this.setVisibility(8);
        }
    }
}
