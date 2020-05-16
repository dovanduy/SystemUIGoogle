// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.ColumbusContentObserver;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SnoozeAlarm_Factory implements Factory<SnoozeAlarm>
{
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    
    public SnoozeAlarm_Factory(final Provider<Context> contextProvider, final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider) {
        this.contextProvider = contextProvider;
        this.settingsObserverFactoryProvider = settingsObserverFactoryProvider;
    }
    
    public static SnoozeAlarm_Factory create(final Provider<Context> provider, final Provider<ColumbusContentObserver.Factory> provider2) {
        return new SnoozeAlarm_Factory(provider, provider2);
    }
    
    public static SnoozeAlarm provideInstance(final Provider<Context> provider, final Provider<ColumbusContentObserver.Factory> provider2) {
        return new SnoozeAlarm(provider.get(), provider2.get());
    }
    
    @Override
    public SnoozeAlarm get() {
        return provideInstance(this.contextProvider, this.settingsObserverFactoryProvider);
    }
}
