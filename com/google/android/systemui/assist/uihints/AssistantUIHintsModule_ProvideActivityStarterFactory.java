// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.StatusBar;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistantUIHintsModule_ProvideActivityStarterFactory implements Factory<NgaMessageHandler.StartActivityInfoListener>
{
    private final Provider<StatusBar> statusBarLazyProvider;
    
    public AssistantUIHintsModule_ProvideActivityStarterFactory(final Provider<StatusBar> statusBarLazyProvider) {
        this.statusBarLazyProvider = statusBarLazyProvider;
    }
    
    public static AssistantUIHintsModule_ProvideActivityStarterFactory create(final Provider<StatusBar> provider) {
        return new AssistantUIHintsModule_ProvideActivityStarterFactory(provider);
    }
    
    public static NgaMessageHandler.StartActivityInfoListener provideInstance(final Provider<StatusBar> provider) {
        return proxyProvideActivityStarter(DoubleCheck.lazy(provider));
    }
    
    public static NgaMessageHandler.StartActivityInfoListener proxyProvideActivityStarter(final Lazy<StatusBar> lazy) {
        final NgaMessageHandler.StartActivityInfoListener provideActivityStarter = AssistantUIHintsModule.provideActivityStarter(lazy);
        Preconditions.checkNotNull(provideActivityStarter, "Cannot return null from a non-@Nullable @Provides method");
        return provideActivityStarter;
    }
    
    @Override
    public NgaMessageHandler.StartActivityInfoListener get() {
        return provideInstance(this.statusBarLazyProvider);
    }
}
