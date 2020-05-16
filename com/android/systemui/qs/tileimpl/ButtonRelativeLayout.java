// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tileimpl;

import android.widget.Button;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.RelativeLayout;

public class ButtonRelativeLayout extends RelativeLayout
{
    public ButtonRelativeLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public CharSequence getAccessibilityClassName() {
        return Button.class.getName();
    }
}
