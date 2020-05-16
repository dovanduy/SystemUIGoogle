// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tracing;

import com.android.systemui.dump.DumpManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ProtoTracer_Factory implements Factory<ProtoTracer>
{
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    
    public ProtoTracer_Factory(final Provider<Context> contextProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static ProtoTracer_Factory create(final Provider<Context> provider, final Provider<DumpManager> provider2) {
        return new ProtoTracer_Factory(provider, provider2);
    }
    
    public static ProtoTracer provideInstance(final Provider<Context> provider, final Provider<DumpManager> provider2) {
        return new ProtoTracer(provider.get(), provider2.get());
    }
    
    @Override
    public ProtoTracer get() {
        return provideInstance(this.contextProvider, this.dumpManagerProvider);
    }
}
