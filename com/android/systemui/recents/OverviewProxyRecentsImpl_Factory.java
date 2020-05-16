// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import com.android.systemui.stackdivider.Divider;
import java.util.Optional;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class OverviewProxyRecentsImpl_Factory implements Factory<OverviewProxyRecentsImpl>
{
    private final Provider<Optional<Divider>> dividerOptionalProvider;
    private final Provider<Optional<Lazy<StatusBar>>> statusBarLazyProvider;
    
    public OverviewProxyRecentsImpl_Factory(final Provider<Optional<Lazy<StatusBar>>> statusBarLazyProvider, final Provider<Optional<Divider>> dividerOptionalProvider) {
        this.statusBarLazyProvider = statusBarLazyProvider;
        this.dividerOptionalProvider = dividerOptionalProvider;
    }
    
    public static OverviewProxyRecentsImpl_Factory create(final Provider<Optional<Lazy<StatusBar>>> provider, final Provider<Optional<Divider>> provider2) {
        return new OverviewProxyRecentsImpl_Factory(provider, provider2);
    }
    
    public static OverviewProxyRecentsImpl provideInstance(final Provider<Optional<Lazy<StatusBar>>> provider, final Provider<Optional<Divider>> provider2) {
        return new OverviewProxyRecentsImpl(provider.get(), provider2.get());
    }
    
    @Override
    public OverviewProxyRecentsImpl get() {
        return provideInstance(this.statusBarLazyProvider, this.dividerOptionalProvider);
    }
}
