// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Slope3C
{
    private Slope1C _slopeX;
    private Slope1C _slopeY;
    private Slope1C _slopeZ;
    
    public Slope3C() {
        this._slopeX = new Slope1C();
        this._slopeY = new Slope1C();
        this._slopeZ = new Slope1C();
    }
    
    public void init(final Point3f point3f) {
        this._slopeX.init(point3f.x);
        this._slopeY.init(point3f.y);
        this._slopeZ.init(point3f.z);
    }
    
    public Point3f update(final Point3f point3f, final float n) {
        return new Point3f(this._slopeX.update(point3f.x, n), this._slopeY.update(point3f.y, n), this._slopeZ.update(point3f.z, n));
    }
}
