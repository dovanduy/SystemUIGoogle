// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Collection;
import java.util.Map;

public interface Multimap<K, V>
{
    Map<K, Collection<V>> asMap();
    
    void clear();
    
    boolean containsEntry(@CompatibleWith("K") final Object p0, @CompatibleWith("V") final Object p1);
    
    @CanIgnoreReturnValue
    boolean remove(@CompatibleWith("K") final Object p0, @CompatibleWith("V") final Object p1);
    
    int size();
}
