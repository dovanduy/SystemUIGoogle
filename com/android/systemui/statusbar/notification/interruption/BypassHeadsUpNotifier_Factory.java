// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.interruption;

import com.android.systemui.tuner.TunerService;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.content.Context;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BypassHeadsUpNotifier_Factory implements Factory<BypassHeadsUpNotifier>
{
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<NotificationMediaManager> mediaManagerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<TunerService> tunerServiceProvider;
    
    public BypassHeadsUpNotifier_Factory(final Provider<Context> contextProvider, final Provider<KeyguardBypassController> bypassControllerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<HeadsUpManagerPhone> headsUpManagerProvider, final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, final Provider<NotificationMediaManager> mediaManagerProvider, final Provider<NotificationEntryManager> entryManagerProvider, final Provider<TunerService> tunerServiceProvider) {
        this.contextProvider = contextProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.mediaManagerProvider = mediaManagerProvider;
        this.entryManagerProvider = entryManagerProvider;
        this.tunerServiceProvider = tunerServiceProvider;
    }
    
    public static BypassHeadsUpNotifier_Factory create(final Provider<Context> provider, final Provider<KeyguardBypassController> provider2, final Provider<StatusBarStateController> provider3, final Provider<HeadsUpManagerPhone> provider4, final Provider<NotificationLockscreenUserManager> provider5, final Provider<NotificationMediaManager> provider6, final Provider<NotificationEntryManager> provider7, final Provider<TunerService> provider8) {
        return new BypassHeadsUpNotifier_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static BypassHeadsUpNotifier provideInstance(final Provider<Context> provider, final Provider<KeyguardBypassController> provider2, final Provider<StatusBarStateController> provider3, final Provider<HeadsUpManagerPhone> provider4, final Provider<NotificationLockscreenUserManager> provider5, final Provider<NotificationMediaManager> provider6, final Provider<NotificationEntryManager> provider7, final Provider<TunerService> provider8) {
        return new BypassHeadsUpNotifier(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    @Override
    public BypassHeadsUpNotifier get() {
        return provideInstance(this.contextProvider, this.bypassControllerProvider, this.statusBarStateControllerProvider, this.headsUpManagerProvider, this.notificationLockscreenUserManagerProvider, this.mediaManagerProvider, this.entryManagerProvider, this.tunerServiceProvider);
    }
}
