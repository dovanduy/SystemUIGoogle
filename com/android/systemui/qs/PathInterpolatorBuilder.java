// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.animation.BaseInterpolator;
import android.view.animation.Interpolator;
import android.graphics.Path;

public class PathInterpolatorBuilder
{
    private float[] mDist;
    private float[] mX;
    private float[] mY;
    
    public PathInterpolatorBuilder(final float n, final float n2, final float n3, final float n4) {
        this.initCubic(n, n2, n3, n4);
    }
    
    private void initCubic(final float n, final float n2, final float n3, final float n4) {
        final Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.cubicTo(n, n2, n3, n4, 1.0f, 1.0f);
        this.initPath(path);
    }
    
    private void initPath(final Path path) {
        final float[] approximate = path.approximate(0.002f);
        final int n = approximate.length / 3;
        final float n2 = approximate[1];
        float n3 = 0.0f;
        if (n2 == 0.0f && approximate[2] == 0.0f && approximate[approximate.length - 2] == 1.0f && approximate[approximate.length - 1] == 1.0f) {
            this.mX = new float[n];
            this.mY = new float[n];
            this.mDist = new float[n];
            final int n4 = 0;
            int n5;
            int i = n5 = 0;
            float n6 = 0.0f;
            while (i < n) {
                final int n7 = n5 + 1;
                final float n8 = approximate[n5];
                n5 = n7 + 1;
                final float n9 = approximate[n7];
                final float n10 = approximate[n5];
                if (n8 == n3 && n9 != n6) {
                    throw new IllegalArgumentException("The Path cannot have discontinuity in the X axis.");
                }
                if (n9 < n6) {
                    throw new IllegalArgumentException("The Path cannot loop back on itself.");
                }
                final float[] mx = this.mX;
                mx[i] = n9;
                final float[] my = this.mY;
                my[i] = n10;
                if (i > 0) {
                    final float n11 = mx[i];
                    final int n12 = i - 1;
                    final float n13 = n11 - mx[n12];
                    final float n14 = my[i] - my[n12];
                    final float n15 = (float)Math.sqrt(n13 * n13 + n14 * n14);
                    final float[] mDist = this.mDist;
                    mDist[i] = mDist[n12] + n15;
                }
                ++i;
                n3 = n8;
                n6 = n9;
                ++n5;
            }
            final float[] mDist2 = this.mDist;
            final float n16 = mDist2[mDist2.length - 1];
            for (int j = n4; j < n; ++j) {
                final float[] mDist3 = this.mDist;
                mDist3[j] /= n16;
            }
            return;
        }
        throw new IllegalArgumentException("The Path must start at (0,0) and end at (1,1)");
    }
    
    public Interpolator getXInterpolator() {
        return (Interpolator)new PathInterpolator(this.mDist, this.mX);
    }
    
    public Interpolator getYInterpolator() {
        return (Interpolator)new PathInterpolator(this.mDist, this.mY);
    }
    
    private static class PathInterpolator extends BaseInterpolator
    {
        private final float[] mX;
        private final float[] mY;
        
        private PathInterpolator(final float[] mx, final float[] my) {
            this.mX = mx;
            this.mY = my;
        }
        
        public float getInterpolation(float n) {
            if (n <= 0.0f) {
                return 0.0f;
            }
            if (n >= 1.0f) {
                return 1.0f;
            }
            int n2 = 0;
            int n3 = this.mX.length - 1;
            while (n3 - n2 > 1) {
                final int n4 = (n2 + n3) / 2;
                if (n < this.mX[n4]) {
                    n3 = n4;
                }
                else {
                    n2 = n4;
                }
            }
            final float[] mx = this.mX;
            final float n5 = mx[n3] - mx[n2];
            if (n5 == 0.0f) {
                return this.mY[n2];
            }
            final float n6 = (n - mx[n2]) / n5;
            final float[] my = this.mY;
            n = my[n2];
            return n + n6 * (my[n3] - n);
        }
    }
}
