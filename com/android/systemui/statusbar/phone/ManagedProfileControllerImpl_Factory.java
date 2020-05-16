// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ManagedProfileControllerImpl_Factory implements Factory<ManagedProfileControllerImpl>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    
    public ManagedProfileControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static ManagedProfileControllerImpl_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2) {
        return new ManagedProfileControllerImpl_Factory(provider, provider2);
    }
    
    public static ManagedProfileControllerImpl provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2) {
        return new ManagedProfileControllerImpl(provider.get(), provider2.get());
    }
    
    @Override
    public ManagedProfileControllerImpl get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider);
    }
}
