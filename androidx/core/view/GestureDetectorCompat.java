// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.view.GestureDetector;
import android.os.Message;
import android.view.ViewConfiguration;
import android.view.VelocityTracker;
import android.view.GestureDetector$OnDoubleTapListener;
import android.view.MotionEvent;
import android.os.Build$VERSION;
import android.os.Handler;
import android.view.GestureDetector$OnGestureListener;
import android.content.Context;

public final class GestureDetectorCompat
{
    private final GestureDetectorCompatImpl mImpl;
    
    public GestureDetectorCompat(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener) {
        this(context, gestureDetector$OnGestureListener, null);
    }
    
    public GestureDetectorCompat(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener, final Handler handler) {
        if (Build$VERSION.SDK_INT > 17) {
            this.mImpl = (GestureDetectorCompatImpl)new GestureDetectorCompatImplJellybeanMr2(context, gestureDetector$OnGestureListener, handler);
        }
        else {
            this.mImpl = (GestureDetectorCompatImpl)new GestureDetectorCompatImplBase(context, gestureDetector$OnGestureListener, handler);
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.mImpl.onTouchEvent(motionEvent);
    }
    
    interface GestureDetectorCompatImpl
    {
        boolean onTouchEvent(final MotionEvent p0);
    }
    
    static class GestureDetectorCompatImplBase implements GestureDetectorCompatImpl
    {
        private static final int DOUBLE_TAP_TIMEOUT;
        private static final int LONGPRESS_TIMEOUT;
        private static final int TAP_TIMEOUT;
        private boolean mAlwaysInBiggerTapRegion;
        private boolean mAlwaysInTapRegion;
        MotionEvent mCurrentDownEvent;
        boolean mDeferConfirmSingleTap;
        GestureDetector$OnDoubleTapListener mDoubleTapListener;
        private int mDoubleTapSlopSquare;
        private float mDownFocusX;
        private float mDownFocusY;
        private final Handler mHandler;
        private boolean mInLongPress;
        private boolean mIsDoubleTapping;
        private boolean mIsLongpressEnabled;
        private float mLastFocusX;
        private float mLastFocusY;
        final GestureDetector$OnGestureListener mListener;
        private int mMaximumFlingVelocity;
        private int mMinimumFlingVelocity;
        private MotionEvent mPreviousUpEvent;
        boolean mStillDown;
        private int mTouchSlopSquare;
        private VelocityTracker mVelocityTracker;
        
        static {
            LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
            TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
            DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
        }
        
        GestureDetectorCompatImplBase(final Context context, final GestureDetector$OnGestureListener mListener, final Handler handler) {
            if (handler != null) {
                this.mHandler = new GestureHandler(handler);
            }
            else {
                this.mHandler = new GestureHandler();
            }
            this.mListener = mListener;
            if (mListener instanceof GestureDetector$OnDoubleTapListener) {
                this.setOnDoubleTapListener((GestureDetector$OnDoubleTapListener)mListener);
            }
            this.init(context);
        }
        
        private void cancel() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
            this.mIsDoubleTapping = false;
            this.mStillDown = false;
            this.mAlwaysInTapRegion = false;
            this.mAlwaysInBiggerTapRegion = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mInLongPress) {
                this.mInLongPress = false;
            }
        }
        
