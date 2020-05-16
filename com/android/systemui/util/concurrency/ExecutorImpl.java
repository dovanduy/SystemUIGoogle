// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import android.os.Message;
import android.os.Handler$Callback;
import android.os.Looper;
import android.os.Handler;

public class ExecutorImpl implements DelayableExecutor
{
    private final Handler mHandler;
    
    ExecutorImpl(final Looper looper) {
        this.mHandler = new Handler(looper, (Handler$Callback)new _$$Lambda$ExecutorImpl$vXdc7rv1NdEmVmxIWaGxknUGa10(this));
    }
    
    private boolean onHandleMessage(final Message message) {
        if (message.what == 0) {
            ((ExecutionToken)message.obj).runnable.run();
            return true;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unrecognized message: ");
        sb.append(message.what);
        throw new IllegalStateException(sb.toString());
    }
    
    @Override
    public void execute(final Runnable runnable) {
        if (this.mHandler.post(runnable)) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(this.mHandler);
        sb.append(" is shutting down");
        throw new RejectedExecutionException(sb.toString());
    }
    
    @Override
    public Runnable executeDelayed(final Runnable runnable, final long duration, final TimeUnit timeUnit) {
        final ExecutionToken executionToken = new ExecutionToken(runnable);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, (Object)executionToken), timeUnit.toMillis(duration));
        return executionToken;
    }
    
    private class ExecutionToken implements Runnable
    {
        public final Runnable runnable;
        
        private ExecutionToken(final Runnable runnable) {
            this.runnable = runnable;
        }
        
        @Override
        public void run() {
            ExecutorImpl.this.mHandler.removeCallbacksAndMessages((Object)this);
        }
    }
}
