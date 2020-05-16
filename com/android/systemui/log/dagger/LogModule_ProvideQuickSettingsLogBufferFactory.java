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

public final class LogModule_ProvideQuickSettingsLogBufferFactory implements Factory<LogBuffer>
{
    private final Provider<LogcatEchoTracker> bufferFilterProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    
    public LogModule_ProvideQuickSettingsLogBufferFactory(final Provider<LogcatEchoTracker> bufferFilterProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.bufferFilterProvider = bufferFilterProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static LogModule_ProvideQuickSettingsLogBufferFactory create(final Provider<LogcatEchoTracker> provider, final Provider<DumpManager> provider2) {
        return new LogModule_ProvideQuickSettingsLogBufferFactory(provider, provider2);
    }
    
    public static LogBuffer provideInstance(final Provider<LogcatEchoTracker> provider, final Provider<DumpManager> provider2) {
        return proxyProvideQuickSettingsLogBuffer(provider.get(), provider2.get());
    }
    
    public static LogBuffer proxyProvideQuickSettingsLogBuffer(final LogcatEchoTracker logcatEchoTracker, final DumpManager dumpManager) {
        final LogBuffer provideQuickSettingsLogBuffer = LogModule.provideQuickSettingsLogBuffer(logcatEchoTracker, dumpManager);
        Preconditions.checkNotNull(provideQuickSettingsLogBuffer, "Cannot return null from a non-@Nullable @Provides method");
        return provideQuickSettingsLogBuffer;
    }
    
    @Override
    public LogBuffer get() {
        return provideInstance(this.bufferFilterProvider, this.dumpManagerProvider);
    }
}
