// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Executor;
import com.google.common.base.Function;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ExecutionException;
import com.google.common.base.Preconditions;
import java.util.concurrent.Future;

public final class Futures extends GwtFuturesCatchingSpecialization
{
    @CanIgnoreReturnValue
    public static <V> V getDone(final Future<V> future) throws ExecutionException {
        Preconditions.checkState(future.isDone(), "Future was expected to be done: %s", future);
        return Uninterruptibles.getUninterruptibly(future);
    }
    
    public static <V> ListenableFuture<V> immediateFuture(final V v) {
        if (v == null) {
            return (ListenableFuture<V>)ImmediateFuture.ImmediateSuccessfulFuture.NULL;
        }
        return new ImmediateFuture.ImmediateSuccessfulFuture<V>(v);
    }
    
    public static <I, O> ListenableFuture<O> transform(final ListenableFuture<I> listenableFuture, final Function<? super I, ? extends O> function, final Executor executor) {
        return AbstractTransformFuture.create(listenableFuture, function, executor);
    }
}
