// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.View;
import android.widget.TextView;
import androidx.leanback.R$id;
import android.widget.ImageView;
import android.view.ViewGroup;
import androidx.leanback.R$layout;
import android.view.LayoutInflater;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class TitleView extends FrameLayout
{
    public TitleView(final Context context) {
        this(context, null);
    }
    
    public TitleView(final Context context, final AttributeSet set) {
        this(context, set, R$attr.browseTitleViewStyle);
    }
    
    public TitleView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        final View inflate = LayoutInflater.from(context).inflate(R$layout.lb_title_view, (ViewGroup)this);
        final ImageView imageView = (ImageView)inflate.findViewById(R$id.title_badge);
        final TextView textView = (TextView)inflate.findViewById(R$id.title_text);
        final SearchOrbView searchOrbView = (SearchOrbView)inflate.findViewById(R$id.title_orb);
        this.setClipToPadding(false);
        this.setClipChildren(false);
    }
}
