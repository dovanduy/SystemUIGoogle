// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import java.io.Serializable;

class Suppliers$MemoizingSupplier<T> implements Supplier<T>, Serializable
{
    private static final long serialVersionUID = 0L;
    final Supplier<T> delegate;
    transient volatile boolean initialized;
    transient T value;
    
    @Override
    public T get() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    final T value = this.delegate.get();
                    this.value = value;
                    this.initialized = true;
                    return value;
                }
            }
        }
        return this.value;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Suppliers.memoize(");
        Object obj;
        if (this.initialized) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("<supplier that returned ");
            sb2.append(this.value);
            sb2.append(">");
            obj = sb2.toString();
        }
        else {
            obj = this.delegate;
        }
        sb.append(obj);
        sb.append(")");
        return sb.toString();
    }
}
