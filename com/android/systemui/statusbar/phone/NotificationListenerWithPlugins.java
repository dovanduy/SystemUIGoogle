// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.os.RemoteException;
import com.android.systemui.Dependency;
import com.android.systemui.shared.plugins.PluginManager;
import android.content.ComponentName;
import com.android.systemui.plugins.Plugin;
import android.content.Context;
import java.util.function.Consumer;
import java.util.Iterator;
import android.service.notification.NotificationListenerService$RankingMap;
import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import com.android.systemui.plugins.NotificationListenerController;
import com.android.systemui.plugins.PluginListener;
import android.service.notification.NotificationListenerService;

public class NotificationListenerWithPlugins extends NotificationListenerService implements PluginListener<NotificationListenerController>
{
    private boolean mConnected;
    private ArrayList<NotificationListenerController> mPlugins;
    
    public NotificationListenerWithPlugins() {
        this.mPlugins = new ArrayList<NotificationListenerController>();
    }
    
    static /* synthetic */ StatusBarNotification[] access$001(final NotificationListenerWithPlugins notificationListenerWithPlugins) {
        return notificationListenerWithPlugins.getActiveNotifications();
    }
    
    static /* synthetic */ NotificationListenerService$RankingMap access$101(final NotificationListenerWithPlugins notificationListenerWithPlugins) {
        return notificationListenerWithPlugins.getCurrentRanking();
    }
    
    private NotificationListenerController.NotificationProvider getProvider() {
        return new NotificationListenerController.NotificationProvider() {
            @Override
            public void addNotification(final StatusBarNotification statusBarNotification) {
                NotificationListenerWithPlugins.this.onNotificationPosted(statusBarNotification, this.getRankingMap());
            }
            
            @Override
            public StatusBarNotification[] getActiveNotifications() {
                return NotificationListenerWithPlugins.access$001(NotificationListenerWithPlugins.this);
            }
            
            @Override
            public NotificationListenerService$RankingMap getRankingMap() {
                return NotificationListenerWithPlugins.access$101(NotificationListenerWithPlugins.this);
            }
            
            @Override
            public void removeNotification(final StatusBarNotification statusBarNotification) {
                NotificationListenerWithPlugins.this.onNotificationRemoved(statusBarNotification, this.getRankingMap());
            }
            
            @Override
            public void updateRanking() {
                NotificationListenerWithPlugins.this.onNotificationRankingUpdate(this.getRankingMap());
            }
        };
    }
    
    public StatusBarNotification[] getActiveNotifications() {
        StatusBarNotification[] array = super.getActiveNotifications();
        final Iterator<NotificationListenerController> iterator = this.mPlugins.iterator();
        while (iterator.hasNext()) {
            array = iterator.next().getActiveNotifications(array);
        }
        return array;
    }
    
    public NotificationListenerService$RankingMap getCurrentRanking() {
        NotificationListenerService$RankingMap notificationListenerService$RankingMap = super.getCurrentRanking();
        final Iterator<NotificationListenerController> iterator = this.mPlugins.iterator();
        while (iterator.hasNext()) {
            notificationListenerService$RankingMap = iterator.next().getCurrentRanking(notificationListenerService$RankingMap);
        }
        return notificationListenerService$RankingMap;
    }
    
    public void onPluginConnected() {
        this.mConnected = true;
        this.mPlugins.forEach(new _$$Lambda$NotificationListenerWithPlugins$AOWJwBGrUF4vFOVx_Lxlu4eVQD0(this));
    }
    
    public void onPluginConnected(final NotificationListenerController e, final Context context) {
        this.mPlugins.add(e);
        if (this.mConnected) {
            e.onListenerConnected(this.getProvider());
        }
    }
    
    public void onPluginDisconnected(final NotificationListenerController o) {
        this.mPlugins.remove(o);
    }
    
    public boolean onPluginNotificationPosted(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        final Iterator<NotificationListenerController> iterator = this.mPlugins.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().onNotificationPosted(statusBarNotification, notificationListenerService$RankingMap)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean onPluginNotificationRemoved(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        final Iterator<NotificationListenerController> iterator = this.mPlugins.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().onNotificationRemoved(statusBarNotification, notificationListenerService$RankingMap)) {
                return true;
            }
        }
        return false;
    }
    
    public NotificationListenerService$RankingMap onPluginRankingUpdate(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        return this.getCurrentRanking();
    }
    
    public void registerAsSystemService(final Context context, final ComponentName componentName, final int n) throws RemoteException {
        super.registerAsSystemService(context, componentName, n);
        Dependency.get(PluginManager.class).addPluginListener((PluginListener<Plugin>)this, NotificationListenerController.class);
    }
    
    public void unregisterAsSystemService() throws RemoteException {
        super.unregisterAsSystemService();
        Dependency.get(PluginManager.class).removePluginListener(this);
    }
}
