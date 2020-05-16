// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.widget.TextView;
import com.android.systemui.R$id;
import android.widget.ImageView;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.RelativeLayout;

public class KeyboardShortcutAppItemLayout extends RelativeLayout
{
    public KeyboardShortcutAppItemLayout(final Context context) {
        super(context);
    }
    
    public KeyboardShortcutAppItemLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    protected void onMeasure(final int n, final int n2) {
        if (View$MeasureSpec.getMode(n) == 1073741824) {
            final ImageView imageView = (ImageView)this.findViewById(R$id.keyboard_shortcuts_icon);
            final TextView textView = (TextView)this.findViewById(R$id.keyboard_shortcuts_keyword);
            int n3 = View$MeasureSpec.getSize(n) - (this.getPaddingLeft() + this.getPaddingRight());
            if (imageView.getVisibility() == 0) {
                n3 -= imageView.getMeasuredWidth();
            }
            textView.setMaxWidth((int)Math.round(n3 * 0.7));
        }
        super.onMeasure(n, n2);
    }
}
