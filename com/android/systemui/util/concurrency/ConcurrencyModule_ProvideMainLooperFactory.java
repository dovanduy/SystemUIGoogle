// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.os.Looper;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideMainLooperFactory implements Factory<Looper>
{
    private static final ConcurrencyModule_ProvideMainLooperFactory INSTANCE;
    
    static {
        INSTANCE = new ConcurrencyModule_ProvideMainLooperFactory();
    }
    
    public static ConcurrencyModule_ProvideMainLooperFactory create() {
        return ConcurrencyModule_ProvideMainLooperFactory.INSTANCE;
    }
    
    public static Looper provideInstance() {
        return proxyProvideMainLooper();
    }
    
    public static Looper proxyProvideMainLooper() {
        final Looper provideMainLooper = ConcurrencyModule.provideMainLooper();
        Preconditions.checkNotNull(provideMainLooper, "Cannot return null from a non-@Nullable @Provides method");
        return provideMainLooper;
    }
    
    @Override
    public Looper get() {
        return provideInstance();
    }
}
