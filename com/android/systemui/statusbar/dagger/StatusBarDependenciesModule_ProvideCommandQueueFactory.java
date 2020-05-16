// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.tracing.ProtoTracer;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.CommandQueue;
import dagger.internal.Factory;

public final class StatusBarDependenciesModule_ProvideCommandQueueFactory implements Factory<CommandQueue>
{
    private final Provider<Context> contextProvider;
    private final Provider<ProtoTracer> protoTracerProvider;
    
    public StatusBarDependenciesModule_ProvideCommandQueueFactory(final Provider<Context> contextProvider, final Provider<ProtoTracer> protoTracerProvider) {
        this.contextProvider = contextProvider;
        this.protoTracerProvider = protoTracerProvider;
    }
    
    public static StatusBarDependenciesModule_ProvideCommandQueueFactory create(final Provider<Context> provider, final Provider<ProtoTracer> provider2) {
        return new StatusBarDependenciesModule_ProvideCommandQueueFactory(provider, provider2);
    }
    
    public static CommandQueue provideInstance(final Provider<Context> provider, final Provider<ProtoTracer> provider2) {
        return proxyProvideCommandQueue(provider.get(), provider2.get());
    }
    
    public static CommandQueue proxyProvideCommandQueue(final Context context, final ProtoTracer protoTracer) {
        final CommandQueue provideCommandQueue = StatusBarDependenciesModule.provideCommandQueue(context, protoTracer);
        Preconditions.checkNotNull(provideCommandQueue, "Cannot return null from a non-@Nullable @Provides method");
        return provideCommandQueue;
    }
    
    @Override
    public CommandQueue get() {
        return provideInstance(this.contextProvider, this.protoTracerProvider);
    }
}
