// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import com.android.systemui.statusbar.NavigationBarController;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideNavigationBarControllerFactory implements Factory<NavigationBarController>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideNavigationBarControllerFactory(final DependencyProvider module, final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static DependencyProvider_ProvideNavigationBarControllerFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider, final Provider<Handler> provider2, final Provider<CommandQueue> provider3) {
        return new DependencyProvider_ProvideNavigationBarControllerFactory(dependencyProvider, provider, provider2, provider3);
    }
    
    public static NavigationBarController provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider, final Provider<Handler> provider2, final Provider<CommandQueue> provider3) {
        return proxyProvideNavigationBarController(dependencyProvider, provider.get(), provider2.get(), provider3.get());
    }
    
    public static NavigationBarController proxyProvideNavigationBarController(final DependencyProvider dependencyProvider, final Context context, final Handler handler, final CommandQueue commandQueue) {
        final NavigationBarController provideNavigationBarController = dependencyProvider.provideNavigationBarController(context, handler, commandQueue);
        Preconditions.checkNotNull(provideNavigationBarController, "Cannot return null from a non-@Nullable @Provides method");
        return provideNavigationBarController;
    }
    
    @Override
    public NavigationBarController get() {
        return provideInstance(this.module, this.contextProvider, this.mainHandlerProvider, this.commandQueueProvider);
    }
}
