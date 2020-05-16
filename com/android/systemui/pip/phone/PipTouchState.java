// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.util.Log;
import java.io.PrintWriter;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.VelocityTracker;
import android.os.Handler;
import android.graphics.PointF;
import com.android.internal.annotations.VisibleForTesting;

public class PipTouchState
{
    @VisibleForTesting
    static final long DOUBLE_TAP_TIMEOUT = 200L;
    private int mActivePointerId;
    private boolean mAllowDraggingOffscreen;
    private boolean mAllowTouches;
    private final Runnable mDoubleTapTimeoutCallback;
    private final PointF mDownDelta;
    private final PointF mDownTouch;
    private long mDownTouchTime;
    private final Handler mHandler;
    private boolean mIsDoubleTap;
    private boolean mIsDragging;
    private boolean mIsUserInteracting;
    private boolean mIsWaitingForDoubleTap;
    private final PointF mLastDelta;
    private long mLastDownTouchTime;
    private final PointF mLastTouch;
    private boolean mPreviouslyDragging;
    private boolean mStartedDragging;
    private long mUpTouchTime;
    private final PointF mVelocity;
    private VelocityTracker mVelocityTracker;
    private final ViewConfiguration mViewConfig;
    
    public PipTouchState(final ViewConfiguration mViewConfig, final Handler mHandler, final Runnable mDoubleTapTimeoutCallback) {
        this.mDownTouchTime = 0L;
        this.mLastDownTouchTime = 0L;
        this.mUpTouchTime = 0L;
        this.mDownTouch = new PointF();
        this.mDownDelta = new PointF();
        this.mLastTouch = new PointF();
        this.mLastDelta = new PointF();
        this.mVelocity = new PointF();
        this.mAllowTouches = true;
        this.mIsUserInteracting = false;
        this.mIsDoubleTap = false;
        this.mIsWaitingForDoubleTap = false;
        this.mIsDragging = false;
        this.mPreviouslyDragging = false;
        this.mStartedDragging = false;
        this.mAllowDraggingOffscreen = false;
        this.mViewConfig = mViewConfig;
        this.mHandler = mHandler;
        this.mDoubleTapTimeoutCallback = mDoubleTapTimeoutCallback;
    }
    
    private void initOrResetVelocityTracker() {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        else {
            mVelocityTracker.clear();
        }
    }
    
    private void recycleVelocityTracker() {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    void addMovementToVelocityTracker(final MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            return;
        }
        final float n = motionEvent.getRawX() - motionEvent.getX();
        final float n2 = motionEvent.getRawY() - motionEvent.getY();
        motionEvent.offsetLocation(n, n2);
        this.mVelocityTracker.addMovement(motionEvent);
        motionEvent.offsetLocation(-n, -n2);
    }
    
