// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.internal.Intrinsics;
import java.util.Set;

class SetsKt__SetsKt extends SetsKt__SetsJVMKt
{
    public static <T> Set<T> emptySet() {
        return (Set<T>)EmptySet.INSTANCE;
    }
    
    public static final <T> Set<T> optimizeReadOnlySet(Set<? extends T> o) {
        Intrinsics.checkParameterIsNotNull(o, "$this$optimizeReadOnlySet");
        final int size = ((Set)o).size();
        if (size != 0) {
            if (size == 1) {
                o = SetsKt__SetsJVMKt.setOf(((Set<T>)o).iterator().next());
            }
        }
        else {
            o = SetsKt.emptySet();
        }
        return (Set<T>)o;
    }
    
    public static <T> Set<T> setOf(final T... array) {
        Intrinsics.checkParameterIsNotNull(array, "elements");
        Set<T> set;
        if (array.length > 0) {
            set = ArraysKt.toSet(array);
        }
        else {
            set = SetsKt.emptySet();
        }
        return set;
    }
}
