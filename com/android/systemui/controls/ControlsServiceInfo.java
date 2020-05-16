// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls;

import kotlin.jvm.internal.Intrinsics;
import android.content.pm.ServiceInfo;
import android.content.Context;
import com.android.settingslib.applications.DefaultAppInfo;

public final class ControlsServiceInfo extends DefaultAppInfo
{
    public ControlsServiceInfo(final Context context, final ServiceInfo serviceInfo) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(serviceInfo, "serviceInfo");
        super(context, context.getPackageManager(), context.getUserId(), serviceInfo.getComponentName());
    }
}
