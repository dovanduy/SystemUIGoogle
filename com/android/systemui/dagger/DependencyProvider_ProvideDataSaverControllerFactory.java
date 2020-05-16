// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.policy.NetworkController;
import javax.inject.Provider;
import com.android.systemui.statusbar.policy.DataSaverController;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideDataSaverControllerFactory implements Factory<DataSaverController>
{
    private final DependencyProvider module;
    private final Provider<NetworkController> networkControllerProvider;
    
    public DependencyProvider_ProvideDataSaverControllerFactory(final DependencyProvider module, final Provider<NetworkController> networkControllerProvider) {
        this.module = module;
        this.networkControllerProvider = networkControllerProvider;
    }
    
    public static DependencyProvider_ProvideDataSaverControllerFactory create(final DependencyProvider dependencyProvider, final Provider<NetworkController> provider) {
        return new DependencyProvider_ProvideDataSaverControllerFactory(dependencyProvider, provider);
    }
    
    public static DataSaverController provideInstance(final DependencyProvider dependencyProvider, final Provider<NetworkController> provider) {
        return proxyProvideDataSaverController(dependencyProvider, provider.get());
    }
    
    public static DataSaverController proxyProvideDataSaverController(final DependencyProvider dependencyProvider, final NetworkController networkController) {
        final DataSaverController provideDataSaverController = dependencyProvider.provideDataSaverController(networkController);
        Preconditions.checkNotNull(provideDataSaverController, "Cannot return null from a non-@Nullable @Provides method");
        return provideDataSaverController;
    }
    
    @Override
    public DataSaverController get() {
        return provideInstance(this.module, this.networkControllerProvider);
    }
}
