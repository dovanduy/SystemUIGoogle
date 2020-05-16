// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.view.View;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.animation.Interpolator;
import android.util.ArrayMap;
import android.util.Property;
import java.util.function.Consumer;

public class AnimationProperties
{
    public long delay;
    public long duration;
    private Consumer<Property> mAnimationEndAction;
    private ArrayMap<Property, Interpolator> mInterpolatorMap;
    
    public void combineCustomInterpolators(final AnimationProperties animationProperties) {
        final ArrayMap<Property, Interpolator> mInterpolatorMap = animationProperties.mInterpolatorMap;
        if (mInterpolatorMap != null) {
            if (this.mInterpolatorMap == null) {
                this.mInterpolatorMap = (ArrayMap<Property, Interpolator>)new ArrayMap();
            }
            this.mInterpolatorMap.putAll((ArrayMap)mInterpolatorMap);
        }
    }
    
    public AnimationFilter getAnimationFilter() {
        return new AnimationFilter(this) {
            @Override
            public boolean shouldAnimateProperty(final Property property) {
                return true;
            }
        };
    }
    
    public AnimatorListenerAdapter getAnimationFinishListener(final Property property) {
        final Consumer<Property> mAnimationEndAction = this.mAnimationEndAction;
        if (mAnimationEndAction == null) {
            return null;
        }
        return new AnimatorListenerAdapter(this) {
            private boolean mCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (!this.mCancelled) {
                    mAnimationEndAction.accept(property);
                }
            }
        };
    }
    
    public Interpolator getCustomInterpolator(final View view, final Property property) {
        final ArrayMap<Property, Interpolator> mInterpolatorMap = this.mInterpolatorMap;
        Interpolator interpolator;
        if (mInterpolatorMap != null) {
            interpolator = (Interpolator)mInterpolatorMap.get((Object)property);
        }
        else {
            interpolator = null;
        }
        return interpolator;
    }
    
    public AnimationProperties resetCustomInterpolators() {
        this.mInterpolatorMap = null;
        return this;
    }
    
    public AnimationProperties setAnimationEndAction(final Consumer<Property> mAnimationEndAction) {
        this.mAnimationEndAction = mAnimationEndAction;
        return this;
    }
    
    public AnimationProperties setCustomInterpolator(final Property property, final Interpolator interpolator) {
        if (this.mInterpolatorMap == null) {
            this.mInterpolatorMap = (ArrayMap<Property, Interpolator>)new ArrayMap();
        }
        this.mInterpolatorMap.put((Object)property, (Object)interpolator);
        return this;
    }
    
    public AnimationProperties setDelay(final long delay) {
        this.delay = delay;
        return this;
    }
    
    public AnimationProperties setDuration(final long duration) {
        this.duration = duration;
        return this;
    }
    
    public boolean wasAdded(final View view) {
        return false;
    }
}
