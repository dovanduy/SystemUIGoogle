// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import com.android.systemui.shared.recents.model.ThumbnailData;
import android.content.ComponentName;
import android.os.IBinder;
import android.app.ActivityManager$RunningTaskInfo;

public abstract class TaskStackChangeListener
{
    public void onActivityDismissingDockedStack() {
    }
    
    public void onActivityForcedResizable(final String s, final int n, final int n2) {
    }
    
    public void onActivityLaunchOnSecondaryDisplayFailed() {
    }
    
    public void onActivityLaunchOnSecondaryDisplayFailed(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        this.onActivityLaunchOnSecondaryDisplayFailed();
    }
    
    public void onActivityLaunchOnSecondaryDisplayRerouted() {
    }
    
    public void onActivityLaunchOnSecondaryDisplayRerouted(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        this.onActivityLaunchOnSecondaryDisplayRerouted();
    }
    
    public void onActivityPinned(final String s, final int n, final int n2, final int n3) {
    }
    
    public void onActivityRequestedOrientationChanged(final int n, final int n2) {
    }
    
    public void onActivityRestartAttempt(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo, final boolean b, final boolean b2) {
    }
    
    public void onActivityUnpinned() {
    }
    
    public void onBackPressedOnTaskRoot(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
    }
    
    public void onRecentTaskListFrozenChanged(final boolean b) {
    }
    
    public void onRecentTaskListUpdated() {
    }
    
    public void onSingleTaskDisplayDrawn(final int n) {
    }
    
    public void onSingleTaskDisplayEmpty(final int n) {
    }
    
    public void onSizeCompatModeActivityChanged(final int n, final IBinder binder) {
    }
    
    public void onTaskCreated(final int n, final ComponentName componentName) {
    }
    
    public void onTaskDescriptionChanged(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
    }
    
    public void onTaskDisplayChanged(final int n, final int n2) {
    }
    
    public void onTaskMovedToFront(final int n) {
    }
    
    public void onTaskMovedToFront(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        this.onTaskMovedToFront(activityManager$RunningTaskInfo.taskId);
    }
    
    public void onTaskProfileLocked(final int n, final int n2) {
    }
    
    public void onTaskRemoved(final int n) {
    }
    
    public void onTaskSnapshotChanged(final int n, final ThumbnailData thumbnailData) {
    }
    
    public void onTaskStackChanged() {
    }
    
    public void onTaskStackChangedBackground() {
    }
}
