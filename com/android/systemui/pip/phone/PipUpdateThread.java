// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.os.Handler;
import android.os.HandlerThread;

public final class PipUpdateThread extends HandlerThread
{
    private static PipUpdateThread sInstance;
    
    private PipUpdateThread() {
        super("pip");
    }
    
    private static void ensureThreadLocked() {
        if (PipUpdateThread.sInstance == null) {
            (PipUpdateThread.sInstance = new PipUpdateThread()).start();
            new Handler(PipUpdateThread.sInstance.getLooper());
        }
    }
    
    public static PipUpdateThread get() {
        synchronized (PipUpdateThread.class) {
            ensureThreadLocked();
            return PipUpdateThread.sInstance;
        }
    }
}
