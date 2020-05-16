// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.widget;

import android.os.Build$VERSION;
import android.widget.EdgeEffect;

public final class EdgeEffectCompat
{
    public static void onPull(final EdgeEffect edgeEffect, final float n, final float n2) {
        if (Build$VERSION.SDK_INT >= 21) {
            edgeEffect.onPull(n, n2);
        }
        else {
            edgeEffect.onPull(n);
        }
    }
}
