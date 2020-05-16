// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

final class Hashing
{
    static int closedTableSize(int highestOneBit, final double n) {
        final int max = Math.max(highestOneBit, 2);
        highestOneBit = Integer.highestOneBit(max);
        if (max > (int)(n * highestOneBit)) {
            highestOneBit <<= 1;
            if (highestOneBit <= 0) {
                highestOneBit = 1073741824;
            }
            return highestOneBit;
        }
        return highestOneBit;
    }
    
    static boolean needsResizing(final int n, final int n2, final double n3) {
        return n > n3 * n2 && n2 < 1073741824;
    }
    
    static int smear(final int n) {
        return (int)(Integer.rotateLeft((int)(n * -862048943L), 15) * 461845907L);
    }
    
    static int smearedHash(final Object o) {
        int hashCode;
        if (o == null) {
            hashCode = 0;
        }
        else {
            hashCode = o.hashCode();
        }
        return smear(hashCode);
    }
}
