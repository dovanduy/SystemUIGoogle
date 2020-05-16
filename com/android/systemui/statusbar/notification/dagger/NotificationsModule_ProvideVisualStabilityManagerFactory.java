// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.os.Handler;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideVisualStabilityManagerFactory implements Factory<VisualStabilityManager>
{
    private final Provider<Handler> handlerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    
    public NotificationsModule_ProvideVisualStabilityManagerFactory(final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<Handler> handlerProvider) {
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.handlerProvider = handlerProvider;
    }
    
    public static NotificationsModule_ProvideVisualStabilityManagerFactory create(final Provider<NotificationEntryManager> provider, final Provider<Handler> provider2) {
        return new NotificationsModule_ProvideVisualStabilityManagerFactory(provider, provider2);
    }
    
    public static VisualStabilityManager provideInstance(final Provider<NotificationEntryManager> provider, final Provider<Handler> provider2) {
        return proxyProvideVisualStabilityManager(provider.get(), provider2.get());
    }
    
    public static VisualStabilityManager proxyProvideVisualStabilityManager(final NotificationEntryManager notificationEntryManager, final Handler handler) {
        final VisualStabilityManager provideVisualStabilityManager = NotificationsModule.provideVisualStabilityManager(notificationEntryManager, handler);
        Preconditions.checkNotNull(provideVisualStabilityManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideVisualStabilityManager;
    }
    
    @Override
    public VisualStabilityManager get() {
        return provideInstance(this.notificationEntryManagerProvider, this.handlerProvider);
    }
}
