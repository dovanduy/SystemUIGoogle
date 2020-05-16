// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Lowpass1C
{
    private float para;
    private float xLast;
    
    public Lowpass1C() {
        this.para = 1.0f;
        this.xLast = 0.0f;
    }
    
    public void init(final float xLast) {
        this.xLast = xLast;
    }
    
    public void setPara(final float para) {
        this.para = para;
    }
    
    public float update(float xLast) {
        final float para = this.para;
        xLast = (1.0f - para) * this.xLast + para * xLast;
        return this.xLast = xLast;
    }
}
