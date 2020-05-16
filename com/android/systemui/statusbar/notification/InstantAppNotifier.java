// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.service.notification.StatusBarNotification;
import android.app.IUserSwitchObserver;
import android.app.ActivityManager;
import com.android.systemui.Dependency;
import android.app.Notification$Action;
import android.app.Notification$Style;
import android.app.Notification$BigTextStyle;
import com.android.systemui.R$drawable;
import com.android.systemui.R$color;
import android.os.Parcelable;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.app.Notification$Builder;
import com.android.systemui.util.NotificationChannels;
import android.graphics.drawable.Icon;
import android.app.Notification$Action$Builder;
import android.app.PendingIntent;
import android.net.Uri;
import com.android.systemui.R$string;
import android.os.Bundle;
import java.util.function.Consumer;
import android.os.UserHandle;
import java.util.List;
import android.app.IActivityTaskManager;
import android.app.ActivityManager$RecentTaskInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.app.ActivityManager$StackInfo;
import android.app.ActivityTaskManager;
import android.content.pm.IPackageManager;
import android.app.NotificationManager;
import android.os.RemoteException;
import android.content.Context;
import android.app.SynchronousUserSwitchObserver;
import java.util.concurrent.Executor;
import android.os.Handler;
import com.android.systemui.stackdivider.Divider;
import android.util.Pair;
import android.util.ArraySet;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.SystemUI;

public class InstantAppNotifier extends SystemUI implements Callbacks, Callback
{
    private final CommandQueue mCommandQueue;
    private final ArraySet<Pair<String, Integer>> mCurrentNotifs;
    private final Divider mDivider;
    private boolean mDockedStackExists;
    private final Handler mHandler;
    private KeyguardStateController mKeyguardStateController;
    private final Executor mUiBgExecutor;
    private final SynchronousUserSwitchObserver mUserSwitchListener;
    
    public InstantAppNotifier(final Context context, final CommandQueue mCommandQueue, final Executor mUiBgExecutor, final Divider mDivider) {
        super(context);
        this.mHandler = new Handler();
        this.mCurrentNotifs = (ArraySet<Pair<String, Integer>>)new ArraySet();
        this.mUserSwitchListener = new SynchronousUserSwitchObserver() {
            public void onUserSwitchComplete(final int n) throws RemoteException {
                InstantAppNotifier.this.mHandler.post((Runnable)new _$$Lambda$InstantAppNotifier$1$2maFdVbSGSmI45ss9sfIaHkOm8U(this));
            }
            
            public void onUserSwitching(final int n) throws RemoteException {
            }
        };
        this.mDivider = mDivider;
        this.mCommandQueue = mCommandQueue;
        this.mUiBgExecutor = mUiBgExecutor;
    }
    
