// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;

public class OneTimeEndListener implements OnAnimationEndListener
{
    @Override
    public void onAnimationEnd(final DynamicAnimation dynamicAnimation, final boolean b, final float n, final float n2) {
        dynamicAnimation.removeEndListener((DynamicAnimation.OnAnimationEndListener)this);
    }
}
