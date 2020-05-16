// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.os.Handler;
import android.view.IWindowManager;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.phone.AutoHideController;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideAutoHideControllerFactory implements Factory<AutoHideController>
{
    private final Provider<Context> contextProvider;
    private final Provider<IWindowManager> iWindowManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideAutoHideControllerFactory(final DependencyProvider module, final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider, final Provider<IWindowManager> iWindowManagerProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.iWindowManagerProvider = iWindowManagerProvider;
    }
    
    public static DependencyProvider_ProvideAutoHideControllerFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider, final Provider<Handler> provider2, final Provider<IWindowManager> provider3) {
        return new DependencyProvider_ProvideAutoHideControllerFactory(dependencyProvider, provider, provider2, provider3);
    }
    
    public static AutoHideController provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider, final Provider<Handler> provider2, final Provider<IWindowManager> provider3) {
        return proxyProvideAutoHideController(dependencyProvider, provider.get(), provider2.get(), provider3.get());
    }
    
    public static AutoHideController proxyProvideAutoHideController(final DependencyProvider dependencyProvider, final Context context, final Handler handler, final IWindowManager windowManager) {
        final AutoHideController provideAutoHideController = dependencyProvider.provideAutoHideController(context, handler, windowManager);
        Preconditions.checkNotNull(provideAutoHideController, "Cannot return null from a non-@Nullable @Provides method");
        return provideAutoHideController;
    }
    
    @Override
    public AutoHideController get() {
        return provideInstance(this.module, this.contextProvider, this.mainHandlerProvider, this.iWindowManagerProvider);
    }
}
