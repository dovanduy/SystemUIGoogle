// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.MotionEvent;
import com.android.systemui.R$dimen;
import android.view.ViewConfiguration;
import android.view.View;

public class DoubleTapHelper
{
    private boolean mActivated;
    private final ActivationListener mActivationListener;
    private float mActivationX;
    private float mActivationY;
    private final DoubleTapListener mDoubleTapListener;
    private final DoubleTapLogListener mDoubleTapLogListener;
    private float mDoubleTapSlop;
    private float mDownX;
    private float mDownY;
    private final SlideBackListener mSlideBackListener;
    private Runnable mTapTimeoutRunnable;
    private float mTouchSlop;
    private boolean mTrackTouch;
    private final View mView;
    
    public DoubleTapHelper(final View mView, final ActivationListener mActivationListener, final DoubleTapListener mDoubleTapListener, final SlideBackListener mSlideBackListener, final DoubleTapLogListener mDoubleTapLogListener) {
        this.mTapTimeoutRunnable = new _$$Lambda$DoubleTapHelper$GFsC9BR8swazZioXO___Yt7_6kU(this);
        this.mTouchSlop = (float)ViewConfiguration.get(mView.getContext()).getScaledTouchSlop();
        this.mDoubleTapSlop = mView.getResources().getDimension(R$dimen.double_tap_slop);
        this.mView = mView;
        this.mActivationListener = mActivationListener;
        this.mDoubleTapListener = mDoubleTapListener;
        this.mSlideBackListener = mSlideBackListener;
        this.mDoubleTapLogListener = mDoubleTapLogListener;
    }
    
    private boolean isWithinTouchSlop(final MotionEvent motionEvent) {
        return Math.abs(motionEvent.getX() - this.mDownX) < this.mTouchSlop && Math.abs(motionEvent.getY() - this.mDownY) < this.mTouchSlop;
    }
    
    private void makeActive() {
        if (!this.mActivated) {
            this.mActivated = true;
            this.mActivationListener.onActiveChanged(true);
        }
    }
    
    private void makeInactive() {
        if (this.mActivated) {
            this.mActivated = false;
            this.mActivationListener.onActiveChanged(false);
            this.mView.removeCallbacks(this.mTapTimeoutRunnable);
        }
    }
    
    public boolean isWithinDoubleTapSlop(final MotionEvent motionEvent) {
        final boolean mActivated = this.mActivated;
        boolean b = true;
        if (!mActivated) {
            return true;
        }
        if (Math.abs(motionEvent.getX() - this.mActivationX) >= this.mDoubleTapSlop || Math.abs(motionEvent.getY() - this.mActivationY) >= this.mDoubleTapSlop) {
            b = false;
        }
        return b;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent, final int n) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked == 3) {
                        this.makeInactive();
                        this.mTrackTouch = false;
                    }
                }
                else if (!this.isWithinTouchSlop(motionEvent)) {
                    this.makeInactive();
                    this.mTrackTouch = false;
                }
            }
            else if (this.isWithinTouchSlop(motionEvent)) {
                final SlideBackListener mSlideBackListener = this.mSlideBackListener;
                if (mSlideBackListener != null && mSlideBackListener.onSlideBack()) {
                    return true;
                }
                if (!this.mActivated) {
                    this.makeActive();
                    this.mView.postDelayed(this.mTapTimeoutRunnable, 1200L);
                    this.mActivationX = motionEvent.getX();
                    this.mActivationY = motionEvent.getY();
                }
                else {
                    final boolean withinDoubleTapSlop = this.isWithinDoubleTapSlop(motionEvent);
                    final DoubleTapLogListener mDoubleTapLogListener = this.mDoubleTapLogListener;
                    if (mDoubleTapLogListener != null) {
                        mDoubleTapLogListener.onDoubleTapLog(withinDoubleTapSlop, motionEvent.getX() - this.mActivationX, motionEvent.getY() - this.mActivationY);
                    }
                    if (withinDoubleTapSlop) {
                        this.makeInactive();
                        if (!this.mDoubleTapListener.onDoubleTap()) {
                            return false;
                        }
                    }
                    else {
                        this.makeInactive();
                        this.mTrackTouch = false;
                    }
                }
            }
            else {
                this.makeInactive();
                this.mTrackTouch = false;
            }
        }
        else {
            this.mDownX = motionEvent.getX();
            final float y = motionEvent.getY();
            this.mDownY = y;
            this.mTrackTouch = true;
            if (y > n) {
                this.mTrackTouch = false;
            }
        }
        return this.mTrackTouch;
    }
    
    @FunctionalInterface
    public interface ActivationListener
    {
        void onActiveChanged(final boolean p0);
    }
    
    @FunctionalInterface
    public interface DoubleTapListener
    {
        boolean onDoubleTap();
    }
    
    @FunctionalInterface
    public interface DoubleTapLogListener
    {
        void onDoubleTapLog(final boolean p0, final float p1, final float p2);
    }
    
    @FunctionalInterface
    public interface SlideBackListener
    {
        boolean onSlideBack();
    }
}
