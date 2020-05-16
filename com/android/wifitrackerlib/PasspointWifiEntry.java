// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import java.util.Collection;
import java.util.StringJoiner;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import androidx.core.util.Preconditions;
import java.util.ArrayList;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.ScanResult;
import java.util.List;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;

@VisibleForTesting
public class PasspointWifiEntry extends WifiEntry
{
    private final Context mContext;
    private final List<ScanResult> mCurrentHomeScanResults;
    private final List<ScanResult> mCurrentRoamingScanResults;
    private String mFriendlyName;
    private final String mKey;
    private int mLevel;
    private final Object mLock;
    private int mMeteredOverride;
    private PasspointConfiguration mPasspointConfig;
    private int mSecurity;
    protected long mSubscriptionExpirationTimeInMillis;
    private WifiConfiguration mWifiConfig;
    
    PasspointWifiEntry(final Context mContext, final Handler handler, final PasspointConfiguration mPasspointConfig, final WifiManager wifiManager, final boolean b) throws IllegalArgumentException {
        super(handler, wifiManager, b);
        this.mLock = new Object();
        this.mCurrentHomeScanResults = new ArrayList<ScanResult>();
        this.mCurrentRoamingScanResults = new ArrayList<ScanResult>();
        this.mLevel = -1;
        Preconditions.checkNotNull(mPasspointConfig, "Cannot construct with null PasspointConfiguration!");
        this.mContext = mContext;
        this.mPasspointConfig = mPasspointConfig;
        this.mKey = uniqueIdToPasspointWifiEntryKey(mPasspointConfig.getUniqueId());
        this.mFriendlyName = mPasspointConfig.getHomeSp().getFriendlyName();
        this.mSecurity = 0;
        this.mSubscriptionExpirationTimeInMillis = mPasspointConfig.getSubscriptionExpirationTimeMillis();
        this.mMeteredOverride = this.mPasspointConfig.getMeteredOverride();
    }
    
    private String getConnectStateDescription() {
        if (this.getConnectedState() == 2) {
            final String currentNetworkCapabilitiesInformation = Utils.getCurrentNetworkCapabilitiesInformation(this.mContext, super.mNetworkCapabilities);
            if (!TextUtils.isEmpty((CharSequence)currentNetworkCapabilitiesInformation)) {
                return currentNetworkCapabilitiesInformation;
            }
        }
        return Utils.getNetworkDetailedState(this.mContext, super.mNetworkInfo);
    }
    
    static String uniqueIdToPasspointWifiEntryKey(final String str) {
        Preconditions.checkNotNull(str, "Cannot create key with null unique id!");
        final StringBuilder sb = new StringBuilder();
        sb.append("PasspointWifiEntry:");
        sb.append(str);
        return sb.toString();
    }
    
    @Override
    public boolean canSetAutoJoinEnabled() {
        return true;
    }
    
    @Override
    public boolean canSetMeteredChoice() {
        return true;
    }
    
    @Override
    protected boolean connectionInfoMatches(final WifiInfo wifiInfo, final NetworkInfo networkInfo) {
        return wifiInfo.isPasspointAp() && TextUtils.equals((CharSequence)wifiInfo.getPasspointFqdn(), (CharSequence)this.mPasspointConfig.getHomeSp().getFqdn());
    }
    
    @Override
    public String getKey() {
        return this.mKey;
    }
    
    @Override
    public int getLevel() {
        return this.mLevel;
    }
    
    @Override
    public int getMeteredChoice() {
        final int mMeteredOverride = this.mMeteredOverride;
        if (mMeteredOverride == 1) {
            return 1;
        }
        if (mMeteredOverride == 2) {
            return 2;
        }
        return 0;
    }
    
    @Override
    String getScanResultDescription() {
        return "";
    }
    
    @Override
    public int getSecurity() {
        return this.mSecurity;
    }
    
