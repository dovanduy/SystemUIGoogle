// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

import androidx.lifecycle.runtime.R$id;
import android.view.View;

public class ViewTreeLifecycleOwner
{
    public static void set(final View view, final LifecycleOwner lifecycleOwner) {
        view.setTag(R$id.view_tree_lifecycle_owner, (Object)lifecycleOwner);
    }
}
