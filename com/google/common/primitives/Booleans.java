// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.primitives;

public final class Booleans
{
    public static int compare(final boolean b, final boolean b2) {
        int n;
        if (b == b2) {
            n = 0;
        }
        else if (b) {
            n = 1;
        }
        else {
            n = -1;
        }
        return n;
    }
}
