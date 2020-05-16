// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.content.Context;

public class BubbleDebugConfig
{
    static boolean forceShowUserEducation(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getInt(contentResolver, "force_show_bubbles_user_education", 0) != 0) {
            b = true;
        }
        return b;
    }
}
