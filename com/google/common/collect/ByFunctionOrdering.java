// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Function;
import java.io.Serializable;

final class ByFunctionOrdering<F, T> extends Ordering<F> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final Function<F, ? extends T> function;
    final Ordering<T> ordering;
    
    ByFunctionOrdering(final Function<F, ? extends T> function, final Ordering<T> ordering) {
        Preconditions.checkNotNull(function);
        this.function = function;
        Preconditions.checkNotNull(ordering);
        this.ordering = ordering;
    }
    
    @Override
    public int compare(final F n, final F n2) {
        return this.ordering.compare((T)this.function.apply(n), (T)this.function.apply(n2));
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof ByFunctionOrdering) {
            final ByFunctionOrdering byFunctionOrdering = (ByFunctionOrdering)o;
            if (!this.function.equals(byFunctionOrdering.function) || !this.ordering.equals(byFunctionOrdering.ordering)) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.function, this.ordering);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.ordering);
        sb.append(".onResultOf(");
        sb.append(this.function);
        sb.append(")");
        return sb.toString();
    }
}
