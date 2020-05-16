// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Set;
import java.util.Map;

public interface BiMap<K, V> extends Map<K, V>
{
    Set<V> values();
}
