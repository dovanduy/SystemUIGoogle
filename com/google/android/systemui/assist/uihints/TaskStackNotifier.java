// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.app.ActivityManager$RunningTaskInfo;
import android.content.ComponentName;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import android.app.PendingIntent;

class TaskStackNotifier implements ConfigInfoListener
{
    private PendingIntent mIntent;
    private final TaskStackChangeListener mListener;
    private boolean mListenerRegistered;
    private final ActivityManagerWrapper mWrapper;
    
    TaskStackNotifier() {
        this.mWrapper = ActivityManagerWrapper.getInstance();
        this.mListenerRegistered = false;
        this.mListener = new TaskStackChangeListener() {
            @Override
            public void onTaskCreated(final int n, final ComponentName componentName) {
                TaskStackNotifier.this.sendIntent();
            }
            
            @Override
            public void onTaskMovedToFront(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
                TaskStackNotifier.this.sendIntent();
            }
        };
    }
    
    private void sendIntent() {
        final PendingIntent mIntent = this.mIntent;
        if (mIntent == null) {
            return;
        }
        try {
            mIntent.send();
        }
        catch (PendingIntent$CanceledException ex) {
            Log.e("TaskStackNotifier", "could not send intent", (Throwable)ex);
        }
    }
    
    @Override
    public void onConfigInfo(final ConfigInfo configInfo) {
        final PendingIntent onTaskChange = configInfo.onTaskChange;
        this.mIntent = onTaskChange;
        if (onTaskChange != null && !this.mListenerRegistered) {
            this.mWrapper.registerTaskStackListener(this.mListener);
            this.mListenerRegistered = true;
        }
        else if (this.mIntent == null && this.mListenerRegistered) {
            this.mWrapper.unregisterTaskStackListener(this.mListener);
            this.mListenerRegistered = false;
        }
    }
}
