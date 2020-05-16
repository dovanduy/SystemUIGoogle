// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.headsup;

import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class HeadsUpBindController_Factory implements Factory<HeadsUpBindController>
{
    private final Provider<HeadsUpViewBinder> headsUpViewBinderProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    
    public HeadsUpBindController_Factory(final Provider<HeadsUpViewBinder> headsUpViewBinderProvider, final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider) {
        this.headsUpViewBinderProvider = headsUpViewBinderProvider;
        this.notificationInterruptStateProvider = notificationInterruptStateProvider;
    }
    
    public static HeadsUpBindController_Factory create(final Provider<HeadsUpViewBinder> provider, final Provider<NotificationInterruptStateProvider> provider2) {
        return new HeadsUpBindController_Factory(provider, provider2);
    }
    
    public static HeadsUpBindController provideInstance(final Provider<HeadsUpViewBinder> provider, final Provider<NotificationInterruptStateProvider> provider2) {
        return new HeadsUpBindController(provider.get(), provider2.get());
    }
    
    @Override
    public HeadsUpBindController get() {
        return provideInstance(this.headsUpViewBinderProvider, this.notificationInterruptStateProvider);
    }
}
