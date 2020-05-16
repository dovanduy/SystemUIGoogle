// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.sensors.config;

import com.android.systemui.Dependency;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.R$dimen;
import android.util.TypedValue;
import android.content.Context;
import android.os.PowerManager;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

public class ScreenStateAdjustment extends Adjustment
{
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private final PowerManager mPowerManager;
    private final float mScreenOffAdjustment;
    
    public ScreenStateAdjustment(final Context context) {
        super(context);
        this.mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onFinishedGoingToSleep(final int n) {
                ScreenStateAdjustment.this.onSensitivityChanged();
            }
            
            @Override
            public void onStartedWakingUp() {
                ScreenStateAdjustment.this.onSensitivityChanged();
            }
        };
        this.mPowerManager = (PowerManager)this.getContext().getSystemService("power");
        final TypedValue typedValue = new TypedValue();
        context.getResources().getValue(R$dimen.elmyra_screen_off_adjustment, typedValue, true);
        this.mScreenOffAdjustment = typedValue.getFloat();
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mKeyguardUpdateMonitorCallback);
    }
    
    @Override
    public float adjustSensitivity(final float n) {
        float n2 = n;
        if (!this.mPowerManager.isInteractive()) {
            n2 = n + this.mScreenOffAdjustment;
        }
        return n2;
    }
}
