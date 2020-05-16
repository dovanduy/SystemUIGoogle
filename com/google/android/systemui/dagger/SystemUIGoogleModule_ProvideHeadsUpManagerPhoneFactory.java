// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import javax.inject.Provider;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import dagger.internal.Factory;

public final class SystemUIGoogleModule_ProvideHeadsUpManagerPhoneFactory implements Factory<HeadsUpManagerPhone>
{
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public SystemUIGoogleModule_ProvideHeadsUpManagerPhoneFactory(final Provider<Context> contextProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<KeyguardBypassController> bypassControllerProvider, final Provider<NotificationGroupManager> groupManagerProvider, final Provider<ConfigurationController> configurationControllerProvider) {
        this.contextProvider = contextProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }
    
    public static SystemUIGoogleModule_ProvideHeadsUpManagerPhoneFactory create(final Provider<Context> provider, final Provider<StatusBarStateController> provider2, final Provider<KeyguardBypassController> provider3, final Provider<NotificationGroupManager> provider4, final Provider<ConfigurationController> provider5) {
        return new SystemUIGoogleModule_ProvideHeadsUpManagerPhoneFactory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static HeadsUpManagerPhone provideInstance(final Provider<Context> provider, final Provider<StatusBarStateController> provider2, final Provider<KeyguardBypassController> provider3, final Provider<NotificationGroupManager> provider4, final Provider<ConfigurationController> provider5) {
        return proxyProvideHeadsUpManagerPhone(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    public static HeadsUpManagerPhone proxyProvideHeadsUpManagerPhone(final Context context, final StatusBarStateController statusBarStateController, final KeyguardBypassController keyguardBypassController, final NotificationGroupManager notificationGroupManager, final ConfigurationController configurationController) {
        final HeadsUpManagerPhone provideHeadsUpManagerPhone = SystemUIGoogleModule.provideHeadsUpManagerPhone(context, statusBarStateController, keyguardBypassController, notificationGroupManager, configurationController);
        Preconditions.checkNotNull(provideHeadsUpManagerPhone, "Cannot return null from a non-@Nullable @Provides method");
        return provideHeadsUpManagerPhone;
    }
    
    @Override
    public HeadsUpManagerPhone get() {
        return provideInstance(this.contextProvider, this.statusBarStateControllerProvider, this.bypassControllerProvider, this.groupManagerProvider, this.configurationControllerProvider);
    }
}
