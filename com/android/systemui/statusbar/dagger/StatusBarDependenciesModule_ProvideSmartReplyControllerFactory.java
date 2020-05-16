// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.dagger;

import dagger.internal.Preconditions;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import javax.inject.Provider;
import com.android.systemui.statusbar.SmartReplyController;
import dagger.internal.Factory;

public final class StatusBarDependenciesModule_ProvideSmartReplyControllerFactory implements Factory<SmartReplyController>
{
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;
    
    public StatusBarDependenciesModule_ProvideSmartReplyControllerFactory(final Provider<NotificationEntryManager> entryManagerProvider, final Provider<IStatusBarService> statusBarServiceProvider) {
        this.entryManagerProvider = entryManagerProvider;
        this.statusBarServiceProvider = statusBarServiceProvider;
    }
    
    public static StatusBarDependenciesModule_ProvideSmartReplyControllerFactory create(final Provider<NotificationEntryManager> provider, final Provider<IStatusBarService> provider2) {
        return new StatusBarDependenciesModule_ProvideSmartReplyControllerFactory(provider, provider2);
    }
    
    public static SmartReplyController provideInstance(final Provider<NotificationEntryManager> provider, final Provider<IStatusBarService> provider2) {
        return proxyProvideSmartReplyController(provider.get(), provider2.get());
    }
    
    public static SmartReplyController proxyProvideSmartReplyController(final NotificationEntryManager notificationEntryManager, final IStatusBarService statusBarService) {
        final SmartReplyController provideSmartReplyController = StatusBarDependenciesModule.provideSmartReplyController(notificationEntryManager, statusBarService);
        Preconditions.checkNotNull(provideSmartReplyController, "Cannot return null from a non-@Nullable @Provides method");
        return provideSmartReplyController;
    }
    
    @Override
    public SmartReplyController get() {
        return provideInstance(this.entryManagerProvider, this.statusBarServiceProvider);
    }
}
