// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import com.android.systemui.statusbar.phone.StatusBar;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SetupWizardAction_Builder_Factory implements Factory<SetupWizardAction.Builder>
{
    private final Provider<Context> contextProvider;
    private final Provider<StatusBar> statusBarProvider;
    
    public SetupWizardAction_Builder_Factory(final Provider<Context> contextProvider, final Provider<StatusBar> statusBarProvider) {
        this.contextProvider = contextProvider;
        this.statusBarProvider = statusBarProvider;
    }
    
    public static SetupWizardAction_Builder_Factory create(final Provider<Context> provider, final Provider<StatusBar> provider2) {
        return new SetupWizardAction_Builder_Factory(provider, provider2);
    }
    
    public static SetupWizardAction.Builder provideInstance(final Provider<Context> provider, final Provider<StatusBar> provider2) {
        return new SetupWizardAction.Builder(provider.get(), provider2.get());
    }
    
    @Override
    public SetupWizardAction.Builder get() {
        return provideInstance(this.contextProvider, this.statusBarProvider);
    }
}
