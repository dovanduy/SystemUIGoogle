// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.qs.QSHost;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GarbageMonitor_MemoryTile_Factory implements Factory<GarbageMonitor.MemoryTile>
{
    private final Provider<QSHost> hostProvider;
    private final Provider<GarbageMonitor> monitorProvider;
    private final Provider<ActivityStarter> starterProvider;
    
    public GarbageMonitor_MemoryTile_Factory(final Provider<QSHost> hostProvider, final Provider<GarbageMonitor> monitorProvider, final Provider<ActivityStarter> starterProvider) {
        this.hostProvider = hostProvider;
        this.monitorProvider = monitorProvider;
        this.starterProvider = starterProvider;
    }
    
    public static GarbageMonitor_MemoryTile_Factory create(final Provider<QSHost> provider, final Provider<GarbageMonitor> provider2, final Provider<ActivityStarter> provider3) {
        return new GarbageMonitor_MemoryTile_Factory(provider, provider2, provider3);
    }
    
    public static GarbageMonitor.MemoryTile provideInstance(final Provider<QSHost> provider, final Provider<GarbageMonitor> provider2, final Provider<ActivityStarter> provider3) {
        return new GarbageMonitor.MemoryTile(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public GarbageMonitor.MemoryTile get() {
        return provideInstance(this.hostProvider, this.monitorProvider, this.starterProvider);
    }
}
