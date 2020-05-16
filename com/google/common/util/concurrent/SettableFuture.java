// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

public final class SettableFuture<V> extends TrustedFuture<V>
{
    private SettableFuture() {
    }
    
    public static <V> SettableFuture<V> create() {
        return new SettableFuture<V>();
    }
    
    @CanIgnoreReturnValue
    public boolean set(final V v) {
        return super.set(v);
    }
    
    @CanIgnoreReturnValue
    public boolean setException(final Throwable exception) {
        return super.setException(exception);
    }
    
    @CanIgnoreReturnValue
    public boolean setFuture(final ListenableFuture<? extends V> future) {
        return super.setFuture(future);
    }
}
