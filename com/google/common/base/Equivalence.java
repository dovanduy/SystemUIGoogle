// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.io.Serializable;
import com.google.errorprone.annotations.ForOverride;

public abstract class Equivalence<T>
{
    protected Equivalence() {
    }
    
    public static Equivalence<Object> equals() {
        return Equals.INSTANCE;
    }
    
    public static Equivalence<Object> identity() {
        return Identity.INSTANCE;
    }
    
    @ForOverride
    protected abstract boolean doEquivalent(final T p0, final T p1);
    
    @ForOverride
    protected abstract int doHash(final T p0);
    
    public final boolean equivalent(final T t, final T t2) {
        return t == t2 || (t != null && t2 != null && this.doEquivalent(t, t2));
    }
    
    public final int hash(final T t) {
        if (t == null) {
            return 0;
        }
        return this.doHash(t);
    }
    
    static final class Equals extends Equivalence<Object> implements Serializable
    {
        static final Equals INSTANCE;
        private static final long serialVersionUID = 1L;
        
        static {
            INSTANCE = new Equals();
        }
        
        private Object readResolve() {
            return Equals.INSTANCE;
        }
        
        @Override
        protected boolean doEquivalent(final Object o, final Object obj) {
            return o.equals(obj);
        }
        
        @Override
        protected int doHash(final Object o) {
            return o.hashCode();
        }
    }
    
    static final class Identity extends Equivalence<Object> implements Serializable
    {
        static final Identity INSTANCE;
        private static final long serialVersionUID = 1L;
        
        static {
            INSTANCE = new Identity();
        }
        
        private Object readResolve() {
            return Identity.INSTANCE;
        }
        
        @Override
        protected boolean doEquivalent(final Object o, final Object o2) {
            return false;
        }
        
        @Override
        protected int doHash(final Object o) {
            return System.identityHashCode(o);
        }
    }
}
