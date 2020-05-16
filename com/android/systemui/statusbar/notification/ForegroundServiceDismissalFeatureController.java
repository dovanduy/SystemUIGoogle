// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.android.systemui.util.DeviceConfigProxy;

public final class ForegroundServiceDismissalFeatureController
{
    private final DeviceConfigProxy proxy;
    
    public ForegroundServiceDismissalFeatureController(final DeviceConfigProxy proxy, final Context context) {
        Intrinsics.checkParameterIsNotNull(proxy, "proxy");
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.proxy = proxy;
    }
    
    public final boolean isForegroundServiceDismissalEnabled() {
        return ForegroundServiceDismissalFeatureControllerKt.access$isEnabled(this.proxy);
    }
}
