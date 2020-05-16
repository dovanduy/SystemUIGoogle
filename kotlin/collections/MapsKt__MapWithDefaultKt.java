// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.functions.Function1;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import java.util.Map;

class MapsKt__MapWithDefaultKt
{
    public static final <K, V> V getOrImplicitDefaultNullable(final Map<K, ? extends V> map, final K obj) {
        Intrinsics.checkParameterIsNotNull(map, "$this$getOrImplicitDefault");
        if (map instanceof MapWithDefault) {
            return ((MapWithDefault<K, V>)map).getOrImplicitDefault(obj);
        }
        final Object value = map.get(obj);
        if (value == null && !map.containsKey(obj)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Key ");
            sb.append(obj);
            sb.append(" is missing in the map.");
            throw new NoSuchElementException(sb.toString());
        }
        return (V)value;
    }
    
    public static <K, V> Map<K, V> withDefault(final Map<K, ? extends V> map, final Function1<? super K, ? extends V> function1) {
        Intrinsics.checkParameterIsNotNull(map, "$this$withDefault");
        Intrinsics.checkParameterIsNotNull(function1, "defaultValue");
        Map<K, V> withDefault;
        if (map instanceof MapWithDefault) {
            withDefault = MapsKt.withDefault((Map<K, ? extends V>)((MapWithDefault<K, ? extends V>)map).getMap(), function1);
        }
        else {
            withDefault = new MapWithDefaultImpl<K, V>(map, function1);
        }
        return withDefault;
    }
}
