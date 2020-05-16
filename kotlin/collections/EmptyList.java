// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.internal.CollectionToArray;
import java.util.ListIterator;
import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import java.util.Collection;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.RandomAccess;
import java.io.Serializable;
import java.util.List;

public final class EmptyList implements List, Serializable, RandomAccess, KMappedMarker
{
    public static final EmptyList INSTANCE;
    private static final long serialVersionUID = -7390468764508069838L;
    
    static {
        INSTANCE = new EmptyList();
    }
    
    private EmptyList() {
    }
    
    private final Object readResolve() {
        return EmptyList.INSTANCE;
    }
    
    @Override
    public boolean addAll(final int n, final Collection collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
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
        return o instanceof List && ((List)o).isEmpty();
    }
    
    @Override
    public Void get(final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Empty list doesn't contain element at index ");
        sb.append(i);
        sb.append('.');
        throw new IndexOutOfBoundsException(sb.toString());
    }
    
    public int getSize() {
        return 0;
    }
    
    @Override
    public int hashCode() {
        return 1;
    }
    
    @Override
    public final /* bridge */ int indexOf(final Object o) {
        if (o instanceof Void) {
            return this.indexOf((Void)o);
        }
        return -1;
    }
    
    public int indexOf(final Void void1) {
        Intrinsics.checkParameterIsNotNull(void1, "element");
        return -1;
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
    public final /* bridge */ int lastIndexOf(final Object o) {
        if (o instanceof Void) {
            return this.lastIndexOf((Void)o);
        }
        return -1;
    }
    
    public int lastIndexOf(final Void void1) {
        Intrinsics.checkParameterIsNotNull(void1, "element");
        return -1;
    }
    
    @Override
    public ListIterator listIterator() {
        return EmptyIterator.INSTANCE;
    }
    
    @Override
    public ListIterator listIterator(final int i) {
        if (i == 0) {
            return EmptyIterator.INSTANCE;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Index: ");
        sb.append(i);
        throw new IndexOutOfBoundsException(sb.toString());
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
    public List subList(final int i, final int j) {
        if (i == 0 && j == 0) {
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("fromIndex: ");
        sb.append(i);
        sb.append(", toIndex: ");
        sb.append(j);
        throw new IndexOutOfBoundsException(sb.toString());
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
