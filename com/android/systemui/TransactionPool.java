// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.view.SurfaceControl$Transaction;
import android.util.Pools$SynchronizedPool;

public class TransactionPool
{
    private final Pools$SynchronizedPool<SurfaceControl$Transaction> mTransactionPool;
    
    TransactionPool() {
        this.mTransactionPool = (Pools$SynchronizedPool<SurfaceControl$Transaction>)new Pools$SynchronizedPool(4);
    }
    
    public SurfaceControl$Transaction acquire() {
        SurfaceControl$Transaction surfaceControl$Transaction;
        if ((surfaceControl$Transaction = (SurfaceControl$Transaction)this.mTransactionPool.acquire()) == null) {
            surfaceControl$Transaction = new SurfaceControl$Transaction();
        }
        return surfaceControl$Transaction;
    }
    
    public void release(final SurfaceControl$Transaction surfaceControl$Transaction) {
        if (!this.mTransactionPool.release((Object)surfaceControl$Transaction)) {
            surfaceControl$Transaction.close();
        }
    }
}
