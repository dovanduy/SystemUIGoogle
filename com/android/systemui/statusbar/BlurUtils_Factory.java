// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.content.res.Resources;
import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BlurUtils_Factory implements Factory<BlurUtils>
{
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Resources> resourcesProvider;
    
    public BlurUtils_Factory(final Provider<Resources> resourcesProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.resourcesProvider = resourcesProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static BlurUtils_Factory create(final Provider<Resources> provider, final Provider<DumpManager> provider2) {
        return new BlurUtils_Factory(provider, provider2);
    }
    
    public static BlurUtils provideInstance(final Provider<Resources> provider, final Provider<DumpManager> provider2) {
        return new BlurUtils(provider.get(), provider2.get());
    }
    
    @Override
    public BlurUtils get() {
        return provideInstance(this.resourcesProvider, this.dumpManagerProvider);
    }
}
