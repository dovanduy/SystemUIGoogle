// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.app;

import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import java.lang.reflect.Method;
import android.os.Build$VERSION;
import android.os.IBinder;
import android.os.Bundle;

public final class BundleCompat
{
    public static IBinder getBinder(final Bundle bundle, final String s) {
        if (Build$VERSION.SDK_INT >= 18) {
            return bundle.getBinder(s);
        }
        return BundleCompatBaseImpl.getBinder(bundle, s);
    }
    
    static class BundleCompatBaseImpl
    {
        private static Method sGetIBinderMethod;
        private static boolean sGetIBinderMethodFetched;
        
        public static IBinder getBinder(final Bundle obj, final String s) {
            if (!BundleCompatBaseImpl.sGetIBinderMethodFetched) {
                try {
                    (BundleCompatBaseImpl.sGetIBinderMethod = Bundle.class.getMethod("getIBinder", String.class)).setAccessible(true);
                }
                catch (NoSuchMethodException ex) {
                    Log.i("BundleCompatBaseImpl", "Failed to retrieve getIBinder method", (Throwable)ex);
                }
                BundleCompatBaseImpl.sGetIBinderMethodFetched = true;
            }
            final Method sGetIBinderMethod = BundleCompatBaseImpl.sGetIBinderMethod;
            if (sGetIBinderMethod != null) {
                try {
                    return (IBinder)sGetIBinderMethod.invoke(obj, s);
                }
                catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ex2) {
                    final Throwable t;
                    Log.i("BundleCompatBaseImpl", "Failed to invoke getIBinder via reflection", t);
                    BundleCompatBaseImpl.sGetIBinderMethod = null;
                }
            }
            return null;
        }
    }
}
