// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.policy.ZenModeController;
import android.content.SharedPreferences;
import com.android.systemui.qs.QSHost;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DndTile_Factory implements Factory<DndTile>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<SharedPreferences> sharedPreferencesProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;
    
    public DndTile_Factory(final Provider<QSHost> hostProvider, final Provider<ZenModeController> zenModeControllerProvider, final Provider<ActivityStarter> activityStarterProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<SharedPreferences> sharedPreferencesProvider) {
        this.hostProvider = hostProvider;
        this.zenModeControllerProvider = zenModeControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.sharedPreferencesProvider = sharedPreferencesProvider;
    }
    
    public static DndTile_Factory create(final Provider<QSHost> provider, final Provider<ZenModeController> provider2, final Provider<ActivityStarter> provider3, final Provider<BroadcastDispatcher> provider4, final Provider<SharedPreferences> provider5) {
        return new DndTile_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static DndTile provideInstance(final Provider<QSHost> provider, final Provider<ZenModeController> provider2, final Provider<ActivityStarter> provider3, final Provider<BroadcastDispatcher> provider4, final Provider<SharedPreferences> provider5) {
        return new DndTile(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public DndTile get() {
        return provideInstance(this.hostProvider, this.zenModeControllerProvider, this.activityStarterProvider, this.broadcastDispatcherProvider, this.sharedPreferencesProvider);
    }
}
