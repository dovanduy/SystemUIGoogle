// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.accessibility;

import android.os.IBinder;
import android.view.MotionEvent;
import android.view.ViewTreeObserver$OnWindowAttachListener;
import android.os.Binder;
import android.view.SurfaceHolder;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.systemui.R$string;
import android.view.WindowManager$LayoutParams;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.R$id;
import android.content.res.Resources;
import com.android.systemui.R$integer;
import com.android.systemui.R$dimen;
import android.view.WindowManager;
import android.view.SurfaceControl$Transaction;
import android.view.SurfaceView;
import android.view.SurfaceControl;
import android.graphics.Rect;
import android.graphics.PointF;
import android.graphics.Point;
import android.content.Context;
import android.view.View;
import android.view.SurfaceHolder$Callback;
import android.view.View$OnTouchListener;

public class WindowMagnificationController implements View$OnTouchListener, SurfaceHolder$Callback, MirrorWindowDelegate
{
    private final int mBorderSize;
    private View mBottomDrag;
    private final Context mContext;
    private final int mDisplayId;
    private final Point mDisplaySize;
    private View mDragView;
    private final PointF mLastDrag;
    private View mLeftDrag;
    private final Rect mMagnificationFrame;
    private final Rect mMagnificationFrameBoundary;
    private SurfaceControl mMirrorSurface;
    private SurfaceView mMirrorSurfaceView;
    private View mMirrorView;
    private MirrorWindowControl mMirrorWindowControl;
    private View mOverlayView;
    private View mRightDrag;
    private float mScale;
    private final Rect mTmpRect;
    private View mTopDrag;
    private final SurfaceControl$Transaction mTransaction;
    private final WindowManager mWm;
    
    WindowMagnificationController(final Context mContext, final MirrorWindowControl mMirrorWindowControl) {
        this.mDisplaySize = new Point();
        this.mMagnificationFrame = new Rect();
        this.mTransaction = new SurfaceControl$Transaction();
        this.mTmpRect = new Rect();
        this.mLastDrag = new PointF();
        this.mMagnificationFrameBoundary = new Rect();
        this.mContext = mContext;
        mContext.getDisplay().getRealSize(this.mDisplaySize);
        this.mDisplayId = this.mContext.getDisplayId();
        this.mWm = (WindowManager)mContext.getSystemService("window");
        final Resources resources = mContext.getResources();
        this.mBorderSize = (int)resources.getDimension(R$dimen.magnification_border_size);
        this.mScale = (float)resources.getInteger(R$integer.magnification_default_scale);
        this.mMirrorWindowControl = mMirrorWindowControl;
        if (mMirrorWindowControl != null) {
            mMirrorWindowControl.setWindowDelegate((MirrorWindowControl.MirrorWindowDelegate)this);
        }
    }
    
    private void addDragTouchListeners() {
        this.mDragView = this.mMirrorView.findViewById(R$id.drag_handle);
        this.mLeftDrag = this.mMirrorView.findViewById(R$id.left_handle);
        this.mTopDrag = this.mMirrorView.findViewById(R$id.top_handle);
        this.mRightDrag = this.mMirrorView.findViewById(R$id.right_handle);
        this.mBottomDrag = this.mMirrorView.findViewById(R$id.bottom_handle);
        this.mDragView.setOnTouchListener((View$OnTouchListener)this);
        this.mLeftDrag.setOnTouchListener((View$OnTouchListener)this);
        this.mTopDrag.setOnTouchListener((View$OnTouchListener)this);
        this.mRightDrag.setOnTouchListener((View$OnTouchListener)this);
        this.mBottomDrag.setOnTouchListener((View$OnTouchListener)this);
    }
    
    private void createControls() {
        final MirrorWindowControl mMirrorWindowControl = this.mMirrorWindowControl;
        if (mMirrorWindowControl == null) {
            return;
        }
        mMirrorWindowControl.showControl(this.mOverlayView.getWindowToken());
        throw null;
    }
    
    private void createMirror() {
        final SurfaceControl mirrorDisplay = WindowManagerWrapper.getInstance().mirrorDisplay(this.mDisplayId);
        this.mMirrorSurface = mirrorDisplay;
        if (!mirrorDisplay.isValid()) {
            return;
        }
        this.mTransaction.show(this.mMirrorSurface).reparent(this.mMirrorSurface, this.mMirrorSurfaceView.getSurfaceControl());
        this.modifyWindowMagnification(this.mTransaction);
        this.mTransaction.apply();
    }
    
