// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.app;

import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.content.Context;
import android.content.ComponentName;
import android.os.Build$VERSION;
import android.content.Intent;
import android.app.Activity;

public final class NavUtils
{
    public static Intent getParentActivityIntent(final Activity activity) {
        if (Build$VERSION.SDK_INT >= 16) {
            final Intent parentActivityIntent = activity.getParentActivityIntent();
            if (parentActivityIntent != null) {
                return parentActivityIntent;
            }
        }
        final String parentActivityName = getParentActivityName(activity);
        if (parentActivityName == null) {
            return null;
        }
        final ComponentName component = new ComponentName((Context)activity, parentActivityName);
        try {
            Intent intent;
            if (getParentActivityName((Context)activity, component) == null) {
                intent = Intent.makeMainActivity(component);
            }
            else {
                intent = new Intent().setComponent(component);
            }
            return intent;
        }
        catch (PackageManager$NameNotFoundException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("getParentActivityIntent: bad parentActivityName '");
            sb.append(parentActivityName);
            sb.append("' in manifest");
            Log.e("NavUtils", sb.toString());
            return null;
        }
    }
    
    public static Intent getParentActivityIntent(final Context context, ComponentName component) throws PackageManager$NameNotFoundException {
        final String parentActivityName = getParentActivityName(context, component);
        if (parentActivityName == null) {
            return null;
        }
        component = new ComponentName(component.getPackageName(), parentActivityName);
        Intent intent;
        if (getParentActivityName(context, component) == null) {
            intent = Intent.makeMainActivity(component);
        }
        else {
            intent = new Intent().setComponent(component);
        }
        return intent;
    }
    
    public static String getParentActivityName(final Activity activity) {
        try {
            return getParentActivityName((Context)activity, activity.getComponentName());
        }
        catch (PackageManager$NameNotFoundException cause) {
            throw new IllegalArgumentException((Throwable)cause);
        }
    }
    
    public static String getParentActivityName(final Context context, final ComponentName componentName) throws PackageManager$NameNotFoundException {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final PackageManager packageManager = context.getPackageManager();
        int n = 640;
        if (sdk_INT >= 29) {
            n = 269222528;
        }
        else if (sdk_INT >= 24) {
            n = 787072;
        }
        final ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, n);
        if (sdk_INT >= 16) {
            final String parentActivityName = activityInfo.parentActivityName;
            if (parentActivityName != null) {
                return parentActivityName;
            }
        }
        final Bundle metaData = activityInfo.metaData;
        if (metaData == null) {
            return null;
        }
        final String string = metaData.getString("android.support.PARENT_ACTIVITY");
        if (string == null) {
            return null;
        }
        String string2 = string;
        if (string.charAt(0) == '.') {
            final StringBuilder sb = new StringBuilder();
            sb.append(context.getPackageName());
            sb.append(string);
            string2 = sb.toString();
        }
        return string2;
    }
    
    public static void navigateUpTo(final Activity activity, final Intent intent) {
        if (Build$VERSION.SDK_INT >= 16) {
            activity.navigateUpTo(intent);
        }
        else {
            intent.addFlags(67108864);
            activity.startActivity(intent);
            activity.finish();
        }
    }
    
    public static boolean shouldUpRecreateTask(final Activity activity, final Intent intent) {
        if (Build$VERSION.SDK_INT >= 16) {
            return activity.shouldUpRecreateTask(intent);
        }
        final String action = activity.getIntent().getAction();
        return action != null && !action.equals("android.intent.action.MAIN");
    }
}
