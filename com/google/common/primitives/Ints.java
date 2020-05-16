// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.primitives;

public final class Ints
{
    public static int saturatedCast(final long n) {
        if (n > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (n < -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int)n;
    }
}
