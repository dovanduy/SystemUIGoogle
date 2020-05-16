// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.app;

import android.app.AppOpsManager;
import android.os.Build$VERSION;
import android.content.Context;

public final class AppOpsManagerCompat
{
    public static int noteProxyOpNoThrow(final Context context, final String s, final String s2) {
        if (Build$VERSION.SDK_INT >= 23) {
            return ((AppOpsManager)context.getSystemService((Class)AppOpsManager.class)).noteProxyOpNoThrow(s, s2);
        }
        return 1;
    }
    
    public static String permissionToOp(final String s) {
        if (Build$VERSION.SDK_INT >= 23) {
            return AppOpsManager.permissionToOp(s);
        }
        return null;
    }
}
