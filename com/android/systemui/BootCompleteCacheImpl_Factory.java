// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BootCompleteCacheImpl_Factory implements Factory<BootCompleteCacheImpl>
{
    private final Provider<DumpManager> dumpManagerProvider;
    
    public BootCompleteCacheImpl_Factory(final Provider<DumpManager> dumpManagerProvider) {
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static BootCompleteCacheImpl_Factory create(final Provider<DumpManager> provider) {
        return new BootCompleteCacheImpl_Factory(provider);
    }
    
    public static BootCompleteCacheImpl provideInstance(final Provider<DumpManager> provider) {
        return new BootCompleteCacheImpl(provider.get());
    }
    
    @Override
    public BootCompleteCacheImpl get() {
        return provideInstance(this.dumpManagerProvider);
    }
}
