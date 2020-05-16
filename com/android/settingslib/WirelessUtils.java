// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import android.content.ContentResolver;
import android.provider.Settings$Global;
import android.content.Context;

public class WirelessUtils
{
    public static boolean isAirplaneModeOn(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$Global.getInt(contentResolver, "airplane_mode_on", 0) != 0) {
            b = true;
        }
        return b;
    }
}
