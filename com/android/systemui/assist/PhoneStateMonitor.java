// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import android.app.KeyguardManager;
import java.util.function.Function;
import java.util.Iterator;
import java.util.List;
import com.android.systemui.shared.system.PackageManagerWrapper;
import android.content.pm.ResolveInfo;
import java.util.ArrayList;
import android.app.ActivityManager$RunningTaskInfo;
import com.android.systemui.shared.system.TaskStackChangeListener;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.Dependency;
import com.android.systemui.BootCompleteCache;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import android.content.ComponentName;
import android.content.Context;

public final class PhoneStateMonitor
{
    private static final String[] DEFAULT_HOME_CHANGE_ACTIONS;
    private final Context mContext;
    private ComponentName mDefaultHome;
    private boolean mLauncherShowing;
    private final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy;
    private final StatusBarStateController mStatusBarStateController;
    
    static {
        DEFAULT_HOME_CHANGE_ACTIONS = new String[] { "android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED", "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED" };
    }
    
    PhoneStateMonitor(final Context mContext, final BroadcastDispatcher broadcastDispatcher, final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy, final BootCompleteCache bootCompleteCache) {
        this.mContext = mContext;
        this.mStatusBarOptionalLazy = mStatusBarOptionalLazy;
        this.mStatusBarStateController = Dependency.get(StatusBarStateController.class);
        final ActivityManagerWrapper instance = ActivityManagerWrapper.getInstance();
        this.mDefaultHome = getCurrentDefaultHome();
        bootCompleteCache.addListener((BootCompleteCache.BootCompleteListener)new _$$Lambda$PhoneStateMonitor$kU1yau2iyc4oGSlu9ejSJU0AW3w(this));
        final IntentFilter intentFilter = new IntentFilter();
        final String[] default_HOME_CHANGE_ACTIONS = PhoneStateMonitor.DEFAULT_HOME_CHANGE_ACTIONS;
        for (int length = default_HOME_CHANGE_ACTIONS.length, i = 0; i < length; ++i) {
            intentFilter.addAction(default_HOME_CHANGE_ACTIONS[i]);
        }
        broadcastDispatcher.registerReceiver(new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                PhoneStateMonitor.this.mDefaultHome = getCurrentDefaultHome();
            }
        }, intentFilter);
        this.mLauncherShowing = this.isLauncherShowing(instance.getRunningTask());
        instance.registerTaskStackListener(new TaskStackChangeListener() {
            @Override
            public void onTaskMovedToFront(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
                final PhoneStateMonitor this$0 = PhoneStateMonitor.this;
                this$0.mLauncherShowing = this$0.isLauncherShowing(activityManager$RunningTaskInfo);
            }
        });
    }
    
    private static ComponentName getCurrentDefaultHome() {
        final ArrayList<ResolveInfo> list = new ArrayList<ResolveInfo>();
        final ComponentName homeActivities = PackageManagerWrapper.getInstance().getHomeActivities(list);
        if (homeActivities != null) {
            return homeActivities;
        }
        int priority = Integer.MIN_VALUE;
        final Iterator<Object> iterator = list.iterator();
        ComponentName componentName = null;
    Label_0032:
        while (true) {
            componentName = null;
            while (iterator.hasNext()) {
                final ResolveInfo resolveInfo = iterator.next();
                final int priority2 = resolveInfo.priority;
                if (priority2 > priority) {
                    componentName = resolveInfo.activityInfo.getComponentName();
                    priority = resolveInfo.priority;
                }
                else {
                    if (priority2 == priority) {
                        continue Label_0032;
                    }
                    continue;
                }
            }
            break;
        }
        return componentName;
    }
    
    private int getPhoneAppState() {
        if (this.isAppImmersive()) {
            return 9;
        }
        if (this.isAppFullscreen()) {
            return 10;
        }
        return 8;
    }
    
    private int getPhoneLauncherState() {
        if (this.isLauncherInOverview()) {
            return 6;
        }
        if (this.isLauncherInAllApps()) {
            return 7;
        }
        return 5;
    }
    
    private int getPhoneLockscreenState() {
        if (this.isDozing()) {
            return 1;
        }
        if (this.isBouncerShowing()) {
            return 3;
        }
        if (this.isKeyguardLocked()) {
            return 2;
        }
        return 4;
    }
    
    private boolean isAppFullscreen() {
        return this.mStatusBarOptionalLazy.get().get().inFullscreenMode();
    }
    
    private boolean isAppImmersive() {
        return this.mStatusBarOptionalLazy.get().get().inImmersiveMode();
    }
    
    private boolean isBouncerShowing() {
        return this.mStatusBarOptionalLazy.map((Function<? super Lazy<StatusBar>, ? extends Boolean>)_$$Lambda$PhoneStateMonitor$m_3mFsd47OeaWHKnwhE_EoNbkyA.INSTANCE).orElse(Boolean.FALSE);
    }
    
    private boolean isDozing() {
        return this.mStatusBarStateController.isDozing();
    }
    
    private boolean isKeyguardLocked() {
        final KeyguardManager keyguardManager = (KeyguardManager)this.mContext.getSystemService((Class)KeyguardManager.class);
        return keyguardManager != null && keyguardManager.isKeyguardLocked();
    }
    
    private boolean isLauncherInAllApps() {
        return false;
    }
    
    private boolean isLauncherInOverview() {
        return false;
    }
    
    private boolean isLauncherShowing(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        return activityManager$RunningTaskInfo != null && activityManager$RunningTaskInfo.topActivity.equals((Object)this.mDefaultHome);
    }
    
    private boolean isShadeFullscreen() {
        final int state = this.mStatusBarStateController.getState();
        boolean b = true;
        if (state != 1) {
            b = (state == 2 && b);
        }
        return b;
    }
    
    int getPhoneState() {
        int n;
        if (this.isShadeFullscreen()) {
            n = this.getPhoneLockscreenState();
        }
        else if (this.mLauncherShowing) {
            n = this.getPhoneLauncherState();
        }
        else {
            n = this.getPhoneAppState();
        }
        return n;
    }
}
