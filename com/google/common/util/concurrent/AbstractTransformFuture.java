// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.Future;
import java.util.concurrent.Executor;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;

abstract class AbstractTransformFuture<I, O, F, T> extends TrustedFuture<O> implements Runnable
{
    F function;
    ListenableFuture<? extends I> inputFuture;
    
    AbstractTransformFuture(final ListenableFuture<? extends I> listenableFuture, final F function) {
        Preconditions.checkNotNull(listenableFuture);
        this.inputFuture = listenableFuture;
        Preconditions.checkNotNull(function);
        this.function = function;
    }
    
    static <I, O> ListenableFuture<O> create(final ListenableFuture<I> listenableFuture, final Function<? super I, ? extends O> function, final Executor executor) {
        Preconditions.checkNotNull(function);
        final TransformFuture transformFuture = new TransformFuture(listenableFuture, (Function<? super Object, ?>)function);
        listenableFuture.addListener(transformFuture, MoreExecutors.rejectionPropagatingExecutor(executor, transformFuture));
        return (ListenableFuture<O>)transformFuture;
    }
    
    @Override
    protected final void afterDone() {
        this.maybePropagateCancellationTo(this.inputFuture);
        this.inputFuture = null;
        this.function = null;
    }
    
    @ForOverride
    abstract T doTransform(final F p0, final I p1) throws Exception;
    
    @Override
    protected String pendingToString() {
        final ListenableFuture<? extends I> inputFuture = this.inputFuture;
        final F function = this.function;
        final String pendingToString = super.pendingToString();
        String string;
        if (inputFuture != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("inputFuture=[");
            sb.append(inputFuture);
            sb.append("], ");
            string = sb.toString();
        }
        else {
            string = "";
        }
        if (function != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(string);
            sb2.append("function=[");
            sb2.append(function);
            sb2.append("]");
            return sb2.toString();
        }
        if (pendingToString != null) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append(string);
            sb3.append(pendingToString);
            return sb3.toString();
        }
        return null;
    }
    
    @Override
    public final void run() {
        final ListenableFuture<? extends I> inputFuture = this.inputFuture;
        final F function = this.function;
        final boolean cancelled = this.isCancelled();
        boolean b = true;
        final boolean b2 = inputFuture == null;
        if (function != null) {
            b = false;
        }
        if (cancelled | b2 | b) {
            return;
        }
        this.inputFuture = null;
        if (inputFuture.isCancelled()) {
            this.setFuture((ListenableFuture<? extends O>)inputFuture);
            return;
        }
        try {
            final I done = Futures.getDone((Future<I>)inputFuture);
            try {
                final T doTransform = this.doTransform(function, done);
                this.function = null;
                this.setResult(doTransform);
            }
            finally {
                try {
                    final Throwable exception;
                    this.setException(exception);
                }
                finally {
                    this.function = null;
                }
            }
        }
        catch (Error exception2) {
            this.setException(exception2);
        }
        catch (RuntimeException exception3) {
            this.setException(exception3);
        }
        catch (ExecutionException ex) {
            this.setException(ex.getCause());
        }
        catch (CancellationException ex2) {
            this.cancel(false);
        }
    }
    
    @ForOverride
    abstract void setResult(final T p0);
    
    private static final class TransformFuture<I, O> extends AbstractTransformFuture<I, O, Function<? super I, ? extends O>, O>
    {
        TransformFuture(final ListenableFuture<? extends I> listenableFuture, final Function<? super I, ? extends O> function) {
            super(listenableFuture, function);
        }
        
        @Override
        O doTransform(final Function<? super I, ? extends O> function, final I n) {
            return (O)function.apply(n);
        }
        
        @Override
        void setResult(final O o) {
            this.set((O)o);
        }
    }
}
