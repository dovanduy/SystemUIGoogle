// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public class KeyguardVisibility extends Gate
{
    private final KeyguardVisibility$keyguardMonitorCallback.KeyguardVisibility$keyguardMonitorCallback$1 keyguardMonitorCallback;
    private final KeyguardStateController keyguardStateController;
    
    public KeyguardVisibility(final Context context, final KeyguardStateController keyguardStateController) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(keyguardStateController, "keyguardStateController");
        super(context);
        this.keyguardStateController = keyguardStateController;
        this.keyguardMonitorCallback = new KeyguardVisibility$keyguardMonitorCallback.KeyguardVisibility$keyguardMonitorCallback$1(this);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.isKeyguardShowing();
    }
    
    public final boolean isKeyguardOccluded() {
        return this.keyguardStateController.isOccluded();
    }
    
    public final boolean isKeyguardShowing() {
        return this.keyguardStateController.isShowing();
    }
    
    @Override
    protected void onActivate() {
        this.keyguardStateController.addCallback((KeyguardStateController.Callback)this.keyguardMonitorCallback);
    }
    
    @Override
    protected void onDeactivate() {
        this.keyguardStateController.removeCallback((KeyguardStateController.Callback)this.keyguardMonitorCallback);
    }
}
