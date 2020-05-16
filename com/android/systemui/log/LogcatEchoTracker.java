// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

public interface LogcatEchoTracker
{
    boolean isBufferLoggable(final String p0, final LogLevel p1);
    
    boolean isTagLoggable(final String p0, final LogLevel p1);
}
