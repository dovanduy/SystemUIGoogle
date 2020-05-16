// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.os.Bundle;
import android.util.TimeUtils;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.SystemClock;
import android.util.MathUtils;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import com.android.systemui.Dependency;
import android.animation.ValueAnimator;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.Dumpable;

public class LightBarTransitionsController implements Dumpable, Callbacks, StateListener
{
    private final DarkIntensityApplier mApplier;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private float mDarkIntensity;
    private int mDisplayId;
    private float mDozeAmount;
    private final Handler mHandler;
    private final KeyguardStateController mKeyguardStateController;
    private float mNextDarkIntensity;
    private float mPendingDarkIntensity;
    private final StatusBarStateController mStatusBarStateController;
    private ValueAnimator mTintAnimator;
    private boolean mTintChangePending;
    private boolean mTransitionDeferring;
    private final Runnable mTransitionDeferringDoneRunnable;
    private long mTransitionDeferringDuration;
    private long mTransitionDeferringStartTime;
    private boolean mTransitionPending;
    
    public LightBarTransitionsController(final Context mContext, final DarkIntensityApplier mApplier, final CommandQueue mCommandQueue) {
        this.mTransitionDeferringDoneRunnable = new Runnable() {
            @Override
            public void run() {
                LightBarTransitionsController.this.mTransitionDeferring = false;
            }
        };
        this.mApplier = mApplier;
        this.mHandler = new Handler();
        this.mKeyguardStateController = Dependency.get(KeyguardStateController.class);
        this.mStatusBarStateController = Dependency.get(StatusBarStateController.class);
        (this.mCommandQueue = mCommandQueue).addCallback((CommandQueue.Callbacks)this);
        this.mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.mDozeAmount = this.mStatusBarStateController.getDozeAmount();
        this.mContext = mContext;
        this.mDisplayId = mContext.getDisplayId();
    }
    
    private void animateIconTint(final float mNextDarkIntensity, final long startDelay, final long duration) {
        if (this.mNextDarkIntensity == mNextDarkIntensity) {
            return;
        }
        final ValueAnimator mTintAnimator = this.mTintAnimator;
        if (mTintAnimator != null) {
            mTintAnimator.cancel();
        }
        this.mNextDarkIntensity = mNextDarkIntensity;
        (this.mTintAnimator = ValueAnimator.ofFloat(new float[] { this.mDarkIntensity, mNextDarkIntensity })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$LightBarTransitionsController$PJRveQsGC7aANrqdSv3tRYb3x7c(this));
        this.mTintAnimator.setDuration(duration);
        this.mTintAnimator.setStartDelay(startDelay);
        this.mTintAnimator.setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
        this.mTintAnimator.start();
    }
    
    private void deferIconTintChange(final float mPendingDarkIntensity) {
        if (this.mTintChangePending && mPendingDarkIntensity == this.mPendingDarkIntensity) {
            return;
        }
        this.mTintChangePending = true;
        this.mPendingDarkIntensity = mPendingDarkIntensity;
    }
    
    private void dispatchDark() {
        this.mApplier.applyDarkIntensity(MathUtils.lerp(this.mDarkIntensity, 0.0f, this.mDozeAmount));
    }
    
    private void setIconTintInternal(final float mDarkIntensity) {
        this.mDarkIntensity = mDarkIntensity;
        this.dispatchDark();
    }
    
    @Override
    public void appTransitionCancelled(final int n) {
        if (this.mDisplayId != n) {
            return;
        }
        if (this.mTransitionPending && this.mTintChangePending) {
            this.mTintChangePending = false;
            this.animateIconTint(this.mPendingDarkIntensity, 0L, this.mApplier.getTintAnimationDuration());
        }
        this.mTransitionPending = false;
    }
    
    @Override
    public void appTransitionPending(final int n, final boolean b) {
        if (this.mDisplayId == n) {
            if (!this.mKeyguardStateController.isKeyguardGoingAway() || b) {
                this.mTransitionPending = true;
            }
        }
    }
    
