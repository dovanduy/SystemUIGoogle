// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Factory;

public final class ColumbusModule_ProvideTransientGateDurationFactory implements Factory<Long>
{
    private static final ColumbusModule_ProvideTransientGateDurationFactory INSTANCE;
    
    static {
        INSTANCE = new ColumbusModule_ProvideTransientGateDurationFactory();
    }
    
    public static ColumbusModule_ProvideTransientGateDurationFactory create() {
        return ColumbusModule_ProvideTransientGateDurationFactory.INSTANCE;
    }
    
    public static Long provideInstance() {
        return proxyProvideTransientGateDuration();
    }
    
    public static long proxyProvideTransientGateDuration() {
        return ColumbusModule.provideTransientGateDuration();
    }
    
    @Override
    public Long get() {
        return provideInstance();
    }
}
