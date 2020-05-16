// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

public interface DeviceProvisionedController extends CallbackController<DeviceProvisionedListener>
{
    int getCurrentUser();
    
    default boolean isCurrentUserSetup() {
        return this.isUserSetup(this.getCurrentUser());
    }
    
    boolean isDeviceProvisioned();
    
    boolean isUserSetup(final int p0);
    
    public interface DeviceProvisionedListener
    {
        default void onDeviceProvisionedChanged() {
        }
        
        default void onUserSetupChanged() {
        }
        
        default void onUserSwitched() {
            this.onUserSetupChanged();
        }
    }
}
