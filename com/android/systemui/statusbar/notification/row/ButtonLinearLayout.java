// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.widget.Button;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class ButtonLinearLayout extends LinearLayout
{
    public ButtonLinearLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public CharSequence getAccessibilityClassName() {
        return Button.class.getName();
    }
}
