// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.view.View;

final class ForegroundHelper
{
    static void setForeground(final View view, final Drawable foreground) {
        if (Build$VERSION.SDK_INT >= 23) {
            view.setForeground(foreground);
        }
    }
}
