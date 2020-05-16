// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.broadcast;

import android.os.Handler;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import android.os.Looper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BroadcastDispatcher_Factory implements Factory<BroadcastDispatcher>
{
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    
    public BroadcastDispatcher_Factory(final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider, final Provider<Looper> bgLooperProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static BroadcastDispatcher_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<Looper> provider3, final Provider<DumpManager> provider4) {
        return new BroadcastDispatcher_Factory(provider, provider2, provider3, provider4);
    }
    
    public static BroadcastDispatcher provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<Looper> provider3, final Provider<DumpManager> provider4) {
        return new BroadcastDispatcher(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public BroadcastDispatcher get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.bgLooperProvider, this.dumpManagerProvider);
    }
}
