// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.util.DeviceConfigProxy;

public final class NotificationSectionsFeatureManagerKt
{
    private static Boolean sUsePeopleFiltering;
    
    private static final boolean usePeopleFiltering(final DeviceConfigProxy deviceConfigProxy) {
        if (NotificationSectionsFeatureManagerKt.sUsePeopleFiltering == null) {
            NotificationSectionsFeatureManagerKt.sUsePeopleFiltering = deviceConfigProxy.getBoolean("systemui", "notifications_use_people_filtering", true);
        }
        final Boolean sUsePeopleFiltering = NotificationSectionsFeatureManagerKt.sUsePeopleFiltering;
        if (sUsePeopleFiltering != null) {
            return sUsePeopleFiltering;
        }
        Intrinsics.throwNpe();
        throw null;
    }
}
