// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.dock.DockManager;
import android.content.Context;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ClockManager_Factory implements Factory<ClockManager>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<InjectionInflationController> injectionInflaterProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    
    public ClockManager_Factory(final Provider<Context> contextProvider, final Provider<InjectionInflationController> injectionInflaterProvider, final Provider<PluginManager> pluginManagerProvider, final Provider<SysuiColorExtractor> colorExtractorProvider, final Provider<DockManager> dockManagerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.injectionInflaterProvider = injectionInflaterProvider;
        this.pluginManagerProvider = pluginManagerProvider;
        this.colorExtractorProvider = colorExtractorProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static ClockManager_Factory create(final Provider<Context> provider, final Provider<InjectionInflationController> provider2, final Provider<PluginManager> provider3, final Provider<SysuiColorExtractor> provider4, final Provider<DockManager> provider5, final Provider<BroadcastDispatcher> provider6) {
        return new ClockManager_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static ClockManager provideInstance(final Provider<Context> provider, final Provider<InjectionInflationController> provider2, final Provider<PluginManager> provider3, final Provider<SysuiColorExtractor> provider4, final Provider<DockManager> provider5, final Provider<BroadcastDispatcher> provider6) {
        return new ClockManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public ClockManager get() {
        return provideInstance(this.contextProvider, this.injectionInflaterProvider, this.pluginManagerProvider, this.colorExtractorProvider, this.dockManagerProvider, this.broadcastDispatcherProvider);
    }
}
