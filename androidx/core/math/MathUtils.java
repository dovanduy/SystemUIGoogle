// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.math;

public class MathUtils
{
    public static float clamp(final float n, final float n2, final float n3) {
        if (n < n2) {
            return n2;
        }
        if (n > n3) {
            return n3;
        }
        return n;
    }
}
