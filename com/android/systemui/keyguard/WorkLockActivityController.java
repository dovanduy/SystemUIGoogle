// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.os.Parcelable;
import android.content.ComponentName;
import android.util.Log;
import android.os.RemoteException;
import android.app.ProfilerInfo;
import android.os.IBinder;
import android.os.Bundle;
import android.content.Intent;
import com.android.internal.annotations.VisibleForTesting;
import android.app.ActivityTaskManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import android.app.IActivityTaskManager;
import android.content.Context;

public class WorkLockActivityController
{
    private static final String TAG = "WorkLockActivityController";
    private final Context mContext;
    private final IActivityTaskManager mIatm;
    private final TaskStackChangeListener mLockListener;
    
    public WorkLockActivityController(final Context context) {
        this(context, ActivityManagerWrapper.getInstance(), ActivityTaskManager.getService());
    }
    
    @VisibleForTesting
    WorkLockActivityController(final Context mContext, final ActivityManagerWrapper activityManagerWrapper, final IActivityTaskManager mIatm) {
        final TaskStackChangeListener mLockListener = new TaskStackChangeListener() {
            @Override
            public void onTaskProfileLocked(final int n, final int n2) {
                WorkLockActivityController.this.startWorkChallengeInTask(n, n2);
            }
        };
        this.mLockListener = mLockListener;
        this.mContext = mContext;
        this.mIatm = mIatm;
        activityManagerWrapper.registerTaskStackListener(mLockListener);
    }
    
    private int startActivityAsUser(final Intent intent, final Bundle bundle, int startActivityAsUser) {
        try {
            startActivityAsUser = this.mIatm.startActivityAsUser(this.mContext.getIApplicationThread(), this.mContext.getBasePackageName(), this.mContext.getAttributionTag(), intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), (IBinder)null, (String)null, 0, 268435456, (ProfilerInfo)null, bundle, startActivityAsUser);
            return startActivityAsUser;
        }
        catch (RemoteException | Exception ex) {
            return -96;
        }
    }
    
    private void startWorkChallengeInTask(final int i, final int n) {
        Object taskDescription;
        try {
            taskDescription = this.mIatm.getTaskDescription(i);
        }
        catch (RemoteException ex) {
            final String tag = WorkLockActivityController.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to get description for task=");
            sb.append(i);
            Log.w(tag, sb.toString());
            taskDescription = null;
        }
        final Intent addFlags = new Intent("android.app.action.CONFIRM_DEVICE_CREDENTIAL_WITH_USER").setComponent(new ComponentName(this.mContext, (Class)WorkLockActivity.class)).putExtra("android.intent.extra.USER_ID", n).putExtra("com.android.systemui.keyguard.extra.TASK_DESCRIPTION", (Parcelable)taskDescription).addFlags(67239936);
        final ActivityOptions basic = ActivityOptions.makeBasic();
        basic.setLaunchTaskId(i);
        basic.setTaskOverlay(true, false);
        if (!ActivityManager.isStartResultSuccessful(this.startActivityAsUser(addFlags, basic.toBundle(), -2))) {
            try {
                this.mIatm.removeTask(i);
            }
            catch (RemoteException ex2) {
                final String tag2 = WorkLockActivityController.TAG;
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Failed to get description for task=");
                sb2.append(i);
                Log.w(tag2, sb2.toString());
            }
        }
    }
}
