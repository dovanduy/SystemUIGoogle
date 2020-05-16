// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.dagger;

import dagger.internal.Preconditions;
import android.service.notification.StatusBarNotification;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory implements Factory<String>
{
    private final Provider<Context> contextProvider;
    private final Provider<StatusBarNotification> statusBarNotificationProvider;
    
    public ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory(final Provider<Context> contextProvider, final Provider<StatusBarNotification> statusBarNotificationProvider) {
        this.contextProvider = contextProvider;
        this.statusBarNotificationProvider = statusBarNotificationProvider;
    }
    
    public static ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory create(final Provider<Context> provider, final Provider<StatusBarNotification> provider2) {
        return new ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory(provider, provider2);
    }
    
    public static String provideInstance(final Provider<Context> provider, final Provider<StatusBarNotification> provider2) {
        return proxyProvideAppName(provider.get(), provider2.get());
    }
    
    public static String proxyProvideAppName(final Context context, final StatusBarNotification statusBarNotification) {
        final String provideAppName = ExpandableNotificationRowComponent.ExpandableNotificationRowModule.provideAppName(context, statusBarNotification);
        Preconditions.checkNotNull(provideAppName, "Cannot return null from a non-@Nullable @Provides method");
        return provideAppName;
    }
    
    @Override
    public String get() {
        return provideInstance(this.contextProvider, this.statusBarNotificationProvider);
    }
}
