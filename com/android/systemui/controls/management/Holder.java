// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Holder extends ViewHolder
{
    private Holder(final View view) {
        super(view);
    }
    
    public abstract void bindData(final ElementWrapper p0);
}
