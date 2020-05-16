// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import java.util.Map;

class MapsKt___MapsKt extends MapsKt__MapsKt
{
    public static <K, V> Sequence<Map.Entry<K, V>> asSequence(final Map<? extends K, ? extends V> map) {
        Intrinsics.checkParameterIsNotNull(map, "$this$asSequence");
        return CollectionsKt.asSequence((Iterable<? extends Map.Entry<K, V>>)map.entrySet());
    }
}
