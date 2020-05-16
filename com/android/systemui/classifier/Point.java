// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class Point
{
    public long timeOffsetNano;
    public float x;
    public float y;
    
    public Point(final float x, final float y) {
        this.x = x;
        this.y = y;
        this.timeOffsetNano = 0L;
    }
    
    public Point(final float x, final float y, final long timeOffsetNano) {
        this.x = x;
        this.y = y;
        this.timeOffsetNano = timeOffsetNano;
    }
    
    public float crossProduct(final Point point, final Point point2) {
        final float x = point.x;
        final float x2 = this.x;
        final float y = point2.y;
        final float y2 = this.y;
        return (x - x2) * (y - y2) - (point.y - y2) * (point2.x - x2);
    }
    
    public float dist(final Point point) {
        return (float)Math.hypot(point.x - this.x, point.y - this.y);
    }
    
    public float dotProduct(final Point point, final Point point2) {
        final float x = point.x;
        final float x2 = this.x;
        final float x3 = point2.x;
        final float y = point.y;
        final float y2 = this.y;
        return (x - x2) * (x3 - x2) + (y - y2) * (point2.y - y2);
    }
    
    public boolean equals(final Point point) {
        return this.x == point.x && this.y == point.y;
    }
    
    public float getAngle(final Point point, final Point point2) {
        final float dist = this.dist(point);
        final float dist2 = this.dist(point2);
        if (dist != 0.0f && dist2 != 0.0f) {
            final float crossProduct = this.crossProduct(point, point2);
            float n = (float)Math.acos(Math.min(1.0f, Math.max(-1.0f, this.dotProduct(point, point2) / dist / dist2)));
            if (crossProduct < 0.0) {
                n = 6.2831855f - n;
            }
            return n;
        }
        return 0.0f;
    }
}
