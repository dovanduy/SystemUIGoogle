// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.WindowManager;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import android.content.res.Resources;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class StatusBarWindowController_Factory implements Factory<StatusBarWindowController>
{
    private final Provider<Context> contextProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<SuperStatusBarViewFactory> superStatusBarViewFactoryProvider;
    private final Provider<WindowManager> windowManagerProvider;
    
    public StatusBarWindowController_Factory(final Provider<Context> contextProvider, final Provider<WindowManager> windowManagerProvider, final Provider<SuperStatusBarViewFactory> superStatusBarViewFactoryProvider, final Provider<Resources> resourcesProvider) {
        this.contextProvider = contextProvider;
        this.windowManagerProvider = windowManagerProvider;
        this.superStatusBarViewFactoryProvider = superStatusBarViewFactoryProvider;
        this.resourcesProvider = resourcesProvider;
    }
    
    public static StatusBarWindowController_Factory create(final Provider<Context> provider, final Provider<WindowManager> provider2, final Provider<SuperStatusBarViewFactory> provider3, final Provider<Resources> provider4) {
        return new StatusBarWindowController_Factory(provider, provider2, provider3, provider4);
    }
    
    public static StatusBarWindowController provideInstance(final Provider<Context> provider, final Provider<WindowManager> provider2, final Provider<SuperStatusBarViewFactory> provider3, final Provider<Resources> provider4) {
        return new StatusBarWindowController(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public StatusBarWindowController get() {
        return provideInstance(this.contextProvider, this.windowManagerProvider, this.superStatusBarViewFactoryProvider, this.resourcesProvider);
    }
}
