// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.internal.statusbar.StatusBarIcon;

public class StatusBarIconHolder
{
    private StatusBarIcon mIcon;
    private StatusBarSignalPolicy.MobileIconState mMobileState;
    private int mTag;
    private int mType;
    private StatusBarSignalPolicy.WifiIconState mWifiState;
    
    public StatusBarIconHolder() {
        this.mType = 0;
        this.mTag = 0;
    }
    
    public static StatusBarIconHolder fromIcon(final StatusBarIcon mIcon) {
        final StatusBarIconHolder statusBarIconHolder = new StatusBarIconHolder();
        statusBarIconHolder.mIcon = mIcon;
        return statusBarIconHolder;
    }
    
    public static StatusBarIconHolder fromMobileIconState(final StatusBarSignalPolicy.MobileIconState mMobileState) {
        final StatusBarIconHolder statusBarIconHolder = new StatusBarIconHolder();
        statusBarIconHolder.mMobileState = mMobileState;
        statusBarIconHolder.mType = 2;
        statusBarIconHolder.mTag = mMobileState.subId;
        return statusBarIconHolder;
    }
    
    public static StatusBarIconHolder fromWifiIconState(final StatusBarSignalPolicy.WifiIconState mWifiState) {
        final StatusBarIconHolder statusBarIconHolder = new StatusBarIconHolder();
        statusBarIconHolder.mWifiState = mWifiState;
        statusBarIconHolder.mType = 1;
        return statusBarIconHolder;
    }
    
    public StatusBarIcon getIcon() {
        return this.mIcon;
    }
    
    public StatusBarSignalPolicy.MobileIconState getMobileState() {
        return this.mMobileState;
    }
    
    public int getTag() {
        return this.mTag;
    }
    
    public int getType() {
        return this.mType;
    }
    
    public StatusBarSignalPolicy.WifiIconState getWifiState() {
        return this.mWifiState;
    }
    
    public boolean isVisible() {
        final int mType = this.mType;
        if (mType == 0) {
            return this.mIcon.visible;
        }
        if (mType != 1) {
            return mType != 2 || this.mMobileState.visible;
        }
        return this.mWifiState.visible;
    }
    
    public void setMobileState(final StatusBarSignalPolicy.MobileIconState mMobileState) {
        this.mMobileState = mMobileState;
    }
    
    public void setVisible(final boolean visible) {
        if (this.isVisible() == visible) {
            return;
        }
        final int mType = this.mType;
        if (mType != 0) {
            if (mType != 1) {
                if (mType == 2) {
                    this.mMobileState.visible = visible;
                }
            }
            else {
                this.mWifiState.visible = visible;
            }
        }
        else {
            this.mIcon.visible = visible;
        }
    }
    
    public void setWifiState(final StatusBarSignalPolicy.WifiIconState mWifiState) {
        this.mWifiState = mWifiState;
    }
}
