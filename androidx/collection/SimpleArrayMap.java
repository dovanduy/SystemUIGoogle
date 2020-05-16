// 
// Decompiled by Procyon v0.5.36
// 

package androidx.collection;

import java.util.Map;
import java.util.ConcurrentModificationException;

public class SimpleArrayMap<K, V>
{
    static Object[] mBaseCache;
    static int mBaseCacheSize;
    static Object[] mTwiceBaseCache;
    static int mTwiceBaseCacheSize;
    Object[] mArray;
    int[] mHashes;
    int mSize;
    
    public SimpleArrayMap() {
        this.mHashes = ContainerHelpers.EMPTY_INTS;
        this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        this.mSize = 0;
    }
    
    public SimpleArrayMap(final int n) {
        if (n == 0) {
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        }
        else {
            this.allocArrays(n);
        }
        this.mSize = 0;
    }
    
    private void allocArrays(final int n) {
        Label_0149: {
            if (n == 8) {
                synchronized (SimpleArrayMap.class) {
                    if (SimpleArrayMap.mTwiceBaseCache != null) {
                        final Object[] mTwiceBaseCache = SimpleArrayMap.mTwiceBaseCache;
                        this.mArray = mTwiceBaseCache;
                        SimpleArrayMap.mTwiceBaseCache = (Object[])mTwiceBaseCache[0];
                        this.mHashes = (int[])mTwiceBaseCache[1];
                        mTwiceBaseCache[0] = (mTwiceBaseCache[1] = null);
                        --SimpleArrayMap.mTwiceBaseCacheSize;
                        return;
                    }
                    break Label_0149;
                }
            }
            if (n == 4) {
                synchronized (SimpleArrayMap.class) {
                    if (SimpleArrayMap.mBaseCache != null) {
                        final Object[] mBaseCache = SimpleArrayMap.mBaseCache;
                        this.mArray = mBaseCache;
                        SimpleArrayMap.mBaseCache = (Object[])mBaseCache[0];
                        this.mHashes = (int[])mBaseCache[1];
                        mBaseCache[0] = (mBaseCache[1] = null);
                        --SimpleArrayMap.mBaseCacheSize;
                        return;
                    }
                }
            }
        }
        this.mHashes = new int[n];
        this.mArray = new Object[n << 1];
    }
    
