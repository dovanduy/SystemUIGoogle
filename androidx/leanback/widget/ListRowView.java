// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import androidx.leanback.R$id;
import android.view.ViewGroup;
import androidx.leanback.R$layout;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public final class ListRowView extends LinearLayout
{
    private HorizontalGridView mGridView;
    
    public ListRowView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ListRowView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        LayoutInflater.from(context).inflate(R$layout.lb_list_row, (ViewGroup)this);
        (this.mGridView = (HorizontalGridView)this.findViewById(R$id.row_content)).setHasFixedSize(false);
        this.setOrientation(1);
        this.setDescendantFocusability(262144);
    }
}
