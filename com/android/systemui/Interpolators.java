// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.view.animation.BounceInterpolator;
import com.android.systemui.statusbar.notification.stack.HeadsUpAppearInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.Interpolator;

public class Interpolators
{
    public static final Interpolator ACCELERATE;
    public static final Interpolator ACCELERATE_DECELERATE;
    public static final Interpolator ALPHA_IN;
    public static final Interpolator ALPHA_OUT;
    public static final Interpolator BOUNCE;
    public static final Interpolator CUSTOM_40_40;
    public static final Interpolator DECELERATE_QUINT;
    public static final Interpolator FAST_OUT_LINEAR_IN;
    public static final Interpolator FAST_OUT_SLOW_IN;
    public static final Interpolator FAST_OUT_SLOW_IN_REVERSE;
    public static final Interpolator HEADS_UP_APPEAR;
    public static final Interpolator ICON_OVERSHOT;
    public static final Interpolator ICON_OVERSHOT_LESS;
    public static final Interpolator LINEAR;
    public static final Interpolator LINEAR_OUT_SLOW_IN;
    public static final Interpolator PANEL_CLOSE_ACCELERATED;
    public static final Interpolator SHADE_ANIMATION;
    public static final Interpolator TOUCH_RESPONSE;
    public static final Interpolator TOUCH_RESPONSE_REVERSE;
    
    static {
        FAST_OUT_SLOW_IN = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
        FAST_OUT_SLOW_IN_REVERSE = (Interpolator)new PathInterpolator(0.8f, 0.0f, 0.6f, 1.0f);
        FAST_OUT_LINEAR_IN = (Interpolator)new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
        LINEAR_OUT_SLOW_IN = (Interpolator)new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
        ALPHA_IN = (Interpolator)new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
        ALPHA_OUT = (Interpolator)new PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f);
        LINEAR = (Interpolator)new LinearInterpolator();
        ACCELERATE = (Interpolator)new AccelerateInterpolator();
        ACCELERATE_DECELERATE = (Interpolator)new AccelerateDecelerateInterpolator();
        DECELERATE_QUINT = (Interpolator)new DecelerateInterpolator(2.5f);
        CUSTOM_40_40 = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
        HEADS_UP_APPEAR = (Interpolator)new HeadsUpAppearInterpolator();
        ICON_OVERSHOT = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.2f, 1.4f);
        SHADE_ANIMATION = (Interpolator)new PathInterpolator(0.6f, 0.02f, 0.4f, 0.98f);
        ICON_OVERSHOT_LESS = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.2f, 1.1f);
        PANEL_CLOSE_ACCELERATED = (Interpolator)new PathInterpolator(0.3f, 0.0f, 0.5f, 1.0f);
        BOUNCE = (Interpolator)new BounceInterpolator();
        TOUCH_RESPONSE = (Interpolator)new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
        TOUCH_RESPONSE_REVERSE = (Interpolator)new PathInterpolator(0.9f, 0.0f, 0.7f, 1.0f);
    }
}
