// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.app;

import android.content.Intent;
import android.os.Build$VERSION;
import android.app.Activity;
import androidx.core.content.ContextCompat;

public class ActivityCompat extends ContextCompat
{
    private static PermissionCompatDelegate sDelegate;
    
    public static void finishAffinity(final Activity activity) {
        if (Build$VERSION.SDK_INT >= 16) {
            activity.finishAffinity();
        }
        else {
            activity.finish();
        }
    }
    
    public static PermissionCompatDelegate getPermissionCompatDelegate() {
        return ActivityCompat.sDelegate;
    }
    
    public static void recreate(final Activity activity) {
        if (Build$VERSION.SDK_INT >= 28) {
            activity.recreate();
        }
        else if (!ActivityRecreator.recreate(activity)) {
            activity.recreate();
        }
    }
    
    public interface PermissionCompatDelegate
    {
        boolean onActivityResult(final Activity p0, final int p1, final int p2, final Intent p3);
    }
}
