// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import java.util.Iterator;
import java.util.Collection;
import java.util.StringJoiner;
import android.net.NetworkInfo;
import android.net.NetworkScorerAppData;
import android.net.NetworkScoreManager;
import java.util.function.Consumer;
import android.os.SystemClock;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.ToIntFunction;
import java.util.Comparator;
import java.util.function.Predicate;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import java.util.ArrayList;
import androidx.core.util.Preconditions;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import java.util.List;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;

@VisibleForTesting
public class StandardWifiEntry extends WifiEntry
{
    private final Context mContext;
    private final List<ScanResult> mCurrentScanResults;
    private boolean mIsUserShareable;
    private final String mKey;
    private final Object mLock;
    private String mRecommendationServiceLabel;
    private final int mSecurity;
    private final String mSsid;
    private WifiConfiguration mWifiConfig;
    
    StandardWifiEntry(final Context context, final Handler handler, final String s, final WifiConfiguration mWifiConfig, final WifiManager wifiManager, final boolean b) throws IllegalArgumentException {
        this(context, handler, s, wifiManager, b);
        Preconditions.checkNotNull(mWifiConfig, "Cannot construct with null config!");
        Preconditions.checkNotNull(mWifiConfig.SSID, "Supplied config must have an SSID!");
        this.mWifiConfig = mWifiConfig;
        this.updateRecommendationServiceLabel();
    }
    
    StandardWifiEntry(final Context mContext, final Handler handler, final String s, final WifiManager wifiManager, final boolean b) {
        super(handler, wifiManager, b);
        this.mLock = new Object();
        this.mCurrentScanResults = new ArrayList<ScanResult>();
        this.mIsUserShareable = false;
        if (s.startsWith("StandardWifiEntry:")) {
            this.mContext = mContext;
            this.mKey = s;
            try {
                final int lastIndex = s.lastIndexOf(",");
                this.mSsid = s.substring(18, lastIndex);
                this.mSecurity = Integer.valueOf(s.substring(lastIndex + 1));
                this.updateRecommendationServiceLabel();
                return;
            }
            catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Malformed key: ");
                sb.append(s);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        throw new IllegalArgumentException("Key does not start with correct prefix!");
    }
    
    StandardWifiEntry(final Context context, final Handler handler, final String s, final List<ScanResult> list, final WifiManager wifiManager, final boolean b) throws IllegalArgumentException {
        this(context, handler, s, wifiManager, b);
        Preconditions.checkNotNull(list, "Cannot construct with null ScanResult list!");
        if (!list.isEmpty()) {
            this.updateScanResultInfo(list);
            this.updateRecommendationServiceLabel();
            return;
        }
        throw new IllegalArgumentException("Cannot construct with empty ScanResult list!");
    }
    
    private String getConnectStateDescription() {
        if (this.getConnectedState() == 2) {
            if (!this.isSaved()) {
                if (!TextUtils.isEmpty((CharSequence)this.mRecommendationServiceLabel)) {
                    return String.format(this.mContext.getString(R$string.connected_via_network_scorer), this.mRecommendationServiceLabel);
                }
                return this.mContext.getString(R$string.connected_via_network_scorer_default);
            }
            else {
                final WifiInfo mWifiInfo = super.mWifiInfo;
                String requestingPackageName;
                if (mWifiInfo != null) {
                    requestingPackageName = mWifiInfo.getRequestingPackageName();
                }
                else {
                    requestingPackageName = null;
                }
                if (!TextUtils.isEmpty((CharSequence)requestingPackageName)) {
                    final Context mContext = this.mContext;
                    return mContext.getString(R$string.connected_via_app, new Object[] { Utils.getAppLabel(mContext, requestingPackageName) });
                }
                final String currentNetworkCapabilitiesInformation = Utils.getCurrentNetworkCapabilitiesInformation(this.mContext, super.mNetworkCapabilities);
                if (!TextUtils.isEmpty((CharSequence)currentNetworkCapabilitiesInformation)) {
                    return currentNetworkCapabilitiesInformation;
                }
            }
        }
        return Utils.getNetworkDetailedState(this.mContext, super.mNetworkInfo);
    }
    
    private String getScanResultDescription(int n, final int n2) {
        Object mLock = this.mLock;
        synchronized (mLock) {
            final List<Object> list = this.mCurrentScanResults.stream().filter(new _$$Lambda$StandardWifiEntry$lKgEQcmtM1x3SpHuutK3I2_nfI0(n, n2)).sorted(Comparator.comparingInt((ToIntFunction<? super Object>)_$$Lambda$StandardWifiEntry$Lr4BrIBW8EpwljEjYsXvjw_UzPU.INSTANCE)).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList());
            // monitorexit(mLock)
            n = list.size();
            if (n == 0) {
                return "";
            }
            mLock = new StringBuilder();
            ((StringBuilder)mLock).append("(");
            ((StringBuilder)mLock).append(n);
            ((StringBuilder)mLock).append(")");
            if (n > 4) {
                n = list.stream().mapToInt((ToIntFunction<? super Object>)_$$Lambda$StandardWifiEntry$ulMGK6KYyQVXHFy8lpHK9UIg2Q4.INSTANCE).max().getAsInt();
                ((StringBuilder)mLock).append("max=");
                ((StringBuilder)mLock).append(n);
                ((StringBuilder)mLock).append(",");
            }
            list.forEach(new _$$Lambda$StandardWifiEntry$HDaxgAFxNOzpZGjcKD6Vxnrfnp4(this, (StringBuilder)mLock, SystemClock.elapsedRealtime()));
            return ((StringBuilder)mLock).toString();
        }
    }
    
