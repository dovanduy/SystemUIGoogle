// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.appops;

import com.android.systemui.dump.DumpManager;
import android.content.Context;
import android.os.Looper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AppOpsControllerImpl_Factory implements Factory<AppOpsControllerImpl>
{
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    
    public AppOpsControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Looper> bgLooperProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static AppOpsControllerImpl_Factory create(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<DumpManager> provider3) {
        return new AppOpsControllerImpl_Factory(provider, provider2, provider3);
    }
    
    public static AppOpsControllerImpl provideInstance(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<DumpManager> provider3) {
        return new AppOpsControllerImpl(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public AppOpsControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.dumpManagerProvider);
    }
}
