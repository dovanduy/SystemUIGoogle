// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.NoSuchElementException;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.util.Iterator;

public abstract class AbstractIterator<T> implements Iterator<T>, KMappedMarker
{
    private T nextValue;
    private State state;
    
    public AbstractIterator() {
        this.state = State.NotReady;
    }
    
    private final boolean tryToComputeNext() {
        this.state = State.Failed;
        this.computeNext();
        return this.state == State.Ready;
    }
    
    protected abstract void computeNext();
    
    protected final void done() {
        this.state = State.Done;
    }
    
    @Override
    public boolean hasNext() {
        final State state = this.state;
        final State failed = State.Failed;
        boolean b = false;
        if (state != failed) {
            final int n = AbstractIterator$WhenMappings.$EnumSwitchMapping$0[this.state.ordinal()];
            if (n != 1) {
                b = (n == 2 || this.tryToComputeNext());
            }
            return b;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }
    
    @Override
    public T next() {
        if (this.hasNext()) {
            this.state = State.NotReady;
            return this.nextValue;
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    protected final void setNext(final T nextValue) {
        this.nextValue = nextValue;
        this.state = State.Ready;
    }
}
