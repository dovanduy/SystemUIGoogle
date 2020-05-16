// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.os.Handler;
import com.android.systemui.util.leak.LeakDetector;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class TunerServiceImpl_Factory implements Factory<TunerServiceImpl>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<Handler> mainHandlerProvider;
    
    public TunerServiceImpl_Factory(final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider, final Provider<LeakDetector> leakDetectorProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.leakDetectorProvider = leakDetectorProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static TunerServiceImpl_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<LeakDetector> provider3, final Provider<BroadcastDispatcher> provider4) {
        return new TunerServiceImpl_Factory(provider, provider2, provider3, provider4);
    }
    
    public static TunerServiceImpl provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<LeakDetector> provider3, final Provider<BroadcastDispatcher> provider4) {
        return new TunerServiceImpl(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public TunerServiceImpl get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.leakDetectorProvider, this.broadcastDispatcherProvider);
    }
}
