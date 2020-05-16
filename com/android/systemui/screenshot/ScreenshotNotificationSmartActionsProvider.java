// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import java.util.Collections;
import android.util.Log;
import android.app.Notification$Action;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import android.content.ComponentName;
import android.graphics.Bitmap;

public class ScreenshotNotificationSmartActionsProvider
{
    public CompletableFuture<List<Notification$Action>> getActions(final String s, final String s2, final Bitmap bitmap, final ComponentName componentName, final boolean b) {
        Log.d("ScreenshotActions", "Returning empty smart action list.");
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
    
    public void notifyAction(final String s, final String s2, final boolean b) {
        Log.d("ScreenshotActions", "Return without notify.");
    }
    
    public void notifyOp(final String s, final ScreenshotOp screenshotOp, final ScreenshotOpStatus screenshotOpStatus, final long n) {
        Log.d("ScreenshotActions", "Return without notify.");
    }
    
    protected enum ScreenshotOp
    {
        OP_UNKNOWN, 
        REQUEST_SMART_ACTIONS, 
        RETRIEVE_SMART_ACTIONS, 
        WAIT_FOR_SMART_ACTIONS;
    }
    
    protected enum ScreenshotOpStatus
    {
        ERROR, 
        OP_STATUS_UNKNOWN, 
        SUCCESS, 
        TIMEOUT;
    }
}
