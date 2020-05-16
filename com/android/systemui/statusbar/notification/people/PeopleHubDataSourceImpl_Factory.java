// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import android.os.UserManager;
import android.content.pm.PackageManager;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import android.content.pm.LauncherApps;
import android.content.Context;
import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PeopleHubDataSourceImpl_Factory implements Factory<PeopleHubDataSourceImpl>
{
    private final Provider<Executor> bgExecutorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationPersonExtractor> extractorProvider;
    private final Provider<LauncherApps> launcherAppsProvider;
    private final Provider<Executor> mainExecutorProvider;
    private final Provider<NotificationLockscreenUserManager> notifLockscreenUserMgrProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationListener> notificationListenerProvider;
    private final Provider<PackageManager> packageManagerProvider;
    private final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider;
    private final Provider<UserManager> userManagerProvider;
    
    public PeopleHubDataSourceImpl_Factory(final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<NotificationPersonExtractor> extractorProvider, final Provider<UserManager> userManagerProvider, final Provider<LauncherApps> launcherAppsProvider, final Provider<PackageManager> packageManagerProvider, final Provider<Context> contextProvider, final Provider<NotificationListener> notificationListenerProvider, final Provider<Executor> bgExecutorProvider, final Provider<Executor> mainExecutorProvider, final Provider<NotificationLockscreenUserManager> notifLockscreenUserMgrProvider, final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider) {
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.extractorProvider = extractorProvider;
        this.userManagerProvider = userManagerProvider;
        this.launcherAppsProvider = launcherAppsProvider;
        this.packageManagerProvider = packageManagerProvider;
        this.contextProvider = contextProvider;
        this.notificationListenerProvider = notificationListenerProvider;
        this.bgExecutorProvider = bgExecutorProvider;
        this.mainExecutorProvider = mainExecutorProvider;
        this.notifLockscreenUserMgrProvider = notifLockscreenUserMgrProvider;
        this.peopleNotificationIdentifierProvider = peopleNotificationIdentifierProvider;
    }
    
    public static PeopleHubDataSourceImpl_Factory create(final Provider<NotificationEntryManager> provider, final Provider<NotificationPersonExtractor> provider2, final Provider<UserManager> provider3, final Provider<LauncherApps> provider4, final Provider<PackageManager> provider5, final Provider<Context> provider6, final Provider<NotificationListener> provider7, final Provider<Executor> provider8, final Provider<Executor> provider9, final Provider<NotificationLockscreenUserManager> provider10, final Provider<PeopleNotificationIdentifier> provider11) {
        return new PeopleHubDataSourceImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
    }
    
    public static PeopleHubDataSourceImpl provideInstance(final Provider<NotificationEntryManager> provider, final Provider<NotificationPersonExtractor> provider2, final Provider<UserManager> provider3, final Provider<LauncherApps> provider4, final Provider<PackageManager> provider5, final Provider<Context> provider6, final Provider<NotificationListener> provider7, final Provider<Executor> provider8, final Provider<Executor> provider9, final Provider<NotificationLockscreenUserManager> provider10, final Provider<PeopleNotificationIdentifier> provider11) {
        return new PeopleHubDataSourceImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get());
    }
    
    @Override
    public PeopleHubDataSourceImpl get() {
        return provideInstance(this.notificationEntryManagerProvider, this.extractorProvider, this.userManagerProvider, this.launcherAppsProvider, this.packageManagerProvider, this.contextProvider, this.notificationListenerProvider, this.bgExecutorProvider, this.mainExecutorProvider, this.notifLockscreenUserMgrProvider, this.peopleNotificationIdentifierProvider);
    }
}
