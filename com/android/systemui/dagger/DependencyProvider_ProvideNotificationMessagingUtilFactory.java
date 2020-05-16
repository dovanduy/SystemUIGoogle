// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import com.android.internal.util.NotificationMessagingUtil;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideNotificationMessagingUtilFactory implements Factory<NotificationMessagingUtil>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideNotificationMessagingUtilFactory(final DependencyProvider module, final Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }
    
    public static DependencyProvider_ProvideNotificationMessagingUtilFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return new DependencyProvider_ProvideNotificationMessagingUtilFactory(dependencyProvider, provider);
    }
    
    public static NotificationMessagingUtil provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return proxyProvideNotificationMessagingUtil(dependencyProvider, provider.get());
    }
    
    public static NotificationMessagingUtil proxyProvideNotificationMessagingUtil(final DependencyProvider dependencyProvider, final Context context) {
        final NotificationMessagingUtil provideNotificationMessagingUtil = dependencyProvider.provideNotificationMessagingUtil(context);
        Preconditions.checkNotNull(provideNotificationMessagingUtil, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationMessagingUtil;
    }
    
    @Override
    public NotificationMessagingUtil get() {
        return provideInstance(this.module, this.contextProvider);
    }
}
