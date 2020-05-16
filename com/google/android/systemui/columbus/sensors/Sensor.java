// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public interface Sensor
{
    boolean isListening();
    
    void startListening(final boolean p0);
    
    void stopListening();
}
