// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dump;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DumpManager_Factory implements Factory<DumpManager>
{
    private final Provider<Context> contextProvider;
    
    public DumpManager_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static DumpManager_Factory create(final Provider<Context> provider) {
        return new DumpManager_Factory(provider);
    }
    
    public static DumpManager provideInstance(final Provider<Context> provider) {
        return new DumpManager(provider.get());
    }
    
    @Override
    public DumpManager get() {
        return provideInstance(this.contextProvider);
    }
}
