// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.internal;

import kotlin.collections.ArraysKt;
import java.lang.reflect.Method;
import kotlin.jvm.internal.Intrinsics;

public class PlatformImplementations
{
    public void addSuppressed(final Throwable obj, final Throwable t) {
        Intrinsics.checkParameterIsNotNull(obj, "cause");
        Intrinsics.checkParameterIsNotNull(t, "exception");
        final Method method = ReflectAddSuppressedMethod.method;
        if (method != null) {
            method.invoke(obj, t);
        }
    }
    
    private static final class ReflectAddSuppressedMethod
    {
        public static final Method method;
        
        static {
            final Method[] methods = Throwable.class.getMethods();
            Intrinsics.checkExpressionValueIsNotNull(methods, "throwableClass.methods");
            while (true) {
                for (final Method method2 : methods) {
                    Intrinsics.checkExpressionValueIsNotNull(method2, "it");
                    boolean b = false;
                    Label_0082: {
                        if (Intrinsics.areEqual(method2.getName(), "addSuppressed")) {
                            final Class<?>[] parameterTypes = method2.getParameterTypes();
                            Intrinsics.checkExpressionValueIsNotNull(parameterTypes, "it.parameterTypes");
                            if (Intrinsics.areEqual(ArraysKt.singleOrNull(parameterTypes), Throwable.class)) {
                                b = true;
                                break Label_0082;
                            }
                        }
                        b = false;
                    }
                    if (b) {
                        method = method2;
                        return;
                    }
                }
                Method method2 = null;
                continue;
            }
        }
    }
}
