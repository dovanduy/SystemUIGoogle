// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import java.util.Locale;
import java.text.SimpleDateFormat;

public final class DozeLoggerKt
{
    private static final SimpleDateFormat DATE_FORMAT;
    
    static {
        DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.S", Locale.US);
    }
    
    public static final SimpleDateFormat getDATE_FORMAT() {
        return DozeLoggerKt.DATE_FORMAT;
    }
}
