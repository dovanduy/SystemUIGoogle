// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.android.systemui.recents.Recents;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LaunchOverview_Factory implements Factory<LaunchOverview>
{
    private final Provider<Context> contextProvider;
    private final Provider<Recents> recentsProvider;
    
    public LaunchOverview_Factory(final Provider<Context> contextProvider, final Provider<Recents> recentsProvider) {
        this.contextProvider = contextProvider;
        this.recentsProvider = recentsProvider;
    }
    
    public static LaunchOverview_Factory create(final Provider<Context> provider, final Provider<Recents> provider2) {
        return new LaunchOverview_Factory(provider, provider2);
    }
    
    public static LaunchOverview provideInstance(final Provider<Context> provider, final Provider<Recents> provider2) {
        return new LaunchOverview(provider.get(), provider2.get());
    }
    
    @Override
    public LaunchOverview get() {
        return provideInstance(this.contextProvider, this.recentsProvider);
    }
}
