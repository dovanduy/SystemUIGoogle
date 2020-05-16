// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Highpass3C
{
    private Highpass1C xHighpass;
    private Highpass1C yHighpass;
    private Highpass1C zHighpass;
    
    public Highpass3C() {
        this.xHighpass = new Highpass1C();
        this.yHighpass = new Highpass1C();
        this.zHighpass = new Highpass1C();
    }
    
    public void init(final Point3f point3f) {
        this.xHighpass.init(point3f.x);
        this.yHighpass.init(point3f.y);
        this.zHighpass.init(point3f.z);
    }
    
    public void setPara(final float para) {
        this.xHighpass.setPara(para);
        this.yHighpass.setPara(para);
        this.zHighpass.setPara(para);
    }
    
    public Point3f update(final Point3f point3f) {
        return new Point3f(this.xHighpass.update(point3f.x), this.yHighpass.update(point3f.y), this.zHighpass.update(point3f.z));
    }
}
