// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import kotlin.jvm.internal.Intrinsics;

class CollectionsKt__IterablesKt extends CollectionsKt__CollectionsKt
{
    public static <T> int collectionSizeOrDefault(final Iterable<? extends T> iterable, int size) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$collectionSizeOrDefault");
        if (iterable instanceof Collection) {
            size = ((Collection)iterable).size();
        }
        return size;
    }
    
    public static final <T> Collection<T> convertToSetForSetOperationWith(final Iterable<? extends T> iterable, final Iterable<? extends T> iterable2) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$convertToSetForSetOperationWith");
        Intrinsics.checkParameterIsNotNull(iterable2, "source");
        Object o;
        if (iterable instanceof Set) {
            o = iterable;
        }
        else if (iterable instanceof Collection) {
            if (iterable2 instanceof Collection && ((Collection)iterable2).size() < 2) {
                o = iterable;
            }
            else {
                final Collection<? extends T> collection = (Collection<? extends T>)iterable;
                if (safeToConvertToSet$CollectionsKt__IterablesKt((Collection<?>)collection)) {
                    o = CollectionsKt___CollectionsKt.toHashSet((Iterable<?>)iterable);
                }
                else {
                    o = collection;
                }
            }
        }
        else {
            o = CollectionsKt___CollectionsKt.toHashSet((Iterable<?>)iterable);
        }
        return (Collection<T>)o;
    }
    
    public static <T> List<T> flatten(final Iterable<? extends Iterable<? extends T>> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$flatten");
        final ArrayList<Object> list = new ArrayList<Object>();
        final Iterator<? extends Iterable<? extends T>> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            CollectionsKt.addAll((Collection<? super Object>)list, (Iterable<?>)iterator.next());
        }
        return (List<T>)list;
    }
    
    private static final <T> boolean safeToConvertToSet$CollectionsKt__IterablesKt(final Collection<? extends T> collection) {
        return collection.size() > 2 && collection instanceof ArrayList;
    }
}
