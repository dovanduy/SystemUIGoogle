// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.util.Log;
import android.os.Looper;
import com.android.systemui.assist.AssistManager;
import dagger.Lazy;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

class TimeoutManager implements KeepAliveListener
{
    private static final long SESSION_TIMEOUT_MS;
    private final Handler mHandler;
    private final Runnable mOnTimeout;
    private TimeoutCallback mTimeoutCallback;
    
    static {
        SESSION_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(10L);
    }
    
    TimeoutManager(final Lazy<AssistManager> lazy) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mOnTimeout = new _$$Lambda$TimeoutManager$X4RuadESl_Hu9ltHRqLw30HWJ8U(this, lazy);
    }
    
    @Override
    public void onKeepAlive(final String s) {
        this.resetTimeout();
    }
    
    void resetTimeout() {
        this.mHandler.removeCallbacks(this.mOnTimeout);
        this.mHandler.postDelayed(this.mOnTimeout, TimeoutManager.SESSION_TIMEOUT_MS);
    }
    
    void setTimeoutCallback(final TimeoutCallback mTimeoutCallback) {
        this.mTimeoutCallback = mTimeoutCallback;
    }
    
    interface TimeoutCallback
    {
        void onTimeout();
    }
}
