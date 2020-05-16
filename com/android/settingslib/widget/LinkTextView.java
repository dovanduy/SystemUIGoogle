// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.Spanned;
import android.widget.TextView$BufferType;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;

public class LinkTextView extends TextView
{
    public LinkTextView(final Context context) {
        this(context, null);
    }
    
    public LinkTextView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public void setText(final CharSequence charSequence, final TextView$BufferType textView$BufferType) {
        super.setText(charSequence, textView$BufferType);
        if (charSequence instanceof Spanned && ((ClickableSpan[])((Spanned)charSequence).getSpans(0, charSequence.length(), (Class)ClickableSpan.class)).length > 0) {
            this.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
