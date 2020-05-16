// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Collections;
import kotlin.jvm.internal.Intrinsics;
import java.util.Map;
import kotlin.Pair;

class MapsKt__MapsJVMKt extends MapsKt__MapWithDefaultKt
{
    public static <K, V> Map<K, V> mapOf(final Pair<? extends K, ? extends V> pair) {
        Intrinsics.checkParameterIsNotNull(pair, "pair");
        final Map<? extends K, ? extends V> singletonMap = Collections.singletonMap(pair.getFirst(), pair.getSecond());
        Intrinsics.checkExpressionValueIsNotNull(singletonMap, "java.util.Collections.si\u2026(pair.first, pair.second)");
        return (Map<K, V>)singletonMap;
    }
    
    public static final <K, V> Map<K, V> toSingletonMap(final Map<? extends K, ? extends V> map) {
        Intrinsics.checkParameterIsNotNull(map, "$this$toSingletonMap");
        final Map.Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
        final Map<K, V> singletonMap = Collections.singletonMap((K)entry.getKey(), (V)entry.getValue());
        Intrinsics.checkExpressionValueIsNotNull(singletonMap, "java.util.Collections.singletonMap(key, value)");
        Intrinsics.checkExpressionValueIsNotNull(singletonMap, "with(entries.iterator().\u2026ingletonMap(key, value) }");
        return (Map<K, V>)singletonMap;
    }
}
