// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.math;

class MathKt__MathJVMKt extends MathKt__MathHKt
{
    public static int roundToInt(final float n) {
        if (!Float.isNaN(n)) {
            return Math.round(n);
        }
        throw new IllegalArgumentException("Cannot round NaN value.");
    }
}
