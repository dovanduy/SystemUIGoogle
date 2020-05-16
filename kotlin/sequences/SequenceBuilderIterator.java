// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.ResultKt;
import kotlin.Result;
import kotlin.jvm.internal.Intrinsics;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.CoroutineContext;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.markers.KMappedMarker;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import java.util.Iterator;

final class SequenceBuilderIterator<T> extends SequenceScope<T> implements Iterator<T>, Continuation<Unit>, KMappedMarker
{
    private Iterator<? extends T> nextIterator;
    private Continuation<? super Unit> nextStep;
    private T nextValue;
    private int state;
    
    public SequenceBuilderIterator() {
    }
    
    private final Throwable exceptionalState() {
        final int state = this.state;
        RuntimeException ex;
        if (state != 4) {
            if (state != 5) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unexpected state of the iterator: ");
                sb.append(this.state);
                ex = new IllegalStateException(sb.toString());
            }
            else {
                ex = new IllegalStateException("Iterator has failed.");
            }
        }
        else {
            ex = new NoSuchElementException();
        }
        return ex;
    }
    
    private final T nextNotReady() {
        if (this.hasNext()) {
            return this.next();
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public CoroutineContext getContext() {
        return EmptyCoroutineContext.INSTANCE;
    }
    
    @Override
    public boolean hasNext() {
        while (true) {
            final int state = this.state;
            if (state != 0) {
                if (state != 1) {
                    if (state == 2 || state == 3) {
                        return true;
                    }
                    if (state == 4) {
                        return false;
                    }
                    throw this.exceptionalState();
                }
                else {
                    final Iterator<? extends T> nextIterator = this.nextIterator;
                    if (nextIterator == null) {
                        Intrinsics.throwNpe();
                        throw null;
                    }
                    if (nextIterator.hasNext()) {
                        this.state = 2;
                        return true;
                    }
                    this.nextIterator = null;
                }
            }
            this.state = 5;
            final Continuation<? super Unit> nextStep = this.nextStep;
            if (nextStep == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            this.nextStep = null;
            final Unit instance = Unit.INSTANCE;
            final Result.Companion companion = Result.Companion;
            Result.constructor-impl(instance);
            nextStep.resumeWith(instance);
        }
    }
    
    @Override
    public T next() {
        final int state = this.state;
        if (state == 0 || state == 1) {
            return this.nextNotReady();
        }
        if (state != 2) {
            if (state == 3) {
                this.state = 0;
                final T nextValue = this.nextValue;
                this.nextValue = null;
                return nextValue;
            }
            throw this.exceptionalState();
        }
        else {
            this.state = 1;
            final Iterator<? extends T> nextIterator = this.nextIterator;
            if (nextIterator != null) {
                return (T)nextIterator.next();
            }
            Intrinsics.throwNpe();
            throw null;
        }
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public void resumeWith(final Object o) {
        ResultKt.throwOnFailure(o);
        this.state = 4;
    }
    
    public final void setNextStep(final Continuation<? super Unit> nextStep) {
        this.nextStep = nextStep;
    }
    
    @Override
    public Object yield(final T nextValue, final Continuation<? super Unit> nextStep) {
        this.nextValue = nextValue;
        this.state = 3;
        this.nextStep = nextStep;
        final Object coroutine_SUSPENDED = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        IntrinsicsKt.getCOROUTINE_SUSPENDED();
        DebugProbesKt.probeCoroutineSuspended(nextStep);
        return coroutine_SUSPENDED;
    }
}
