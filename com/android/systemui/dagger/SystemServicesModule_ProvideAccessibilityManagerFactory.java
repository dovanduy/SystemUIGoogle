// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.view.accessibility.AccessibilityManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideAccessibilityManagerFactory implements Factory<AccessibilityManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideAccessibilityManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideAccessibilityManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideAccessibilityManagerFactory(provider);
    }
    
    public static AccessibilityManager provideInstance(final Provider<Context> provider) {
        return proxyProvideAccessibilityManager(provider.get());
    }
    
    public static AccessibilityManager proxyProvideAccessibilityManager(final Context context) {
        final AccessibilityManager provideAccessibilityManager = SystemServicesModule.provideAccessibilityManager(context);
        Preconditions.checkNotNull(provideAccessibilityManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideAccessibilityManager;
    }
    
    @Override
    public AccessibilityManager get() {
        return provideInstance(this.contextProvider);
    }
}
