// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.StatusBar;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PowerUI_Factory implements Factory<PowerUI>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<StatusBar> statusBarLazyProvider;
    
    public PowerUI_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<StatusBar> statusBarLazyProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.statusBarLazyProvider = statusBarLazyProvider;
    }
    
    public static PowerUI_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<CommandQueue> provider3, final Provider<StatusBar> provider4) {
        return new PowerUI_Factory(provider, provider2, provider3, provider4);
    }
    
    public static PowerUI provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<CommandQueue> provider3, final Provider<StatusBar> provider4) {
        return new PowerUI(provider.get(), provider2.get(), provider3.get(), DoubleCheck.lazy(provider4));
    }
    
    @Override
    public PowerUI get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.commandQueueProvider, this.statusBarLazyProvider);
    }
}
