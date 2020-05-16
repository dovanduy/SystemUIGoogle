// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import java.util.ArrayList;
import android.util.Property;
import android.view.View;
import androidx.collection.ArraySet;

public class AnimationFilter
{
    boolean animateAlpha;
    boolean animateDimmed;
    boolean animateHeight;
    boolean animateHideSensitive;
    boolean animateTopInset;
    boolean animateX;
    boolean animateY;
    ArraySet<View> animateYViews;
    boolean animateZ;
    long customDelay;
    boolean hasDelays;
    boolean hasGoToFullShadeEvent;
    private ArraySet<Property> mAnimatedProperties;
    
    public AnimationFilter() {
        this.animateYViews = new ArraySet<View>();
        this.mAnimatedProperties = new ArraySet<Property>();
    }
    
    public AnimationFilter animate(final Property property) {
        this.mAnimatedProperties.add(property);
        return this;
    }
    
    public AnimationFilter animateAlpha() {
        this.animateAlpha = true;
        return this;
    }
    
    public AnimationFilter animateDimmed() {
        this.animateDimmed = true;
        return this;
    }
    
    public AnimationFilter animateHeight() {
        this.animateHeight = true;
        return this;
    }
    
    public AnimationFilter animateHideSensitive() {
        this.animateHideSensitive = true;
        return this;
    }
    
    public AnimationFilter animateScale() {
        this.animate(View.SCALE_X);
        this.animate(View.SCALE_Y);
        return this;
    }
    
    public AnimationFilter animateTopInset() {
        this.animateTopInset = true;
        return this;
    }
    
    public AnimationFilter animateX() {
        this.animateX = true;
        return this;
    }
    
    public AnimationFilter animateY() {
        this.animateY = true;
        return this;
    }
    
    public AnimationFilter animateZ() {
        this.animateZ = true;
        return this;
    }
    
    public void applyCombination(final ArrayList<NotificationStackScrollLayout.AnimationEvent> list) {
        this.reset();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final NotificationStackScrollLayout.AnimationEvent animationEvent = list.get(i);
            this.combineFilter(list.get(i).filter);
            if (animationEvent.animationType == 7) {
                this.hasGoToFullShadeEvent = true;
            }
            final int animationType = animationEvent.animationType;
            if (animationType == 12) {
                this.customDelay = 120L;
            }
            else if (animationType == 13) {
                this.customDelay = 240L;
            }
        }
    }
    
    public void combineFilter(final AnimationFilter animationFilter) {
        this.animateAlpha |= animationFilter.animateAlpha;
        this.animateX |= animationFilter.animateX;
        this.animateY |= animationFilter.animateY;
        this.animateYViews.addAll(animationFilter.animateYViews);
        this.animateZ |= animationFilter.animateZ;
        this.animateHeight |= animationFilter.animateHeight;
        this.animateTopInset |= animationFilter.animateTopInset;
        this.animateDimmed |= animationFilter.animateDimmed;
        this.animateHideSensitive |= animationFilter.animateHideSensitive;
        this.hasDelays |= animationFilter.hasDelays;
        this.mAnimatedProperties.addAll(animationFilter.mAnimatedProperties);
    }
    
    public AnimationFilter hasDelays() {
        this.hasDelays = true;
        return this;
    }
    
    public void reset() {
        this.animateAlpha = false;
        this.animateX = false;
        this.animateY = false;
        this.animateYViews.clear();
        this.animateZ = false;
        this.animateHeight = false;
        this.animateTopInset = false;
        this.animateDimmed = false;
        this.animateHideSensitive = false;
        this.hasDelays = false;
        this.hasGoToFullShadeEvent = false;
        this.customDelay = -1L;
        this.mAnimatedProperties.clear();
    }
    
    public boolean shouldAnimateProperty(final Property property) {
        return this.mAnimatedProperties.contains(property);
    }
    
    public boolean shouldAnimateY(final View view) {
        return this.animateY || this.animateYViews.contains(view);
    }
}
