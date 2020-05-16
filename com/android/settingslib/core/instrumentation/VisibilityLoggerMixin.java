// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.core.instrumentation;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.SystemClock;
import com.android.settingslib.core.lifecycle.events.OnAttach;
import com.android.settingslib.core.lifecycle.LifecycleObserver;

public class VisibilityLoggerMixin implements LifecycleObserver, OnAttach
{
    private long mCreationTimestamp;
    private final int mMetricsCategory;
    private MetricsFeatureProvider mMetricsFeature;
    private int mSourceMetricsCategory;
    private long mVisibleTimestamp;
    
    @Override
    public void onAttach() {
        this.mCreationTimestamp = SystemClock.elapsedRealtime();
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mCreationTimestamp = 0L;
        if (this.mMetricsFeature != null && this.mMetricsCategory != 0) {
            this.mMetricsFeature.hidden(null, this.mMetricsCategory, (int)(SystemClock.elapsedRealtime() - this.mVisibleTimestamp));
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mMetricsFeature != null) {
            if (this.mMetricsCategory != 0) {
                final long elapsedRealtime = SystemClock.elapsedRealtime();
                this.mVisibleTimestamp = elapsedRealtime;
                final long mCreationTimestamp = this.mCreationTimestamp;
                if (mCreationTimestamp != 0L) {
                    this.mMetricsFeature.visible(null, this.mSourceMetricsCategory, this.mMetricsCategory, (int)(elapsedRealtime - mCreationTimestamp));
                }
                else {
                    this.mMetricsFeature.visible(null, this.mSourceMetricsCategory, this.mMetricsCategory, 0);
                }
            }
        }
    }
}
