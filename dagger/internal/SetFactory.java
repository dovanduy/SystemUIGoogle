// 
// Decompiled by Procyon v0.5.36
// 

package dagger.internal;

import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import javax.inject.Provider;
import java.util.List;
import java.util.Set;

public final class SetFactory<T> implements Factory<Set<T>>
{
    private final List<Provider<Collection<T>>> collectionProviders;
    private final List<Provider<T>> individualProviders;
    
    static {
        InstanceFactory.create(Collections.emptySet());
    }
    
    private SetFactory(final List<Provider<T>> individualProviders, final List<Provider<Collection<T>>> collectionProviders) {
        this.individualProviders = individualProviders;
        this.collectionProviders = collectionProviders;
    }
    
    public static <T> Builder<T> builder(final int n, final int n2) {
        return new Builder<T>(n, n2);
    }
    
    @Override
    public Set<T> get() {
        int size = this.individualProviders.size();
        final ArrayList<Collection<Object>> list = new ArrayList<Collection<Object>>(this.collectionProviders.size());
        final int size2 = this.collectionProviders.size();
        final int n = 0;
        for (int i = 0; i < size2; ++i) {
            final Collection<T> collection = this.collectionProviders.get(i).get();
            size += collection.size();
            list.add((Collection<Object>)collection);
        }
        final HashSet<Object> hashSetWithExpectedSize = DaggerCollections.newHashSetWithExpectedSize(size);
        for (int size3 = this.individualProviders.size(), j = 0; j < size3; ++j) {
            final T value = this.individualProviders.get(j).get();
            Preconditions.checkNotNull(value);
            hashSetWithExpectedSize.add(value);
        }
        for (int size4 = list.size(), k = n; k < size4; ++k) {
            for (final T next : list.get(k)) {
                Preconditions.checkNotNull(next);
                hashSetWithExpectedSize.add(next);
            }
        }
        return Collections.unmodifiableSet((Set<? extends T>)hashSetWithExpectedSize);
    }
    
    public static final class Builder<T>
    {
        private final List<Provider<Collection<T>>> collectionProviders;
        private final List<Provider<T>> individualProviders;
        
        private Builder(final int n, final int n2) {
            this.individualProviders = DaggerCollections.presizedList(n);
            this.collectionProviders = DaggerCollections.presizedList(n2);
        }
        
        public Builder<T> addCollectionProvider(final Provider<? extends Collection<? extends T>> provider) {
            this.collectionProviders.add((Provider<Collection<T>>)provider);
            return this;
        }
        
        public Builder<T> addProvider(final Provider<? extends T> provider) {
            this.individualProviders.add((Provider<T>)provider);
            return this;
        }
        
        public SetFactory<T> build() {
            return new SetFactory<T>(this.individualProviders, this.collectionProviders, null);
        }
    }
}
