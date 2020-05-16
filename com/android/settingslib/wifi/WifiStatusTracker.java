// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import android.net.INetworkScoreCache;
import android.net.NetworkInfo;
import android.content.Intent;
import android.provider.Settings$Global;
import com.android.settingslib.R$string;
import android.net.NetworkKey;
import android.net.wifi.WifiConfiguration;
import android.net.NetworkRequest$Builder;
import android.net.ScoredNetwork;
import java.util.List;
import android.os.Looper;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.NetworkScoreManager;
import android.net.NetworkRequest;
import android.os.Handler;
import android.net.NetworkCapabilities;
import android.net.ConnectivityManager$NetworkCallback;
import android.net.Network;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiNetworkScoreCache$CacheListener;

public class WifiStatusTracker
{
    public boolean connected;
    public boolean enabled;
    public boolean isCaptivePortal;
    public boolean isDefaultNetwork;
    public int level;
    private final WifiNetworkScoreCache$CacheListener mCacheListener;
    private final Runnable mCallback;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private Network mDefaultNetwork;
    private final ConnectivityManager$NetworkCallback mDefaultNetworkCallback;
    private NetworkCapabilities mDefaultNetworkCapabilities;
    private final Handler mHandler;
    private final ConnectivityManager$NetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    private final NetworkScoreManager mNetworkScoreManager;
    private WifiInfo mWifiInfo;
    private final WifiManager mWifiManager;
    private final WifiNetworkScoreCache mWifiNetworkScoreCache;
    public int rssi;
    public String ssid;
    public int state;
    public String statusLabel;
    
