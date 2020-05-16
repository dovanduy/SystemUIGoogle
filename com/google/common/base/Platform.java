// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.util.logging.Logger;

final class Platform
{
    static {
        Logger.getLogger(Platform.class.getName());
        loadPatternCompiler();
    }
    
    private Platform() {
    }
    
    private static PatternCompiler loadPatternCompiler() {
        return new JdkPatternCompiler();
    }
    
    static long systemNanoTime() {
        return System.nanoTime();
    }
    
    private static final class JdkPatternCompiler implements PatternCompiler
    {
    }
}
