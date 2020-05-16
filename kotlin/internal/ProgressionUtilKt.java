// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.internal;

public final class ProgressionUtilKt
{
    private static final int differenceModulo(final int n, final int n2, final int n3) {
        return mod(mod(n, n3) - mod(n2, n3), n3);
    }
    
    public static final int getProgressionLastElement(final int n, int n2, final int n3) {
        if (n3 > 0) {
            if (n < n2) {
                n2 -= differenceModulo(n2, n, n3);
            }
        }
        else {
            if (n3 >= 0) {
                throw new IllegalArgumentException("Step is zero.");
            }
            if (n > n2) {
                n2 += differenceModulo(n, n2, -n3);
            }
        }
        return n2;
    }
    
    private static final int mod(int n, final int n2) {
        n %= n2;
        if (n < 0) {
            n += n2;
        }
        return n;
    }
}
