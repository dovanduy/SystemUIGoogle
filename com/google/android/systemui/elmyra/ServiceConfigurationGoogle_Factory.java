// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import com.google.android.systemui.elmyra.actions.UnpinNotifications;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons;
import com.google.android.systemui.elmyra.actions.SetupWizardAction;
import com.google.android.systemui.elmyra.actions.SettingsAction;
import com.google.android.systemui.elmyra.actions.LaunchOpa;
import android.content.Context;
import com.google.android.systemui.elmyra.actions.CameraAction;
import com.google.android.systemui.elmyra.feedback.AssistInvocationEffect;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ServiceConfigurationGoogle_Factory implements Factory<ServiceConfigurationGoogle>
{
    private final Provider<AssistInvocationEffect> assistInvocationEffectProvider;
    private final Provider<CameraAction.Builder> cameraActionBuilderProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LaunchOpa.Builder> launchOpaBuilderProvider;
    private final Provider<SettingsAction.Builder> settingsActionBuilderProvider;
    private final Provider<SetupWizardAction.Builder> setupWizardActionBuilderProvider;
    private final Provider<SquishyNavigationButtons> squishyNavigationButtonsProvider;
    private final Provider<UnpinNotifications> unpinNotificationsProvider;
    
    public ServiceConfigurationGoogle_Factory(final Provider<Context> contextProvider, final Provider<AssistInvocationEffect> assistInvocationEffectProvider, final Provider<LaunchOpa.Builder> launchOpaBuilderProvider, final Provider<SettingsAction.Builder> settingsActionBuilderProvider, final Provider<CameraAction.Builder> cameraActionBuilderProvider, final Provider<SetupWizardAction.Builder> setupWizardActionBuilderProvider, final Provider<SquishyNavigationButtons> squishyNavigationButtonsProvider, final Provider<UnpinNotifications> unpinNotificationsProvider) {
        this.contextProvider = contextProvider;
        this.assistInvocationEffectProvider = assistInvocationEffectProvider;
        this.launchOpaBuilderProvider = launchOpaBuilderProvider;
        this.settingsActionBuilderProvider = settingsActionBuilderProvider;
        this.cameraActionBuilderProvider = cameraActionBuilderProvider;
        this.setupWizardActionBuilderProvider = setupWizardActionBuilderProvider;
        this.squishyNavigationButtonsProvider = squishyNavigationButtonsProvider;
        this.unpinNotificationsProvider = unpinNotificationsProvider;
    }
    
    public static ServiceConfigurationGoogle_Factory create(final Provider<Context> provider, final Provider<AssistInvocationEffect> provider2, final Provider<LaunchOpa.Builder> provider3, final Provider<SettingsAction.Builder> provider4, final Provider<CameraAction.Builder> provider5, final Provider<SetupWizardAction.Builder> provider6, final Provider<SquishyNavigationButtons> provider7, final Provider<UnpinNotifications> provider8) {
        return new ServiceConfigurationGoogle_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static ServiceConfigurationGoogle provideInstance(final Provider<Context> provider, final Provider<AssistInvocationEffect> provider2, final Provider<LaunchOpa.Builder> provider3, final Provider<SettingsAction.Builder> provider4, final Provider<CameraAction.Builder> provider5, final Provider<SetupWizardAction.Builder> provider6, final Provider<SquishyNavigationButtons> provider7, final Provider<UnpinNotifications> provider8) {
        return new ServiceConfigurationGoogle(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    @Override
    public ServiceConfigurationGoogle get() {
        return provideInstance(this.contextProvider, this.assistInvocationEffectProvider, this.launchOpaBuilderProvider, this.settingsActionBuilderProvider, this.cameraActionBuilderProvider, this.setupWizardActionBuilderProvider, this.squishyNavigationButtonsProvider, this.unpinNotificationsProvider);
    }
}
