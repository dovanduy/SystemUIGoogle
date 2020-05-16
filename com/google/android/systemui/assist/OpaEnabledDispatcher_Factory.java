// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.StatusBar;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class OpaEnabledDispatcher_Factory implements Factory<OpaEnabledDispatcher>
{
    private final Provider<StatusBar> statusBarLazyProvider;
    
    public OpaEnabledDispatcher_Factory(final Provider<StatusBar> statusBarLazyProvider) {
        this.statusBarLazyProvider = statusBarLazyProvider;
    }
    
    public static OpaEnabledDispatcher_Factory create(final Provider<StatusBar> provider) {
        return new OpaEnabledDispatcher_Factory(provider);
    }
    
    public static OpaEnabledDispatcher provideInstance(final Provider<StatusBar> provider) {
        return new OpaEnabledDispatcher(DoubleCheck.lazy(provider));
    }
    
    @Override
    public OpaEnabledDispatcher get() {
        return provideInstance(this.statusBarLazyProvider);
    }
}
