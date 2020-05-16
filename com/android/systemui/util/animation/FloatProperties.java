// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.animation;

import android.graphics.Rect;
import androidx.dynamicanimation.animation.FloatPropertyCompat;

public final class FloatProperties
{
    public static final FloatPropertyCompat<Rect> RECT_X;
    public static final FloatPropertyCompat<Rect> RECT_Y;
    
    static {
        RECT_X = (FloatPropertyCompat)new FloatProperties$Companion$RECT_X.FloatProperties$Companion$RECT_X$1("RectX");
        RECT_Y = (FloatPropertyCompat)new FloatProperties$Companion$RECT_Y.FloatProperties$Companion$RECT_Y$1("RectY");
    }
}
