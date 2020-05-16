// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

class Suppliers$NonSerializableMemoizingSupplier<T> implements Supplier<T>
{
    volatile Supplier<T> delegate;
    volatile boolean initialized;
    T value;
    
    @Override
    public T get() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    final T value = this.delegate.get();
                    this.value = value;
                    this.initialized = true;
                    this.delegate = null;
                    return value;
                }
            }
        }
        return this.value;
    }
    
    @Override
    public String toString() {
        final Supplier<T> delegate = this.delegate;
        final StringBuilder sb = new StringBuilder();
        sb.append("Suppliers.memoize(");
        Object string = delegate;
        if (delegate == null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("<supplier that returned ");
            sb2.append(this.value);
            sb2.append(">");
            string = sb2.toString();
        }
        sb.append(string);
        sb.append(")");
        return sb.toString();
    }
}
