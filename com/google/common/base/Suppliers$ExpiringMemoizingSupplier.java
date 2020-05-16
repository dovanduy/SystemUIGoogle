// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.io.Serializable;

class Suppliers$ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable
{
    private static final long serialVersionUID = 0L;
    final Supplier<T> delegate;
    final long durationNanos;
    transient volatile long expirationNanos;
    transient volatile T value;
    
    @Override
    public T get() {
        final long expirationNanos = this.expirationNanos;
        final long systemNanoTime = Platform.systemNanoTime();
        Label_0080: {
            if (expirationNanos != 0L && systemNanoTime - expirationNanos < 0L) {
                break Label_0080;
            }
            synchronized (this) {
                if (expirationNanos == this.expirationNanos) {
                    final T value = this.delegate.get();
                    this.value = value;
                    long expirationNanos2;
                    if ((expirationNanos2 = systemNanoTime + this.durationNanos) == 0L) {
                        expirationNanos2 = 1L;
                    }
                    this.expirationNanos = expirationNanos2;
                    return value;
                }
                // monitorexit(this)
                return this.value;
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Suppliers.memoizeWithExpiration(");
        sb.append(this.delegate);
        sb.append(", ");
        sb.append(this.durationNanos);
        sb.append(", NANOS)");
        return sb.toString();
    }
}
