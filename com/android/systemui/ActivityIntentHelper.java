// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.content.Context;

public class ActivityIntentHelper
{
    private final Context mContext;
    
    public ActivityIntentHelper(final Context mContext) {
        this.mContext = mContext;
    }
    
    public ActivityInfo getTargetActivityInfo(final Intent intent, final int n, final boolean b) {
        final PackageManager packageManager = this.mContext.getPackageManager();
        int n2;
        if (!b) {
            n2 = 851968;
        }
        else {
            n2 = 65536;
        }
        final List queryIntentActivitiesAsUser = packageManager.queryIntentActivitiesAsUser(intent, n2, n);
        if (queryIntentActivitiesAsUser.size() == 0) {
            return null;
        }
        final ResolveInfo resolveActivityAsUser = packageManager.resolveActivityAsUser(intent, n2 | 0x80, n);
        if (resolveActivityAsUser != null && !this.wouldLaunchResolverActivity(resolveActivityAsUser, queryIntentActivitiesAsUser)) {
            return resolveActivityAsUser.activityInfo;
        }
        return null;
    }
    
    public boolean wouldLaunchResolverActivity(final Intent intent, final int n) {
        boolean b = false;
        if (this.getTargetActivityInfo(intent, n, false) == null) {
            b = true;
        }
        return b;
    }
    
    public boolean wouldLaunchResolverActivity(final ResolveInfo resolveInfo, final List<ResolveInfo> list) {
        for (int i = 0; i < list.size(); ++i) {
            final ResolveInfo resolveInfo2 = list.get(i);
            if (resolveInfo2.activityInfo.name.equals(resolveInfo.activityInfo.name) && resolveInfo2.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean wouldShowOverLockscreen(final Intent intent, final int n) {
        final boolean b = false;
        final ActivityInfo targetActivityInfo = this.getTargetActivityInfo(intent, n, false);
        boolean b2 = b;
        if (targetActivityInfo != null) {
            b2 = b;
            if ((targetActivityInfo.flags & 0x800400) > 0) {
                b2 = true;
            }
        }
        return b2;
    }
}
