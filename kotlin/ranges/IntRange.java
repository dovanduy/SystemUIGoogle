// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.ranges;

public final class IntRange extends IntProgression implements Object<Integer>
{
    public static final Companion Companion;
    private static final IntRange EMPTY;
    
    static {
        Companion = new Companion(null);
        EMPTY = new IntRange(1, 0);
    }
    
    public IntRange(final int n, final int n2) {
        super(n, n2, 1);
    }
    
    public static final /* synthetic */ IntRange access$getEMPTY$cp() {
        return IntRange.EMPTY;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof IntRange) {
            if (!this.isEmpty() || !((IntRange)o).isEmpty()) {
                final int first = this.getFirst();
                final IntRange intRange = (IntRange)o;
                if (first != intRange.getFirst() || this.getLast() != intRange.getLast()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int n;
        if (this.isEmpty()) {
            n = -1;
        }
        else {
            n = this.getLast() + this.getFirst() * 31;
        }
        return n;
    }
    
    @Override
    public boolean isEmpty() {
        return this.getFirst() > this.getLast();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getFirst());
        sb.append("..");
        sb.append(this.getLast());
        return sb.toString();
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final IntRange getEMPTY() {
            return IntRange.access$getEMPTY$cp();
        }
    }
}
