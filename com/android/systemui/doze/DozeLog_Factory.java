// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DozeLog_Factory implements Factory<DozeLog>
{
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<DozeLogger> loggerProvider;
    
    public DozeLog_Factory(final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<DozeLogger> loggerProvider) {
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.loggerProvider = loggerProvider;
    }
    
    public static DozeLog_Factory create(final Provider<KeyguardUpdateMonitor> provider, final Provider<DumpManager> provider2, final Provider<DozeLogger> provider3) {
        return new DozeLog_Factory(provider, provider2, provider3);
    }
    
    public static DozeLog provideInstance(final Provider<KeyguardUpdateMonitor> provider, final Provider<DumpManager> provider2, final Provider<DozeLogger> provider3) {
        return new DozeLog(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public DozeLog get() {
        return provideInstance(this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.loggerProvider);
    }
}
