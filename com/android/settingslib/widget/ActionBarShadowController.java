// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import android.view.View$OnScrollChangeListener;
import android.view.View;
import androidx.lifecycle.LifecycleObserver;

public class ActionBarShadowController implements LifecycleObserver
{
    static final float ELEVATION_HIGH = 8.0f;
    static final float ELEVATION_LOW = 0.0f;
    private boolean mIsScrollWatcherAttached;
    ScrollChangeWatcher mScrollChangeWatcher;
    private View mScrollView;
    
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void attachScrollWatcher() {
        if (!this.mIsScrollWatcherAttached) {
            this.mIsScrollWatcherAttached = true;
            this.mScrollView.setOnScrollChangeListener((View$OnScrollChangeListener)this.mScrollChangeWatcher);
            this.mScrollChangeWatcher.updateDropShadow(this.mScrollView);
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void detachScrollWatcher() {
        this.mScrollView.setOnScrollChangeListener((View$OnScrollChangeListener)null);
        this.mIsScrollWatcherAttached = false;
    }
    
    final class ScrollChangeWatcher implements View$OnScrollChangeListener
    {
        public abstract void updateDropShadow(final View p0);
    }
}
