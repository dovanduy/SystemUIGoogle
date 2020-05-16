// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import com.google.android.systemui.columbus.actions.UserSelectedAction;
import com.google.android.systemui.columbus.actions.UnpinNotifications;
import com.google.android.systemui.columbus.actions.SetupWizardAction;
import javax.inject.Provider;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideColumbusActionsFactory implements Factory<List<Action>>
{
    private final Provider<List<Action>> fullscreenActionsProvider;
    private final Provider<SetupWizardAction> setupWizardActionProvider;
    private final Provider<UnpinNotifications> unpinNotificationsProvider;
    private final Provider<UserSelectedAction> userSelectedActionProvider;
    
    public ColumbusModule_ProvideColumbusActionsFactory(final Provider<List<Action>> fullscreenActionsProvider, final Provider<UnpinNotifications> unpinNotificationsProvider, final Provider<SetupWizardAction> setupWizardActionProvider, final Provider<UserSelectedAction> userSelectedActionProvider) {
        this.fullscreenActionsProvider = fullscreenActionsProvider;
        this.unpinNotificationsProvider = unpinNotificationsProvider;
        this.setupWizardActionProvider = setupWizardActionProvider;
        this.userSelectedActionProvider = userSelectedActionProvider;
    }
    
    public static ColumbusModule_ProvideColumbusActionsFactory create(final Provider<List<Action>> provider, final Provider<UnpinNotifications> provider2, final Provider<SetupWizardAction> provider3, final Provider<UserSelectedAction> provider4) {
        return new ColumbusModule_ProvideColumbusActionsFactory(provider, provider2, provider3, provider4);
    }
    
    public static List<Action> provideInstance(final Provider<List<Action>> provider, final Provider<UnpinNotifications> provider2, final Provider<SetupWizardAction> provider3, final Provider<UserSelectedAction> provider4) {
        return proxyProvideColumbusActions(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    public static List<Action> proxyProvideColumbusActions(final List<Action> list, final UnpinNotifications unpinNotifications, final SetupWizardAction setupWizardAction, final UserSelectedAction userSelectedAction) {
        final List<Action> provideColumbusActions = ColumbusModule.provideColumbusActions(list, unpinNotifications, setupWizardAction, userSelectedAction);
        Preconditions.checkNotNull(provideColumbusActions, "Cannot return null from a non-@Nullable @Provides method");
        return provideColumbusActions;
    }
    
    @Override
    public List<Action> get() {
        return provideInstance(this.fullscreenActionsProvider, this.unpinNotificationsProvider, this.setupWizardActionProvider, this.userSelectedActionProvider);
    }
}
