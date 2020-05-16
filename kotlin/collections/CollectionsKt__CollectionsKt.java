// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import java.util.Collection;

class CollectionsKt__CollectionsKt extends CollectionsKt__CollectionsJVMKt
{
    public static final <T> Collection<T> asCollection(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "$this$asCollection");
        return new ArrayAsCollection<T>(array, false);
    }
    
    public static <T> List<T> emptyList() {
        return (List<T>)EmptyList.INSTANCE;
    }
    
    public static final <T> int getLastIndex(final List<? extends T> list) {
        Intrinsics.checkParameterIsNotNull(list, "$this$lastIndex");
        return list.size() - 1;
    }
    
    public static <T> List<T> listOf(final T... array) {
        Intrinsics.checkParameterIsNotNull(array, "elements");
        List<T> list;
        if (array.length > 0) {
            list = ArraysKt___ArraysJvmKt.asList(array);
        }
        else {
            list = CollectionsKt.emptyList();
        }
        return list;
    }
    
    public static <T> List<T> optimizeReadOnlyList(List<? extends T> o) {
        Intrinsics.checkParameterIsNotNull(o, "$this$optimizeReadOnlyList");
        final int size = ((List)o).size();
        if (size != 0) {
            if (size == 1) {
                o = CollectionsKt.listOf(((List<T>)o).get(0));
            }
        }
        else {
            o = CollectionsKt.emptyList();
        }
        return (List<T>)o;
    }
    
    public static final void throwIndexOverflow() {
        throw new ArithmeticException("Index overflow has happened.");
    }
}
