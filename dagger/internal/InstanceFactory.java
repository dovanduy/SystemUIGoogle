// 
// Decompiled by Procyon v0.5.36
// 

package dagger.internal;

import dagger.Lazy;

public final class InstanceFactory<T> implements Factory<T>, Lazy<T>
{
    private final T instance;
    
    private InstanceFactory(final T instance) {
        this.instance = instance;
    }
    
    public static <T> Factory<T> create(final T t) {
        Preconditions.checkNotNull(t, "instance cannot be null");
        return new InstanceFactory<T>(t);
    }
    
    @Override
    public T get() {
        return this.instance;
    }
}
