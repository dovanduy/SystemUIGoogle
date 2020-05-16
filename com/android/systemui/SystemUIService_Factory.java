// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.os.Handler;
import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SystemUIService_Factory implements Factory<SystemUIService>
{
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    
    public SystemUIService_Factory(final Provider<Handler> mainHandlerProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.mainHandlerProvider = mainHandlerProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static SystemUIService_Factory create(final Provider<Handler> provider, final Provider<DumpManager> provider2) {
        return new SystemUIService_Factory(provider, provider2);
    }
    
    public static SystemUIService provideInstance(final Provider<Handler> provider, final Provider<DumpManager> provider2) {
        return new SystemUIService(provider.get(), provider2.get());
    }
    
    @Override
    public SystemUIService get() {
        return provideInstance(this.mainHandlerProvider, this.dumpManagerProvider);
    }
}