    private void checkAndPostForPrimaryScreen(final ArraySet<Pair<String, Integer>> set, final NotificationManager notificationManager, final IPackageManager packageManager) {
        try {
            this.checkAndPostForStack(ActivityTaskManager.getService().getStackInfo(3, 0), set, notificationManager, packageManager);
        }
        catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
        }
    }
    
    private void checkAndPostForStack(final ActivityManager$StackInfo activityManager$StackInfo, final ArraySet<Pair<String, Integer>> set, final NotificationManager notificationManager, final IPackageManager packageManager) {
        if (activityManager$StackInfo != null) {
            try {
                if (activityManager$StackInfo.topActivity != null) {
                    final String packageName = activityManager$StackInfo.topActivity.getPackageName();
                    if (!set.remove((Object)new Pair((Object)packageName, (Object)activityManager$StackInfo.userId))) {
                        final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 8192, activityManager$StackInfo.userId);
                        if (applicationInfo.isInstantApp()) {
                            this.postInstantAppNotif(packageName, activityManager$StackInfo.userId, applicationInfo, notificationManager, activityManager$StackInfo.taskIds[activityManager$StackInfo.taskIds.length - 1]);
                        }
                    }
                }
            }
            catch (RemoteException ex) {
                ex.rethrowFromSystemServer();
            }
        }
    }
    
    private Intent getTaskIntent(final int n, int i) {
        try {
            final IActivityTaskManager service = ActivityTaskManager.getService();
            final int n2 = 0;
            List list;
            for (list = service.getRecentTasks(5, 0, i).getList(), i = n2; i < list.size(); ++i) {
                if (list.get(i).id == n) {
                    return list.get(i).baseIntent;
                }
            }
            return null;
        }
        catch (RemoteException ex) {
            return null;
        }
    }
    
    private void postInstantAppNotif(final String s, final int i, final ApplicationInfo applicationInfo, final NotificationManager notificationManager, final int n) {
        final Bundle bundle = new Bundle();
        bundle.putString("android.substName", super.mContext.getString(R$string.instant_apps));
        this.mCurrentNotifs.add((Object)new Pair((Object)s, (Object)i));
        final String string = super.mContext.getString(R$string.instant_apps_help_url);
        final boolean b = string.isEmpty() ^ true;
        final Context mContext = super.mContext;
        int n2;
        if (b) {
            n2 = R$string.instant_apps_message_with_help;
        }
        else {
            n2 = R$string.instant_apps_message;
        }
        final String string2 = mContext.getString(n2);
        final UserHandle of = UserHandle.of(i);
        final Notification$Action build = new Notification$Action$Builder((Icon)null, (CharSequence)super.mContext.getString(R$string.app_info), PendingIntent.getActivityAsUser(super.mContext, 0, new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", s, (String)null)), 0, (Bundle)null, of)).build();
        PendingIntent activityAsUser;
        if (b) {
            activityAsUser = PendingIntent.getActivityAsUser(super.mContext, 0, new Intent("android.intent.action.VIEW").setData(Uri.parse(string)), 0, (Bundle)null, of);
        }
        else {
            activityAsUser = null;
        }
        final Intent taskIntent = this.getTaskIntent(n, i);
        final Notification$Builder notification$Builder = new Notification$Builder(super.mContext, NotificationChannels.GENERAL);
        if (taskIntent != null && taskIntent.isWebIntent()) {
            taskIntent.setComponent((ComponentName)null).setPackage((String)null).addFlags(512).addFlags(268435456);
            final PendingIntent activityAsUser2 = PendingIntent.getActivityAsUser(super.mContext, 0, taskIntent, 0, (Bundle)null, of);
            ComponentName instantAppInstallerComponent;
            try {
                instantAppInstallerComponent = AppGlobals.getPackageManager().getInstantAppInstallerComponent();
            }
            catch (RemoteException ex) {
                ex.rethrowFromSystemServer();
                instantAppInstallerComponent = null;
            }
            final Intent addCategory = new Intent().setComponent(instantAppInstallerComponent).setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE");
            final StringBuilder sb = new StringBuilder();
            sb.append("unique:");
            sb.append(System.currentTimeMillis());
            notification$Builder.addAction(new Notification$Action$Builder((Icon)null, (CharSequence)super.mContext.getString(R$string.go_to_web), PendingIntent.getActivityAsUser(super.mContext, 0, addCategory.addCategory(sb.toString()).putExtra("android.intent.extra.PACKAGE_NAME", applicationInfo.packageName).putExtra("android.intent.extra.VERSION_CODE", applicationInfo.versionCode & Integer.MAX_VALUE).putExtra("android.intent.extra.LONG_VERSION_CODE", applicationInfo.longVersionCode).putExtra("android.intent.extra.INSTANT_APP_FAILURE", (Parcelable)activityAsUser2), 0, (Bundle)null, of)).build());
        }
        final Notification$Builder setColor = notification$Builder.addExtras(bundle).addAction(build).setContentIntent(activityAsUser).setColor(super.mContext.getColor(R$color.instant_apps_color));
        final Context mContext2 = super.mContext;
        notificationManager.notifyAsUser(s, 7, setColor.setContentTitle((CharSequence)mContext2.getString(R$string.instant_apps_title, new Object[] { applicationInfo.loadLabel(mContext2.getPackageManager()) })).setLargeIcon(Icon.createWithResource(s, applicationInfo.icon)).setSmallIcon(Icon.createWithResource(super.mContext.getPackageName(), R$drawable.instant_icon)).setContentText((CharSequence)string2).setStyle((Notification$Style)new Notification$BigTextStyle().bigText((CharSequence)string2)).setOngoing(true).build(), new UserHandle(i));
    }
    
    private void updateForegroundInstantApps() {
        this.mUiBgExecutor.execute(new _$$Lambda$InstantAppNotifier$_jG9Ev_YNY9H1cwQp_C5lfrjo3s(this, (NotificationManager)super.mContext.getSystemService((Class)NotificationManager.class), AppGlobals.getPackageManager()));
    }
    
    @Override
    public void appTransitionStarting(final int n, final long n2, final long n3, final boolean b) {
        if (super.mContext.getDisplayId() == n) {
            this.updateForegroundInstantApps();
        }
    }
    
    @Override
    public void onKeyguardShowingChanged() {
        this.updateForegroundInstantApps();
    }
    
    @Override
    public void preloadRecentApps() {
        this.updateForegroundInstantApps();
    }
    
    @Override
    public void start() {
        this.mKeyguardStateController = Dependency.get(KeyguardStateController.class);
        while (true) {
            try {
                ActivityManager.getService().registerUserSwitchObserver((IUserSwitchObserver)this.mUserSwitchListener, "InstantAppNotifier");
                this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
                this.mKeyguardStateController.addCallback((KeyguardStateController.Callback)this);
                this.mDivider.registerInSplitScreenListener(new _$$Lambda$InstantAppNotifier$QG8UJHrN7yIZpZAc2flF_n_csdY(this));
                final NotificationManager notificationManager = (NotificationManager)super.mContext.getSystemService((Class)NotificationManager.class);
                for (final StatusBarNotification statusBarNotification : notificationManager.getActiveNotifications()) {
                    if (statusBarNotification.getId() == 7) {
                        notificationManager.cancel(statusBarNotification.getTag(), statusBarNotification.getId());
                    }
                }
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
}
