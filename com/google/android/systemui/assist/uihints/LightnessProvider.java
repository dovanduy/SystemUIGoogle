// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.SurfaceControl;
import android.graphics.Rect;
import java.util.concurrent.Executor;
import android.os.Looper;
import android.os.Handler;
import android.view.CompositionSamplingListener;

final class LightnessProvider implements CardInfoListener
{
    private boolean mCardVisible;
    private int mColorMode;
    private final CompositionSamplingListener mColorMonitor;
    private boolean mIsMonitoringColor;
    private LightnessListener mListener;
    private boolean mMuted;
    private final Handler mUiHandler;
    
    LightnessProvider() {
        this.mCardVisible = false;
        this.mColorMode = 0;
        this.mIsMonitoringColor = false;
        this.mMuted = false;
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mColorMonitor = new CompositionSamplingListener((Executor)_$$Lambda$_14QHG018Z6p13d3hzJuGTWnNeo.INSTANCE) {
            public void onSampleCollected(final float n) {
                LightnessProvider.this.mUiHandler.post((Runnable)new _$$Lambda$LightnessProvider$1$XlZA6YE6nJBV8mUm2Sl59Ijw5dc(this, n));
            }
        };
    }
    
    void enableColorMonitoring(final boolean mIsMonitoringColor, final Rect rect, final SurfaceControl surfaceControl) {
        if (this.mIsMonitoringColor == mIsMonitoringColor) {
            return;
        }
        this.mIsMonitoringColor = mIsMonitoringColor;
        if (mIsMonitoringColor) {
            CompositionSamplingListener.register(this.mColorMonitor, 0, surfaceControl, rect);
        }
        else {
            CompositionSamplingListener.unregister(this.mColorMonitor);
        }
    }
    
    @Override
    public void onCardInfo(final boolean b, final int n, final boolean b2, final boolean b3) {
        this.setCardVisible(b, n);
    }
    
    void setCardVisible(final boolean mCardVisible, final int mColorMode) {
        this.mCardVisible = mCardVisible;
        this.mColorMode = mColorMode;
        final LightnessListener mListener = this.mListener;
        if (mListener != null && mCardVisible) {
            if (mColorMode == 1) {
                mListener.onLightnessUpdate(0.0f);
            }
            else if (mColorMode == 2) {
                mListener.onLightnessUpdate(1.0f);
            }
        }
    }
    
    void setListener(final LightnessListener mListener) {
        this.mListener = mListener;
    }
    
    void setMuted(final boolean mMuted) {
        this.mMuted = mMuted;
    }
}
