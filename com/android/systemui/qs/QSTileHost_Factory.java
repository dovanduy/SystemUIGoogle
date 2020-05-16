// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import com.android.systemui.tuner.TunerService;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.Optional;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.shared.plugins.PluginManager;
import android.os.Handler;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.qs.QSFactory;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Looper;
import com.android.systemui.statusbar.phone.AutoTileManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class QSTileHost_Factory implements Factory<QSTileHost>
{
    private final Provider<AutoTileManager> autoTilesProvider;
    private final Provider<Looper> bgLooperProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<QSFactory> defaultFactoryProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<StatusBarIconController> iconControllerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    private final Provider<QSLogger> qsLoggerProvider;
    private final Provider<Optional<StatusBar>> statusBarOptionalProvider;
    private final Provider<TunerService> tunerServiceProvider;
    
    public QSTileHost_Factory(final Provider<Context> contextProvider, final Provider<StatusBarIconController> iconControllerProvider, final Provider<QSFactory> defaultFactoryProvider, final Provider<Handler> mainHandlerProvider, final Provider<Looper> bgLooperProvider, final Provider<PluginManager> pluginManagerProvider, final Provider<TunerService> tunerServiceProvider, final Provider<AutoTileManager> autoTilesProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Optional<StatusBar>> statusBarOptionalProvider, final Provider<QSLogger> qsLoggerProvider) {
        this.contextProvider = contextProvider;
        this.iconControllerProvider = iconControllerProvider;
        this.defaultFactoryProvider = defaultFactoryProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.pluginManagerProvider = pluginManagerProvider;
        this.tunerServiceProvider = tunerServiceProvider;
        this.autoTilesProvider = autoTilesProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.statusBarOptionalProvider = statusBarOptionalProvider;
        this.qsLoggerProvider = qsLoggerProvider;
    }
    
    public static QSTileHost_Factory create(final Provider<Context> provider, final Provider<StatusBarIconController> provider2, final Provider<QSFactory> provider3, final Provider<Handler> provider4, final Provider<Looper> provider5, final Provider<PluginManager> provider6, final Provider<TunerService> provider7, final Provider<AutoTileManager> provider8, final Provider<DumpManager> provider9, final Provider<BroadcastDispatcher> provider10, final Provider<Optional<StatusBar>> provider11, final Provider<QSLogger> provider12) {
        return new QSTileHost_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12);
    }
    
    public static QSTileHost provideInstance(final Provider<Context> provider, final Provider<StatusBarIconController> provider2, final Provider<QSFactory> provider3, final Provider<Handler> provider4, final Provider<Looper> provider5, final Provider<PluginManager> provider6, final Provider<TunerService> provider7, final Provider<AutoTileManager> provider8, final Provider<DumpManager> provider9, final Provider<BroadcastDispatcher> provider10, final Provider<Optional<StatusBar>> provider11, final Provider<QSLogger> provider12) {
        return new QSTileHost(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8, provider9.get(), provider10.get(), provider11.get(), provider12.get());
    }
    
    @Override
    public QSTileHost get() {
        return provideInstance(this.contextProvider, this.iconControllerProvider, this.defaultFactoryProvider, this.mainHandlerProvider, this.bgLooperProvider, this.pluginManagerProvider, this.tunerServiceProvider, this.autoTilesProvider, this.dumpManagerProvider, this.broadcastDispatcherProvider, this.statusBarOptionalProvider, this.qsLoggerProvider);
    }
}
