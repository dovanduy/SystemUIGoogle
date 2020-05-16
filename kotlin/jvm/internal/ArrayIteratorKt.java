// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import java.util.Iterator;

public final class ArrayIteratorKt
{
    public static final <T> Iterator<T> iterator(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "array");
        return new ArrayIterator<T>(array);
    }
}
