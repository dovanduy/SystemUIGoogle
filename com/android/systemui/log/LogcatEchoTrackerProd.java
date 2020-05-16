// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

import kotlin.jvm.internal.Intrinsics;

public final class LogcatEchoTrackerProd implements LogcatEchoTracker
{
    @Override
    public boolean isBufferLoggable(final String s, final LogLevel logLevel) {
        Intrinsics.checkParameterIsNotNull(s, "bufferName");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        return logLevel.compareTo(LogLevel.WARNING) >= 0;
    }
    
    @Override
    public boolean isTagLoggable(final String s, final LogLevel logLevel) {
        Intrinsics.checkParameterIsNotNull(s, "tagName");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        return logLevel.compareTo(LogLevel.WARNING) >= 0;
    }
}
