// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.io.Serializable;

public final class Range<C extends Comparable> extends RangeGwtSerializationDependencies implements Object<C>, Serializable
{
    private static final Range<Comparable> ALL;
    private static final long serialVersionUID = 0L;
    final Cut<C> lowerBound;
    final Cut<C> upperBound;
    
    static {
        ALL = new Range<Comparable>((Cut<Comparable>)Cut.belowAll(), (Cut<Comparable>)Cut.aboveAll());
    }
    
    private Range(final Cut<C> cut, final Cut<C> cut2) {
        Preconditions.checkNotNull(cut);
        this.lowerBound = cut;
        Preconditions.checkNotNull(cut2);
        this.upperBound = cut2;
        if (cut.compareTo(cut2) <= 0 && cut != Cut.aboveAll() && cut2 != Cut.belowAll()) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid range: ");
        sb.append(toString(cut, cut2));
        throw new IllegalArgumentException(sb.toString());
    }
    
    public static <C extends Comparable<?>> Range<C> all() {
        return (Range<C>)Range.ALL;
    }
    
    public static <C extends Comparable<?>> Range<C> atLeast(final C c) {
        return create((Cut<C>)Cut.belowValue((C)c), Cut.aboveAll());
    }
    
    public static <C extends Comparable<?>> Range<C> atMost(final C c) {
        return create(Cut.belowAll(), (Cut<C>)Cut.aboveValue((C)c));
    }
    
    static int compareOrThrow(final Comparable comparable, final Comparable comparable2) {
        return comparable.compareTo(comparable2);
    }
    
    static <C extends Comparable<?>> Range<C> create(final Cut<C> cut, final Cut<C> cut2) {
        return new Range<C>(cut, cut2);
    }
    
    public static <C extends Comparable<?>> Range<C> downTo(final C c, final BoundType boundType) {
        final int n = Range$1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()];
        if (n == 1) {
            return greaterThan(c);
        }
        if (n == 2) {
            return atLeast(c);
        }
        throw new AssertionError();
    }
    
    public static <C extends Comparable<?>> Range<C> greaterThan(final C c) {
        return create((Cut<C>)Cut.aboveValue((C)c), Cut.aboveAll());
    }
    
    public static <C extends Comparable<?>> Range<C> lessThan(final C c) {
        return create(Cut.belowAll(), (Cut<C>)Cut.belowValue((C)c));
    }
    
    public static <C extends Comparable<?>> Range<C> range(final C c, final BoundType boundType, final C c2, final BoundType boundType2) {
        Preconditions.checkNotNull(boundType);
        Preconditions.checkNotNull(boundType2);
        Cut<C> cut;
        if (boundType == BoundType.OPEN) {
            cut = Cut.aboveValue(c);
        }
        else {
            cut = Cut.belowValue(c);
        }
        Cut<C> cut2;
        if (boundType2 == BoundType.OPEN) {
            cut2 = Cut.belowValue(c2);
        }
        else {
            cut2 = Cut.aboveValue(c2);
        }
        return create(cut, cut2);
    }
    
    private static String toString(final Cut<?> cut, final Cut<?> cut2) {
        final StringBuilder sb = new StringBuilder(16);
        cut.describeAsLowerBound(sb);
        sb.append("..");
        cut2.describeAsUpperBound(sb);
        return sb.toString();
    }
    
    public static <C extends Comparable<?>> Range<C> upTo(final C c, final BoundType boundType) {
        final int n = Range$1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()];
        if (n == 1) {
            return lessThan(c);
        }
        if (n == 2) {
            return atMost(c);
        }
        throw new AssertionError();
    }
    
    public boolean contains(final C c) {
        Preconditions.checkNotNull(c);
        return this.lowerBound.isLessThan(c) && !this.upperBound.isLessThan(c);
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = o instanceof Range;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final Range range = (Range)o;
            b3 = b2;
            if (this.lowerBound.equals(range.lowerBound)) {
                b3 = b2;
                if (this.upperBound.equals(range.upperBound)) {
                    b3 = true;
                }
            }
        }
        return b3;
    }
    
    public boolean hasLowerBound() {
        return this.lowerBound != Cut.belowAll();
    }
    
    public boolean hasUpperBound() {
        return this.upperBound != Cut.aboveAll();
    }
    
    @Override
    public int hashCode() {
        return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
    }
    
    public Range<C> intersection(final Range<C> range) {
        final int compareTo = this.lowerBound.compareTo(range.lowerBound);
        final int compareTo2 = this.upperBound.compareTo(range.upperBound);
        if (compareTo >= 0 && compareTo2 <= 0) {
            return this;
        }
        if (compareTo <= 0 && compareTo2 >= 0) {
            return range;
        }
        Cut<C> cut;
        if (compareTo >= 0) {
            cut = this.lowerBound;
        }
        else {
            cut = range.lowerBound;
        }
        Cut<C> cut2;
        if (compareTo2 <= 0) {
            cut2 = this.upperBound;
        }
        else {
            cut2 = range.upperBound;
        }
        return create(cut, cut2);
    }
    
    public boolean isConnected(final Range<C> range) {
        return this.lowerBound.compareTo(range.upperBound) <= 0 && range.lowerBound.compareTo(this.upperBound) <= 0;
    }
    
    public C lowerEndpoint() {
        return this.lowerBound.endpoint();
    }
    
    Object readResolve() {
        Range<Comparable> all = (Range<Comparable>)this;
        if (this.equals(Range.ALL)) {
            all = all();
        }
        return all;
    }
    
    @Override
    public String toString() {
        return toString(this.lowerBound, this.upperBound);
    }
    
    public C upperEndpoint() {
        return this.upperBound.endpoint();
    }
}
