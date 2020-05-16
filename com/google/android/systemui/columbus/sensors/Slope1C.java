// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Slope1C
{
    private float xDelta;
    private float xRawLast;
    
    public Slope1C() {
        this.xDelta = 0.0f;
    }
    
    public void init(final float xRawLast) {
        this.xRawLast = xRawLast;
    }
    
    public float update(float xDelta, float xRawLast) {
        xRawLast *= xDelta;
        xDelta = xRawLast - this.xRawLast;
        this.xDelta = xDelta;
        this.xRawLast = xRawLast;
        return xDelta;
    }
}
