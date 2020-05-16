// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Executor;

public abstract class FluentFuture<V> extends GwtFluentFutureCatchingSpecialization<V>
{
    FluentFuture() {
    }
    
    abstract static class TrustedFuture<V> extends FluentFuture<V> implements Trusted<V>
    {
        @Override
        public final void addListener(final Runnable runnable, final Executor executor) {
            super.addListener(runnable, executor);
        }
        
        @CanIgnoreReturnValue
        @Override
        public final boolean cancel(final boolean b) {
            return super.cancel(b);
        }
        
        @CanIgnoreReturnValue
        @Override
        public final V get() throws InterruptedException, ExecutionException {
            return super.get();
        }
        
        @CanIgnoreReturnValue
        @Override
        public final V get(final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return super.get(n, timeUnit);
        }
        
        @Override
        public final boolean isCancelled() {
            return super.isCancelled();
        }
        
        @Override
        public final boolean isDone() {
            return super.isDone();
        }
    }
}
