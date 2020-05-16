// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.content.ContentResolver;
import android.provider.Settings$Secure;
import com.android.systemui.R$id;
import com.android.internal.util.ContrastColorUtil;
import android.widget.ImageView;
import android.graphics.Color;
import android.view.View;
import android.content.Context;

public class NotificationUtils
{
    private static final int[] sLocationBase;
    private static final int[] sLocationOffset;
    private static Boolean sUseNewInterruptionModel;
    
    static {
        sLocationBase = new int[2];
        sLocationOffset = new int[2];
    }
    
    public static int getFontScaledHeight(final Context context, int dimensionPixelSize) {
        dimensionPixelSize = context.getResources().getDimensionPixelSize(dimensionPixelSize);
        return (int)(dimensionPixelSize * Math.max(1.0f, context.getResources().getDisplayMetrics().scaledDensity / context.getResources().getDisplayMetrics().density));
    }
    
    public static float getRelativeYOffset(final View view, final View view2) {
        final int[] sLocationOffset = NotificationUtils.sLocationOffset;
        final int[] sLocationBase = NotificationUtils.sLocationBase;
        view2.getLocationOnScreen(sLocationBase);
        view.getLocationOnScreen(sLocationOffset);
        return (float)(sLocationOffset[1] - sLocationBase[1]);
    }
    
    public static float interpolate(final float n, final float n2, final float n3) {
        return n * (1.0f - n3) + n2 * n3;
    }
    
    public static int interpolateColors(final int n, final int n2, final float n3) {
        return Color.argb((int)interpolate((float)Color.alpha(n), (float)Color.alpha(n2), n3), (int)interpolate((float)Color.red(n), (float)Color.red(n2), n3), (int)interpolate((float)Color.green(n), (float)Color.green(n2), n3), (int)interpolate((float)Color.blue(n), (float)Color.blue(n2), n3));
    }
    
    public static boolean isGrayscale(final ImageView imageView, final ContrastColorUtil contrastColorUtil) {
        final Object tag = imageView.getTag(R$id.icon_is_grayscale);
        if (tag != null) {
            return Boolean.TRUE.equals(tag);
        }
        final boolean grayscaleIcon = contrastColorUtil.isGrayscaleIcon(imageView.getDrawable());
        imageView.setTag(R$id.icon_is_grayscale, (Object)grayscaleIcon);
        return grayscaleIcon;
    }
    
    public static boolean useNewInterruptionModel(final Context context) {
        if (NotificationUtils.sUseNewInterruptionModel == null) {
            final ContentResolver contentResolver = context.getContentResolver();
            boolean b = true;
            if (Settings$Secure.getInt(contentResolver, "new_interruption_model", 1) == 0) {
                b = false;
            }
            NotificationUtils.sUseNewInterruptionModel = b;
        }
        return NotificationUtils.sUseNewInterruptionModel;
    }
}
