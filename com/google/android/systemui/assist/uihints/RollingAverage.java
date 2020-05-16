// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

public class RollingAverage
{
    private int mIndex;
    private float[] mSamples;
    private int mSize;
    private float mTotal;
    
    public RollingAverage(final int mSize) {
        this.mTotal = 0.0f;
        int i = 0;
        this.mIndex = 0;
        this.mSize = mSize;
        this.mSamples = new float[mSize];
        while (i < mSize) {
            this.mSamples[i] = 0.0f;
            ++i;
        }
    }
    
    public void add(final float n) {
        final int mSize = this.mSize;
        if (mSize <= 0) {
            return;
        }
        final float mTotal = this.mTotal;
        final float[] mSamples = this.mSamples;
        int mIndex = this.mIndex;
        final float mTotal2 = mTotal - mSamples[mIndex];
        this.mTotal = mTotal2;
        mSamples[mIndex] = n;
        this.mTotal = mTotal2 + n;
        ++mIndex;
        if ((this.mIndex = mIndex) == mSize) {
            this.mIndex = 0;
        }
    }
    
    public double getAverage() {
        return this.mTotal / this.mSize;
    }
}
