// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import android.util.Log;
import com.android.systemui.doze.DozeHost;
import android.os.Handler;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.plugins.statusbar.StatusBarStateController;

public class DozeScrimController implements StateListener
{
    private static final boolean DEBUG;
    private final DozeLog mDozeLog;
    private final DozeParameters mDozeParameters;
    private boolean mDozing;
    private boolean mFullyPulsing;
    private final Handler mHandler;
    private DozeHost.PulseCallback mPulseCallback;
    private final Runnable mPulseOut;
    private final Runnable mPulseOutExtended;
    private int mPulseReason;
    private final ScrimController.Callback mScrimCallback;
    
    static {
        DEBUG = Log.isLoggable("DozeScrimController", 3);
    }
    
    public DozeScrimController(final DozeParameters mDozeParameters, final DozeLog mDozeLog) {
        this.mHandler = new Handler();
        this.mScrimCallback = new ScrimController.Callback() {
            @Override
            public void onCancelled() {
                DozeScrimController.this.pulseFinished();
            }
            
            @Override
            public void onDisplayBlanked() {
                if (DozeScrimController.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Pulse in, mDozing=");
                    sb.append(DozeScrimController.this.mDozing);
                    sb.append(" mPulseReason=");
                    sb.append(DozeLog.reasonToString(DozeScrimController.this.mPulseReason));
                    Log.d("DozeScrimController", sb.toString());
                }
                if (!DozeScrimController.this.mDozing) {
                    return;
                }
                DozeScrimController.this.pulseStarted();
            }
            
            @Override
            public void onFinished() {
                if (DozeScrimController.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Pulse in finished, mDozing=");
                    sb.append(DozeScrimController.this.mDozing);
                    Log.d("DozeScrimController", sb.toString());
                }
                if (!DozeScrimController.this.mDozing) {
                    return;
                }
                if (DozeScrimController.this.mPulseReason != 1 && DozeScrimController.this.mPulseReason != 6) {
                    DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseOut, (long)DozeScrimController.this.mDozeParameters.getPulseVisibleDuration());
                    DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseOutExtended, (long)DozeScrimController.this.mDozeParameters.getPulseVisibleDurationExtended());
                }
                DozeScrimController.this.mFullyPulsing = true;
            }
        };
        this.mPulseOutExtended = new Runnable() {
            @Override
            public void run() {
                DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
                DozeScrimController.this.mPulseOut.run();
            }
        };
        this.mPulseOut = new Runnable() {
            @Override
            public void run() {
                DozeScrimController.this.mFullyPulsing = false;
                DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
                DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOutExtended);
                if (DozeScrimController.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Pulse out, mDozing=");
                    sb.append(DozeScrimController.this.mDozing);
                    Log.d("DozeScrimController", sb.toString());
                }
                if (!DozeScrimController.this.mDozing) {
                    return;
                }
                DozeScrimController.this.pulseFinished();
            }
        };
        this.mDozeParameters = mDozeParameters;
        Dependency.get(StatusBarStateController.class).addCallback((StatusBarStateController.StateListener)this);
        this.mDozeLog = mDozeLog;
    }
    
    private void cancelPulsing() {
        if (this.mPulseCallback != null) {
            if (DozeScrimController.DEBUG) {
                Log.d("DozeScrimController", "Cancel pulsing");
            }
            this.mFullyPulsing = false;
            this.mHandler.removeCallbacks(this.mPulseOut);
            this.mHandler.removeCallbacks(this.mPulseOutExtended);
            this.pulseFinished();
        }
    }
    
    private void pulseFinished() {
        this.mDozeLog.tracePulseFinish();
        final DozeHost.PulseCallback mPulseCallback = this.mPulseCallback;
        if (mPulseCallback != null) {
            mPulseCallback.onPulseFinished();
            this.mPulseCallback = null;
        }
    }
    
    private void pulseStarted() {
        this.mDozeLog.tracePulseStart(this.mPulseReason);
        final DozeHost.PulseCallback mPulseCallback = this.mPulseCallback;
        if (mPulseCallback != null) {
            mPulseCallback.onPulseStarted();
        }
    }
    
    public void cancelPendingPulseTimeout() {
        this.mHandler.removeCallbacks(this.mPulseOut);
        this.mHandler.removeCallbacks(this.mPulseOutExtended);
    }
    
    public void extendPulse() {
        this.mHandler.removeCallbacks(this.mPulseOut);
    }
    
    public ScrimController.Callback getScrimCallback() {
        return this.mScrimCallback;
    }
    
    public boolean isPulsing() {
        return this.mPulseCallback != null;
    }
    
    @Override
    public void onDozingChanged(final boolean dozing) {
        this.setDozing(dozing);
    }
    
    @Override
    public void onStateChanged(final int n) {
    }
    
    public void pulse(final DozeHost.PulseCallback mPulseCallback, final int mPulseReason) {
        if (mPulseCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        if (this.mDozing && this.mPulseCallback == null) {
            this.mPulseCallback = mPulseCallback;
            this.mPulseReason = mPulseReason;
            return;
        }
        if (DozeScrimController.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Pulse supressed. Dozing: ");
            sb.append(this.mDozeParameters);
            sb.append(" had callback? ");
            sb.append(this.mPulseCallback != null);
            Log.d("DozeScrimController", sb.toString());
        }
        mPulseCallback.onPulseFinished();
    }
    
    public void pulseOutNow() {
        if (this.mPulseCallback != null && this.mFullyPulsing) {
            this.mPulseOut.run();
        }
    }
    
    @VisibleForTesting
    public void setDozing(final boolean mDozing) {
        if (this.mDozing == mDozing) {
            return;
        }
        if (!(this.mDozing = mDozing)) {
            this.cancelPulsing();
        }
    }
}
