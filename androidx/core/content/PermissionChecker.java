// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.content;

import android.os.Process;
import androidx.core.app.AppOpsManagerCompat;
import android.content.Context;

public final class PermissionChecker
{
    public static int checkPermission(final Context context, String s, final int n, final int n2, final String s2) {
        if (context.checkPermission(s, n, n2) == -1) {
            return -1;
        }
        final String permissionToOp = AppOpsManagerCompat.permissionToOp(s);
        if (permissionToOp == null) {
            return 0;
        }
        if ((s = s2) == null) {
            final String[] packagesForUid = context.getPackageManager().getPackagesForUid(n2);
            if (packagesForUid == null || packagesForUid.length <= 0) {
                return -1;
            }
            s = packagesForUid[0];
        }
        if (AppOpsManagerCompat.noteProxyOpNoThrow(context, permissionToOp, s) != 0) {
            return -2;
        }
        return 0;
    }
    
    public static int checkSelfPermission(final Context context, final String s) {
        return checkPermission(context, s, Process.myPid(), Process.myUid(), context.getPackageName());
    }
}
