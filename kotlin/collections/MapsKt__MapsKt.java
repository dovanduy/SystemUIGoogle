// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Iterator;
import kotlin.sequences.Sequence;
import java.util.LinkedHashMap;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.TypeCastException;
import java.util.Map;

class MapsKt__MapsKt extends MapsKt__MapsJVMKt
{
    public static <K, V> Map<K, V> emptyMap() {
        final EmptyMap instance = EmptyMap.INSTANCE;
        if (instance != null) {
            return (Map<K, V>)instance;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Map<K, V>");
    }
    
    public static <K, V> V getValue(final Map<K, ? extends V> map, final K k) {
        Intrinsics.checkParameterIsNotNull(map, "$this$getValue");
        return MapsKt__MapWithDefaultKt.getOrImplicitDefaultNullable(map, k);
    }
    
    public static int mapCapacity(final int n) {
        if (n < 3) {
            return n + 1;
        }
        if (n < 1073741824) {
            return n + n / 3;
        }
        return Integer.MAX_VALUE;
    }
    
    public static <K, V> Map<K, V> mapOf(final Pair<? extends K, ? extends V>... array) {
        Intrinsics.checkParameterIsNotNull(array, "pairs");
        Map<K, V> emptyMap;
        if (((Pair<? extends K, ? extends V>[])array).length > 0) {
            final LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<K, V>(MapsKt.mapCapacity(((Pair<? extends K, ? extends V>[])array).length));
            toMap((Pair<?, ?>[])array, linkedHashMap);
            emptyMap = linkedHashMap;
        }
        else {
            emptyMap = MapsKt.emptyMap();
        }
        return emptyMap;
    }
    
    public static final <K, V> Map<K, V> optimizeReadOnlyMap(Map<K, ? extends V> map) {
        Intrinsics.checkParameterIsNotNull(map, "$this$optimizeReadOnlyMap");
        final int size = map.size();
        if (size != 0) {
            if (size == 1) {
                map = (Map<? extends K, ? extends V>)MapsKt__MapsJVMKt.toSingletonMap((Map<?, ?>)map);
            }
        }
        else {
            map = (Map<? extends K, ? extends V>)MapsKt.emptyMap();
        }
        return (Map<K, V>)map;
    }
    
    public static final <K, V> void putAll(final Map<? super K, ? super V> map, final Sequence<? extends Pair<? extends K, ? extends V>> sequence) {
        Intrinsics.checkParameterIsNotNull(map, "$this$putAll");
        Intrinsics.checkParameterIsNotNull(sequence, "pairs");
        for (final Pair<? super K, B> pair : sequence) {
            map.put(pair.component1(), (Object)pair.component2());
        }
    }
    
    public static final <K, V> void putAll(final Map<? super K, ? super V> map, final Pair<? extends K, ? extends V>[] array) {
        Intrinsics.checkParameterIsNotNull(map, "$this$putAll");
        Intrinsics.checkParameterIsNotNull(array, "pairs");
        for (final Pair<? extends K, ? extends V> pair : array) {
            map.put((Object)pair.component1(), (Object)pair.component2());
        }
    }
    
    public static <K, V> Map<K, V> toMap(final Sequence<? extends Pair<? extends K, ? extends V>> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$toMap");
        final LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<K, V>();
        toMap((Sequence<? extends Pair<?, ?>>)sequence, (LinkedHashMap<K, ? extends V>)linkedHashMap);
        return optimizeReadOnlyMap((Map<K, ? extends V>)linkedHashMap);
    }
    
    public static final <K, V, M extends Map<? super K, ? super V>> M toMap(final Sequence<? extends Pair<? extends K, ? extends V>> sequence, final M m) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$toMap");
        Intrinsics.checkParameterIsNotNull(m, "destination");
        putAll((Map<? super Object, ? super Object>)m, (Sequence<? extends Pair<?, ?>>)sequence);
        return m;
    }
    
    public static final <K, V, M extends Map<? super K, ? super V>> M toMap(final Pair<? extends K, ? extends V>[] array, final M m) {
        Intrinsics.checkParameterIsNotNull(array, "$this$toMap");
        Intrinsics.checkParameterIsNotNull(m, "destination");
        putAll((Map<? super Object, ? super Object>)m, (Pair<?, ?>[])array);
        return m;
    }
    
    public static <K, V> Map<K, V> toMutableMap(final Map<? extends K, ? extends V> m) {
        Intrinsics.checkParameterIsNotNull(m, "$this$toMutableMap");
        return new LinkedHashMap<K, V>(m);
    }
}
