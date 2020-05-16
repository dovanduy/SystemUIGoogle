// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.controls.ControlsServiceInfo;
import java.util.List;
import android.content.ComponentName;
import com.android.systemui.controls.UserAwareController;
import com.android.systemui.statusbar.policy.CallbackController;

public interface ControlsListingController extends CallbackController<ControlsListingCallback>, UserAwareController
{
    CharSequence getAppLabel(final ComponentName p0);
    
    @FunctionalInterface
    public interface ControlsListingCallback
    {
        void onServicesUpdated(final List<ControlsServiceInfo> p0);
    }
}
