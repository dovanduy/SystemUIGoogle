// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class WallpaperNotifier_Factory implements Factory<WallpaperNotifier>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    
    public WallpaperNotifier_Factory(final Provider<Context> contextProvider, final Provider<NotificationEntryManager> entryManagerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.entryManagerProvider = entryManagerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static WallpaperNotifier_Factory create(final Provider<Context> provider, final Provider<NotificationEntryManager> provider2, final Provider<BroadcastDispatcher> provider3) {
        return new WallpaperNotifier_Factory(provider, provider2, provider3);
    }
    
    public static WallpaperNotifier provideInstance(final Provider<Context> provider, final Provider<NotificationEntryManager> provider2, final Provider<BroadcastDispatcher> provider3) {
        return new WallpaperNotifier(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public WallpaperNotifier get() {
        return provideInstance(this.contextProvider, this.entryManagerProvider, this.broadcastDispatcherProvider);
    }
}
