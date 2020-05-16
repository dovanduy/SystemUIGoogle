// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.android.systemui.statusbar.phone.StatusBar;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SetupWizardAction_Factory implements Factory<SetupWizardAction>
{
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardDeferredSetup> keyguardDeferredSetupGateProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<SettingsAction> settingsActionProvider;
    private final Provider<StatusBar> statusBarProvider;
    private final Provider<UserSelectedAction> userSelectedActionProvider;
    
    public SetupWizardAction_Factory(final Provider<Context> contextProvider, final Provider<SettingsAction> settingsActionProvider, final Provider<UserSelectedAction> userSelectedActionProvider, final Provider<KeyguardDeferredSetup> keyguardDeferredSetupGateProvider, final Provider<StatusBar> statusBarProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider) {
        this.contextProvider = contextProvider;
        this.settingsActionProvider = settingsActionProvider;
        this.userSelectedActionProvider = userSelectedActionProvider;
        this.keyguardDeferredSetupGateProvider = keyguardDeferredSetupGateProvider;
        this.statusBarProvider = statusBarProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
    }
    
    public static SetupWizardAction_Factory create(final Provider<Context> provider, final Provider<SettingsAction> provider2, final Provider<UserSelectedAction> provider3, final Provider<KeyguardDeferredSetup> provider4, final Provider<StatusBar> provider5, final Provider<KeyguardUpdateMonitor> provider6) {
        return new SetupWizardAction_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static SetupWizardAction provideInstance(final Provider<Context> provider, final Provider<SettingsAction> provider2, final Provider<UserSelectedAction> provider3, final Provider<KeyguardDeferredSetup> provider4, final Provider<StatusBar> provider5, final Provider<KeyguardUpdateMonitor> provider6) {
        return new SetupWizardAction(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public SetupWizardAction get() {
        return provideInstance(this.contextProvider, this.settingsActionProvider, this.userSelectedActionProvider, this.keyguardDeferredSetupGateProvider, this.statusBarProvider, this.keyguardUpdateMonitorProvider);
    }
}
