// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.os.Looper;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideBgLooperFactory implements Factory<Looper>
{
    private static final ConcurrencyModule_ProvideBgLooperFactory INSTANCE;
    
    static {
        INSTANCE = new ConcurrencyModule_ProvideBgLooperFactory();
    }
    
    public static ConcurrencyModule_ProvideBgLooperFactory create() {
        return ConcurrencyModule_ProvideBgLooperFactory.INSTANCE;
    }
    
    public static Looper provideInstance() {
        return proxyProvideBgLooper();
    }
    
    public static Looper proxyProvideBgLooper() {
        final Looper provideBgLooper = ConcurrencyModule.provideBgLooper();
        Preconditions.checkNotNull(provideBgLooper, "Cannot return null from a non-@Nullable @Provides method");
        return provideBgLooper;
    }
    
    @Override
    public Looper get() {
        return provideInstance();
    }
}
