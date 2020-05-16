// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.app.KeyguardManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideKeyguardManagerFactory implements Factory<KeyguardManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideKeyguardManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideKeyguardManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideKeyguardManagerFactory(provider);
    }
    
    public static KeyguardManager provideInstance(final Provider<Context> provider) {
        return proxyProvideKeyguardManager(provider.get());
    }
    
    public static KeyguardManager proxyProvideKeyguardManager(final Context context) {
        final KeyguardManager provideKeyguardManager = SystemServicesModule.provideKeyguardManager(context);
        Preconditions.checkNotNull(provideKeyguardManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideKeyguardManager;
    }
    
    @Override
    public KeyguardManager get() {
        return provideInstance(this.contextProvider);
    }
}
