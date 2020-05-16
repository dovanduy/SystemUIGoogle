// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import android.content.Context;
import java.io.PrintWriter;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.model.SysUiState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import dagger.Lazy;

final class AssistHandleLikeHomeBehavior implements BehaviorController
{
    private AssistHandleCallbacks mAssistHandleCallbacks;
    private boolean mIsAwake;
    private boolean mIsDozing;
    private boolean mIsHomeHandleHiding;
    private final Lazy<StatusBarStateController> mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStateListener;
    private final Lazy<SysUiState> mSysUiFlagContainer;
    private final SysUiState.SysUiStateCallback mSysUiStateCallback;
    private final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    private final WakefulnessLifecycle.Observer mWakefulnessLifecycleObserver;
    
    AssistHandleLikeHomeBehavior(final Lazy<StatusBarStateController> mStatusBarStateController, final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle, final Lazy<SysUiState> mSysUiFlagContainer) {
        this.mStatusBarStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onDozingChanged(final boolean b) {
                AssistHandleLikeHomeBehavior.this.handleDozingChanged(b);
            }
        };
        this.mWakefulnessLifecycleObserver = new WakefulnessLifecycle.Observer() {
            @Override
            public void onFinishedGoingToSleep() {
                AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
            }
            
            @Override
            public void onFinishedWakingUp() {
                AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(true);
            }
            
            @Override
            public void onStartedGoingToSleep() {
                AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
            }
            
            @Override
            public void onStartedWakingUp() {
                AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
            }
        };
        this.mSysUiStateCallback = new _$$Lambda$AssistHandleLikeHomeBehavior$vrkd_H0qzooln_t3TWfQihWw8WM(this);
        this.mStatusBarStateController = mStatusBarStateController;
        this.mWakefulnessLifecycle = mWakefulnessLifecycle;
        this.mSysUiFlagContainer = mSysUiFlagContainer;
    }
    
    private void callbackForCurrentState() {
        if (this.mAssistHandleCallbacks == null) {
            return;
        }
        if (!this.mIsHomeHandleHiding && this.isFullyAwake()) {
            this.mAssistHandleCallbacks.showAndStay();
        }
        else {
            this.mAssistHandleCallbacks.hide();
        }
    }
    
    private void handleDozingChanged(final boolean mIsDozing) {
        if (this.mIsDozing == mIsDozing) {
            return;
        }
        this.mIsDozing = mIsDozing;
        this.callbackForCurrentState();
    }
    
    private void handleSystemUiStateChange(final int n) {
        final boolean homeHandleHiding = isHomeHandleHiding(n);
        if (this.mIsHomeHandleHiding == homeHandleHiding) {
            return;
        }
        this.mIsHomeHandleHiding = homeHandleHiding;
        this.callbackForCurrentState();
    }
    
    private void handleWakefullnessChanged(final boolean mIsAwake) {
        if (this.mIsAwake == mIsAwake) {
            return;
        }
        this.mIsAwake = mIsAwake;
        this.callbackForCurrentState();
    }
    
    private boolean isFullyAwake() {
        return this.mIsAwake && !this.mIsDozing;
    }
    
    private static boolean isHomeHandleHiding(final int n) {
        return (n & 0x2) != 0x0;
    }
    
    @Override
    public void dump(final PrintWriter printWriter, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("Current AssistHandleLikeHomeBehavior State:");
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append("   mIsDozing=");
        sb2.append(this.mIsDozing);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(s);
        sb3.append("   mIsAwake=");
        sb3.append(this.mIsAwake);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(s);
        sb4.append("   mIsHomeHandleHiding=");
        sb4.append(this.mIsHomeHandleHiding);
        printWriter.println(sb4.toString());
    }
    
    @Override
    public void onModeActivated(final Context context, final AssistHandleCallbacks mAssistHandleCallbacks) {
        this.mAssistHandleCallbacks = mAssistHandleCallbacks;
        this.mIsDozing = this.mStatusBarStateController.get().isDozing();
        this.mStatusBarStateController.get().addCallback(this.mStatusBarStateListener);
        this.mIsAwake = (this.mWakefulnessLifecycle.get().getWakefulness() == 2);
        this.mWakefulnessLifecycle.get().addObserver(this.mWakefulnessLifecycleObserver);
        this.mSysUiFlagContainer.get().addCallback(this.mSysUiStateCallback);
        this.callbackForCurrentState();
    }
    
    @Override
    public void onModeDeactivated() {
        this.mAssistHandleCallbacks = null;
        this.mStatusBarStateController.get().removeCallback(this.mStatusBarStateListener);
        this.mWakefulnessLifecycle.get().removeObserver(this.mWakefulnessLifecycleObserver);
        this.mSysUiFlagContainer.get().removeCallback(this.mSysUiStateCallback);
    }
}
