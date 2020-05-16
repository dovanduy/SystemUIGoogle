// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.primitives;

public final class UnsignedLongs
{
    public static int compare(final long n, final long n2) {
        return Longs.compare(flip(n), flip(n2));
    }
    
    private static long flip(final long n) {
        return n ^ Long.MIN_VALUE;
    }
}
