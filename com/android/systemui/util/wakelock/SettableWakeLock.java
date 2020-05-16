// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.wakelock;

import java.util.Objects;

public class SettableWakeLock
{
    private boolean mAcquired;
    private final WakeLock mInner;
    private final String mWhy;
    
    public SettableWakeLock(final WakeLock wakeLock, final String mWhy) {
        Objects.requireNonNull(wakeLock, "inner wakelock required");
        this.mInner = wakeLock;
        this.mWhy = mWhy;
    }
    
    public void setAcquired(final boolean mAcquired) {
        synchronized (this) {
            if (this.mAcquired != mAcquired) {
                if (mAcquired) {
                    this.mInner.acquire(this.mWhy);
                }
                else {
                    this.mInner.release(this.mWhy);
                }
                this.mAcquired = mAcquired;
            }
        }
    }
}
