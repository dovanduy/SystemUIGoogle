// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.functions.Function0;
import java.io.Serializable;

final class SynchronizedLazyImpl<T> implements Lazy<T>, Serializable
{
    private volatile Object _value;
    private Function0<? extends T> initializer;
    private final Object lock;
    
    public SynchronizedLazyImpl(final Function0<? extends T> initializer, Object lock) {
        Intrinsics.checkParameterIsNotNull(initializer, "initializer");
        this.initializer = initializer;
        this._value = UNINITIALIZED_VALUE.INSTANCE;
        if (lock == null) {
            lock = this;
        }
        this.lock = lock;
    }
    
    private final Object writeReplace() {
        return new InitializedLazyImpl(this.getValue());
    }
    
    @Override
    public T getValue() {
        final Object value = this._value;
        if (value != UNINITIALIZED_VALUE.INSTANCE) {
            return (T)value;
        }
        synchronized (this.lock) {
            Object value2 = this._value;
            if (value2 == UNINITIALIZED_VALUE.INSTANCE) {
                final Function0<? extends T> initializer = this.initializer;
                if (initializer == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                value2 = initializer.invoke();
                this._value = value2;
                this.initializer = null;
            }
            return (T)value2;
        }
    }
    
    public boolean isInitialized() {
        return this._value != UNINITIALIZED_VALUE.INSTANCE;
    }
    
    @Override
    public String toString() {
        String value;
        if (this.isInitialized()) {
            value = String.valueOf(this.getValue());
        }
        else {
            value = "Lazy value not initialized yet.";
        }
        return value;
    }
}
