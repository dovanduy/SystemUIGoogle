// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import androidx.core.util.ObjectsCompat;
import java.lang.reflect.Array;

class ArrayUtils
{
    public static <T> T[] appendElement(final Class<T> clazz, final T[] array, final T t) {
        int length = 0;
        Object[] array2;
        if (array != null) {
            length = array.length;
            array2 = (Object[])Array.newInstance(clazz, length + 1);
            System.arraycopy(array, 0, array2, 0, length);
        }
        else {
            array2 = (Object[])Array.newInstance(clazz, 1);
        }
        array2[length] = t;
        return (T[])array2;
    }
    
    public static <T> boolean contains(final T[] array, final T t) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (ObjectsCompat.equals(array[i], t)) {
                return true;
            }
        }
        return false;
    }
    
    public static <T> T[] removeElement(final Class<T> componentType, final T[] array, final T t) {
        if (array != null) {
            if (!contains(array, t)) {
                return array;
            }
            final int length = array.length;
            int i = 0;
            while (i < length) {
                if (ObjectsCompat.equals(array[i], t)) {
                    if (length == 1) {
                        return null;
                    }
                    final Object[] array2 = (Object[])Array.newInstance(componentType, length - 1);
                    System.arraycopy(array, 0, array2, 0, i);
                    System.arraycopy(array, i + 1, array2, i, length - i - 1);
                    return (T[])array2;
                }
                else {
                    ++i;
                }
            }
        }
        return array;
    }
}
