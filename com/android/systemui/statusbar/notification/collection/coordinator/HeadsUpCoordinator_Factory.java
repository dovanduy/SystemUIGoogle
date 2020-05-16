// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.headsup.HeadsUpViewBinder;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class HeadsUpCoordinator_Factory implements Factory<HeadsUpCoordinator>
{
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<HeadsUpViewBinder> headsUpViewBinderProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    
    public HeadsUpCoordinator_Factory(final Provider<HeadsUpManager> headsUpManagerProvider, final Provider<HeadsUpViewBinder> headsUpViewBinderProvider, final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider, final Provider<NotificationRemoteInputManager> remoteInputManagerProvider) {
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.headsUpViewBinderProvider = headsUpViewBinderProvider;
        this.notificationInterruptStateProvider = notificationInterruptStateProvider;
        this.remoteInputManagerProvider = remoteInputManagerProvider;
    }
    
    public static HeadsUpCoordinator_Factory create(final Provider<HeadsUpManager> provider, final Provider<HeadsUpViewBinder> provider2, final Provider<NotificationInterruptStateProvider> provider3, final Provider<NotificationRemoteInputManager> provider4) {
        return new HeadsUpCoordinator_Factory(provider, provider2, provider3, provider4);
    }
    
    public static HeadsUpCoordinator provideInstance(final Provider<HeadsUpManager> provider, final Provider<HeadsUpViewBinder> provider2, final Provider<NotificationInterruptStateProvider> provider3, final Provider<NotificationRemoteInputManager> provider4) {
        return new HeadsUpCoordinator(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public HeadsUpCoordinator get() {
        return provideInstance(this.headsUpManagerProvider, this.headsUpViewBinderProvider, this.notificationInterruptStateProvider, this.remoteInputManagerProvider);
    }
}
