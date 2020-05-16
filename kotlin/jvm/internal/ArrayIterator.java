// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import java.util.NoSuchElementException;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.Iterator;

final class ArrayIterator<T> implements Iterator<T>, KMappedMarker
{
    private final T[] array;
    private int index;
    
    public ArrayIterator(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "array");
        this.array = array;
    }
    
    @Override
    public boolean hasNext() {
        return this.index < this.array.length;
    }
    
    @Override
    public T next() {
        try {
            return this.array[this.index++];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            --this.index;
            throw new NoSuchElementException(ex.getMessage());
        }
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
