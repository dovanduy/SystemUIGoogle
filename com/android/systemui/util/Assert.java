// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.os.Looper;

public class Assert
{
    private static final Looper sMainLooper;
    private static Looper sTestLooper;
    
    static {
        sMainLooper = Looper.getMainLooper();
        Assert.sTestLooper = null;
    }
    
    public static void isMainThread() {
        if (!Assert.sMainLooper.isCurrentThread()) {
            final Looper sTestLooper = Assert.sTestLooper;
            if (sTestLooper == null || !sTestLooper.isCurrentThread()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("should be called from the main thread. sMainLooper.threadName=");
                sb.append(Assert.sMainLooper.getThread().getName());
                sb.append(" Thread.currentThread()=");
                sb.append(Thread.currentThread().getName());
                throw new IllegalStateException(sb.toString());
            }
        }
    }
    
    public static void setTestableLooper(final Looper sTestLooper) {
        Assert.sTestLooper = sTestLooper;
    }
}
