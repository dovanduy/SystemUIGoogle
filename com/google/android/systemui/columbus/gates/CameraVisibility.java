// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.app.ITaskStackListener;
import android.content.ComponentName;
import android.app.ActivityManager$RunningTaskInfo;
import java.util.Iterator;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.RemoteException;
import android.util.Log;
import kotlin.text.StringsKt;
import android.app.ActivityManager$RunningAppProcessInfo;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.os.Handler;
import android.content.pm.PackageManager;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import android.app.IActivityManager;

public final class CameraVisibility extends Gate
{
    private final IActivityManager activityManager;
    private boolean cameraShowing;
    private final List<Action> exceptions;
    private final CameraVisibility$gateListener.CameraVisibility$gateListener$1 gateListener;
    private final KeyguardVisibility keyguardGate;
    private final PackageManager packageManager;
    private final PowerState powerState;
    private final CameraVisibility$taskStackListener.CameraVisibility$taskStackListener$1 taskStackListener;
    private final Handler updateHandler;
    
    public CameraVisibility(final Context context, final List<Action> exceptions, final KeyguardVisibility keyguardGate, final PowerState powerState, final IActivityManager activityManager, final Handler updateHandler) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(exceptions, "exceptions");
        Intrinsics.checkParameterIsNotNull(keyguardGate, "keyguardGate");
        Intrinsics.checkParameterIsNotNull(powerState, "powerState");
        Intrinsics.checkParameterIsNotNull(activityManager, "activityManager");
        Intrinsics.checkParameterIsNotNull(updateHandler, "updateHandler");
        super(context);
        this.exceptions = exceptions;
        this.keyguardGate = keyguardGate;
        this.powerState = powerState;
        this.activityManager = activityManager;
        this.updateHandler = updateHandler;
        this.packageManager = context.getPackageManager();
        this.taskStackListener = new CameraVisibility$taskStackListener.CameraVisibility$taskStackListener$1(this);
        final CameraVisibility$gateListener.CameraVisibility$gateListener$1 cameraVisibility$gateListener$1 = new CameraVisibility$gateListener.CameraVisibility$gateListener$1(this);
        this.gateListener = cameraVisibility$gateListener$1;
        this.keyguardGate.setListener((Listener)cameraVisibility$gateListener$1);
        this.powerState.setListener((Listener)this.gateListener);
    }
    
    private final boolean isCameraInForeground() {
        try {
            final int uid = this.packageManager.getApplicationInfoAsUser("com.google.android.GoogleCamera", 0, this.activityManager.getCurrentUser().id).uid;
            final List runningAppProcesses = this.activityManager.getRunningAppProcesses();
            Intrinsics.checkExpressionValueIsNotNull(runningAppProcesses, "activityManager.runningAppProcesses");
            while (true) {
                for (final ActivityManager$RunningAppProcessInfo next : runningAppProcesses) {
                    final ActivityManager$RunningAppProcessInfo activityManager$RunningAppProcessInfo = next;
                    if (activityManager$RunningAppProcessInfo.uid == uid && StringsKt.equals(activityManager$RunningAppProcessInfo.processName, "com.google.android.GoogleCamera", true)) {
                        final ActivityManager$RunningAppProcessInfo activityManager$RunningAppProcessInfo2 = next;
                        goto Label_0151;
                    }
                }
                ActivityManager$RunningAppProcessInfo next = null;
                continue;
            }
        }
        catch (RemoteException ex) {
            Log.e("Columbus/CameraVisibility", "Could not check camera foreground status", (Throwable)ex);
        }
        catch (PackageManager$NameNotFoundException ex2) {
            goto Label_0151;
        }
    }
    
    private final boolean isCameraTopActivity() {
        try {
            final List tasks = this.activityManager.getTasks(1);
            if (tasks.isEmpty()) {
                return false;
            }
            final ComponentName topActivity = tasks.get(0).topActivity;
            Intrinsics.checkExpressionValueIsNotNull(topActivity, "topActivityComponent");
            return StringsKt.equals(topActivity.getPackageName(), "com.google.android.GoogleCamera", true);
        }
        catch (RemoteException ex) {
            Log.e("Columbus/CameraVisibility", "unable to check task stack", (Throwable)ex);
            return false;
        }
    }
    
    private final void updateCameraIsShowing() {
        final boolean cameraShowing = this.isCameraShowing();
        if (this.cameraShowing != cameraShowing) {
            this.cameraShowing = cameraShowing;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        for (final Action next : this.exceptions) {
            if (next.isAvailable()) {
                return next == null && this.cameraShowing;
            }
        }
        Action next = null;
        return next == null && this.cameraShowing;
    }
    
    public final boolean isCameraShowing() {
        return this.isCameraTopActivity() && this.isCameraInForeground() && !this.powerState.isBlocking();
    }
    
    @Override
    protected void onActivate() {
        this.keyguardGate.activate();
        this.powerState.activate();
        this.cameraShowing = this.isCameraShowing();
        try {
            this.activityManager.registerTaskStackListener((ITaskStackListener)this.taskStackListener);
        }
        catch (RemoteException ex) {
            Log.e("Columbus/CameraVisibility", "Could not register task stack listener", (Throwable)ex);
        }
    }
    
    @Override
    protected void onDeactivate() {
        this.keyguardGate.deactivate();
        this.powerState.deactivate();
        try {
            this.activityManager.unregisterTaskStackListener((ITaskStackListener)this.taskStackListener);
        }
        catch (RemoteException ex) {
            Log.e("Columbus/CameraVisibility", "Could not unregister task stack listener", (Throwable)ex);
        }
    }
}
