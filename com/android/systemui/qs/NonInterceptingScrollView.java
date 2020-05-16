// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.MotionEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ScrollView;

public class NonInterceptingScrollView extends ScrollView
{
    public NonInterceptingScrollView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            if (this.canScrollVertically(1)) {
                this.requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.onTouchEvent(motionEvent);
    }
}
