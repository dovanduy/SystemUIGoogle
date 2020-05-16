// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.view.SurfaceControl;
import android.os.IBinder;
import android.os.Bundle;

public class SurfaceViewRequestUtils
{
    public static int getDisplayId(final Bundle bundle) {
        return bundle.getInt("display_id");
    }
    
    public static IBinder getHostToken(final Bundle bundle) {
        return bundle.getBinder("host_token");
    }
    
    public static SurfaceControl getSurfaceControl(final Bundle bundle) {
        return (SurfaceControl)bundle.getParcelable("surface_control");
    }
}
