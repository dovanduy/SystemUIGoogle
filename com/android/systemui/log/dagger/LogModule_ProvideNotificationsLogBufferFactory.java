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

public final class LogModule_ProvideNotificationsLogBufferFactory implements Factory<LogBuffer>
{
    private final Provider<LogcatEchoTracker> bufferFilterProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    
    public LogModule_ProvideNotificationsLogBufferFactory(final Provider<LogcatEchoTracker> bufferFilterProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.bufferFilterProvider = bufferFilterProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static LogModule_ProvideNotificationsLogBufferFactory create(final Provider<LogcatEchoTracker> provider, final Provider<DumpManager> provider2) {
        return new LogModule_ProvideNotificationsLogBufferFactory(provider, provider2);
    }
    
    public static LogBuffer provideInstance(final Provider<LogcatEchoTracker> provider, final Provider<DumpManager> provider2) {
        return proxyProvideNotificationsLogBuffer(provider.get(), provider2.get());
    }
    
    public static LogBuffer proxyProvideNotificationsLogBuffer(final LogcatEchoTracker logcatEchoTracker, final DumpManager dumpManager) {
        final LogBuffer provideNotificationsLogBuffer = LogModule.provideNotificationsLogBuffer(logcatEchoTracker, dumpManager);
        Preconditions.checkNotNull(provideNotificationsLogBuffer, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationsLogBuffer;
    }
    
    @Override
    public LogBuffer get() {
        return provideInstance(this.bufferFilterProvider, this.dumpManagerProvider);
    }
}
