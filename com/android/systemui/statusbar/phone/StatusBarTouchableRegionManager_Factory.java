// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.bubbles.BubbleController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class StatusBarTouchableRegionManager_Factory implements Factory<StatusBarTouchableRegionManager>
{
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    
    public StatusBarTouchableRegionManager_Factory(final Provider<Context> contextProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<HeadsUpManagerPhone> headsUpManagerProvider, final Provider<BubbleController> bubbleControllerProvider) {
        this.contextProvider = contextProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.bubbleControllerProvider = bubbleControllerProvider;
    }
    
    public static StatusBarTouchableRegionManager_Factory create(final Provider<Context> provider, final Provider<NotificationShadeWindowController> provider2, final Provider<ConfigurationController> provider3, final Provider<HeadsUpManagerPhone> provider4, final Provider<BubbleController> provider5) {
        return new StatusBarTouchableRegionManager_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static StatusBarTouchableRegionManager provideInstance(final Provider<Context> provider, final Provider<NotificationShadeWindowController> provider2, final Provider<ConfigurationController> provider3, final Provider<HeadsUpManagerPhone> provider4, final Provider<BubbleController> provider5) {
        return new StatusBarTouchableRegionManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public StatusBarTouchableRegionManager get() {
        return provideInstance(this.contextProvider, this.notificationShadeWindowControllerProvider, this.configurationControllerProvider, this.headsUpManagerProvider, this.bubbleControllerProvider);
    }
}
