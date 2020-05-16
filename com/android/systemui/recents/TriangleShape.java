// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.drawable.shapes.PathShape;

public class TriangleShape extends PathShape
{
    private Path mTriangularPath;
    
    public TriangleShape(final Path mTriangularPath, final float n, final float n2) {
        super(mTriangularPath, n, n2);
        this.mTriangularPath = mTriangularPath;
    }
    
    public static TriangleShape create(final float n, final float n2, final boolean b) {
        final Path path = new Path();
        if (b) {
            path.moveTo(0.0f, n2);
            path.lineTo(n, n2);
            path.lineTo(n / 2.0f, 0.0f);
            path.close();
        }
        else {
            path.moveTo(0.0f, 0.0f);
            path.lineTo(n / 2.0f, n2);
            path.lineTo(n, 0.0f);
            path.close();
        }
        return new TriangleShape(path, n, n2);
    }
    
    public static TriangleShape createHorizontal(final float n, final float n2, final boolean b) {
        final Path path = new Path();
        if (b) {
            path.moveTo(0.0f, n2 / 2.0f);
            path.lineTo(n, n2);
            path.lineTo(n, 0.0f);
            path.close();
        }
        else {
            path.moveTo(0.0f, n2);
            path.lineTo(n, n2 / 2.0f);
            path.lineTo(0.0f, 0.0f);
            path.close();
        }
        return new TriangleShape(path, n, n2);
    }
    
    public void getOutline(final Outline outline) {
        outline.setPath(this.mTriangularPath);
    }
}
