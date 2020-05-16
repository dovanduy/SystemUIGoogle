// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.WindowManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.CommandQueue;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LightsOutNotifController_Factory implements Factory<LightsOutNotifController>
{
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<WindowManager> windowManagerProvider;
    
    public LightsOutNotifController_Factory(final Provider<WindowManager> windowManagerProvider, final Provider<NotificationEntryManager> entryManagerProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.windowManagerProvider = windowManagerProvider;
        this.entryManagerProvider = entryManagerProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static LightsOutNotifController_Factory create(final Provider<WindowManager> provider, final Provider<NotificationEntryManager> provider2, final Provider<CommandQueue> provider3) {
        return new LightsOutNotifController_Factory(provider, provider2, provider3);
    }
    
    public static LightsOutNotifController provideInstance(final Provider<WindowManager> provider, final Provider<NotificationEntryManager> provider2, final Provider<CommandQueue> provider3) {
        return new LightsOutNotifController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public LightsOutNotifController get() {
        return provideInstance(this.windowManagerProvider, this.entryManagerProvider, this.commandQueueProvider);
    }
}
