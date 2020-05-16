// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NavigationBarController;

public class NavUndimEffect implements FeedbackEffect
{
    private final NavigationBarController mNavBarController;
    
    public NavUndimEffect() {
        this.mNavBarController = Dependency.get(NavigationBarController.class);
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.mNavBarController.touchAutoDim(0);
    }
    
    @Override
    public void onRelease() {
        this.mNavBarController.touchAutoDim(0);
    }
    
    @Override
    public void onResolve(final GestureSensor.DetectionProperties detectionProperties) {
        this.mNavBarController.touchAutoDim(0);
    }
}
