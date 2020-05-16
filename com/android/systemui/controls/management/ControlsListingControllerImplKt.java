// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import kotlin.jvm.internal.Intrinsics;
import com.android.settingslib.applications.ServiceListing;
import android.content.Context;

public final class ControlsListingControllerImplKt
{
    private static final ServiceListing createServiceListing(final Context context) {
        final ServiceListing.Builder builder = new ServiceListing.Builder(context);
        builder.setIntentAction("android.service.controls.ControlsProviderService");
        builder.setPermission("android.permission.BIND_CONTROLS");
        builder.setNoun("Controls Provider");
        builder.setSetting("controls_providers");
        builder.setTag("controls_providers");
        final ServiceListing build = builder.build();
        Intrinsics.checkExpressionValueIsNotNull(build, "ServiceListing.Builder(c\u2026providers\")\n    }.build()");
        return build;
    }
}
