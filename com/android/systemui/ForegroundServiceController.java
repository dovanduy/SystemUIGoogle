// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.os.UserHandle;
import com.android.systemui.util.Assert;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import com.android.systemui.appops.AppOpsController;
import android.util.SparseArray;
import android.os.Handler;
import com.android.systemui.statusbar.notification.NotificationEntryManager;

public class ForegroundServiceController
{
    public static final int[] APP_OPS;
    private final NotificationEntryManager mEntryManager;
    private final Handler mMainHandler;
    private final Object mMutex;
    private final SparseArray<ForegroundServicesUserState> mUserServices;
    
    static {
        APP_OPS = new int[] { 26, 24, 27, 0, 1 };
    }
    
    public ForegroundServiceController(final NotificationEntryManager mEntryManager, final AppOpsController appOpsController, final Handler mMainHandler) {
        this.mUserServices = (SparseArray<ForegroundServicesUserState>)new SparseArray();
        this.mMutex = new Object();
        this.mEntryManager = mEntryManager;
        this.mMainHandler = mMainHandler;
        appOpsController.addCallback(ForegroundServiceController.APP_OPS, (AppOpsController.Callback)new _$$Lambda$ForegroundServiceController$6VuUZsronrAWhzH49_rmYg_sL9o(this));
    }
    
    public ArraySet<Integer> getAppOps(final int n, final String s) {
        synchronized (this.mMutex) {
            final ForegroundServicesUserState foregroundServicesUserState = (ForegroundServicesUserState)this.mUserServices.get(n);
            if (foregroundServicesUserState == null) {
                return null;
            }
            return foregroundServicesUserState.getFeatures(s);
        }
    }
    
    public String getStandardLayoutKey(final int n, String standardLayoutKey) {
        synchronized (this.mMutex) {
            final ForegroundServicesUserState foregroundServicesUserState = (ForegroundServicesUserState)this.mUserServices.get(n);
            if (foregroundServicesUserState == null) {
                return null;
            }
            standardLayoutKey = foregroundServicesUserState.getStandardLayoutKey(standardLayoutKey);
            return standardLayoutKey;
        }
    }
    
    public boolean isDisclosureNeededForUser(final int n) {
        synchronized (this.mMutex) {
            final ForegroundServicesUserState foregroundServicesUserState = (ForegroundServicesUserState)this.mUserServices.get(n);
            return foregroundServicesUserState != null && foregroundServicesUserState.isDisclosureNeeded();
        }
    }
    
    public boolean isDisclosureNotification(final StatusBarNotification statusBarNotification) {
        return statusBarNotification.getId() == 40 && statusBarNotification.getTag() == null && statusBarNotification.getPackageName().equals("android");
    }
    
    public boolean isSystemAlertNotification(final StatusBarNotification statusBarNotification) {
        return statusBarNotification.getPackageName().equals("android") && statusBarNotification.getTag() != null && statusBarNotification.getTag().contains("AlertWindowNotification");
    }
    
    public boolean isSystemAlertWarningNeeded(final int n, final String s) {
        synchronized (this.mMutex) {
            final ForegroundServicesUserState foregroundServicesUserState = (ForegroundServicesUserState)this.mUserServices.get(n);
            boolean b = false;
            if (foregroundServicesUserState == null) {
                return false;
            }
            if (foregroundServicesUserState.getStandardLayoutKey(s) == null) {
                b = true;
            }
            return b;
        }
    }
    
    void onAppOpChanged(final int n, final int n2, final String str, final boolean b) {
        Assert.isMainThread();
        final int userId = UserHandle.getUserId(n2);
        synchronized (this.mMutex) {
            ForegroundServicesUserState foregroundServicesUserState;
            if ((foregroundServicesUserState = (ForegroundServicesUserState)this.mUserServices.get(userId)) == null) {
                foregroundServicesUserState = new ForegroundServicesUserState();
                this.mUserServices.put(userId, (Object)foregroundServicesUserState);
            }
            if (b) {
                foregroundServicesUserState.addOp(str, n);
            }
            else {
                foregroundServicesUserState.removeOp(str, n);
            }
            // monitorexit(this.mMutex)
            final String standardLayoutKey = this.getStandardLayoutKey(userId, str);
            if (standardLayoutKey != null) {
                final NotificationEntry pendingOrActiveNotif = this.mEntryManager.getPendingOrActiveNotif(standardLayoutKey);
                if (pendingOrActiveNotif != null && n2 == pendingOrActiveNotif.getSbn().getUid() && str.equals(pendingOrActiveNotif.getSbn().getPackageName())) {
                    Object mActiveAppOps = pendingOrActiveNotif.mActiveAppOps;
                    // monitorenter(mActiveAppOps)
                    Label_0177: {
                        if (!b) {
                            break Label_0177;
                        }
                        try {
                            boolean b2 = pendingOrActiveNotif.mActiveAppOps.add((Object)n);
                            while (true) {
                                if (b2) {
                                    final NotificationEntryManager mEntryManager = this.mEntryManager;
                                    mActiveAppOps = new StringBuilder();
                                    ((StringBuilder)mActiveAppOps).append("appOpChanged pkg=");
                                    ((StringBuilder)mActiveAppOps).append(str);
                                    mEntryManager.updateNotifications(((StringBuilder)mActiveAppOps).toString());
                                    return;
                                }
                                return;
                                b2 = pendingOrActiveNotif.mActiveAppOps.remove((Object)n);
                                continue;
                            }
                        }
                        // monitorexit(mActiveAppOps)
                        finally {}
                    }
                }
            }
        }
    }
    
    boolean updateUserState(final int n, final UserStateUpdateCallback userStateUpdateCallback, final boolean b) {
        synchronized (this.mMutex) {
            ForegroundServicesUserState foregroundServicesUserState;
            if ((foregroundServicesUserState = (ForegroundServicesUserState)this.mUserServices.get(n)) == null) {
                if (!b) {
                    return false;
                }
                foregroundServicesUserState = new ForegroundServicesUserState();
                this.mUserServices.put(n, (Object)foregroundServicesUserState);
            }
            return userStateUpdateCallback.updateUserState(foregroundServicesUserState);
        }
    }
    
    interface UserStateUpdateCallback
    {
        boolean updateUserState(final ForegroundServicesUserState p0);
    }
}
