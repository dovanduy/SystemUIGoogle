// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import com.android.internal.logging.MetricsLogger;
import android.metrics.LogMaker;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import com.android.systemui.shared.system.QuickStepContract;
import android.view.MotionEvent;
import android.view.View;
import android.os.Looper;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.assist.AssistManager;
import dagger.Lazy;
import android.app.PendingIntent;
import android.os.Handler;
import android.view.View$OnTouchListener;
import android.view.View$OnClickListener;

public class TouchInsideHandler implements ConfigInfoListener, View$OnClickListener, View$OnTouchListener
{
    private Runnable mFallback;
    private boolean mGuardLocked;
    private boolean mGuarded;
    private final Handler mHandler;
    private boolean mInGesturalMode;
    private PendingIntent mTouchInside;
    
    TouchInsideHandler(final Lazy<AssistManager> lazy, final NavigationModeController navigationModeController) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mFallback = new _$$Lambda$TouchInsideHandler$ErXXZ7cWUAKcVb_XHbSrbkRqQrE(lazy);
        this.onNavigationModeChange(navigationModeController.addListener((NavigationModeController.ModeChangedListener)new _$$Lambda$TouchInsideHandler$i_OEzJG8jTQsMjzJeFFCpNpm2g4(this)));
    }
    
    private void gestureModeOnTouch(final View view, final MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.onTouchInside();
        }
    }
    
    private void nonGestureModeOnTouch(final View view, final MotionEvent motionEvent) {
        if (this.mGuarded && !this.mGuardLocked && motionEvent.getAction() == 0) {
            this.mGuarded = false;
        }
        else if (!this.mGuarded && motionEvent.getAction() == 1) {
            this.onTouchInside();
        }
    }
    
    private void onNavigationModeChange(final int n) {
        final boolean gesturalMode = QuickStepContract.isGesturalMode(n);
        this.mInGesturalMode = gesturalMode;
        if (gesturalMode) {
            this.mGuardLocked = false;
            this.mGuarded = false;
        }
    }
    
    private void unlockGuard() {
        this.mGuardLocked = false;
    }
    
    void maybeSetGuarded() {
        if (!this.mInGesturalMode) {
            this.mGuardLocked = true;
            this.mGuarded = true;
            this.mHandler.postDelayed((Runnable)new _$$Lambda$TouchInsideHandler$cD1H94p_TroLIRcH0iEaKrlYATs(this), 500L);
        }
    }
    
    public void onClick(final View view) {
        this.onTouchInside();
    }
    
    @Override
    public void onConfigInfo(final ConfigInfo configInfo) {
        this.mTouchInside = configInfo.onTouchInside;
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        if (this.mInGesturalMode) {
            this.gestureModeOnTouch(view, motionEvent);
        }
        else {
            this.nonGestureModeOnTouch(view, motionEvent);
        }
        return true;
    }
    
    public void onTouchInside() {
        final PendingIntent mTouchInside = this.mTouchInside;
        if (mTouchInside != null) {
            try {
                mTouchInside.send();
            }
            catch (PendingIntent$CanceledException ex) {
                Log.w("TouchInsideHandler", "Touch outside PendingIntent canceled");
                this.mFallback.run();
            }
        }
        else {
            this.mFallback.run();
        }
        MetricsLogger.action(new LogMaker(1716).setType(5).setSubtype(2));
    }
    
    void setFallback(final Runnable mFallback) {
        this.mFallback = mFallback;
    }
}
