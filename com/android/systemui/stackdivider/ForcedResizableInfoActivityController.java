// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.os.UserHandle;
import android.app.ActivityOptions;
import android.content.Intent;
import android.widget.Toast;
import com.android.systemui.R$string;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.util.ArraySet;
import android.os.Handler;
import java.util.function.Consumer;
import android.content.Context;

public class ForcedResizableInfoActivityController
{
    private final Context mContext;
    private boolean mDividerDragging;
    private final Consumer<Boolean> mDockedStackExistsListener;
    private final Handler mHandler;
    private final ArraySet<String> mPackagesShownInSession;
    private final ArraySet<PendingTaskRecord> mPendingTasks;
    private final Runnable mTimeoutRunnable;
    
    public ForcedResizableInfoActivityController(final Context mContext, final Divider divider) {
        this.mHandler = new Handler();
        this.mPendingTasks = (ArraySet<PendingTaskRecord>)new ArraySet();
        this.mPackagesShownInSession = (ArraySet<String>)new ArraySet();
        this.mTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                ForcedResizableInfoActivityController.this.showPending();
            }
        };
        this.mDockedStackExistsListener = (Consumer<Boolean>)new _$$Lambda$ForcedResizableInfoActivityController$54i4Imkkt5mUAQHgG0VrHpWhZ10(this);
        this.mContext = mContext;
        ActivityManagerWrapper.getInstance().registerTaskStackListener(new TaskStackChangeListener() {
            @Override
            public void onActivityDismissingDockedStack() {
                ForcedResizableInfoActivityController.this.activityDismissingDockedStack();
            }
            
            @Override
            public void onActivityForcedResizable(final String s, final int n, final int n2) {
                ForcedResizableInfoActivityController.this.activityForcedResizable(s, n, n2);
            }
            
            @Override
            public void onActivityLaunchOnSecondaryDisplayFailed() {
                ForcedResizableInfoActivityController.this.activityLaunchOnSecondaryDisplayFailed();
            }
        });
        divider.registerInSplitScreenListener(this.mDockedStackExistsListener);
    }
    
    private void activityDismissingDockedStack() {
        Toast.makeText(this.mContext, R$string.dock_non_resizeble_failed_to_dock_text, 0).show();
    }
    
    private void activityForcedResizable(final String s, final int n, final int n2) {
        if (this.debounce(s)) {
            return;
        }
        this.mPendingTasks.add((Object)new PendingTaskRecord(n, n2));
        this.postTimeout();
    }
    
    private void activityLaunchOnSecondaryDisplayFailed() {
        Toast.makeText(this.mContext, R$string.activity_launch_on_secondary_display_failed_text, 0).show();
    }
    
    private boolean debounce(final String anObject) {
        if (anObject == null) {
            return false;
        }
        if ("com.android.systemui".equals(anObject)) {
            return true;
        }
        final boolean contains = this.mPackagesShownInSession.contains((Object)anObject);
        this.mPackagesShownInSession.add((Object)anObject);
        return contains;
    }
    
    private void postTimeout() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        this.mHandler.postDelayed(this.mTimeoutRunnable, 1000L);
    }
    
    private void showPending() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        for (int i = this.mPendingTasks.size() - 1; i >= 0; --i) {
            final PendingTaskRecord pendingTaskRecord = (PendingTaskRecord)this.mPendingTasks.valueAt(i);
            final Intent intent = new Intent(this.mContext, (Class)ForcedResizableInfoActivity.class);
            final ActivityOptions basic = ActivityOptions.makeBasic();
            basic.setLaunchTaskId(pendingTaskRecord.taskId);
            basic.setTaskOverlay(true, true);
            intent.putExtra("extra_forced_resizeable_reason", pendingTaskRecord.reason);
            this.mContext.startActivityAsUser(intent, basic.toBundle(), UserHandle.CURRENT);
        }
        this.mPendingTasks.clear();
    }
    
    public void onAppTransitionFinished() {
        if (!this.mDividerDragging) {
            this.showPending();
        }
    }
    
    void onDraggingEnd() {
        this.mDividerDragging = false;
        this.showPending();
    }
    
    void onDraggingStart() {
        this.mDividerDragging = true;
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    }
    
    private class PendingTaskRecord
    {
        int reason;
        int taskId;
        
        PendingTaskRecord(final ForcedResizableInfoActivityController forcedResizableInfoActivityController, final int taskId, final int reason) {
            this.taskId = taskId;
            this.reason = reason;
        }
    }
}
