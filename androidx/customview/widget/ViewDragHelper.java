// 
// Decompiled by Procyon v0.5.36
// 

package androidx.customview.widget;

import android.view.MotionEvent;
import android.util.Log;
import androidx.core.view.ViewCompat;
import java.util.Arrays;
import android.view.ViewConfiguration;
import android.content.Context;
import android.view.VelocityTracker;
import android.widget.OverScroller;
import android.view.ViewGroup;
import android.view.View;
import android.view.animation.Interpolator;

public class ViewDragHelper
{
    private static final Interpolator sInterpolator;
    private int mActivePointerId;
    private final Callback mCallback;
    private View mCapturedView;
    private final int mDefaultEdgeSize;
    private int mDragState;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    private int mEdgeSize;
    private int[] mInitialEdgesTouched;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private float mMaxVelocity;
    private float mMinVelocity;
    private final ViewGroup mParentView;
    private int mPointersDown;
    private boolean mReleaseInProgress;
    private OverScroller mScroller;
    private final Runnable mSetIdleRunnable;
    private int mTouchSlop;
    private int mTrackingEdges;
    private VelocityTracker mVelocityTracker;
    
    static {
        sInterpolator = (Interpolator)new Interpolator() {
            public float getInterpolation(float n) {
                --n;
                return n * n * n * n * n + 1.0f;
            }
        };
    }
    
    private ViewDragHelper(final Context context, final ViewGroup mParentView, final Callback mCallback) {
        this.mActivePointerId = -1;
        this.mSetIdleRunnable = new Runnable() {
            @Override
            public void run() {
                ViewDragHelper.this.setDragState(0);
            }
        };
        if (mParentView == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        }
        if (mCallback != null) {
            this.mParentView = mParentView;
            this.mCallback = mCallback;
            final ViewConfiguration value = ViewConfiguration.get(context);
            final int n = (int)(context.getResources().getDisplayMetrics().density * 20.0f + 0.5f);
            this.mDefaultEdgeSize = n;
            this.mEdgeSize = n;
            this.mTouchSlop = value.getScaledTouchSlop();
            this.mMaxVelocity = (float)value.getScaledMaximumFlingVelocity();
            this.mMinVelocity = (float)value.getScaledMinimumFlingVelocity();
            this.mScroller = new OverScroller(context, ViewDragHelper.sInterpolator);
            return;
        }
        throw new IllegalArgumentException("Callback may not be null");
    }
    