    private void createMirrorWindow() {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(this.mMagnificationFrame.width() + this.mBorderSize * 2, this.mMagnificationFrame.height() + (int)this.mContext.getResources().getDimension(R$dimen.magnification_drag_view_height) + this.mBorderSize * 2, 1000, 40, -2);
        windowManager$LayoutParams.gravity = 51;
        windowManager$LayoutParams.token = this.mOverlayView.getWindowToken();
        final Rect mMagnificationFrame = this.mMagnificationFrame;
        windowManager$LayoutParams.x = mMagnificationFrame.left;
        windowManager$LayoutParams.y = mMagnificationFrame.top;
        windowManager$LayoutParams.layoutInDisplayCutoutMode = 1;
        windowManager$LayoutParams.setTitle((CharSequence)this.mContext.getString(R$string.magnification_window_title));
        final View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.window_magnifier_view, (ViewGroup)null);
        this.mMirrorView = inflate;
        (this.mMirrorSurfaceView = (SurfaceView)inflate.findViewById(R$id.surface_view)).setZOrderOnTop(true);
        this.mMirrorView.setSystemUiVisibility(5894);
        this.mWm.addView(this.mMirrorView, (ViewGroup$LayoutParams)windowManager$LayoutParams);
        final SurfaceHolder holder = this.mMirrorSurfaceView.getHolder();
        holder.addCallback((SurfaceHolder$Callback)this);
        holder.setFormat(1);
        this.addDragTouchListeners();
    }
    
    private void createOverlayWindow() {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-1, -1, 2039, 24, -2);
        windowManager$LayoutParams.gravity = 51;
        windowManager$LayoutParams.token = (IBinder)new Binder();
        windowManager$LayoutParams.setTitle((CharSequence)this.mContext.getString(R$string.magnification_overlay_title));
        final View mOverlayView = new View(this.mContext);
        this.mOverlayView = mOverlayView;
        mOverlayView.getViewTreeObserver().addOnWindowAttachListener((ViewTreeObserver$OnWindowAttachListener)new ViewTreeObserver$OnWindowAttachListener() {
            public void onWindowAttached() {
                WindowMagnificationController.this.mOverlayView.getViewTreeObserver().removeOnWindowAttachListener((ViewTreeObserver$OnWindowAttachListener)this);
                WindowMagnificationController.this.createMirrorWindow();
                WindowMagnificationController.this.createControls();
            }
            
            public void onWindowDetached() {
            }
        });
        this.mOverlayView.setSystemUiVisibility(5894);
        this.mWm.addView(this.mOverlayView, (ViewGroup$LayoutParams)windowManager$LayoutParams);
    }
    
    private Rect getSourceBounds(final Rect rect, final float n) {
        final int n2 = rect.width() / 2;
        final int n3 = rect.height() / 2;
        final int left = rect.left;
        final int n4 = n2 - (int)(n2 / n);
        final int right = rect.right;
        final int top = rect.top;
        final int n5 = n3 - (int)(n3 / n);
        return new Rect(left + n4, top + n5, right - n4, rect.bottom - n5);
    }
    
    private boolean handleDragTouchEvent(final MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        if (action == 0) {
            this.mLastDrag.set(motionEvent.getRawX(), motionEvent.getRawY());
            return true;
        }
        if (action != 2) {
            return false;
        }
        this.moveMirrorWindow((int)(motionEvent.getRawX() - this.mLastDrag.x), (int)(motionEvent.getRawY() - this.mLastDrag.y));
        this.mLastDrag.set(motionEvent.getRawX(), motionEvent.getRawY());
        return true;
    }
    
    private void modifyWindowMagnification(final SurfaceControl$Transaction surfaceControl$Transaction) {
        final Rect sourceBounds = this.getSourceBounds(this.mMagnificationFrame, this.mScale);
        this.mTmpRect.set(0, 0, this.mMagnificationFrame.width(), this.mMagnificationFrame.height());
        final WindowManager$LayoutParams windowManager$LayoutParams = (WindowManager$LayoutParams)this.mMirrorView.getLayoutParams();
        final Rect mMagnificationFrame = this.mMagnificationFrame;
        windowManager$LayoutParams.x = mMagnificationFrame.left;
        windowManager$LayoutParams.y = mMagnificationFrame.top;
        this.mWm.updateViewLayout(this.mMirrorView, (ViewGroup$LayoutParams)windowManager$LayoutParams);
        surfaceControl$Transaction.setGeometry(this.mMirrorSurface, sourceBounds, this.mTmpRect, 0);
    }
    
    private void moveMirrorWindow(final int n, final int n2) {
        if (this.updateMagnificationFramePosition(n, n2)) {
            this.modifyWindowMagnification(this.mTransaction);
            this.mTransaction.apply();
        }
    }
    
    private void setInitialStartBounds() {
        final Point mDisplaySize = this.mDisplaySize;
        final int n = Math.min(mDisplaySize.x, mDisplaySize.y) / 2;
        final Point mDisplaySize2 = this.mDisplaySize;
        final int n2 = mDisplaySize2.x / 2;
        final int n3 = n / 2;
        final int n4 = n2 - n3;
        final int n5 = mDisplaySize2.y / 2 - n3;
        this.mMagnificationFrame.set(n4, n5, n4 + n, n + n5);
    }
    
    private void setMagnificationFrameBoundary() {
        final int n = this.mMagnificationFrame.width() / 2;
        final int n2 = this.mMagnificationFrame.height() / 2;
        final float n3 = (float)n;
        final float mScale = this.mScale;
        final int n4 = (int)(n3 / mScale);
        final int n5 = (int)(n2 / mScale);
        final int n6 = n - n4;
        final int n7 = n2 - n5;
        final Rect mMagnificationFrameBoundary = this.mMagnificationFrameBoundary;
        final int n8 = -n6;
        final int n9 = -n7;
        final Point mDisplaySize = this.mDisplaySize;
        mMagnificationFrameBoundary.set(n8, n9, mDisplaySize.x + n6, mDisplaySize.y + n7);
    }
    
    private boolean updateMagnificationFramePosition(int n, int n2) {
        this.mTmpRect.set(this.mMagnificationFrame);
        this.mTmpRect.offset(n, n2);
        final Rect mTmpRect = this.mTmpRect;
        n2 = mTmpRect.left;
        final Rect mMagnificationFrameBoundary = this.mMagnificationFrameBoundary;
        n = mMagnificationFrameBoundary.left;
        if (n2 < n) {
            mTmpRect.offsetTo(n, mTmpRect.top);
        }
        else {
            n2 = mTmpRect.right;
            n = mMagnificationFrameBoundary.right;
            if (n2 > n) {
                n2 = this.mMagnificationFrame.width();
                final Rect mTmpRect2 = this.mTmpRect;
                mTmpRect2.offsetTo(n - n2, mTmpRect2.top);
            }
        }
        final Rect mTmpRect3 = this.mTmpRect;
        n = mTmpRect3.top;
        final Rect mMagnificationFrameBoundary2 = this.mMagnificationFrameBoundary;
        n2 = mMagnificationFrameBoundary2.top;
        if (n < n2) {
            mTmpRect3.offsetTo(mTmpRect3.left, n2);
        }
        else {
            n2 = mTmpRect3.bottom;
            n = mMagnificationFrameBoundary2.bottom;
            if (n2 > n) {
                n2 = this.mMagnificationFrame.height();
                final Rect mTmpRect4 = this.mTmpRect;
                mTmpRect4.offsetTo(mTmpRect4.left, n - n2);
            }
        }
        if (!this.mTmpRect.equals((Object)this.mMagnificationFrame)) {
            this.mMagnificationFrame.set(this.mTmpRect);
            return true;
        }
        return false;
    }
    
    void createWindowMagnification() {
        if (this.mMirrorView != null) {
            return;
        }
        this.setInitialStartBounds();
        this.setMagnificationFrameBoundary();
        this.createOverlayWindow();
    }
    
    void deleteWindowMagnification() {
        final SurfaceControl mMirrorSurface = this.mMirrorSurface;
        if (mMirrorSurface != null) {
            this.mTransaction.remove(mMirrorSurface).apply();
            this.mMirrorSurface = null;
        }
        final View mOverlayView = this.mOverlayView;
        if (mOverlayView != null) {
            this.mWm.removeView(mOverlayView);
            this.mOverlayView = null;
        }
        final View mMirrorView = this.mMirrorView;
        if (mMirrorView != null) {
            this.mWm.removeView(mMirrorView);
            this.mMirrorView = null;
        }
        final MirrorWindowControl mMirrorWindowControl = this.mMirrorWindowControl;
        if (mMirrorWindowControl == null) {
            return;
        }
        mMirrorWindowControl.destroyControl();
        throw null;
    }
    
    void onConfigurationChanged(final int n) {
        final View mMirrorView = this.mMirrorView;
        if (mMirrorView != null) {
            this.mWm.removeView(mMirrorView);
            this.createMirrorWindow();
        }
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return (view == this.mDragView || view == this.mLeftDrag || view == this.mTopDrag || view == this.mRightDrag || view == this.mBottomDrag) && this.handleDragTouchEvent(motionEvent);
    }
    
    public void surfaceChanged(final SurfaceHolder surfaceHolder, final int n, final int n2, final int n3) {
    }
    
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        this.createMirror();
    }
    
    public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
    }
}
