// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.statusbar;

import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;

@DependsOn(target = StateListener.class)
@ProvidesInterface(version = 1)
public interface StatusBarStateController
{
    public static final int VERSION = 1;
    
    void addCallback(final StateListener p0);
    
    float getDozeAmount();
    
    int getState();
    
    boolean isDozing();
    
    void removeCallback(final StateListener p0);
    
    @ProvidesInterface(version = 1)
    public interface StateListener
    {
        public static final int VERSION = 1;
        
        default void onDozeAmountChanged(final float n, final float n2) {
        }
        
        default void onDozingChanged(final boolean b) {
        }
        
        default void onFullscreenStateChanged(final boolean b, final boolean b2) {
        }
        
        default void onPulsingChanged(final boolean b) {
        }
        
        default void onStateChanged(final int n) {
        }
        
        default void onStatePostChange() {
        }
        
        default void onStatePreChange(final int n, final int n2) {
        }
    }
}
