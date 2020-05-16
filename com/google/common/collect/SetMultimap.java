// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Set;

public interface SetMultimap<K, V> extends Multimap<K, V>
{
    Set<V> get(final K p0);
}
