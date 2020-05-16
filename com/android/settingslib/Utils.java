// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import android.location.LocationManager;
import android.provider.Settings$Secure;
import android.telephony.NetworkRegistrationInfo;
import android.content.res.Resources;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.telephony.ServiceState;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import com.android.launcher3.icons.IconFactory;
import android.os.UserHandle;
import android.graphics.drawable.Drawable;
import android.content.pm.ApplicationInfo;
import java.text.NumberFormat;
import android.content.res.TypedArray;
import android.content.Context;
import android.graphics.Color;
import android.content.pm.Signature;
import com.android.internal.annotations.VisibleForTesting;

public class Utils
{
    @VisibleForTesting
    static final String STORAGE_MANAGER_ENABLED_PROPERTY = "ro.storage_manager.enabled";
    private static String sPermissionControllerPackageName;
    private static String sServicesSystemSharedLibPackageName;
    private static String sSharedSystemSharedLibPackageName;
    private static Signature[] sSystemSignature;
    
    public static int applyAlpha(final float n, final int n2) {
        return Color.argb((int)(n * Color.alpha(n2)), Color.red(n2), Color.green(n2), Color.blue(n2));
    }
    
    public static int applyAlphaAttr(final Context context, final int n, final int n2) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[] { n });
        final float float1 = obtainStyledAttributes.getFloat(0, 0.0f);
        obtainStyledAttributes.recycle();
        return applyAlpha(float1, n2);
    }
    
    public static String formatPercentage(final double number) {
        return NumberFormat.getPercentInstance().format(number);
    }
    
    public static String formatPercentage(final int n) {
        return formatPercentage(n / 100.0);
    }
    
    public static String formatPercentage(final long n, final long n2) {
        return formatPercentage(n / (double)n2);
    }
    
    public static Drawable getBadgedIcon(final Context context, final ApplicationInfo applicationInfo) {
        final UserHandle userHandleForUid = UserHandle.getUserHandleForUid(applicationInfo.uid);
        final IconFactory obtain = IconFactory.obtain(context);
        try {
            final BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), obtain.createBadgedIconBitmap(applicationInfo.loadUnbadgedIcon(context.getPackageManager()), userHandleForUid, false).icon);
            if (obtain != null) {
                obtain.close();
            }
            return (Drawable)bitmapDrawable;
        }
        finally {
            if (obtain != null) {
                try {
                    obtain.close();
                }
                finally {
                    final Throwable exception;
                    ((Throwable)context).addSuppressed(exception);
                }
            }
        }
    }
    
    public static ColorStateList getColorAccent(final Context context) {
        return getColorAttr(context, 16843829);
    }
    
    public static int getColorAccentDefaultColor(final Context context) {
        return getColorAttrDefaultColor(context, 16843829);
    }
    
    public static ColorStateList getColorAttr(Context obtainStyledAttributes, final int n) {
        obtainStyledAttributes = (Context)obtainStyledAttributes.obtainStyledAttributes(new int[] { n });
        try {
            return ((TypedArray)obtainStyledAttributes).getColorStateList(0);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    public static int getColorAttrDefaultColor(final Context context, int color) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[] { color });
        color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        return color;
    }
    
    public static ColorStateList getColorError(final Context context) {
        return getColorAttr(context, 16844099);
    }
    
    public static int getColorErrorDefaultColor(final Context context) {
        return getColorAttrDefaultColor(context, 16844099);
    }
    
    public static int getColorStateListDefaultColor(final Context context, final int n) {
        return context.getResources().getColorStateList(n, context.getTheme()).getDefaultColor();
    }
    
    public static int getCombinedServiceState(final ServiceState serviceState) {
        if (serviceState == null) {
            return 1;
        }
        final int state = serviceState.getState();
        final int dataRegistrationState = serviceState.getDataRegistrationState();
        if ((state == 1 || state == 2) && dataRegistrationState == 0 && isNotInIwlan(serviceState)) {
            return 0;
        }
        return state;
    }
    
    public static int getDisabled(final Context context, final int n) {
        return applyAlphaAttr(context, 16842803, n);
    }
    
    public static Drawable getDrawable(final Context context, final int n) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[] { n });
        final Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }
    
    private static Signature getFirstSignature(final PackageInfo packageInfo) {
        if (packageInfo != null) {
            final Signature[] signatures = packageInfo.signatures;
            if (signatures != null && signatures.length > 0) {
                return signatures[0];
            }
        }
        return null;
    }
    
    private static Signature getSystemSignature(final PackageManager packageManager) {
        try {
            return getFirstSignature(packageManager.getPackageInfo("android", 64));
        }
        catch (PackageManager$NameNotFoundException ex) {
            return null;
        }
    }
    
    public static int getThemeAttr(final Context context, int resourceId) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[] { resourceId });
        resourceId = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
        return resourceId;
    }
    
    public static boolean isDeviceProvisioningPackage(final Resources resources, final String anObject) {
        final String string = resources.getString(17039885);
        return string != null && string.equals(anObject);
    }
    
    public static boolean isInService(final ServiceState serviceState) {
        if (serviceState == null) {
            return false;
        }
        final int combinedServiceState = getCombinedServiceState(serviceState);
        return combinedServiceState != 3 && combinedServiceState != 1 && combinedServiceState != 2;
    }
    
    private static boolean isNotInIwlan(final ServiceState serviceState) {
        final NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 2);
        return networkRegistrationInfo == null || ((networkRegistrationInfo.getRegistrationState() == 1 || networkRegistrationInfo.getRegistrationState() == 5) ^ true);
    }
    
    public static boolean isSystemPackage(final Resources resources, final PackageManager packageManager, final PackageInfo packageInfo) {
        final Signature[] sSystemSignature = Utils.sSystemSignature;
        final boolean b = true;
        if (sSystemSignature == null) {
            Utils.sSystemSignature = new Signature[] { getSystemSignature(packageManager) };
        }
        if (Utils.sPermissionControllerPackageName == null) {
            Utils.sPermissionControllerPackageName = packageManager.getPermissionControllerPackageName();
        }
        if (Utils.sServicesSystemSharedLibPackageName == null) {
            Utils.sServicesSystemSharedLibPackageName = packageManager.getServicesSystemSharedLibraryPackageName();
        }
        if (Utils.sSharedSystemSharedLibPackageName == null) {
            Utils.sSharedSystemSharedLibPackageName = packageManager.getSharedSystemSharedLibraryPackageName();
        }
        final Signature[] sSystemSignature2 = Utils.sSystemSignature;
        if (sSystemSignature2[0] != null) {
            final boolean b2 = b;
            if (sSystemSignature2[0].equals((Object)getFirstSignature(packageInfo))) {
                return b2;
            }
        }
        boolean b2 = b;
        if (!packageInfo.packageName.equals(Utils.sPermissionControllerPackageName)) {
            b2 = b;
            if (!packageInfo.packageName.equals(Utils.sServicesSystemSharedLibPackageName)) {
                b2 = b;
                if (!packageInfo.packageName.equals(Utils.sSharedSystemSharedLibPackageName)) {
                    b2 = b;
                    if (!packageInfo.packageName.equals("com.android.printspooler")) {
                        b2 = (isDeviceProvisioningPackage(resources, packageInfo.packageName) && b);
                    }
                }
            }
        }
        return b2;
    }
    
    public static void updateLocationEnabled(final Context context, final boolean b, final int n, final int n2) {
        Settings$Secure.putIntForUser(context.getContentResolver(), "location_changer", n2, n);
        ((LocationManager)context.getSystemService((Class)LocationManager.class)).setLocationEnabledForUser(b, UserHandle.of(n));
    }
}
