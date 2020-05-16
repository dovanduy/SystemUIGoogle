// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.content.ContentResolver;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideContentResolverFactory implements Factory<ContentResolver>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideContentResolverFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideContentResolverFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideContentResolverFactory(provider);
    }
    
    public static ContentResolver provideInstance(final Provider<Context> provider) {
        return proxyProvideContentResolver(provider.get());
    }
    
    public static ContentResolver proxyProvideContentResolver(final Context context) {
        final ContentResolver provideContentResolver = SystemServicesModule.provideContentResolver(context);
        Preconditions.checkNotNull(provideContentResolver, "Cannot return null from a non-@Nullable @Provides method");
        return provideContentResolver;
    }
    
    @Override
    public ContentResolver get() {
        return provideInstance(this.contextProvider);
    }
}
