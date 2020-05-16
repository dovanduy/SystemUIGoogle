// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui;

import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.ServiceConfigurationGoogle;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GoogleServices_Factory implements Factory<GoogleServices>
{
    private final Provider<Context> contextProvider;
    private final Provider<ServiceConfigurationGoogle> serviceConfigurationGoogleProvider;
    private final Provider<StatusBar> statusBarProvider;
    
    public GoogleServices_Factory(final Provider<Context> contextProvider, final Provider<ServiceConfigurationGoogle> serviceConfigurationGoogleProvider, final Provider<StatusBar> statusBarProvider) {
        this.contextProvider = contextProvider;
        this.serviceConfigurationGoogleProvider = serviceConfigurationGoogleProvider;
        this.statusBarProvider = statusBarProvider;
    }
    
    public static GoogleServices_Factory create(final Provider<Context> provider, final Provider<ServiceConfigurationGoogle> provider2, final Provider<StatusBar> provider3) {
        return new GoogleServices_Factory(provider, provider2, provider3);
    }
    
    public static GoogleServices provideInstance(final Provider<Context> provider, final Provider<ServiceConfigurationGoogle> provider2, final Provider<StatusBar> provider3) {
        return new GoogleServices(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public GoogleServices get() {
        return provideInstance(this.contextProvider, this.serviceConfigurationGoogleProvider, this.statusBarProvider);
    }
}
