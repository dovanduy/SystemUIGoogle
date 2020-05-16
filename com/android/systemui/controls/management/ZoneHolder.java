// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import android.view.View;
import android.widget.TextView;

final class ZoneHolder extends Holder
{
    private final TextView zone;
    
    public ZoneHolder(View itemView) {
        Intrinsics.checkParameterIsNotNull(itemView, "view");
        super(itemView, null);
        itemView = super.itemView;
        if (itemView != null) {
            this.zone = (TextView)itemView;
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.widget.TextView");
    }
    
    @Override
    public void bindData(final ElementWrapper elementWrapper) {
        Intrinsics.checkParameterIsNotNull(elementWrapper, "wrapper");
        this.zone.setText(((ZoneNameWrapper)elementWrapper).getZoneName());
    }
}
