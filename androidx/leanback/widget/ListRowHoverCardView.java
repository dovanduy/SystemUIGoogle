// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import androidx.leanback.R$id;
import android.widget.TextView;
import android.view.ViewGroup;
import androidx.leanback.R$layout;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public final class ListRowHoverCardView extends LinearLayout
{
    public ListRowHoverCardView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ListRowHoverCardView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        LayoutInflater.from(context).inflate(R$layout.lb_list_row_hovercard, (ViewGroup)this);
        final TextView textView = (TextView)this.findViewById(R$id.title);
        final TextView textView2 = (TextView)this.findViewById(R$id.description);
    }
}
