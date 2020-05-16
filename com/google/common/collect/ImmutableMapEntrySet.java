// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.Map;

abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Map.Entry<K, V>>
{
    @Override
    public boolean contains(final Object o) {
        final boolean b = o instanceof Map.Entry;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final Map.Entry entry = (Map.Entry)o;
            final V value = this.map().get(entry.getKey());
            b3 = b2;
            if (value != null) {
                b3 = b2;
                if (value.equals(entry.getValue())) {
                    b3 = true;
                }
            }
        }
        return b3;
    }
    
    @Override
    public int hashCode() {
        return this.map().hashCode();
    }
    
    @Override
    boolean isHashCodeFast() {
        return this.map().isHashCodeFast();
    }
    
    @Override
    boolean isPartialView() {
        return this.map().isPartialView();
    }
    
    abstract ImmutableMap<K, V> map();
    
    @Override
    public int size() {
        return this.map().size();
    }
    
    @Override
    Object writeReplace() {
        return new EntrySetSerializedForm(this.map());
    }
    
    private static class EntrySetSerializedForm<K, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final ImmutableMap<K, V> map;
        
        EntrySetSerializedForm(final ImmutableMap<K, V> map) {
            this.map = map;
        }
        
        Object readResolve() {
            return this.map.entrySet();
        }
    }
}
