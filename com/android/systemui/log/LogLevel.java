// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

public enum LogLevel
{
    DEBUG(3), 
    ERROR(6), 
    INFO(4), 
    VERBOSE(2), 
    WARNING(5), 
    WTF(7);
    
    private final int nativeLevel;
    
    private LogLevel(final int nativeLevel) {
        this.nativeLevel = nativeLevel;
    }
}
