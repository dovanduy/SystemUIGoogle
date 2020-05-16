// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import com.google.android.systemui.columbus.gates.WakeMode;
import com.google.android.systemui.columbus.gates.VrMode;
import com.google.android.systemui.columbus.gates.UsbState;
import com.google.android.systemui.columbus.gates.TelephonyActivity;
import com.google.android.systemui.columbus.gates.SystemKeyPress;
import com.google.android.systemui.columbus.gates.SetupWizard;
import com.google.android.systemui.columbus.gates.PowerSaveState;
import com.google.android.systemui.columbus.gates.NavigationBarVisibility;
import com.google.android.systemui.columbus.gates.KeyguardProximity;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup;
import com.google.android.systemui.columbus.gates.FlagEnabled;
import com.google.android.systemui.columbus.gates.ChargingState;
import com.google.android.systemui.columbus.gates.CameraVisibility;
import javax.inject.Provider;
import com.google.android.systemui.columbus.gates.Gate;
import java.util.Set;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideColumbusGatesFactory implements Factory<Set<Gate>>
{
    private final Provider<CameraVisibility> cameraVisibilityProvider;
    private final Provider<ChargingState> chargingStateProvider;
    private final Provider<FlagEnabled> flagEnabledProvider;
    private final Provider<KeyguardDeferredSetup> keyguardDeferredSetupProvider;
    private final Provider<KeyguardProximity> keyguardProximityProvider;
    private final Provider<NavigationBarVisibility> navigationBarVisibilityProvider;
    private final Provider<PowerSaveState> powerSaveStateProvider;
    private final Provider<SetupWizard> setupWizardProvider;
    private final Provider<SystemKeyPress> systemKeyPressProvider;
    private final Provider<TelephonyActivity> telephonyActivityProvider;
    private final Provider<UsbState> usbStateProvider;
    private final Provider<VrMode> vrModeProvider;
    private final Provider<WakeMode> wakeModeProvider;
    
    public ColumbusModule_ProvideColumbusGatesFactory(final Provider<FlagEnabled> flagEnabledProvider, final Provider<WakeMode> wakeModeProvider, final Provider<ChargingState> chargingStateProvider, final Provider<UsbState> usbStateProvider, final Provider<KeyguardProximity> keyguardProximityProvider, final Provider<SetupWizard> setupWizardProvider, final Provider<NavigationBarVisibility> navigationBarVisibilityProvider, final Provider<SystemKeyPress> systemKeyPressProvider, final Provider<TelephonyActivity> telephonyActivityProvider, final Provider<VrMode> vrModeProvider, final Provider<KeyguardDeferredSetup> keyguardDeferredSetupProvider, final Provider<CameraVisibility> cameraVisibilityProvider, final Provider<PowerSaveState> powerSaveStateProvider) {
        this.flagEnabledProvider = flagEnabledProvider;
        this.wakeModeProvider = wakeModeProvider;
        this.chargingStateProvider = chargingStateProvider;
        this.usbStateProvider = usbStateProvider;
        this.keyguardProximityProvider = keyguardProximityProvider;
        this.setupWizardProvider = setupWizardProvider;
        this.navigationBarVisibilityProvider = navigationBarVisibilityProvider;
        this.systemKeyPressProvider = systemKeyPressProvider;
        this.telephonyActivityProvider = telephonyActivityProvider;
        this.vrModeProvider = vrModeProvider;
        this.keyguardDeferredSetupProvider = keyguardDeferredSetupProvider;
        this.cameraVisibilityProvider = cameraVisibilityProvider;
        this.powerSaveStateProvider = powerSaveStateProvider;
    }
    
    public static ColumbusModule_ProvideColumbusGatesFactory create(final Provider<FlagEnabled> provider, final Provider<WakeMode> provider2, final Provider<ChargingState> provider3, final Provider<UsbState> provider4, final Provider<KeyguardProximity> provider5, final Provider<SetupWizard> provider6, final Provider<NavigationBarVisibility> provider7, final Provider<SystemKeyPress> provider8, final Provider<TelephonyActivity> provider9, final Provider<VrMode> provider10, final Provider<KeyguardDeferredSetup> provider11, final Provider<CameraVisibility> provider12, final Provider<PowerSaveState> provider13) {
        return new ColumbusModule_ProvideColumbusGatesFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13);
    }
    
    public static Set<Gate> provideInstance(final Provider<FlagEnabled> provider, final Provider<WakeMode> provider2, final Provider<ChargingState> provider3, final Provider<UsbState> provider4, final Provider<KeyguardProximity> provider5, final Provider<SetupWizard> provider6, final Provider<NavigationBarVisibility> provider7, final Provider<SystemKeyPress> provider8, final Provider<TelephonyActivity> provider9, final Provider<VrMode> provider10, final Provider<KeyguardDeferredSetup> provider11, final Provider<CameraVisibility> provider12, final Provider<PowerSaveState> provider13) {
        return proxyProvideColumbusGates(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get());
    }
    
    public static Set<Gate> proxyProvideColumbusGates(final FlagEnabled flagEnabled, final WakeMode wakeMode, final ChargingState chargingState, final UsbState usbState, final KeyguardProximity keyguardProximity, final SetupWizard setupWizard, final NavigationBarVisibility navigationBarVisibility, final SystemKeyPress systemKeyPress, final TelephonyActivity telephonyActivity, final VrMode vrMode, final KeyguardDeferredSetup keyguardDeferredSetup, final CameraVisibility cameraVisibility, final PowerSaveState powerSaveState) {
        final Set<Gate> provideColumbusGates = ColumbusModule.provideColumbusGates(flagEnabled, wakeMode, chargingState, usbState, keyguardProximity, setupWizard, navigationBarVisibility, systemKeyPress, telephonyActivity, vrMode, keyguardDeferredSetup, cameraVisibility, powerSaveState);
        Preconditions.checkNotNull(provideColumbusGates, "Cannot return null from a non-@Nullable @Provides method");
        return provideColumbusGates;
    }
    
    @Override
    public Set<Gate> get() {
        return provideInstance(this.flagEnabledProvider, this.wakeModeProvider, this.chargingStateProvider, this.usbStateProvider, this.keyguardProximityProvider, this.setupWizardProvider, this.navigationBarVisibilityProvider, this.systemKeyPressProvider, this.telephonyActivityProvider, this.vrModeProvider, this.keyguardDeferredSetupProvider, this.cameraVisibilityProvider, this.powerSaveStateProvider);
    }
}
