// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.Preconditions;
import androidx.slice.Clock;
import dagger.internal.Factory;

public final class AssistModule_ProvideSystemClockFactory implements Factory<Clock>
{
    private static final AssistModule_ProvideSystemClockFactory INSTANCE;
    
    static {
        INSTANCE = new AssistModule_ProvideSystemClockFactory();
    }
    
    public static AssistModule_ProvideSystemClockFactory create() {
        return AssistModule_ProvideSystemClockFactory.INSTANCE;
    }
    
    public static Clock provideInstance() {
        return proxyProvideSystemClock();
    }
    
    public static Clock proxyProvideSystemClock() {
        final Clock provideSystemClock = AssistModule.provideSystemClock();
        Preconditions.checkNotNull(provideSystemClock, "Cannot return null from a non-@Nullable @Provides method");
        return provideSystemClock;
    }
    
    @Override
    public Clock get() {
        return provideInstance();
    }
}
