// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import android.media.MediaRoute2Info;

public class MediaDeviceUtils
{
    public static String getId(final MediaRoute2Info mediaRoute2Info) {
        return mediaRoute2Info.getId();
    }
    
    public static String getId(final CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice.isHearingAidDevice()) {
            return Long.toString(cachedBluetoothDevice.getHiSyncId());
        }
        return cachedBluetoothDevice.getAddress();
    }
}