    private String getScanResultDescription(final ScanResult scanResult, final long n) {
        final StringBuilder sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        final WifiInfo mWifiInfo = super.mWifiInfo;
        if (mWifiInfo != null && scanResult.BSSID.equals(mWifiInfo.getBSSID())) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        sb.append(",");
        sb.append(scanResult.level);
        final int i = (int)(n - scanResult.timestamp / 1000L) / 1000;
        sb.append(",");
        sb.append(i);
        sb.append("s");
        sb.append("}");
        return sb.toString();
    }
    
    static String ssidAndSecurityToStandardWifiEntryKey(final String str, final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append("StandardWifiEntry:");
        sb.append(str);
        sb.append(",");
        sb.append(i);
        return sb.toString();
    }
    
    private void updateEapType(final ScanResult scanResult) {
        if (!scanResult.capabilities.contains("RSN-EAP")) {
            scanResult.capabilities.contains("WPA-EAP");
        }
    }
    
    private void updatePskType(final ScanResult scanResult) {
        if (this.mSecurity != 2) {
            return;
        }
        scanResult.capabilities.contains("WPA-PSK");
        scanResult.capabilities.contains("RSN-PSK");
    }
    
    private void updateRecommendationServiceLabel() {
        final NetworkScorerAppData activeScorer = ((NetworkScoreManager)this.mContext.getSystemService("network_score")).getActiveScorer();
        if (activeScorer != null) {
            this.mRecommendationServiceLabel = activeScorer.getRecommendationServiceLabel();
        }
    }
    
    static String wifiConfigToStandardWifiEntryKey(final WifiConfiguration wifiConfiguration) {
        Preconditions.checkNotNull(wifiConfiguration, "Cannot create key with null config!");
        Preconditions.checkNotNull(wifiConfiguration.SSID, "Cannot create key with null SSID in config!");
        final StringBuilder sb = new StringBuilder();
        sb.append("StandardWifiEntry:");
        sb.append(WifiInfo.sanitizeSsid(wifiConfiguration.SSID));
        sb.append(",");
        sb.append(Utils.getSecurityTypeFromWifiConfiguration(wifiConfiguration));
        return sb.toString();
    }
    
    @Override
    public boolean canSetAutoJoinEnabled() {
        return this.isSaved();
    }
    
    @Override
    public boolean canSetMeteredChoice() {
        return this.getWifiConfiguration() != null;
    }
    
