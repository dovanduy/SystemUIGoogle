// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.ranges;

class RangesKt___RangesKt extends RangesKt__RangesKt
{
    public static int coerceAtLeast(final int n, final int n2) {
        int n3 = n;
        if (n < n2) {
            n3 = n2;
        }
        return n3;
    }
    
    public static int coerceAtMost(final int n, final int n2) {
        int n3 = n;
        if (n > n2) {
            n3 = n2;
        }
        return n3;
    }
    
    public static IntProgression downTo(final int n, final int n2) {
        return IntProgression.Companion.fromClosedRange(n, n2, -1);
    }
    
    public static IntRange until(final int n, final int n2) {
        if (n2 <= Integer.MIN_VALUE) {
            return IntRange.Companion.getEMPTY();
        }
        return new IntRange(n, n2 - 1);
    }
}
