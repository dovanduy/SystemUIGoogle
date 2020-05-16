// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import android.os.UserHandle;
import android.content.Intent;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import java.util.Collections;
import com.android.systemui.Dependency;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.R$string;
import android.os.UserManager;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;

public class SetupWizardAction extends Action
{
    private boolean mDeviceInDemoMode;
    private final KeyguardDeferredSetup mKeyguardDeferredSetupGate;
    private final Gate.Listener mKeyguardDeferredSetupListener;
    private final LaunchOpa mLaunchOpa;
    private final SettingsAction mSettingsAction;
    private final String mSettingsPackageName;
    private final StatusBar mStatusBar;
    private boolean mUserCompletedSuw;
    private final KeyguardUpdateMonitorCallback mUserSwitchCallback;
    
    private SetupWizardAction(final Context context, final SettingsAction mSettingsAction, final LaunchOpa mLaunchOpa, final StatusBar mStatusBar) {
        super(context, null);
        this.mUserSwitchCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitching(final int n) {
                final SetupWizardAction this$0 = SetupWizardAction.this;
                this$0.mDeviceInDemoMode = UserManager.isDeviceInDemoMode(this$0.getContext());
                SetupWizardAction.this.notifyListener();
            }
        };
        this.mKeyguardDeferredSetupListener = new Gate.Listener() {
            @Override
            public void onGateChanged(final Gate gate) {
                SetupWizardAction.this.mUserCompletedSuw = ((KeyguardDeferredSetup)gate).isSuwComplete();
                SetupWizardAction.this.notifyListener();
            }
        };
        this.mSettingsPackageName = context.getResources().getString(R$string.settings_app_package_name);
        this.mSettingsAction = mSettingsAction;
        this.mLaunchOpa = mLaunchOpa;
        this.mStatusBar = mStatusBar;
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mUserSwitchCallback);
        (this.mKeyguardDeferredSetupGate = new KeyguardDeferredSetup(context, Collections.emptyList())).activate();
        this.mKeyguardDeferredSetupGate.setListener(this.mKeyguardDeferredSetupListener);
        this.mUserCompletedSuw = this.mKeyguardDeferredSetupGate.isSuwComplete();
    }
    
    @Override
    public boolean isAvailable() {
        final boolean mDeviceInDemoMode = this.mDeviceInDemoMode;
        final boolean b = false;
        if (mDeviceInDemoMode) {
            return false;
        }
        boolean b2 = b;
        if (this.mLaunchOpa.isAvailable()) {
            b2 = b;
            if (!this.mUserCompletedSuw) {
                b2 = b;
                if (!this.mSettingsAction.isAvailable()) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.updateFeedbackEffects(n, n2);
    }
    
    @Override
    public void onTrigger(final GestureSensor.DetectionProperties detectionProperties) {
        this.mStatusBar.collapseShade();
        this.triggerFeedbackEffects(detectionProperties);
        if (!this.mUserCompletedSuw && !this.mSettingsAction.isAvailable()) {
            final Intent intent = new Intent();
            intent.setAction("com.google.android.settings.ASSIST_GESTURE_TRAINING");
            intent.setPackage(this.mSettingsPackageName);
            intent.setFlags(268468224);
            this.getContext().startActivityAsUser(intent, UserHandle.of(-2));
        }
    }
    
    @Override
    protected void triggerFeedbackEffects(final GestureSensor.DetectionProperties detectionProperties) {
        super.triggerFeedbackEffects(detectionProperties);
        this.mLaunchOpa.triggerFeedbackEffects(detectionProperties);
    }
    
    @Override
    protected void updateFeedbackEffects(final float n, final int n2) {
        super.updateFeedbackEffects(n, n2);
        this.mLaunchOpa.updateFeedbackEffects(n, n2);
    }
    
    public static class Builder
    {
        private final Context mContext;
        private LaunchOpa mLaunchOpa;
        private SettingsAction mSettingsAction;
        private final StatusBar mStatusBar;
        
        public Builder(final Context mContext, final StatusBar mStatusBar) {
            this.mContext = mContext;
            this.mStatusBar = mStatusBar;
        }
        
        public SetupWizardAction build() {
            return new SetupWizardAction(this.mContext, this.mSettingsAction, this.mLaunchOpa, this.mStatusBar, null);
        }
        
        public Builder setLaunchOpa(final LaunchOpa mLaunchOpa) {
            this.mLaunchOpa = mLaunchOpa;
            return this;
        }
        
        public Builder setSettingsAction(final SettingsAction mSettingsAction) {
            this.mSettingsAction = mSettingsAction;
            return this;
        }
    }
}
