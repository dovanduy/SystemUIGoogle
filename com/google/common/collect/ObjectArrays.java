// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Iterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public final class ObjectArrays
{
    @CanIgnoreReturnValue
    static Object checkElementNotNull(final Object o, final int i) {
        if (o != null) {
            return o;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("at index ");
        sb.append(i);
        throw new NullPointerException(sb.toString());
    }
    
    @CanIgnoreReturnValue
    static Object[] checkElementsNotNull(final Object... array) {
        checkElementsNotNull(array, array.length);
        return array;
    }
    
    @CanIgnoreReturnValue
    static Object[] checkElementsNotNull(final Object[] array, final int n) {
        for (int i = 0; i < n; ++i) {
            checkElementNotNull(array[i], i);
        }
        return array;
    }
    
    @CanIgnoreReturnValue
    private static Object[] fillArray(final Iterable<?> iterable, final Object[] array) {
        final Iterator<?> iterator = iterable.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            array[n] = iterator.next();
            ++n;
        }
        return array;
    }
    
    public static <T> T[] newArray(final T[] array, final int n) {
        return Platform.newArray(array, n);
    }
    
    static Object[] toArrayImpl(final Collection<?> collection) {
        final Object[] array = new Object[collection.size()];
        fillArray(collection, array);
        return array;
    }
    
    static <T> T[] toArrayImpl(final Collection<?> collection, final T[] array) {
        final int size = collection.size();
        T[] array2 = array;
        if (array.length < size) {
            array2 = newArray(array, size);
        }
        fillArray(collection, array2);
        if (array2.length > size) {
            array2[size] = null;
        }
        return array2;
    }
    
    static <T> T[] toArrayImpl(final Object[] array, final int n, final int n2, final T[] array2) {
        Preconditions.checkPositionIndexes(n, n + n2, array.length);
        T[] array3;
        if (array2.length < n2) {
            array3 = newArray(array2, n2);
        }
        else {
            array3 = array2;
            if (array2.length > n2) {
                array2[n2] = null;
                array3 = array2;
            }
        }
        System.arraycopy(array, n, array3, 0, n2);
        return array3;
    }
}
