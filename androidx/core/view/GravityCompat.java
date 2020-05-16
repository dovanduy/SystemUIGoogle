// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.view.Gravity;
import android.os.Build$VERSION;
import android.graphics.Rect;

public final class GravityCompat
{
    public static void apply(final int n, final int n2, final int n3, final Rect rect, final Rect rect2, final int n4) {
        if (Build$VERSION.SDK_INT >= 17) {
            Gravity.apply(n, n2, n3, rect, rect2, n4);
        }
        else {
            Gravity.apply(n, n2, n3, rect, rect2);
        }
    }
    
    public static int getAbsoluteGravity(final int n, final int n2) {
        if (Build$VERSION.SDK_INT >= 17) {
            return Gravity.getAbsoluteGravity(n, n2);
        }
        return n & 0xFF7FFFFF;
    }
}
