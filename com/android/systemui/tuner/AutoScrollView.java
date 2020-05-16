// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.view.DragEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ScrollView;

public class AutoScrollView extends ScrollView
{
    public AutoScrollView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public boolean onDragEvent(final DragEvent dragEvent) {
        if (dragEvent.getAction() == 2) {
            final int n = (int)dragEvent.getY();
            final int height = this.getHeight();
            final int n2 = (int)(height * 0.1f);
            if (n < n2) {
                this.scrollBy(0, n - n2);
            }
            else if (n > height - n2) {
                this.scrollBy(0, n - height + n2);
            }
        }
        return false;
    }
}
