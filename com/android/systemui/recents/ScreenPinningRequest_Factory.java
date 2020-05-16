// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ScreenPinningRequest_Factory implements Factory<ScreenPinningRequest>
{
    private final Provider<Context> contextProvider;
    private final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider;
    
    public ScreenPinningRequest_Factory(final Provider<Context> contextProvider, final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider) {
        this.contextProvider = contextProvider;
        this.statusBarOptionalLazyProvider = statusBarOptionalLazyProvider;
    }
    
    public static ScreenPinningRequest_Factory create(final Provider<Context> provider, final Provider<Optional<Lazy<StatusBar>>> provider2) {
        return new ScreenPinningRequest_Factory(provider, provider2);
    }
    
    public static ScreenPinningRequest provideInstance(final Provider<Context> provider, final Provider<Optional<Lazy<StatusBar>>> provider2) {
        return new ScreenPinningRequest(provider.get(), provider2.get());
    }
    
    @Override
    public ScreenPinningRequest get() {
        return provideInstance(this.contextProvider, this.statusBarOptionalLazyProvider);
    }
}
