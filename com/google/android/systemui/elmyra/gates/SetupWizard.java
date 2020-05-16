// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import com.android.systemui.Dependency;
import java.util.Collection;
import java.util.ArrayList;
import android.content.Context;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;

public class SetupWizard extends Gate
{
    private final List<Action> mExceptions;
    private final DeviceProvisionedController mProvisionedController;
    private final DeviceProvisionedController.DeviceProvisionedListener mProvisionedListener;
    private boolean mSetupComplete;
    
    public SetupWizard(final Context context, final List<Action> c) {
        super(context);
        this.mProvisionedListener = new DeviceProvisionedController.DeviceProvisionedListener() {
            private void updateSetupComplete() {
                final boolean access$000 = SetupWizard.this.isSetupComplete();
                if (access$000 != SetupWizard.this.mSetupComplete) {
                    SetupWizard.this.mSetupComplete = access$000;
                    SetupWizard.this.notifyListener();
                }
            }
            
            @Override
            public void onDeviceProvisionedChanged() {
                this.updateSetupComplete();
            }
            
            @Override
            public void onUserSetupChanged() {
                this.updateSetupComplete();
            }
        };
        this.mExceptions = new ArrayList<Action>(c);
        this.mProvisionedController = Dependency.get(DeviceProvisionedController.class);
    }
    
    private boolean isSetupComplete() {
        return this.mProvisionedController.isDeviceProvisioned() && this.mProvisionedController.isCurrentUserSetup();
    }
    
    @Override
    protected boolean isBlocked() {
        for (int i = 0; i < this.mExceptions.size(); ++i) {
            if (this.mExceptions.get(i).isAvailable()) {
                return false;
            }
        }
        return this.mSetupComplete ^ true;
    }
    
    @Override
    protected void onActivate() {
        this.mSetupComplete = this.isSetupComplete();
        this.mProvisionedController.addCallback(this.mProvisionedListener);
    }
    
    @Override
    protected void onDeactivate() {
        this.mProvisionedController.removeCallback(this.mProvisionedListener);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isDeviceProvisioned -> ");
        sb.append(this.mProvisionedController.isDeviceProvisioned());
        sb.append("; isCurrentUserSetup -> ");
        sb.append(this.mProvisionedController.isCurrentUserSetup());
        sb.append("]");
        return sb.toString();
    }
}
