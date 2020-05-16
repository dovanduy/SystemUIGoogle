// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;

public class DevicePolicyManagerWrapper
{
    private static final DevicePolicyManager sDevicePolicyManager;
    private static final DevicePolicyManagerWrapper sInstance;
    
    static {
        sInstance = new DevicePolicyManagerWrapper();
        sDevicePolicyManager = (DevicePolicyManager)AppGlobals.getInitialApplication().getSystemService((Class)DevicePolicyManager.class);
    }
    
    private DevicePolicyManagerWrapper() {
    }
    
    public static DevicePolicyManagerWrapper getInstance() {
        return DevicePolicyManagerWrapper.sInstance;
    }
    
    public boolean isLockTaskPermitted(final String s) {
        return DevicePolicyManagerWrapper.sDevicePolicyManager.isLockTaskPermitted(s);
    }
}