    public WifiStatusTracker(final Context mContext, final WifiManager mWifiManager, final NetworkScoreManager mNetworkScoreManager, final ConnectivityManager mConnectivityManager, final Runnable mCallback) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mCacheListener = new WifiNetworkScoreCache$CacheListener(this.mHandler) {
            public void networkCacheUpdated(final List<ScoredNetwork> list) {
                WifiStatusTracker.this.updateStatusLabel();
                WifiStatusTracker.this.mCallback.run();
            }
        };
        this.mNetworkRequest = new NetworkRequest$Builder().clearCapabilities().addCapability(15).addTransportType(1).build();
        this.mNetworkCallback = new ConnectivityManager$NetworkCallback() {
            public void onCapabilitiesChanged(final Network network, final NetworkCapabilities networkCapabilities) {
                WifiStatusTracker.this.updateStatusLabel();
                WifiStatusTracker.this.mCallback.run();
            }
        };
        this.mDefaultNetworkCallback = new ConnectivityManager$NetworkCallback() {
            public void onCapabilitiesChanged(final Network network, final NetworkCapabilities networkCapabilities) {
                WifiStatusTracker.this.mDefaultNetwork = network;
                WifiStatusTracker.this.mDefaultNetworkCapabilities = networkCapabilities;
                WifiStatusTracker.this.updateStatusLabel();
                WifiStatusTracker.this.mCallback.run();
            }
            
            public void onLost(final Network network) {
                WifiStatusTracker.this.mDefaultNetwork = null;
                WifiStatusTracker.this.mDefaultNetworkCapabilities = null;
                WifiStatusTracker.this.updateStatusLabel();
                WifiStatusTracker.this.mCallback.run();
            }
        };
        this.mDefaultNetwork = null;
        this.mDefaultNetworkCapabilities = null;
        this.mContext = mContext;
        this.mWifiManager = mWifiManager;
        this.mWifiNetworkScoreCache = new WifiNetworkScoreCache(mContext);
        this.mNetworkScoreManager = mNetworkScoreManager;
        this.mConnectivityManager = mConnectivityManager;
        this.mCallback = mCallback;
    }
    
    private String getValidSsid(final WifiInfo wifiInfo) {
        final String ssid = wifiInfo.getSSID();
        if (ssid != null && !"<unknown ssid>".equals(ssid)) {
            return ssid;
        }
        final List configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        for (int size = configuredNetworks.size(), i = 0; i < size; ++i) {
            if (configuredNetworks.get(i).networkId == wifiInfo.getNetworkId()) {
                return configuredNetworks.get(i).SSID;
            }
        }
        return null;
    }
    
    private void maybeRequestNetworkScore() {
        final NetworkKey fromWifiInfo = NetworkKey.createFromWifiInfo(this.mWifiInfo);
        if (this.mWifiNetworkScoreCache.getScoredNetwork(fromWifiInfo) == null) {
            this.mNetworkScoreManager.requestScores(new NetworkKey[] { fromWifiInfo });
        }
    }
    
    private void updateRssi(final int rssi) {
        this.rssi = rssi;
        this.level = this.mWifiManager.calculateSignalLevel(rssi);
    }
    
    private void updateStatusLabel() {
        final Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        NetworkCapabilities networkCapabilities;
        if (currentNetwork != null && currentNetwork.equals((Object)this.mDefaultNetwork)) {
            this.isDefaultNetwork = true;
            networkCapabilities = this.mDefaultNetworkCapabilities;
        }
        else {
            this.isDefaultNetwork = false;
            networkCapabilities = this.mConnectivityManager.getNetworkCapabilities(this.mWifiManager.getCurrentNetwork());
        }
        this.isCaptivePortal = false;
        if (networkCapabilities != null) {
            if (networkCapabilities.hasCapability(17)) {
                this.statusLabel = this.mContext.getString(R$string.wifi_status_sign_in_required);
                this.isCaptivePortal = true;
                return;
            }
            if (networkCapabilities.hasCapability(24)) {
                this.statusLabel = this.mContext.getString(R$string.wifi_limited_connection);
                return;
            }
            if (!networkCapabilities.hasCapability(16)) {
                Settings$Global.getString(this.mContext.getContentResolver(), "private_dns_mode");
                if (networkCapabilities.isPrivateDnsBroken()) {
                    this.statusLabel = this.mContext.getString(R$string.private_dns_broken);
                }
                else {
                    this.statusLabel = this.mContext.getString(R$string.wifi_status_no_internet);
                }
                return;
            }
        }
        final ScoredNetwork scoredNetwork = this.mWifiNetworkScoreCache.getScoredNetwork(NetworkKey.createFromWifiInfo(this.mWifiInfo));
        String speedLabel;
        if (scoredNetwork == null) {
            speedLabel = null;
        }
        else {
            speedLabel = AccessPoint.getSpeedLabel(this.mContext, scoredNetwork, this.rssi);
        }
        this.statusLabel = speedLabel;
    }
    
    private void updateWifiState() {
        final int wifiState = this.mWifiManager.getWifiState();
        this.state = wifiState;
        this.enabled = (wifiState == 3);
    }
    
    public void handleBroadcast(final Intent intent) {
        if (this.mWifiManager == null) {
            return;
        }
        final String action = intent.getAction();
        if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            this.updateWifiState();
        }
        else if (action.equals("android.net.wifi.STATE_CHANGE")) {
            this.updateWifiState();
            final NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra("networkInfo");
            final boolean connected = networkInfo != null && networkInfo.isConnected();
            this.connected = connected;
            this.mWifiInfo = null;
            this.ssid = null;
            if (connected) {
                final WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
                if ((this.mWifiInfo = connectionInfo) != null) {
                    if (!connectionInfo.isPasspointAp() && !this.mWifiInfo.isOsuAp()) {
                        this.ssid = this.getValidSsid(this.mWifiInfo);
                    }
                    else {
                        this.ssid = this.mWifiInfo.getPasspointProviderFriendlyName();
                    }
                    this.updateRssi(this.mWifiInfo.getRssi());
                    this.maybeRequestNetworkScore();
                }
            }
            this.updateStatusLabel();
        }
        else if (action.equals("android.net.wifi.RSSI_CHANGED")) {
            this.updateRssi(intent.getIntExtra("newRssi", -200));
            this.updateStatusLabel();
        }
    }
    
    public void refreshLocale() {
        this.updateStatusLabel();
        this.mCallback.run();
    }
    
    public void setListening(final boolean b) {
        if (b) {
            this.mNetworkScoreManager.registerNetworkScoreCache(1, (INetworkScoreCache)this.mWifiNetworkScoreCache, 1);
            this.mWifiNetworkScoreCache.registerListener(this.mCacheListener);
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mHandler);
            this.mConnectivityManager.registerDefaultNetworkCallback(this.mDefaultNetworkCallback, this.mHandler);
        }
        else {
            this.mNetworkScoreManager.unregisterNetworkScoreCache(1, (INetworkScoreCache)this.mWifiNetworkScoreCache);
            this.mWifiNetworkScoreCache.unregisterListener();
            this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
            this.mConnectivityManager.unregisterNetworkCallback(this.mDefaultNetworkCallback);
        }
    }
}
