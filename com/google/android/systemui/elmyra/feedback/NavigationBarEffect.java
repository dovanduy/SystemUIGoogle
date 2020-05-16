// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.android.systemui.statusbar.phone.NavigationBarView;
import java.util.Collection;
import java.util.ArrayList;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.List;

public abstract class NavigationBarEffect implements FeedbackEffect
{
    private final List<FeedbackEffect> mFeedbackEffects;
    private final StatusBar mStatusBar;
    
    public NavigationBarEffect(final StatusBar mStatusBar) {
        this.mFeedbackEffects = new ArrayList<FeedbackEffect>();
        this.mStatusBar = mStatusBar;
    }
    
    private void refreshFeedbackEffects() {
        final NavigationBarView navigationBarView = this.mStatusBar.getNavigationBarView();
        if (navigationBarView == null) {
            this.mFeedbackEffects.clear();
            return;
        }
        if (!this.validateFeedbackEffects(this.mFeedbackEffects)) {
            this.mFeedbackEffects.clear();
        }
        if (this.mFeedbackEffects.isEmpty()) {
            this.mFeedbackEffects.addAll(this.findFeedbackEffects(navigationBarView));
        }
    }
    
    protected abstract List<FeedbackEffect> findFeedbackEffects(final NavigationBarView p0);
    
    protected abstract boolean isActiveFeedbackEffect(final FeedbackEffect p0);
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.refreshFeedbackEffects();
        for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
            final FeedbackEffect feedbackEffect = this.mFeedbackEffects.get(i);
            if (this.isActiveFeedbackEffect(feedbackEffect)) {
                feedbackEffect.onProgress(n, n2);
            }
        }
    }
    
    @Override
    public void onRelease() {
        this.refreshFeedbackEffects();
        for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
            this.mFeedbackEffects.get(i).onRelease();
        }
    }
    
    @Override
    public void onResolve(final GestureSensor.DetectionProperties detectionProperties) {
        this.refreshFeedbackEffects();
        for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
            this.mFeedbackEffects.get(i).onResolve(detectionProperties);
        }
    }
    
    protected abstract boolean validateFeedbackEffects(final List<FeedbackEffect> p0);
}
