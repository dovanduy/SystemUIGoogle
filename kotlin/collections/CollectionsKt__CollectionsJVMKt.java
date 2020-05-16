// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;

class CollectionsKt__CollectionsJVMKt
{
    public static final <T> Object[] copyToArrayOfAny(T[] copy, final boolean b) {
        Intrinsics.checkParameterIsNotNull(copy, "$this$copyToArrayOfAny");
        if (!b || !Intrinsics.areEqual(copy.getClass(), Object[].class)) {
            copy = Arrays.copyOf(copy, copy.length, (Class<? extends T[]>)Object[].class);
            Intrinsics.checkExpressionValueIsNotNull(copy, "java.util.Arrays.copyOf(\u2026 Array<Any?>::class.java)");
        }
        return copy;
    }
    
    public static <T> List<T> listOf(final T o) {
        final List<T> singletonList = Collections.singletonList(o);
        Intrinsics.checkExpressionValueIsNotNull(singletonList, "java.util.Collections.singletonList(element)");
        return singletonList;
    }
}
