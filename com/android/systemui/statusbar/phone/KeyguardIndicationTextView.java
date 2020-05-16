// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;

public class KeyguardIndicationTextView extends TextView
{
    private CharSequence mText;
    
    public KeyguardIndicationTextView(final Context context) {
        super(context);
        this.mText = "";
    }
    
    public KeyguardIndicationTextView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mText = "";
    }
    
    public KeyguardIndicationTextView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mText = "";
    }
    
    public KeyguardIndicationTextView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mText = "";
    }
    
    public void switchIndication(final int n) {
        this.switchIndication(this.getResources().getText(n));
    }
    
    public void switchIndication(final CharSequence mText) {
        if (TextUtils.isEmpty(mText)) {
            this.mText = "";
            this.setVisibility(4);
        }
        else if (!TextUtils.equals(mText, this.mText)) {
            this.mText = mText;
            this.setVisibility(0);
            this.setText(this.mText);
        }
    }
}
