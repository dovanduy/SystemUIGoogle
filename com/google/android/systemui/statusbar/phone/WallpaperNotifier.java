// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.statusbar.phone;

import android.content.IntentFilter;
import android.content.ComponentName;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Intent;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.google.android.collect.Sets;
import android.content.BroadcastReceiver;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import android.content.Context;
import java.util.HashSet;

public class WallpaperNotifier
{
    private static final HashSet<String> NOTIFYABLE_PACKAGES;
    private static final String[] NOTIFYABLE_WALLPAPERS;
    private final Context mContext;
    private final NotificationEntryListener mEntryListener;
    private final NotificationEntryManager mEntryManager;
    private boolean mShouldBroadcastNotifications;
    private final CurrentUserTracker mUserTracker;
    private BroadcastReceiver mWallpaperChangedReceiver;
    private String mWallpaperPackage;
    
    static {
        NOTIFYABLE_WALLPAPERS = new String[] { "com.breel.wallpapers.imprint", "com.breel.wallpapers18.tactile", "com.breel.wallpapers18.delight", "com.breel.wallpapers18.miniman", "com.google.pixel.livewallpaper.imprint", "com.google.pixel.livewallpaper.tactile", "com.google.pixel.livewallpaper.delight", "com.google.pixel.livewallpaper.miniman" };
        NOTIFYABLE_PACKAGES = Sets.newHashSet((Object[])new String[] { "com.breel.wallpapers", "com.breel.wallpapers18", "com.google.pixel.livewallpaper" });
    }
    
    public WallpaperNotifier(final Context mContext, final NotificationEntryManager mEntryManager, final BroadcastDispatcher broadcastDispatcher) {
        this.mEntryListener = new NotificationEntryListener() {
            @Override
            public void onNotificationAdded(final NotificationEntry notificationEntry) {
                final boolean b = WallpaperNotifier.this.mUserTracker.getCurrentUserId() == 0;
                if (WallpaperNotifier.this.mShouldBroadcastNotifications && b) {
                    final Intent intent = new Intent();
                    intent.setPackage(WallpaperNotifier.this.mWallpaperPackage);
                    intent.setAction("com.breel.wallpapers.NOTIFICATION_RECEIVED");
                    intent.putExtra("notification_color", notificationEntry.getSbn().getNotification().color);
                    WallpaperNotifier.this.mContext.sendBroadcast(intent, "com.breel.wallpapers.notifications");
                }
            }
        };
        this.mWallpaperChangedReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals("android.intent.action.WALLPAPER_CHANGED")) {
                    WallpaperNotifier.this.checkNotificationBroadcastSupport();
                }
            }
        };
        this.mContext = mContext;
        this.mEntryManager = mEntryManager;
        this.mUserTracker = new CurrentUserTracker(this, broadcastDispatcher) {
            @Override
            public void onUserSwitched(final int n) {
            }
        };
    }
    
    private void checkNotificationBroadcastSupport() {
        int i = 0;
        this.mShouldBroadcastNotifications = false;
        final WallpaperManager wallpaperManager = (WallpaperManager)this.mContext.getSystemService((Class)WallpaperManager.class);
        if (wallpaperManager == null) {
            return;
        }
        final WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        if (wallpaperInfo == null) {
            return;
        }
        final ComponentName component = wallpaperInfo.getComponent();
        final String packageName = component.getPackageName();
        if (!WallpaperNotifier.NOTIFYABLE_PACKAGES.contains(packageName)) {
            return;
        }
        this.mWallpaperPackage = packageName;
        final String className = component.getClassName();
        for (String[] notifyable_WALLPAPERS = WallpaperNotifier.NOTIFYABLE_WALLPAPERS; i < notifyable_WALLPAPERS.length; ++i) {
            if (className.startsWith(notifyable_WALLPAPERS[i])) {
                this.mShouldBroadcastNotifications = true;
                break;
            }
        }
    }
    
    public void attach() {
        this.mEntryManager.addNotificationEntryListener(this.mEntryListener);
        this.mContext.registerReceiver(this.mWallpaperChangedReceiver, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"));
        this.checkNotificationBroadcastSupport();
    }
}
