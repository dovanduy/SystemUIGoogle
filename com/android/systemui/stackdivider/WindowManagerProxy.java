// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.app.ActivityTaskManager;
import java.util.ArrayList;
import java.util.List;
import android.window.WindowOrganizer;
import android.app.ActivityManager$RunningTaskInfo;
import android.window.WindowContainerTransaction;
import android.window.WindowContainerToken;
import android.window.TaskOrganizer;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManagerGlobal;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.android.internal.annotations.GuardedBy;
import android.graphics.Rect;

public class WindowManagerProxy
{
    private static final int[] HOME_AND_RECENTS;
    private static final WindowManagerProxy sInstance;
    @GuardedBy({ "mDockedRect" })
    private final Rect mDockedRect;
    private final ExecutorService mExecutor;
    private final Runnable mSetTouchableRegionRunnable;
    private final Rect mTmpRect1;
    @GuardedBy({ "mDockedRect" })
    private final Rect mTouchableRegion;
    
    static {
        HOME_AND_RECENTS = new int[] { 2, 3 };
        sInstance = new WindowManagerProxy();
    }
    
    private WindowManagerProxy() {
        this.mDockedRect = new Rect();
        this.mTmpRect1 = new Rect();
        this.mTouchableRegion = new Rect();
        this.mExecutor = Executors.newSingleThreadExecutor();
        this.mSetTouchableRegionRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (WindowManagerProxy.this.mDockedRect) {
                        WindowManagerProxy.this.mTmpRect1.set(WindowManagerProxy.this.mTouchableRegion);
                        // monitorexit(WindowManagerProxy.access$000(this.this$0))
                        WindowManagerGlobal.getWindowManagerService().setDockedStackDividerTouchRegion(WindowManagerProxy.this.mTmpRect1);
                    }
                }
                catch (RemoteException obj) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Failed to set touchable region: ");
                    sb.append(obj);
                    Log.w("WindowManagerProxy", sb.toString());
                }
            }
        };
    }
    
    static void applyDismissSplit(final SplitScreenTaskOrganizer splitScreenTaskOrganizer, final boolean b) {
        TaskOrganizer.setLaunchRoot(0, (WindowContainerToken)null);
        splitScreenTaskOrganizer.mHomeAndRecentsSurfaces.clear();
        final List childTasks = TaskOrganizer.getChildTasks(splitScreenTaskOrganizer.mPrimary.token, (int[])null);
        final List childTasks2 = TaskOrganizer.getChildTasks(splitScreenTaskOrganizer.mSecondary.token, (int[])null);
        final List rootTasks = TaskOrganizer.getRootTasks(0, WindowManagerProxy.HOME_AND_RECENTS);
        if (childTasks.isEmpty() && childTasks2.isEmpty() && rootTasks.isEmpty()) {
            return;
        }
        final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        if (b) {
            for (int i = childTasks.size() - 1; i >= 0; --i) {
                windowContainerTransaction.reparent(childTasks.get(i).token, (WindowContainerToken)null, true);
            }
            for (int j = childTasks2.size() - 1; j >= 0; --j) {
                final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo = childTasks2.get(j);
                windowContainerTransaction.reparent(activityManager$RunningTaskInfo.token, (WindowContainerToken)null, true);
                if (isHomeOrRecentTask(activityManager$RunningTaskInfo)) {
                    windowContainerTransaction.setBounds(activityManager$RunningTaskInfo.token, (Rect)null);
                }
            }
        }
        else {
            for (int k = childTasks2.size() - 1; k >= 0; --k) {
                if (!isHomeOrRecentTask(childTasks2.get(k))) {
                    windowContainerTransaction.reparent(childTasks2.get(k).token, (WindowContainerToken)null, true);
                }
            }
            for (int l = childTasks2.size() - 1; l >= 0; --l) {
                final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo2 = childTasks2.get(l);
                if (isHomeOrRecentTask(activityManager$RunningTaskInfo2)) {
                    windowContainerTransaction.reparent(activityManager$RunningTaskInfo2.token, (WindowContainerToken)null, true);
                    windowContainerTransaction.setBounds(activityManager$RunningTaskInfo2.token, (Rect)null);
                }
            }
            for (int n = childTasks.size() - 1; n >= 0; --n) {
                windowContainerTransaction.reparent(childTasks.get(n).token, (WindowContainerToken)null, true);
            }
        }
        for (int n2 = rootTasks.size() - 1; n2 >= 0; --n2) {
            windowContainerTransaction.setBounds(rootTasks.get(n2).token, (Rect)null);
        }
        windowContainerTransaction.setFocusable(splitScreenTaskOrganizer.mPrimary.token, true);
        WindowOrganizer.applyTransaction(windowContainerTransaction);
    }
    
    static boolean applyEnterSplit(final SplitScreenTaskOrganizer splitScreenTaskOrganizer, final SplitDisplayLayout splitDisplayLayout) {
        TaskOrganizer.setLaunchRoot(0, splitScreenTaskOrganizer.mSecondary.token);
        final List rootTasks = TaskOrganizer.getRootTasks(0, (int[])null);
        final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        if (rootTasks.isEmpty()) {
            return false;
        }
        splitScreenTaskOrganizer.mHomeAndRecentsSurfaces.clear();
        for (int i = rootTasks.size() - 1; i >= 0; --i) {
            final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo = rootTasks.get(i);
            if (isHomeOrRecentTask(activityManager$RunningTaskInfo)) {
                splitScreenTaskOrganizer.mHomeAndRecentsSurfaces.add(activityManager$RunningTaskInfo.token.getLeash());
            }
            if (activityManager$RunningTaskInfo.configuration.windowConfiguration.getWindowingMode() == 1) {
                windowContainerTransaction.reparent(activityManager$RunningTaskInfo.token, splitScreenTaskOrganizer.mSecondary.token, true);
            }
        }
        final boolean applyHomeTasksMinimized = applyHomeTasksMinimized(splitDisplayLayout, null, windowContainerTransaction);
        WindowOrganizer.applyTransaction(windowContainerTransaction);
        return applyHomeTasksMinimized;
    }
    
    static boolean applyHomeTasksMinimized(final SplitDisplayLayout splitDisplayLayout, final WindowContainerToken windowContainerToken, final WindowContainerTransaction windowContainerTransaction) {
        final ArrayList<WindowContainerToken> list = new ArrayList<WindowContainerToken>();
        final boolean homeAndRecentsTasks = getHomeAndRecentsTasks(list, windowContainerToken);
        Rect calcMinimizedHomeStackBounds;
        if (homeAndRecentsTasks) {
            calcMinimizedHomeStackBounds = splitDisplayLayout.calcMinimizedHomeStackBounds();
        }
        else {
            calcMinimizedHomeStackBounds = new Rect(0, 0, splitDisplayLayout.mDisplayLayout.width(), splitDisplayLayout.mDisplayLayout.height());
        }
        for (int i = list.size() - 1; i >= 0; --i) {
            windowContainerTransaction.setBounds((WindowContainerToken)list.get(i), calcMinimizedHomeStackBounds);
        }
        splitDisplayLayout.mTiles.mHomeBounds.set(calcMinimizedHomeStackBounds);
        return homeAndRecentsTasks;
    }
    
    static void applyResizeSplits(final int n, final SplitDisplayLayout splitDisplayLayout) {
        final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        splitDisplayLayout.resizeSplits(n, windowContainerTransaction);
        WindowOrganizer.applyTransaction(windowContainerTransaction);
    }
    
    private static boolean getHomeAndRecentsTasks(final List<WindowContainerToken> list, final WindowContainerToken windowContainerToken) {
        final int[] home_AND_RECENTS = WindowManagerProxy.HOME_AND_RECENTS;
        int i = 0;
        List list2;
        if (windowContainerToken == null) {
            list2 = TaskOrganizer.getRootTasks(0, home_AND_RECENTS);
        }
        else {
            list2 = TaskOrganizer.getChildTasks(windowContainerToken, home_AND_RECENTS);
        }
        final int size = list2.size();
        boolean resizable = false;
        while (i < size) {
            final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo = list2.get(i);
            list.add(activityManager$RunningTaskInfo.token);
            if (activityManager$RunningTaskInfo.topActivityType == 2) {
                resizable = activityManager$RunningTaskInfo.isResizable();
            }
            ++i;
        }
        return resizable;
    }
    
    public static WindowManagerProxy getInstance() {
        return WindowManagerProxy.sInstance;
    }
    
    private static boolean isHomeOrRecentTask(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        final int activityType = activityManager$RunningTaskInfo.configuration.windowConfiguration.getActivityType();
        return activityType == 2 || activityType == 3;
    }
    
    void dismissOrMaximizeDocked(final SplitScreenTaskOrganizer splitScreenTaskOrganizer, final boolean b) {
        this.mExecutor.execute(new _$$Lambda$WindowManagerProxy$dWN4726LPwIh6aPFRkp1dFYT0Ec(splitScreenTaskOrganizer, b));
    }
    
    public void setResizing(final boolean b) {
        this.mExecutor.execute(new Runnable(this) {
            @Override
            public void run() {
                try {
                    ActivityTaskManager.getService().setSplitScreenResizing(b);
                }
                catch (RemoteException obj) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Error calling setDockedStackResizing: ");
                    sb.append(obj);
                    Log.w("WindowManagerProxy", sb.toString());
                }
            }
        });
    }
    
    public void setTouchRegion(final Rect rect) {
        synchronized (this.mDockedRect) {
            this.mTouchableRegion.set(rect);
            // monitorexit(this.mDockedRect)
            this.mExecutor.execute(this.mSetTouchableRegionRunnable);
        }
    }
}
