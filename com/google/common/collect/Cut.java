// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.io.Serializable;

abstract class Cut<C extends Comparable> implements Comparable<Cut<C>>, Serializable
{
    private static final long serialVersionUID = 0L;
    final C endpoint;
    
    Cut(final C endpoint) {
        this.endpoint = endpoint;
    }
    
    static <C extends Comparable> Cut<C> aboveAll() {
        return (Cut<C>)AboveAll.INSTANCE;
    }
    
    static <C extends Comparable> Cut<C> aboveValue(final C c) {
        return new AboveValue<C>(c);
    }
    
    static <C extends Comparable> Cut<C> belowAll() {
        return (Cut<C>)BelowAll.INSTANCE;
    }
    
    static <C extends Comparable> Cut<C> belowValue(final C c) {
        return new BelowValue<C>(c);
    }
    
    @Override
    public int compareTo(final Cut<C> cut) {
        if (cut == belowAll()) {
            return 1;
        }
        if (cut == aboveAll()) {
            return -1;
        }
        final int compareOrThrow = Range.compareOrThrow(this.endpoint, cut.endpoint);
        if (compareOrThrow != 0) {
            return compareOrThrow;
        }
        return Booleans.compare(this instanceof AboveValue, cut instanceof AboveValue);
    }
    
    abstract void describeAsLowerBound(final StringBuilder p0);
    
    abstract void describeAsUpperBound(final StringBuilder p0);
    
    C endpoint() {
        return this.endpoint;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = o instanceof Cut;
        boolean b3;
        final boolean b2 = b3 = false;
        if (!b) {
            return b3;
        }
        final Cut cut = (Cut)o;
        try {
            final int compareTo = this.compareTo(cut);
            b3 = b2;
            if (compareTo == 0) {
                b3 = true;
            }
            return b3;
        }
        catch (ClassCastException ex) {
            b3 = b2;
            return b3;
        }
    }
    
    @Override
    public abstract int hashCode();
    
    abstract boolean isLessThan(final C p0);
    
    private static final class AboveAll extends Cut<Comparable<?>>
    {
        private static final AboveAll INSTANCE;
        private static final long serialVersionUID = 0L;
        
        static {
            INSTANCE = new AboveAll();
        }
        
        private AboveAll() {
            super(null);
        }
        
        private Object readResolve() {
            return AboveAll.INSTANCE;
        }
        
        @Override
        public int compareTo(final Cut<Comparable<?>> cut) {
            return (cut != this) ? 1 : 0;
        }
        
        @Override
        void describeAsLowerBound(final StringBuilder sb) {
            throw new AssertionError();
        }
        
        @Override
        void describeAsUpperBound(final StringBuilder sb) {
            sb.append("+\u221e)");
        }
        
        @Override
        Comparable<?> endpoint() {
            throw new IllegalStateException("range unbounded on this side");
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
        
        @Override
        boolean isLessThan(final Comparable<?> comparable) {
            return false;
        }
        
        @Override
        public String toString() {
            return "+\u221e";
        }
    }
    
    private static final class AboveValue<C extends Comparable> extends Cut<C>
    {
        private static final long serialVersionUID = 0L;
        
        AboveValue(final C c) {
            Preconditions.checkNotNull(c);
            super(c);
        }
        
        @Override
        void describeAsLowerBound(final StringBuilder sb) {
            sb.append('(');
            sb.append(super.endpoint);
        }
        
        @Override
        void describeAsUpperBound(final StringBuilder sb) {
            sb.append(super.endpoint);
            sb.append(']');
        }
        
        @Override
        public int hashCode() {
            return super.endpoint.hashCode();
        }
        
        @Override
        boolean isLessThan(final C c) {
            return Range.compareOrThrow(super.endpoint, c) < 0;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(super.endpoint);
            sb.append("\\");
            return sb.toString();
        }
    }
    
    private static final class BelowAll extends Cut<Comparable<?>>
    {
        private static final BelowAll INSTANCE;
        private static final long serialVersionUID = 0L;
        
        static {
            INSTANCE = new BelowAll();
        }
        
        private BelowAll() {
            super(null);
        }
        
        private Object readResolve() {
            return BelowAll.INSTANCE;
        }
        
        @Override
        public int compareTo(final Cut<Comparable<?>> cut) {
            int n;
            if (cut == this) {
                n = 0;
            }
            else {
                n = -1;
            }
            return n;
        }
        
        @Override
        void describeAsLowerBound(final StringBuilder sb) {
            sb.append("(-\u221e");
        }
        
        @Override
        void describeAsUpperBound(final StringBuilder sb) {
            throw new AssertionError();
        }
        
        @Override
        Comparable<?> endpoint() {
            throw new IllegalStateException("range unbounded on this side");
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
        
        @Override
        boolean isLessThan(final Comparable<?> comparable) {
            return true;
        }
        
        @Override
        public String toString() {
            return "-\u221e";
        }
    }
    
    private static final class BelowValue<C extends Comparable> extends Cut<C>
    {
        private static final long serialVersionUID = 0L;
        
        BelowValue(final C c) {
            Preconditions.checkNotNull(c);
            super(c);
        }
        
        @Override
        void describeAsLowerBound(final StringBuilder sb) {
            sb.append('[');
            sb.append(super.endpoint);
        }
        
        @Override
        void describeAsUpperBound(final StringBuilder sb) {
            sb.append(super.endpoint);
            sb.append(')');
        }
        
        @Override
        public int hashCode() {
            return super.endpoint.hashCode();
        }
        
        @Override
        boolean isLessThan(final C c) {
            return Range.compareOrThrow(super.endpoint, c) <= 0;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("\\");
            sb.append(super.endpoint);
            sb.append("/");
            return sb.toString();
        }
    }
}
