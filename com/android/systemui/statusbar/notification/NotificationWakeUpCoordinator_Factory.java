// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationWakeUpCoordinator_Factory implements Factory<NotificationWakeUpCoordinator>
{
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<HeadsUpManager> mHeadsUpManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public NotificationWakeUpCoordinator_Factory(final Provider<HeadsUpManager> mHeadsUpManagerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<KeyguardBypassController> bypassControllerProvider, final Provider<DozeParameters> dozeParametersProvider) {
        this.mHeadsUpManagerProvider = mHeadsUpManagerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.dozeParametersProvider = dozeParametersProvider;
    }
    
    public static NotificationWakeUpCoordinator_Factory create(final Provider<HeadsUpManager> provider, final Provider<StatusBarStateController> provider2, final Provider<KeyguardBypassController> provider3, final Provider<DozeParameters> provider4) {
        return new NotificationWakeUpCoordinator_Factory(provider, provider2, provider3, provider4);
    }
    
    public static NotificationWakeUpCoordinator provideInstance(final Provider<HeadsUpManager> provider, final Provider<StatusBarStateController> provider2, final Provider<KeyguardBypassController> provider3, final Provider<DozeParameters> provider4) {
        return new NotificationWakeUpCoordinator(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public NotificationWakeUpCoordinator get() {
        return provideInstance(this.mHeadsUpManagerProvider, this.statusBarStateControllerProvider, this.bypassControllerProvider, this.dozeParametersProvider);
    }
}
