// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import com.android.systemui.shared.recents.model.ThumbnailData;
import android.os.Trace;
import android.os.Message;
import java.util.Collection;
import android.app.ActivityManager$TaskSnapshot;
import android.content.ComponentName;
import android.os.IBinder;
import com.android.internal.os.SomeArgs;
import android.app.ActivityManager$RunningTaskInfo;
import android.os.RemoteException;
import android.util.Log;
import android.app.ITaskStackListener;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import java.util.ArrayList;
import android.os.Looper;
import java.util.List;
import android.os.Handler;
import android.app.TaskStackListener;

public class TaskStackChangeListeners extends TaskStackListener
{
    private static final String TAG;
    private final Handler mHandler;
    private boolean mRegistered;
    private final List<TaskStackChangeListener> mTaskStackListeners;
    private final List<TaskStackChangeListener> mTmpListeners;
    
    static {
        TAG = TaskStackChangeListeners.class.getSimpleName();
    }
    
    public TaskStackChangeListeners(final Looper looper) {
        this.mTaskStackListeners = new ArrayList<TaskStackChangeListener>();
        this.mTmpListeners = new ArrayList<TaskStackChangeListener>();
        this.mHandler = new H(looper);
    }
    
    public void addListener(IActivityManager mTaskStackListeners, final TaskStackChangeListener taskStackChangeListener) {
        mTaskStackListeners = (Exception)this.mTaskStackListeners;
        synchronized (mTaskStackListeners) {
            this.mTaskStackListeners.add(taskStackChangeListener);
            // monitorexit(mTaskStackListeners)
            if (!this.mRegistered) {
                try {
                    ActivityTaskManager.getService().registerTaskStackListener((ITaskStackListener)this);
                    this.mRegistered = true;
                }
                catch (Exception mTaskStackListeners) {
                    Log.w(TaskStackChangeListeners.TAG, "Failed to call registerTaskStackListener", (Throwable)mTaskStackListeners);
                }
            }
        }
    }
    
    public void onActivityDismissingDockedStack() throws RemoteException {
        this.mHandler.sendEmptyMessage(7);
    }
    
    public void onActivityForcedResizable(final String s, final int n, final int n2) throws RemoteException {
        this.mHandler.obtainMessage(6, n, n2, (Object)s).sendToTarget();
    }
    
    public void onActivityLaunchOnSecondaryDisplayFailed(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo, final int n) throws RemoteException {
        this.mHandler.obtainMessage(11, n, 0, (Object)activityManager$RunningTaskInfo).sendToTarget();
    }
    
    public void onActivityLaunchOnSecondaryDisplayRerouted(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo, final int n) throws RemoteException {
        this.mHandler.obtainMessage(16, n, 0, (Object)activityManager$RunningTaskInfo).sendToTarget();
    }
    
    public void onActivityPinned(final String s, final int n, final int n2, final int n3) throws RemoteException {
        this.mHandler.removeMessages(3);
        this.mHandler.obtainMessage(3, (Object)new PinnedActivityInfo(s, n, n2, n3)).sendToTarget();
    }
    
    public void onActivityRequestedOrientationChanged(final int n, final int n2) throws RemoteException {
        this.mHandler.obtainMessage(15, n, n2).sendToTarget();
    }
    
    public void onActivityRestartAttempt(final ActivityManager$RunningTaskInfo arg1, final boolean argi1, final boolean argi2) throws RemoteException {
        final SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = arg1;
        obtain.argi1 = (argi1 ? 1 : 0);
        obtain.argi2 = (argi2 ? 1 : 0);
        this.mHandler.removeMessages(4);
        this.mHandler.obtainMessage(4, (Object)obtain).sendToTarget();
    }
    
    public void onActivityUnpinned() throws RemoteException {
        this.mHandler.removeMessages(10);
        this.mHandler.sendEmptyMessage(10);
    }
    
    public void onBackPressedOnTaskRoot(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) throws RemoteException {
        this.mHandler.obtainMessage(18, (Object)activityManager$RunningTaskInfo).sendToTarget();
    }
    
