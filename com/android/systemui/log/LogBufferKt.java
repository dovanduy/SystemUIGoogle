// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

import java.util.Locale;
import java.text.SimpleDateFormat;

public final class LogBufferKt
{
    private static final SimpleDateFormat DATE_FORMAT;
    
    static {
        DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.S", Locale.US);
    }
}
