// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.applications;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.IconDrawableFactory;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.app.AppGlobals;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.ComponentName;
import com.android.settingslib.widget.CandidateInfo;

public class DefaultAppInfo extends CandidateInfo
{
    public final ComponentName componentName;
    private final Context mContext;
    protected final PackageManager mPm;
    public final PackageItemInfo packageItemInfo;
    public final int userId;
    
    public DefaultAppInfo(final Context context, final PackageManager packageManager, final int n, final ComponentName componentName) {
        this(context, packageManager, n, componentName, null, true);
    }
    
    public DefaultAppInfo(final Context mContext, final PackageManager mPm, final int userId, final ComponentName componentName, final String s, final boolean b) {
        super(b);
        this.mContext = mContext;
        this.mPm = mPm;
        this.packageItemInfo = null;
        this.userId = userId;
        this.componentName = componentName;
    }
    
    private ComponentInfo getComponentInfo() {
        try {
            Object o;
            if ((o = AppGlobals.getPackageManager().getActivityInfo(this.componentName, 0, this.userId)) == null) {
                o = AppGlobals.getPackageManager().getServiceInfo(this.componentName, 0, this.userId);
            }
            return (ComponentInfo)o;
        }
        catch (RemoteException ex) {
            return null;
        }
    }
    
    public String getKey() {
        final ComponentName componentName = this.componentName;
        if (componentName != null) {
            return componentName.flattenToString();
        }
        final PackageItemInfo packageItemInfo = this.packageItemInfo;
        if (packageItemInfo != null) {
            return packageItemInfo.packageName;
        }
        return null;
    }
    
    public Drawable loadIcon() {
        final IconDrawableFactory instance = IconDrawableFactory.newInstance(this.mContext);
        if (this.componentName != null) {
            try {
                final ComponentInfo componentInfo = this.getComponentInfo();
                final ApplicationInfo applicationInfoAsUser = this.mPm.getApplicationInfoAsUser(this.componentName.getPackageName(), 0, this.userId);
                if (componentInfo != null) {
                    return instance.getBadgedIcon((PackageItemInfo)componentInfo, applicationInfoAsUser, this.userId);
                }
                return instance.getBadgedIcon(applicationInfoAsUser);
            }
            catch (PackageManager$NameNotFoundException ex) {
                return null;
            }
        }
        final PackageItemInfo packageItemInfo = this.packageItemInfo;
        Label_0108: {
            if (packageItemInfo == null) {
                break Label_0108;
            }
            try {
                return instance.getBadgedIcon(this.packageItemInfo, this.mPm.getApplicationInfoAsUser(packageItemInfo.packageName, 0, this.userId), this.userId);
                return null;
            }
            catch (PackageManager$NameNotFoundException ex2) {
                return null;
            }
        }
    }
    
    public CharSequence loadLabel() {
        if (this.componentName != null) {
            try {
                final ComponentInfo componentInfo = this.getComponentInfo();
                if (componentInfo != null) {
                    return componentInfo.loadLabel(this.mPm);
                }
                return this.mPm.getApplicationInfoAsUser(this.componentName.getPackageName(), 0, this.userId).loadLabel(this.mPm);
            }
            catch (PackageManager$NameNotFoundException ex) {
                return null;
            }
        }
        final PackageItemInfo packageItemInfo = this.packageItemInfo;
        if (packageItemInfo != null) {
            return packageItemInfo.loadLabel(this.mPm);
        }
        return null;
    }
}
