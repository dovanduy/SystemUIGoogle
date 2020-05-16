// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import android.app.ITaskStackListener;
import android.app.ActivityManager$RunningTaskInfo;
import android.content.pm.UserInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.RemoteException;
import android.util.Log;
import android.app.ActivityManager$RunningAppProcessInfo;
import com.android.systemui.R$string;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.app.TaskStackListener;
import android.content.pm.PackageManager;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;
import com.google.android.systemui.elmyra.actions.CameraAction;
import android.app.IActivityManager;

public class CameraVisibility extends Gate
{
    private final IActivityManager mActivityManager;
    private final CameraAction mCameraAction;
    private final String mCameraPackageName;
    private boolean mCameraShowing;
    private final List<Action> mExceptions;
    private final Listener mGateListener;
    private final KeyguardVisibility mKeyguardGate;
    private final PackageManager mPackageManager;
    private final PowerState mPowerState;
    private final TaskStackListener mTaskStackListener;
    private final Handler mUpdateHandler;
    
    public CameraVisibility(final Context context, final CameraAction mCameraAction, final List<Action> mExceptions) {
        super(context);
        this.mTaskStackListener = new TaskStackListener() {
            public void onTaskStackChanged() {
                CameraVisibility.this.mUpdateHandler.post((Runnable)new _$$Lambda$CameraVisibility$1$X_K32nTSgqALN1DA7GlsqyIM0Ns(CameraVisibility.this));
            }
        };
        this.mGateListener = new Listener() {
            @Override
            public void onGateChanged(final Gate gate) {
                CameraVisibility.this.mUpdateHandler.post((Runnable)new _$$Lambda$CameraVisibility$2$B_qu82ozOy_obNvcYz2PEaBQIyk(CameraVisibility.this));
            }
        };
        this.mCameraAction = mCameraAction;
        this.mExceptions = mExceptions;
        this.mPackageManager = context.getPackageManager();
        final ActivityManager activityManager = (ActivityManager)context.getSystemService("activity");
        this.mActivityManager = ActivityManager.getService();
        this.mKeyguardGate = new KeyguardVisibility(context);
        this.mPowerState = new PowerState(context);
        this.mKeyguardGate.setListener(this.mGateListener);
        this.mPowerState.setListener(this.mGateListener);
        this.mCameraPackageName = context.getResources().getString(R$string.google_camera_app_package_name);
        this.mUpdateHandler = new Handler(context.getMainLooper());
    }
    
    private boolean isCameraInForeground() {
        boolean b = false;
        try {
            final UserInfo currentUser = this.mActivityManager.getCurrentUser();
            final PackageManager mPackageManager = this.mPackageManager;
            final String mCameraPackageName = this.mCameraPackageName;
            int id;
            if (currentUser != null) {
                id = currentUser.id;
            }
            else {
                id = 0;
            }
            final int uid = mPackageManager.getApplicationInfoAsUser(mCameraPackageName, 0, id).uid;
            final List runningAppProcesses = this.mActivityManager.getRunningAppProcesses();
            ActivityManager$RunningAppProcessInfo activityManager$RunningAppProcessInfo = null;
            Block_6: {
                for (int i = 0; i < runningAppProcesses.size(); ++i) {
                    activityManager$RunningAppProcessInfo = runningAppProcesses.get(i);
                    if (activityManager$RunningAppProcessInfo.uid == uid && activityManager$RunningAppProcessInfo.processName.equalsIgnoreCase(this.mCameraPackageName)) {
                        break Block_6;
                    }
                }
                goto Label_0145;
            }
            if (activityManager$RunningAppProcessInfo.importance == 100) {
                b = true;
            }
            return b;
        }
        catch (RemoteException ex) {
            Log.e("Elmyra/CameraVisibility", "Could not check camera foreground status", (Throwable)ex);
        }
        catch (PackageManager$NameNotFoundException ex2) {
            goto Label_0145;
        }
    }
    
    private boolean isCameraTopActivity() {
        try {
            final List tasks = ActivityManager.getService().getTasks(1);
            return !tasks.isEmpty() && tasks.get(0).topActivity.getPackageName().equalsIgnoreCase(this.mCameraPackageName);
        }
        catch (RemoteException ex) {
            Log.e("Elmyra/CameraVisibility", "unable to check task stack", (Throwable)ex);
            return false;
        }
    }
    
    private void updateCameraIsShowing() {
        final boolean cameraShowing = this.isCameraShowing();
        if (this.mCameraShowing != cameraShowing) {
            this.mCameraShowing = cameraShowing;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        final boolean b = false;
        for (int i = 0; i < this.mExceptions.size(); ++i) {
            if (this.mExceptions.get(i).isAvailable()) {
                return false;
            }
        }
        boolean b2 = b;
        if (this.mCameraShowing) {
            b2 = b;
            if (!this.mCameraAction.isAvailable()) {
                b2 = true;
            }
        }
        return b2;
    }
    
    public boolean isCameraShowing() {
        return this.isCameraTopActivity() && this.isCameraInForeground() && !this.mPowerState.isBlocking();
    }
    
    @Override
    protected void onActivate() {
        this.mKeyguardGate.activate();
        this.mPowerState.activate();
        this.mCameraShowing = this.isCameraShowing();
        try {
            this.mActivityManager.registerTaskStackListener((ITaskStackListener)this.mTaskStackListener);
        }
        catch (RemoteException ex) {
            Log.e("Elmyra/CameraVisibility", "Could not register task stack listener", (Throwable)ex);
        }
    }
    
    @Override
    protected void onDeactivate() {
        this.mKeyguardGate.deactivate();
        this.mPowerState.deactivate();
        try {
            this.mActivityManager.unregisterTaskStackListener((ITaskStackListener)this.mTaskStackListener);
        }
        catch (RemoteException ex) {
            Log.e("Elmyra/CameraVisibility", "Could not unregister task stack listener", (Throwable)ex);
        }
    }
}
