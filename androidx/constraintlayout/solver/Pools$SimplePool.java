// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver;

class Pools$SimplePool<T> implements Pools$Pool<T>
{
    private final Object[] mPool;
    private int mPoolSize;
    
    Pools$SimplePool(final int n) {
        if (n > 0) {
            this.mPool = new Object[n];
            return;
        }
        throw new IllegalArgumentException("The max pool size must be > 0");
    }
    
    @Override
    public T acquire() {
        final int mPoolSize = this.mPoolSize;
        if (mPoolSize > 0) {
            final int n = mPoolSize - 1;
            final Object[] mPool = this.mPool;
            final Object o = mPool[n];
            mPool[n] = null;
            this.mPoolSize = mPoolSize - 1;
            return (T)o;
        }
        return null;
    }
    
    @Override
    public boolean release(final T t) {
        final int mPoolSize = this.mPoolSize;
        final Object[] mPool = this.mPool;
        if (mPoolSize < mPool.length) {
            mPool[mPoolSize] = t;
            this.mPoolSize = mPoolSize + 1;
            return true;
        }
        return false;
    }
    
    @Override
    public void releaseAll(final T[] array, int i) {
        int length = i;
        if (i > array.length) {
            length = array.length;
        }
        T t;
        int mPoolSize;
        Object[] mPool;
        for (i = 0; i < length; ++i) {
            t = array[i];
            mPoolSize = this.mPoolSize;
            mPool = this.mPool;
            if (mPoolSize < mPool.length) {
                mPool[mPoolSize] = t;
                this.mPoolSize = mPoolSize + 1;
            }
        }
    }
}
