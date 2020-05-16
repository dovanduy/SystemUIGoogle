// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import android.os.UserHandle;
import android.content.Intent;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.gates.Gate;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup;

public final class SetupWizardAction extends Action
{
    private boolean deviceInDemoMode;
    private final KeyguardDeferredSetup keyguardDeferredSetupGate;
    private final SetupWizardAction$keyguardDeferredSetupListener.SetupWizardAction$keyguardDeferredSetupListener$1 keyguardDeferredSetupListener;
    private final SettingsAction settingsAction;
    private final StatusBar statusBar;
    private boolean userCompletedSuw;
    private final UserSelectedAction userSelectedAction;
    private final SetupWizardAction$userSwitchCallback.SetupWizardAction$userSwitchCallback$1 userSwitchCallback;
    
    public SetupWizardAction(final Context context, final SettingsAction settingsAction, final UserSelectedAction userSelectedAction, final KeyguardDeferredSetup keyguardDeferredSetupGate, final StatusBar statusBar, final KeyguardUpdateMonitor keyguardUpdateMonitor) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(settingsAction, "settingsAction");
        Intrinsics.checkParameterIsNotNull(userSelectedAction, "userSelectedAction");
        Intrinsics.checkParameterIsNotNull(keyguardDeferredSetupGate, "keyguardDeferredSetupGate");
        Intrinsics.checkParameterIsNotNull(statusBar, "statusBar");
        Intrinsics.checkParameterIsNotNull(keyguardUpdateMonitor, "keyguardUpdateMonitor");
        super(context, null);
        this.settingsAction = settingsAction;
        this.userSelectedAction = userSelectedAction;
        this.keyguardDeferredSetupGate = keyguardDeferredSetupGate;
        this.statusBar = statusBar;
        this.userSwitchCallback = new SetupWizardAction$userSwitchCallback.SetupWizardAction$userSwitchCallback$1(this, context);
        this.keyguardDeferredSetupListener = new SetupWizardAction$keyguardDeferredSetupListener.SetupWizardAction$keyguardDeferredSetupListener$1(this);
        this.userCompletedSuw = true;
        keyguardUpdateMonitor.registerCallback((KeyguardUpdateMonitorCallback)this.userSwitchCallback);
        this.keyguardDeferredSetupGate.activate();
        this.keyguardDeferredSetupGate.setListener((Gate.Listener)this.keyguardDeferredSetupListener);
    }
    
    @Override
    public boolean isAvailable() {
        return !this.deviceInDemoMode && this.userSelectedAction.isAssistant() && this.userSelectedAction.isAvailable() && !this.userCompletedSuw && !this.settingsAction.isAvailable();
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (n == 3) {
            this.statusBar.collapseShade();
            if (!this.userCompletedSuw && !this.settingsAction.isAvailable()) {
                final Intent intent = new Intent();
                intent.setAction("com.google.android.settings.COLUMBUS_GESTURE_TRAINING");
                intent.setPackage("com.android.settings");
                intent.setFlags(268468224);
                this.getContext().startActivityAsUser(intent, UserHandle.of(-2));
            }
        }
        this.updateFeedbackEffects(n, detectionProperties);
    }
    
    @Override
    public void updateFeedbackEffects(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        super.updateFeedbackEffects(n, detectionProperties);
        this.userSelectedAction.updateFeedbackEffects(n, detectionProperties);
    }
}
