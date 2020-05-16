// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.internal.CollectionToArray;
import kotlin.jvm.internal.ArrayIteratorKt;
import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.Collection;

final class ArrayAsCollection<T> implements Collection<T>, KMappedMarker
{
    private final boolean isVarargs;
    private final T[] values;
    
    public ArrayAsCollection(final T[] values, final boolean isVarargs) {
        Intrinsics.checkParameterIsNotNull(values, "values");
        this.values = values;
        this.isVarargs = isVarargs;
    }
    
    @Override
    public boolean add(final T t) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean contains(final Object o) {
        return ArraysKt.contains(this.values, o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        Intrinsics.checkParameterIsNotNull(collection, "elements");
        final boolean empty = collection.isEmpty();
        final boolean b = true;
        boolean b2;
        if (empty) {
            b2 = b;
        }
        else {
            final Iterator<Object> iterator = collection.iterator();
            do {
                b2 = b;
                if (iterator.hasNext()) {
                    continue;
                }
                return b2;
            } while (this.contains(iterator.next()));
            b2 = false;
        }
        return b2;
    }
    
    public int getSize() {
        return this.values.length;
    }
    
    @Override
    public boolean isEmpty() {
        return this.values.length == 0;
    }
    
    @Override
    public Iterator<T> iterator() {
        return ArrayIteratorKt.iterator(this.values);
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public final /* bridge */ int size() {
        return this.getSize();
    }
    
    @Override
    public final Object[] toArray() {
        return CollectionsKt__CollectionsJVMKt.copyToArrayOfAny(this.values, this.isVarargs);
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return (T[])CollectionToArray.toArray(this, array);
    }
}
