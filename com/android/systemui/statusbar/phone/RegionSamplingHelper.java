// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.io.Serializable;
import java.io.PrintWriter;
import android.view.ViewRootImpl;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import java.util.concurrent.Executor;
import android.view.ViewTreeObserver$OnDrawListener;
import android.view.CompositionSamplingListener;
import android.view.View;
import android.view.SurfaceControl;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View$OnLayoutChangeListener;
import android.view.View$OnAttachStateChangeListener;

public class RegionSamplingHelper implements View$OnAttachStateChangeListener, View$OnLayoutChangeListener
{
    private final SamplingCallback mCallback;
    private float mCurrentMedianLuma;
    private boolean mFirstSamplingAfterStart;
    private final Handler mHandler;
    private float mLastMedianLuma;
    private final float mLuminanceChangeThreshold;
    private final float mLuminanceThreshold;
    private final Rect mRegisteredSamplingBounds;
    private SurfaceControl mRegisteredStopLayer;
    private Runnable mRemoveDrawRunnable;
    private final View mSampledView;
    private boolean mSamplingEnabled;
    private final CompositionSamplingListener mSamplingListener;
    private boolean mSamplingListenerRegistered;
    private final Rect mSamplingRequestBounds;
    private ViewTreeObserver$OnDrawListener mUpdateOnDraw;
    private boolean mWaitingOnDraw;
    private boolean mWindowVisible;
    
    public RegionSamplingHelper(final View mSampledView, final SamplingCallback mCallback) {
        this.mHandler = new Handler();
        this.mSamplingRequestBounds = new Rect();
        this.mRegisteredSamplingBounds = new Rect();
        this.mSamplingEnabled = false;
        this.mSamplingListenerRegistered = false;
        this.mRegisteredStopLayer = null;
        this.mUpdateOnDraw = (ViewTreeObserver$OnDrawListener)new ViewTreeObserver$OnDrawListener() {
            public void onDraw() {
                RegionSamplingHelper.this.mHandler.post(RegionSamplingHelper.this.mRemoveDrawRunnable);
                RegionSamplingHelper.this.onDraw();
            }
        };
        this.mRemoveDrawRunnable = new Runnable() {
            @Override
            public void run() {
                RegionSamplingHelper.this.mSampledView.getViewTreeObserver().removeOnDrawListener(RegionSamplingHelper.this.mUpdateOnDraw);
            }
        };
        this.mSamplingListener = new CompositionSamplingListener(mSampledView.getContext().getMainExecutor()) {
            public void onSampleCollected(final float n) {
                if (RegionSamplingHelper.this.mSamplingEnabled) {
                    RegionSamplingHelper.this.updateMediaLuma(n);
                }
            }
        };
        (this.mSampledView = mSampledView).addOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
        this.mSampledView.addOnLayoutChangeListener((View$OnLayoutChangeListener)this);
        final Resources resources = mSampledView.getResources();
        this.mLuminanceThreshold = resources.getFloat(R$dimen.navigation_luminance_threshold);
        this.mLuminanceChangeThreshold = resources.getFloat(R$dimen.navigation_luminance_change_threshold);
        this.mCallback = mCallback;
    }
    
    private void onDraw() {
        if (this.mWaitingOnDraw) {
            this.mWaitingOnDraw = false;
            this.updateSamplingListener();
        }
    }
    
    private void unregisterSamplingListener() {
        if (this.mSamplingListenerRegistered) {
            this.mSamplingListenerRegistered = false;
            this.mRegisteredStopLayer = null;
            this.mRegisteredSamplingBounds.setEmpty();
            CompositionSamplingListener.unregister(this.mSamplingListener);
        }
    }
    
    private void updateMediaLuma(final float n) {
        this.mCurrentMedianLuma = n;
        if (Math.abs(n - this.mLastMedianLuma) > this.mLuminanceChangeThreshold) {
            this.mCallback.onRegionDarknessChanged(n < this.mLuminanceThreshold);
            this.mLastMedianLuma = n;
        }
    }
    
