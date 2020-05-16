// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.os.RemoteException;
import android.content.ComponentName;
import android.util.Log;
import java.util.Iterator;
import android.service.notification.StatusBarNotification;
import android.content.pm.ShortcutInfo;
import android.app.NotificationChannel;
import android.service.notification.NotificationListenerService$Ranking;
import android.service.notification.NotificationListenerService$RankingMap;
import java.util.ArrayList;
import android.app.NotificationManager;
import java.util.List;
import android.os.Handler;
import android.content.Context;
import android.annotation.SuppressLint;
import com.android.systemui.statusbar.phone.NotificationListenerWithPlugins;

@SuppressLint({ "OverrideAbstract" })
public class NotificationListener extends NotificationListenerWithPlugins
{
    private final Context mContext;
    private final Handler mMainHandler;
    private final List<NotificationHandler> mNotificationHandlers;
    private final NotificationManager mNotificationManager;
    private final ArrayList<NotificationSettingsListener> mSettingsListeners;
    
    public NotificationListener(final Context mContext, final NotificationManager mNotificationManager, final Handler mMainHandler) {
        this.mNotificationHandlers = new ArrayList<NotificationHandler>();
        this.mSettingsListeners = new ArrayList<NotificationSettingsListener>();
        this.mContext = mContext;
        this.mNotificationManager = mNotificationManager;
        this.mMainHandler = mMainHandler;
    }
    
    private static NotificationListenerService$Ranking getRankingOrTemporaryStandIn(final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final String s) {
        final NotificationListenerService$Ranking notificationListenerService$Ranking = new NotificationListenerService$Ranking();
        if (!notificationListenerService$RankingMap.getRanking(s, notificationListenerService$Ranking)) {
            notificationListenerService$Ranking.populate(s, 0, false, 0, 0, 0, (CharSequence)null, (String)null, (NotificationChannel)null, new ArrayList(), new ArrayList(), false, 0, false, 0L, false, new ArrayList(), new ArrayList(), false, false, false, (ShortcutInfo)null, false);
        }
        return notificationListenerService$Ranking;
    }
    
    public void addNotificationHandler(final NotificationHandler notificationHandler) {
        if (!this.mNotificationHandlers.contains(notificationHandler)) {
            this.mNotificationHandlers.add(notificationHandler);
            return;
        }
        throw new IllegalArgumentException("Listener is already added");
    }
    
    public void addNotificationSettingsListener(final NotificationSettingsListener e) {
        this.mSettingsListeners.add(e);
    }
    
    public void onListenerConnected() {
        this.onPluginConnected();
        final StatusBarNotification[] activeNotifications = this.getActiveNotifications();
        if (activeNotifications == null) {
            Log.w("NotificationListener", "onListenerConnected unable to get active notifications.");
            return;
        }
        this.mMainHandler.post((Runnable)new _$$Lambda$NotificationListener$IqvG8K3BFQSXJ_G1S_U_QONW3G4(this, activeNotifications, this.getCurrentRanking()));
        this.onSilentStatusBarIconsVisibilityChanged(this.mNotificationManager.shouldHideSilentStatusBarIcons());
    }
    
    public void onNotificationPosted(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        if (statusBarNotification != null && !this.onPluginNotificationPosted(statusBarNotification, notificationListenerService$RankingMap)) {
            this.mMainHandler.post((Runnable)new _$$Lambda$NotificationListener$NvFmU0XrVPuc5pizHcri9I0apkw(this, statusBarNotification, notificationListenerService$RankingMap));
        }
    }
    
    public void onNotificationRankingUpdate(NotificationListenerService$RankingMap onPluginRankingUpdate) {
        if (onPluginRankingUpdate != null) {
            onPluginRankingUpdate = this.onPluginRankingUpdate(onPluginRankingUpdate);
            this.mMainHandler.post((Runnable)new _$$Lambda$NotificationListener$MPB4hTnfgfJz099PViVIkkbEBIE(this, onPluginRankingUpdate));
        }
    }
    
    public void onNotificationRemoved(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        this.onNotificationRemoved(statusBarNotification, notificationListenerService$RankingMap, 0);
    }
    
    public void onNotificationRemoved(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final int n) {
        if (statusBarNotification != null && !this.onPluginNotificationRemoved(statusBarNotification, notificationListenerService$RankingMap)) {
            this.mMainHandler.post((Runnable)new _$$Lambda$NotificationListener$WRx7hwu_hf4Oq9iR81FcmuDk9R0(this, statusBarNotification, notificationListenerService$RankingMap, n));
        }
    }
    
    public void onSilentStatusBarIconsVisibilityChanged(final boolean b) {
        final Iterator<NotificationSettingsListener> iterator = this.mSettingsListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onStatusBarIconsBehaviorChanged(b);
        }
    }
    
    public void registerAsSystemService() {
        try {
            this.registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), this.getClass().getCanonicalName()), -1);
        }
        catch (RemoteException ex) {
            Log.e("NotificationListener", "Unable to register notification listener", (Throwable)ex);
        }
    }
    
    public interface NotificationHandler
    {
        void onNotificationPosted(final StatusBarNotification p0, final NotificationListenerService$RankingMap p1);
        
        void onNotificationRankingUpdate(final NotificationListenerService$RankingMap p0);
        
        void onNotificationRemoved(final StatusBarNotification p0, final NotificationListenerService$RankingMap p1, final int p2);
    }
    
    public interface NotificationSettingsListener
    {
        default void onStatusBarIconsBehaviorChanged(final boolean b) {
        }
    }
}
