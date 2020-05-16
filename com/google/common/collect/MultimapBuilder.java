// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.Set;
import com.google.common.base.Supplier;
import java.util.Collection;
import java.util.Map;

public abstract class MultimapBuilder<K0, V0>
{
    private MultimapBuilder() {
    }
    
    public static MultimapBuilderWithKeys<Object> hashKeys() {
        return hashKeys(8);
    }
    
    public static MultimapBuilderWithKeys<Object> hashKeys(final int n) {
        CollectPreconditions.checkNonnegative(n, "expectedKeys");
        return (MultimapBuilderWithKeys<Object>)new MultimapBuilderWithKeys<Object>() {
            @Override
             <K, V> Map<K, Collection<V>> createMap() {
                return Platform.newHashMapWithExpectedSize(n);
            }
        };
    }
    
    private static final class LinkedHashSetSupplier<V> implements Supplier<Set<V>>, Serializable
    {
        private final int expectedValuesPerKey;
        
        LinkedHashSetSupplier(final int expectedValuesPerKey) {
            CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
            this.expectedValuesPerKey = expectedValuesPerKey;
        }
        
        @Override
        public Set<V> get() {
            return Platform.newLinkedHashSetWithExpectedSize(this.expectedValuesPerKey);
        }
    }
    
    public abstract static class MultimapBuilderWithKeys<K0>
    {
        MultimapBuilderWithKeys() {
        }
        
        abstract <K extends K0, V> Map<K, Collection<V>> createMap();
        
        public SetMultimapBuilder<K0, Object> linkedHashSetValues() {
            return this.linkedHashSetValues(2);
        }
        
        public SetMultimapBuilder<K0, Object> linkedHashSetValues(final int n) {
            CollectPreconditions.checkNonnegative(n, "expectedValuesPerKey");
            return (SetMultimapBuilder<K0, Object>)new SetMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> SetMultimap<K, V> build() {
                    return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), (Supplier<? extends Set<V>>)new LinkedHashSetSupplier(n));
                }
            };
        }
    }
    
    public abstract static class SetMultimapBuilder<K0, V0> extends MultimapBuilder<K0, V0>
    {
        SetMultimapBuilder() {
            super(null);
        }
        
        public abstract <K extends K0, V extends V0> SetMultimap<K, V> build();
    }
}
