// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.io.Serializable;

final class ComparatorOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final Comparator<T> comparator;
    
    ComparatorOrdering(final Comparator<T> comparator) {
        Preconditions.checkNotNull(comparator);
        this.comparator = comparator;
    }
    
    @Override
    public int compare(final T t, final T t2) {
        return this.comparator.compare(t, t2);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof ComparatorOrdering && this.comparator.equals(((ComparatorOrdering)o).comparator));
    }
    
    @Override
    public int hashCode() {
        return this.comparator.hashCode();
    }
    
    @Override
    public String toString() {
        return this.comparator.toString();
    }
}
