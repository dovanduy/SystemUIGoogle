// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import com.android.systemui.util.sensors.AsyncSensorManager;
import javax.inject.Provider;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import dagger.internal.Factory;

public final class SystemUIModule_ProvideKeyguardLiftControllerFactory implements Factory<KeyguardLiftController>
{
    private final Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public SystemUIModule_ProvideKeyguardLiftControllerFactory(final Provider<Context> contextProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<AsyncSensorManager> asyncSensorManagerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.asyncSensorManagerProvider = asyncSensorManagerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static SystemUIModule_ProvideKeyguardLiftControllerFactory create(final Provider<Context> provider, final Provider<StatusBarStateController> provider2, final Provider<AsyncSensorManager> provider3, final Provider<KeyguardUpdateMonitor> provider4, final Provider<DumpManager> provider5) {
        return new SystemUIModule_ProvideKeyguardLiftControllerFactory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static KeyguardLiftController provideInstance(final Provider<Context> provider, final Provider<StatusBarStateController> provider2, final Provider<AsyncSensorManager> provider3, final Provider<KeyguardUpdateMonitor> provider4, final Provider<DumpManager> provider5) {
        return proxyProvideKeyguardLiftController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    public static KeyguardLiftController proxyProvideKeyguardLiftController(final Context context, final StatusBarStateController statusBarStateController, final AsyncSensorManager asyncSensorManager, final KeyguardUpdateMonitor keyguardUpdateMonitor, final DumpManager dumpManager) {
        return SystemUIModule.provideKeyguardLiftController(context, statusBarStateController, asyncSensorManager, keyguardUpdateMonitor, dumpManager);
    }
    
    @Override
    public KeyguardLiftController get() {
        return provideInstance(this.contextProvider, this.statusBarStateControllerProvider, this.asyncSensorManagerProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider);
    }
}
