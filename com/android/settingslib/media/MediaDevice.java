// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.media.MediaRouter2Manager;
import android.media.MediaRoute2Info;
import android.content.Context;

public abstract class MediaDevice implements Comparable<MediaDevice>
{
    private int mConnectedRecord;
    protected final Context mContext;
    protected final String mPackageName;
    protected final MediaRoute2Info mRouteInfo;
    protected final MediaRouter2Manager mRouterManager;
    private int mState;
    int mType;
    
    MediaDevice(final Context mContext, final int mType, final MediaRouter2Manager mRouterManager, final MediaRoute2Info mRouteInfo, final String mPackageName) {
        this.mType = mType;
        this.mContext = mContext;
        this.mRouteInfo = mRouteInfo;
        this.mRouterManager = mRouterManager;
        this.mPackageName = mPackageName;
    }
    
    @Override
    public int compareTo(final MediaDevice mediaDevice) {
        if (this.isConnected() ^ mediaDevice.isConnected()) {
            if (this.isConnected()) {
                return -1;
            }
            return 1;
        }
        else {
            if (this.mType == 1) {
                return -1;
            }
            if (mediaDevice.mType == 1) {
                return 1;
            }
            if (this.isCarKitDevice()) {
                return -1;
            }
            if (mediaDevice.isCarKitDevice()) {
                return 1;
            }
            final String lastSelectedDevice = ConnectionRecordManager.getInstance().getLastSelectedDevice();
            if (TextUtils.equals((CharSequence)lastSelectedDevice, (CharSequence)this.getId())) {
                return -1;
            }
            if (TextUtils.equals((CharSequence)lastSelectedDevice, (CharSequence)mediaDevice.getId())) {
                return 1;
            }
            final int mConnectedRecord = this.mConnectedRecord;
            final int mConnectedRecord2 = mediaDevice.mConnectedRecord;
            if (mConnectedRecord != mConnectedRecord2 && (mConnectedRecord2 > 0 || mConnectedRecord > 0)) {
                return mediaDevice.mConnectedRecord - this.mConnectedRecord;
            }
            final int mType = this.mType;
            final int mType2 = mediaDevice.mType;
            if (mType == mType2) {
                return this.getName().compareToIgnoreCase(mediaDevice.getName());
            }
            return mType - mType2;
        }
    }
    
    public boolean connect() {
        this.setConnectedRecord();
        this.mRouterManager.selectRoute(this.mPackageName, this.mRouteInfo);
        return true;
    }
    
    public void disconnect() {
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof MediaDevice && ((MediaDevice)o).getId().equals(this.getId());
    }
    
    public abstract Drawable getIcon();
    
    public abstract String getId();
    
    public abstract String getName();
    
    public int getState() {
        return this.mState;
    }
    
    void initDeviceRecord() {
        ConnectionRecordManager.getInstance().fetchLastSelectedDevice(this.mContext);
        this.mConnectedRecord = ConnectionRecordManager.getInstance().fetchConnectionRecord(this.mContext, this.getId());
    }
    
    protected boolean isCarKitDevice() {
        return false;
    }
    
    public abstract boolean isConnected();
    
    void setConnectedRecord() {
        ++this.mConnectedRecord;
        ConnectionRecordManager.getInstance().setConnectionRecord(this.mContext, this.getId(), this.mConnectedRecord);
    }
    
    public void setState(final int mState) {
        this.mState = mState;
    }
}