    @Override
    public void appTransitionStarting(final int n, final long mTransitionDeferringStartTime, final long mTransitionDeferringDuration, final boolean b) {
        if (this.mDisplayId == n) {
            if (!this.mKeyguardStateController.isKeyguardGoingAway() || b) {
                if (this.mTransitionPending && this.mTintChangePending) {
                    this.mTintChangePending = false;
                    this.animateIconTint(this.mPendingDarkIntensity, Math.max(0L, mTransitionDeferringStartTime - SystemClock.uptimeMillis()), mTransitionDeferringDuration);
                }
                else if (this.mTransitionPending) {
                    this.mTransitionDeferring = true;
                    this.mTransitionDeferringStartTime = mTransitionDeferringStartTime;
                    this.mTransitionDeferringDuration = mTransitionDeferringDuration;
                    this.mHandler.removeCallbacks(this.mTransitionDeferringDoneRunnable);
                    this.mHandler.postAtTime(this.mTransitionDeferringDoneRunnable, mTransitionDeferringStartTime);
                }
                this.mTransitionPending = false;
            }
        }
    }
    
    public void destroy(final Context context) {
        this.mCommandQueue.removeCallback((CommandQueue.Callbacks)this);
        this.mStatusBarStateController.removeCallback((StatusBarStateController.StateListener)this);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("  mTransitionDeferring=");
        printWriter.print(this.mTransitionDeferring);
        if (this.mTransitionDeferring) {
            printWriter.println();
            printWriter.print("   mTransitionDeferringStartTime=");
            printWriter.println(TimeUtils.formatUptime(this.mTransitionDeferringStartTime));
            printWriter.print("   mTransitionDeferringDuration=");
            TimeUtils.formatDuration(this.mTransitionDeferringDuration, printWriter);
            printWriter.println();
        }
        printWriter.print("  mTransitionPending=");
        printWriter.print(this.mTransitionPending);
        printWriter.print(" mTintChangePending=");
        printWriter.println(this.mTintChangePending);
        printWriter.print("  mPendingDarkIntensity=");
        printWriter.print(this.mPendingDarkIntensity);
        printWriter.print(" mDarkIntensity=");
        printWriter.print(this.mDarkIntensity);
        printWriter.print(" mNextDarkIntensity=");
        printWriter.println(this.mNextDarkIntensity);
    }
    
    public float getCurrentDarkIntensity() {
        return this.mDarkIntensity;
    }
    
    @Override
    public void onDozeAmountChanged(final float n, final float mDozeAmount) {
        this.mDozeAmount = mDozeAmount;
        this.dispatchDark();
    }
    
    @Override
    public void onStateChanged(final int n) {
    }
    
    public void restoreState(final Bundle bundle) {
        this.setIconTintInternal(bundle.getFloat("dark_intensity", 0.0f));
    }
    
    public void saveState(final Bundle bundle) {
        final ValueAnimator mTintAnimator = this.mTintAnimator;
        float n;
        if (mTintAnimator != null && mTintAnimator.isRunning()) {
            n = this.mNextDarkIntensity;
        }
        else {
            n = this.mDarkIntensity;
        }
        bundle.putFloat("dark_intensity", n);
    }
    
    public void setIconsDark(final boolean b, final boolean b2) {
        float mNextDarkIntensity = 1.0f;
        if (!b2) {
            float iconTintInternal;
            if (b) {
                iconTintInternal = 1.0f;
            }
            else {
                iconTintInternal = 0.0f;
            }
            this.setIconTintInternal(iconTintInternal);
            if (!b) {
                mNextDarkIntensity = 0.0f;
            }
            this.mNextDarkIntensity = mNextDarkIntensity;
        }
        else if (this.mTransitionPending) {
            if (!b) {
                mNextDarkIntensity = 0.0f;
            }
            this.deferIconTintChange(mNextDarkIntensity);
        }
        else if (this.mTransitionDeferring) {
            if (!b) {
                mNextDarkIntensity = 0.0f;
            }
            this.animateIconTint(mNextDarkIntensity, Math.max(0L, this.mTransitionDeferringStartTime - SystemClock.uptimeMillis()), this.mTransitionDeferringDuration);
        }
        else {
            if (!b) {
                mNextDarkIntensity = 0.0f;
            }
            this.animateIconTint(mNextDarkIntensity, 0L, this.mApplier.getTintAnimationDuration());
        }
    }
    
    public interface DarkIntensityApplier
    {
        void applyDarkIntensity(final float p0);
        
        int getTintAnimationDuration();
    }
}
