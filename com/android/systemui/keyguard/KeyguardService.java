// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import com.android.systemui.SystemUIApplication;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;
import android.os.Debug;
import android.os.Binder;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardDrawnCallback;
import android.os.Trace;
import android.os.Bundle;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardStateCallback;
import com.android.internal.policy.IKeyguardService$Stub;
import android.app.Service;

public class KeyguardService extends Service
{
    private final IKeyguardService$Stub mBinder;
    private final KeyguardLifecyclesDispatcher mKeyguardLifecyclesDispatcher;
    private final KeyguardViewMediator mKeyguardViewMediator;
    
    public KeyguardService(final KeyguardViewMediator mKeyguardViewMediator, final KeyguardLifecyclesDispatcher mKeyguardLifecyclesDispatcher) {
        this.mBinder = new IKeyguardService$Stub() {
            public void addStateMonitorCallback(final IKeyguardStateCallback keyguardStateCallback) {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.addStateMonitorCallback(keyguardStateCallback);
            }
            
            public void dismiss(final IKeyguardDismissCallback keyguardDismissCallback, final CharSequence charSequence) {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.dismiss(keyguardDismissCallback, charSequence);
            }
            
            public void doKeyguardTimeout(final Bundle bundle) {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.doKeyguardTimeout(bundle);
            }
            
            public void onBootCompleted() {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onBootCompleted();
            }
            
            public void onDreamingStarted() {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onDreamingStarted();
            }
            
            public void onDreamingStopped() {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onDreamingStopped();
            }
            
            public void onFinishedGoingToSleep(final int n, final boolean b) {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onFinishedGoingToSleep(n, b);
                KeyguardService.this.mKeyguardLifecyclesDispatcher.dispatch(7);
            }
            
            public void onFinishedWakingUp() {
                Trace.beginSection("KeyguardService.mBinder#onFinishedWakingUp");
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardLifecyclesDispatcher.dispatch(5);
                Trace.endSection();
            }
            
            public void onScreenTurnedOff() {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onScreenTurnedOff();
                KeyguardService.this.mKeyguardLifecyclesDispatcher.dispatch(3);
            }
            
            public void onScreenTurnedOn() {
                Trace.beginSection("KeyguardService.mBinder#onScreenTurnedOn");
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onScreenTurnedOn();
                KeyguardService.this.mKeyguardLifecyclesDispatcher.dispatch(1);
                Trace.endSection();
            }
            
            public void onScreenTurningOff() {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardLifecyclesDispatcher.dispatch(2);
            }
            
            public void onScreenTurningOn(final IKeyguardDrawnCallback keyguardDrawnCallback) {
                Trace.beginSection("KeyguardService.mBinder#onScreenTurningOn");
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onScreenTurningOn(keyguardDrawnCallback);
                KeyguardService.this.mKeyguardLifecyclesDispatcher.dispatch(0);
                Trace.endSection();
            }
            
            public void onShortPowerPressedGoHome() {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onShortPowerPressedGoHome();
            }
            
            public void onStartedGoingToSleep(final int n) {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onStartedGoingToSleep(n);
                KeyguardService.this.mKeyguardLifecyclesDispatcher.dispatch(6);
            }
            
            public void onStartedWakingUp() {
                Trace.beginSection("KeyguardService.mBinder#onStartedWakingUp");
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onStartedWakingUp();
                KeyguardService.this.mKeyguardLifecyclesDispatcher.dispatch(4);
                Trace.endSection();
            }
            
            public void onSystemReady() {
                Trace.beginSection("KeyguardService.mBinder#onSystemReady");
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.onSystemReady();
                Trace.endSection();
            }
            
            public void setCurrentUser(final int currentUser) {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.setCurrentUser(currentUser);
            }
            
            public void setKeyguardEnabled(final boolean keyguardEnabled) {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.setKeyguardEnabled(keyguardEnabled);
            }
            
            public void setOccluded(final boolean b, final boolean b2) {
                Trace.beginSection("KeyguardService.mBinder#setOccluded");
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.setOccluded(b, b2);
                Trace.endSection();
            }
            
            public void setSwitchingUser(final boolean switchingUser) {
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.setSwitchingUser(switchingUser);
            }
            
            public void startKeyguardExitAnimation(final long n, final long n2) {
                Trace.beginSection("KeyguardService.mBinder#startKeyguardExitAnimation");
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.startKeyguardExitAnimation(n, n2);
                Trace.endSection();
            }
            
            public void verifyUnlock(final IKeyguardExitCallback keyguardExitCallback) {
                Trace.beginSection("KeyguardService.mBinder#verifyUnlock");
                KeyguardService.this.checkPermission();
                KeyguardService.this.mKeyguardViewMediator.verifyUnlock(keyguardExitCallback);
                Trace.endSection();
            }
        };
        this.mKeyguardViewMediator = mKeyguardViewMediator;
        this.mKeyguardLifecyclesDispatcher = mKeyguardLifecyclesDispatcher;
    }
    
    void checkPermission() {
        if (Binder.getCallingUid() == 1000) {
            return;
        }
        if (this.getBaseContext().checkCallingOrSelfPermission("android.permission.CONTROL_KEYGUARD") == 0) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Caller needs permission 'android.permission.CONTROL_KEYGUARD' to call ");
        sb.append(Debug.getCaller());
        Log.w("KeyguardService", sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Access denied to process: ");
        sb2.append(Binder.getCallingPid());
        sb2.append(", must have permission ");
        sb2.append("android.permission.CONTROL_KEYGUARD");
        throw new SecurityException(sb2.toString());
    }
    
    public IBinder onBind(final Intent intent) {
        return (IBinder)this.mBinder;
    }
    
    public void onCreate() {
        ((SystemUIApplication)this.getApplication()).startServicesIfNeeded();
    }
}
