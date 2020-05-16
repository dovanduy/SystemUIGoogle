// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import kotlin.jvm.internal.Intrinsics;
import java.io.Serializable;

public final class Result<T> implements Serializable
{
    public static final Companion Companion;
    private final Object value;
    
    static {
        Companion = new Companion(null);
    }
    
    public static Object constructor-impl(final Object o) {
        return o;
    }
    
    public static boolean equals-impl(final Object o, final Object o2) {
        return o2 instanceof Result && Intrinsics.areEqual(o, ((Result)o2).unbox-impl());
    }
    
    public static int hashCode-impl(final Object o) {
        int hashCode;
        if (o != null) {
            hashCode = o.hashCode();
        }
        else {
            hashCode = 0;
        }
        return hashCode;
    }
    
    public static String toString-impl(final Object obj) {
        String s;
        if (obj instanceof Failure) {
            s = obj.toString();
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append("Success(");
            sb.append(obj);
            sb.append(')');
            s = sb.toString();
        }
        return s;
    }
    
    @Override
    public boolean equals(final Object o) {
        return equals-impl(this.value, o);
    }
    
    @Override
    public int hashCode() {
        return hashCode-impl(this.value);
    }
    
    @Override
    public String toString() {
        return toString-impl(this.value);
    }
    
    public final /* synthetic */ Object unbox-impl() {
        return this.value;
    }
    
    public static final class Companion
    {
        private Companion() {
        }
    }
    
    public static final class Failure implements Serializable
    {
        public final Throwable exception;
        
        public Failure(final Throwable exception) {
            Intrinsics.checkParameterIsNotNull(exception, "exception");
            this.exception = exception;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Failure && Intrinsics.areEqual(this.exception, ((Failure)o).exception);
        }
        
        @Override
        public int hashCode() {
            return this.exception.hashCode();
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failure(");
            sb.append(this.exception);
            sb.append(')');
            return sb.toString();
        }
    }
}
