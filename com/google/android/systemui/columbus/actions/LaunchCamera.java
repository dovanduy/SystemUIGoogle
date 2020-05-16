// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import android.os.UserHandle;
import android.content.Intent;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;

public final class LaunchCamera extends Action
{
    private final boolean cameraAvailable;
    
    public LaunchCamera(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, null);
        this.cameraAvailable = context.getPackageManager().hasSystemFeature("android.hardware.camera");
    }
    
    @Override
    public boolean isAvailable() {
        return this.cameraAvailable;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (n == 3) {
            this.onTrigger();
        }
    }
    
    @Override
    public void onTrigger() {
        new Intent("android.media.action.IMAGE_CAPTURE").setFlags(268468224);
        this.getContext().startActivityAsUser(new Intent("android.media.action.IMAGE_CAPTURE"), UserHandle.of(-2));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [cameraAvailable -> ");
        sb.append(this.cameraAvailable);
        sb.append("]");
        return sb.toString();
    }
}
