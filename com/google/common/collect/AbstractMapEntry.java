// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Objects;
import java.util.Map;

abstract class AbstractMapEntry<K, V> implements Entry<K, V>
{
    @Override
    public boolean equals(final Object o) {
        final boolean b = o instanceof Entry;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final Entry entry = (Entry)o;
            b3 = b2;
            if (Objects.equal(this.getKey(), entry.getKey())) {
                b3 = b2;
                if (Objects.equal(this.getValue(), entry.getValue())) {
                    b3 = true;
                }
            }
        }
        return b3;
    }
    
    @Override
    public abstract K getKey();
    
    @Override
    public abstract V getValue();
    
    @Override
    public int hashCode() {
        final Object key = this.getKey();
        final Object value = this.getValue();
        int hashCode = 0;
        int hashCode2;
        if (key == null) {
            hashCode2 = 0;
        }
        else {
            hashCode2 = key.hashCode();
        }
        if (value != null) {
            hashCode = value.hashCode();
        }
        return hashCode2 ^ hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getKey());
        sb.append("=");
        sb.append(this.getValue());
        return sb.toString();
    }
}
