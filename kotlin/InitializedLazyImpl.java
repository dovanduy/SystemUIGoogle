// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import java.io.Serializable;

public final class InitializedLazyImpl<T> implements Lazy<T>, Serializable
{
    private final T value;
    
    public InitializedLazyImpl(final T value) {
        this.value = value;
    }
    
    @Override
    public T getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getValue());
    }
}
