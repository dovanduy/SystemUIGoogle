// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.Optional;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UnpinNotifications_Factory implements Factory<UnpinNotifications>
{
    private final Provider<Context> contextProvider;
    private final Provider<Optional<HeadsUpManager>> headsUpManagerOptionalProvider;
    
    public UnpinNotifications_Factory(final Provider<Context> contextProvider, final Provider<Optional<HeadsUpManager>> headsUpManagerOptionalProvider) {
        this.contextProvider = contextProvider;
        this.headsUpManagerOptionalProvider = headsUpManagerOptionalProvider;
    }
    
    public static UnpinNotifications_Factory create(final Provider<Context> provider, final Provider<Optional<HeadsUpManager>> provider2) {
        return new UnpinNotifications_Factory(provider, provider2);
    }
    
    public static UnpinNotifications provideInstance(final Provider<Context> provider, final Provider<Optional<HeadsUpManager>> provider2) {
        return new UnpinNotifications(provider.get(), provider2.get());
    }
    
    @Override
    public UnpinNotifications get() {
        return provideInstance(this.contextProvider, this.headsUpManagerOptionalProvider);
    }
}
