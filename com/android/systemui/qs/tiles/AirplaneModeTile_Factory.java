// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AirplaneModeTile_Factory implements Factory<AirplaneModeTile>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<QSHost> hostProvider;
    
    public AirplaneModeTile_Factory(final Provider<QSHost> hostProvider, final Provider<ActivityStarter> activityStarterProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.hostProvider = hostProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static AirplaneModeTile_Factory create(final Provider<QSHost> provider, final Provider<ActivityStarter> provider2, final Provider<BroadcastDispatcher> provider3) {
        return new AirplaneModeTile_Factory(provider, provider2, provider3);
    }
    
    public static AirplaneModeTile provideInstance(final Provider<QSHost> provider, final Provider<ActivityStarter> provider2, final Provider<BroadcastDispatcher> provider3) {
        return new AirplaneModeTile(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public AirplaneModeTile get() {
        return provideInstance(this.hostProvider, this.activityStarterProvider, this.broadcastDispatcherProvider);
    }
}
