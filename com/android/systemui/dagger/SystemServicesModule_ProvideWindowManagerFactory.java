// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.view.WindowManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideWindowManagerFactory implements Factory<WindowManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideWindowManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideWindowManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideWindowManagerFactory(provider);
    }
    
    public static WindowManager provideInstance(final Provider<Context> provider) {
        return proxyProvideWindowManager(provider.get());
    }
    
    public static WindowManager proxyProvideWindowManager(final Context context) {
        final WindowManager provideWindowManager = SystemServicesModule.provideWindowManager(context);
        Preconditions.checkNotNull(provideWindowManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideWindowManager;
    }
    
    @Override
    public WindowManager get() {
        return provideInstance(this.contextProvider);
    }
}