    private void updateSamplingListener() {
        if (this.mSamplingEnabled && !this.mSamplingRequestBounds.isEmpty() && this.mWindowVisible && (this.mSampledView.isAttachedToWindow() || this.mFirstSamplingAfterStart)) {
            final ViewRootImpl viewRootImpl = this.mSampledView.getViewRootImpl();
            final SurfaceControl surfaceControl = null;
            SurfaceControl surfaceControl2;
            if (viewRootImpl != null) {
                surfaceControl2 = viewRootImpl.getSurfaceControl();
            }
            else {
                surfaceControl2 = null;
            }
            if (surfaceControl2 == null || !surfaceControl2.isValid()) {
                surfaceControl2 = surfaceControl;
                if (!this.mWaitingOnDraw) {
                    this.mWaitingOnDraw = true;
                    if (this.mHandler.hasCallbacks(this.mRemoveDrawRunnable)) {
                        this.mHandler.removeCallbacks(this.mRemoveDrawRunnable);
                        surfaceControl2 = surfaceControl;
                    }
                    else {
                        this.mSampledView.getViewTreeObserver().addOnDrawListener(this.mUpdateOnDraw);
                        surfaceControl2 = surfaceControl;
                    }
                }
            }
            if (!this.mSamplingRequestBounds.equals((Object)this.mRegisteredSamplingBounds) || this.mRegisteredStopLayer != surfaceControl2) {
                this.unregisterSamplingListener();
                this.mSamplingListenerRegistered = true;
                CompositionSamplingListener.register(this.mSamplingListener, 0, surfaceControl2, this.mSamplingRequestBounds);
                this.mRegisteredSamplingBounds.set(this.mSamplingRequestBounds);
                this.mRegisteredStopLayer = surfaceControl2;
            }
            this.mFirstSamplingAfterStart = false;
        }
        else {
            this.unregisterSamplingListener();
        }
    }
    
    void dump(final PrintWriter printWriter) {
        printWriter.println("RegionSamplingHelper:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  sampleView isAttached: ");
        sb.append(this.mSampledView.isAttachedToWindow());
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  sampleView isScValid: ");
        Serializable value;
        if (this.mSampledView.isAttachedToWindow()) {
            value = this.mSampledView.getViewRootImpl().getSurfaceControl().isValid();
        }
        else {
            value = "false";
        }
        sb2.append(value);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mSamplingListenerRegistered: ");
        sb3.append(this.mSamplingListenerRegistered);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mSamplingRequestBounds: ");
        sb4.append(this.mSamplingRequestBounds);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mLastMedianLuma: ");
        sb5.append(this.mLastMedianLuma);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mCurrentMedianLuma: ");
        sb6.append(this.mCurrentMedianLuma);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  mWindowVisible: ");
        sb7.append(this.mWindowVisible);
        printWriter.println(sb7.toString());
    }
    
    public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        this.updateSamplingRect();
    }
    
    public void onViewAttachedToWindow(final View view) {
        this.updateSamplingListener();
    }
    
    public void onViewDetachedFromWindow(final View view) {
        this.stopAndDestroy();
    }
    
    void setWindowVisible(final boolean mWindowVisible) {
        this.mWindowVisible = mWindowVisible;
        this.updateSamplingListener();
    }
    
    void start(final Rect rect) {
        if (!this.mCallback.isSamplingEnabled()) {
            return;
        }
        if (rect != null) {
            this.mSamplingRequestBounds.set(rect);
        }
        this.mSamplingEnabled = true;
        this.mLastMedianLuma = -1.0f;
        this.mFirstSamplingAfterStart = true;
        this.updateSamplingListener();
    }
    
    void stop() {
        this.mSamplingEnabled = false;
        this.updateSamplingListener();
    }
    
    void stopAndDestroy() {
        this.stop();
        this.mSamplingListener.destroy();
    }
    
    public void updateSamplingRect() {
        final Rect sampledRegion = this.mCallback.getSampledRegion(this.mSampledView);
        if (!this.mSamplingRequestBounds.equals((Object)sampledRegion)) {
            this.mSamplingRequestBounds.set(sampledRegion);
            this.updateSamplingListener();
        }
    }
    
    public interface SamplingCallback
    {
        Rect getSampledRegion(final View p0);
        
        default boolean isSamplingEnabled() {
            return true;
        }
        
        void onRegionDarknessChanged(final boolean p0);
    }
}
