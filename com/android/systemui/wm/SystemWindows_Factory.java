// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wm;

import android.view.IWindowManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SystemWindows_Factory implements Factory<SystemWindows>
{
    private final Provider<Context> contextProvider;
    private final Provider<DisplayController> displayControllerProvider;
    private final Provider<IWindowManager> wmServiceProvider;
    
    public SystemWindows_Factory(final Provider<Context> contextProvider, final Provider<DisplayController> displayControllerProvider, final Provider<IWindowManager> wmServiceProvider) {
        this.contextProvider = contextProvider;
        this.displayControllerProvider = displayControllerProvider;
        this.wmServiceProvider = wmServiceProvider;
    }
    
    public static SystemWindows_Factory create(final Provider<Context> provider, final Provider<DisplayController> provider2, final Provider<IWindowManager> provider3) {
        return new SystemWindows_Factory(provider, provider2, provider3);
    }
    
    public static SystemWindows provideInstance(final Provider<Context> provider, final Provider<DisplayController> provider2, final Provider<IWindowManager> provider3) {
        return new SystemWindows(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public SystemWindows get() {
        return provideInstance(this.contextProvider, this.displayControllerProvider, this.wmServiceProvider);
    }
}
