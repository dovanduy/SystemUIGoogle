// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.functions.Function0;

class LazyKt__LazyJVMKt
{
    public static <T> Lazy<T> lazy(final Function0<? extends T> function0) {
        Intrinsics.checkParameterIsNotNull(function0, "initializer");
        return new SynchronizedLazyImpl<T>(function0, null, 2, null);
    }
}
