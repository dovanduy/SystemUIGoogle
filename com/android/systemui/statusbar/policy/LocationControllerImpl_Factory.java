// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.BootCompleteCache;
import android.os.Looper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LocationControllerImpl_Factory implements Factory<LocationControllerImpl>
{
    private final Provider<Looper> bgLooperProvider;
    private final Provider<BootCompleteCache> bootCompleteCacheProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    
    public LocationControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Looper> bgLooperProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<BootCompleteCache> bootCompleteCacheProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.bootCompleteCacheProvider = bootCompleteCacheProvider;
    }
    
    public static LocationControllerImpl_Factory create(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<BootCompleteCache> provider4) {
        return new LocationControllerImpl_Factory(provider, provider2, provider3, provider4);
    }
    
    public static LocationControllerImpl provideInstance(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<BootCompleteCache> provider4) {
        return new LocationControllerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public LocationControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.broadcastDispatcherProvider, this.bootCompleteCacheProvider);
    }
}
