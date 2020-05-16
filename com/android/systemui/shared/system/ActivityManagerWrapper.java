// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.content.Context;
import android.graphics.Rect;
import java.util.List;
import android.app.ActivityTaskManager;
import android.app.ActivityManager$RunningTaskInfo;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.util.Log;
import android.app.ActivityManager;
import java.util.concurrent.Future;
import android.os.Looper;
import android.app.AppGlobals;

public class ActivityManagerWrapper
{
    private static final ActivityManagerWrapper sInstance;
    private final BackgroundExecutor mBackgroundExecutor;
    private final TaskStackChangeListeners mTaskStackChangeListeners;
    
    static {
        sInstance = new ActivityManagerWrapper();
    }
    
    private ActivityManagerWrapper() {
        ((Context)AppGlobals.getInitialApplication()).getPackageManager();
        this.mBackgroundExecutor = BackgroundExecutor.get();
        this.mTaskStackChangeListeners = new TaskStackChangeListeners(Looper.getMainLooper());
    }
    
    public static ActivityManagerWrapper getInstance() {
        return ActivityManagerWrapper.sInstance;
    }
    
    public Future<?> closeSystemWindows(final String s) {
        return this.mBackgroundExecutor.submit(new Runnable(this) {
            @Override
            public void run() {
                try {
                    ActivityManager.getService().closeSystemDialogs(s);
                }
                catch (RemoteException ex) {
                    Log.w("ActivityManagerWrapper", "Failed to close system windows", (Throwable)ex);
                }
            }
        });
    }
    
    public int getCurrentUserId() {
        try {
            final UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            int id;
            if (currentUser != null) {
                id = currentUser.id;
            }
            else {
                id = 0;
            }
            return id;
        }
        catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    
    public ActivityManager$RunningTaskInfo getRunningTask() {
        return this.getRunningTask(false);
    }
    
    public ActivityManager$RunningTaskInfo getRunningTask(final boolean b) {
        try {
            final List filteredTasks = ActivityTaskManager.getService().getFilteredTasks(1, b);
            if (filteredTasks.isEmpty()) {
                return null;
            }
            return filteredTasks.get(0);
        }
        catch (RemoteException ex) {
            return null;
        }
    }
    
    public boolean isLockTaskKioskModeActive() {
        boolean b = false;
        try {
            if (ActivityTaskManager.getService().getLockTaskModeState() == 1) {
                b = true;
            }
            return b;
        }
        catch (RemoteException ex) {
            return b;
        }
    }
    
    public boolean isScreenPinningActive() {
        boolean b = false;
        try {
            if (ActivityTaskManager.getService().getLockTaskModeState() == 2) {
                b = true;
            }
            return b;
        }
        catch (RemoteException ex) {
            return b;
        }
    }
    
    public void registerTaskStackListener(final TaskStackChangeListener taskStackChangeListener) {
        synchronized (this.mTaskStackChangeListeners) {
            this.mTaskStackChangeListeners.addListener(ActivityManager.getService(), taskStackChangeListener);
        }
    }
    
    public boolean setTaskWindowingModeSplitScreenPrimary(final int n, final int n2, final Rect rect) {
        try {
            return ActivityTaskManager.getService().setTaskWindowingModeSplitScreenPrimary(n, true);
        }
        catch (RemoteException ex) {
            return false;
        }
    }
    
    public void unregisterTaskStackListener(final TaskStackChangeListener taskStackChangeListener) {
        synchronized (this.mTaskStackChangeListeners) {
            this.mTaskStackChangeListeners.removeListener(taskStackChangeListener);
        }
    }
}
