// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.widget.TextView;
import android.view.View;

public class FontSizeUtils
{
    public static void updateFontSize(final View view, final int n, final int n2) {
        updateFontSize((TextView)view.findViewById(n), n2);
    }
    
    public static void updateFontSize(final TextView textView, final int n) {
        if (textView != null) {
            textView.setTextSize(0, (float)textView.getResources().getDimensionPixelSize(n));
        }
    }
}
