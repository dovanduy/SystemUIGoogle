// 
// Decompiled by Procyon v0.5.36
// 

package dagger.internal;

import dagger.Lazy;
import javax.inject.Provider;

public final class DoubleCheck<T> implements Provider<T>, Lazy<T>
{
    private static final Object UNINITIALIZED;
    private volatile Object instance;
    private volatile Provider<T> provider;
    
    static {
        UNINITIALIZED = new Object();
    }
    
    private DoubleCheck(final Provider<T> provider) {
        this.instance = DoubleCheck.UNINITIALIZED;
        this.provider = provider;
    }
    
    public static <P extends Provider<T>, T> Lazy<T> lazy(final P p) {
        if (p instanceof Lazy) {
            return (Lazy<T>)p;
        }
        Preconditions.checkNotNull(p);
        return new DoubleCheck<T>(p);
    }
    
    public static <P extends Provider<T>, T> Provider<T> provider(final P p) {
        Preconditions.checkNotNull(p);
        if (p instanceof DoubleCheck) {
            return p;
        }
        return new DoubleCheck<T>(p);
    }
    
    public static Object reentrantCheck(final Object obj, final Object obj2) {
        if (obj != DoubleCheck.UNINITIALIZED && obj != obj2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Scoped provider was invoked recursively returning different results: ");
            sb.append(obj);
            sb.append(" & ");
            sb.append(obj2);
            sb.append(". This is likely due to a circular dependency.");
            throw new IllegalStateException(sb.toString());
        }
        return obj2;
    }
    
    @Override
    public T get() {
        final Object uninitialized = DoubleCheck.UNINITIALIZED;
        final Object instance;
        if ((instance = this.instance) == uninitialized) {
            synchronized (this) {
                if (this.instance == uninitialized) {
                    final T value = this.provider.get();
                    reentrantCheck(this.instance, value);
                    this.instance = value;
                    this.provider = null;
                }
            }
        }
        return (T)instance;
    }
}
