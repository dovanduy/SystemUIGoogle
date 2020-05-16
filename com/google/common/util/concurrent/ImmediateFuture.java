// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

abstract class ImmediateFuture<V> implements ListenableFuture<V>
{
    private static final Logger log;
    
    static {
        log = Logger.getLogger(ImmediateFuture.class.getName());
    }
    
    @Override
    public void addListener(final Runnable obj, final Executor obj2) {
        Preconditions.checkNotNull(obj, "Runnable was null.");
        Preconditions.checkNotNull(obj2, "Executor was null.");
        try {
            obj2.execute(obj);
        }
        catch (RuntimeException thrown) {
            final Logger log = ImmediateFuture.log;
            final Level severe = Level.SEVERE;
            final StringBuilder sb = new StringBuilder();
            sb.append("RuntimeException while executing runnable ");
            sb.append(obj);
            sb.append(" with executor ");
            sb.append(obj2);
            log.log(severe, sb.toString(), thrown);
        }
    }
    
    @Override
    public boolean cancel(final boolean b) {
        return false;
    }
    
    @Override
    public abstract V get() throws ExecutionException;
    
    @Override
    public V get(final long n, final TimeUnit timeUnit) throws ExecutionException {
        Preconditions.checkNotNull(timeUnit);
        return this.get();
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public boolean isDone() {
        return true;
    }
    
    static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V>
    {
        static final ImmediateSuccessfulFuture<Object> NULL;
        private final V value;
        
        static {
            NULL = new ImmediateSuccessfulFuture<Object>(null);
        }
        
        ImmediateSuccessfulFuture(final V value) {
            this.value = value;
        }
        
        @Override
        public V get() {
            return this.value;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("[status=SUCCESS, result=[");
            sb.append(this.value);
            sb.append("]]");
            return sb.toString();
        }
    }
}
