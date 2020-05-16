// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.os.Handler;
import android.os.AsyncTask;
import com.android.systemui.SystemUIFactory;
import android.content.Context;
import android.app.ActivityManager$RunningTaskInfo;
import android.content.ComponentName;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.concurrent.TimeoutException;
import android.util.Slog;
import java.util.concurrent.TimeUnit;
import android.os.SystemClock;
import android.app.Notification$Action;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScreenshotSmartActions
{
    @VisibleForTesting
    static List<Notification$Action> getSmartActions(final String s, final String s2, final CompletableFuture<List<Notification$Action>> completableFuture, final int n, final ScreenshotNotificationSmartActionsProvider screenshotNotificationSmartActionsProvider) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        final long timeout = n;
        try {
            final List<Notification$Action> list = completableFuture.get(timeout, TimeUnit.MILLISECONDS);
            final long l = SystemClock.uptimeMillis() - uptimeMillis;
            Slog.d("ScreenshotSmartActions", String.format("Got %d smart actions. Wait time: %d ms", list.size(), l));
            notifyScreenshotOp(s, screenshotNotificationSmartActionsProvider, ScreenshotNotificationSmartActionsProvider.ScreenshotOp.WAIT_FOR_SMART_ACTIONS, ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus.SUCCESS, l);
            return list;
        }
        finally {
            final long i = SystemClock.uptimeMillis() - uptimeMillis;
            final Throwable t;
            Slog.e("ScreenshotSmartActions", String.format("Error getting smart actions. Wait time: %d ms", i), t);
            ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus screenshotOpStatus;
            if (t instanceof TimeoutException) {
                screenshotOpStatus = ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus.TIMEOUT;
            }
            else {
                screenshotOpStatus = ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus.ERROR;
            }
            notifyScreenshotOp(s, screenshotNotificationSmartActionsProvider, ScreenshotNotificationSmartActionsProvider.ScreenshotOp.WAIT_FOR_SMART_ACTIONS, screenshotOpStatus, i);
            return Collections.emptyList();
        }
    }
    
    @VisibleForTesting
    static CompletableFuture<List<Notification$Action>> getSmartActionsFuture(String actions, final String s, final Bitmap bitmap, final ScreenshotNotificationSmartActionsProvider screenshotNotificationSmartActionsProvider, final boolean b, final boolean b2) {
        if (!b) {
            Slog.i("ScreenshotSmartActions", "Screenshot Intelligence not enabled, returning empty list.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        if (bitmap.getConfig() != Bitmap$Config.HARDWARE) {
            Slog.w("ScreenshotSmartActions", String.format("Bitmap expected: Hardware, Bitmap found: %s. Returning empty list.", bitmap.getConfig()));
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Screenshot from a managed profile: ");
        sb.append(b2);
        Slog.d("ScreenshotSmartActions", sb.toString());
        final long uptimeMillis = SystemClock.uptimeMillis();
        CompletableFuture<List<Object>> completableFuture = null;
        try {
            final ActivityManager$RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
            ComponentName topActivity;
            if (runningTask != null && runningTask.topActivity != null) {
                topActivity = runningTask.topActivity;
            }
            else {
                topActivity = new ComponentName("", "");
            }
            actions = (String)screenshotNotificationSmartActionsProvider.getActions(actions, s, bitmap, topActivity, b2);
        }
        finally {
            final long uptimeMillis2 = SystemClock.uptimeMillis();
            final CompletableFuture<List<Object>> completedFuture = CompletableFuture.completedFuture(Collections.emptyList());
            final Throwable t;
            Slog.e("ScreenshotSmartActions", "Failed to get future for screenshot notification smart actions.", t);
            notifyScreenshotOp(actions, screenshotNotificationSmartActionsProvider, ScreenshotNotificationSmartActionsProvider.ScreenshotOp.REQUEST_SMART_ACTIONS, ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus.ERROR, uptimeMillis2 - uptimeMillis);
            completableFuture = completedFuture;
        }
        return (CompletableFuture<List<Notification$Action>>)completableFuture;
    }
    
    static void notifyScreenshotAction(final Context context, final String s, final String s2, final boolean b) {
        try {
            SystemUIFactory.getInstance().createScreenshotNotificationSmartActionsProvider(context, AsyncTask.THREAD_POOL_EXECUTOR, new Handler()).notifyAction(s, s2, b);
        }
        finally {
            final Throwable t;
            Slog.e("ScreenshotSmartActions", "Error in notifyScreenshotAction: ", t);
        }
    }
    
    static void notifyScreenshotOp(final String s, final ScreenshotNotificationSmartActionsProvider screenshotNotificationSmartActionsProvider, final ScreenshotNotificationSmartActionsProvider.ScreenshotOp screenshotOp, final ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus screenshotOpStatus, final long n) {
        try {
            screenshotNotificationSmartActionsProvider.notifyOp(s, screenshotOp, screenshotOpStatus, n);
        }
        finally {
            final Throwable t;
            Slog.e("ScreenshotSmartActions", "Error in notifyScreenshotOp: ", t);
        }
    }
}
