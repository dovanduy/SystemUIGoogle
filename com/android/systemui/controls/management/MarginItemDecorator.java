// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import kotlin.jvm.internal.Intrinsics;
import android.view.View;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;

public final class MarginItemDecorator extends ItemDecoration
{
    private final int sideMargins;
    private final int topMargin;
    
    public MarginItemDecorator(final int topMargin, final int sideMargins) {
        this.topMargin = topMargin;
        this.sideMargins = sideMargins;
    }
    
    @Override
    public void getItemOffsets(final Rect rect, final View view, final RecyclerView recyclerView, final State state) {
        Intrinsics.checkParameterIsNotNull(rect, "outRect");
        Intrinsics.checkParameterIsNotNull(view, "view");
        Intrinsics.checkParameterIsNotNull(recyclerView, "parent");
        Intrinsics.checkParameterIsNotNull(state, "state");
        rect.top = this.topMargin;
        final int sideMargins = this.sideMargins;
        rect.left = sideMargins;
        rect.right = sideMargins;
    }
}
