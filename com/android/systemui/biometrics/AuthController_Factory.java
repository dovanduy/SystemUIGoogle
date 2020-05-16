// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AuthController_Factory implements Factory<AuthController>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    
    public AuthController_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static AuthController_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new AuthController_Factory(provider, provider2);
    }
    
    public static AuthController provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2) {
        return new AuthController(provider.get(), provider2.get());
    }
    
    @Override
    public AuthController get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider);
    }
}
