// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.SurfaceView;
import android.content.Intent;
import android.view.View;
import android.view.SurfaceHolder;
import android.view.SurfaceControlViewHost$SurfacePackage;
import android.os.UserHandle;
import android.app.admin.IKeyguardCallback$Stub;
import android.os.RemoteException;
import android.util.Log;
import android.app.admin.IKeyguardClient$Stub;
import android.os.IBinder;
import android.content.ComponentName;
import com.android.internal.annotations.VisibleForTesting;
import android.view.SurfaceHolder$Callback;
import android.view.ViewGroup;
import android.os.IBinder$DeathRecipient;
import android.os.Handler;
import android.content.Context;
import android.content.ServiceConnection;
import android.app.admin.IKeyguardClient;
import android.app.admin.IKeyguardCallback;

public class AdminSecondaryLockScreenController
{
    private final IKeyguardCallback mCallback;
    private IKeyguardClient mClient;
    private final ServiceConnection mConnection;
    private final Context mContext;
    private Handler mHandler;
    private KeyguardSecurityCallback mKeyguardCallback;
    private final IBinder$DeathRecipient mKeyguardClientDeathRecipient;
    private final ViewGroup mParent;
    @VisibleForTesting
    protected SurfaceHolder$Callback mSurfaceHolderCallback;
    private final KeyguardUpdateMonitorCallback mUpdateCallback;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private AdminSecurityView mView;
    
    public AdminSecondaryLockScreenController(final Context mContext, final ViewGroup mParent, final KeyguardUpdateMonitor mUpdateMonitor, final KeyguardSecurityCallback mKeyguardCallback, final Handler mHandler) {
        this.mConnection = (ServiceConnection)new ServiceConnection() {
            public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                AdminSecondaryLockScreenController.this.mClient = IKeyguardClient$Stub.asInterface(binder);
                if (AdminSecondaryLockScreenController.this.mView.isAttachedToWindow() && AdminSecondaryLockScreenController.this.mClient != null) {
                    AdminSecondaryLockScreenController.this.onSurfaceReady();
                    try {
                        binder.linkToDeath(AdminSecondaryLockScreenController.this.mKeyguardClientDeathRecipient, 0);
                    }
                    catch (RemoteException ex) {
                        Log.e("AdminSecondaryLockScreenController", "Lost connection to secondary lockscreen service", (Throwable)ex);
                        AdminSecondaryLockScreenController.this.dismiss(KeyguardUpdateMonitor.getCurrentUser());
                    }
                }
            }
            
            public void onServiceDisconnected(final ComponentName componentName) {
                AdminSecondaryLockScreenController.this.mClient = null;
            }
        };
        this.mKeyguardClientDeathRecipient = (IBinder$DeathRecipient)new _$$Lambda$AdminSecondaryLockScreenController$hZxS1P2BiTbPLgxMgeMNzfQP6sE(this);
        this.mCallback = (IKeyguardCallback)new IKeyguardCallback$Stub() {
            public void onDismiss() {
                AdminSecondaryLockScreenController.this.dismiss(UserHandle.getCallingUserId());
            }
            
            public void onRemoteContentReady(final SurfaceControlViewHost$SurfacePackage childSurfacePackage) {
                if (AdminSecondaryLockScreenController.this.mHandler != null) {
                    AdminSecondaryLockScreenController.this.mHandler.removeCallbacksAndMessages((Object)null);
                }
                if (childSurfacePackage != null) {
                    AdminSecondaryLockScreenController.this.mView.setChildSurfacePackage(childSurfacePackage);
                }
                else {
                    AdminSecondaryLockScreenController.this.dismiss(KeyguardUpdateMonitor.getCurrentUser());
                }
            }
        };
        this.mUpdateCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onSecondaryLockscreenRequirementChanged(final int n) {
                if (AdminSecondaryLockScreenController.this.mUpdateMonitor.getSecondaryLockscreenRequirement(n) == null) {
                    AdminSecondaryLockScreenController.this.dismiss(n);
                }
            }
        };
        this.mSurfaceHolderCallback = (SurfaceHolder$Callback)new SurfaceHolder$Callback() {
            public void surfaceChanged(final SurfaceHolder surfaceHolder, final int n, final int n2, final int n3) {
            }
            
            public void surfaceCreated(final SurfaceHolder surfaceHolder) {
                final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                AdminSecondaryLockScreenController.this.mUpdateMonitor.registerCallback(AdminSecondaryLockScreenController.this.mUpdateCallback);
                if (AdminSecondaryLockScreenController.this.mClient != null) {
                    AdminSecondaryLockScreenController.this.onSurfaceReady();
                }
                AdminSecondaryLockScreenController.this.mHandler.postDelayed((Runnable)new _$$Lambda$AdminSecondaryLockScreenController$4$_S47yCokzqIXXVhoyS6AoyEb9Xw(this, currentUser), 500L);
            }
            
            public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
                AdminSecondaryLockScreenController.this.mUpdateMonitor.removeCallback(AdminSecondaryLockScreenController.this.mUpdateCallback);
            }
        };
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mParent = mParent;
        this.mUpdateMonitor = mUpdateMonitor;
        this.mKeyguardCallback = mKeyguardCallback;
        this.mView = new AdminSecurityView(this.mContext, this.mSurfaceHolderCallback);
    }
    
    private void dismiss(final int n) {
        this.mHandler.removeCallbacksAndMessages((Object)null);
        final AdminSecurityView mView = this.mView;
        if (mView != null && mView.isAttachedToWindow() && n == KeyguardUpdateMonitor.getCurrentUser()) {
            this.hide();
            this.mKeyguardCallback.dismiss(true, n);
        }
    }
    
    private void onSurfaceReady() {
        try {
            final IBinder hostToken = this.mView.getHostToken();
            if (hostToken != null) {
                this.mClient.onCreateKeyguardSurface(hostToken, this.mCallback);
            }
            else {
                this.hide();
            }
        }
        catch (RemoteException ex) {
            Log.e("AdminSecondaryLockScreenController", "Error in onCreateKeyguardSurface", (Throwable)ex);
            this.dismiss(KeyguardUpdateMonitor.getCurrentUser());
        }
    }
    
    public void hide() {
        if (this.mView.isAttachedToWindow()) {
            this.mParent.removeView((View)this.mView);
        }
        final IKeyguardClient mClient = this.mClient;
        if (mClient != null) {
            mClient.asBinder().unlinkToDeath(this.mKeyguardClientDeathRecipient, 0);
            this.mContext.unbindService(this.mConnection);
            this.mClient = null;
        }
    }
    
    public void show(final Intent intent) {
        this.mContext.bindService(intent, this.mConnection, 1);
        this.mParent.addView((View)this.mView);
    }
    
    private class AdminSecurityView extends SurfaceView
    {
        private SurfaceHolder$Callback mSurfaceHolderCallback;
        
        AdminSecurityView(final AdminSecondaryLockScreenController adminSecondaryLockScreenController, final Context context, final SurfaceHolder$Callback mSurfaceHolderCallback) {
            super(context);
            this.mSurfaceHolderCallback = mSurfaceHolderCallback;
            this.setZOrderOnTop(true);
        }
        
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.getHolder().addCallback(this.mSurfaceHolderCallback);
        }
        
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.getHolder().removeCallback(this.mSurfaceHolderCallback);
        }
    }
}
