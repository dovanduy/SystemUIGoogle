// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import com.google.android.systemui.reversecharging.ReverseWirelessCharger;
import java.util.Optional;
import dagger.internal.Factory;

public final class SystemUIGoogleModule_ProvideReverseWirelessChargerFactory implements Factory<Optional<ReverseWirelessCharger>>
{
    private final Provider<Context> contextProvider;
    
    public SystemUIGoogleModule_ProvideReverseWirelessChargerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemUIGoogleModule_ProvideReverseWirelessChargerFactory create(final Provider<Context> provider) {
        return new SystemUIGoogleModule_ProvideReverseWirelessChargerFactory(provider);
    }
    
    public static Optional<ReverseWirelessCharger> provideInstance(final Provider<Context> provider) {
        return proxyProvideReverseWirelessCharger(provider.get());
    }
    
    public static Optional<ReverseWirelessCharger> proxyProvideReverseWirelessCharger(final Context context) {
        final Optional<ReverseWirelessCharger> provideReverseWirelessCharger = SystemUIGoogleModule.provideReverseWirelessCharger(context);
        Preconditions.checkNotNull(provideReverseWirelessCharger, "Cannot return null from a non-@Nullable @Provides method");
        return provideReverseWirelessCharger;
    }
    
    @Override
    public Optional<ReverseWirelessCharger> get() {
        return provideInstance(this.contextProvider);
    }
}
