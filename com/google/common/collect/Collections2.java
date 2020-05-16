// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Collection;

public final class Collections2
{
    static <T> Collection<T> cast(final Iterable<T> iterable) {
        return (Collection<T>)iterable;
    }
    
    static StringBuilder newStringBuilderForCollection(final int n) {
        CollectPreconditions.checkNonnegative(n, "size");
        return new StringBuilder((int)Math.min(n * 8L, 1073741824L));
    }
    
    static boolean safeContains(final Collection<?> collection, final Object o) {
        Preconditions.checkNotNull(collection);
        try {
            return collection.contains(o);
        }
        catch (ClassCastException | NullPointerException ex) {
            return false;
        }
    }
}
