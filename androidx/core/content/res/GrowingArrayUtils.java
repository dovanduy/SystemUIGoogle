// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.content.res;

import java.lang.reflect.Array;

final class GrowingArrayUtils
{
    public static int[] append(final int[] array, final int n, final int n2) {
        int[] array2 = array;
        if (n + 1 > array.length) {
            array2 = new int[growSize(n)];
            System.arraycopy(array, 0, array2, 0, n);
        }
        array2[n] = n2;
        return array2;
    }
    
    public static <T> T[] append(final T[] array, final int n, final T t) {
        Object[] array2 = array;
        if (n + 1 > array.length) {
            array2 = (Object[])Array.newInstance(array.getClass().getComponentType(), growSize(n));
            System.arraycopy(array, 0, array2, 0, n);
        }
        array2[n] = t;
        return (T[])array2;
    }
    
    public static int growSize(int n) {
        if (n <= 4) {
            n = 8;
        }
        else {
            n *= 2;
        }
        return n;
    }
}
