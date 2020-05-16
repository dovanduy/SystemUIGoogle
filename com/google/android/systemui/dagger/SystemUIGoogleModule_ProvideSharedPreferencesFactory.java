// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.content.SharedPreferences;
import dagger.internal.Factory;

public final class SystemUIGoogleModule_ProvideSharedPreferencesFactory implements Factory<SharedPreferences>
{
    private final Provider<Context> contextProvider;
    
    public SystemUIGoogleModule_ProvideSharedPreferencesFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemUIGoogleModule_ProvideSharedPreferencesFactory create(final Provider<Context> provider) {
        return new SystemUIGoogleModule_ProvideSharedPreferencesFactory(provider);
    }
    
    public static SharedPreferences provideInstance(final Provider<Context> provider) {
        return proxyProvideSharedPreferences(provider.get());
    }
    
    public static SharedPreferences proxyProvideSharedPreferences(final Context context) {
        final SharedPreferences provideSharedPreferences = SystemUIGoogleModule.provideSharedPreferences(context);
        Preconditions.checkNotNull(provideSharedPreferences, "Cannot return null from a non-@Nullable @Provides method");
        return provideSharedPreferences;
    }
    
    @Override
    public SharedPreferences get() {
        return provideInstance(this.contextProvider);
    }
}
