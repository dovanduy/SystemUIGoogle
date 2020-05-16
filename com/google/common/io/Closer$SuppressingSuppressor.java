// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.io;

import java.lang.reflect.Method;

final class Closer$SuppressingSuppressor implements Closer$Suppressor
{
    static {
        new Closer$SuppressingSuppressor();
        getAddSuppressed();
    }
    
    private static Method getAddSuppressed() {
        try {
            return Throwable.class.getMethod("addSuppressed", Throwable.class);
        }
        finally {
            return null;
        }
    }
}
