// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.android.systemui.statusbar.phone.StatusBar;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SettingsAction_Factory implements Factory<SettingsAction>
{
    private final Provider<Context> contextProvider;
    private final Provider<StatusBar> statusBarProvider;
    private final Provider<UserSelectedAction> userSelectedActionProvider;
    
    public SettingsAction_Factory(final Provider<Context> contextProvider, final Provider<UserSelectedAction> userSelectedActionProvider, final Provider<StatusBar> statusBarProvider) {
        this.contextProvider = contextProvider;
        this.userSelectedActionProvider = userSelectedActionProvider;
        this.statusBarProvider = statusBarProvider;
    }
    
    public static SettingsAction_Factory create(final Provider<Context> provider, final Provider<UserSelectedAction> provider2, final Provider<StatusBar> provider3) {
        return new SettingsAction_Factory(provider, provider2, provider3);
    }
    
    public static SettingsAction provideInstance(final Provider<Context> provider, final Provider<UserSelectedAction> provider2, final Provider<StatusBar> provider3) {
        return new SettingsAction(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public SettingsAction get() {
        return provideInstance(this.contextProvider, this.userSelectedActionProvider, this.statusBarProvider);
    }
}
