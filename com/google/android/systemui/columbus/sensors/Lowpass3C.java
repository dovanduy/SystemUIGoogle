// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Lowpass3C extends Lowpass1C
{
    private Lowpass1C xLowpass;
    private Lowpass1C yLowpass;
    private Lowpass1C zLowpass;
    
    public Lowpass3C() {
        this.xLowpass = new Lowpass1C();
        this.yLowpass = new Lowpass1C();
        this.zLowpass = new Lowpass1C();
    }
    
    public void init(final Point3f point3f) {
        this.xLowpass.init(point3f.x);
        this.yLowpass.init(point3f.y);
        this.zLowpass.init(point3f.z);
    }
    
    @Override
    public void setPara(final float para) {
        this.xLowpass.setPara(para);
        this.yLowpass.setPara(para);
        this.zLowpass.setPara(para);
    }
    
    public Point3f update(final Point3f point3f) {
        return new Point3f(this.xLowpass.update(point3f.x), this.yLowpass.update(point3f.y), this.zLowpass.update(point3f.z));
    }
}