    public void onRecentTaskListFrozenChanged(final boolean b) {
        this.mHandler.obtainMessage(23, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    public void onRecentTaskListUpdated() throws RemoteException {
        this.mHandler.obtainMessage(21).sendToTarget();
    }
    
    public void onSingleTaskDisplayDrawn(final int n) throws RemoteException {
        this.mHandler.obtainMessage(19, n, 0).sendToTarget();
    }
    
    public void onSingleTaskDisplayEmpty(final int n) throws RemoteException {
        this.mHandler.obtainMessage(22, n, 0).sendToTarget();
    }
    
    public void onSizeCompatModeActivityChanged(final int n, final IBinder binder) throws RemoteException {
        this.mHandler.obtainMessage(17, n, 0, (Object)binder).sendToTarget();
    }
    
    public void onTaskCreated(final int n, final ComponentName componentName) throws RemoteException {
        this.mHandler.obtainMessage(12, n, 0, (Object)componentName).sendToTarget();
    }
    
    public void onTaskDescriptionChanged(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        this.mHandler.obtainMessage(24, (Object)activityManager$RunningTaskInfo).sendToTarget();
    }
    
    public void onTaskDisplayChanged(final int n, final int n2) throws RemoteException {
        this.mHandler.obtainMessage(20, n, n2).sendToTarget();
    }
    
    public void onTaskMovedToFront(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) throws RemoteException {
        this.mHandler.obtainMessage(14, (Object)activityManager$RunningTaskInfo).sendToTarget();
    }
    
    public void onTaskProfileLocked(final int n, final int n2) throws RemoteException {
        this.mHandler.obtainMessage(8, n, n2).sendToTarget();
    }
    
    public void onTaskRemoved(final int n) throws RemoteException {
        this.mHandler.obtainMessage(13, n, 0).sendToTarget();
    }
    
    public void onTaskSnapshotChanged(final int n, final ActivityManager$TaskSnapshot activityManager$TaskSnapshot) throws RemoteException {
        this.mHandler.obtainMessage(2, n, 0, (Object)activityManager$TaskSnapshot).sendToTarget();
    }
    
    public void onTaskStackChanged() throws RemoteException {
        synchronized (this.mTaskStackListeners) {
            this.mTmpListeners.addAll(this.mTaskStackListeners);
            // monitorexit(this.mTaskStackListeners)
            for (int i = this.mTmpListeners.size() - 1; i >= 0; --i) {
                this.mTmpListeners.get(i).onTaskStackChangedBackground();
            }
            this.mTmpListeners.clear();
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessage(1);
        }
    }
    
    public void removeListener(final TaskStackChangeListener taskStackChangeListener) {
        synchronized (this.mTaskStackListeners) {
            this.mTaskStackListeners.remove(taskStackChangeListener);
            final boolean empty = this.mTaskStackListeners.isEmpty();
            // monitorexit(this.mTaskStackListeners)
            if (empty && this.mRegistered) {
                try {
                    ActivityTaskManager.getService().unregisterTaskStackListener((ITaskStackListener)this);
                    this.mRegistered = false;
                }
                catch (Exception ex) {
                    Log.w(TaskStackChangeListeners.TAG, "Failed to call unregisterTaskStackListener", (Throwable)ex);
                }
            }
        }
    }
    
    private final class H extends Handler
    {
        public H(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            synchronized (TaskStackChangeListeners.this.mTaskStackListeners) {
                final int what = message.what;
                boolean b = false;
                switch (what) {
                    case 24: {
                        final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo = (ActivityManager$RunningTaskInfo)message.obj;
                        for (int i = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; i >= 0; --i) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(i)).onTaskDescriptionChanged(activityManager$RunningTaskInfo);
                        }
                        break;
                    }
                    case 23: {
                        for (int j = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; j >= 0; --j) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(j)).onRecentTaskListFrozenChanged(message.arg1 != 0);
                        }
                        break;
                    }
                    case 22: {
                        for (int k = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; k >= 0; --k) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(k)).onSingleTaskDisplayEmpty(message.arg1);
                        }
                        break;
                    }
                    case 21: {
                        for (int l = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; l >= 0; --l) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(l)).onRecentTaskListUpdated();
                        }
                        break;
                    }
                    case 20: {
                        for (int n = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n >= 0; --n) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n)).onTaskDisplayChanged(message.arg1, message.arg2);
                        }
                        break;
                    }
                    case 19: {
                        for (int n2 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n2 >= 0; --n2) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n2)).onSingleTaskDisplayDrawn(message.arg1);
                        }
                        break;
                    }
                    case 18: {
                        for (int n3 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n3 >= 0; --n3) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n3)).onBackPressedOnTaskRoot((ActivityManager$RunningTaskInfo)message.obj);
                        }
                        break;
                    }
                    case 17: {
                        for (int n4 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n4 >= 0; --n4) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n4)).onSizeCompatModeActivityChanged(message.arg1, (IBinder)message.obj);
                        }
                        break;
                    }
                    case 16: {
                        final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo2 = (ActivityManager$RunningTaskInfo)message.obj;
                        for (int n5 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n5 >= 0; --n5) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n5)).onActivityLaunchOnSecondaryDisplayRerouted(activityManager$RunningTaskInfo2);
                        }
                        break;
                    }
                    case 15: {
                        for (int n6 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n6 >= 0; --n6) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n6)).onActivityRequestedOrientationChanged(message.arg1, message.arg2);
                        }
                        break;
                    }
                    case 14: {
                        final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo3 = (ActivityManager$RunningTaskInfo)message.obj;
                        for (int n7 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n7 >= 0; --n7) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n7)).onTaskMovedToFront(activityManager$RunningTaskInfo3);
                        }
                        break;
                    }
                    case 13: {
                        for (int n8 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n8 >= 0; --n8) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n8)).onTaskRemoved(message.arg1);
                        }
                        break;
                    }
                    case 12: {
                        for (int n9 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n9 >= 0; --n9) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n9)).onTaskCreated(message.arg1, (ComponentName)message.obj);
                        }
                        break;
                    }
                    case 11: {
                        final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo4 = (ActivityManager$RunningTaskInfo)message.obj;
                        for (int n10 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n10 >= 0; --n10) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n10)).onActivityLaunchOnSecondaryDisplayFailed(activityManager$RunningTaskInfo4);
                        }
                        break;
                    }
                    case 10: {
                        for (int n11 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n11 >= 0; --n11) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n11)).onActivityUnpinned();
                        }
                        break;
                    }
                    case 8: {
                        for (int n12 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n12 >= 0; --n12) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n12)).onTaskProfileLocked(message.arg1, message.arg2);
                        }
                        break;
                    }
                    case 7: {
                        for (int n13 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n13 >= 0; --n13) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n13)).onActivityDismissingDockedStack();
                        }
                        break;
                    }
                    case 6: {
                        for (int n14 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n14 >= 0; --n14) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n14)).onActivityForcedResizable((String)message.obj, message.arg1, message.arg2);
                        }
                        break;
                    }
                    case 4: {
                        final SomeArgs someArgs = (SomeArgs)message.obj;
                        final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo5 = (ActivityManager$RunningTaskInfo)someArgs.arg1;
                        final boolean b2 = someArgs.argi1 != 0;
                        if (someArgs.argi2 != 0) {
                            b = true;
                        }
                        for (int n15 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n15 >= 0; --n15) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n15)).onActivityRestartAttempt(activityManager$RunningTaskInfo5, b2, b);
                        }
                        break;
                    }
                    case 3: {
                        final PinnedActivityInfo pinnedActivityInfo = (PinnedActivityInfo)message.obj;
                        for (int n16 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n16 >= 0; --n16) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n16)).onActivityPinned(pinnedActivityInfo.mPackageName, pinnedActivityInfo.mUserId, pinnedActivityInfo.mTaskId, pinnedActivityInfo.mStackId);
                        }
                        break;
                    }
                    case 2: {
                        Trace.beginSection("onTaskSnapshotChanged");
                        for (int n17 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n17 >= 0; --n17) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n17)).onTaskSnapshotChanged(message.arg1, new ThumbnailData((ActivityManager$TaskSnapshot)message.obj));
                        }
                        Trace.endSection();
                        break;
                    }
                    case 1: {
                        Trace.beginSection("onTaskStackChanged");
                        for (int n18 = TaskStackChangeListeners.this.mTaskStackListeners.size() - 1; n18 >= 0; --n18) {
                            ((TaskStackChangeListener)TaskStackChangeListeners.this.mTaskStackListeners.get(n18)).onTaskStackChanged();
                        }
                        Trace.endSection();
                        break;
                    }
                }
                // monitorexit(TaskStackChangeListeners.access$000(this.this$0))
                final Object obj = message.obj;
                if (obj instanceof SomeArgs) {
                    ((SomeArgs)obj).recycle();
                }
            }
        }
    }
    
    private static class PinnedActivityInfo
    {
        final String mPackageName;
        final int mStackId;
        final int mTaskId;
        final int mUserId;
        
        PinnedActivityInfo(final String mPackageName, final int mUserId, final int mTaskId, final int mStackId) {
            this.mPackageName = mPackageName;
            this.mUserId = mUserId;
            this.mTaskId = mTaskId;
            this.mStackId = mStackId;
        }
    }
}
