// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.internal.statusbar.IStatusBarService;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class RemoteInputUriController_Factory implements Factory<RemoteInputUriController>
{
    private final Provider<IStatusBarService> statusBarServiceProvider;
    
    public RemoteInputUriController_Factory(final Provider<IStatusBarService> statusBarServiceProvider) {
        this.statusBarServiceProvider = statusBarServiceProvider;
    }
    
    public static RemoteInputUriController_Factory create(final Provider<IStatusBarService> provider) {
        return new RemoteInputUriController_Factory(provider);
    }
    
    public static RemoteInputUriController provideInstance(final Provider<IStatusBarService> provider) {
        return new RemoteInputUriController(provider.get());
    }
    
    @Override
    public RemoteInputUriController get() {
        return provideInstance(this.statusBarServiceProvider);
    }
}
