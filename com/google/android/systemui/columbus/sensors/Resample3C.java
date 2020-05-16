// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Resample3C extends Resample1C
{
    private float yRawLast;
    private float yResampledThis;
    private float zRawLast;
    private float zResampledThis;
    
    public Sample3C getResults() {
        return new Sample3C(super.xResampledThis, this.yResampledThis, this.zResampledThis, super.tResampledLast);
    }
    
    public void init(final float n, final float n2, final float n3, final long n4, final long n5) {
        this.init(n, n4, n5);
        this.yRawLast = n2;
        this.zRawLast = n3;
        this.yResampledThis = n2;
        this.zResampledThis = n3;
    }
    
    public boolean update(final float n, final float n2, final float n3, final long n4) {
        final long tRawLast = super.tRawLast;
        if (n4 == tRawLast) {
            return false;
        }
        long tInterval;
        if ((tInterval = super.tInterval) <= 0L) {
            tInterval = n4 - tRawLast;
        }
        final long tResampledLast = super.tResampledLast + tInterval;
        if (n4 < tResampledLast) {
            super.tRawLast = n4;
            super.xRawLast = n;
            this.yRawLast = n2;
            this.zRawLast = n3;
            return false;
        }
        final long tRawLast2 = super.tRawLast;
        final float n5 = (tResampledLast - tRawLast2) / (float)(n4 - tRawLast2);
        final float xRawLast = super.xRawLast;
        super.xResampledThis = (n - xRawLast) * n5 + xRawLast;
        final float yRawLast = this.yRawLast;
        this.yResampledThis = (n2 - yRawLast) * n5 + yRawLast;
        final float zRawLast = this.zRawLast;
        this.zResampledThis = (n3 - zRawLast) * n5 + zRawLast;
        super.tResampledLast = tResampledLast;
        if (tRawLast2 < tResampledLast) {
            super.tRawLast = n4;
            super.xRawLast = n;
            this.yRawLast = n2;
            this.zRawLast = n3;
        }
        return true;
    }
}
