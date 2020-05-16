// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import com.android.systemui.statusbar.NavigationBarController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistModule_ProvideAssistHandleViewControllerFactory implements Factory<AssistHandleViewController>
{
    private final Provider<NavigationBarController> navigationBarControllerProvider;
    
    public AssistModule_ProvideAssistHandleViewControllerFactory(final Provider<NavigationBarController> navigationBarControllerProvider) {
        this.navigationBarControllerProvider = navigationBarControllerProvider;
    }
    
    public static AssistModule_ProvideAssistHandleViewControllerFactory create(final Provider<NavigationBarController> provider) {
        return new AssistModule_ProvideAssistHandleViewControllerFactory(provider);
    }
    
    public static AssistHandleViewController provideInstance(final Provider<NavigationBarController> provider) {
        return proxyProvideAssistHandleViewController(provider.get());
    }
    
    public static AssistHandleViewController proxyProvideAssistHandleViewController(final NavigationBarController navigationBarController) {
        return AssistModule.provideAssistHandleViewController(navigationBarController);
    }
    
    @Override
    public AssistHandleViewController get() {
        return provideInstance(this.navigationBarControllerProvider);
    }
}
