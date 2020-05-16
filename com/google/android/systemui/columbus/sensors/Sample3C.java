// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class Sample3C
{
    public Point3f point;
    public long t;
    
    public Sample3C(final float x, final float y, final float z, final long t) {
        final Point3f point = new Point3f(0.0f, 0.0f, 0.0f);
        this.point = point;
        point.x = x;
        point.y = y;
        point.z = z;
        this.t = t;
    }
}
