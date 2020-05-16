// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public abstract class AbstractIterator<T> extends UnmodifiableIterator<T>
{
    private T next;
    private State state;
    
    protected AbstractIterator() {
        this.state = State.NOT_READY;
    }
    
    private boolean tryToComputeNext() {
        this.state = State.FAILED;
        this.next = this.computeNext();
        if (this.state != State.DONE) {
            this.state = State.READY;
            return true;
        }
        return false;
    }
    
    protected abstract T computeNext();
    
    @CanIgnoreReturnValue
    protected final T endOfData() {
        this.state = State.DONE;
        return null;
    }
    
    @CanIgnoreReturnValue
    @Override
    public final boolean hasNext() {
        Preconditions.checkState(this.state != State.FAILED);
        final int n = AbstractIterator$1.$SwitchMap$com$google$common$collect$AbstractIterator$State[this.state.ordinal()];
        return n != 1 && (n == 2 || this.tryToComputeNext());
    }
    
    @CanIgnoreReturnValue
    @Override
    public final T next() {
        if (this.hasNext()) {
            this.state = State.NOT_READY;
            final T next = this.next;
            this.next = null;
            return next;
        }
        throw new NoSuchElementException();
    }
    
    private enum State
    {
        DONE, 
        FAILED, 
        NOT_READY, 
        READY;
    }
}
