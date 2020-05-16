// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.NavigationBarController;

public final class NavUndimEffect implements FeedbackEffect
{
    private final NavigationBarController navBarController;
    
    public NavUndimEffect(final NavigationBarController navBarController) {
        Intrinsics.checkParameterIsNotNull(navBarController, "navBarController");
        this.navBarController = navBarController;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        this.navBarController.touchAutoDim(0);
    }
}
