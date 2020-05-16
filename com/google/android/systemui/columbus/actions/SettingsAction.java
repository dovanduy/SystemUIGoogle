// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;

public final class SettingsAction extends ServiceAction
{
    private final StatusBar statusBar;
    private final UserSelectedAction userSelectedAction;
    
    public SettingsAction(final Context context, final UserSelectedAction userSelectedAction, final StatusBar statusBar) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(userSelectedAction, "userSelectedAction");
        Intrinsics.checkParameterIsNotNull(statusBar, "statusBar");
        super(context, null);
        this.userSelectedAction = userSelectedAction;
        this.statusBar = statusBar;
    }
    
    @Override
    protected boolean checkSupportedCaller() {
        return this.checkSupportedCaller("com.android.settings");
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (n == 3) {
            this.statusBar.collapseShade();
        }
        super.onProgress(n, detectionProperties);
    }
    
    @Override
    protected void triggerAction() {
        if (this.userSelectedAction.isAvailable()) {
            this.userSelectedAction.onTrigger();
        }
    }
}
