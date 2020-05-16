// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.app.ITaskStackListener;
import android.content.res.Configuration;
import android.os.Bundle;
import java.util.List;
import android.app.ActivityManager$RunningTaskInfo;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;
import android.content.Intent;
import android.os.Looper;
import android.content.Context;
import android.view.WindowManager;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.os.Handler;
import com.android.internal.os.SomeArgs;
import android.content.BroadcastReceiver;
import com.android.internal.annotations.VisibleForTesting;
import android.app.IActivityTaskManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.SystemUI;

public class AuthController extends SystemUI implements Callbacks, AuthDialogCallback
{
    @VisibleForTesting
    IActivityTaskManager mActivityTaskManager;
    @VisibleForTesting
    final BroadcastReceiver mBroadcastReceiver;
    private final CommandQueue mCommandQueue;
    @VisibleForTesting
    AuthDialog mCurrentDialog;
    private SomeArgs mCurrentDialogArgs;
    private Handler mHandler;
    private final Injector mInjector;
    @VisibleForTesting
    IBiometricServiceReceiverInternal mReceiver;
    private final Runnable mTaskStackChangedRunnable;
    @VisibleForTesting
    BiometricTaskStackListener mTaskStackListener;
    private WindowManager mWindowManager;
    
    public AuthController(final Context context, final CommandQueue commandQueue) {
        this(context, commandQueue, new Injector());
    }
    
