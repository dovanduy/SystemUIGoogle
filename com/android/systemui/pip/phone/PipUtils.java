// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.app.ActivityManager$StackInfo;
import android.os.RemoteException;
import android.util.Log;
import android.app.ActivityTaskManager;
import android.content.ComponentName;
import android.util.Pair;
import android.app.IActivityManager;
import android.content.Context;

public class PipUtils
{
    public static Pair<ComponentName, Integer> getTopPipActivity(final Context context, final IActivityManager activityManager) {
        try {
            final String packageName = context.getPackageName();
            final ActivityManager$StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
            if (stackInfo != null && stackInfo.taskIds != null && stackInfo.taskIds.length > 0) {
                for (int i = stackInfo.taskNames.length - 1; i >= 0; --i) {
                    final ComponentName unflattenFromString = ComponentName.unflattenFromString(stackInfo.taskNames[i]);
                    if (unflattenFromString != null && !unflattenFromString.getPackageName().equals(packageName)) {
                        return (Pair<ComponentName, Integer>)new Pair((Object)unflattenFromString, (Object)stackInfo.taskUserIds[i]);
                    }
                }
                return (Pair<ComponentName, Integer>)new Pair((Object)null, (Object)0);
            }
        }
        catch (RemoteException ex) {
            Log.w("PipUtils", "Unable to get pinned stack.");
        }
        return (Pair<ComponentName, Integer>)new Pair((Object)null, (Object)0);
    }
}