        private void cancelTaps() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mIsDoubleTapping = false;
            this.mAlwaysInTapRegion = false;
            this.mAlwaysInBiggerTapRegion = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mInLongPress) {
                this.mInLongPress = false;
            }
        }
        
        private void init(final Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            }
            if (this.mListener != null) {
                this.mIsLongpressEnabled = true;
                final ViewConfiguration value = ViewConfiguration.get(context);
                final int scaledTouchSlop = value.getScaledTouchSlop();
                final int scaledDoubleTapSlop = value.getScaledDoubleTapSlop();
                this.mMinimumFlingVelocity = value.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = value.getScaledMaximumFlingVelocity();
                this.mTouchSlopSquare = scaledTouchSlop * scaledTouchSlop;
                this.mDoubleTapSlopSquare = scaledDoubleTapSlop * scaledDoubleTapSlop;
                return;
            }
            throw new IllegalArgumentException("OnGestureListener must not be null");
        }
        
        private boolean isConsideredDoubleTap(final MotionEvent motionEvent, final MotionEvent motionEvent2, final MotionEvent motionEvent3) {
            final boolean mAlwaysInBiggerTapRegion = this.mAlwaysInBiggerTapRegion;
            boolean b = false;
            if (!mAlwaysInBiggerTapRegion) {
                return false;
            }
            if (motionEvent3.getEventTime() - motionEvent2.getEventTime() > GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT) {
                return false;
            }
            final int n = (int)motionEvent.getX() - (int)motionEvent3.getX();
            final int n2 = (int)motionEvent.getY() - (int)motionEvent3.getY();
            if (n * n + n2 * n2 < this.mDoubleTapSlopSquare) {
                b = true;
            }
            return b;
        }
        
        void dispatchLongPress() {
            this.mHandler.removeMessages(3);
            this.mDeferConfirmSingleTap = false;
            this.mInLongPress = true;
            this.mListener.onLongPress(this.mCurrentDownEvent);
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent mPreviousUpEvent) {
            final int action = mPreviousUpEvent.getAction();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(mPreviousUpEvent);
            final int n = action & 0xFF;
            final boolean b = false;
            final boolean b2 = n == 6;
            int actionIndex;
            if (b2) {
                actionIndex = mPreviousUpEvent.getActionIndex();
            }
            else {
                actionIndex = -1;
            }
            final int pointerCount = mPreviousUpEvent.getPointerCount();
            int i = 0;
            float n3;
            float n2 = n3 = 0.0f;
            while (i < pointerCount) {
                if (actionIndex != i) {
                    n2 += mPreviousUpEvent.getX(i);
                    n3 += mPreviousUpEvent.getY(i);
                }
                ++i;
            }
            int n4;
            if (b2) {
                n4 = pointerCount - 1;
            }
            else {
                n4 = pointerCount;
            }
            final float n5 = (float)n4;
            final float n6 = n2 / n5;
            final float n7 = n3 / n5;
            boolean b3;
            if (n != 0) {
                if (n != 1) {
                    if (n != 2) {
                        if (n != 3) {
                            if (n != 5) {
                                if (n != 6) {
                                    b3 = b;
                                }
                                else {
                                    this.mLastFocusX = n6;
                                    this.mDownFocusX = n6;
                                    this.mLastFocusY = n7;
                                    this.mDownFocusY = n7;
                                    this.mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumFlingVelocity);
                                    final int actionIndex2 = mPreviousUpEvent.getActionIndex();
                                    final int pointerId = mPreviousUpEvent.getPointerId(actionIndex2);
                                    final float xVelocity = this.mVelocityTracker.getXVelocity(pointerId);
                                    final float yVelocity = this.mVelocityTracker.getYVelocity(pointerId);
                                    int n8 = 0;
                                    while (true) {
                                        b3 = b;
                                        if (n8 >= pointerCount) {
                                            break;
                                        }
                                        if (n8 != actionIndex2) {
                                            final int pointerId2 = mPreviousUpEvent.getPointerId(n8);
                                            if (this.mVelocityTracker.getXVelocity(pointerId2) * xVelocity + this.mVelocityTracker.getYVelocity(pointerId2) * yVelocity < 0.0f) {
                                                this.mVelocityTracker.clear();
                                                b3 = b;
                                                break;
                                            }
                                        }
                                        ++n8;
                                    }
                                }
                            }
                            else {
                                this.mLastFocusX = n6;
                                this.mDownFocusX = n6;
                                this.mLastFocusY = n7;
                                this.mDownFocusY = n7;
                                this.cancelTaps();
                                b3 = b;
                            }
                        }
                        else {
                            this.cancel();
                            b3 = b;
                        }
                    }
                    else if (this.mInLongPress) {
                        b3 = b;
                    }
                    else {
                        final float a = this.mLastFocusX - n6;
                        final float a2 = this.mLastFocusY - n7;
                        if (this.mIsDoubleTapping) {
                            b3 = (false | this.mDoubleTapListener.onDoubleTapEvent(mPreviousUpEvent));
                        }
                        else if (this.mAlwaysInTapRegion) {
                            final int n9 = (int)(n6 - this.mDownFocusX);
                            final int n10 = (int)(n7 - this.mDownFocusY);
                            final int n11 = n9 * n9 + n10 * n10;
                            boolean onScroll;
                            if (n11 > this.mTouchSlopSquare) {
                                onScroll = this.mListener.onScroll(this.mCurrentDownEvent, mPreviousUpEvent, a, a2);
                                this.mLastFocusX = n6;
                                this.mLastFocusY = n7;
                                this.mAlwaysInTapRegion = false;
                                this.mHandler.removeMessages(3);
                                this.mHandler.removeMessages(1);
                                this.mHandler.removeMessages(2);
                            }
                            else {
                                onScroll = false;
                            }
                            b3 = onScroll;
                            if (n11 > this.mTouchSlopSquare) {
                                this.mAlwaysInBiggerTapRegion = false;
                                b3 = onScroll;
                            }
                        }
                        else {
                            if (Math.abs(a) < 1.0f) {
                                b3 = b;
                                if (Math.abs(a2) < 1.0f) {
                                    return b3;
                                }
                            }
                            b3 = this.mListener.onScroll(this.mCurrentDownEvent, mPreviousUpEvent, a, a2);
                            this.mLastFocusX = n6;
                            this.mLastFocusY = n7;
                        }
                    }
                }
                else {
                    this.mStillDown = false;
                    final MotionEvent obtain = MotionEvent.obtain(mPreviousUpEvent);
                    Label_0847: {
                        if (this.mIsDoubleTapping) {
                            b3 = (this.mDoubleTapListener.onDoubleTapEvent(mPreviousUpEvent) | false);
                        }
                        else {
                            if (this.mInLongPress) {
                                this.mHandler.removeMessages(3);
                                this.mInLongPress = false;
                            }
                            else {
                                if (this.mAlwaysInTapRegion) {
                                    b3 = this.mListener.onSingleTapUp(mPreviousUpEvent);
                                    if (this.mDeferConfirmSingleTap) {
                                        final GestureDetector$OnDoubleTapListener mDoubleTapListener = this.mDoubleTapListener;
                                        if (mDoubleTapListener != null) {
                                            mDoubleTapListener.onSingleTapConfirmed(mPreviousUpEvent);
                                        }
                                    }
                                    break Label_0847;
                                }
                                final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                                final int pointerId3 = mPreviousUpEvent.getPointerId(0);
                                mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumFlingVelocity);
                                final float yVelocity2 = mVelocityTracker.getYVelocity(pointerId3);
                                final float xVelocity2 = mVelocityTracker.getXVelocity(pointerId3);
                                if (Math.abs(yVelocity2) > this.mMinimumFlingVelocity || Math.abs(xVelocity2) > this.mMinimumFlingVelocity) {
                                    b3 = this.mListener.onFling(this.mCurrentDownEvent, mPreviousUpEvent, xVelocity2, yVelocity2);
                                    break Label_0847;
                                }
                            }
                            b3 = false;
                        }
                    }
                    mPreviousUpEvent = this.mPreviousUpEvent;
                    if (mPreviousUpEvent != null) {
                        mPreviousUpEvent.recycle();
                    }
                    this.mPreviousUpEvent = obtain;
                    final VelocityTracker mVelocityTracker2 = this.mVelocityTracker;
                    if (mVelocityTracker2 != null) {
                        mVelocityTracker2.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.mIsDoubleTapping = false;
                    this.mDeferConfirmSingleTap = false;
                    this.mHandler.removeMessages(1);
                    this.mHandler.removeMessages(2);
                }
            }
            else {
                boolean b4 = false;
                Label_1032: {
                    if (this.mDoubleTapListener != null) {
                        final boolean hasMessages = this.mHandler.hasMessages(3);
                        if (hasMessages) {
                            this.mHandler.removeMessages(3);
                        }
                        final MotionEvent mCurrentDownEvent = this.mCurrentDownEvent;
                        if (mCurrentDownEvent != null) {
                            final MotionEvent mPreviousUpEvent2 = this.mPreviousUpEvent;
                            if (mPreviousUpEvent2 != null && hasMessages && this.isConsideredDoubleTap(mCurrentDownEvent, mPreviousUpEvent2, mPreviousUpEvent)) {
                                this.mIsDoubleTapping = true;
                                b4 = (this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | false | this.mDoubleTapListener.onDoubleTapEvent(mPreviousUpEvent));
                                break Label_1032;
                            }
                        }
                        this.mHandler.sendEmptyMessageDelayed(3, (long)GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT);
                    }
                    b4 = false;
                }
                this.mLastFocusX = n6;
                this.mDownFocusX = n6;
                this.mLastFocusY = n7;
                this.mDownFocusY = n7;
                final MotionEvent mCurrentDownEvent2 = this.mCurrentDownEvent;
                if (mCurrentDownEvent2 != null) {
                    mCurrentDownEvent2.recycle();
                }
                this.mCurrentDownEvent = MotionEvent.obtain(mPreviousUpEvent);
                this.mAlwaysInTapRegion = true;
                this.mAlwaysInBiggerTapRegion = true;
                this.mStillDown = true;
                this.mInLongPress = false;
                this.mDeferConfirmSingleTap = false;
                if (this.mIsLongpressEnabled) {
                    this.mHandler.removeMessages(2);
                    this.mHandler.sendEmptyMessageAtTime(2, this.mCurrentDownEvent.getDownTime() + GestureDetectorCompatImplBase.TAP_TIMEOUT + GestureDetectorCompatImplBase.LONGPRESS_TIMEOUT);
                }
                this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + GestureDetectorCompatImplBase.TAP_TIMEOUT);
                b3 = (b4 | this.mListener.onDown(mPreviousUpEvent));
            }
            return b3;
        }
        
        public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener mDoubleTapListener) {
            this.mDoubleTapListener = mDoubleTapListener;
        }
        
        private class GestureHandler extends Handler
        {
            GestureHandler() {
            }
            
            GestureHandler(final Handler handler) {
                super(handler.getLooper());
            }
            
            public void handleMessage(final Message obj) {
                final int what = obj.what;
                if (what != 1) {
                    if (what != 2) {
                        if (what != 3) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Unknown message ");
                            sb.append(obj);
                            throw new RuntimeException(sb.toString());
                        }
                        final GestureDetectorCompatImplBase this$0 = GestureDetectorCompatImplBase.this;
                        final GestureDetector$OnDoubleTapListener mDoubleTapListener = this$0.mDoubleTapListener;
                        if (mDoubleTapListener != null) {
                            if (!this$0.mStillDown) {
                                mDoubleTapListener.onSingleTapConfirmed(this$0.mCurrentDownEvent);
                            }
                            else {
                                this$0.mDeferConfirmSingleTap = true;
                            }
                        }
                    }
                    else {
                        GestureDetectorCompatImplBase.this.dispatchLongPress();
                    }
                }
                else {
                    final GestureDetectorCompatImplBase this$2 = GestureDetectorCompatImplBase.this;
                    this$2.mListener.onShowPress(this$2.mCurrentDownEvent);
                }
            }
        }
    }
    
    static class GestureDetectorCompatImplJellybeanMr2 implements GestureDetectorCompatImpl
    {
        private final GestureDetector mDetector;
        
        GestureDetectorCompatImplJellybeanMr2(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener, final Handler handler) {
            this.mDetector = new GestureDetector(context, gestureDetector$OnGestureListener, handler);
        }
        
        @Override
        public boolean onTouchEvent(final MotionEvent motionEvent) {
            return this.mDetector.onTouchEvent(motionEvent);
        }
    }
}
