// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.Dumpable;

public interface SecurityController extends CallbackController<SecurityControllerCallback>, Dumpable
{
    CharSequence getDeviceOwnerOrganizationName();
    
    String getPrimaryVpnName();
    
    CharSequence getWorkProfileOrganizationName();
    
    String getWorkProfileVpnName();
    
    boolean hasCACertInCurrentUser();
    
    boolean hasCACertInWorkProfile();
    
    boolean hasWorkProfile();
    
    boolean isDeviceManaged();
    
    boolean isNetworkLoggingEnabled();
    
    boolean isVpnBranded();
    
    boolean isVpnEnabled();
    
    public interface SecurityControllerCallback
    {
        void onStateChanged();
    }
}
