// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Set;
import java.util.Map;
import java.lang.reflect.Array;
import java.util.Arrays;

final class Platform
{
    static <T> T[] copy(final Object[] original, final int from, final int to, final T[] array) {
        return Arrays.copyOfRange(original, from, to, (Class<? extends T[]>)array.getClass());
    }
    
    static <T> T[] newArray(final T[] array, final int length) {
        return (T[])Array.newInstance(array.getClass().getComponentType(), length);
    }
    
    static <K, V> Map<K, V> newHashMapWithExpectedSize(final int n) {
        return (Map<K, V>)CompactHashMap.createWithExpectedSize(n);
    }
    
    static <K, V> Map<K, V> newLinkedHashMapWithExpectedSize(final int n) {
        return (Map<K, V>)CompactLinkedHashMap.createWithExpectedSize(n);
    }
    
    static <E> Set<E> newLinkedHashSetWithExpectedSize(final int n) {
        return (Set<E>)CompactLinkedHashSet.createWithExpectedSize(n);
    }
    
    static MapMaker tryWeakKeys(final MapMaker mapMaker) {
        mapMaker.weakKeys();
        return mapMaker;
    }
}
