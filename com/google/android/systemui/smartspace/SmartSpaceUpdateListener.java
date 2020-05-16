// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

public interface SmartSpaceUpdateListener
{
    default void onGsaChanged() {
    }
    
    default void onSensitiveModeChanged(final boolean b, final boolean b2) {
    }
    
    void onSmartSpaceUpdated(final SmartSpaceData p0);
}
