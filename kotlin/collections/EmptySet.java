// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.internal.CollectionToArray;
import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import java.util.Collection;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.io.Serializable;
import java.util.Set;

public final class EmptySet implements Set, Serializable, KMappedMarker
{
    public static final EmptySet INSTANCE;
    private static final long serialVersionUID = 3406603774387020532L;
    
    static {
        INSTANCE = new EmptySet();
    }
    
    private EmptySet() {
    }
    
    private final Object readResolve() {
        return EmptySet.INSTANCE;
    }
    
    @Override
    public boolean addAll(final Collection collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public final /* bridge */ boolean contains(final Object o) {
        return o instanceof Void && this.contains((Void)o);
    }
    
    public boolean contains(final Void void1) {
        Intrinsics.checkParameterIsNotNull(void1, "element");
        return false;
    }
    
    @Override
    public boolean containsAll(final Collection collection) {
        Intrinsics.checkParameterIsNotNull(collection, "elements");
        return collection.isEmpty();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Set && ((Set)o).isEmpty();
    }
    
    public int getSize() {
        return 0;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public Iterator iterator() {
        return EmptyIterator.INSTANCE;
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean removeAll(final Collection collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean retainAll(final Collection collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public final /* bridge */ int size() {
        return this.getSize();
    }
    
    @Override
    public Object[] toArray() {
        return CollectionToArray.toArray(this);
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return (T[])CollectionToArray.toArray(this, array);
    }
    
    @Override
    public String toString() {
        return "[]";
    }
}
