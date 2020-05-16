// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DismissCallbackRegistry_Factory implements Factory<DismissCallbackRegistry>
{
    private final Provider<Executor> uiBgExecutorProvider;
    
    public DismissCallbackRegistry_Factory(final Provider<Executor> uiBgExecutorProvider) {
        this.uiBgExecutorProvider = uiBgExecutorProvider;
    }
    
    public static DismissCallbackRegistry_Factory create(final Provider<Executor> provider) {
        return new DismissCallbackRegistry_Factory(provider);
    }
    
    public static DismissCallbackRegistry provideInstance(final Provider<Executor> provider) {
        return new DismissCallbackRegistry(provider.get());
    }
    
    @Override
    public DismissCallbackRegistry get() {
        return provideInstance(this.uiBgExecutorProvider);
    }
}
