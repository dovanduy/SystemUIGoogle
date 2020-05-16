// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.plugins.FalsingManager;
import android.content.Context;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PulseExpansionHandler_Factory implements Factory<PulseExpansionHandler>
{
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<NotificationRoundnessManager> roundnessManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider;
    
    public PulseExpansionHandler_Factory(final Provider<Context> contextProvider, final Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider, final Provider<KeyguardBypassController> bypassControllerProvider, final Provider<HeadsUpManagerPhone> headsUpManagerProvider, final Provider<NotificationRoundnessManager> roundnessManagerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<FalsingManager> falsingManagerProvider) {
        this.contextProvider = contextProvider;
        this.wakeUpCoordinatorProvider = wakeUpCoordinatorProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.roundnessManagerProvider = roundnessManagerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.falsingManagerProvider = falsingManagerProvider;
    }
    
    public static PulseExpansionHandler_Factory create(final Provider<Context> provider, final Provider<NotificationWakeUpCoordinator> provider2, final Provider<KeyguardBypassController> provider3, final Provider<HeadsUpManagerPhone> provider4, final Provider<NotificationRoundnessManager> provider5, final Provider<StatusBarStateController> provider6, final Provider<FalsingManager> provider7) {
        return new PulseExpansionHandler_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
    }
    
    public static PulseExpansionHandler provideInstance(final Provider<Context> provider, final Provider<NotificationWakeUpCoordinator> provider2, final Provider<KeyguardBypassController> provider3, final Provider<HeadsUpManagerPhone> provider4, final Provider<NotificationRoundnessManager> provider5, final Provider<StatusBarStateController> provider6, final Provider<FalsingManager> provider7) {
        return new PulseExpansionHandler(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get());
    }
    
    @Override
    public PulseExpansionHandler get() {
        return provideInstance(this.contextProvider, this.wakeUpCoordinatorProvider, this.bypassControllerProvider, this.headsUpManagerProvider, this.roundnessManagerProvider, this.statusBarStateControllerProvider, this.falsingManagerProvider);
    }
}
