// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Highpass1C
{
    private float para;
    private float xLast;
    private float yLast;
    
    public Highpass1C() {
        this.para = 1.0f;
        this.xLast = 0.0f;
        this.yLast = 0.0f;
    }
    
    public void init(final float n) {
        this.xLast = n;
        this.yLast = n;
    }
    
    public void setPara(final float para) {
        this.para = para;
    }
    
    public float update(final float xLast) {
        final float para = this.para;
        final float yLast = this.yLast * para + para * (xLast - this.xLast);
        this.yLast = yLast;
        this.xLast = xLast;
        return yLast;
    }
}
