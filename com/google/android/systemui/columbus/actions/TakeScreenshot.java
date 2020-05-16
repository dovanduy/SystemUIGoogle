// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import java.util.function.Consumer;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.android.internal.util.ScreenshotHelper;
import android.os.Handler;

public final class TakeScreenshot extends Action
{
    private final Handler handler;
    private final ScreenshotHelper screenshotHelper;
    
    public TakeScreenshot(final Context context, final Handler handler) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        super(context, null);
        this.handler = handler;
        this.screenshotHelper = new ScreenshotHelper(context);
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
        this.screenshotHelper.takeScreenshot(1, true, true, this.handler, (Consumer)null);
    }
}