    public void dump(final PrintWriter printWriter, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("  ");
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append("PipTouchHandler");
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string);
        sb3.append("mAllowTouches=");
        sb3.append(this.mAllowTouches);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(string);
        sb4.append("mActivePointerId=");
        sb4.append(this.mActivePointerId);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(string);
        sb5.append("mDownTouch=");
        sb5.append(this.mDownTouch);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append(string);
        sb6.append("mDownDelta=");
        sb6.append(this.mDownDelta);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append(string);
        sb7.append("mLastTouch=");
        sb7.append(this.mLastTouch);
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append(string);
        sb8.append("mLastDelta=");
        sb8.append(this.mLastDelta);
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append(string);
        sb9.append("mVelocity=");
        sb9.append(this.mVelocity);
        printWriter.println(sb9.toString());
        final StringBuilder sb10 = new StringBuilder();
        sb10.append(string);
        sb10.append("mIsUserInteracting=");
        sb10.append(this.mIsUserInteracting);
        printWriter.println(sb10.toString());
        final StringBuilder sb11 = new StringBuilder();
        sb11.append(string);
        sb11.append("mIsDragging=");
        sb11.append(this.mIsDragging);
        printWriter.println(sb11.toString());
        final StringBuilder sb12 = new StringBuilder();
        sb12.append(string);
        sb12.append("mStartedDragging=");
        sb12.append(this.mStartedDragging);
        printWriter.println(sb12.toString());
        final StringBuilder sb13 = new StringBuilder();
        sb13.append(string);
        sb13.append("mAllowDraggingOffscreen=");
        sb13.append(this.mAllowDraggingOffscreen);
        printWriter.println(sb13.toString());
    }
    
    @VisibleForTesting
    long getDoubleTapTimeoutCallbackDelay() {
        if (this.mIsWaitingForDoubleTap) {
            return Math.max(0L, 200L - (this.mUpTouchTime - this.mDownTouchTime));
        }
        return -1L;
    }
    
    public PointF getDownTouchPosition() {
        return this.mDownTouch;
    }
    
    public PointF getLastTouchDelta() {
        return this.mLastDelta;
    }
    
    public PointF getLastTouchPosition() {
        return this.mLastTouch;
    }
    
    public PointF getVelocity() {
        return this.mVelocity;
    }
    
    public boolean isDoubleTap() {
        return this.mIsDoubleTap;
    }
    
    public boolean isDragging() {
        return this.mIsDragging;
    }
    
    public boolean isUserInteracting() {
        return this.mIsUserInteracting;
    }
    
    public boolean isWaitingForDoubleTap() {
        return this.mIsWaitingForDoubleTap;
    }
    
    public void onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        final boolean b = false;
        int n = 0;
        boolean mIsDoubleTap = true;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 6) {
                            return;
                        }
                        if (!this.mIsUserInteracting) {
                            return;
                        }
                        this.addMovementToVelocityTracker(motionEvent);
                        final int actionIndex = motionEvent.getActionIndex();
                        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                            if (actionIndex == 0) {
                                n = 1;
                            }
                            this.mActivePointerId = motionEvent.getPointerId(n);
                            this.mLastTouch.set(motionEvent.getRawX(n), motionEvent.getRawY(n));
                        }
                        return;
                    }
                }
                else {
                    if (!this.mIsUserInteracting) {
                        return;
                    }
                    this.addMovementToVelocityTracker(motionEvent);
                    final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex == -1) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Invalid active pointer id on MOVE: ");
                        sb.append(this.mActivePointerId);
                        Log.e("PipTouchHandler", sb.toString());
                        return;
                    }
                    final float rawX = motionEvent.getRawX(pointerIndex);
                    final float rawY = motionEvent.getRawY(pointerIndex);
                    final PointF mLastDelta = this.mLastDelta;
                    final PointF mLastTouch = this.mLastTouch;
                    mLastDelta.set(rawX - mLastTouch.x, rawY - mLastTouch.y);
                    final PointF mDownDelta = this.mDownDelta;
                    final PointF mDownTouch = this.mDownTouch;
                    mDownDelta.set(rawX - mDownTouch.x, rawY - mDownTouch.y);
                    final boolean b2 = this.mDownDelta.length() > this.mViewConfig.getScaledTouchSlop();
                    if (!this.mIsDragging) {
                        if (b2) {
                            this.mIsDragging = true;
                            this.mStartedDragging = true;
                        }
                    }
                    else {
                        this.mStartedDragging = false;
                    }
                    this.mLastTouch.set(rawX, rawY);
                    return;
                }
            }
            else {
                if (!this.mIsUserInteracting) {
                    return;
                }
                this.addMovementToVelocityTracker(motionEvent);
                this.mVelocityTracker.computeCurrentVelocity(1000, (float)this.mViewConfig.getScaledMaximumFlingVelocity());
                this.mVelocity.set(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
                final int pointerIndex2 = motionEvent.findPointerIndex(this.mActivePointerId);
                if (pointerIndex2 == -1) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Invalid active pointer id on UP: ");
                    sb2.append(this.mActivePointerId);
                    Log.e("PipTouchHandler", sb2.toString());
                    return;
                }
                this.mUpTouchTime = motionEvent.getEventTime();
                this.mLastTouch.set(motionEvent.getRawX(pointerIndex2), motionEvent.getRawY(pointerIndex2));
                final boolean mIsDragging = this.mIsDragging;
                this.mPreviouslyDragging = mIsDragging;
                boolean mIsWaitingForDoubleTap = b;
                if (!this.mIsDoubleTap) {
                    mIsWaitingForDoubleTap = b;
                    if (!mIsDragging) {
                        mIsWaitingForDoubleTap = b;
                        if (this.mUpTouchTime - this.mDownTouchTime < 200L) {
                            mIsWaitingForDoubleTap = true;
                        }
                    }
                }
                this.mIsWaitingForDoubleTap = mIsWaitingForDoubleTap;
            }
            this.recycleVelocityTracker();
        }
        else {
            if (!this.mAllowTouches) {
                return;
            }
            this.initOrResetVelocityTracker();
            this.addMovementToVelocityTracker(motionEvent);
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mLastTouch.set(motionEvent.getRawX(), motionEvent.getRawY());
            this.mDownTouch.set(this.mLastTouch);
            this.mAllowDraggingOffscreen = true;
            this.mIsUserInteracting = true;
            final long eventTime = motionEvent.getEventTime();
            this.mDownTouchTime = eventTime;
            if (this.mPreviouslyDragging || eventTime - this.mLastDownTouchTime >= 200L) {
                mIsDoubleTap = false;
            }
            this.mIsDoubleTap = mIsDoubleTap;
            this.mIsWaitingForDoubleTap = false;
            this.mIsDragging = false;
            this.mLastDownTouchTime = this.mDownTouchTime;
            final Runnable mDoubleTapTimeoutCallback = this.mDoubleTapTimeoutCallback;
            if (mDoubleTapTimeoutCallback != null) {
                this.mHandler.removeCallbacks(mDoubleTapTimeoutCallback);
            }
        }
    }
    
    public void reset() {
        this.mAllowDraggingOffscreen = false;
        this.mIsDragging = false;
        this.mStartedDragging = false;
        this.mIsUserInteracting = false;
    }
    
    public void scheduleDoubleTapTimeoutCallback() {
        if (this.mIsWaitingForDoubleTap) {
            final long doubleTapTimeoutCallbackDelay = this.getDoubleTapTimeoutCallbackDelay();
            this.mHandler.removeCallbacks(this.mDoubleTapTimeoutCallback);
            this.mHandler.postDelayed(this.mDoubleTapTimeoutCallback, doubleTapTimeoutCallbackDelay);
        }
    }
    
    public void setAllowTouches(final boolean mAllowTouches) {
        this.mAllowTouches = mAllowTouches;
        if (this.mIsUserInteracting) {
            this.reset();
        }
    }
    
    public boolean startedDragging() {
        return this.mStartedDragging;
    }
}
