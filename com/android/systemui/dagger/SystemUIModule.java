// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.systemui.model.SysUiState;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import com.android.systemui.dump.DumpManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.content.Context;

public abstract class SystemUIModule
{
    static KeyguardLiftController provideKeyguardLiftController(final Context context, final StatusBarStateController statusBarStateController, final AsyncSensorManager asyncSensorManager, final KeyguardUpdateMonitor keyguardUpdateMonitor, final DumpManager dumpManager) {
        if (!context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            return null;
        }
        return new KeyguardLiftController(statusBarStateController, asyncSensorManager, keyguardUpdateMonitor, dumpManager);
    }
    
    static SysUiState provideSysUiState() {
        return new SysUiState();
    }
}
