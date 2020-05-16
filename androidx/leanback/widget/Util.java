// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.ViewParent;
import android.view.View;
import android.view.ViewGroup;

public class Util
{
    public static boolean isDescendant(final ViewGroup viewGroup, View view) {
        while (view != null) {
            if (view == viewGroup) {
                return true;
            }
            final ViewParent parent = view.getParent();
            if (!(parent instanceof View)) {
                return false;
            }
            view = (View)parent;
        }
        return false;
    }
}
