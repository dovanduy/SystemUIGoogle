// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.applications;

import android.os.SystemProperties;
import android.content.pm.ApplicationInfo;
import com.android.settingslib.applications.instantapps.InstantAppDataProvider;

public class AppUtils
{
    private static InstantAppDataProvider sInstantAppDataProvider;
    
    public static boolean isInstant(final ApplicationInfo applicationInfo) {
        final InstantAppDataProvider sInstantAppDataProvider = AppUtils.sInstantAppDataProvider;
        if (sInstantAppDataProvider != null) {
            if (sInstantAppDataProvider.isInstantApp(applicationInfo)) {
                return true;
            }
        }
        else if (applicationInfo.isInstantApp()) {
            return true;
        }
        final String value = SystemProperties.get("settingsdebug.instant.packages");
        if (value != null && !value.isEmpty() && applicationInfo.packageName != null) {
            final String[] split = value.split(",");
            if (split != null) {
                for (int length = split.length, i = 0; i < length; ++i) {
                    if (applicationInfo.packageName.contains(split[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
