// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist.ui;

import java.util.Iterator;
import java.util.ArrayList;
import android.graphics.PointF;
import java.util.List;
import android.graphics.Path;

public abstract class CornerPathRenderer
{
    private Path approximateInnerPath(final Path path, final float n) {
        return this.toPath(this.shiftBy(this.getApproximatePoints(path), n));
    }
    
    private ArrayList<PointF> getApproximatePoints(final Path path) {
        final float[] approximate = path.approximate(0.1f);
        final ArrayList<PointF> list = new ArrayList<PointF>();
        for (int i = 0; i < approximate.length; i += 3) {
            list.add(new PointF(approximate[i + 1], approximate[i + 2]));
        }
        return list;
    }
    
    private float magnitude(final PointF pointF) {
        final float x = pointF.x;
        final float y = pointF.y;
        return (float)Math.sqrt(x * x + y * y);
    }
    
    private PointF normalAt(final List<PointF> list, final int n) {
        PointF pointF;
        if (n == 0) {
            pointF = new PointF(0.0f, 0.0f);
        }
        else {
            final PointF pointF2 = list.get(n);
            final PointF pointF3 = list.get(n - 1);
            pointF = new PointF(pointF2.x - pointF3.x, pointF2.y - pointF3.y);
        }
        PointF pointF4;
        if (n == list.size() - 1) {
            pointF4 = new PointF(0.0f, 0.0f);
        }
        else {
            final PointF pointF5 = list.get(n);
            final PointF pointF6 = list.get(n + 1);
            pointF4 = new PointF(pointF6.x - pointF5.x, pointF6.y - pointF5.y);
        }
        return this.rotate90Ccw(this.normalize(new PointF(pointF.x + pointF4.x, pointF.y + pointF4.y)));
    }
    
    private PointF normalize(final PointF pointF) {
        final float magnitude = this.magnitude(pointF);
        if (magnitude == 0.0f) {
            return pointF;
        }
        final float n = 1.0f / magnitude;
        return new PointF(pointF.x * n, pointF.y * n);
    }
    
    private PointF rotate90Ccw(final PointF pointF) {
        return new PointF(-pointF.y, pointF.x);
    }
    
    private ArrayList<PointF> shiftBy(final ArrayList<PointF> list, final float n) {
        final ArrayList<PointF> list2 = new ArrayList<PointF>();
        for (int i = 0; i < list.size(); ++i) {
            final PointF pointF = list.get(i);
            final PointF normal = this.normalAt(list, i);
            list2.add(new PointF(pointF.x + normal.x * n, pointF.y + normal.y * n));
        }
        return list2;
    }
    
    private Path toPath(final List<PointF> list) {
        final Path path = new Path();
        if (list.size() > 0) {
            path.moveTo(list.get(0).x, list.get(0).y);
            for (final PointF pointF : list.subList(1, list.size())) {
                path.lineTo(pointF.x, pointF.y);
            }
        }
        return path;
    }
    
    public abstract Path getCornerPath(final Corner p0);
    
    public Path getInsetPath(final Corner corner, final float n) {
        return this.approximateInnerPath(this.getCornerPath(corner), -n);
    }
    
    public enum Corner
    {
        BOTTOM_LEFT, 
        BOTTOM_RIGHT, 
        TOP_LEFT, 
        TOP_RIGHT;
    }
}
