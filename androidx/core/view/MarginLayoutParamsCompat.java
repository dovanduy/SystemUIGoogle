// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.os.Build$VERSION;
import android.view.ViewGroup$MarginLayoutParams;

public final class MarginLayoutParamsCompat
{
    public static int getMarginEnd(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
        if (Build$VERSION.SDK_INT >= 17) {
            return viewGroup$MarginLayoutParams.getMarginEnd();
        }
        return viewGroup$MarginLayoutParams.rightMargin;
    }
    
    public static int getMarginStart(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
        if (Build$VERSION.SDK_INT >= 17) {
            return viewGroup$MarginLayoutParams.getMarginStart();
        }
        return viewGroup$MarginLayoutParams.leftMargin;
    }
}
