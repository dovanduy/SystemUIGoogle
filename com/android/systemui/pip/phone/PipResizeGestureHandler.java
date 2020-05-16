// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.view.InputChannel;
import android.os.Looper;
import android.hardware.input.InputManager;
import java.util.function.Consumer;
import com.android.internal.policy.TaskResizingAlgorithm;
import android.view.MotionEvent;
import android.graphics.Region$Op;
import android.view.InputEvent;
import android.content.res.Resources;
import android.provider.DeviceConfig$Properties;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import android.provider.DeviceConfig;
import com.android.systemui.R$dimen;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;
import android.graphics.Region;
import com.android.systemui.pip.PipTaskOrganizer;
import com.android.systemui.pip.PipBoundsHandler;
import android.graphics.Point;
import java.util.concurrent.Executor;
import android.graphics.Rect;
import android.view.InputMonitor;
import android.view.InputEventReceiver;
import android.graphics.PointF;
import android.util.DisplayMetrics;

public class PipResizeGestureHandler
{
    private boolean mAllowGesture;
    private int mCtrlType;
    private final int mDelta;
    private final int mDisplayId;
    private final DisplayMetrics mDisplayMetrics;
    private final PointF mDownPoint;
    private boolean mEnablePipResize;
    private InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private boolean mIsAttached;
    private boolean mIsEnabled;
    private final Rect mLastDownBounds;
    private final Rect mLastResizeBounds;
    private final Executor mMainExecutor;
    private final Point mMaxSize;
    private final Point mMinSize;
    private final PipMotionHelper mMotionHelper;
    private final PipBoundsHandler mPipBoundsHandler;
    private PipTaskOrganizer mPipTaskOrganizer;
    private final Rect mTmpBounds;
    private final Region mTmpRegion;
    
    public PipResizeGestureHandler(final Context context, final PipBoundsHandler mPipBoundsHandler, final PipTouchHandler pipTouchHandler, final PipMotionHelper mMotionHelper, final DeviceConfigProxy deviceConfigProxy, final PipTaskOrganizer mPipTaskOrganizer) {
        this.mDisplayMetrics = new DisplayMetrics();
        this.mTmpRegion = new Region();
        this.mDownPoint = new PointF();
        this.mMaxSize = new Point();
        this.mMinSize = new Point();
        this.mLastResizeBounds = new Rect();
        this.mLastDownBounds = new Rect();
        this.mTmpBounds = new Rect();
        this.mAllowGesture = false;
        final Resources resources = context.getResources();
        context.getDisplay().getMetrics(this.mDisplayMetrics);
        this.mDisplayId = context.getDisplayId();
        this.mMainExecutor = context.getMainExecutor();
        this.mPipBoundsHandler = mPipBoundsHandler;
        this.mMotionHelper = mMotionHelper;
        this.mPipTaskOrganizer = mPipTaskOrganizer;
        context.getDisplay().getRealSize(this.mMaxSize);
        this.mDelta = resources.getDimensionPixelSize(R$dimen.pip_resize_edge_size);
        this.mEnablePipResize = DeviceConfig.getBoolean("systemui", "pip_user_resize", true);
        deviceConfigProxy.addOnPropertiesChangedListener("systemui", this.mMainExecutor, (DeviceConfig$OnPropertiesChangedListener)new DeviceConfig$OnPropertiesChangedListener() {
            public void onPropertiesChanged(final DeviceConfig$Properties deviceConfig$Properties) {
                if (deviceConfig$Properties.getKeyset().contains("pip_user_resize")) {
                    PipResizeGestureHandler.this.mEnablePipResize = deviceConfig$Properties.getBoolean("pip_user_resize", true);
                }
            }
        });
    }
    
