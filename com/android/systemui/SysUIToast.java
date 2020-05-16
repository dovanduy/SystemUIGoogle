// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.widget.Toast;
import android.content.Context;

public class SysUIToast
{
    public static Toast makeText(final Context context, final int n, final int n2) {
        return makeText(context, context.getString(n), n2);
    }
    
    public static Toast makeText(final Context context, final CharSequence charSequence, final int n) {
        return Toast.makeText(context, charSequence, n);
    }
}
