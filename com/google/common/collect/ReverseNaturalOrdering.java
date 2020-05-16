// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.io.Serializable;

final class ReverseNaturalOrdering extends Ordering<Comparable> implements Serializable
{
    static final ReverseNaturalOrdering INSTANCE;
    private static final long serialVersionUID = 0L;
    
    static {
        INSTANCE = new ReverseNaturalOrdering();
    }
    
    private ReverseNaturalOrdering() {
    }
    
    private Object readResolve() {
        return ReverseNaturalOrdering.INSTANCE;
    }
    
    @Override
    public int compare(final Comparable comparable, final Comparable comparable2) {
        Preconditions.checkNotNull(comparable);
        if (comparable == comparable2) {
            return 0;
        }
        return comparable2.compareTo(comparable);
    }
    
    @Override
    public <S extends Comparable> Ordering<S> reverse() {
        return Ordering.natural();
    }
    
    @Override
    public String toString() {
        return "Ordering.natural().reverse()";
    }
}
