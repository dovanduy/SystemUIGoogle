// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ConcurrentMap;

public abstract class ForwardingConcurrentMap<K, V> extends ForwardingMap<K, V> implements ConcurrentMap<K, V>
{
    protected ForwardingConcurrentMap() {
    }
    
    @Override
    protected abstract ConcurrentMap<K, V> delegate();
    
    @CanIgnoreReturnValue
    @Override
    public V putIfAbsent(final K k, final V v) {
        return this.delegate().putIfAbsent(k, v);
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean remove(final Object o, final Object o2) {
        return this.delegate().remove(o, o2);
    }
    
    @CanIgnoreReturnValue
    @Override
    public V replace(final K k, final V v) {
        return this.delegate().replace(k, v);
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean replace(final K k, final V v, final V v2) {
        return this.delegate().replace(k, v, v2);
    }
}
