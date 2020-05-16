// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import android.os.Handler;
import android.content.Context;

public abstract class Gate
{
    private boolean mActive;
    private final Context mContext;
    private Listener mListener;
    private final Handler mNotifyHandler;
    
    public Gate(final Context mContext) {
        this.mContext = mContext;
        this.mNotifyHandler = new Handler(mContext.getMainLooper());
        this.mActive = false;
    }
    
    public void activate() {
        if (!this.isActive()) {
            this.mActive = true;
            this.onActivate();
        }
    }
    
    public void deactivate() {
        if (this.isActive()) {
            this.mActive = false;
            this.onDeactivate();
        }
    }
    
    protected Context getContext() {
        return this.mContext;
    }
    
    public final boolean isActive() {
        return this.mActive;
    }
    
    protected abstract boolean isBlocked();
    
    public final boolean isBlocking() {
        return this.isActive() && this.isBlocked();
    }
    
    protected void notifyListener() {
        if (this.isActive() && this.mListener != null) {
            this.mNotifyHandler.post((Runnable)new _$$Lambda$Gate$Zso7JKgyGOlwxNCrlJqPhKj2Wp4(this));
        }
    }
    
    protected abstract void onActivate();
    
    protected abstract void onDeactivate();
    
    public void setListener(final Listener mListener) {
        this.mListener = mListener;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    public interface Listener
    {
        void onGateChanged(final Gate p0);
    }
}
