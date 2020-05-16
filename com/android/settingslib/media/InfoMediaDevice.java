// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import com.android.settingslib.bluetooth.BluetoothUtils;
import android.graphics.drawable.Drawable;
import com.android.settingslib.R$drawable;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.content.Context;

public class InfoMediaDevice extends MediaDevice
{
    InfoMediaDevice(final Context context, final MediaRouter2Manager mediaRouter2Manager, final MediaRoute2Info mediaRoute2Info, final String s) {
        super(context, 2, mediaRouter2Manager, mediaRoute2Info, s);
        this.initDeviceRecord();
    }
    
    int getDrawableResId() {
        int n;
        if (super.mRouteInfo.getType() != 2000) {
            n = R$drawable.ic_media_device;
        }
        else {
            n = R$drawable.ic_media_group_device;
        }
        return n;
    }
    
    @Override
    public Drawable getIcon() {
        final Context mContext = super.mContext;
        return BluetoothUtils.buildBtRainbowDrawable(mContext, mContext.getDrawable(this.getDrawableResId()), this.getId().hashCode());
    }
    
    @Override
    public String getId() {
        return MediaDeviceUtils.getId(super.mRouteInfo);
    }
    
    @Override
    public String getName() {
        return super.mRouteInfo.getName().toString();
    }
    
    @Override
    public boolean isConnected() {
        return true;
    }
}