    private boolean checkNewEdgeDrag(float abs, float abs2, final int n, final int n2) {
        abs = Math.abs(abs);
        abs2 = Math.abs(abs2);
        final int n3 = this.mInitialEdgesTouched[n];
        boolean b2;
        final boolean b = b2 = false;
        if ((n3 & n2) == n2) {
            b2 = b;
            if ((this.mTrackingEdges & n2) != 0x0) {
                b2 = b;
                if ((this.mEdgeDragsLocked[n] & n2) != n2) {
                    b2 = b;
                    if ((this.mEdgeDragsInProgress[n] & n2) != n2) {
                        final int mTouchSlop = this.mTouchSlop;
                        if (abs <= mTouchSlop && abs2 <= mTouchSlop) {
                            b2 = b;
                        }
                        else {
                            if (abs < abs2 * 0.5f && this.mCallback.onEdgeLock(n2)) {
                                final int[] mEdgeDragsLocked = this.mEdgeDragsLocked;
                                mEdgeDragsLocked[n] |= n2;
                                return false;
                            }
                            b2 = b;
                            if ((this.mEdgeDragsInProgress[n] & n2) == 0x0) {
                                b2 = b;
                                if (abs > this.mTouchSlop) {
                                    b2 = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    private boolean checkTouchSlop(final View view, final float a, final float a2) {
        final boolean b = false;
        final boolean b2 = false;
        boolean b3 = false;
        if (view == null) {
            return false;
        }
        final boolean b4 = this.mCallback.getViewHorizontalDragRange(view) > 0;
        final boolean b5 = this.mCallback.getViewVerticalDragRange(view) > 0;
        if (b4 && b5) {
            final int mTouchSlop = this.mTouchSlop;
            if (a * a + a2 * a2 > mTouchSlop * mTouchSlop) {
                b3 = true;
            }
            return b3;
        }
        if (b4) {
            boolean b6 = b;
            if (Math.abs(a) > this.mTouchSlop) {
                b6 = true;
            }
            return b6;
        }
        boolean b7 = b2;
        if (b5) {
            b7 = b2;
            if (Math.abs(a2) > this.mTouchSlop) {
                b7 = true;
            }
        }
        return b7;
    }
    
    private float clampMag(final float a, final float n, float n2) {
        final float abs = Math.abs(a);
        if (abs < n) {
            return 0.0f;
        }
        if (abs > n2) {
            if (a <= 0.0f) {
                n2 = -n2;
            }
            return n2;
        }
        return a;
    }
    
    private int clampMag(final int a, final int n, int n2) {
        final int abs = Math.abs(a);
        if (abs < n) {
            return 0;
        }
        if (abs > n2) {
            if (a <= 0) {
                n2 = -n2;
            }
            return n2;
        }
        return a;
    }
    
    private void clearMotionHistory() {
        final float[] mInitialMotionX = this.mInitialMotionX;
        if (mInitialMotionX == null) {
            return;
        }
        Arrays.fill(mInitialMotionX, 0.0f);
        Arrays.fill(this.mInitialMotionY, 0.0f);
        Arrays.fill(this.mLastMotionX, 0.0f);
        Arrays.fill(this.mLastMotionY, 0.0f);
        Arrays.fill(this.mInitialEdgesTouched, 0);
        Arrays.fill(this.mEdgeDragsInProgress, 0);
        Arrays.fill(this.mEdgeDragsLocked, 0);
        this.mPointersDown = 0;
    }
    
    private void clearMotionHistory(final int n) {
        if (this.mInitialMotionX != null) {
            if (this.isPointerDown(n)) {
                this.mInitialMotionX[n] = 0.0f;
                this.mInitialMotionY[n] = 0.0f;
                this.mLastMotionX[n] = 0.0f;
                this.mLastMotionY[n] = 0.0f;
                this.mInitialEdgesTouched[n] = 0;
                this.mEdgeDragsInProgress[n] = 0;
                this.mEdgeDragsLocked[n] = 0;
                this.mPointersDown &= 1 << n;
            }
        }
    }
    
    private int computeAxisDuration(int a, int abs, final int n) {
        if (a == 0) {
            return 0;
        }
        final int width = this.mParentView.getWidth();
        final int n2 = width / 2;
        final float min = Math.min(1.0f, Math.abs(a) / (float)width);
        final float n3 = (float)n2;
        final float distanceInfluenceForSnapDuration = this.distanceInfluenceForSnapDuration(min);
        abs = Math.abs(abs);
        if (abs > 0) {
            a = Math.round(Math.abs((n3 + distanceInfluenceForSnapDuration * n3) / abs) * 1000.0f) * 4;
        }
        else {
            a = (int)((Math.abs(a) / (float)n + 1.0f) * 256.0f);
        }
        return Math.min(a, 600);
    }
    
    private int computeSettleDuration(final View view, int computeAxisDuration, int computeAxisDuration2, int clampMag, int n) {
        final int clampMag2 = this.clampMag(clampMag, (int)this.mMinVelocity, (int)this.mMaxVelocity);
        clampMag = this.clampMag(n, (int)this.mMinVelocity, (int)this.mMaxVelocity);
        final int abs = Math.abs(computeAxisDuration);
        final int abs2 = Math.abs(computeAxisDuration2);
        final int abs3 = Math.abs(clampMag2);
        final int abs4 = Math.abs(clampMag);
        n = abs3 + abs4;
        final int n2 = abs + abs2;
        float n3;
        float n4;
        if (clampMag2 != 0) {
            n3 = (float)abs3;
            n4 = (float)n;
        }
        else {
            n3 = (float)abs;
            n4 = (float)n2;
        }
        final float n5 = n3 / n4;
        float n6;
        float n7;
        if (clampMag != 0) {
            n6 = (float)abs4;
            n7 = (float)n;
        }
        else {
            n6 = (float)abs2;
            n7 = (float)n2;
        }
        final float n8 = n6 / n7;
        computeAxisDuration = this.computeAxisDuration(computeAxisDuration, clampMag2, this.mCallback.getViewHorizontalDragRange(view));
        computeAxisDuration2 = this.computeAxisDuration(computeAxisDuration2, clampMag, this.mCallback.getViewVerticalDragRange(view));
        return (int)(computeAxisDuration * n5 + computeAxisDuration2 * n8);
    }
    
    public static ViewDragHelper create(final ViewGroup viewGroup, final float n, final Callback callback) {
        final ViewDragHelper create = create(viewGroup, callback);
        create.mTouchSlop *= (int)(1.0f / n);
        return create;
    }
    
    public static ViewDragHelper create(final ViewGroup viewGroup, final Callback callback) {
        return new ViewDragHelper(viewGroup.getContext(), viewGroup, callback);
    }
    
    private void dispatchViewReleased(final float n, final float n2) {
        this.mReleaseInProgress = true;
        this.mCallback.onViewReleased(this.mCapturedView, n, n2);
        this.mReleaseInProgress = false;
        if (this.mDragState == 1) {
            this.setDragState(0);
        }
    }
    
    private float distanceInfluenceForSnapDuration(final float n) {
        return (float)Math.sin((n - 0.5f) * 0.47123894f);
    }
    
    private void dragTo(int clampViewPositionVertical, final int n, final int n2, final int n3) {
        final int left = this.mCapturedView.getLeft();
        final int top = this.mCapturedView.getTop();
        int clampViewPositionHorizontal = clampViewPositionVertical;
        if (n2 != 0) {
            clampViewPositionHorizontal = this.mCallback.clampViewPositionHorizontal(this.mCapturedView, clampViewPositionVertical, n2);
            ViewCompat.offsetLeftAndRight(this.mCapturedView, clampViewPositionHorizontal - left);
        }
        clampViewPositionVertical = n;
        if (n3 != 0) {
            clampViewPositionVertical = this.mCallback.clampViewPositionVertical(this.mCapturedView, n, n3);
            ViewCompat.offsetTopAndBottom(this.mCapturedView, clampViewPositionVertical - top);
        }
        if (n2 != 0 || n3 != 0) {
            this.mCallback.onViewPositionChanged(this.mCapturedView, clampViewPositionHorizontal, clampViewPositionVertical, clampViewPositionHorizontal - left, clampViewPositionVertical - top);
        }
    }
    
    private void ensureMotionHistorySizeForId(int n) {
        final float[] mInitialMotionX = this.mInitialMotionX;
        if (mInitialMotionX == null || mInitialMotionX.length <= n) {
            final float[] mInitialMotionX2 = new float[++n];
            final float[] mInitialMotionY = new float[n];
            final float[] mLastMotionX = new float[n];
            final float[] mLastMotionY = new float[n];
            final int[] mInitialEdgesTouched = new int[n];
            final int[] mEdgeDragsInProgress = new int[n];
            final int[] mEdgeDragsLocked = new int[n];
            final float[] mInitialMotionX3 = this.mInitialMotionX;
            if (mInitialMotionX3 != null) {
                System.arraycopy(mInitialMotionX3, 0, mInitialMotionX2, 0, mInitialMotionX3.length);
                final float[] mInitialMotionY2 = this.mInitialMotionY;
                System.arraycopy(mInitialMotionY2, 0, mInitialMotionY, 0, mInitialMotionY2.length);
                final float[] mLastMotionX2 = this.mLastMotionX;
                System.arraycopy(mLastMotionX2, 0, mLastMotionX, 0, mLastMotionX2.length);
                final float[] mLastMotionY2 = this.mLastMotionY;
                System.arraycopy(mLastMotionY2, 0, mLastMotionY, 0, mLastMotionY2.length);
                final int[] mInitialEdgesTouched2 = this.mInitialEdgesTouched;
                System.arraycopy(mInitialEdgesTouched2, 0, mInitialEdgesTouched, 0, mInitialEdgesTouched2.length);
                final int[] mEdgeDragsInProgress2 = this.mEdgeDragsInProgress;
                System.arraycopy(mEdgeDragsInProgress2, 0, mEdgeDragsInProgress, 0, mEdgeDragsInProgress2.length);
                final int[] mEdgeDragsLocked2 = this.mEdgeDragsLocked;
                System.arraycopy(mEdgeDragsLocked2, 0, mEdgeDragsLocked, 0, mEdgeDragsLocked2.length);
            }
            this.mInitialMotionX = mInitialMotionX2;
            this.mInitialMotionY = mInitialMotionY;
            this.mLastMotionX = mLastMotionX;
            this.mLastMotionY = mLastMotionY;
            this.mInitialEdgesTouched = mInitialEdgesTouched;
            this.mEdgeDragsInProgress = mEdgeDragsInProgress;
            this.mEdgeDragsLocked = mEdgeDragsLocked;
        }
    }
    
    private boolean forceSettleCapturedViewAt(int n, int n2, int computeSettleDuration, final int n3) {
        final int left = this.mCapturedView.getLeft();
        final int top = this.mCapturedView.getTop();
        n -= left;
        n2 -= top;
        if (n == 0 && n2 == 0) {
            this.mScroller.abortAnimation();
            this.setDragState(0);
            return false;
        }
        computeSettleDuration = this.computeSettleDuration(this.mCapturedView, n, n2, computeSettleDuration, n3);
        this.mScroller.startScroll(left, top, n, n2, computeSettleDuration);
        this.setDragState(2);
        return true;
    }
    
    private int getEdgesTouched(int n, final int n2) {
        int n3;
        final boolean b = (n3 = ((n < this.mParentView.getLeft() + this.mEdgeSize) ? 1 : 0)) != 0;
        if (n2 < this.mParentView.getTop() + this.mEdgeSize) {
            n3 = ((b ? 1 : 0) | 0x4);
        }
        int n4 = n3;
        if (n > this.mParentView.getRight() - this.mEdgeSize) {
            n4 = (n3 | 0x2);
        }
        n = n4;
        if (n2 > this.mParentView.getBottom() - this.mEdgeSize) {
            n = (n4 | 0x8);
        }
        return n;
    }
    
    private boolean isValidPointerForActionMove(final int i) {
        if (!this.isPointerDown(i)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Ignoring pointerId=");
            sb.append(i);
            sb.append(" because ACTION_DOWN was not received for this pointer before ACTION_MOVE. It likely happened because  ViewDragHelper did not receive all the events in the event stream.");
            Log.e("ViewDragHelper", sb.toString());
            return false;
        }
        return true;
    }
    
    private void releaseViewForPointerUp() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        this.dispatchViewReleased(this.clampMag(this.mVelocityTracker.getXVelocity(this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity), this.clampMag(this.mVelocityTracker.getYVelocity(this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity));
    }
    
    private void reportNewEdgeDrags(final float n, final float n2, final int n3) {
        boolean b = true;
        if (!this.checkNewEdgeDrag(n, n2, n3, 1)) {
            b = false;
        }
        int n4 = b ? 1 : 0;
        if (this.checkNewEdgeDrag(n2, n, n3, 4)) {
            n4 = ((b ? 1 : 0) | 0x4);
        }
        int n5 = n4;
        if (this.checkNewEdgeDrag(n, n2, n3, 2)) {
            n5 = (n4 | 0x2);
        }
        int n6 = n5;
        if (this.checkNewEdgeDrag(n2, n, n3, 8)) {
            n6 = (n5 | 0x8);
        }
        if (n6 != 0) {
            final int[] mEdgeDragsInProgress = this.mEdgeDragsInProgress;
            mEdgeDragsInProgress[n3] |= n6;
            this.mCallback.onEdgeDragStarted(n6, n3);
        }
    }
    
    private void saveInitialMotion(final float n, final float n2, final int n3) {
        this.ensureMotionHistorySizeForId(n3);
        this.mInitialMotionX[n3] = (this.mLastMotionX[n3] = n);
        this.mInitialMotionY[n3] = (this.mLastMotionY[n3] = n2);
        this.mInitialEdgesTouched[n3] = this.getEdgesTouched((int)n, (int)n2);
        this.mPointersDown |= 1 << n3;
    }
    
    private void saveLastMotion(final MotionEvent motionEvent) {
        for (int pointerCount = motionEvent.getPointerCount(), i = 0; i < pointerCount; ++i) {
            final int pointerId = motionEvent.getPointerId(i);
            if (this.isValidPointerForActionMove(pointerId)) {
                final float x = motionEvent.getX(i);
                final float y = motionEvent.getY(i);
                this.mLastMotionX[pointerId] = x;
                this.mLastMotionY[pointerId] = y;
            }
        }
    }
    
    public void abort() {
        this.cancel();
        if (this.mDragState == 2) {
            final int currX = this.mScroller.getCurrX();
            final int currY = this.mScroller.getCurrY();
            this.mScroller.abortAnimation();
            final int currX2 = this.mScroller.getCurrX();
            final int currY2 = this.mScroller.getCurrY();
            this.mCallback.onViewPositionChanged(this.mCapturedView, currX2, currY2, currX2 - currX, currY2 - currY);
        }
        this.setDragState(0);
    }
    
    public void cancel() {
        this.mActivePointerId = -1;
        this.clearMotionHistory();
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    public void captureChildView(final View mCapturedView, final int mActivePointerId) {
        if (mCapturedView.getParent() == this.mParentView) {
            this.mCapturedView = mCapturedView;
            this.mActivePointerId = mActivePointerId;
            this.mCallback.onViewCaptured(mCapturedView, mActivePointerId);
            this.setDragState(1);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (");
        sb.append(this.mParentView);
        sb.append(")");
        throw new IllegalArgumentException(sb.toString());
    }
    
    public boolean checkTouchSlop(final int n) {
        for (int length = this.mInitialMotionX.length, i = 0; i < length; ++i) {
            if (this.checkTouchSlop(n, i)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkTouchSlop(int mTouchSlop, final int n) {
        final boolean pointerDown = this.isPointerDown(n);
        final boolean b = false;
        final boolean b2 = false;
        boolean b3 = false;
        if (!pointerDown) {
            return false;
        }
        final boolean b4 = (mTouchSlop & 0x1) == 0x1;
        if ((mTouchSlop & 0x2) == 0x2) {
            mTouchSlop = 1;
        }
        else {
            mTouchSlop = 0;
        }
        final float a = this.mLastMotionX[n] - this.mInitialMotionX[n];
        final float a2 = this.mLastMotionY[n] - this.mInitialMotionY[n];
        if (b4 && mTouchSlop != 0) {
            mTouchSlop = this.mTouchSlop;
            if (a * a + a2 * a2 > mTouchSlop * mTouchSlop) {
                b3 = true;
            }
            return b3;
        }
        if (b4) {
            boolean b5 = b;
            if (Math.abs(a) > this.mTouchSlop) {
                b5 = true;
            }
            return b5;
        }
        boolean b6 = b2;
        if (mTouchSlop != 0) {
            b6 = b2;
            if (Math.abs(a2) > this.mTouchSlop) {
                b6 = true;
            }
        }
        return b6;
    }
    
    public boolean continueSettling(final boolean b) {
        final int mDragState = this.mDragState;
        final boolean b2 = false;
        if (mDragState == 2) {
            final boolean computeScrollOffset = this.mScroller.computeScrollOffset();
            final int currX = this.mScroller.getCurrX();
            final int currY = this.mScroller.getCurrY();
            final int n = currX - this.mCapturedView.getLeft();
            final int n2 = currY - this.mCapturedView.getTop();
            if (n != 0) {
                ViewCompat.offsetLeftAndRight(this.mCapturedView, n);
            }
            if (n2 != 0) {
                ViewCompat.offsetTopAndBottom(this.mCapturedView, n2);
            }
            if (n != 0 || n2 != 0) {
                this.mCallback.onViewPositionChanged(this.mCapturedView, currX, currY, n, n2);
            }
            boolean b3 = computeScrollOffset;
            if (computeScrollOffset) {
                b3 = computeScrollOffset;
                if (currX == this.mScroller.getFinalX()) {
                    b3 = computeScrollOffset;
                    if (currY == this.mScroller.getFinalY()) {
                        this.mScroller.abortAnimation();
                        b3 = false;
                    }
                }
            }
            if (!b3) {
                if (b) {
                    this.mParentView.post(this.mSetIdleRunnable);
                }
                else {
                    this.setDragState(0);
                }
            }
        }
        boolean b4 = b2;
        if (this.mDragState == 2) {
            b4 = true;
        }
        return b4;
    }
    
    public View findTopChildUnder(final int n, final int n2) {
        for (int i = this.mParentView.getChildCount() - 1; i >= 0; --i) {
            final ViewGroup mParentView = this.mParentView;
            this.mCallback.getOrderedChildIndex(i);
            final View child = mParentView.getChildAt(i);
            if (n >= child.getLeft() && n < child.getRight() && n2 >= child.getTop() && n2 < child.getBottom()) {
                return child;
            }
        }
        return null;
    }
    
    public View getCapturedView() {
        return this.mCapturedView;
    }
    
    public int getDefaultEdgeSize() {
        return this.mDefaultEdgeSize;
    }
    
    public int getEdgeSize() {
        return this.mEdgeSize;
    }
    
    public int getTouchSlop() {
        return this.mTouchSlop;
    }
    
    public int getViewDragState() {
        return this.mDragState;
    }
    
    public boolean isCapturedViewUnder(final int n, final int n2) {
        return this.isViewUnder(this.mCapturedView, n, n2);
    }
    
    public boolean isPointerDown(final int n) {
        final int mPointersDown = this.mPointersDown;
        boolean b = true;
        if ((mPointersDown & 1 << n) == 0x0) {
            b = false;
        }
        return b;
    }
    
    public boolean isViewUnder(final View view, final int n, final int n2) {
        final boolean b = false;
        if (view == null) {
            return false;
        }
        boolean b2 = b;
        if (n >= view.getLeft()) {
            b2 = b;
            if (n < view.getRight()) {
                b2 = b;
                if (n2 >= view.getTop()) {
                    b2 = b;
                    if (n2 < view.getBottom()) {
                        b2 = true;
                    }
                }
            }
        }
        return b2;
    }
    
    public void processTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        final int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            this.cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        final int n = 0;
        int i = 0;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked == 6) {
                                final int pointerId = motionEvent.getPointerId(actionIndex);
                                Label_0211: {
                                    if (this.mDragState == 1 && pointerId == this.mActivePointerId) {
                                        while (true) {
                                            while (i < motionEvent.getPointerCount()) {
                                                final int pointerId2 = motionEvent.getPointerId(i);
                                                if (pointerId2 != this.mActivePointerId) {
                                                    final View topChildUnder = this.findTopChildUnder((int)motionEvent.getX(i), (int)motionEvent.getY(i));
                                                    final View mCapturedView = this.mCapturedView;
                                                    if (topChildUnder == mCapturedView && this.tryCaptureViewForDrag(mCapturedView, pointerId2)) {
                                                        final int mActivePointerId = this.mActivePointerId;
                                                        if (mActivePointerId == -1) {
                                                            this.releaseViewForPointerUp();
                                                        }
                                                        break Label_0211;
                                                    }
                                                }
                                                ++i;
                                            }
                                            final int mActivePointerId = -1;
                                            continue;
                                        }
                                    }
                                }
                                this.clearMotionHistory(pointerId);
                            }
                        }
                        else {
                            final int pointerId3 = motionEvent.getPointerId(actionIndex);
                            final float x = motionEvent.getX(actionIndex);
                            final float y = motionEvent.getY(actionIndex);
                            this.saveInitialMotion(x, y, pointerId3);
                            if (this.mDragState == 0) {
                                this.tryCaptureViewForDrag(this.findTopChildUnder((int)x, (int)y), pointerId3);
                                final int n2 = this.mInitialEdgesTouched[pointerId3];
                                final int mTrackingEdges = this.mTrackingEdges;
                                if ((n2 & mTrackingEdges) != 0x0) {
                                    this.mCallback.onEdgeTouched(n2 & mTrackingEdges, pointerId3);
                                }
                            }
                            else if (this.isCapturedViewUnder((int)x, (int)y)) {
                                this.tryCaptureViewForDrag(this.mCapturedView, pointerId3);
                            }
                        }
                    }
                    else {
                        if (this.mDragState == 1) {
                            this.dispatchViewReleased(0.0f, 0.0f);
                        }
                        this.cancel();
                    }
                }
                else if (this.mDragState == 1) {
                    if (this.isValidPointerForActionMove(this.mActivePointerId)) {
                        final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        final float x2 = motionEvent.getX(pointerIndex);
                        final float y2 = motionEvent.getY(pointerIndex);
                        final float[] mLastMotionX = this.mLastMotionX;
                        final int mActivePointerId2 = this.mActivePointerId;
                        final int n3 = (int)(x2 - mLastMotionX[mActivePointerId2]);
                        final int n4 = (int)(y2 - this.mLastMotionY[mActivePointerId2]);
                        this.dragTo(this.mCapturedView.getLeft() + n3, this.mCapturedView.getTop() + n4, n3, n4);
                        this.saveLastMotion(motionEvent);
                    }
                }
                else {
                    for (int pointerCount = motionEvent.getPointerCount(), j = n; j < pointerCount; ++j) {
                        final int pointerId4 = motionEvent.getPointerId(j);
                        if (this.isValidPointerForActionMove(pointerId4)) {
                            final float x3 = motionEvent.getX(j);
                            final float y3 = motionEvent.getY(j);
                            final float n5 = x3 - this.mInitialMotionX[pointerId4];
                            final float n6 = y3 - this.mInitialMotionY[pointerId4];
                            this.reportNewEdgeDrags(n5, n6, pointerId4);
                            if (this.mDragState == 1) {
                                break;
                            }
                            final View topChildUnder2 = this.findTopChildUnder((int)x3, (int)y3);
                            if (this.checkTouchSlop(topChildUnder2, n5, n6) && this.tryCaptureViewForDrag(topChildUnder2, pointerId4)) {
                                break;
                            }
                        }
                    }
                    this.saveLastMotion(motionEvent);
                }
            }
            else {
                if (this.mDragState == 1) {
                    this.releaseViewForPointerUp();
                }
                this.cancel();
            }
        }
        else {
            final float x4 = motionEvent.getX();
            final float y4 = motionEvent.getY();
            final int pointerId5 = motionEvent.getPointerId(0);
            final View topChildUnder3 = this.findTopChildUnder((int)x4, (int)y4);
            this.saveInitialMotion(x4, y4, pointerId5);
            this.tryCaptureViewForDrag(topChildUnder3, pointerId5);
            final int n7 = this.mInitialEdgesTouched[pointerId5];
            final int mTrackingEdges2 = this.mTrackingEdges;
            if ((n7 & mTrackingEdges2) != 0x0) {
                this.mCallback.onEdgeTouched(n7 & mTrackingEdges2, pointerId5);
            }
        }
    }
    
    void setDragState(final int mDragState) {
        this.mParentView.removeCallbacks(this.mSetIdleRunnable);
        if (this.mDragState != mDragState) {
            this.mDragState = mDragState;
            this.mCallback.onViewDragStateChanged(mDragState);
            if (this.mDragState == 0) {
                this.mCapturedView = null;
            }
        }
    }
    
    public void setEdgeSize(final int mEdgeSize) {
        this.mEdgeSize = mEdgeSize;
    }
    
    public void setEdgeTrackingEnabled(final int mTrackingEdges) {
        this.mTrackingEdges = mTrackingEdges;
    }
    
    public void setMinVelocity(final float mMinVelocity) {
        this.mMinVelocity = mMinVelocity;
    }
    
    public boolean settleCapturedViewAt(final int n, final int n2) {
        if (this.mReleaseInProgress) {
            return this.forceSettleCapturedViewAt(n, n2, (int)this.mVelocityTracker.getXVelocity(this.mActivePointerId), (int)this.mVelocityTracker.getYVelocity(this.mActivePointerId));
        }
        throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }
    
    public boolean shouldInterceptTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        final int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            this.cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        Label_0593: {
            if (actionMasked != 0) {
                Label_0493: {
                    if (actionMasked == 1) {
                        break Label_0493;
                    }
                    if (actionMasked != 2) {
                        if (actionMasked == 3) {
                            break Label_0493;
                        }
                        if (actionMasked != 5) {
                            if (actionMasked == 6) {
                                this.clearMotionHistory(motionEvent.getPointerId(actionIndex));
                            }
                        }
                        else {
                            final int pointerId = motionEvent.getPointerId(actionIndex);
                            final float x = motionEvent.getX(actionIndex);
                            final float y = motionEvent.getY(actionIndex);
                            this.saveInitialMotion(x, y, pointerId);
                            final int mDragState = this.mDragState;
                            if (mDragState == 0) {
                                final int n = this.mInitialEdgesTouched[pointerId];
                                final int mTrackingEdges = this.mTrackingEdges;
                                if ((n & mTrackingEdges) != 0x0) {
                                    this.mCallback.onEdgeTouched(n & mTrackingEdges, pointerId);
                                }
                            }
                            else if (mDragState == 2) {
                                final View topChildUnder = this.findTopChildUnder((int)x, (int)y);
                                if (topChildUnder == this.mCapturedView) {
                                    this.tryCaptureViewForDrag(topChildUnder, pointerId);
                                }
                            }
                        }
                    }
                    else if (this.mInitialMotionX != null) {
                        if (this.mInitialMotionY != null) {
                            for (int pointerCount = motionEvent.getPointerCount(), i = 0; i < pointerCount; ++i) {
                                final int pointerId2 = motionEvent.getPointerId(i);
                                if (this.isValidPointerForActionMove(pointerId2)) {
                                    final float x2 = motionEvent.getX(i);
                                    final float y2 = motionEvent.getY(i);
                                    final float n2 = x2 - this.mInitialMotionX[pointerId2];
                                    final float n3 = y2 - this.mInitialMotionY[pointerId2];
                                    final View topChildUnder2 = this.findTopChildUnder((int)x2, (int)y2);
                                    final boolean b = topChildUnder2 != null && this.checkTouchSlop(topChildUnder2, n2, n3);
                                    if (b) {
                                        final int left = topChildUnder2.getLeft();
                                        final int n4 = (int)n2;
                                        final int clampViewPositionHorizontal = this.mCallback.clampViewPositionHorizontal(topChildUnder2, left + n4, n4);
                                        final int top = topChildUnder2.getTop();
                                        final int n5 = (int)n3;
                                        final int clampViewPositionVertical = this.mCallback.clampViewPositionVertical(topChildUnder2, top + n5, n5);
                                        final int viewHorizontalDragRange = this.mCallback.getViewHorizontalDragRange(topChildUnder2);
                                        final int viewVerticalDragRange = this.mCallback.getViewVerticalDragRange(topChildUnder2);
                                        if (viewHorizontalDragRange == 0 || (viewHorizontalDragRange > 0 && clampViewPositionHorizontal == left)) {
                                            if (viewVerticalDragRange == 0) {
                                                break;
                                            }
                                            if (viewVerticalDragRange > 0 && clampViewPositionVertical == top) {
                                                break;
                                            }
                                        }
                                    }
                                    this.reportNewEdgeDrags(n2, n3, pointerId2);
                                    if (this.mDragState == 1) {
                                        break;
                                    }
                                    if (b && this.tryCaptureViewForDrag(topChildUnder2, pointerId2)) {
                                        break;
                                    }
                                }
                            }
                            this.saveLastMotion(motionEvent);
                        }
                    }
                    break Label_0593;
                }
                this.cancel();
            }
            else {
                final float x3 = motionEvent.getX();
                final float y3 = motionEvent.getY();
                final int pointerId3 = motionEvent.getPointerId(0);
                this.saveInitialMotion(x3, y3, pointerId3);
                final View topChildUnder3 = this.findTopChildUnder((int)x3, (int)y3);
                if (topChildUnder3 == this.mCapturedView && this.mDragState == 2) {
                    this.tryCaptureViewForDrag(topChildUnder3, pointerId3);
                }
                final int n6 = this.mInitialEdgesTouched[pointerId3];
                final int mTrackingEdges2 = this.mTrackingEdges;
                if ((n6 & mTrackingEdges2) != 0x0) {
                    this.mCallback.onEdgeTouched(n6 & mTrackingEdges2, pointerId3);
                }
            }
        }
        boolean b2 = false;
        if (this.mDragState == 1) {
            b2 = true;
        }
        return b2;
    }
    
    public boolean smoothSlideViewTo(final View mCapturedView, final int n, final int n2) {
        this.mCapturedView = mCapturedView;
        this.mActivePointerId = -1;
        final boolean forceSettleCapturedView = this.forceSettleCapturedViewAt(n, n2, 0, 0);
        if (!forceSettleCapturedView && this.mDragState == 0 && this.mCapturedView != null) {
            this.mCapturedView = null;
        }
        return forceSettleCapturedView;
    }
    
    boolean tryCaptureViewForDrag(final View view, final int mActivePointerId) {
        if (view == this.mCapturedView && this.mActivePointerId == mActivePointerId) {
            return true;
        }
        if (view != null && this.mCallback.tryCaptureView(view, mActivePointerId)) {
            this.captureChildView(view, this.mActivePointerId = mActivePointerId);
            return true;
        }
        return false;
    }
    
    public abstract static class Callback
    {
        public abstract int clampViewPositionHorizontal(final View p0, final int p1, final int p2);
        
        public abstract int clampViewPositionVertical(final View p0, final int p1, final int p2);
        
        public int getOrderedChildIndex(final int n) {
            return n;
        }
        
        public abstract int getViewHorizontalDragRange(final View p0);
        
        public int getViewVerticalDragRange(final View view) {
            return 0;
        }
        
        public abstract void onEdgeDragStarted(final int p0, final int p1);
        
        public boolean onEdgeLock(final int n) {
            return false;
        }
        
        public void onEdgeTouched(final int n, final int n2) {
        }
        
        public abstract void onViewCaptured(final View p0, final int p1);
        
        public abstract void onViewDragStateChanged(final int p0);
        
        public abstract void onViewPositionChanged(final View p0, final int p1, final int p2, final int p3, final int p4);
        
        public abstract void onViewReleased(final View p0, final float p1, final float p2);
        
        public abstract boolean tryCaptureView(final View p0, final int p1);
    }
}
