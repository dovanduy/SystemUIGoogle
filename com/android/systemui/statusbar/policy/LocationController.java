// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

public interface LocationController extends CallbackController<LocationChangeCallback>
{
    boolean isLocationActive();
    
    boolean isLocationEnabled();
    
    boolean setLocationEnabled(final boolean p0);
    
    public interface LocationChangeCallback
    {
        default void onLocationActiveChanged(final boolean b) {
        }
        
        default void onLocationSettingsChanged(final boolean b) {
        }
    }
}
