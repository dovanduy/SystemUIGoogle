// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import com.android.systemui.Dependency;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.content.Context;
import android.os.PowerManager;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

public class PowerState extends Gate
{
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private final PowerManager mPowerManager;
    
    public PowerState(final Context context) {
        super(context);
        this.mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onFinishedGoingToSleep(final int n) {
                PowerState.this.notifyListener();
            }
            
            @Override
            public void onStartedWakingUp() {
                PowerState.this.notifyListener();
            }
        };
        this.mPowerManager = (PowerManager)context.getSystemService("power");
    }
    
    @Override
    protected boolean isBlocked() {
        return this.mPowerManager.isInteractive() ^ true;
    }
    
    @Override
    protected void onActivate() {
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mKeyguardUpdateMonitorCallback);
    }
    
    @Override
    protected void onDeactivate() {
        Dependency.get(KeyguardUpdateMonitor.class).removeCallback(this.mKeyguardUpdateMonitorCallback);
    }
}
