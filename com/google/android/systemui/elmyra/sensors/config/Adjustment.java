// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.sensors.config;

import android.content.Context;
import java.util.function.Consumer;

public abstract class Adjustment
{
    private Consumer<Adjustment> mCallback;
    private final Context mContext;
    
    public Adjustment(final Context mContext) {
        this.mContext = mContext;
    }
    
    public abstract float adjustSensitivity(final float p0);
    
    protected Context getContext() {
        return this.mContext;
    }
    
    protected void onSensitivityChanged() {
        final Consumer<Adjustment> mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.accept(this);
        }
    }
    
    public void setCallback(final Consumer<Adjustment> mCallback) {
        this.mCallback = mCallback;
    }
}
