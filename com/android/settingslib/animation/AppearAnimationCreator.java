// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.animation;

import android.view.animation.Interpolator;

public interface AppearAnimationCreator<T>
{
    void createAnimation(final T p0, final long p1, final long p2, final float p3, final boolean p4, final Interpolator p5, final Runnable p6);
}
