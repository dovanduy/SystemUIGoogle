// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.MotionEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class AssistUIView extends FrameLayout
{
    private TouchOutsideHandler mTouchOutside;
    
    public AssistUIView(final Context context) {
        this(context, null);
    }
    
    public AssistUIView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public AssistUIView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public AssistUIView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.setClipChildren(false);
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4) {
            final TouchOutsideHandler mTouchOutside = this.mTouchOutside;
            if (mTouchOutside != null) {
                mTouchOutside.onTouchOutside();
                return false;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
    
    void setTouchOutside(final TouchOutsideHandler mTouchOutside) {
        this.mTouchOutside = mTouchOutside;
    }
}
