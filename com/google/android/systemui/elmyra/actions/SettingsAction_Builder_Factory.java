// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import com.android.systemui.statusbar.phone.StatusBar;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SettingsAction_Builder_Factory implements Factory<SettingsAction.Builder>
{
    private final Provider<Context> contextProvider;
    private final Provider<StatusBar> statusBarProvider;
    
    public SettingsAction_Builder_Factory(final Provider<Context> contextProvider, final Provider<StatusBar> statusBarProvider) {
        this.contextProvider = contextProvider;
        this.statusBarProvider = statusBarProvider;
    }
    
    public static SettingsAction_Builder_Factory create(final Provider<Context> provider, final Provider<StatusBar> provider2) {
        return new SettingsAction_Builder_Factory(provider, provider2);
    }
    
    public static SettingsAction.Builder provideInstance(final Provider<Context> provider, final Provider<StatusBar> provider2) {
        return new SettingsAction.Builder(provider.get(), provider2.get());
    }
    
    @Override
    public SettingsAction.Builder get() {
        return provideInstance(this.contextProvider, this.statusBarProvider);
    }
}
