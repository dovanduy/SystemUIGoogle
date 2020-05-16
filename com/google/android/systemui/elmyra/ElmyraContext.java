// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import android.os.Build;
import java.util.Arrays;
import android.content.Context;
import java.util.List;

public final class ElmyraContext
{
    private static final List<String> SUPPORTED_DEVICES;
    private Context mContext;
    
    static {
        SUPPORTED_DEVICES = Arrays.asList("walleye", "taimen", "blueline", "crosshatch", "bonito", "sargo", "coral", "flame");
    }
    
    public ElmyraContext(final Context mContext) {
        this.mContext = mContext;
    }
    
    public boolean isAvailable() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.sensor.assist") && ElmyraContext.SUPPORTED_DEVICES.contains(Build.DEVICE);
    }
}
