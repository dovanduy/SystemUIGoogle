// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import kotlin.UninitializedPropertyAccessException;
import kotlin.KotlinNullPointerException;
import java.util.Arrays;

public class Intrinsics
{
    private Intrinsics() {
    }
    
    public static boolean areEqual(final float n, final Float n2) {
        return n2 != null && n == n2;
    }
    
    public static boolean areEqual(final Float n, final float n2) {
        return n != null && n == n2;
    }
    
    public static boolean areEqual(final Object o, final Object obj) {
        boolean equals;
        if (o == null) {
            equals = (obj == null);
        }
        else {
            equals = o.equals(obj);
        }
        return equals;
    }
    
    public static void checkExpressionValueIsNotNull(final Object o, final String str) {
        if (o != null) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" must not be null");
        final IllegalStateException ex = new IllegalStateException(sb.toString());
        sanitizeStackTrace(ex);
        throw ex;
    }
    
    public static void checkParameterIsNotNull(final Object o, final String s) {
        if (o != null) {
            return;
        }
        throwParameterIsNullException(s);
        throw null;
    }
    
    public static int compare(int n, final int n2) {
        if (n < n2) {
            n = -1;
        }
        else if (n == n2) {
            n = 0;
        }
        else {
            n = 1;
        }
        return n;
    }
    
    private static <T extends Throwable> T sanitizeStackTrace(final T t) {
        sanitizeStackTrace(t, Intrinsics.class.getName());
        return t;
    }
    
    static <T extends Throwable> T sanitizeStackTrace(final T t, final String s) {
        final StackTraceElement[] stackTrace = t.getStackTrace();
        final int length = stackTrace.length;
        int n = -1;
        for (int i = 0; i < length; ++i) {
            if (s.equals(stackTrace[i].getClassName())) {
                n = i;
            }
        }
        t.setStackTrace(Arrays.copyOfRange(stackTrace, n + 1, length));
        return t;
    }
    
    public static void throwNpe() {
        final KotlinNullPointerException ex = new KotlinNullPointerException();
        sanitizeStackTrace(ex);
        throw ex;
    }
    
    private static void throwParameterIsNullException(final String str) {
        final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        final String className = stackTraceElement.getClassName();
        final String methodName = stackTraceElement.getMethodName();
        final StringBuilder sb = new StringBuilder();
        sb.append("Parameter specified as non-null is null: method ");
        sb.append(className);
        sb.append(".");
        sb.append(methodName);
        sb.append(", parameter ");
        sb.append(str);
        final IllegalArgumentException ex = new IllegalArgumentException(sb.toString());
        sanitizeStackTrace(ex);
        throw ex;
    }
    
    public static void throwUninitializedProperty(final String s) {
        final UninitializedPropertyAccessException ex = new UninitializedPropertyAccessException(s);
        sanitizeStackTrace(ex);
        throw ex;
    }
    
    public static void throwUninitializedPropertyAccessException(final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append("lateinit property ");
        sb.append(str);
        sb.append(" has not been initialized");
        throwUninitializedProperty(sb.toString());
        throw null;
    }
}
