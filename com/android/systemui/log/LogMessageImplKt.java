// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

import kotlin.jvm.functions.Function1;

public final class LogMessageImplKt
{
    private static final Function1<LogMessage, String> DEFAULT_RENDERER;
    
    static {
        DEFAULT_RENDERER = (Function1)LogMessageImplKt$DEFAULT_RENDERER.LogMessageImplKt$DEFAULT_RENDERER$1.INSTANCE;
    }
}
