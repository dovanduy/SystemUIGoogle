// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import android.content.res.Configuration;
import android.content.Context;

public class RotationUtils
{
    public static int getExactRotation(final Context context) {
        final Configuration configuration = context.getResources().getConfiguration();
        final int rotation = context.getDisplay().getRotation();
        if (configuration.smallestScreenWidthDp < 600) {
            if (rotation == 1) {
                return 1;
            }
            if (rotation == 3) {
                return 2;
            }
            if (rotation == 2) {
                return 3;
            }
        }
        return 0;
    }
    
    public static int getRotation(final Context context) {
        final Configuration configuration = context.getResources().getConfiguration();
        final int rotation = context.getDisplay().getRotation();
        if (configuration.smallestScreenWidthDp < 600) {
            if (rotation == 1) {
                return 1;
            }
            if (rotation == 3) {
                return 2;
            }
        }
        return 0;
    }
}
