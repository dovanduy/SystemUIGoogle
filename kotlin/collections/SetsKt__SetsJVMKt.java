// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.jvm.internal.Intrinsics;
import java.util.Collections;
import java.util.Set;

class SetsKt__SetsJVMKt
{
    public static final <T> Set<T> setOf(final T o) {
        final Set<T> singleton = Collections.singleton(o);
        Intrinsics.checkExpressionValueIsNotNull(singleton, "java.util.Collections.singleton(element)");
        return singleton;
    }
}
