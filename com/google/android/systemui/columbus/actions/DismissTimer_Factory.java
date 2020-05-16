// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.ColumbusContentObserver;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DismissTimer_Factory implements Factory<DismissTimer>
{
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    
    public DismissTimer_Factory(final Provider<Context> contextProvider, final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider) {
        this.contextProvider = contextProvider;
        this.settingsObserverFactoryProvider = settingsObserverFactoryProvider;
    }
    
    public static DismissTimer_Factory create(final Provider<Context> provider, final Provider<ColumbusContentObserver.Factory> provider2) {
        return new DismissTimer_Factory(provider, provider2);
    }
    
    public static DismissTimer provideInstance(final Provider<Context> provider, final Provider<ColumbusContentObserver.Factory> provider2) {
        return new DismissTimer(provider.get(), provider2.get());
    }
    
    @Override
    public DismissTimer get() {
        return provideInstance(this.contextProvider, this.settingsObserverFactoryProvider);
    }
}
