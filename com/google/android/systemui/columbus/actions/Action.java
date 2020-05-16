// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import java.util.Iterator;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import kotlin.collections.CollectionsKt;
import android.os.Looper;
import kotlin.jvm.internal.Intrinsics;
import android.os.Handler;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;

public abstract class Action
{
    private final Context context;
    private final List<FeedbackEffect> feedbackEffects;
    private final Handler handler;
    private Listener listener;
    
    public Action(final Context context, List<? extends FeedbackEffect> emptyList) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        if (emptyList == null) {
            emptyList = CollectionsKt.emptyList();
        }
        this.feedbackEffects = CollectionsKt.toList((Iterable<? extends FeedbackEffect>)emptyList);
    }
    
    protected final Context getContext() {
        return this.context;
    }
    
    public Listener getListener() {
        return this.listener;
    }
    
    public abstract boolean isAvailable();
    
    protected final void notifyListener() {
        if (this.getListener() != null) {
            this.handler.post((Runnable)new Action$notifyListener.Action$notifyListener$1(this));
        }
        if (!this.isAvailable()) {
            this.handler.post((Runnable)new Action$notifyListener.Action$notifyListener$2(this));
        }
    }
    
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
    }
    
    public void onTrigger() {
    }
    
    public void setListener(final Listener listener) {
        this.listener = listener;
    }
    
    @Override
    public String toString() {
        final String simpleName = this.getClass().getSimpleName();
        Intrinsics.checkExpressionValueIsNotNull(simpleName, "javaClass.simpleName");
        return simpleName;
    }
    
    public void updateFeedbackEffects(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        final Iterator<FeedbackEffect> iterator = this.feedbackEffects.iterator();
        while (iterator.hasNext()) {
            iterator.next().onProgress(n, detectionProperties);
        }
    }
    
    public interface Listener
    {
        void onActionAvailabilityChanged(final Action p0);
    }
}
