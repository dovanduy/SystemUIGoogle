// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import java.util.Map;
import java.util.Set;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class WeakIdentityHashMap<K, V>
{
    private final HashMap<WeakReference<K>, V> mMap;
    private final ReferenceQueue<Object> mRefQueue;
    
    public WeakIdentityHashMap() {
        this.mMap = new HashMap<WeakReference<K>, V>();
        this.mRefQueue = new ReferenceQueue<Object>();
    }
    
    private void cleanUp() {
        while (true) {
            final Reference<?> poll = this.mRefQueue.poll();
            if (poll == null) {
                break;
            }
            this.mMap.remove(poll);
        }
    }
    
    public Set<Map.Entry<WeakReference<K>, V>> entrySet() {
        return this.mMap.entrySet();
    }
    
    public V get(final K k) {
        this.cleanUp();
        return this.mMap.get(new CmpWeakReference(k));
    }
    
    public boolean isEmpty() {
        this.cleanUp();
        return this.mMap.isEmpty();
    }
    
    public void put(final K k, final V value) {
        this.cleanUp();
        this.mMap.put(new CmpWeakReference<K>(k, this.mRefQueue), value);
    }
    
    public int size() {
        this.cleanUp();
        return this.mMap.size();
    }
    
    private static class CmpWeakReference<K> extends WeakReference<K>
    {
        private final int mHashCode;
        
        public CmpWeakReference(final K referent) {
            super(referent);
            this.mHashCode = System.identityHashCode(referent);
        }
        
        public CmpWeakReference(final K referent, final ReferenceQueue<Object> q) {
            super(referent, q);
            this.mHashCode = System.identityHashCode(referent);
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = true;
            if (o == this) {
                return true;
            }
            final Object value = this.get();
            if (value != null && o instanceof CmpWeakReference) {
                if (((CmpWeakReference)o).get() != value) {
                    b = false;
                }
                return b;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.mHashCode;
        }
    }
}
