// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.R$id;
import android.graphics.Paint;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.view.View;

public class CrossFadeHelper
{
    public static void fadeIn(final View view) {
        fadeIn(view, 210L, 0);
    }
    
    public static void fadeIn(final View view, final float n) {
        fadeIn(view, n, true);
    }
    
    public static void fadeIn(final View view, float interpolation, final boolean b) {
        view.animate().cancel();
        if (view.getVisibility() == 4) {
            view.setVisibility(0);
        }
        float mapToFadeDuration = interpolation;
        if (b) {
            mapToFadeDuration = mapToFadeDuration(interpolation);
        }
        interpolation = Interpolators.ALPHA_IN.getInterpolation(mapToFadeDuration);
        view.setAlpha(interpolation);
        updateLayerType(view, interpolation);
    }
    
    public static void fadeIn(final View view, final long duration, final int n) {
        view.animate().cancel();
        if (view.getVisibility() == 4) {
            view.setAlpha(0.0f);
            view.setVisibility(0);
        }
        view.animate().alpha(1.0f).setDuration(duration).setStartDelay((long)n).setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN).withEndAction((Runnable)null);
        if (view.hasOverlappingRendering() && view.getLayerType() != 2) {
            view.animate().withLayer();
        }
    }
    
    public static void fadeOut(final View view) {
        fadeOut(view, null);
    }
    
    public static void fadeOut(final View view, final float n) {
        fadeOut(view, n, true);
    }
    
    public static void fadeOut(final View view, float interpolation, final boolean b) {
        view.animate().cancel();
        if (interpolation == 1.0f && view.getVisibility() != 8) {
            view.setVisibility(4);
        }
        else if (view.getVisibility() == 4) {
            view.setVisibility(0);
        }
        float mapToFadeDuration = interpolation;
        if (b) {
            mapToFadeDuration = mapToFadeDuration(interpolation);
        }
        interpolation = Interpolators.ALPHA_OUT.getInterpolation(1.0f - mapToFadeDuration);
        view.setAlpha(interpolation);
        updateLayerType(view, interpolation);
    }
    
    public static void fadeOut(final View view, final long duration, final int n, final Runnable runnable) {
        view.animate().cancel();
        view.animate().alpha(0.0f).setDuration(duration).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT).setStartDelay((long)n).withEndAction((Runnable)new Runnable() {
            @Override
            public void run() {
                final Runnable val$endRunnable = runnable;
                if (val$endRunnable != null) {
                    val$endRunnable.run();
                }
                if (view.getVisibility() != 8) {
                    view.setVisibility(4);
                }
            }
        });
        if (view.hasOverlappingRendering()) {
            view.animate().withLayer();
        }
    }
    
    public static void fadeOut(final View view, final Runnable runnable) {
        fadeOut(view, 210L, 0, runnable);
    }
    
    private static float mapToFadeDuration(final float n) {
        return Math.min(n / 0.5833333f, 1.0f);
    }
    
    private static void updateLayerType(final View view, final float n) {
        if (view.hasOverlappingRendering() && n > 0.0f && n < 1.0f) {
            if (view.getLayerType() != 2) {
                view.setLayerType(2, (Paint)null);
                view.setTag(R$id.cross_fade_layer_type_changed_tag, (Object)Boolean.TRUE);
            }
        }
        else if (view.getLayerType() == 2 && view.getTag(R$id.cross_fade_layer_type_changed_tag) != null && view.getTag(R$id.cross_fade_layer_type_changed_tag) != null) {
            view.setLayerType(0, (Paint)null);
        }
    }
}
