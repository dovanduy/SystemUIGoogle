// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.android.systemui.recents.Recents;

public final class LaunchOverview extends Action
{
    private final Recents recents;
    
    public LaunchOverview(final Context context, final Recents recents) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(recents, "recents");
        super(context, null);
        this.recents = recents;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (n == 3) {
            this.onTrigger();
        }
    }
    
    @Override
    public void onTrigger() {
        this.recents.toggleRecentApps();
    }
}
