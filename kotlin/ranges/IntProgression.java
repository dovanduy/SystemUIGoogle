// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.ranges;

import kotlin.collections.IntIterator;
import java.util.Iterator;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.internal.markers.KMappedMarker;

public class IntProgression implements Iterable<Integer>, KMappedMarker
{
    public static final Companion Companion;
    private final int first;
    private final int last;
    private final int step;
    
    static {
        Companion = new Companion(null);
    }
    
    public IntProgression(final int first, final int n, final int step) {
        if (step == 0) {
            throw new IllegalArgumentException("Step must be non-zero.");
        }
        if (step != Integer.MIN_VALUE) {
            this.first = first;
            this.last = ProgressionUtilKt.getProgressionLastElement(first, n, step);
            this.step = step;
            return;
        }
        throw new IllegalArgumentException("Step must be greater than Int.MIN_VALUE to avoid overflow on negation.");
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof IntProgression) {
            if (!this.isEmpty() || !((IntProgression)o).isEmpty()) {
                final int first = this.first;
                final IntProgression intProgression = (IntProgression)o;
                if (first != intProgression.first || this.last != intProgression.last || this.step != intProgression.step) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public final int getFirst() {
        return this.first;
    }
    
    public final int getLast() {
        return this.last;
    }
    
    public final int getStep() {
        return this.step;
    }
    
    @Override
    public int hashCode() {
        int n;
        if (this.isEmpty()) {
            n = -1;
        }
        else {
            n = this.step + (this.first * 31 + this.last) * 31;
        }
        return n;
    }
    
    public boolean isEmpty() {
        final int step = this.step;
        boolean b = true;
        if (step > 0) {
            if (this.first > this.last) {
                return b;
            }
        }
        else if (this.first < this.last) {
            return b;
        }
        b = false;
        return b;
    }
    
    @Override
    public IntIterator iterator() {
        return new IntProgressionIterator(this.first, this.last, this.step);
    }
    
    @Override
    public String toString() {
        StringBuilder sb;
        int step;
        if (this.step > 0) {
            sb = new StringBuilder();
            sb.append(this.first);
            sb.append("..");
            sb.append(this.last);
            sb.append(" step ");
            step = this.step;
        }
        else {
            sb = new StringBuilder();
            sb.append(this.first);
            sb.append(" downTo ");
            sb.append(this.last);
            sb.append(" step ");
            step = -this.step;
        }
        sb.append(step);
        return sb.toString();
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final IntProgression fromClosedRange(final int n, final int n2, final int n3) {
            return new IntProgression(n, n2, n3);
        }
    }
}
