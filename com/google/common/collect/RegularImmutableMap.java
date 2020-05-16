// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Arrays;
import com.google.common.base.Preconditions;

final class RegularImmutableMap<K, V> extends ImmutableMap<K, V>
{
    static final ImmutableMap<Object, Object> EMPTY;
    private static final long serialVersionUID = 0L;
    final transient Object[] alternatingKeysAndValues;
    private final transient int[] hashTable;
    private final transient int size;
    
    static {
        EMPTY = new RegularImmutableMap<Object, Object>(null, new Object[0], 0);
    }
    
    private RegularImmutableMap(final int[] hashTable, final Object[] alternatingKeysAndValues, final int size) {
        this.hashTable = hashTable;
        this.alternatingKeysAndValues = alternatingKeysAndValues;
        this.size = size;
    }
    
    static <K, V> RegularImmutableMap<K, V> create(final int n, final Object[] array) {
        if (n == 0) {
            return (RegularImmutableMap<K, V>)(RegularImmutableMap)RegularImmutableMap.EMPTY;
        }
        if (n == 1) {
            CollectPreconditions.checkEntryNotNull(array[0], array[1]);
            return new RegularImmutableMap<K, V>(null, array, 1);
        }
        Preconditions.checkPositionIndex(n, array.length >> 1);
        return new RegularImmutableMap<K, V>(createHashTable(array, n, ImmutableSet.chooseTableSize(n), 0), array, n);
    }
    
    static int[] createHashTable(final Object[] array, final int n, final int n2, final int n3) {
        if (n == 1) {
            CollectPreconditions.checkEntryNotNull(array[n3], array[n3 ^ 0x1]);
            return null;
        }
        final int[] a = new int[n2];
        Arrays.fill(a, -1);
        for (int i = 0; i < n; ++i) {
            final int n4 = i * 2;
            final int n5 = n4 + n3;
            final Object o = array[n5];
            final Object obj = array[n4 + (n3 ^ 0x1)];
            CollectPreconditions.checkEntryNotNull(o, obj);
            int smear = Hashing.smear(o.hashCode());
            while (true) {
                final int n6 = smear & n2 - 1;
                final int n7 = a[n6];
                if (n7 == -1) {
                    a[n6] = n5;
                    break;
                }
                if (array[n7].equals(o)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Multiple entries with same key: ");
                    sb.append(o);
                    sb.append("=");
                    sb.append(obj);
                    sb.append(" and ");
                    sb.append(array[n7]);
                    sb.append("=");
                    sb.append(array[n7 ^ 0x1]);
                    throw new IllegalArgumentException(sb.toString());
                }
                smear = n6 + 1;
            }
        }
        return a;
    }
    
    static Object get(final int[] array, final Object[] array2, int smear, int length, final Object o) {
        final Object o2 = null;
        if (o == null) {
            return null;
        }
        if (smear == 1) {
            Object o3 = o2;
            if (array2[length].equals(o)) {
                o3 = array2[length ^ 0x1];
            }
            return o3;
        }
        if (array == null) {
            return null;
        }
        length = array.length;
        smear = Hashing.smear(o.hashCode());
        while (true) {
            final int n = smear & length - 1;
            smear = array[n];
            if (smear == -1) {
                return null;
            }
            if (array2[smear].equals(o)) {
                return array2[smear ^ 0x1];
            }
            smear = n + 1;
        }
    }
    
    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        return (ImmutableSet<Entry<K, V>>)new EntrySet((ImmutableMap<Object, Object>)this, this.alternatingKeysAndValues, 0, this.size);
    }
    
    @Override
    ImmutableSet<K> createKeySet() {
        return new KeySet<K>(this, (ImmutableList<K>)new KeysOrValuesAsList(this.alternatingKeysAndValues, 0, this.size));
    }
    
    @Override
    ImmutableCollection<V> createValues() {
        return (ImmutableCollection<V>)new KeysOrValuesAsList(this.alternatingKeysAndValues, 1, this.size);
    }
    
    @Override
    public V get(final Object o) {
        return (V)get(this.hashTable, this.alternatingKeysAndValues, this.size, 0, o);
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    static class EntrySet<K, V> extends ImmutableSet<Entry<K, V>>
    {
        private final transient Object[] alternatingKeysAndValues;
        private final transient int keyOffset;
        private final transient ImmutableMap<K, V> map;
        private final transient int size;
        
        EntrySet(final ImmutableMap<K, V> map, final Object[] alternatingKeysAndValues, final int keyOffset, final int size) {
            this.map = map;
            this.alternatingKeysAndValues = alternatingKeysAndValues;
            this.keyOffset = keyOffset;
            this.size = size;
        }
        
        @Override
        public boolean contains(Object key) {
            final boolean b = key instanceof Entry;
            boolean b3;
            final boolean b2 = b3 = false;
            if (b) {
                final Entry entry = (Entry)key;
                key = entry.getKey();
                final Object value = entry.getValue();
                b3 = b2;
                if (value != null) {
                    b3 = b2;
                    if (value.equals(this.map.get(key))) {
                        b3 = true;
                    }
                }
            }
            return b3;
        }
        
        @Override
        int copyIntoArray(final Object[] array, final int n) {
            return this.asList().copyIntoArray(array, n);
        }
        
        @Override
        ImmutableList<Entry<K, V>> createAsList() {
            return new ImmutableList<Entry<K, V>>() {
                @Override
                public Entry<K, V> get(int n) {
                    Preconditions.checkElementIndex(n, EntrySet.this.size);
                    final Object[] access$100 = EntrySet.this.alternatingKeysAndValues;
                    n *= 2;
                    return (Entry<K, V>)new AbstractMap.SimpleImmutableEntry(access$100[EntrySet.this.keyOffset + n], EntrySet.this.alternatingKeysAndValues[n + (EntrySet.this.keyOffset ^ 0x1)]);
                }
                
                public boolean isPartialView() {
                    return true;
                }
                
                @Override
                public int size() {
                    return EntrySet.this.size;
                }
            };
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public UnmodifiableIterator<Entry<K, V>> iterator() {
            return this.asList().iterator();
        }
        
        @Override
        public int size() {
            return this.size;
        }
    }
    
    static final class KeySet<K> extends ImmutableSet<K>
    {
        private final transient ImmutableList<K> list;
        private final transient ImmutableMap<K, ?> map;
        
        KeySet(final ImmutableMap<K, ?> map, final ImmutableList<K> list) {
            this.map = map;
            this.list = list;
        }
        
        @Override
        public ImmutableList<K> asList() {
            return this.list;
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map.get(o) != null;
        }
        
        @Override
        int copyIntoArray(final Object[] array, final int n) {
            return this.asList().copyIntoArray(array, n);
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public UnmodifiableIterator<K> iterator() {
            return this.asList().iterator();
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
    }
    
    static final class KeysOrValuesAsList extends ImmutableList<Object>
    {
        private final transient Object[] alternatingKeysAndValues;
        private final transient int offset;
        private final transient int size;
        
        KeysOrValuesAsList(final Object[] alternatingKeysAndValues, final int offset, final int size) {
            this.alternatingKeysAndValues = alternatingKeysAndValues;
            this.offset = offset;
            this.size = size;
        }
        
        @Override
        public Object get(final int n) {
            Preconditions.checkElementIndex(n, this.size);
            return this.alternatingKeysAndValues[n * 2 + this.offset];
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public int size() {
            return this.size;
        }
    }
}