    private void disposeInputChannel() {
        final InputEventReceiver mInputEventReceiver = this.mInputEventReceiver;
        if (mInputEventReceiver != null) {
            mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        final InputMonitor mInputMonitor = this.mInputMonitor;
        if (mInputMonitor != null) {
            mInputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }
    
    private boolean isWithinTouchRegion(final int n, final int n2) {
        final Rect bounds = this.mMotionHelper.getBounds();
        if (bounds == null) {
            return false;
        }
        this.mTmpBounds.set(bounds);
        final Rect mTmpBounds = this.mTmpBounds;
        final int mDelta = this.mDelta;
        mTmpBounds.inset(-mDelta, -mDelta);
        this.mTmpRegion.set(this.mTmpBounds);
        this.mTmpRegion.op(bounds, Region$Op.DIFFERENCE);
        if (this.mTmpRegion.contains(n, n2)) {
            if (n < bounds.left) {
                this.mCtrlType |= 0x1;
            }
            if (n > bounds.right) {
                this.mCtrlType |= 0x2;
            }
            if (n2 < bounds.top) {
                this.mCtrlType |= 0x4;
            }
            if (n2 > bounds.bottom) {
                this.mCtrlType |= 0x8;
            }
            return true;
        }
        return false;
    }
    
    private void onInputEvent(final InputEvent inputEvent) {
        if (inputEvent instanceof MotionEvent) {
            this.onMotionEvent((MotionEvent)inputEvent);
        }
    }
    
    private void onMotionEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mLastResizeBounds.setEmpty();
            final boolean withinTouchRegion = this.isWithinTouchRegion((int)motionEvent.getX(), (int)motionEvent.getY());
            this.mAllowGesture = withinTouchRegion;
            if (withinTouchRegion) {
                this.mDownPoint.set(motionEvent.getX(), motionEvent.getY());
                this.mLastDownBounds.set(this.mMotionHelper.getBounds());
            }
        }
        else if (this.mAllowGesture) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    this.mInputMonitor.pilferPointers();
                    final Rect bounds = this.mMotionHelper.getBounds();
                    final Rect mLastResizeBounds = this.mLastResizeBounds;
                    final float x = motionEvent.getX();
                    final float y = motionEvent.getY();
                    final PointF mDownPoint = this.mDownPoint;
                    final float x2 = mDownPoint.x;
                    final float y2 = mDownPoint.y;
                    final int mCtrlType = this.mCtrlType;
                    final Point mMinSize = this.mMinSize;
                    mLastResizeBounds.set(TaskResizingAlgorithm.resizeDrag(x, y, x2, y2, bounds, mCtrlType, mMinSize.x, mMinSize.y, this.mMaxSize, true, this.mLastDownBounds.width() > this.mLastDownBounds.height()));
                    this.mPipBoundsHandler.transformBoundsToAspectRatio(this.mLastResizeBounds);
                    this.mPipTaskOrganizer.scheduleUserResizePip(this.mLastDownBounds, this.mLastResizeBounds, null);
                    return;
                }
                if (actionMasked != 3) {
                    if (actionMasked != 5) {
                        return;
                    }
                    this.mAllowGesture = false;
                    return;
                }
            }
            this.mPipTaskOrganizer.scheduleFinishResizePip(this.mLastResizeBounds);
            this.mMotionHelper.synchronizePinnedStackBounds();
            this.mCtrlType = 0;
            this.mAllowGesture = false;
        }
    }
    
    private void updateIsEnabled() {
        final boolean mIsEnabled = this.mIsAttached && this.mEnablePipResize;
        if (mIsEnabled == this.mIsEnabled) {
            return;
        }
        this.mIsEnabled = mIsEnabled;
        this.disposeInputChannel();
        if (this.mIsEnabled) {
            this.mInputMonitor = InputManager.getInstance().monitorGestureInput("pip-resize", this.mDisplayId);
            this.mInputEventReceiver = new SysUiInputEventReceiver(this.mInputMonitor.getInputChannel(), Looper.getMainLooper());
        }
    }
    
    void onActivityPinned() {
        this.mIsAttached = true;
        this.updateIsEnabled();
    }
    
    void onActivityUnpinned() {
        this.mIsAttached = false;
        this.updateIsEnabled();
    }
    
    void updateMaxSize(final int n, final int n2) {
        this.mMaxSize.set(n, n2);
    }
    
    void updateMinSize(final int n, final int n2) {
        this.mMinSize.set(n, n2);
    }
    
    class SysUiInputEventReceiver extends InputEventReceiver
    {
        SysUiInputEventReceiver(final InputChannel inputChannel, final Looper looper) {
            super(inputChannel, looper);
        }
        
        public void onInputEvent(final InputEvent inputEvent) {
            PipResizeGestureHandler.this.onInputEvent(inputEvent);
            this.finishInputEvent(inputEvent, true);
        }
    }
}
