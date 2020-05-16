// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.notification.init.NotificationsControllerStub;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideNotificationsControllerFactory implements Factory<NotificationsController>
{
    private final Provider<Context> contextProvider;
    private final Provider<NotificationsControllerImpl> realControllerProvider;
    private final Provider<NotificationsControllerStub> stubControllerProvider;
    
    public NotificationsModule_ProvideNotificationsControllerFactory(final Provider<Context> contextProvider, final Provider<NotificationsControllerImpl> realControllerProvider, final Provider<NotificationsControllerStub> stubControllerProvider) {
        this.contextProvider = contextProvider;
        this.realControllerProvider = realControllerProvider;
        this.stubControllerProvider = stubControllerProvider;
    }
    
    public static NotificationsModule_ProvideNotificationsControllerFactory create(final Provider<Context> provider, final Provider<NotificationsControllerImpl> provider2, final Provider<NotificationsControllerStub> provider3) {
        return new NotificationsModule_ProvideNotificationsControllerFactory(provider, provider2, provider3);
    }
    
    public static NotificationsController provideInstance(final Provider<Context> provider, final Provider<NotificationsControllerImpl> provider2, final Provider<NotificationsControllerStub> provider3) {
        return proxyProvideNotificationsController(provider.get(), DoubleCheck.lazy(provider2), DoubleCheck.lazy(provider3));
    }
    
    public static NotificationsController proxyProvideNotificationsController(final Context context, final Lazy<NotificationsControllerImpl> lazy, final Lazy<NotificationsControllerStub> lazy2) {
        final NotificationsController provideNotificationsController = NotificationsModule.provideNotificationsController(context, lazy, lazy2);
        Preconditions.checkNotNull(provideNotificationsController, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationsController;
    }
    
    @Override
    public NotificationsController get() {
        return provideInstance(this.contextProvider, this.realControllerProvider, this.stubControllerProvider);
    }
}
