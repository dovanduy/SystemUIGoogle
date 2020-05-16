// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;
import kotlin.jvm.internal.Intrinsics;
import java.util.Set;

class SetsKt___SetsKt extends SetsKt__SetsKt
{
    public static <T> Set<T> minus(final Set<? extends T> c, final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(c, "$this$minus");
        Intrinsics.checkParameterIsNotNull(iterable, "elements");
        final Collection<?> convertToSetForSetOperationWith = CollectionsKt__IterablesKt.convertToSetForSetOperationWith((Iterable<?>)iterable, (Iterable<?>)c);
        if (convertToSetForSetOperationWith.isEmpty()) {
            return CollectionsKt.toSet((Iterable<? extends T>)c);
        }
        if (convertToSetForSetOperationWith instanceof Set) {
            final LinkedHashSet<Object> set = new LinkedHashSet<Object>();
            for (final T next : c) {
                if (!convertToSetForSetOperationWith.contains(next)) {
                    set.add(next);
                }
            }
            return (Set<T>)set;
        }
        final LinkedHashSet<T> set2 = new LinkedHashSet<T>(c);
        set2.removeAll(convertToSetForSetOperationWith);
        return set2;
    }
}
