// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import dagger.internal.DoubleCheck;
import com.google.android.systemui.columbus.actions.Action;
import java.util.Set;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SetupWizard_Factory implements Factory<SetupWizard>
{
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> provisionedControllerProvider;
    private final Provider<Set<Action>> setupWizardExceptionsProvider;
    
    public SetupWizard_Factory(final Provider<Context> contextProvider, final Provider<Set<Action>> setupWizardExceptionsProvider, final Provider<DeviceProvisionedController> provisionedControllerProvider) {
        this.contextProvider = contextProvider;
        this.setupWizardExceptionsProvider = setupWizardExceptionsProvider;
        this.provisionedControllerProvider = provisionedControllerProvider;
    }
    
    public static SetupWizard_Factory create(final Provider<Context> provider, final Provider<Set<Action>> provider2, final Provider<DeviceProvisionedController> provider3) {
        return new SetupWizard_Factory(provider, provider2, provider3);
    }
    
    public static SetupWizard provideInstance(final Provider<Context> provider, final Provider<Set<Action>> provider2, final Provider<DeviceProvisionedController> provider3) {
        return new SetupWizard(provider.get(), provider2.get(), DoubleCheck.lazy(provider3));
    }
    
    @Override
    public SetupWizard get() {
        return provideInstance(this.contextProvider, this.setupWizardExceptionsProvider, this.provisionedControllerProvider);
    }
}
