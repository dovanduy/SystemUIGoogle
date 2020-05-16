// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.time;

public class SystemClockImpl implements SystemClock
{
    @Override
    public long elapsedRealtime() {
        return android.os.SystemClock.elapsedRealtime();
    }
    
    @Override
    public long uptimeMillis() {
        return android.os.SystemClock.uptimeMillis();
    }
}
