// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NfcTile_Factory implements Factory<NfcTile>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<QSHost> hostProvider;
    
    public NfcTile_Factory(final Provider<QSHost> hostProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.hostProvider = hostProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static NfcTile_Factory create(final Provider<QSHost> provider, final Provider<BroadcastDispatcher> provider2) {
        return new NfcTile_Factory(provider, provider2);
    }
    
    public static NfcTile provideInstance(final Provider<QSHost> provider, final Provider<BroadcastDispatcher> provider2) {
        return new NfcTile(provider.get(), provider2.get());
    }
    
    @Override
    public NfcTile get() {
        return provideInstance(this.hostProvider, this.broadcastDispatcherProvider);
    }
}
