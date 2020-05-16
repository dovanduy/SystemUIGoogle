// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Collection;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import java.util.Map;
import kotlin.jvm.functions.Function1;

final class MapWithDefaultImpl<K, V> implements MapWithDefault<K, V>
{
    private final Function1<K, V> default;
    private final Map<K, V> map;
    
    public MapWithDefaultImpl(final Map<K, ? extends V> map, final Function1<? super K, ? extends V> default1) {
        Intrinsics.checkParameterIsNotNull(map, "map");
        Intrinsics.checkParameterIsNotNull(default1, "default");
        this.map = (Map<K, V>)map;
        this.default = (Function1<K, V>)default1;
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.getMap().containsKey(o);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.getMap().containsValue(o);
    }
    
    @Override
    public final /* bridge */ Set<Entry<K, V>> entrySet() {
        return this.getEntries();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.getMap().equals(obj);
    }
    
    @Override
    public V get(final Object o) {
        return this.getMap().get(o);
    }
    
    public Set<Entry<K, V>> getEntries() {
        return this.getMap().entrySet();
    }
    
    public Set<K> getKeys() {
        return this.getMap().keySet();
    }
    
    @Override
    public Map<K, V> getMap() {
        return this.map;
    }
    
    @Override
    public V getOrImplicitDefault(final K k) {
        final Map<K, Object> map = this.getMap();
        Object o2;
        final Object o = o2 = map.get(k);
        if (o == null) {
            o2 = o;
            if (!map.containsKey(k)) {
                o2 = this.default.invoke(k);
            }
        }
        return (V)o2;
    }
    
    public int getSize() {
        return this.getMap().size();
    }
    
    public Collection<V> getValues() {
        return this.getMap().values();
    }
    
    @Override
    public int hashCode() {
        return this.getMap().hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.getMap().isEmpty();
    }
    
    @Override
    public final /* bridge */ Set<K> keySet() {
        return this.getKeys();
    }
    
    @Override
    public V put(final K k, final V v) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public V remove(final Object o) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public final /* bridge */ int size() {
        return this.getSize();
    }
    
    @Override
    public String toString() {
        return this.getMap().toString();
    }
    
    @Override
    public final /* bridge */ Collection<V> values() {
        return this.getValues();
    }
}
