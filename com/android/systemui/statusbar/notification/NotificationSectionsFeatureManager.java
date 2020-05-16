// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.util.Utils;
import com.android.internal.annotations.VisibleForTesting;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;

public final class NotificationSectionsFeatureManager
{
    private final Context context;
    private final DeviceConfigProxy proxy;
    
    public NotificationSectionsFeatureManager(final DeviceConfigProxy proxy, final Context context) {
        Intrinsics.checkParameterIsNotNull(proxy, "proxy");
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.proxy = proxy;
        this.context = context;
    }
    
    @VisibleForTesting
    public final void clearCache() {
        NotificationSectionsFeatureManagerKt.access$setSUsePeopleFiltering$p(null);
    }
    
    public final int[] getNotificationBuckets() {
        int[] array;
        if (this.isFilteringEnabled() && this.isMediaControlsEnabled()) {
            final int[] array2;
            array = (array2 = new int[5]);
            array2[0] = 0;
            array2[1] = 1;
            array2[2] = 2;
            array2[3] = 3;
            array2[4] = 4;
        }
        else if (!this.isFilteringEnabled() && this.isMediaControlsEnabled()) {
            final int[] array3;
            array = (array3 = new int[4]);
            array3[0] = 0;
            array3[1] = 1;
            array3[array3[2] = 3] = 4;
        }
        else if (this.isFilteringEnabled() && !this.isMediaControlsEnabled()) {
            final int[] array4;
            array = (array4 = new int[4]);
            array4[0] = 0;
            array4[1] = 2;
            array4[array4[2] = 3] = 4;
        }
        else if (NotificationUtils.useNewInterruptionModel(this.context)) {
            final int[] array5;
            array = (array5 = new int[2]);
            array5[0] = 3;
            array5[1] = 4;
        }
        else {
            array = new int[] { 3 };
        }
        return array;
    }
    
    public final int getNumberOfBuckets() {
        return this.getNotificationBuckets().length;
    }
    
    public final boolean isFilteringEnabled() {
        return NotificationSectionsFeatureManagerKt.access$usePeopleFiltering(this.proxy);
    }
    
    public final boolean isMediaControlsEnabled() {
        return Utils.useQsMediaPlayer(this.context);
    }
}
