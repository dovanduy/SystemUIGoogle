// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import dagger.internal.Factory;

public final class TransactionPool_Factory implements Factory<TransactionPool>
{
    private static final TransactionPool_Factory INSTANCE;
    
    static {
        INSTANCE = new TransactionPool_Factory();
    }
    
    public static TransactionPool_Factory create() {
        return TransactionPool_Factory.INSTANCE;
    }
    
    public static TransactionPool provideInstance() {
        return new TransactionPool();
    }
    
    @Override
    public TransactionPool get() {
        return provideInstance();
    }
}
