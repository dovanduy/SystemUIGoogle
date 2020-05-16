// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.log.LogcatEchoTracker;
import javax.inject.Provider;
import com.android.systemui.log.LogBuffer;
import dagger.internal.Factory;

public final class LogModule_ProvideDozeLogBufferFactory implements Factory<LogBuffer>
{
    private final Provider<LogcatEchoTracker> bufferFilterProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    
    public LogModule_ProvideDozeLogBufferFactory(final Provider<LogcatEchoTracker> bufferFilterProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.bufferFilterProvider = bufferFilterProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static LogModule_ProvideDozeLogBufferFactory create(final Provider<LogcatEchoTracker> provider, final Provider<DumpManager> provider2) {
        return new LogModule_ProvideDozeLogBufferFactory(provider, provider2);
    }
    
    public static LogBuffer provideInstance(final Provider<LogcatEchoTracker> provider, final Provider<DumpManager> provider2) {
        return proxyProvideDozeLogBuffer(provider.get(), provider2.get());
    }
    
    public static LogBuffer proxyProvideDozeLogBuffer(final LogcatEchoTracker logcatEchoTracker, final DumpManager dumpManager) {
        final LogBuffer provideDozeLogBuffer = LogModule.provideDozeLogBuffer(logcatEchoTracker, dumpManager);
        Preconditions.checkNotNull(provideDozeLogBuffer, "Cannot return null from a non-@Nullable @Provides method");
        return provideDozeLogBuffer;
    }
    
    @Override
    public LogBuffer get() {
        return provideInstance(this.bufferFilterProvider, this.dumpManagerProvider);
    }
}
