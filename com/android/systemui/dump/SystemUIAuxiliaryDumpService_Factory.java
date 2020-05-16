// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dump;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class SystemUIAuxiliaryDumpService_Factory implements Factory<SystemUIAuxiliaryDumpService>
{
    private final Provider<DumpManager> dumpManagerProvider;
    
    public SystemUIAuxiliaryDumpService_Factory(final Provider<DumpManager> dumpManagerProvider) {
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static SystemUIAuxiliaryDumpService_Factory create(final Provider<DumpManager> provider) {
        return new SystemUIAuxiliaryDumpService_Factory(provider);
    }
    
    public static SystemUIAuxiliaryDumpService provideInstance(final Provider<DumpManager> provider) {
        return new SystemUIAuxiliaryDumpService(provider.get());
    }
    
    @Override
    public SystemUIAuxiliaryDumpService get() {
        return provideInstance(this.dumpManagerProvider);
    }
}
