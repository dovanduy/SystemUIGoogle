// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.wakelock;

import android.content.Context;
import android.os.Handler;

public class DelayedWakeLock implements WakeLock
{
    private final Handler mHandler;
    private final WakeLock mInner;
    
    public DelayedWakeLock(final Handler mHandler, final WakeLock mInner) {
        this.mHandler = mHandler;
        this.mInner = mInner;
    }
    
    @Override
    public void acquire(final String s) {
        this.mInner.acquire(s);
    }
    
    @Override
    public void release(final String s) {
        this.mHandler.postDelayed((Runnable)new _$$Lambda$DelayedWakeLock$aTG9u0wfrNahXJF_VixBxfvFqfg(this, s), 100L);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[DelayedWakeLock] ");
        sb.append(this.mInner);
        return sb.toString();
    }
    
    @Override
    public Runnable wrap(final Runnable runnable) {
        return WakeLock.wrapImpl(this, runnable);
    }
    
    public static class Builder
    {
        private final Context mContext;
        private Handler mHandler;
        private String mTag;
        
        public Builder(final Context mContext) {
            this.mContext = mContext;
        }
        
        public DelayedWakeLock build() {
            return new DelayedWakeLock(this.mHandler, WakeLock.createPartial(this.mContext, this.mTag));
        }
        
        public Builder setHandler(final Handler mHandler) {
            this.mHandler = mHandler;
            return this;
        }
        
        public Builder setTag(final String mTag) {
            this.mTag = mTag;
            return this;
        }
    }
}
