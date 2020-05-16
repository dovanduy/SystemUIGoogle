// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import com.android.internal.util.LatencyTracker;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideLatencyTrackerFactory implements Factory<LatencyTracker>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideLatencyTrackerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideLatencyTrackerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideLatencyTrackerFactory(provider);
    }
    
    public static LatencyTracker provideInstance(final Provider<Context> provider) {
        return proxyProvideLatencyTracker(provider.get());
    }
    
    public static LatencyTracker proxyProvideLatencyTracker(final Context context) {
        final LatencyTracker provideLatencyTracker = SystemServicesModule.provideLatencyTracker(context);
        Preconditions.checkNotNull(provideLatencyTracker, "Cannot return null from a non-@Nullable @Provides method");
        return provideLatencyTracker;
    }
    
    @Override
    public LatencyTracker get() {
        return provideInstance(this.contextProvider);
    }
}
