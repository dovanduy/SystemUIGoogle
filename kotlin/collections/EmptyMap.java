// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.Collection;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;
import java.io.Serializable;
import java.util.Map;

final class EmptyMap implements Map, Serializable, KMappedMarker
{
    public static final EmptyMap INSTANCE;
    private static final long serialVersionUID = 8246714829545688274L;
    
    static {
        INSTANCE = new EmptyMap();
    }
    
    private EmptyMap() {
    }
    
    private final Object readResolve() {
        return EmptyMap.INSTANCE;
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return false;
    }
    
    @Override
    public final /* bridge */ boolean containsValue(final Object o) {
        return o instanceof Void && this.containsValue((Void)o);
    }
    
    public boolean containsValue(final Void void1) {
        Intrinsics.checkParameterIsNotNull(void1, "value");
        return false;
    }
    
    @Override
    public final /* bridge */ Set<Entry> entrySet() {
        return this.getEntries();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Map && ((Map)o).isEmpty();
    }
    
    @Override
    public final /* bridge */ Object get(final Object o) {
        return this.get(o);
    }
    
    @Override
    public Void get(final Object o) {
        return null;
    }
    
    public Set<Entry> getEntries() {
        return (Set<Entry>)EmptySet.INSTANCE;
    }
    
    public Set<Object> getKeys() {
        return (Set<Object>)EmptySet.INSTANCE;
    }
    
    public int getSize() {
        return 0;
    }
    
    public Collection getValues() {
        return EmptyList.INSTANCE;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public final /* bridge */ Set<Object> keySet() {
        return this.getKeys();
    }
    
    @Override
    public void putAll(final Map map) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public Object remove(final Object o) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
    
    @Override
    public final /* bridge */ int size() {
        return this.getSize();
    }
    
    @Override
    public String toString() {
        return "{}";
    }
    
    @Override
    public final /* bridge */ Collection values() {
        return this.getValues();
    }
}
