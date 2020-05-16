// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.os.Bundle;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import java.util.ArrayList;
import android.net.NetworkInfo;
import android.content.Context;
import androidx.annotation.Keep;

@Keep
public class TestAccessPointBuilder
{
    private static final int MAX_RSSI = -55;
    private static final int MIN_RSSI = -100;
    private String mBssid;
    Context mContext;
    private String mFqdn;
    private int mNetworkId;
    private NetworkInfo mNetworkInfo;
    private String mProviderFriendlyName;
    private int mRssi;
    private ArrayList<ScanResult> mScanResults;
    private ArrayList<TimestampedScoredNetwork> mScoredNetworkCache;
    private int mSecurity;
    private int mSpeed;
    private WifiConfiguration mWifiConfig;
    private WifiInfo mWifiInfo;
    private String ssid;
    
    @Keep
    public TestAccessPointBuilder(final Context mContext) {
        this.mBssid = null;
        this.mSpeed = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mNetworkId = -1;
        this.ssid = "TestSsid";
        this.mNetworkInfo = null;
        this.mFqdn = null;
        this.mProviderFriendlyName = null;
        this.mSecurity = 0;
        this.mContext = mContext;
    }
    
    @Keep
    public AccessPoint build() {
        final Bundle bundle = new Bundle();
        WifiConfiguration wifiConfiguration;
        if (this.mNetworkId != -1) {
            wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.networkId = this.mNetworkId;
            wifiConfiguration.BSSID = this.mBssid;
        }
        else {
            wifiConfiguration = null;
        }
        bundle.putString("key_ssid", this.ssid);
        bundle.putParcelable("key_config", (Parcelable)wifiConfiguration);
        bundle.putParcelable("key_networkinfo", (Parcelable)this.mNetworkInfo);
        bundle.putParcelable("key_wifiinfo", (Parcelable)this.mWifiInfo);
        final String mFqdn = this.mFqdn;
        if (mFqdn != null) {
            bundle.putString("key_passpoint_unique_id", mFqdn);
        }
        final String mProviderFriendlyName = this.mProviderFriendlyName;
        if (mProviderFriendlyName != null) {
            bundle.putString("key_provider_friendly_name", mProviderFriendlyName);
        }
        final ArrayList<ScanResult> mScanResults = this.mScanResults;
        if (mScanResults != null) {
            bundle.putParcelableArray("key_scanresults", (Parcelable[])mScanResults.toArray(new Parcelable[mScanResults.size()]));
        }
        final ArrayList<TimestampedScoredNetwork> mScoredNetworkCache = this.mScoredNetworkCache;
        if (mScoredNetworkCache != null) {
            bundle.putParcelableArrayList("key_scorednetworkcache", (ArrayList)mScoredNetworkCache);
        }
        bundle.putInt("key_security", this.mSecurity);
        bundle.putInt("key_speed", this.mSpeed);
        final AccessPoint accessPoint = new AccessPoint(this.mContext, bundle);
        accessPoint.setRssi(this.mRssi);
        return accessPoint;
    }
    
    @Keep
    public TestAccessPointBuilder setActive(final boolean b) {
        if (b) {
            this.mNetworkInfo = new NetworkInfo(8, 8, "TestNetwork", "TestNetwork");
        }
        else {
            this.mNetworkInfo = null;
        }
        return this;
    }
    
    public TestAccessPointBuilder setBssid(final String mBssid) {
        this.mBssid = mBssid;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setFqdn(final String mFqdn) {
        this.mFqdn = mFqdn;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setLevel(final int n) {
        final int maxSignalLevel = ((WifiManager)this.mContext.getSystemService((Class)WifiManager.class)).getMaxSignalLevel();
        if (n == 0) {
            this.mRssi = -100;
        }
        else if (n > maxSignalLevel) {
            this.mRssi = -55;
        }
        else {
            this.mRssi = (int)(n * 45.0f / maxSignalLevel - 100.0f);
        }
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setNetworkId(final int mNetworkId) {
        this.mNetworkId = mNetworkId;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setNetworkInfo(final NetworkInfo mNetworkInfo) {
        this.mNetworkInfo = mNetworkInfo;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setProviderFriendlyName(final String mProviderFriendlyName) {
        this.mProviderFriendlyName = mProviderFriendlyName;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setReachable(final boolean b) {
        if (b) {
            if (this.mRssi == Integer.MIN_VALUE) {
                this.mRssi = -100;
            }
        }
        else {
            this.mRssi = Integer.MIN_VALUE;
        }
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setRssi(final int mRssi) {
        this.mRssi = mRssi;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setSaved(final boolean b) {
        if (b) {
            this.mNetworkId = 1;
        }
        else {
            this.mNetworkId = -1;
        }
        return this;
    }
    
    public TestAccessPointBuilder setScanResults(final ArrayList<ScanResult> mScanResults) {
        this.mScanResults = mScanResults;
        return this;
    }
    
    public TestAccessPointBuilder setScoredNetworkCache(final ArrayList<TimestampedScoredNetwork> mScoredNetworkCache) {
        this.mScoredNetworkCache = mScoredNetworkCache;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setSecurity(final int mSecurity) {
        this.mSecurity = mSecurity;
        return this;
    }
    
    public TestAccessPointBuilder setSpeed(final int mSpeed) {
        this.mSpeed = mSpeed;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setSsid(final String ssid) {
        this.ssid = ssid;
        return this;
    }
    
    @Keep
    public TestAccessPointBuilder setWifiInfo(final WifiInfo mWifiInfo) {
        this.mWifiInfo = mWifiInfo;
        return this;
    }
}
