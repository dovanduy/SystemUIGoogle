// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.util.DeviceConfigProxy;

public final class ForegroundServiceDismissalFeatureControllerKt
{
    private static Boolean sIsEnabled;
    
    private static final boolean isEnabled(final DeviceConfigProxy deviceConfigProxy) {
        if (ForegroundServiceDismissalFeatureControllerKt.sIsEnabled == null) {
            ForegroundServiceDismissalFeatureControllerKt.sIsEnabled = deviceConfigProxy.getBoolean("systemui", "notifications_allow_fgs_dismissal", false);
        }
        final Boolean sIsEnabled = ForegroundServiceDismissalFeatureControllerKt.sIsEnabled;
        if (sIsEnabled != null) {
            return sIsEnabled;
        }
        Intrinsics.throwNpe();
        throw null;
    }
}
