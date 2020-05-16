// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.os.PowerManager$WakeLock;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.os.PowerManager;

public class PowerManagerWrapper
{
    private final PowerManager powerManager;
    
    public PowerManagerWrapper(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.powerManager = (PowerManager)context.getSystemService("power");
    }
    
    public Boolean isInteractive() {
        final PowerManager powerManager = this.powerManager;
        Boolean value;
        if (powerManager != null) {
            value = powerManager.isInteractive();
        }
        else {
            value = null;
        }
        return value;
    }
    
    public WakeLockWrapper newWakeLock(final int n, final String s) {
        Intrinsics.checkParameterIsNotNull(s, "tag");
        final PowerManager powerManager = this.powerManager;
        PowerManager$WakeLock wakeLock;
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(n, s);
        }
        else {
            wakeLock = null;
        }
        return new WakeLockWrapper(wakeLock);
    }
    
    public static class WakeLockWrapper
    {
        private final PowerManager$WakeLock wakeLock;
        
        public WakeLockWrapper(final PowerManager$WakeLock wakeLock) {
            this.wakeLock = wakeLock;
        }
        
        public void acquire(final long n) {
            final PowerManager$WakeLock wakeLock = this.wakeLock;
            if (wakeLock != null) {
                wakeLock.acquire(n);
            }
        }
    }
}