    @Override
    protected boolean connectionInfoMatches(final WifiInfo wifiInfo, final NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp()) {
            if (!wifiInfo.isOsuAp()) {
                final WifiConfiguration mWifiConfig = this.mWifiConfig;
                if (mWifiConfig != null) {
                    if (mWifiConfig.fromWifiNetworkSuggestion) {
                        return TextUtils.equals((CharSequence)this.mSsid, (CharSequence)WifiInfo.sanitizeSsid(wifiInfo.getSSID()));
                    }
                    if (mWifiConfig.networkId == wifiInfo.getNetworkId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public ConnectedInfo getConnectedInfo() {
        return super.mConnectedInfo;
    }
    
    @Override
    public String getKey() {
        return this.mKey;
    }
    
    @Override
    public int getLevel() {
        return super.mLevel;
    }
    
    @Override
    public int getMeteredChoice() {
        if (this.getWifiConfiguration() != null) {
            final int meteredOverride = this.getWifiConfiguration().meteredOverride;
            if (meteredOverride == 1) {
                return 1;
            }
            if (meteredOverride == 2) {
                return 2;
            }
        }
        return 0;
    }
    
    @Override
    String getScanResultDescription() {
        Object mLock = this.mLock;
        synchronized (mLock) {
            if (this.mCurrentScanResults.size() == 0) {
                return "";
            }
            // monitorexit(mLock)
            mLock = new StringBuilder();
            ((StringBuilder)mLock).append("[");
            ((StringBuilder)mLock).append(this.getScanResultDescription(2400, 2500));
            ((StringBuilder)mLock).append(";");
            ((StringBuilder)mLock).append(this.getScanResultDescription(4900, 5900));
            ((StringBuilder)mLock).append(";");
            ((StringBuilder)mLock).append(this.getScanResultDescription(5700, 7100));
            ((StringBuilder)mLock).append("]");
            return ((StringBuilder)mLock).toString();
        }
    }
    
    @Override
    public int getSecurity() {
        return this.mSecurity;
    }
    
    @Override
    public String getSummary(final boolean b) {
        final StringJoiner stringJoiner = new StringJoiner(this.mContext.getString(R$string.summary_separator));
        final String speedDescription = Utils.getSpeedDescription(this.mContext, this);
        if (!TextUtils.isEmpty((CharSequence)speedDescription)) {
            stringJoiner.add(speedDescription);
        }
        if (!b && super.mForSavedNetworksPage && this.isSaved()) {
            final CharSequence appLabelForSavedNetwork = Utils.getAppLabelForSavedNetwork(this.mContext, this);
            if (!TextUtils.isEmpty(appLabelForSavedNetwork)) {
                stringJoiner.add(this.mContext.getString(R$string.saved_network, new Object[] { appLabelForSavedNetwork }));
            }
        }
        if (this.getConnectedState() == 0) {
            final String disconnectedStateDescription = Utils.getDisconnectedStateDescription(this.mContext, this);
            if (TextUtils.isEmpty((CharSequence)disconnectedStateDescription)) {
                if (b) {
                    stringJoiner.add(this.mContext.getString(R$string.wifi_disconnected));
                }
                else if (!super.mForSavedNetworksPage && this.isSaved()) {
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
        return this.mSsid;
    }
    
    @Override
    public WifiConfiguration getWifiConfiguration() {
        final WifiConfiguration mWifiConfig = this.mWifiConfig;
        if (mWifiConfig != null && !mWifiConfig.fromWifiNetworkSuggestion) {
            return mWifiConfig;
        }
        return null;
    }
    
    @Override
    public boolean isAutoJoinEnabled() {
        return this.isSaved() && this.mWifiConfig.allowAutojoin;
    }
    
    @Override
    public boolean isMetered() {
        final int meteredChoice = this.getMeteredChoice();
        boolean b = true;
        if (meteredChoice != 1) {
            final WifiConfiguration mWifiConfig = this.mWifiConfig;
            b = (mWifiConfig != null && mWifiConfig.meteredHint && b);
        }
        return b;
    }
    
    @Override
    public boolean isSaved() {
        return this.mWifiConfig != null;
    }
    
    @Override
    public boolean isSubscription() {
        return false;
    }
    
    boolean isUserShareable() {
        return this.mIsUserShareable;
    }
    
    void setUserShareable(final boolean mIsUserShareable) {
        this.mIsUserShareable = mIsUserShareable;
    }
    
    void updateConfig(final WifiConfiguration mWifiConfig) throws IllegalArgumentException {
        if (mWifiConfig != null) {
            if (!TextUtils.equals((CharSequence)this.mSsid, (CharSequence)WifiInfo.sanitizeSsid(mWifiConfig.SSID))) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Attempted to update with wrong SSID! Expected: ");
                sb.append(this.mSsid);
                sb.append(", Actual: ");
                sb.append(WifiInfo.sanitizeSsid(mWifiConfig.SSID));
                sb.append(", Config: ");
                sb.append(mWifiConfig);
                throw new IllegalArgumentException(sb.toString());
            }
            if (this.mSecurity != Utils.getSecurityTypeFromWifiConfiguration(mWifiConfig)) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Attempted to update with wrong security! Expected: ");
                sb2.append(this.mSecurity);
                sb2.append(", Actual: ");
                sb2.append(Utils.getSecurityTypeFromWifiConfiguration(mWifiConfig));
                sb2.append(", Config: ");
                sb2.append(mWifiConfig);
                throw new IllegalArgumentException(sb2.toString());
            }
        }
        this.mWifiConfig = mWifiConfig;
        this.notifyOnUpdated();
    }
    
    void updateScanResultInfo(List<ScanResult> o) throws IllegalArgumentException {
        Object o2 = o;
        if (o == null) {
            o2 = new ArrayList<ScanResult>();
        }
        for (final ScanResult obj : o2) {
            if (TextUtils.equals((CharSequence)obj.SSID, (CharSequence)this.mSsid)) {
                continue;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Attempted to update with wrong SSID! Expected: ");
            sb.append(this.mSsid);
            sb.append(", Actual: ");
            sb.append(obj.SSID);
            sb.append(", ScanResult: ");
            sb.append(obj);
            throw new IllegalArgumentException(sb.toString());
        }
        o = this.mLock;
        synchronized (o) {
            this.mCurrentScanResults.clear();
            this.mCurrentScanResults.addAll((Collection<? extends ScanResult>)o2);
            // monitorexit(o)
            o = Utils.getBestScanResultByLevel((List<ScanResult>)o2);
            if (o == null) {
                super.mLevel = -1;
            }
            else {
                super.mLevel = super.mWifiManager.calculateSignalLevel(((ScanResult)o).level);
                this.updateEapType((ScanResult)o);
                this.updatePskType((ScanResult)o);
            }
            this.notifyOnUpdated();
        }
    }
}
