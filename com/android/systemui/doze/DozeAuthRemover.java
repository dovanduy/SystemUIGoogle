// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.Dependency;
import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitor;

public class DozeAuthRemover implements Part
{
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    
    public DozeAuthRemover(final Context context) {
        this.mKeyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class);
    }
    
    @Override
    public void transitionTo(final State state, final State state2) {
        if ((state2 == State.DOZE || state2 == State.DOZE_AOD) && this.mKeyguardUpdateMonitor.getUserUnlockedWithBiometric(KeyguardUpdateMonitor.getCurrentUser())) {
            this.mKeyguardUpdateMonitor.clearBiometricRecognized();
        }
    }
}
