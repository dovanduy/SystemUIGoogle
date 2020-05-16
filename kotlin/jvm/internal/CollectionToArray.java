// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import kotlin.TypeCastException;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collection;

public final class CollectionToArray
{
    private static final Object[] EMPTY;
    
    static {
        EMPTY = new Object[0];
    }
    
    public static final Object[] toArray(final Collection<?> collection) {
        final Object[] empty = CollectionToArray.EMPTY;
        Intrinsics.checkParameterIsNotNull(collection, "collection");
        final int size = collection.size();
        Object[] copy;
        if (size == 0) {
            copy = empty;
        }
        else {
            final Iterator<?> iterator = collection.iterator();
            if (!iterator.hasNext()) {
                copy = empty;
            }
            else {
                copy = new Object[size];
                int n = 0;
                while (true) {
                    final int newLength = n + 1;
                    copy[n] = iterator.next();
                    Object[] copy2;
                    if (newLength >= copy.length) {
                        if (!iterator.hasNext()) {
                            break;
                        }
                        int newLength2;
                        if ((newLength2 = newLength * 3 + 1 >>> 1) <= newLength) {
                            if (newLength >= 2147483645) {
                                throw new OutOfMemoryError();
                            }
                            newLength2 = 2147483645;
                        }
                        copy2 = Arrays.copyOf(copy, newLength2);
                        Intrinsics.checkExpressionValueIsNotNull(copy2, "Arrays.copyOf(result, newSize)");
                    }
                    else {
                        copy2 = copy;
                        if (!iterator.hasNext()) {
                            copy = Arrays.copyOf(copy, newLength);
                            Intrinsics.checkExpressionValueIsNotNull(copy, "Arrays.copyOf(result, size)");
                            break;
                        }
                    }
                    n = newLength;
                    copy = copy2;
                }
            }
        }
        return copy;
    }
    
    public static final Object[] toArray(final Collection<?> collection, final Object[] array) {
        Intrinsics.checkParameterIsNotNull(collection, "collection");
        if (array != null) {
            final int size = collection.size();
            int n = 0;
            Object[] copy;
            if (size == 0) {
                copy = array;
                if (array.length > 0) {
                    array[0] = null;
                    copy = array;
                }
            }
            else {
                final Iterator<?> iterator = collection.iterator();
                if (!iterator.hasNext()) {
                    copy = array;
                    if (array.length > 0) {
                        array[0] = null;
                        copy = array;
                    }
                }
                else {
                    if (size <= array.length) {
                        copy = array;
                    }
                    else {
                        final Object instance = Array.newInstance(array.getClass().getComponentType(), size);
                        if (instance == null) {
                            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<kotlin.Any?>");
                        }
                        copy = (Object[])instance;
                    }
                    while (true) {
                        final int newLength = n + 1;
                        copy[n] = iterator.next();
                        Object[] copy2;
                        if (newLength >= copy.length) {
                            if (!iterator.hasNext()) {
                                break;
                            }
                            int newLength2;
                            if ((newLength2 = newLength * 3 + 1 >>> 1) <= newLength) {
                                if (newLength >= 2147483645) {
                                    throw new OutOfMemoryError();
                                }
                                newLength2 = 2147483645;
                            }
                            copy2 = Arrays.copyOf(copy, newLength2);
                            Intrinsics.checkExpressionValueIsNotNull(copy2, "Arrays.copyOf(result, newSize)");
                        }
                        else {
                            copy2 = copy;
                            if (!iterator.hasNext()) {
                                if (copy == array) {
                                    array[newLength] = null;
                                    copy = array;
                                    break;
                                }
                                copy = Arrays.copyOf(copy, newLength);
                                Intrinsics.checkExpressionValueIsNotNull(copy, "Arrays.copyOf(result, size)");
                                break;
                            }
                        }
                        n = newLength;
                        copy = copy2;
                    }
                }
            }
            return copy;
        }
        throw null;
    }
}
