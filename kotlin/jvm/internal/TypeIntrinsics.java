// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import kotlin.Function;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function0;
import java.util.Set;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.Collection;

public class TypeIntrinsics
{
    public static Collection asMutableCollection(final Object o) {
        if (o instanceof KMappedMarker) {
            throwCce(o, "kotlin.collections.MutableCollection");
            throw null;
        }
        return castToCollection(o);
    }
    
    public static Iterable asMutableIterable(final Object o) {
        if (o instanceof KMappedMarker) {
            throwCce(o, "kotlin.collections.MutableIterable");
            throw null;
        }
        return castToIterable(o);
    }
    
    public static Set asMutableSet(final Object o) {
        if (o instanceof KMappedMarker) {
            throwCce(o, "kotlin.collections.MutableSet");
            throw null;
        }
        return castToSet(o);
    }
    
    public static Object beforeCheckcastToFunctionOfArity(final Object o, final int i) {
        if (o != null && !isFunctionOfArity(o, i)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("kotlin.jvm.functions.Function");
            sb.append(i);
            throwCce(o, sb.toString());
            throw null;
        }
        return o;
    }
    
    public static Collection castToCollection(final Object o) {
        try {
            return (Collection)o;
        }
        catch (ClassCastException ex) {
            throwCce(ex);
            throw null;
        }
    }
    
    public static Iterable castToIterable(final Object o) {
        try {
            return (Iterable)o;
        }
        catch (ClassCastException ex) {
            throwCce(ex);
            throw null;
        }
    }
    
    public static Set castToSet(final Object o) {
        try {
            return (Set)o;
        }
        catch (ClassCastException ex) {
            throwCce(ex);
            throw null;
        }
    }
    
    public static int getFunctionArity(final Object o) {
        if (o instanceof FunctionBase) {
            return ((FunctionBase)o).getArity();
        }
        if (o instanceof Function0) {
            return 0;
        }
        if (o instanceof Function1) {
            return 1;
        }
        if (o instanceof Function2) {
            return 2;
        }
        if (o instanceof Function3) {
            return 3;
        }
        return -1;
    }
    
    public static boolean isFunctionOfArity(final Object o, final int n) {
        return o instanceof Function && getFunctionArity(o) == n;
    }
    
    private static <T extends Throwable> T sanitizeStackTrace(final T t) {
        Intrinsics.sanitizeStackTrace(t, TypeIntrinsics.class.getName());
        return t;
    }
    
    public static ClassCastException throwCce(final ClassCastException ex) {
        sanitizeStackTrace(ex);
        throw ex;
    }
    
    public static void throwCce(final Object o, final String str) {
        String name;
        if (o == null) {
            name = "null";
        }
        else {
            name = o.getClass().getName();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" cannot be cast to ");
        sb.append(str);
        throwCce(sb.toString());
        throw null;
    }
    
    public static void throwCce(final String s) {
        throwCce(new ClassCastException(s));
        throw null;
    }
}
