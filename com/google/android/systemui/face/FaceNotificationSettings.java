// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.face;

import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.content.Context;

class FaceNotificationSettings
{
    static boolean isReenrollRequired(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getIntForUser(contentResolver, "face_unlock_re_enroll", 0, -2) == 3) {
            b = true;
        }
        return b;
    }
    
    static void updateReenrollSetting(final Context context, final int n) {
        Settings$Secure.putIntForUser(context.getContentResolver(), "face_unlock_re_enroll", n, -2);
    }
}
