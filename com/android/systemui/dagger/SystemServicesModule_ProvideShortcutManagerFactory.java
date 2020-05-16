// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.content.pm.ShortcutManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideShortcutManagerFactory implements Factory<ShortcutManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideShortcutManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideShortcutManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideShortcutManagerFactory(provider);
    }
    
    public static ShortcutManager provideInstance(final Provider<Context> provider) {
        return proxyProvideShortcutManager(provider.get());
    }
    
    public static ShortcutManager proxyProvideShortcutManager(final Context context) {
        final ShortcutManager provideShortcutManager = SystemServicesModule.provideShortcutManager(context);
        Preconditions.checkNotNull(provideShortcutManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideShortcutManager;
    }
    
    @Override
    public ShortcutManager get() {
        return provideInstance(this.contextProvider);
    }
}
