// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import com.google.android.systemui.columbus.actions.SnoozeAlarm;
import com.google.android.systemui.columbus.actions.SilenceCall;
import com.google.android.systemui.columbus.actions.SettingsAction;
import com.google.android.systemui.columbus.actions.DismissTimer;
import javax.inject.Provider;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideFullscreenActionsFactory implements Factory<List<Action>>
{
    private final Provider<DismissTimer> dismissTimerProvider;
    private final Provider<SettingsAction> settingsActionProvider;
    private final Provider<SilenceCall> silenceCallProvider;
    private final Provider<SnoozeAlarm> snoozeAlarmProvider;
    
    public ColumbusModule_ProvideFullscreenActionsFactory(final Provider<DismissTimer> dismissTimerProvider, final Provider<SnoozeAlarm> snoozeAlarmProvider, final Provider<SilenceCall> silenceCallProvider, final Provider<SettingsAction> settingsActionProvider) {
        this.dismissTimerProvider = dismissTimerProvider;
        this.snoozeAlarmProvider = snoozeAlarmProvider;
        this.silenceCallProvider = silenceCallProvider;
        this.settingsActionProvider = settingsActionProvider;
    }
    
    public static ColumbusModule_ProvideFullscreenActionsFactory create(final Provider<DismissTimer> provider, final Provider<SnoozeAlarm> provider2, final Provider<SilenceCall> provider3, final Provider<SettingsAction> provider4) {
        return new ColumbusModule_ProvideFullscreenActionsFactory(provider, provider2, provider3, provider4);
    }
    
    public static List<Action> provideInstance(final Provider<DismissTimer> provider, final Provider<SnoozeAlarm> provider2, final Provider<SilenceCall> provider3, final Provider<SettingsAction> provider4) {
        return proxyProvideFullscreenActions(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    public static List<Action> proxyProvideFullscreenActions(final DismissTimer dismissTimer, final SnoozeAlarm snoozeAlarm, final SilenceCall silenceCall, final SettingsAction settingsAction) {
        final List<Action> provideFullscreenActions = ColumbusModule.provideFullscreenActions(dismissTimer, snoozeAlarm, silenceCall, settingsAction);
        Preconditions.checkNotNull(provideFullscreenActions, "Cannot return null from a non-@Nullable @Provides method");
        return provideFullscreenActions;
    }
    
    @Override
    public List<Action> get() {
        return provideInstance(this.dismissTimerProvider, this.snoozeAlarmProvider, this.silenceCallProvider, this.settingsActionProvider);
    }
}
