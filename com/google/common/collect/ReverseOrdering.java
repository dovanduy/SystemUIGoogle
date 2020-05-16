// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.io.Serializable;

final class ReverseOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final Ordering<? super T> forwardOrder;
    
    ReverseOrdering(final Ordering<? super T> ordering) {
        Preconditions.checkNotNull(ordering);
        this.forwardOrder = ordering;
    }
    
    @Override
    public int compare(final T t, final T t2) {
        return this.forwardOrder.compare((Object)t2, (Object)t);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof ReverseOrdering && this.forwardOrder.equals(((ReverseOrdering)o).forwardOrder));
    }
    
    @Override
    public int hashCode() {
        return -this.forwardOrder.hashCode();
    }
    
    @Override
    public <S extends T> Ordering<S> reverse() {
        return (Ordering<S>)this.forwardOrder;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.forwardOrder);
        sb.append(".reverse()");
        return sb.toString();
    }
}
