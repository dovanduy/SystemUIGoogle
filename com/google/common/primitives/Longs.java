// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.primitives;

public final class Longs
{
    public static int compare(final long n, final long n2) {
        final long n3 = lcmp(n, n2);
        int n4;
        if (n3 < 0) {
            n4 = -1;
        }
        else if (n3 > 0) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        return n4;
    }
}
