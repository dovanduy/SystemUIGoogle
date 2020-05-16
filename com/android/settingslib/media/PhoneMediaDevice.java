// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import com.android.settingslib.R$string;
import com.android.settingslib.bluetooth.BluetoothUtils;
import android.graphics.drawable.Drawable;
import com.android.settingslib.R$drawable;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.content.Context;

public class PhoneMediaDevice extends MediaDevice
{
    PhoneMediaDevice(final Context context, final MediaRouter2Manager mediaRouter2Manager, final MediaRoute2Info mediaRoute2Info, final String s) {
        super(context, 1, mediaRouter2Manager, mediaRoute2Info, s);
        this.initDeviceRecord();
    }
    
    int getDrawableResId() {
        final int type = super.mRouteInfo.getType();
        int ic_smartphone;
        if (type != 3 && type != 4) {
            ic_smartphone = R$drawable.ic_smartphone;
        }
        else {
            ic_smartphone = 17302320;
        }
        return ic_smartphone;
    }
    
    @Override
    public Drawable getIcon() {
        final Context mContext = super.mContext;
        return BluetoothUtils.buildBtRainbowDrawable(mContext, mContext.getDrawable(this.getDrawableResId()), this.getId().hashCode());
    }
    
    @Override
    public String getId() {
        return "phone_media_device_id_1";
    }
    
    @Override
    public String getName() {
        final int type = super.mRouteInfo.getType();
        CharSequence charSequence;
        if (type != 3 && type != 4) {
            charSequence = super.mContext.getString(R$string.media_transfer_this_device_name);
        }
        else {
            charSequence = super.mRouteInfo.getName();
        }
        return charSequence.toString();
    }
    
    @Override
    public boolean isConnected() {
        return true;
    }
}
