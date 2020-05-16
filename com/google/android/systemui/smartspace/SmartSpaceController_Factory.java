// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

import com.android.keyguard.KeyguardUpdateMonitor;
import android.os.Handler;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import android.app.AlarmManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SmartSpaceController_Factory implements Factory<SmartSpaceController>
{
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    
    public SmartSpaceController_Factory(final Provider<Context> contextProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<Handler> handlerProvider, final Provider<AlarmManager> alarmManagerProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.handlerProvider = handlerProvider;
        this.alarmManagerProvider = alarmManagerProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static SmartSpaceController_Factory create(final Provider<Context> provider, final Provider<KeyguardUpdateMonitor> provider2, final Provider<Handler> provider3, final Provider<AlarmManager> provider4, final Provider<DumpManager> provider5) {
        return new SmartSpaceController_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static SmartSpaceController provideInstance(final Provider<Context> provider, final Provider<KeyguardUpdateMonitor> provider2, final Provider<Handler> provider3, final Provider<AlarmManager> provider4, final Provider<DumpManager> provider5) {
        return new SmartSpaceController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public SmartSpaceController get() {
        return provideInstance(this.contextProvider, this.keyguardUpdateMonitorProvider, this.handlerProvider, this.alarmManagerProvider, this.dumpManagerProvider);
    }
}
