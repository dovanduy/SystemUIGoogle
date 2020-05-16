// 
// Decompiled by Procyon v0.5.36
// 

package dagger.internal;

import javax.inject.Provider;

public final class DelegateFactory<T> implements Factory<T>
{
    private Provider<T> delegate;
    
    @Override
    public T get() {
        final Provider<T> delegate = this.delegate;
        if (delegate != null) {
            return delegate.get();
        }
        throw new IllegalStateException();
    }
    
    public void setDelegatedProvider(final Provider<T> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException();
        }
        if (this.delegate == null) {
            this.delegate = delegate;
            return;
        }
        throw new IllegalStateException();
    }
}