    @VisibleForTesting
    AuthController(final Context context, final CommandQueue mCommandQueue, final Injector mInjector) {
        super(context);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (AuthController.this.mCurrentDialog != null && "android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                    Log.w("BiometricPrompt/AuthController", "ACTION_CLOSE_SYSTEM_DIALOGS received");
                    AuthController.this.mCurrentDialog.dismissWithoutCallback(true);
                    final AuthController this$0 = AuthController.this;
                    this$0.mCurrentDialog = null;
                    try {
                        if (this$0.mReceiver != null) {
                            this$0.mReceiver.onDialogDismissed(3, (byte[])null);
                            AuthController.this.mReceiver = null;
                        }
                    }
                    catch (RemoteException ex) {
                        Log.e("BiometricPrompt/AuthController", "Remote exception", (Throwable)ex);
                    }
                }
            }
        };
        this.mTaskStackChangedRunnable = new _$$Lambda$AuthController$9xrKq1STVRQV2C5tA3mmrn4d1Bk(this);
        this.mCommandQueue = mCommandQueue;
        this.mInjector = mInjector;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        context.registerReceiver(this.mBroadcastReceiver, intentFilter);
    }
    
    private String getErrorString(final int n, final int n2, final int n3) {
        if (n == 2) {
            return FingerprintManager.getErrorString(super.mContext, n2, n3);
        }
        if (n != 8) {
            return "";
        }
        return FaceManager.getErrorString(super.mContext, n2, n3);
    }
    
    private void onDialogDismissed(final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append("onDialogDismissed: ");
        sb.append(i);
        Log.d("BiometricPrompt/AuthController", sb.toString());
        if (this.mCurrentDialog == null) {
            Log.w("BiometricPrompt/AuthController", "Dialog already dismissed");
        }
        this.mReceiver = null;
        this.mCurrentDialog = null;
    }
    
    private void sendResultAndCleanUp(final int n, final byte[] array) {
        final IBiometricServiceReceiverInternal mReceiver = this.mReceiver;
        if (mReceiver == null) {
            Log.e("BiometricPrompt/AuthController", "sendResultAndCleanUp: Receiver is null");
            return;
        }
        try {
            mReceiver.onDialogDismissed(n, array);
        }
        catch (RemoteException ex) {
            Log.w("BiometricPrompt/AuthController", "Remote exception", (Throwable)ex);
        }
        this.onDialogDismissed(n);
    }
    
    private void showDialog(final SomeArgs mCurrentDialogArgs, final boolean b, final Bundle obj) {
        this.mCurrentDialogArgs = mCurrentDialogArgs;
        final int argi1 = mCurrentDialogArgs.argi1;
        final Bundle bundle = (Bundle)mCurrentDialogArgs.arg1;
        final boolean booleanValue = (boolean)mCurrentDialogArgs.arg3;
        final int argi2 = mCurrentDialogArgs.argi2;
        final AuthDialog buildDialog = this.buildDialog(bundle, booleanValue, argi2, argi1, (String)mCurrentDialogArgs.arg4, b, (long)mCurrentDialogArgs.arg5);
        if (buildDialog == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unsupported type: ");
            sb.append(argi1);
            Log.e("BiometricPrompt/AuthController", sb.toString());
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("userId: ");
        sb2.append(argi2);
        sb2.append(" savedState: ");
        sb2.append(obj);
        sb2.append(" mCurrentDialog: ");
        sb2.append(this.mCurrentDialog);
        sb2.append(" newDialog: ");
        sb2.append(buildDialog);
        sb2.append(" type: ");
        sb2.append(argi1);
        Log.d("BiometricPrompt/AuthController", sb2.toString());
        final AuthDialog mCurrentDialog = this.mCurrentDialog;
        if (mCurrentDialog != null) {
            mCurrentDialog.dismissWithoutCallback(false);
        }
        this.mReceiver = (IBiometricServiceReceiverInternal)mCurrentDialogArgs.arg2;
        (this.mCurrentDialog = buildDialog).show(this.mWindowManager, obj);
    }
    
    protected AuthDialog buildDialog(final Bundle biometricPromptBundle, final boolean requireConfirmation, final int userId, final int n, final String opPackageName, final boolean skipIntro, final long operationId) {
        final AuthContainerView.Builder builder = new AuthContainerView.Builder(super.mContext);
        builder.setCallback(this);
        builder.setBiometricPromptBundle(biometricPromptBundle);
        builder.setRequireConfirmation(requireConfirmation);
        builder.setUserId(userId);
        builder.setOpPackageName(opPackageName);
        builder.setSkipIntro(skipIntro);
        builder.setOperationId(operationId);
        return builder.build(n);
    }
    
    @Override
    public void hideAuthenticationDialog() {
        final StringBuilder sb = new StringBuilder();
        sb.append("hideAuthenticationDialog: ");
        sb.append(this.mCurrentDialog);
        Log.d("BiometricPrompt/AuthController", sb.toString());
        final AuthDialog mCurrentDialog = this.mCurrentDialog;
        if (mCurrentDialog == null) {
            return;
        }
        mCurrentDialog.dismissFromSystemServer();
        this.mCurrentDialog = null;
    }
    
    @Override
    public void onBiometricAuthenticated() {
        this.mCurrentDialog.onAuthenticationSucceeded();
    }
    
    @Override
    public void onBiometricError(final int i, final int j, final int k) {
        boolean b = false;
        Log.d("BiometricPrompt/AuthController", String.format("onBiometricError(%d, %d, %d)", i, j, k));
        final boolean b2 = j == 7 || j == 9;
        if (j == 100 || j == 3) {
            b = true;
        }
        if (this.mCurrentDialog.isAllowDeviceCredentials() && b2) {
            Log.d("BiometricPrompt/AuthController", "onBiometricError, lockout");
            this.mCurrentDialog.animateToCredentialUI();
        }
        else if (b) {
            String str;
            if (j == 100) {
                str = super.mContext.getString(17039769);
            }
            else {
                str = this.getErrorString(i, j, k);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("onBiometricError, soft error: ");
            sb.append(str);
            Log.d("BiometricPrompt/AuthController", sb.toString());
            this.mCurrentDialog.onAuthenticationFailed(str);
        }
        else {
            final String errorString = this.getErrorString(i, j, k);
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("onBiometricError, hard error: ");
            sb2.append(errorString);
            Log.d("BiometricPrompt/AuthController", sb2.toString());
            this.mCurrentDialog.onError(errorString);
        }
    }
    
    @Override
    public void onBiometricHelp(final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append("onBiometricHelp: ");
        sb.append(str);
        Log.d("BiometricPrompt/AuthController", sb.toString());
        this.mCurrentDialog.onHelp(str);
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mCurrentDialog != null) {
            final Bundle bundle = new Bundle();
            this.mCurrentDialog.onSaveState(bundle);
            this.mCurrentDialog.dismissWithoutCallback(false);
            this.mCurrentDialog = null;
            if (bundle.getInt("container_state") != 4) {
                if (bundle.getBoolean("credential_showing")) {
                    ((Bundle)this.mCurrentDialogArgs.arg1).putInt("authenticators_allowed", 32768);
                }
                this.showDialog(this.mCurrentDialogArgs, true, bundle);
            }
        }
    }
    
    @Override
    public void onDeviceCredentialPressed() {
        final IBiometricServiceReceiverInternal mReceiver = this.mReceiver;
        if (mReceiver == null) {
            Log.e("BiometricPrompt/AuthController", "onDeviceCredentialPressed: Receiver is null");
            return;
        }
        try {
            mReceiver.onDeviceCredentialPressed();
        }
        catch (RemoteException ex) {
            Log.e("BiometricPrompt/AuthController", "RemoteException when handling credential button", (Throwable)ex);
        }
    }
    
    @Override
    public void onDismissed(final int i, final byte[] array) {
        switch (i) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unhandled reason: ");
                sb.append(i);
                Log.e("BiometricPrompt/AuthController", sb.toString());
                break;
            }
            case 7: {
                this.sendResultAndCleanUp(7, array);
                break;
            }
            case 6: {
                this.sendResultAndCleanUp(6, array);
                break;
            }
            case 5: {
                this.sendResultAndCleanUp(5, array);
                break;
            }
            case 4: {
                this.sendResultAndCleanUp(4, array);
                break;
            }
            case 3: {
                this.sendResultAndCleanUp(1, array);
                break;
            }
            case 2: {
                this.sendResultAndCleanUp(2, array);
                break;
            }
            case 1: {
                this.sendResultAndCleanUp(3, array);
                break;
            }
        }
    }
    
    @Override
    public void onSystemEvent(final int i) {
        final IBiometricServiceReceiverInternal mReceiver = this.mReceiver;
        if (mReceiver == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onSystemEvent(");
            sb.append(i);
            sb.append("): Receiver is null");
            Log.e("BiometricPrompt/AuthController", sb.toString());
            return;
        }
        try {
            mReceiver.onSystemEvent(i);
        }
        catch (RemoteException ex) {
            Log.e("BiometricPrompt/AuthController", "RemoteException when sending system event", (Throwable)ex);
        }
    }
    
    @Override
    public void onTryAgainPressed() {
        final IBiometricServiceReceiverInternal mReceiver = this.mReceiver;
        if (mReceiver == null) {
            Log.e("BiometricPrompt/AuthController", "onTryAgainPressed: Receiver is null");
            return;
        }
        try {
            mReceiver.onTryAgainPressed();
        }
        catch (RemoteException ex) {
            Log.e("BiometricPrompt/AuthController", "RemoteException when handling try again", (Throwable)ex);
        }
    }
    
    @Override
    public void showAuthenticationDialog(final Bundle arg1, final IBiometricServiceReceiverInternal arg2, final int n, final boolean b, final int argi2, final String arg3, final long n2) {
        final int authenticators = Utils.getAuthenticators(arg1);
        final StringBuilder sb = new StringBuilder();
        sb.append("showAuthenticationDialog, authenticators: ");
        sb.append(authenticators);
        sb.append(", biometricModality: ");
        sb.append(n);
        sb.append(", requireConfirmation: ");
        sb.append(b);
        sb.append(", operationId: ");
        sb.append(n2);
        Log.d("BiometricPrompt/AuthController", sb.toString());
        final SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = arg1;
        obtain.arg2 = arg2;
        obtain.argi1 = n;
        obtain.arg3 = b;
        obtain.argi2 = argi2;
        obtain.arg4 = arg3;
        obtain.arg5 = n2;
        boolean b2;
        if (this.mCurrentDialog != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("mCurrentDialog: ");
            sb2.append(this.mCurrentDialog);
            Log.w("BiometricPrompt/AuthController", sb2.toString());
            b2 = true;
        }
        else {
            b2 = false;
        }
        this.showDialog(obtain, b2, null);
    }
    
    @Override
    public void start() {
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
        this.mWindowManager = (WindowManager)super.mContext.getSystemService("window");
        this.mActivityTaskManager = this.mInjector.getActivityTaskManager();
        try {
            final BiometricTaskStackListener mTaskStackListener = new BiometricTaskStackListener();
            this.mTaskStackListener = mTaskStackListener;
            this.mActivityTaskManager.registerTaskStackListener((ITaskStackListener)mTaskStackListener);
        }
        catch (RemoteException ex) {
            Log.w("BiometricPrompt/AuthController", "Unable to register task stack listener", (Throwable)ex);
        }
    }
    
    public class BiometricTaskStackListener extends TaskStackListener
    {
        public void onTaskStackChanged() {
            AuthController.this.mHandler.post(AuthController.this.mTaskStackChangedRunnable);
        }
    }
    
    public static class Injector
    {
        IActivityTaskManager getActivityTaskManager() {
            return ActivityTaskManager.getService();
        }
    }
}
