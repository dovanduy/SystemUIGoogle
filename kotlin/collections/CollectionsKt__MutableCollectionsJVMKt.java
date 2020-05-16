// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Collections;
import kotlin.jvm.internal.Intrinsics;
import java.util.Comparator;
import java.util.List;

class CollectionsKt__MutableCollectionsJVMKt extends CollectionsKt__IteratorsKt
{
    public static <T> void sortWith(final List<T> list, final Comparator<? super T> c) {
        Intrinsics.checkParameterIsNotNull(list, "$this$sortWith");
        Intrinsics.checkParameterIsNotNull(c, "comparator");
        if (list.size() > 1) {
            Collections.sort(list, c);
        }
    }
}
