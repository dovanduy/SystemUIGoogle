// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.io.Serializable;

final class NaturalOrdering extends Ordering<Comparable> implements Serializable
{
    static final NaturalOrdering INSTANCE;
    private static final long serialVersionUID = 0L;
    
    static {
        INSTANCE = new NaturalOrdering();
    }
    
    private NaturalOrdering() {
    }
    
    private Object readResolve() {
        return NaturalOrdering.INSTANCE;
    }
    
    @Override
    public int compare(final Comparable comparable, final Comparable comparable2) {
        Preconditions.checkNotNull(comparable);
        Preconditions.checkNotNull(comparable2);
        return comparable.compareTo(comparable2);
    }
    
    @Override
    public <S extends Comparable> Ordering<S> reverse() {
        return (Ordering<S>)ReverseNaturalOrdering.INSTANCE;
    }
    
    @Override
    public String toString() {
        return "Ordering.natural()";
    }
}
