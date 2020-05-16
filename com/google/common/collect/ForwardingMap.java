// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Collection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;
import java.util.Map;

public abstract class ForwardingMap<K, V> extends ForwardingObject implements Map<K, V>
{
    protected ForwardingMap() {
    }
    
    @Override
    public void clear() {
        this.delegate().clear();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.delegate().containsKey(o);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.delegate().containsValue(o);
    }
    
    @Override
    protected abstract Map<K, V> delegate();
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.delegate().entrySet();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || this.delegate().equals(o);
    }
    
    @Override
    public V get(final Object o) {
        return this.delegate().get(o);
    }
    
    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }
    
    @Override
    public Set<K> keySet() {
        return this.delegate().keySet();
    }
    
    @CanIgnoreReturnValue
    @Override
    public V put(final K k, final V v) {
        return this.delegate().put(k, v);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        this.delegate().putAll(map);
    }
    
    @CanIgnoreReturnValue
    @Override
    public V remove(final Object o) {
        return this.delegate().remove(o);
    }
    
    @Override
    public int size() {
        return this.delegate().size();
    }
    
    protected String standardToString() {
        return Maps.toStringImpl(this);
    }
    
    @Override
    public Collection<V> values() {
        return this.delegate().values();
    }
}
