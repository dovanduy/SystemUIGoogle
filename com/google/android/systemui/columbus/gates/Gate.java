// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.os.Looper;
import kotlin.jvm.internal.Intrinsics;
import android.os.Handler;
import android.content.Context;

public abstract class Gate
{
    private boolean active;
    private final Context context;
    private Listener listener;
    private final Handler notifyHandler;
    
    public Gate(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.context = context;
        this.notifyHandler = new Handler(Looper.getMainLooper());
    }
    
    public void activate() {
        if (!this.active) {
            this.active = true;
            this.onActivate();
        }
    }
    
    public void deactivate() {
        if (this.active) {
            this.active = false;
            this.onDeactivate();
        }
    }
    
    public final boolean getActive() {
        return this.active;
    }
    
    public final Context getContext() {
        return this.context;
    }
    
    public Listener getListener() {
        return this.listener;
    }
    
    protected abstract boolean isBlocked();
    
    public boolean isBlocking() {
        return this.active && this.isBlocked();
    }
    
    protected final void notifyListener() {
        if (this.active && this.getListener() != null) {
            this.notifyHandler.post((Runnable)new Gate$notifyListener.Gate$notifyListener$1(this));
        }
    }
    
    protected abstract void onActivate();
    
    protected abstract void onDeactivate();
    
    public void setListener(final Listener listener) {
        this.listener = listener;
    }
    
    @Override
    public String toString() {
        final String simpleName = this.getClass().getSimpleName();
        Intrinsics.checkExpressionValueIsNotNull(simpleName, "javaClass.simpleName");
        return simpleName;
    }
    
    public interface Listener
    {
        void onGateChanged(final Gate p0);
    }
}