    private static int binarySearchHashes(final int[] array, int binarySearch, final int n) {
        try {
            binarySearch = ContainerHelpers.binarySearch(array, binarySearch, n);
            return binarySearch;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
    }
    
    private static void freeArrays(final int[] array, final Object[] array2, int i) {
        if (array.length == 8) {
            synchronized (SimpleArrayMap.class) {
                if (SimpleArrayMap.mTwiceBaseCacheSize < 10) {
                    array2[0] = SimpleArrayMap.mTwiceBaseCache;
                    array2[1] = array;
                    for (i = (i << 1) - 1; i >= 2; --i) {
                        array2[i] = null;
                    }
                    SimpleArrayMap.mTwiceBaseCache = array2;
                    ++SimpleArrayMap.mTwiceBaseCacheSize;
                }
                return;
            }
        }
        if (array.length == 4) {
            synchronized (SimpleArrayMap.class) {
                if (SimpleArrayMap.mBaseCacheSize < 10) {
                    array2[0] = SimpleArrayMap.mBaseCache;
                    array2[1] = array;
                    for (i = (i << 1) - 1; i >= 2; --i) {
                        array2[i] = null;
                    }
                    SimpleArrayMap.mBaseCache = array2;
                    ++SimpleArrayMap.mBaseCacheSize;
                }
            }
        }
    }
    
    public void clear() {
        final int mSize = this.mSize;
        if (mSize > 0) {
            final int[] mHashes = this.mHashes;
            final Object[] mArray = this.mArray;
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            this.mSize = 0;
            freeArrays(mHashes, mArray, mSize);
        }
        if (this.mSize <= 0) {
            return;
        }
        throw new ConcurrentModificationException();
    }
    
    public boolean containsKey(final Object o) {
        return this.indexOfKey(o) >= 0;
    }
    
    public boolean containsValue(final Object o) {
        return this.indexOfValue(o) >= 0;
    }
    
    public void ensureCapacity(final int n) {
        final int mSize = this.mSize;
        final int[] mHashes = this.mHashes;
        if (mHashes.length < n) {
            final Object[] mArray = this.mArray;
            this.allocArrays(n);
            if (this.mSize > 0) {
                System.arraycopy(mHashes, 0, this.mHashes, 0, mSize);
                System.arraycopy(mArray, 0, this.mArray, 0, mSize << 1);
            }
            freeArrays(mHashes, mArray, mSize);
        }
        if (this.mSize == mSize) {
            return;
        }
        throw new ConcurrentModificationException();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SimpleArrayMap) {
            final SimpleArrayMap simpleArrayMap = (SimpleArrayMap)o;
            if (this.size() != simpleArrayMap.size()) {
                return false;
            }
            int i = 0;
            try {
                while (i < this.mSize) {
                    final K key = this.keyAt(i);
                    o = this.valueAt(i);
                    final Object value = simpleArrayMap.get(key);
                    if (o == null) {
                        if (value != null || !simpleArrayMap.containsKey(key)) {
                            return false;
                        }
                    }
                    else if (!o.equals(value)) {
                        return false;
                    }
                    ++i;
                }
                return true;
            }
            catch (NullPointerException | ClassCastException ex) {
                return false;
            }
        }
        if (!(o instanceof Map)) {
            return false;
        }
        final Map map = (Map)o;
        if (this.size() != map.size()) {
            return false;
        }
        int j = 0;
        try {
            while (j < this.mSize) {
                o = this.keyAt(j);
                final V value2 = this.valueAt(j);
                final Object value3 = map.get(o);
                if (value2 == null) {
                    if (value3 != null || !map.containsKey(o)) {
                        return false;
                    }
                }
                else if (!value2.equals(value3)) {
                    return false;
                }
                ++j;
            }
            return true;
        }
        catch (NullPointerException | ClassCastException ex2) {
            return false;
        }
    }
    
    public V get(final Object o) {
        return this.getOrDefault(o, null);
    }
    
    public V getOrDefault(final Object o, V v) {
        final int indexOfKey = this.indexOfKey(o);
        if (indexOfKey >= 0) {
            v = (V)this.mArray[(indexOfKey << 1) + 1];
        }
        return v;
    }
    
    @Override
    public int hashCode() {
        final int[] mHashes = this.mHashes;
        final Object[] mArray = this.mArray;
        int n2;
        for (int mSize = this.mSize, n = 1, i = n2 = 0; i < mSize; ++i, n += 2) {
            final Object o = mArray[n];
            final int n3 = mHashes[i];
            int hashCode;
            if (o == null) {
                hashCode = 0;
            }
            else {
                hashCode = o.hashCode();
            }
            n2 += (hashCode ^ n3);
        }
        return n2;
    }
    
    int indexOf(final Object o, final int n) {
        final int mSize = this.mSize;
        if (mSize == 0) {
            return -1;
        }
        final int binarySearchHashes = binarySearchHashes(this.mHashes, mSize, n);
        if (binarySearchHashes < 0) {
            return binarySearchHashes;
        }
        if (o.equals(this.mArray[binarySearchHashes << 1])) {
            return binarySearchHashes;
        }
        int n2;
        for (n2 = binarySearchHashes + 1; n2 < mSize && this.mHashes[n2] == n; ++n2) {
            if (o.equals(this.mArray[n2 << 1])) {
                return n2;
            }
        }
        for (int n3 = binarySearchHashes - 1; n3 >= 0 && this.mHashes[n3] == n; --n3) {
            if (o.equals(this.mArray[n3 << 1])) {
                return n3;
            }
        }
        return n2;
    }
    
    public int indexOfKey(final Object o) {
        int n;
        if (o == null) {
            n = this.indexOfNull();
        }
        else {
            n = this.indexOf(o, o.hashCode());
        }
        return n;
    }
    
