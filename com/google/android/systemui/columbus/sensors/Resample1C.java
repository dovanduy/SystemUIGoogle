// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Resample1C
{
    protected long tInterval;
    protected long tRawLast;
    protected long tResampledLast;
    protected float xRawLast;
    protected float xResampledThis;
    
    public Resample1C() {
        this.xResampledThis = 0.0f;
        this.tInterval = 0L;
    }
    
    public long getInterval() {
        return this.tInterval;
    }
    
    public void init(final float n, final long n2, final long tInterval) {
        this.xRawLast = n;
        this.tRawLast = n2;
        this.xResampledThis = n;
        this.tResampledLast = n2;
        this.tInterval = tInterval;
    }
    
    public void setSyncTime(final long tResampledLast) {
        this.tResampledLast = tResampledLast;
    }
}
