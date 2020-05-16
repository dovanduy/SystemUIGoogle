// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.view.View;
import androidx.core.R$id;
import android.os.Build$VERSION;
import android.view.ViewGroup;

public final class ViewGroupCompat
{
    public static boolean isTransitionGroup(final ViewGroup viewGroup) {
        if (Build$VERSION.SDK_INT >= 21) {
            return viewGroup.isTransitionGroup();
        }
        final Boolean b = (Boolean)viewGroup.getTag(R$id.tag_transition_group);
        return (b != null && b) || viewGroup.getBackground() != null || ViewCompat.getTransitionName((View)viewGroup) != null;
    }
}
