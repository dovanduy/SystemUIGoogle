// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import android.bluetooth.BluetoothClass;
import com.android.settingslib.bluetooth.BluetoothUtils;
import android.graphics.drawable.Drawable;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.content.Context;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;

public class BluetoothMediaDevice extends MediaDevice
{
    private CachedBluetoothDevice mCachedDevice;
    
    BluetoothMediaDevice(final Context context, final CachedBluetoothDevice mCachedDevice, final MediaRouter2Manager mediaRouter2Manager, final MediaRoute2Info mediaRoute2Info, final String s) {
        super(context, 3, mediaRouter2Manager, mediaRoute2Info, s);
        this.mCachedDevice = mCachedDevice;
        this.initDeviceRecord();
    }
    
    public CachedBluetoothDevice getCachedDevice() {
        return this.mCachedDevice;
    }
    
    @Override
    public Drawable getIcon() {
        return (Drawable)BluetoothUtils.getBtRainbowDrawableWithDescription(super.mContext, this.mCachedDevice).first;
    }
    
    @Override
    public String getId() {
        return MediaDeviceUtils.getId(this.mCachedDevice);
    }
    
    @Override
    public String getName() {
        return this.mCachedDevice.getName();
    }
    
    @Override
    protected boolean isCarKitDevice() {
        final BluetoothClass bluetoothClass = this.mCachedDevice.getDevice().getBluetoothClass();
        if (bluetoothClass != null) {
            final int deviceClass = bluetoothClass.getDeviceClass();
            if (deviceClass == 1032 || deviceClass == 1056) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isConnected() {
        return this.mCachedDevice.getBondState() == 12 && this.mCachedDevice.isConnected();
    }
}
