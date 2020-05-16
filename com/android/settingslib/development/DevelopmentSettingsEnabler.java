// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.development;

import android.provider.Settings$Global;
import android.os.Build;
import android.os.UserManager;
import android.content.Context;

public class DevelopmentSettingsEnabler
{
    public static boolean isDevelopmentSettingsEnabled(final Context context) {
        final UserManager userManager = (UserManager)context.getSystemService("user");
        final int int1 = Settings$Global.getInt(context.getContentResolver(), "development_settings_enabled", (int)(Build.TYPE.equals("eng") ? 1 : 0));
        boolean b = true;
        final boolean b2 = int1 != 0;
        final boolean hasUserRestriction = userManager.hasUserRestriction("no_debugging_features");
        if (!userManager.isAdminUser() || hasUserRestriction || !b2) {
            b = false;
        }
        return b;
    }
}
