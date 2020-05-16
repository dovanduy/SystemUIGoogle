// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.util.time.SystemClock;
import com.android.systemui.statusbar.notification.collection.listbuilder.ShadeListBuilderLogger;
import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ShadeListBuilder_Factory implements Factory<ShadeListBuilder>
{
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<ShadeListBuilderLogger> loggerProvider;
    private final Provider<SystemClock> systemClockProvider;
    
    public ShadeListBuilder_Factory(final Provider<SystemClock> systemClockProvider, final Provider<ShadeListBuilderLogger> loggerProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.systemClockProvider = systemClockProvider;
        this.loggerProvider = loggerProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static ShadeListBuilder_Factory create(final Provider<SystemClock> provider, final Provider<ShadeListBuilderLogger> provider2, final Provider<DumpManager> provider3) {
        return new ShadeListBuilder_Factory(provider, provider2, provider3);
    }
    
    public static ShadeListBuilder provideInstance(final Provider<SystemClock> provider, final Provider<ShadeListBuilderLogger> provider2, final Provider<DumpManager> provider3) {
        return new ShadeListBuilder(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ShadeListBuilder get() {
        return provideInstance(this.systemClockProvider, this.loggerProvider, this.dumpManagerProvider);
    }
}
