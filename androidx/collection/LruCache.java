// 
// Decompiled by Procyon v0.5.36
// 

package androidx.collection;

import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;

public class LruCache<K, V>
{
    private int createCount;
    private int evictionCount;
    private int hitCount;
    private final LinkedHashMap<K, V> map;
    private int maxSize;
    private int missCount;
    private int putCount;
    private int size;
    
    public LruCache(final int maxSize) {
        if (maxSize > 0) {
            this.maxSize = maxSize;
            this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }
    
    private int safeSizeOf(final K obj, final V obj2) {
        final int size = this.sizeOf(obj, obj2);
        if (size >= 0) {
            return size;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Negative size: ");
        sb.append(obj);
        sb.append("=");
        sb.append(obj2);
        throw new IllegalStateException(sb.toString());
    }
    
    protected V create(final K k) {
        return null;
    }
    
    protected void entryRemoved(final boolean b, final K k, final V v, final V v2) {
    }
    
    public final void evictAll() {
        this.trimToSize(-1);
    }
    
    public final V get(final K key) {
        if (key != null) {
            synchronized (this) {
                final V value = this.map.get(key);
                if (value != null) {
                    ++this.hitCount;
                    return value;
                }
                ++this.missCount;
                // monitorexit(this)
                final V create = this.create(key);
                if (create == null) {
                    return null;
                }
                synchronized (this) {
                    ++this.createCount;
                    final V put = this.map.put(key, create);
                    if (put != null) {
                        this.map.put(key, put);
                    }
                    else {
                        this.size += this.safeSizeOf(key, create);
                    }
                    // monitorexit(this)
                    if (put != null) {
                        this.entryRemoved(false, key, create, put);
                        return put;
                    }
                    this.trimToSize(this.maxSize);
                    return create;
                }
            }
        }
        throw new NullPointerException("key == null");
    }
    
    public final V put(final K key, final V value) {
        if (key != null && value != null) {
            synchronized (this) {
                ++this.putCount;
                this.size += this.safeSizeOf(key, value);
                final V put = this.map.put(key, value);
                if (put != null) {
                    this.size -= this.safeSizeOf(key, put);
                }
                // monitorexit(this)
                if (put != null) {
                    this.entryRemoved(false, key, put, value);
                }
                this.trimToSize(this.maxSize);
                return put;
            }
        }
        throw new NullPointerException("key == null || value == null");
    }
    
    public final V remove(final K key) {
        if (key != null) {
            synchronized (this) {
                final V remove = this.map.remove(key);
                if (remove != null) {
                    this.size -= this.safeSizeOf(key, remove);
                }
                // monitorexit(this)
                if (remove != null) {
                    this.entryRemoved(false, key, remove, null);
                }
                return remove;
            }
        }
        throw new NullPointerException("key == null");
    }
    
    public final int size() {
        synchronized (this) {
            return this.size;
        }
    }
    
    protected int sizeOf(final K k, final V v) {
        return 1;
    }
    
    public final Map<K, V> snapshot() {
        synchronized (this) {
            return new LinkedHashMap<K, V>((Map<? extends K, ? extends V>)this.map);
        }
    }
    
    @Override
    public final String toString() {
        synchronized (this) {
            final int n = this.hitCount + this.missCount;
            int i;
            if (n != 0) {
                i = this.hitCount * 100 / n;
            }
            else {
                i = 0;
            }
            return String.format(Locale.US, "LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", this.maxSize, this.hitCount, this.missCount, i);
        }
    }
    
    public void trimToSize(final int n) {
        while (true) {
            synchronized (this) {
                if (this.size < 0 || (this.map.isEmpty() && this.size != 0)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(this.getClass().getName());
                    sb.append(".sizeOf() is reporting inconsistent results!");
                    throw new IllegalStateException(sb.toString());
                }
                if (this.size <= n || this.map.isEmpty()) {
                    return;
                }
                final Map.Entry<K, V> entry = this.map.entrySet().iterator().next();
                final K key = entry.getKey();
                final V value = entry.getValue();
                this.map.remove(key);
                this.size -= this.safeSizeOf(key, value);
                ++this.evictionCount;
                // monitorexit(this)
                this.entryRemoved(true, key, value, null);
            }
        }
    }
}
