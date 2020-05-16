// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GarbageMonitor_Service_Factory implements Factory<GarbageMonitor.Service>
{
    private final Provider<Context> contextProvider;
    private final Provider<GarbageMonitor> garbageMonitorProvider;
    
    public GarbageMonitor_Service_Factory(final Provider<Context> contextProvider, final Provider<GarbageMonitor> garbageMonitorProvider) {
        this.contextProvider = contextProvider;
        this.garbageMonitorProvider = garbageMonitorProvider;
    }
    
    public static GarbageMonitor_Service_Factory create(final Provider<Context> provider, final Provider<GarbageMonitor> provider2) {
        return new GarbageMonitor_Service_Factory(provider, provider2);
    }
    
    public static GarbageMonitor.Service provideInstance(final Provider<Context> provider, final Provider<GarbageMonitor> provider2) {
        return new GarbageMonitor.Service(provider.get(), provider2.get());
    }
    
    @Override
    public GarbageMonitor.Service get() {
        return provideInstance(this.contextProvider, this.garbageMonitorProvider);
    }
}
