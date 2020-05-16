// 
// Decompiled by Procyon v0.5.36
// 

package dagger.internal;

import java.util.LinkedHashMap;
import java.util.Collections;
import dagger.Lazy;
import javax.inject.Provider;
import java.util.Map;

public final class MapProviderFactory<K, V> implements Factory<Map<K, Provider<V>>>, Lazy<Map<K, Provider<V>>>
{
    private final Map<K, Provider<V>> contributingMap;
    
    private MapProviderFactory(final Map<K, Provider<V>> m) {
        this.contributingMap = Collections.unmodifiableMap((Map<? extends K, ? extends Provider<V>>)m);
    }
    
    public static <K, V> Builder<K, V> builder(final int n) {
        return new Builder<K, V>(n);
    }
    
    @Override
    public Map<K, Provider<V>> get() {
        return this.contributingMap;
    }
    
    public static final class Builder<K, V>
    {
        private final LinkedHashMap<K, Provider<V>> map;
        
        private Builder(final int n) {
            this.map = DaggerCollections.newLinkedHashMapWithExpectedSize(n);
        }
        
        public MapProviderFactory<K, V> build() {
            return new MapProviderFactory<K, V>(this.map, null);
        }
        
        public Builder<K, V> put(final K key, final Provider<V> value) {
            final LinkedHashMap<K, Provider<V>> map = this.map;
            Preconditions.checkNotNull(key, "key");
            Preconditions.checkNotNull(value, "provider");
            map.put(key, value);
            return this;
        }
    }
}
