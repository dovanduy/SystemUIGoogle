// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.graphics.Path;
import android.view.animation.PathInterpolator;

public class HeadsUpAppearInterpolator extends PathInterpolator
{
    private static float X1 = 250.0f;
    private static float X2 = 200.0f;
    private static float XTOT;
    
    static {
        HeadsUpAppearInterpolator.XTOT = 250.0f + 200.0f;
    }
    
    public HeadsUpAppearInterpolator() {
        super(getAppearPath());
    }
    
    private static Path getAppearPath() {
        final float xtot = HeadsUpAppearInterpolator.XTOT;
        final Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        final float x1 = HeadsUpAppearInterpolator.X1;
        path.cubicTo(x1 * 0.8f / xtot, 1.125f, 0.8f * x1 / xtot, 1.125f, x1 / xtot, 1.125f);
        final float x2 = HeadsUpAppearInterpolator.X1;
        final float x3 = HeadsUpAppearInterpolator.X2;
        path.cubicTo((0.4f * x3 + x2) / xtot, 1.125f, (x2 + x3 * 0.2f) / xtot, 1.0f, 1.0f, 1.0f);
        return path;
    }
    
    public static float getFractionUntilOvershoot() {
        return HeadsUpAppearInterpolator.X1 / HeadsUpAppearInterpolator.XTOT;
    }
}
