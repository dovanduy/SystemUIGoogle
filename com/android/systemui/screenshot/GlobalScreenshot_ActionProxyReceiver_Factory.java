// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GlobalScreenshot_ActionProxyReceiver_Factory implements Factory<GlobalScreenshot.ActionProxyReceiver>
{
    private final Provider<Optional<Lazy<StatusBar>>> statusBarLazyProvider;
    
    public GlobalScreenshot_ActionProxyReceiver_Factory(final Provider<Optional<Lazy<StatusBar>>> statusBarLazyProvider) {
        this.statusBarLazyProvider = statusBarLazyProvider;
    }
    
    public static GlobalScreenshot_ActionProxyReceiver_Factory create(final Provider<Optional<Lazy<StatusBar>>> provider) {
        return new GlobalScreenshot_ActionProxyReceiver_Factory(provider);
    }
    
    public static GlobalScreenshot.ActionProxyReceiver provideInstance(final Provider<Optional<Lazy<StatusBar>>> provider) {
        return new GlobalScreenshot.ActionProxyReceiver(provider.get());
    }
    
    @Override
    public GlobalScreenshot.ActionProxyReceiver get() {
        return provideInstance(this.statusBarLazyProvider);
    }
}