    int indexOfNull() {
        final int mSize = this.mSize;
        if (mSize == 0) {
            return -1;
        }
        int binarySearchHashes = binarySearchHashes(this.mHashes, mSize, 0);
        if (binarySearchHashes < 0) {
            return binarySearchHashes;
        }
        if (this.mArray[binarySearchHashes << 1] == null) {
            return binarySearchHashes;
        }
        int n;
        for (n = binarySearchHashes + 1; n < mSize && this.mHashes[n] == 0; ++n) {
            if (this.mArray[n << 1] == null) {
                return n;
            }
        }
        --binarySearchHashes;
        while (binarySearchHashes >= 0 && this.mHashes[binarySearchHashes] == 0) {
            if (this.mArray[binarySearchHashes << 1] == null) {
                return binarySearchHashes;
            }
            --binarySearchHashes;
        }
        return n;
    }
    
    int indexOfValue(final Object o) {
        final int n = this.mSize * 2;
        final Object[] mArray = this.mArray;
        if (o == null) {
            for (int i = 1; i < n; i += 2) {
                if (mArray[i] == null) {
                    return i >> 1;
                }
            }
        }
        else {
            for (int j = 1; j < n; j += 2) {
                if (o.equals(mArray[j])) {
                    return j >> 1;
                }
            }
        }
        return -1;
    }
    
    public boolean isEmpty() {
        return this.mSize <= 0;
    }
    
    public K keyAt(final int n) {
        return (K)this.mArray[n << 1];
    }
    
    public V put(final K k, final V v) {
        final int mSize = this.mSize;
        int n;
        int hashCode;
        if (k == null) {
            n = this.indexOfNull();
            hashCode = 0;
        }
        else {
            hashCode = k.hashCode();
            n = this.indexOf(k, hashCode);
        }
        if (n >= 0) {
            final int n2 = (n << 1) + 1;
            final Object[] mArray = this.mArray;
            final Object o = mArray[n2];
            mArray[n2] = v;
            return (V)o;
        }
        final int n3 = n;
        if (mSize >= this.mHashes.length) {
            int n4 = 4;
            if (mSize >= 8) {
                n4 = (mSize >> 1) + mSize;
            }
            else if (mSize >= 4) {
                n4 = 8;
            }
            final int[] mHashes = this.mHashes;
            final Object[] mArray2 = this.mArray;
            this.allocArrays(n4);
            if (mSize != this.mSize) {
                throw new ConcurrentModificationException();
            }
            final int[] mHashes2 = this.mHashes;
            if (mHashes2.length > 0) {
                System.arraycopy(mHashes, 0, mHashes2, 0, mHashes.length);
                System.arraycopy(mArray2, 0, this.mArray, 0, mArray2.length);
            }
            freeArrays(mHashes, mArray2, mSize);
        }
        if (n3 < mSize) {
            final int[] mHashes3 = this.mHashes;
            final int n5 = n3 + 1;
            System.arraycopy(mHashes3, n3, mHashes3, n5, mSize - n3);
            final Object[] mArray3 = this.mArray;
            System.arraycopy(mArray3, n3 << 1, mArray3, n5 << 1, this.mSize - n3 << 1);
        }
        final int mSize2 = this.mSize;
        if (mSize == mSize2) {
            final int[] mHashes4 = this.mHashes;
            if (n3 < mHashes4.length) {
                mHashes4[n3] = hashCode;
                final Object[] mArray4 = this.mArray;
                final int n6 = n3 << 1;
                mArray4[n6] = k;
                mArray4[n6 + 1] = v;
                this.mSize = mSize2 + 1;
                return null;
            }
        }
        throw new ConcurrentModificationException();
    }
    
    public V putIfAbsent(final K k, final V v) {
        Object o;
        if ((o = this.get(k)) == null) {
            o = this.put(k, v);
        }
        return (V)o;
    }
    
