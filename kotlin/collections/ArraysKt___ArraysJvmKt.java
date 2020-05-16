// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Arrays;
import java.util.Comparator;
import kotlin.jvm.internal.Intrinsics;
import java.util.List;

class ArraysKt___ArraysJvmKt extends ArraysKt__ArraysKt
{
    public static final <T> List<T> asList(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "$this$asList");
        final List<T> list = ArraysUtilJVM.asList(array);
        Intrinsics.checkExpressionValueIsNotNull(list, "ArraysUtilJVM.asList(this)");
        return list;
    }
    
    public static final <T> void sortWith(final T[] a, final Comparator<? super T> c) {
        Intrinsics.checkParameterIsNotNull(a, "$this$sortWith");
        Intrinsics.checkParameterIsNotNull(c, "comparator");
        if (a.length > 1) {
            Arrays.sort(a, c);
        }
    }
}
