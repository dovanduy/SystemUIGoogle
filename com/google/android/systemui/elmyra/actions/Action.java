// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import java.util.Collection;
import java.util.ArrayList;
import android.os.Handler;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;

public abstract class Action
{
    private final Context mContext;
    private final List<FeedbackEffect> mFeedbackEffects;
    private final Handler mHandler;
    private Listener mListener;
    
    public Action(final Context mContext, final List<FeedbackEffect> list) {
        this.mFeedbackEffects = new ArrayList<FeedbackEffect>();
        this.mContext = mContext;
        this.mHandler = new Handler(mContext.getMainLooper());
        if (list != null) {
            this.mFeedbackEffects.addAll(list);
        }
    }
    
    protected Context getContext() {
        return this.mContext;
    }
    
    public abstract boolean isAvailable();
    
    protected void notifyListener() {
        if (this.mListener != null) {
            this.mHandler.post((Runnable)new _$$Lambda$Action$j2J8_IgWsMdJmJbAPdwLJPf2ZWA(this));
        }
        if (!this.isAvailable()) {
            this.mHandler.post((Runnable)new _$$Lambda$Action$065n3tshnSDLPbdPQiUaqEYgAYY(this));
        }
    }
    
    public void onProgress(final float n, final int n2) {
    }
    
    public abstract void onTrigger(final GestureSensor.DetectionProperties p0);
    
    public void setListener(final Listener mListener) {
        this.mListener = mListener;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    protected void triggerFeedbackEffects(final GestureSensor.DetectionProperties detectionProperties) {
        if (!this.isAvailable()) {
            return;
        }
        for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
            this.mFeedbackEffects.get(i).onResolve(detectionProperties);
        }
    }
    
    protected void updateFeedbackEffects(final float n, final int n2) {
        final int n3 = 0;
        final int n4 = 0;
        int i = n3;
        if (n != 0.0f) {
            if (n2 == 0) {
                i = n3;
            }
            else {
                if (this.isAvailable()) {
                    for (int j = n4; j < this.mFeedbackEffects.size(); ++j) {
                        this.mFeedbackEffects.get(j).onProgress(n, n2);
                    }
                }
                return;
            }
        }
        while (i < this.mFeedbackEffects.size()) {
            this.mFeedbackEffects.get(i).onRelease();
            ++i;
        }
    }
    
    public interface Listener
    {
        void onActionAvailabilityChanged(final Action p0);
    }
}
