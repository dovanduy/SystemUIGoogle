// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.external;

import android.os.RemoteException;
import android.content.pm.ServiceInfo;
import android.content.ComponentName;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.pm.PackageInfo;
import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.IPackageManager;

public class PackageManagerAdapter
{
    private IPackageManager mIPackageManager;
    private PackageManager mPackageManager;
    
    public PackageManagerAdapter(final Context context) {
        this.mPackageManager = context.getPackageManager();
        this.mIPackageManager = AppGlobals.getPackageManager();
    }
    
    public PackageInfo getPackageInfoAsUser(final String s, final int n, final int n2) throws PackageManager$NameNotFoundException {
        return this.mPackageManager.getPackageInfoAsUser(s, n, n2);
    }
    
    public ServiceInfo getServiceInfo(final ComponentName componentName, final int n) throws PackageManager$NameNotFoundException {
        return this.mPackageManager.getServiceInfo(componentName, n);
    }
    
    public ServiceInfo getServiceInfo(final ComponentName componentName, final int n, final int n2) throws RemoteException {
        return this.mIPackageManager.getServiceInfo(componentName, n, n2);
    }
}
