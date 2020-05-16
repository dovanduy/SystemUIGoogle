// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import com.android.systemui.Dependency;
import android.content.Context;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public class KeyguardVisibility extends Gate
{
    private final KeyguardStateController.Callback mKeyguardMonitorCallback;
    private final KeyguardStateController mKeyguardStateController;
    
    public KeyguardVisibility(final Context context) {
        super(context);
        this.mKeyguardMonitorCallback = new KeyguardStateController.Callback() {
            @Override
            public void onKeyguardShowingChanged() {
                KeyguardVisibility.this.notifyListener();
            }
        };
        this.mKeyguardStateController = Dependency.get(KeyguardStateController.class);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.isKeyguardShowing();
    }
    
    public boolean isKeyguardOccluded() {
        return this.mKeyguardStateController.isOccluded();
    }
    
    public boolean isKeyguardShowing() {
        return this.mKeyguardStateController.isShowing();
    }
    
    @Override
    protected void onActivate() {
        this.mKeyguardStateController.addCallback(this.mKeyguardMonitorCallback);
    }
    
    @Override
    protected void onDeactivate() {
        this.mKeyguardStateController.removeCallback(this.mKeyguardMonitorCallback);
    }
}
