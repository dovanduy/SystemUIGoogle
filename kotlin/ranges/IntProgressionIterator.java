// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.ranges;

import java.util.NoSuchElementException;
import kotlin.collections.IntIterator;

public final class IntProgressionIterator extends IntIterator
{
    private final int finalElement;
    private boolean hasNext;
    private int next;
    private final int step;
    
    public IntProgressionIterator(int finalElement, final int finalElement2, final int step) {
        this.step = step;
        this.finalElement = finalElement2;
        boolean hasNext = true;
        Label_0040: {
            if (step > 0) {
                if (finalElement <= finalElement2) {
                    break Label_0040;
                }
            }
            else if (finalElement >= finalElement2) {
                break Label_0040;
            }
            hasNext = false;
        }
        this.hasNext = hasNext;
        if (!hasNext) {
            finalElement = this.finalElement;
        }
        this.next = finalElement;
    }
    
    @Override
    public boolean hasNext() {
        return this.hasNext;
    }
    
    @Override
    public int nextInt() {
        final int next = this.next;
        if (next == this.finalElement) {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            this.hasNext = false;
        }
        else {
            this.next = this.step + next;
        }
        return next;
    }
}
