// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist.ui;

import android.util.Log;

public final class EdgeLight
{
    private int mColor;
    private float mLength;
    private float mStart;
    
    public EdgeLight(final int mColor, final float mStart, final float mLength) {
        this.mColor = mColor;
        this.mStart = mStart;
        this.mLength = mLength;
    }
    
    public EdgeLight(final EdgeLight edgeLight) {
        this.mColor = edgeLight.getColor();
        this.mStart = edgeLight.getStart();
        this.mLength = edgeLight.getLength();
    }
    
    public static EdgeLight[] copy(final EdgeLight[] array) {
        final EdgeLight[] array2 = new EdgeLight[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new EdgeLight(array[i]);
        }
        return array2;
    }
    
    public int getColor() {
        return this.mColor;
    }
    
    public float getEnd() {
        return this.mStart + this.mLength;
    }
    
    public float getLength() {
        return this.mLength;
    }
    
    public float getStart() {
        return this.mStart;
    }
    
    public boolean setColor(final int mColor) {
        final boolean b = this.mColor != mColor;
        this.mColor = mColor;
        return b;
    }
    
    public void setEndpoints(final float n, final float f) {
        if (n > f) {
            Log.e("EdgeLight", String.format("Endpoint must be >= start (add 1 if necessary). Got [%f, %f]", n, f));
            return;
        }
        this.mStart = n;
        this.mLength = f - n;
    }
    
    public void setLength(final float mLength) {
        this.mLength = mLength;
    }
    
    public void setStart(final float mStart) {
        this.mStart = mStart;
    }
}
