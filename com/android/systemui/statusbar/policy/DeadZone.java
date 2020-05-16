// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.util.Slog;
import android.os.SystemClock;
import android.graphics.Canvas;
import android.content.res.Resources;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$integer;
import android.view.MotionEvent;
import com.android.systemui.Dependency;
import android.animation.ObjectAnimator;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.NavigationBarController;

public class DeadZone
{
    private final Runnable mDebugFlash;
    private int mDecay;
    private final int mDisplayId;
    private int mDisplayRotation;
    private float mFlashFrac;
    private int mHold;
    private long mLastPokeTime;
    private final NavigationBarController mNavBarController;
    private final NavigationBarView mNavigationBarView;
    private boolean mShouldFlash;
    private int mSizeMax;
    private int mSizeMin;
    private boolean mVertical;
    
    public DeadZone(final NavigationBarView mNavigationBarView) {
        this.mFlashFrac = 0.0f;
        this.mDebugFlash = new Runnable() {
            @Override
            public void run() {
                ObjectAnimator.ofFloat((Object)DeadZone.this, "flash", new float[] { 1.0f, 0.0f }).setDuration(150L).start();
            }
        };
        this.mNavigationBarView = mNavigationBarView;
        this.mNavBarController = Dependency.get(NavigationBarController.class);
        this.mDisplayId = mNavigationBarView.getContext().getDisplayId();
        this.onConfigurationChanged(0);
    }
    
    private float getSize(long n) {
        final int mSizeMax = this.mSizeMax;
        if (mSizeMax == 0) {
            return 0.0f;
        }
        n -= this.mLastPokeTime;
        final int mHold = this.mHold;
        final int mDecay = this.mDecay;
        int mSizeMin;
        if (n > mHold + mDecay) {
            mSizeMin = this.mSizeMin;
        }
        else {
            if (n < mHold) {
                return (float)mSizeMax;
            }
            mSizeMin = (int)lerp((float)mSizeMax, (float)this.mSizeMin, (n - mHold) / (float)mDecay);
        }
        return (float)mSizeMin;
    }
    
    static float lerp(final float n, final float n2, final float n3) {
        return (n2 - n) * n3 + n;
    }
    
    private void poke(final MotionEvent motionEvent) {
        this.mLastPokeTime = motionEvent.getEventTime();
        if (this.mShouldFlash) {
            this.mNavigationBarView.postInvalidate();
        }
    }
    
    public void onConfigurationChanged(int integer) {
        this.mDisplayRotation = integer;
        final Resources resources = this.mNavigationBarView.getResources();
        this.mHold = resources.getInteger(R$integer.navigation_bar_deadzone_hold);
        this.mDecay = resources.getInteger(R$integer.navigation_bar_deadzone_decay);
        this.mSizeMin = resources.getDimensionPixelSize(R$dimen.navigation_bar_deadzone_size);
        this.mSizeMax = resources.getDimensionPixelSize(R$dimen.navigation_bar_deadzone_size_max);
        integer = resources.getInteger(R$integer.navigation_bar_deadzone_orientation);
        boolean mVertical = true;
        if (integer != 1) {
            mVertical = false;
        }
        this.mVertical = mVertical;
        this.setFlashOnTouchCapture(resources.getBoolean(R$bool.config_dead_zone_flash));
    }
    
    public void onDraw(final Canvas canvas) {
        if (this.mShouldFlash) {
            if (this.mFlashFrac > 0.0f) {
                final int n = (int)this.getSize(SystemClock.uptimeMillis());
                if (this.mVertical) {
                    if (this.mDisplayRotation == 3) {
                        canvas.clipRect(canvas.getWidth() - n, 0, canvas.getWidth(), canvas.getHeight());
                    }
                    else {
                        canvas.clipRect(0, 0, n, canvas.getHeight());
                    }
                }
                else {
                    canvas.clipRect(0, 0, canvas.getWidth(), n);
                }
                canvas.drawARGB((int)(this.mFlashFrac * 255.0f), 221, 238, 170);
            }
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getToolType(0) == 3) {
            return false;
        }
        final int action = motionEvent.getAction();
        if (action == 4) {
            this.poke(motionEvent);
            return true;
        }
        if (action == 0) {
            this.mNavBarController.touchAutoDim(this.mDisplayId);
            final int n = (int)this.getSize(motionEvent.getEventTime());
            if (this.mVertical ? ((this.mDisplayRotation != 3) ? (motionEvent.getX() < n) : (motionEvent.getX() > this.mNavigationBarView.getWidth() - n)) : (motionEvent.getY() < n)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("consuming errant click: (");
                sb.append(motionEvent.getX());
                sb.append(",");
                sb.append(motionEvent.getY());
                sb.append(")");
                Slog.v("DeadZone", sb.toString());
                if (this.mShouldFlash) {
                    this.mNavigationBarView.post(this.mDebugFlash);
                    this.mNavigationBarView.postInvalidate();
                }
                return true;
            }
        }
        return false;
    }
    
    public void setFlashOnTouchCapture(final boolean mShouldFlash) {
        this.mShouldFlash = mShouldFlash;
        this.mFlashFrac = 0.0f;
        this.mNavigationBarView.postInvalidate();
    }
}