    public V remove(final Object o) {
        final int indexOfKey = this.indexOfKey(o);
        if (indexOfKey >= 0) {
            return this.removeAt(indexOfKey);
        }
        return null;
    }
    
    public boolean remove(Object value, final Object o) {
        final int indexOfKey = this.indexOfKey(value);
        if (indexOfKey >= 0) {
            value = this.valueAt(indexOfKey);
            if (o == value || (o != null && o.equals(value))) {
                this.removeAt(indexOfKey);
                return true;
            }
        }
        return false;
    }
    
    public V removeAt(int mSize) {
        final Object[] mArray = this.mArray;
        final int n = mSize << 1;
        final Object o = mArray[n + 1];
        final int mSize2 = this.mSize;
        final int n2 = 0;
        if (mSize2 <= 1) {
            freeArrays(this.mHashes, mArray, mSize2);
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            mSize = n2;
        }
        else {
            final int n3 = mSize2 - 1;
            final int[] mHashes = this.mHashes;
            final int length = mHashes.length;
            int n4 = 8;
            if (length > 8 && mSize2 < mHashes.length / 3) {
                if (mSize2 > 8) {
                    n4 = mSize2 + (mSize2 >> 1);
                }
                final int[] mHashes2 = this.mHashes;
                final Object[] mArray2 = this.mArray;
                this.allocArrays(n4);
                if (mSize2 != this.mSize) {
                    throw new ConcurrentModificationException();
                }
                if (mSize > 0) {
                    System.arraycopy(mHashes2, 0, this.mHashes, 0, mSize);
                    System.arraycopy(mArray2, 0, this.mArray, 0, n);
                }
                if (mSize < n3) {
                    final int n5 = mSize + 1;
                    final int[] mHashes3 = this.mHashes;
                    final int n6 = n3 - mSize;
                    System.arraycopy(mHashes2, n5, mHashes3, mSize, n6);
                    System.arraycopy(mArray2, n5 << 1, this.mArray, n, n6 << 1);
                }
            }
            else {
                if (mSize < n3) {
                    final int[] mHashes4 = this.mHashes;
                    final int n7 = mSize + 1;
                    final int n8 = n3 - mSize;
                    System.arraycopy(mHashes4, n7, mHashes4, mSize, n8);
                    final Object[] mArray3 = this.mArray;
                    System.arraycopy(mArray3, n7 << 1, mArray3, n, n8 << 1);
                }
                final Object[] mArray4 = this.mArray;
                mSize = n3 << 1;
                mArray4[mSize + 1] = (mArray4[mSize] = null);
            }
            mSize = n3;
        }
        if (mSize2 == this.mSize) {
            this.mSize = mSize;
            return (V)o;
        }
        throw new ConcurrentModificationException();
    }
    
    public V replace(final K k, final V v) {
        final int indexOfKey = this.indexOfKey(k);
        if (indexOfKey >= 0) {
            return this.setValueAt(indexOfKey, v);
        }
        return null;
    }
    
    public boolean replace(final K k, final V v, final V v2) {
        final int indexOfKey = this.indexOfKey(k);
        if (indexOfKey >= 0) {
            final V value = this.valueAt(indexOfKey);
            if (value == v || (v != null && v.equals(value))) {
                this.setValueAt(indexOfKey, v2);
                return true;
            }
        }
        return false;
    }
    
    public V setValueAt(int n, final V v) {
        n = (n << 1) + 1;
        final Object[] mArray = this.mArray;
        final Object o = mArray[n];
        mArray[n] = v;
        return (V)o;
    }
    
    public int size() {
        return this.mSize;
    }
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder(this.mSize * 28);
        sb.append('{');
        for (int i = 0; i < this.mSize; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            final K key = this.keyAt(i);
            if (key != this) {
                sb.append(key);
            }
            else {
                sb.append("(this Map)");
            }
            sb.append('=');
            final V value = this.valueAt(i);
            if (value != this) {
                sb.append(value);
            }
            else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }
    
    public V valueAt(final int n) {
        return (V)this.mArray[(n << 1) + 1];
    }
}
