// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import java.util.concurrent.Executor;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.dock.DockManager;
import android.util.DisplayMetrics;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class FalsingManagerProxy_Factory implements Factory<FalsingManagerProxy>
{
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> deviceConfigProvider;
    private final Provider<DisplayMetrics> displayMetricsProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Executor> executorProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    private final Provider<ProximitySensor> proximitySensorProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    
    public FalsingManagerProxy_Factory(final Provider<Context> contextProvider, final Provider<PluginManager> pluginManagerProvider, final Provider<Executor> executorProvider, final Provider<DisplayMetrics> displayMetricsProvider, final Provider<ProximitySensor> proximitySensorProvider, final Provider<DeviceConfigProxy> deviceConfigProvider, final Provider<DockManager> dockManagerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<Executor> uiBgExecutorProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider) {
        this.contextProvider = contextProvider;
        this.pluginManagerProvider = pluginManagerProvider;
        this.executorProvider = executorProvider;
        this.displayMetricsProvider = displayMetricsProvider;
        this.proximitySensorProvider = proximitySensorProvider;
        this.deviceConfigProvider = deviceConfigProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.uiBgExecutorProvider = uiBgExecutorProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
    }
    
    public static FalsingManagerProxy_Factory create(final Provider<Context> provider, final Provider<PluginManager> provider2, final Provider<Executor> provider3, final Provider<DisplayMetrics> provider4, final Provider<ProximitySensor> provider5, final Provider<DeviceConfigProxy> provider6, final Provider<DockManager> provider7, final Provider<KeyguardUpdateMonitor> provider8, final Provider<DumpManager> provider9, final Provider<Executor> provider10, final Provider<StatusBarStateController> provider11) {
        return new FalsingManagerProxy_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
    }
    
    public static FalsingManagerProxy provideInstance(final Provider<Context> provider, final Provider<PluginManager> provider2, final Provider<Executor> provider3, final Provider<DisplayMetrics> provider4, final Provider<ProximitySensor> provider5, final Provider<DeviceConfigProxy> provider6, final Provider<DockManager> provider7, final Provider<KeyguardUpdateMonitor> provider8, final Provider<DumpManager> provider9, final Provider<Executor> provider10, final Provider<StatusBarStateController> provider11) {
        return new FalsingManagerProxy(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get());
    }
    
    @Override
    public FalsingManagerProxy get() {
        return provideInstance(this.contextProvider, this.pluginManagerProvider, this.executorProvider, this.displayMetricsProvider, this.proximitySensorProvider, this.deviceConfigProvider, this.dockManagerProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.uiBgExecutorProvider, this.statusBarStateControllerProvider);
    }
}
