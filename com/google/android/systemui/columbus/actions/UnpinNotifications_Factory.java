// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.Optional;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UnpinNotifications_Factory implements Factory<UnpinNotifications>
{
    private final Provider<Context> contextProvider;
    private final Provider<Optional<HeadsUpManager>> headsUpManagerOptionalProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    
    public UnpinNotifications_Factory(final Provider<Optional<HeadsUpManager>> headsUpManagerOptionalProvider, final Provider<Context> contextProvider, final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider) {
        this.headsUpManagerOptionalProvider = headsUpManagerOptionalProvider;
        this.contextProvider = contextProvider;
        this.settingsObserverFactoryProvider = settingsObserverFactoryProvider;
    }
    
    public static UnpinNotifications_Factory create(final Provider<Optional<HeadsUpManager>> provider, final Provider<Context> provider2, final Provider<ColumbusContentObserver.Factory> provider3) {
        return new UnpinNotifications_Factory(provider, provider2, provider3);
    }
    
    public static UnpinNotifications provideInstance(final Provider<Optional<HeadsUpManager>> provider, final Provider<Context> provider2, final Provider<ColumbusContentObserver.Factory> provider3) {
        return new UnpinNotifications(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public UnpinNotifications get() {
        return provideInstance(this.headsUpManagerOptionalProvider, this.contextProvider, this.settingsObserverFactoryProvider);
    }
}
