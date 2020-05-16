// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.os.UserHandle;
import android.content.Intent;
import android.os.RemoteException;
import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import java.util.List;
import android.app.AppGlobals;
import android.content.pm.IPackageManager;

public class PackageManagerWrapper
{
    private static final IPackageManager mIPackageManager;
    private static final PackageManagerWrapper sInstance;
    
    static {
        sInstance = new PackageManagerWrapper();
        mIPackageManager = AppGlobals.getPackageManager();
    }
    
    private PackageManagerWrapper() {
    }
    
    public static PackageManagerWrapper getInstance() {
        return PackageManagerWrapper.sInstance;
    }
    
    public ComponentName getHomeActivities(final List<ResolveInfo> list) {
        try {
            return PackageManagerWrapper.mIPackageManager.getHomeActivities((List)list);
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public ResolveInfo resolveActivity(final Intent intent, final int n) {
        final String resolveTypeIfNeeded = intent.resolveTypeIfNeeded(AppGlobals.getInitialApplication().getContentResolver());
        try {
            return PackageManagerWrapper.mIPackageManager.resolveIntent(intent, resolveTypeIfNeeded, n, UserHandle.getCallingUserId());
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
