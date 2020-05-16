// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E>
{
    static final RegularImmutableSortedSet<Comparable> NATURAL_EMPTY_SET;
    final transient ImmutableList<E> elements;
    
    static {
        NATURAL_EMPTY_SET = new RegularImmutableSortedSet<Comparable>((ImmutableList<Comparable>)ImmutableList.of(), Ordering.natural());
    }
    
    RegularImmutableSortedSet(final ImmutableList<E> elements, final Comparator<? super E> comparator) {
        super(comparator);
        this.elements = elements;
    }
    
    private int unsafeBinarySearch(final Object key) throws ClassCastException {
        return Collections.binarySearch(this.elements, key, this.unsafeComparator());
    }
    
    @Override
    public ImmutableList<E> asList() {
        return this.elements;
    }
    
    @Override
    public E ceiling(final E e) {
        final int tailIndex = this.tailIndex(e, true);
        Object value;
        if (tailIndex == this.size()) {
            value = null;
        }
        else {
            value = this.elements.get(tailIndex);
        }
        return (E)value;
    }
    
    @Override
    public boolean contains(final Object o) {
        boolean b2;
        final boolean b = b2 = false;
        if (o == null) {
            return b2;
        }
        try {
            final int unsafeBinarySearch = this.unsafeBinarySearch(o);
            b2 = b;
            if (unsafeBinarySearch >= 0) {
                b2 = true;
            }
            return b2;
        }
        catch (ClassCastException ex) {
            b2 = b;
            return b2;
        }
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        Object elementSet = collection;
        if (collection instanceof Multiset) {
            elementSet = ((Multiset<?>)collection).elementSet();
        }
        Label_0151: {
            if (!SortedIterables.hasSameComparator(this.comparator(), (Iterable<?>)elementSet) || ((Collection)elementSet).size() <= 1) {
                break Label_0151;
            }
            final UnmodifiableIterator<Object> iterator = (UnmodifiableIterator<Object>)this.iterator();
            final Iterator<?> iterator2 = ((Collection<?>)elementSet).iterator();
            if (!iterator.hasNext()) {
                return false;
            }
            Object o = iterator2.next();
            Object o2 = iterator.next();
            try {
                while (true) {
                    final int unsafeCompare = this.unsafeCompare(o2, o);
                    if (unsafeCompare < 0) {
                        if (!iterator.hasNext()) {
                            return false;
                        }
                        o2 = iterator.next();
                    }
                    else if (unsafeCompare == 0) {
                        if (!iterator2.hasNext()) {
                            return true;
                        }
                        o = iterator2.next();
                    }
                    else {
                        if (unsafeCompare > 0) {
                            return false;
                        }
                        continue;
                    }
                }
                return super.containsAll((Collection<?>)elementSet);
            }
            catch (NullPointerException | ClassCastException ex) {
                return false;
            }
        }
    }
    
    @Override
    int copyIntoArray(final Object[] array, final int n) {
        return this.elements.copyIntoArray(array, n);
    }
    
    @Override
    ImmutableSortedSet<E> createDescendingSet() {
        final Comparator<? super E> reverseOrder = Collections.reverseOrder((Comparator<? super E>)super.comparator);
        RegularImmutableSortedSet<Object> emptySet;
        if (this.isEmpty()) {
            emptySet = ImmutableSortedSet.emptySet((Comparator<? super Object>)reverseOrder);
        }
        else {
            emptySet = new RegularImmutableSortedSet<Object>((ImmutableList<Object>)this.elements.reverse(), (Comparator<? super Object>)reverseOrder);
        }
        return (ImmutableSortedSet<E>)emptySet;
    }
    
    @Override
    public UnmodifiableIterator<E> descendingIterator() {
        return this.elements.reverse().iterator();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Set set = (Set)o;
        if (this.size() != set.size()) {
            return false;
        }
        if (this.isEmpty()) {
            return true;
        }
        if (SortedIterables.hasSameComparator(super.comparator, set)) {
            final Iterator<?> iterator = set.iterator();
            try {
                for (final Object next : this) {
                    final Object next2 = iterator.next();
                    if (next2 == null || this.unsafeCompare(next, next2) != 0) {
                        return false;
                    }
                }
                return true;
            }
            catch (ClassCastException | NoSuchElementException ex) {
                return false;
            }
        }
        return this.containsAll(set);
    }
    
    @Override
    public E first() {
        if (!this.isEmpty()) {
            return this.elements.get(0);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public E floor(final E e) {
        final int n = this.headIndex(e, true) - 1;
        Object value;
        if (n == -1) {
            value = null;
        }
        else {
            value = this.elements.get(n);
        }
        return (E)value;
    }
    
    RegularImmutableSortedSet<E> getSubSet(final int n, final int n2) {
        if (n == 0 && n2 == this.size()) {
            return this;
        }
        if (n < n2) {
            return new RegularImmutableSortedSet<E>(this.elements.subList(n, n2), super.comparator);
        }
        return ImmutableSortedSet.emptySet((Comparator<? super E>)super.comparator);
    }
    
    int headIndex(final E key, final boolean b) {
        final ImmutableList<E> elements = this.elements;
        Preconditions.checkNotNull(key);
        final int binarySearch = Collections.binarySearch((List<? extends E>)elements, key, this.comparator());
        if (binarySearch >= 0) {
            int n = binarySearch;
            if (b) {
                n = binarySearch + 1;
            }
            return n;
        }
        return binarySearch;
    }
    
    @Override
    ImmutableSortedSet<E> headSetImpl(final E e, final boolean b) {
        return this.getSubSet(0, this.headIndex(e, b));
    }
    
    @Override
    public E higher(final E e) {
        final int tailIndex = this.tailIndex(e, false);
        Object value;
        if (tailIndex == this.size()) {
            value = null;
        }
        else {
            value = this.elements.get(tailIndex);
        }
        return (E)value;
    }
    
    int indexOf(final Object key) {
        int n = -1;
        if (key == null) {
            return -1;
        }
        try {
            final int binarySearch = Collections.binarySearch(this.elements, key, this.unsafeComparator());
            if (binarySearch >= 0) {
                n = binarySearch;
            }
            return n;
        }
        catch (ClassCastException ex) {
            return n;
        }
    }
    
    @Override
    Object[] internalArray() {
        return this.elements.internalArray();
    }
    
    @Override
    int internalArrayEnd() {
        return this.elements.internalArrayEnd();
    }
    
    @Override
    int internalArrayStart() {
        return this.elements.internalArrayStart();
    }
    
    @Override
    boolean isPartialView() {
        return this.elements.isPartialView();
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.elements.iterator();
    }
    
    @Override
    public E last() {
        if (!this.isEmpty()) {
            return this.elements.get(this.size() - 1);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public E lower(final E e) {
        final int n = this.headIndex(e, false) - 1;
        Object value;
        if (n == -1) {
            value = null;
        }
        else {
            value = this.elements.get(n);
        }
        return (E)value;
    }
    
    @Override
    public int size() {
        return this.elements.size();
    }
    
    @Override
    ImmutableSortedSet<E> subSetImpl(final E e, final boolean b, final E e2, final boolean b2) {
        return this.tailSetImpl(e, b).headSetImpl(e2, b2);
    }
    
    int tailIndex(final E key, final boolean b) {
        final ImmutableList<E> elements = this.elements;
        Preconditions.checkNotNull(key);
        int binarySearch = Collections.binarySearch((List<? extends E>)elements, key, this.comparator());
        if (binarySearch >= 0) {
            if (!b) {
                ++binarySearch;
            }
            return binarySearch;
        }
        return binarySearch;
    }
    
    @Override
    ImmutableSortedSet<E> tailSetImpl(final E e, final boolean b) {
        return this.getSubSet(this.tailIndex(e, b), this.size());
    }
    
    Comparator<Object> unsafeComparator() {
        return (Comparator<Object>)super.comparator;
    }
}