    @Override
    public String getSummary(final boolean b) {
        if (this.isExpired()) {
            return this.mContext.getString(R$string.wifi_passpoint_expired);
        }
        final StringJoiner stringJoiner = new StringJoiner(this.mContext.getString(R$string.summary_separator));
        final String speedDescription = Utils.getSpeedDescription(this.mContext, this);
        if (!TextUtils.isEmpty((CharSequence)speedDescription)) {
            stringJoiner.add(speedDescription);
        }
        if (this.getConnectedState() == 0) {
            final String disconnectedStateDescription = Utils.getDisconnectedStateDescription(this.mContext, this);
            if (TextUtils.isEmpty((CharSequence)disconnectedStateDescription)) {
                if (b) {
                    stringJoiner.add(this.mContext.getString(R$string.wifi_disconnected));
                }
                else if (!super.mForSavedNetworksPage) {
                    stringJoiner.add(this.mContext.getString(R$string.wifi_remembered));
                }
            }
            else {
                stringJoiner.add(disconnectedStateDescription);
            }
        }
        else {
            final String connectStateDescription = this.getConnectStateDescription();
            if (!TextUtils.isEmpty((CharSequence)connectStateDescription)) {
                stringJoiner.add(connectStateDescription);
            }
        }
        final String autoConnectDescription = Utils.getAutoConnectDescription(this.mContext, this);
        if (!TextUtils.isEmpty((CharSequence)autoConnectDescription)) {
            stringJoiner.add(autoConnectDescription);
        }
        final String meteredDescription = Utils.getMeteredDescription(this.mContext, this);
        if (!TextUtils.isEmpty((CharSequence)meteredDescription)) {
            stringJoiner.add(meteredDescription);
        }
        if (!b) {
            final String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty((CharSequence)verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }
    
    @Override
    public String getTitle() {
        return this.mFriendlyName;
    }
    
    @Override
    public WifiConfiguration getWifiConfiguration() {
        return null;
    }
    
    @Override
    public boolean isAutoJoinEnabled() {
        return this.mPasspointConfig.isAutojoinEnabled();
    }
    
    public boolean isExpired() {
        final long mSubscriptionExpirationTimeInMillis = this.mSubscriptionExpirationTimeInMillis;
        boolean b = false;
        if (mSubscriptionExpirationTimeInMillis <= 0L) {
            return false;
        }
        if (System.currentTimeMillis() >= this.mSubscriptionExpirationTimeInMillis) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean isMetered() {
        return false;
    }
    
    @Override
    public boolean isSaved() {
        return false;
    }
    
    @Override
    public boolean isSubscription() {
        return true;
    }
    
    void updatePasspointConfig(final PasspointConfiguration mPasspointConfig) {
        this.mPasspointConfig = mPasspointConfig;
        this.mFriendlyName = mPasspointConfig.getHomeSp().getFriendlyName();
        this.mSubscriptionExpirationTimeInMillis = mPasspointConfig.getSubscriptionExpirationTimeMillis();
        this.mMeteredOverride = this.mPasspointConfig.getMeteredOverride();
        this.notifyOnUpdated();
    }
    
    void updateScanResultInfo(final WifiConfiguration mWifiConfig, final List<ScanResult> list, final List<ScanResult> list2) throws IllegalArgumentException {
        this.mWifiConfig = mWifiConfig;
        Object mLock = this.mLock;
        synchronized (mLock) {
            this.mCurrentHomeScanResults.clear();
            this.mCurrentRoamingScanResults.clear();
            if (list != null) {
                this.mCurrentHomeScanResults.addAll(list);
            }
            if (list2 != null) {
                this.mCurrentRoamingScanResults.addAll(list2);
            }
            // monitorexit(mLock)
            if (this.mWifiConfig != null) {
                this.mSecurity = Utils.getSecurityTypeFromWifiConfiguration(mWifiConfig);
                mLock = null;
                if (list != null && !list.isEmpty()) {
                    Utils.getBestScanResultByLevel(list);
                }
                else if (list2 != null && !list2.isEmpty()) {
                    Utils.getBestScanResultByLevel(list2);
                }
                if (mLock == null) {
                    this.mLevel = -1;
                }
                else {
                    final WifiConfiguration mWifiConfig2 = this.mWifiConfig;
                    final StringBuilder sb = new StringBuilder();
                    sb.append("\"");
                    sb.append(((ScanResult)mLock).SSID);
                    sb.append("\"");
                    mWifiConfig2.SSID = sb.toString();
                    this.mLevel = super.mWifiManager.calculateSignalLevel(((ScanResult)mLock).level);
                }
            }
            else {
                this.mLevel = -1;
            }
            this.notifyOnUpdated();
        }
    }
}
