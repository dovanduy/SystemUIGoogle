// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

import android.app.PendingIntent$CanceledException;
import android.view.MotionEvent;
import android.util.Log;
import android.os.UserHandle;
import android.content.Intent;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.R$dimen;
import android.view.View$OnClickListener;
import android.view.GestureDetector$OnGestureListener;
import java.util.concurrent.TimeUnit;
import android.view.View;
import android.app.PendingIntent;
import android.widget.ImageView;
import android.view.GestureDetector;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.FlingAnimationUtils;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.view.View$OnTouchListener;
import android.view.GestureDetector$SimpleOnGestureListener;

public class DockGestureController extends GestureDetector$SimpleOnGestureListener implements View$OnTouchListener, StateListener
{
    private static final long GEAR_VISIBLE_TIME_MILLIS;
    private final AccessibilityManager mAccessibilityManager;
    private final Context mContext;
    private final FlingAnimationUtils mFlingAnimationUtils;
    @VisibleForTesting
    int mFlingDiffThreshold;
    @VisibleForTesting
    GestureDetector mGestureDetector;
    private final Runnable mHideGearRunnable;
    private final ImageView mSettingsGear;
    private final StatusBarStateController mStatusBarStateController;
    private PendingIntent mTapAction;
    private final View mTouchDelegateView;
    
    static {
        GEAR_VISIBLE_TIME_MILLIS = TimeUnit.SECONDS.toMillis(15L);
    }
    
    DockGestureController(final Context mContext, final ImageView mSettingsGear, final View mTouchDelegateView, final StatusBarStateController mStatusBarStateController) {
        this.mContext = mContext;
        this.mHideGearRunnable = new _$$Lambda$DockGestureController$U1uRB1rafEG4u0AfmTb_Bp1jSDU(this);
        this.mGestureDetector = new GestureDetector(mContext, (GestureDetector$OnGestureListener)this);
        this.mTouchDelegateView = mTouchDelegateView;
        (this.mSettingsGear = mSettingsGear).setOnClickListener((View$OnClickListener)new _$$Lambda$DockGestureController$iWzCDc7XlM4FNawePU9ege6eABY(this));
        this.mAccessibilityManager = (AccessibilityManager)this.mContext.getSystemService("accessibility");
        this.mFlingDiffThreshold = this.mContext.getResources().getDimensionPixelSize(R$dimen.dock_fling_diff);
        this.mFlingAnimationUtils = new FlingAnimationUtils.Builder(this.mContext.getResources().getDisplayMetrics()).build();
        this.mStatusBarStateController = mStatusBarStateController;
    }
    
    private long getRecommendedTimeoutMillis() {
        final AccessibilityManager mAccessibilityManager = this.mAccessibilityManager;
        long gear_VISIBLE_TIME_MILLIS;
        if (mAccessibilityManager == null) {
            gear_VISIBLE_TIME_MILLIS = DockGestureController.GEAR_VISIBLE_TIME_MILLIS;
        }
        else {
            gear_VISIBLE_TIME_MILLIS = mAccessibilityManager.getRecommendedTimeoutMillis(Math.toIntExact(DockGestureController.GEAR_VISIBLE_TIME_MILLIS), 5);
        }
        return gear_VISIBLE_TIME_MILLIS;
    }
    
    private void hideGear() {
        if (this.mSettingsGear.isVisibleToUser()) {
            this.mSettingsGear.removeCallbacks(this.mHideGearRunnable);
            this.mSettingsGear.animate().setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN).alpha(0.0f).withEndAction((Runnable)new _$$Lambda$DockGestureController$2ftpWdljdMTMQ6z1n9fO7rtpQoE(this)).start();
        }
    }
    
    private void sendProtectedBroadcast(final Intent intent) {
        try {
            this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        }
        catch (SecurityException ex) {
            Log.w("DLGestureController", "Cannot send event", (Throwable)ex);
        }
    }
    
    private void showGear() {
        if (this.mTapAction != null) {
            return;
        }
        if (!this.mSettingsGear.isVisibleToUser()) {
            this.mSettingsGear.setVisibility(0);
            this.mSettingsGear.animate().setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN).alpha(1.0f).start();
        }
        this.mSettingsGear.removeCallbacks(this.mHideGearRunnable);
        this.mSettingsGear.postDelayed(this.mHideGearRunnable, this.getRecommendedTimeoutMillis());
    }
    
    public boolean onDown(final MotionEvent motionEvent) {
        this.sendProtectedBroadcast(new Intent("com.google.android.systemui.dreamliner.TOUCH_EVENT"));
        return false;
    }
    
    public void onDozingChanged(final boolean b) {
        if (b) {
            this.mTouchDelegateView.setOnTouchListener((View$OnTouchListener)this);
            this.showGear();
        }
        else {
            this.mTouchDelegateView.setOnTouchListener((View$OnTouchListener)null);
            this.hideGear();
        }
    }
    
    public boolean onFling(final MotionEvent motionEvent, final MotionEvent motionEvent2, final float n, float f) {
        f = motionEvent2.getX() - motionEvent.getX();
        if (Math.abs(f) > Math.abs(motionEvent2.getY() - motionEvent.getY()) && Math.abs(f) > this.mFlingDiffThreshold && (Math.abs(n) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond() || Math.signum(f) == Math.signum(n))) {
            int n2;
            if (f < 0.0f) {
                n2 = 1;
            }
            else {
                n2 = 2;
            }
            this.sendProtectedBroadcast(new Intent("com.google.android.systemui.dreamliner.FLING_EVENT").putExtra("direction", n2));
            return true;
        }
        return false;
    }
    
    public boolean onSingleTapConfirmed(final MotionEvent motionEvent) {
        final PendingIntent mTapAction = this.mTapAction;
        if (mTapAction != null) {
            try {
                mTapAction.send();
            }
            catch (PendingIntent$CanceledException ex) {
                Log.w("DLGestureController", "Tap action pending intent cancelled", (Throwable)ex);
            }
        }
        this.showGear();
        return false;
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        this.mGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    
    void setTapAction(final PendingIntent mTapAction) {
        this.mTapAction = mTapAction;
    }
    
    void startMonitoring() {
        this.mSettingsGear.setVisibility(4);
        this.onDozingChanged(this.mStatusBarStateController.isDozing());
        this.mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this);
    }
    
    void stopMonitoring() {
        this.mStatusBarStateController.removeCallback((StatusBarStateController.StateListener)this);
        this.onDozingChanged(false);
        this.mSettingsGear.setVisibility(8);
    }
}
