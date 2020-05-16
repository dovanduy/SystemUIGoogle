// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.Dumpable;
import com.android.systemui.DemoMode;

public interface BatteryController extends DemoMode, Dumpable, CallbackController<BatteryStateChangeCallback>
{
    default void getEstimatedTimeRemainingString(final EstimateFetchCompletion estimateFetchCompletion) {
    }
    
    boolean isAodPowerSave();
    
    boolean isPowerSave();
    
    default boolean isReverseSupported() {
        return false;
    }
    
    void setPowerSaveMode(final boolean p0);
    
    default void setReverseState(final boolean b) {
    }
    
    public interface BatteryStateChangeCallback
    {
        default void onBatteryLevelChanged(final int n, final boolean b, final boolean b2) {
        }
        
        default void onPowerSaveChanged(final boolean b) {
        }
        
        default void onReverseChanged(final boolean b, final int n, final String s) {
        }
    }
    
    public interface EstimateFetchCompletion
    {
        void onBatteryRemainingEstimateRetrieved(final String p0);
    }
}
