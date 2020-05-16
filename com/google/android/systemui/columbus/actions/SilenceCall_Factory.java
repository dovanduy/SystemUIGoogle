// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.ColumbusContentObserver;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SilenceCall_Factory implements Factory<SilenceCall>
{
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    
    public SilenceCall_Factory(final Provider<Context> contextProvider, final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider) {
        this.contextProvider = contextProvider;
        this.settingsObserverFactoryProvider = settingsObserverFactoryProvider;
    }
    
    public static SilenceCall_Factory create(final Provider<Context> provider, final Provider<ColumbusContentObserver.Factory> provider2) {
        return new SilenceCall_Factory(provider, provider2);
    }
    
    public static SilenceCall provideInstance(final Provider<Context> provider, final Provider<ColumbusContentObserver.Factory> provider2) {
        return new SilenceCall(provider.get(), provider2.get());
    }
    
    @Override
    public SilenceCall get() {
        return provideInstance(this.contextProvider, this.settingsObserverFactoryProvider);
    }
}
