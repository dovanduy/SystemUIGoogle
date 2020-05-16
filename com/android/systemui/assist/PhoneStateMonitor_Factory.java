// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.BootCompleteCache;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PhoneStateMonitor_Factory implements Factory<PhoneStateMonitor>
{
    private final Provider<BootCompleteCache> bootCompleteCacheProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider;
    
    public PhoneStateMonitor_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider, final Provider<BootCompleteCache> bootCompleteCacheProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.statusBarOptionalLazyProvider = statusBarOptionalLazyProvider;
        this.bootCompleteCacheProvider = bootCompleteCacheProvider;
    }
    
    public static PhoneStateMonitor_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<Optional<Lazy<StatusBar>>> provider3, final Provider<BootCompleteCache> provider4) {
        return new PhoneStateMonitor_Factory(provider, provider2, provider3, provider4);
    }
    
    public static PhoneStateMonitor provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<Optional<Lazy<StatusBar>>> provider3, final Provider<BootCompleteCache> provider4) {
        return new PhoneStateMonitor(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public PhoneStateMonitor get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.statusBarOptionalLazyProvider, this.bootCompleteCacheProvider);
    }
}
