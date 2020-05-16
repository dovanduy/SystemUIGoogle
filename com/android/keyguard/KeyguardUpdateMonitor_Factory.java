// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.os.Looper;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardUpdateMonitor_Factory implements Factory<KeyguardUpdateMonitor>
{
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Looper> mainLooperProvider;
    
    public KeyguardUpdateMonitor_Factory(final Provider<Context> contextProvider, final Provider<Looper> mainLooperProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<Executor> backgroundExecutorProvider) {
        this.contextProvider = contextProvider;
        this.mainLooperProvider = mainLooperProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.backgroundExecutorProvider = backgroundExecutorProvider;
    }
    
    public static KeyguardUpdateMonitor_Factory create(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<DumpManager> provider4, final Provider<Executor> provider5) {
        return new KeyguardUpdateMonitor_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static KeyguardUpdateMonitor provideInstance(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<DumpManager> provider4, final Provider<Executor> provider5) {
        return new KeyguardUpdateMonitor(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public KeyguardUpdateMonitor get() {
        return provideInstance(this.contextProvider, this.mainLooperProvider, this.broadcastDispatcherProvider, this.dumpManagerProvider, this.backgroundExecutorProvider);
    }
}
