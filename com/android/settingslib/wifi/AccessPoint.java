// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import java.util.List;
import android.util.Pair;
import java.util.Collections;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.ProvisioningCallback;
import com.android.settingslib.utils.ThreadUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.annotations.VisibleForTesting;
import android.net.NetworkInfo$State;
import java.util.Set;
import java.util.function.Consumer;
import android.os.SystemClock;
import android.net.NetworkKey;
import android.net.wifi.WifiNetworkScoreCache;
import android.content.res.Resources;
import android.net.NetworkCapabilities;
import android.net.NetworkScorerAppData;
import com.android.settingslib.R$array;
import android.provider.Settings$Global;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.NetworkInfo$DetailedState;
import android.net.ScoredNetwork;
import com.android.settingslib.R$string;
import java.util.BitSet;
import android.text.TextUtils;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.UserHandle;
import android.content.pm.PackageManager;
import android.util.Log;
import java.util.Iterator;
import android.os.Parcelable;
import android.os.Bundle;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import android.net.wifi.WifiManager;
import java.util.Map;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.ScanResult;
import android.util.ArraySet;
import android.content.Context;
import android.net.wifi.WifiManager$ActionListener;
import android.net.wifi.WifiConfiguration;

public class AccessPoint implements Comparable<AccessPoint>
{
    private String bssid;
    AccessPointListener mAccessPointListener;
    private WifiConfiguration mConfig;
    private WifiManager$ActionListener mConnectListener;
    private final Context mContext;
    private final ArraySet<ScanResult> mExtraScanResults;
    private WifiInfo mInfo;
    private boolean mIsOweTransitionMode;
    private boolean mIsPskSaeTransitionMode;
    private boolean mIsScoredNetworkMetered;
    private String mKey;
    private final Object mLock;
    private NetworkInfo mNetworkInfo;
    private String mOsuFailure;
    private OsuProvider mOsuProvider;
    private boolean mOsuProvisioningComplete;
    private String mOsuStatus;
    private int mPasspointConfigurationVersion;
    private String mPasspointUniqueId;
    private String mProviderFriendlyName;
    private int mRssi;
    private final ArraySet<ScanResult> mScanResults;
    private final Map<String, TimestampedScoredNetwork> mScoredNetworkCache;
    private int mSpeed;
    private long mSubscriptionExpirationTimeInMillis;
    private Object mTag;
    private WifiManager mWifiManager;
    private int networkId;
    private int pskType;
    private int security;
    private String ssid;
    
    static {
        new AtomicInteger(0);
    }
    
    public AccessPoint(final Context mContext, final WifiConfiguration wifiConfiguration) {
        this.mLock = new Object();
        this.mScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mExtraScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mScoredNetworkCache = new HashMap<String, TimestampedScoredNetwork>();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = mContext;
        this.loadConfig(wifiConfiguration);
        this.updateKey();
    }
    
    public AccessPoint(final Context mContext, final WifiConfiguration mConfig, final Collection<ScanResult> collection, final Collection<ScanResult> collection2) {
        this.mLock = new Object();
        this.mScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mExtraScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mScoredNetworkCache = new HashMap<String, TimestampedScoredNetwork>();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = mContext;
        this.networkId = mConfig.networkId;
        this.mConfig = mConfig;
        this.mPasspointUniqueId = mConfig.getKey();
        final String fqdn = mConfig.FQDN;
        this.setScanResultsPasspoint(collection, collection2);
        this.updateKey();
    }
    
    public AccessPoint(final Context mContext, final OsuProvider mOsuProvider, final Collection<ScanResult> scanResults) {
        this.mLock = new Object();
        this.mScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mExtraScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mScoredNetworkCache = new HashMap<String, TimestampedScoredNetwork>();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = mContext;
        this.mOsuProvider = mOsuProvider;
        this.setScanResults(scanResults);
        this.updateKey();
    }
    
    public AccessPoint(final Context mContext, final Bundle bundle) {
        this.mLock = new Object();
        this.mScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mExtraScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mScoredNetworkCache = new HashMap<String, TimestampedScoredNetwork>();
        this.networkId = -1;
        int i = 0;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = mContext;
        if (bundle.containsKey("key_config")) {
            this.mConfig = (WifiConfiguration)bundle.getParcelable("key_config");
        }
        final WifiConfiguration mConfig = this.mConfig;
        if (mConfig != null) {
            this.loadConfig(mConfig);
        }
        if (bundle.containsKey("key_ssid")) {
            this.ssid = bundle.getString("key_ssid");
        }
        if (bundle.containsKey("key_security")) {
            this.security = bundle.getInt("key_security");
        }
        if (bundle.containsKey("key_speed")) {
            this.mSpeed = bundle.getInt("key_speed");
        }
        if (bundle.containsKey("key_psktype")) {
            this.pskType = bundle.getInt("key_psktype");
        }
        if (bundle.containsKey("eap_psktype")) {
            bundle.getInt("eap_psktype");
        }
        this.mInfo = (WifiInfo)bundle.getParcelable("key_wifiinfo");
        if (bundle.containsKey("key_networkinfo")) {
            this.mNetworkInfo = (NetworkInfo)bundle.getParcelable("key_networkinfo");
        }
        if (bundle.containsKey("key_scanresults")) {
            final Parcelable[] parcelableArray = bundle.getParcelableArray("key_scanresults");
            this.mScanResults.clear();
            while (i < parcelableArray.length) {
                this.mScanResults.add((Object)parcelableArray[i]);
                ++i;
            }
        }
        if (bundle.containsKey("key_scorednetworkcache")) {
            for (final TimestampedScoredNetwork timestampedScoredNetwork : bundle.getParcelableArrayList("key_scorednetworkcache")) {
                this.mScoredNetworkCache.put(timestampedScoredNetwork.getScore().networkKey.wifiKey.bssid, timestampedScoredNetwork);
            }
        }
        if (bundle.containsKey("key_passpoint_unique_id")) {
            this.mPasspointUniqueId = bundle.getString("key_passpoint_unique_id");
        }
        if (bundle.containsKey("key_fqdn")) {
            bundle.getString("key_fqdn");
        }
        if (bundle.containsKey("key_provider_friendly_name")) {
            this.mProviderFriendlyName = bundle.getString("key_provider_friendly_name");
        }
        if (bundle.containsKey("key_subscription_expiration_time_in_millis")) {
            this.mSubscriptionExpirationTimeInMillis = bundle.getLong("key_subscription_expiration_time_in_millis");
        }
        if (bundle.containsKey("key_passpoint_configuration_version")) {
            this.mPasspointConfigurationVersion = bundle.getInt("key_passpoint_configuration_version");
        }
        if (bundle.containsKey("key_is_psk_sae_transition_mode")) {
            this.mIsPskSaeTransitionMode = bundle.getBoolean("key_is_psk_sae_transition_mode");
        }
        if (bundle.containsKey("key_is_owe_transition_mode")) {
            this.mIsOweTransitionMode = bundle.getBoolean("key_is_owe_transition_mode");
        }
        this.update(this.mConfig, this.mInfo, this.mNetworkInfo);
        this.updateKey();
        this.updateBestRssiInfo();
    }
    
    AccessPoint(final Context mContext, final Collection<ScanResult> scanResults) {
        this.mLock = new Object();
        this.mScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mExtraScanResults = (ArraySet<ScanResult>)new ArraySet();
        this.mScoredNetworkCache = new HashMap<String, TimestampedScoredNetwork>();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = mContext;
        this.setScanResults(scanResults);
        this.updateKey();
    }
    
    public static String convertToQuotedString(final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(str);
        sb.append("\"");
        return sb.toString();
    }
    
    private int generateAverageSpeedForSsid() {
        if (this.mScoredNetworkCache.isEmpty()) {
            return 0;
        }
        if (Log.isLoggable("SettingsLib.AccessPoint", 3)) {
            Log.d("SettingsLib.AccessPoint", String.format("Generating fallbackspeed for %s using cache: %s", this.getSsidStr(), this.mScoredNetworkCache));
        }
        final Iterator<TimestampedScoredNetwork> iterator = this.mScoredNetworkCache.values().iterator();
        int n2;
        int n = n2 = 0;
        while (iterator.hasNext()) {
            final int calculateBadge = iterator.next().getScore().calculateBadge(this.mRssi);
            if (calculateBadge != 0) {
                ++n;
                n2 += calculateBadge;
            }
        }
        int i;
        if (n == 0) {
            i = 0;
        }
        else {
            i = n2 / n;
        }
        if (isVerboseLoggingEnabled()) {
            Log.i("SettingsLib.AccessPoint", String.format("%s generated fallback speed is: %d", this.getSsidStr(), i));
        }
        return roundToClosestSpeedEnum(i);
    }
    
    private static CharSequence getAppLabel(String loadLabel, final PackageManager packageManager) {
        final String s = "";
        try {
            final ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser((String)loadLabel, 0, UserHandle.getUserId(-2));
            loadLabel = s;
            if (applicationInfoAsUser != null) {
                loadLabel = applicationInfoAsUser.loadLabel(packageManager);
            }
            return loadLabel;
        }
        catch (PackageManager$NameNotFoundException ex) {
            Log.e("SettingsLib.AccessPoint", "Failed to get app info", (Throwable)ex);
            return "";
        }
    }
    
    private static int getEapType(final ScanResult scanResult) {
        if (scanResult.capabilities.contains("RSN-EAP")) {
            return 2;
        }
        if (scanResult.capabilities.contains("WPA-EAP")) {
            return 1;
        }
        return 0;
    }
    
    public static String getKey(final Context context, final ScanResult scanResult) {
        return getKey(scanResult.SSID, scanResult.BSSID, getSecurity(context, scanResult));
    }
    
    public static String getKey(final WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.isPasspoint()) {
            return getKey(wifiConfiguration.getKey());
        }
        return getKey(removeDoubleQuotes(wifiConfiguration.SSID), wifiConfiguration.BSSID, getSecurity(wifiConfiguration));
    }
    
    public static String getKey(final OsuProvider osuProvider) {
        final StringBuilder sb = new StringBuilder();
        sb.append("OSU:");
        sb.append(osuProvider.getFriendlyName());
        sb.append(',');
        sb.append(osuProvider.getServerUri());
        return sb.toString();
    }
    
    public static String getKey(final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append("PASSPOINT:");
        sb.append(str);
        return sb.toString();
    }
    
    private static String getKey(final String str, final String str2, final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append("AP:");
        if (TextUtils.isEmpty((CharSequence)str)) {
            sb.append(str2);
        }
        else {
            sb.append(str);
        }
        sb.append(',');
        sb.append(i);
        return sb.toString();
    }
    
    private static int getPskType(final ScanResult scanResult) {
        final boolean contains = scanResult.capabilities.contains("WPA-PSK");
        final boolean contains2 = scanResult.capabilities.contains("RSN-PSK");
        final boolean contains3 = scanResult.capabilities.contains("RSN-SAE");
        if (contains2 && contains) {
            return 3;
        }
        if (contains2) {
            return 2;
        }
        if (contains) {
            return 1;
        }
        if (!contains3) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Received abnormal flag string: ");
            sb.append(scanResult.capabilities);
            Log.w("SettingsLib.AccessPoint", sb.toString());
        }
        return 0;
    }
    
    private static int getSecurity(final Context context, final ScanResult scanResult) {
        final boolean contains = scanResult.capabilities.contains("WEP");
        final boolean contains2 = scanResult.capabilities.contains("SAE");
        final boolean contains3 = scanResult.capabilities.contains("PSK");
        final boolean contains4 = scanResult.capabilities.contains("EAP_SUITE_B_192");
        final boolean contains5 = scanResult.capabilities.contains("EAP");
        final boolean contains6 = scanResult.capabilities.contains("OWE");
        final boolean contains7 = scanResult.capabilities.contains("OWE_TRANSITION");
        int n = 5;
        if (contains2 && contains3) {
            if (!((WifiManager)context.getSystemService("wifi")).isWpa3SaeSupported()) {
                n = 2;
            }
            return n;
        }
        int n2 = 4;
        if (contains7) {
            if (!((WifiManager)context.getSystemService("wifi")).isEnhancedOpenSupported()) {
                n2 = 0;
            }
            return n2;
        }
        if (contains) {
            return 1;
        }
        if (contains2) {
            return 5;
        }
        if (contains3) {
            return 2;
        }
        if (contains4) {
            return 6;
        }
        if (contains5) {
            return 3;
        }
        if (contains6) {
            return 4;
        }
        return 0;
    }
    
    static int getSecurity(final WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return 5;
        }
        final BitSet allowedKeyManagement = wifiConfiguration.allowedKeyManagement;
        int n = 1;
        if (allowedKeyManagement.get(1)) {
            return 2;
        }
        if (wifiConfiguration.allowedKeyManagement.get(10)) {
            return 6;
        }
        if (wifiConfiguration.allowedKeyManagement.get(2) || wifiConfiguration.allowedKeyManagement.get(3)) {
            return 3;
        }
        if (wifiConfiguration.allowedKeyManagement.get(9)) {
            return 4;
        }
        final int wepTxKeyIndex = wifiConfiguration.wepTxKeyIndex;
        if (wepTxKeyIndex >= 0) {
            final String[] wepKeys = wifiConfiguration.wepKeys;
            if (wepTxKeyIndex < wepKeys.length && wepKeys[wepTxKeyIndex] != null) {
                return n;
            }
        }
        n = 0;
        return n;
    }
    
    private static String getSpeedLabel(final Context context, final int n) {
        if (n == 5) {
            return context.getString(R$string.speed_label_slow);
        }
        if (n == 10) {
            return context.getString(R$string.speed_label_okay);
        }
        if (n == 20) {
            return context.getString(R$string.speed_label_fast);
        }
        if (n != 30) {
            return null;
        }
        return context.getString(R$string.speed_label_very_fast);
    }
    
    public static String getSpeedLabel(final Context context, final ScoredNetwork scoredNetwork, final int n) {
        return getSpeedLabel(context, roundToClosestSpeedEnum(scoredNetwork.calculateBadge(n)));
    }
    
    public static String getSummary(final Context context, final String s, final NetworkInfo$DetailedState networkInfo$DetailedState, final boolean b, final String s2) {
        if (networkInfo$DetailedState == NetworkInfo$DetailedState.CONNECTED) {
            if (b && !TextUtils.isEmpty((CharSequence)s2)) {
                return context.getString(R$string.connected_via_app, new Object[] { getAppLabel(s2, context.getPackageManager()) });
            }
            if (b) {
                final NetworkScorerAppData activeScorer = ((NetworkScoreManager)context.getSystemService((Class)NetworkScoreManager.class)).getActiveScorer();
                if (activeScorer != null && activeScorer.getRecommendationServiceLabel() != null) {
                    return String.format(context.getString(R$string.connected_via_network_scorer), activeScorer.getRecommendationServiceLabel());
                }
                return context.getString(R$string.connected_via_network_scorer_default);
            }
        }
        final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
        if (networkInfo$DetailedState == NetworkInfo$DetailedState.CONNECTED) {
            final NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(((WifiManager)context.getSystemService((Class)WifiManager.class)).getCurrentNetwork());
            if (networkCapabilities != null) {
                if (networkCapabilities.hasCapability(17)) {
                    return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", "android"));
                }
                if (networkCapabilities.hasCapability(24)) {
                    return context.getString(R$string.wifi_limited_connection);
                }
                if (!networkCapabilities.hasCapability(16)) {
                    Settings$Global.getString(context.getContentResolver(), "private_dns_mode");
                    if (networkCapabilities.isPrivateDnsBroken()) {
                        return context.getString(R$string.private_dns_broken);
                    }
                    return context.getString(R$string.wifi_connected_no_internet);
                }
            }
        }
        if (networkInfo$DetailedState == null) {
            Log.w("SettingsLib.AccessPoint", "state is null, returning empty summary");
            return "";
        }
        final Resources resources = context.getResources();
        int n;
        if (s == null) {
            n = R$array.wifi_status;
        }
        else {
            n = R$array.wifi_status_with_ssid;
        }
        final String[] stringArray = resources.getStringArray(n);
        final int ordinal = networkInfo$DetailedState.ordinal();
        if (ordinal < stringArray.length && stringArray[ordinal].length() != 0) {
            return String.format(stringArray[ordinal], s);
        }
        return "";
    }
    
    private WifiManager getWifiManager() {
        if (this.mWifiManager == null) {
            this.mWifiManager = (WifiManager)this.mContext.getSystemService("wifi");
        }
        return this.mWifiManager;
    }
    
    private boolean isInfoForThisAccessPoint(final WifiConfiguration wifiConfiguration, final WifiInfo wifiInfo) {
        final boolean osuAp = wifiInfo.isOsuAp();
        final boolean b = true;
        final boolean b2 = true;
        boolean b3 = true;
        if (osuAp || this.mOsuStatus != null) {
            return wifiInfo.isOsuAp() && this.mOsuStatus != null && b2;
        }
        if (wifiInfo.isPasspointAp() || this.isPasspoint()) {
            return wifiInfo.isPasspointAp() && this.isPasspoint() && TextUtils.equals((CharSequence)wifiInfo.getPasspointFqdn(), (CharSequence)this.mConfig.FQDN) && TextUtils.equals((CharSequence)wifiInfo.getPasspointProviderFriendlyName(), (CharSequence)this.mConfig.providerFriendlyName) && b;
        }
        final int networkId = this.networkId;
        if (networkId != -1) {
            if (networkId != wifiInfo.getNetworkId()) {
                b3 = false;
            }
            return b3;
        }
        if (wifiConfiguration != null) {
            return this.matches(wifiConfiguration, wifiInfo);
        }
        return TextUtils.equals((CharSequence)removeDoubleQuotes(wifiInfo.getSSID()), (CharSequence)this.ssid);
    }
    
    private static boolean isOweTransitionMode(final ScanResult scanResult) {
        return scanResult.capabilities.contains("OWE_TRANSITION");
    }
    
    private static boolean isPskSaeTransitionMode(final ScanResult scanResult) {
        return scanResult.capabilities.contains("PSK") && scanResult.capabilities.contains("SAE");
    }
    
    private boolean isSameSsidOrBssid(final ScanResult scanResult) {
        if (scanResult == null) {
            return false;
        }
        if (TextUtils.equals((CharSequence)this.ssid, (CharSequence)scanResult.SSID)) {
            return true;
        }
        final String bssid = scanResult.BSSID;
        return bssid != null && TextUtils.equals((CharSequence)this.bssid, (CharSequence)bssid);
    }
    
    private boolean isSameSsidOrBssid(final WifiInfo wifiInfo) {
        return wifiInfo != null && (TextUtils.equals((CharSequence)this.ssid, (CharSequence)removeDoubleQuotes(wifiInfo.getSSID())) || (wifiInfo.getBSSID() != null && TextUtils.equals((CharSequence)this.bssid, (CharSequence)wifiInfo.getBSSID())));
    }
    
    private static boolean isVerboseLoggingEnabled() {
        return WifiTracker.sVerboseLogging || Log.isLoggable("SettingsLib.AccessPoint", 2);
    }
    
    private boolean matches(final WifiConfiguration wifiConfiguration, final WifiInfo wifiInfo) {
        return wifiConfiguration != null && wifiInfo != null && (wifiConfiguration.isPasspoint() || this.isSameSsidOrBssid(wifiInfo)) && this.matches(wifiConfiguration);
    }
    
    static String removeDoubleQuotes(final String s) {
        if (TextUtils.isEmpty((CharSequence)s)) {
            return "";
        }
        int length = s.length();
        String substring = s;
        if (length > 1) {
            substring = s;
            if (s.charAt(0) == '\"') {
                --length;
                substring = s;
                if (s.charAt(length) == '\"') {
                    substring = s.substring(1, length);
                }
            }
        }
        return substring;
    }
    
    private static int roundToClosestSpeedEnum(final int n) {
        if (n < 5) {
            return 0;
        }
        if (n < 7) {
            return 5;
        }
        if (n < 15) {
            return 10;
        }
        if (n < 25) {
            return 20;
        }
        return 30;
    }
    
    public static String securityToString(final int n, final int n2) {
        if (n == 1) {
            return "WEP";
        }
        if (n == 2) {
            if (n2 == 1) {
                return "WPA";
            }
            if (n2 == 2) {
                return "WPA2";
            }
            if (n2 == 3) {
                return "WPA_WPA2";
            }
            return "PSK";
        }
        else {
            if (n == 3) {
                return "EAP";
            }
            if (n == 5) {
                return "SAE";
            }
            if (n == 6) {
                return "SUITE_B";
            }
            if (n == 4) {
                return "OWE";
            }
            return "NONE";
        }
    }
    
    private void updateBestRssiInfo() {
        if (this.isActive()) {
            return;
        }
        ScanResult scanResult = null;
        synchronized (this.mLock) {
            final Iterator iterator = this.mScanResults.iterator();
            int level = Integer.MIN_VALUE;
            while (iterator.hasNext()) {
                final ScanResult scanResult2 = iterator.next();
                if (scanResult2.level > level) {
                    level = scanResult2.level;
                    scanResult = scanResult2;
                }
            }
            // monitorexit(this.mLock)
            Label_0114: {
                if (level != Integer.MIN_VALUE) {
                    final int mRssi = this.mRssi;
                    if (mRssi != Integer.MIN_VALUE) {
                        this.mRssi = (mRssi + level) / 2;
                        break Label_0114;
                    }
                }
                this.mRssi = level;
            }
            if (scanResult != null) {
                this.ssid = scanResult.SSID;
                this.bssid = scanResult.BSSID;
                final int security = getSecurity(this.mContext, scanResult);
                this.security = security;
                if (security == 2 || security == 5) {
                    this.pskType = getPskType(scanResult);
                }
                if (this.security == 3) {
                    getEapType(scanResult);
                }
                this.mIsPskSaeTransitionMode = isPskSaeTransitionMode(scanResult);
                this.mIsOweTransitionMode = isOweTransitionMode(scanResult);
            }
            if (this.isPasspoint()) {
                this.mConfig.SSID = convertToQuotedString(this.ssid);
            }
        }
    }
    
    private void updateKey() {
        if (this.isPasspoint()) {
            this.mKey = getKey(this.mConfig);
        }
        else if (this.isPasspointConfig()) {
            this.mKey = getKey(this.mPasspointUniqueId);
        }
        else if (this.isOsuProvider()) {
            this.mKey = getKey(this.mOsuProvider);
        }
        else {
            this.mKey = getKey(this.getSsidStr(), this.getBssid(), this.getSecurity());
        }
    }
    
    private boolean updateMetered(final WifiNetworkScoreCache wifiNetworkScoreCache) {
        final boolean mIsScoredNetworkMetered = this.mIsScoredNetworkMetered;
        boolean b = false;
        this.mIsScoredNetworkMetered = false;
        Label_0140: {
            if (this.isActive()) {
                final WifiInfo mInfo = this.mInfo;
                if (mInfo != null) {
                    final ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(NetworkKey.createFromWifiInfo(mInfo));
                    if (scoredNetwork != null) {
                        this.mIsScoredNetworkMetered |= scoredNetwork.meteredHint;
                        break Label_0140;
                    }
                    break Label_0140;
                }
            }
            synchronized (this.mLock) {
                final Iterator iterator = this.mScanResults.iterator();
                while (iterator.hasNext()) {
                    final ScoredNetwork scoredNetwork2 = wifiNetworkScoreCache.getScoredNetwork((ScanResult)iterator.next());
                    if (scoredNetwork2 == null) {
                        continue;
                    }
                    this.mIsScoredNetworkMetered |= scoredNetwork2.meteredHint;
                }
                // monitorexit(this.mLock)
                if (mIsScoredNetworkMetered != this.mIsScoredNetworkMetered) {
                    b = true;
                }
                return b;
            }
        }
    }
    
    private boolean updateScores(final WifiNetworkScoreCache wifiNetworkScoreCache, final long n) {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        synchronized (this.mLock) {
            for (final ScanResult scanResult : this.mScanResults) {
                final ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(scanResult);
                if (scoredNetwork == null) {
                    continue;
                }
                final TimestampedScoredNetwork timestampedScoredNetwork = this.mScoredNetworkCache.get(scanResult.BSSID);
                if (timestampedScoredNetwork == null) {
                    this.mScoredNetworkCache.put(scanResult.BSSID, new TimestampedScoredNetwork(scoredNetwork, elapsedRealtime));
                }
                else {
                    timestampedScoredNetwork.update(scoredNetwork, elapsedRealtime);
                }
            }
            // monitorexit(this.mLock)
            final Iterator<TimestampedScoredNetwork> iterator2 = this.mScoredNetworkCache.values().iterator();
            iterator2.forEachRemaining(new _$$Lambda$AccessPoint$OIXfUc7y1PqI_zmQ3STe_086YzY(elapsedRealtime - n, iterator2));
            return this.updateSpeed();
        }
    }
    
    private boolean updateSpeed() {
        final int mSpeed = this.mSpeed;
        final int generateAverageSpeedForSsid = this.generateAverageSpeedForSsid();
        this.mSpeed = generateAverageSpeedForSsid;
        final boolean b = mSpeed != generateAverageSpeedForSsid;
        if (isVerboseLoggingEnabled() && b) {
            Log.i("SettingsLib.AccessPoint", String.format("%s: Set speed to %d", this.ssid, this.mSpeed));
        }
        return b;
    }
    
    @Override
    public int compareTo(final AccessPoint accessPoint) {
        if (this.isActive() && !accessPoint.isActive()) {
            return -1;
        }
        if (!this.isActive() && accessPoint.isActive()) {
            return 1;
        }
        if (this.isReachable() && !accessPoint.isReachable()) {
            return -1;
        }
        if (!this.isReachable() && accessPoint.isReachable()) {
            return 1;
        }
        if (this.isSaved() && !accessPoint.isSaved()) {
            return -1;
        }
        if (!this.isSaved() && accessPoint.isSaved()) {
            return 1;
        }
        if (this.getSpeed() != accessPoint.getSpeed()) {
            return accessPoint.getSpeed() - this.getSpeed();
        }
        final WifiManager wifiManager = this.getWifiManager();
        final int n = wifiManager.calculateSignalLevel(accessPoint.mRssi) - wifiManager.calculateSignalLevel(this.mRssi);
        if (n != 0) {
            return n;
        }
        final int compareToIgnoreCase = this.getTitle().compareToIgnoreCase(accessPoint.getTitle());
        if (compareToIgnoreCase != 0) {
            return compareToIgnoreCase;
        }
        return this.getSsidStr().compareTo(accessPoint.getSsidStr());
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = o instanceof AccessPoint;
        boolean b2 = false;
        if (!b) {
            return false;
        }
        if (this.compareTo((AccessPoint)o) == 0) {
            b2 = true;
        }
        return b2;
    }
    
    public void generateOpenNetworkConfig() {
        if (!this.isOpenNetwork()) {
            throw new IllegalStateException();
        }
        if (this.mConfig != null) {
            return;
        }
        final WifiConfiguration mConfig = new WifiConfiguration();
        this.mConfig = mConfig;
        mConfig.SSID = convertToQuotedString(this.ssid);
        if (this.security == 0) {
            this.mConfig.allowedKeyManagement.set(0);
        }
        else {
            this.mConfig.allowedKeyManagement.set(9);
            this.mConfig.requirePmf = true;
        }
    }
    
    public String getBssid() {
        return this.bssid;
    }
    
    public WifiConfiguration getConfig() {
        return this.mConfig;
    }
    
    public NetworkInfo$DetailedState getDetailedState() {
        final NetworkInfo mNetworkInfo = this.mNetworkInfo;
        if (mNetworkInfo != null) {
            return mNetworkInfo.getDetailedState();
        }
        Log.w("SettingsLib.AccessPoint", "NetworkInfo is null, cannot return detailed state");
        return null;
    }
    
    public WifiInfo getInfo() {
        return this.mInfo;
    }
    
    public String getKey() {
        return this.mKey;
    }
    
    public int getLevel() {
        return this.getWifiManager().calculateSignalLevel(this.mRssi);
    }
    
    public Set<ScanResult> getScanResults() {
        final ArraySet set = new ArraySet();
        synchronized (this.mLock) {
            ((Set)set).addAll((Collection)this.mScanResults);
            ((Set)set).addAll((Collection)this.mExtraScanResults);
            return (Set<ScanResult>)set;
        }
    }
    
    public Map<String, TimestampedScoredNetwork> getScoredNetworkCache() {
        return this.mScoredNetworkCache;
    }
    
    public int getSecurity() {
        return this.security;
    }
    
    public String getSettingsSummary() {
        return this.getSettingsSummary(false);
    }
    
    public String getSettingsSummary(final boolean b) {
        if (this.isPasspointConfigurationR1() && this.isExpired()) {
            return this.mContext.getString(R$string.wifi_passpoint_expired);
        }
        final StringBuilder sb = new StringBuilder();
        if (this.isOsuProvider()) {
            if (this.mOsuProvisioningComplete) {
                sb.append(this.mContext.getString(R$string.osu_sign_up_complete));
            }
            else {
                final String mOsuFailure = this.mOsuFailure;
                if (mOsuFailure != null) {
                    sb.append(mOsuFailure);
                }
                else {
                    final String mOsuStatus = this.mOsuStatus;
                    if (mOsuStatus != null) {
                        sb.append(mOsuStatus);
                    }
                    else {
                        sb.append(this.mContext.getString(R$string.tap_to_sign_up));
                    }
                }
            }
        }
        else if (this.isActive()) {
            final Context mContext = this.mContext;
            final NetworkInfo$DetailedState detailedState = this.getDetailedState();
            final WifiInfo mInfo = this.mInfo;
            final boolean b2 = mInfo != null && mInfo.isEphemeral();
            final WifiInfo mInfo2 = this.mInfo;
            String requestingPackageName;
            if (mInfo2 != null) {
                requestingPackageName = mInfo2.getRequestingPackageName();
            }
            else {
                requestingPackageName = null;
            }
            sb.append(getSummary(mContext, null, detailedState, b2, requestingPackageName));
        }
        else {
            final WifiConfiguration mConfig = this.mConfig;
            if (mConfig != null && mConfig.hasNoInternetAccess()) {
                int n;
                if (this.mConfig.getNetworkSelectionStatus().getNetworkSelectionStatus() == 2) {
                    n = R$string.wifi_no_internet_no_reconnect;
                }
                else {
                    n = R$string.wifi_no_internet;
                }
                sb.append(this.mContext.getString(n));
            }
            else {
                final WifiConfiguration mConfig2 = this.mConfig;
                if (mConfig2 != null && mConfig2.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
                    final int networkSelectionDisableReason = this.mConfig.getNetworkSelectionStatus().getNetworkSelectionDisableReason();
                    if (networkSelectionDisableReason != 1) {
                        if (networkSelectionDisableReason != 2) {
                            if (networkSelectionDisableReason != 3) {
                                if (networkSelectionDisableReason == 8) {
                                    sb.append(this.mContext.getString(R$string.wifi_check_password_try_again));
                                }
                            }
                            else {
                                sb.append(this.mContext.getString(R$string.wifi_disabled_network_failure));
                            }
                        }
                        else {
                            sb.append(this.mContext.getString(R$string.wifi_disabled_password_failure));
                        }
                    }
                    else {
                        sb.append(this.mContext.getString(R$string.wifi_disabled_generic));
                    }
                }
                else if (!this.isReachable()) {
                    sb.append(this.mContext.getString(R$string.wifi_not_in_range));
                }
                else {
                    final WifiConfiguration mConfig3 = this.mConfig;
                    if (mConfig3 != null) {
                        if (mConfig3.getRecentFailureReason() != 17) {
                            if (b) {
                                sb.append(this.mContext.getString(R$string.wifi_disconnected));
                            }
                            else {
                                sb.append(this.mContext.getString(R$string.wifi_remembered));
                            }
                        }
                        else {
                            sb.append(this.mContext.getString(R$string.wifi_ap_unable_to_handle_new_sta));
                        }
                    }
                }
            }
        }
        if (isVerboseLoggingEnabled()) {
            sb.append(WifiUtils.buildLoggingSummary(this, this.mConfig));
        }
        final WifiConfiguration mConfig4 = this.mConfig;
        if (mConfig4 != null && (WifiUtils.isMeteredOverridden(mConfig4) || this.mConfig.meteredHint)) {
            return this.mContext.getResources().getString(R$string.preference_summary_default_combination, new Object[] { WifiUtils.getMeteredLabel(this.mContext, this.mConfig), sb.toString() });
        }
        if (this.getSpeedLabel() != null && sb.length() != 0) {
            return this.mContext.getResources().getString(R$string.preference_summary_default_combination, new Object[] { this.getSpeedLabel(), sb.toString() });
        }
        if (this.getSpeedLabel() != null) {
            return this.getSpeedLabel();
        }
        return sb.toString();
    }
    
    int getSpeed() {
        return this.mSpeed;
    }
    
    String getSpeedLabel() {
        return this.getSpeedLabel(this.mSpeed);
    }
    
    String getSpeedLabel(final int n) {
        return getSpeedLabel(this.mContext, n);
    }
    
    public CharSequence getSsid() {
        return this.ssid;
    }
    
    public String getSsidStr() {
        return this.ssid;
    }
    
    public String getSummary() {
        return this.getSettingsSummary();
    }
    
    public String getTitle() {
        if (this.isPasspoint()) {
            return this.mConfig.providerFriendlyName;
        }
        if (this.isPasspointConfig()) {
            return this.mProviderFriendlyName;
        }
        if (this.isOsuProvider()) {
            return this.mOsuProvider.getFriendlyName();
        }
        return this.getSsidStr();
    }
    
    @Override
    public int hashCode() {
        final WifiInfo mInfo = this.mInfo;
        int n = 0;
        if (mInfo != null) {
            n = 0 + mInfo.hashCode() * 13;
        }
        return n + this.mRssi * 19 + this.networkId * 23 + this.ssid.hashCode() * 29;
    }
    
    public boolean isActive() {
        final NetworkInfo mNetworkInfo = this.mNetworkInfo;
        return mNetworkInfo != null && (this.networkId != -1 || mNetworkInfo.getState() != NetworkInfo$State.DISCONNECTED);
    }
    
    public boolean isConnectable() {
        return this.getLevel() != -1 && this.getDetailedState() == null;
    }
    
    public boolean isEphemeral() {
        final WifiInfo mInfo = this.mInfo;
        if (mInfo != null && mInfo.isEphemeral()) {
            final NetworkInfo mNetworkInfo = this.mNetworkInfo;
            if (mNetworkInfo != null && mNetworkInfo.getState() != NetworkInfo$State.DISCONNECTED) {
                return true;
            }
        }
        return false;
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
    
    public boolean isMetered() {
        return this.mIsScoredNetworkMetered || WifiConfiguration.isMetered(this.mConfig, this.mInfo);
    }
    
    public boolean isOpenNetwork() {
        final int security = this.security;
        return security == 0 || security == 4;
    }
    
    public boolean isOsuProvider() {
        return this.mOsuProvider != null;
    }
    
    public boolean isPasspoint() {
        final WifiConfiguration mConfig = this.mConfig;
        return mConfig != null && mConfig.isPasspoint();
    }
    
    public boolean isPasspointConfig() {
        return this.mPasspointUniqueId != null && this.mConfig == null;
    }
    
    public boolean isPasspointConfigurationR1() {
        final int mPasspointConfigurationVersion = this.mPasspointConfigurationVersion;
        boolean b = true;
        if (mPasspointConfigurationVersion != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isReachable() {
        return this.mRssi != Integer.MIN_VALUE;
    }
    
    public boolean isSaved() {
        return this.mConfig != null;
    }
    
    @VisibleForTesting
    void loadConfig(final WifiConfiguration mConfig) {
        final String ssid = mConfig.SSID;
        String removeDoubleQuotes;
        if (ssid == null) {
            removeDoubleQuotes = "";
        }
        else {
            removeDoubleQuotes = removeDoubleQuotes(ssid);
        }
        this.ssid = removeDoubleQuotes;
        this.bssid = mConfig.BSSID;
        this.security = getSecurity(mConfig);
        this.networkId = mConfig.networkId;
        this.mConfig = mConfig;
    }
    
    @VisibleForTesting
    boolean matches(final ScanResult scanResult) {
        boolean b = false;
        if (scanResult == null) {
            return false;
        }
        if (this.isPasspoint() || this.isOsuProvider()) {
            throw new IllegalStateException("Should not matches a Passpoint by ScanResult");
        }
        if (!this.isSameSsidOrBssid(scanResult)) {
            return false;
        }
        if (this.mIsPskSaeTransitionMode) {
            if (scanResult.capabilities.contains("SAE") && this.getWifiManager().isWpa3SaeSupported()) {
                return true;
            }
            if (scanResult.capabilities.contains("PSK")) {
                return true;
            }
        }
        else {
            final int security = this.security;
            if ((security == 5 || security == 2) && isPskSaeTransitionMode(scanResult)) {
                return true;
            }
        }
        if (this.mIsOweTransitionMode) {
            final int security2 = getSecurity(this.mContext, scanResult);
            if (security2 == 4 && this.getWifiManager().isEnhancedOpenSupported()) {
                return true;
            }
            if (security2 == 0) {
                return true;
            }
        }
        else {
            final int security3 = this.security;
            if ((security3 == 4 || security3 == 0) && isOweTransitionMode(scanResult)) {
                return true;
            }
        }
        if (this.security == getSecurity(this.mContext, scanResult)) {
            b = true;
        }
        return b;
    }
    
    public boolean matches(final WifiConfiguration wifiConfiguration) {
        final boolean passpoint = wifiConfiguration.isPasspoint();
        final boolean b = false;
        final boolean b2 = false;
        if (passpoint) {
            boolean b3 = b2;
            if (this.isPasspoint()) {
                b3 = b2;
                if (wifiConfiguration.getKey().equals(this.mConfig.getKey())) {
                    b3 = true;
                }
            }
            return b3;
        }
        boolean b4 = b;
        if (this.ssid.equals(removeDoubleQuotes(wifiConfiguration.SSID))) {
            final WifiConfiguration mConfig = this.mConfig;
            if (mConfig != null && mConfig.shared != wifiConfiguration.shared) {
                b4 = b;
            }
            else {
                final int security = getSecurity(wifiConfiguration);
                if (this.mIsPskSaeTransitionMode) {
                    if (security == 5 && this.getWifiManager().isWpa3SaeSupported()) {
                        return true;
                    }
                    if (security == 2) {
                        return true;
                    }
                }
                if (this.mIsOweTransitionMode) {
                    if (security == 4 && this.getWifiManager().isEnhancedOpenSupported()) {
                        return true;
                    }
                    if (security == 0) {
                        return true;
                    }
                }
                b4 = b;
                if (this.security == getSecurity(wifiConfiguration)) {
                    b4 = true;
                }
            }
        }
        return b4;
    }
    
    @VisibleForTesting
    void setRssi(final int mRssi) {
        this.mRssi = mRssi;
    }
    
    void setScanResults(final Collection<ScanResult> collection) {
        if (CollectionUtils.isEmpty((Collection)collection)) {
            Log.d("SettingsLib.AccessPoint", "Cannot set scan results to empty list");
            return;
        }
        if (this.mKey != null && !this.isPasspoint() && !this.isOsuProvider()) {
            for (final ScanResult scanResult : collection) {
                if (!this.matches(scanResult)) {
                    Log.d("SettingsLib.AccessPoint", String.format("ScanResult %s\nkey of %s did not match current AP key %s", scanResult, getKey(this.mContext, scanResult), this.mKey));
                    return;
                }
            }
        }
        final int level = this.getLevel();
        synchronized (this.mLock) {
            this.mScanResults.clear();
            this.mScanResults.addAll((Collection)collection);
            // monitorexit(this.mLock)
            this.updateBestRssiInfo();
            final int level2 = this.getLevel();
            if (level2 > 0 && level2 != level) {
                this.updateSpeed();
                ThreadUtils.postOnMainThread(new _$$Lambda$AccessPoint$MkkIS1nUbezHicDMmYnviyiBJyo(this));
            }
            ThreadUtils.postOnMainThread(new _$$Lambda$AccessPoint$0Yq14aFJZLjPMzFGAvglLaxsblI(this));
        }
    }
    
    void setScanResultsPasspoint(final Collection<ScanResult> scanResults, final Collection<ScanResult> scanResults2) {
        synchronized (this.mLock) {
            this.mExtraScanResults.clear();
            if (!CollectionUtils.isEmpty((Collection)scanResults)) {
                if (!CollectionUtils.isEmpty((Collection)scanResults2)) {
                    this.mExtraScanResults.addAll((Collection)scanResults2);
                }
                this.setScanResults(scanResults);
            }
            else if (!CollectionUtils.isEmpty((Collection)scanResults2)) {
                this.setScanResults(scanResults2);
            }
        }
    }
    
    public void setTag(final Object mTag) {
        this.mTag = mTag;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AccessPoint(");
        sb.append(this.ssid);
        if (this.bssid != null) {
            sb.append(":");
            sb.append(this.bssid);
        }
        if (this.isSaved()) {
            sb.append(',');
            sb.append("saved");
        }
        if (this.isActive()) {
            sb.append(',');
            sb.append("active");
        }
        if (this.isEphemeral()) {
            sb.append(',');
            sb.append("ephemeral");
        }
        if (this.isConnectable()) {
            sb.append(',');
            sb.append("connectable");
        }
        final int security = this.security;
        if (security != 0 && security != 4) {
            sb.append(',');
            sb.append(securityToString(this.security, this.pskType));
        }
        sb.append(",level=");
        sb.append(this.getLevel());
        if (this.mSpeed != 0) {
            sb.append(",speed=");
            sb.append(this.mSpeed);
        }
        sb.append(",metered=");
        sb.append(this.isMetered());
        if (isVerboseLoggingEnabled()) {
            sb.append(",rssi=");
            sb.append(this.mRssi);
            synchronized (this.mLock) {
                sb.append(",scan cache size=");
                sb.append(this.mScanResults.size() + this.mExtraScanResults.size());
            }
        }
        sb.append(')');
        return sb.toString();
    }
    
    void update(final WifiConfiguration mConfig) {
        this.mConfig = mConfig;
        if (mConfig != null && !this.isPasspoint()) {
            this.ssid = removeDoubleQuotes(this.mConfig.SSID);
        }
        int networkId;
        if (mConfig != null) {
            networkId = mConfig.networkId;
        }
        else {
            networkId = -1;
        }
        this.networkId = networkId;
        ThreadUtils.postOnMainThread(new _$$Lambda$AccessPoint$QyP0aXhFuWtm7lmBu1IY3qbfmBA(this));
    }
    
    public boolean update(final WifiConfiguration wifiConfiguration, final WifiInfo mInfo, final NetworkInfo mNetworkInfo) {
        final int level = this.getLevel();
        boolean b = false;
        boolean b2 = false;
        if (mInfo != null && this.isInfoForThisAccessPoint(wifiConfiguration, mInfo)) {
            if (this.mInfo == null) {
                b2 = true;
            }
            if (!this.isPasspoint() && this.mConfig != wifiConfiguration) {
                this.update(wifiConfiguration);
            }
            Label_0128: {
                if (this.mRssi != mInfo.getRssi() && mInfo.getRssi() != -127) {
                    this.mRssi = mInfo.getRssi();
                }
                else {
                    final NetworkInfo mNetworkInfo2 = this.mNetworkInfo;
                    b = b2;
                    if (mNetworkInfo2 == null) {
                        break Label_0128;
                    }
                    b = b2;
                    if (mNetworkInfo == null) {
                        break Label_0128;
                    }
                    b = b2;
                    if (mNetworkInfo2.getDetailedState() == mNetworkInfo.getDetailedState()) {
                        break Label_0128;
                    }
                }
                b = true;
            }
            this.mInfo = mInfo;
            this.mNetworkInfo = mNetworkInfo;
        }
        else if (this.mInfo != null) {
            this.mInfo = null;
            this.mNetworkInfo = null;
            b = true;
        }
        if (b && this.mAccessPointListener != null) {
            ThreadUtils.postOnMainThread(new _$$Lambda$AccessPoint$S7H59e_8IxpVPy0V68Oc2_zX_rg(this));
            if (level != this.getLevel()) {
                ThreadUtils.postOnMainThread(new _$$Lambda$AccessPoint$QW_1Uw0oxoaKqUtEtPO0oPvH5ng(this));
            }
        }
        return b;
    }
    
    boolean update(final WifiNetworkScoreCache wifiNetworkScoreCache, final boolean b, final long n) {
        boolean b2 = false;
        final boolean b3 = b && this.updateScores(wifiNetworkScoreCache, n);
        if (this.updateMetered(wifiNetworkScoreCache) || b3) {
            b2 = true;
        }
        return b2;
    }
    
    public interface AccessPointListener
    {
        void onAccessPointChanged(final AccessPoint p0);
        
        void onLevelChanged(final AccessPoint p0);
    }
    
    @VisibleForTesting
    class AccessPointProvisioningCallback extends ProvisioningCallback
    {
        final /* synthetic */ AccessPoint this$0;
        
        public void onProvisioningComplete() {
            this.this$0.mOsuProvisioningComplete = true;
            this.this$0.mOsuFailure = null;
            this.this$0.mOsuStatus = null;
            ThreadUtils.postOnMainThread(new _$$Lambda$AccessPoint$AccessPointProvisioningCallback$8NkGPNV0jfGEnIZHmtcNMYE5Q7Q(this));
            final WifiManager access$500 = AccessPoint.this.getWifiManager();
            final PasspointConfiguration passpointConfiguration = access$500.getMatchingPasspointConfigsForOsuProviders((Set)Collections.singleton(this.this$0.mOsuProvider)).get(this.this$0.mOsuProvider);
            if (passpointConfiguration == null) {
                Log.e("SettingsLib.AccessPoint", "Missing PasspointConfiguration for newly provisioned network!");
                if (this.this$0.mConnectListener != null) {
                    this.this$0.mConnectListener.onFailure(0);
                }
                return;
            }
            final String uniqueId = passpointConfiguration.getUniqueId();
            for (final Pair pair : access$500.getAllMatchingWifiConfigs(access$500.getScanResults())) {
                final WifiConfiguration wifiConfiguration = (WifiConfiguration)pair.first;
                if (TextUtils.equals((CharSequence)wifiConfiguration.getKey(), (CharSequence)uniqueId)) {
                    access$500.connect(new AccessPoint(this.this$0.mContext, wifiConfiguration, ((Map)pair.second).get(0), ((Map)pair.second).get(1)).getConfig(), this.this$0.mConnectListener);
                    return;
                }
            }
            if (this.this$0.mConnectListener != null) {
                this.this$0.mConnectListener.onFailure(0);
            }
        }
        
        public void onProvisioningFailure(final int n) {
            if (TextUtils.equals((CharSequence)this.this$0.mOsuStatus, (CharSequence)this.this$0.mContext.getString(R$string.osu_completing_sign_up))) {
                final AccessPoint this$0 = this.this$0;
                this$0.mOsuFailure = this$0.mContext.getString(R$string.osu_sign_up_failed);
            }
            else {
                final AccessPoint this$2 = this.this$0;
                this$2.mOsuFailure = this$2.mContext.getString(R$string.osu_connect_failed);
            }
            this.this$0.mOsuStatus = null;
            this.this$0.mOsuProvisioningComplete = false;
            ThreadUtils.postOnMainThread(new _$$Lambda$AccessPoint$AccessPointProvisioningCallback$74qKnAJvzvRGvsJDwRIri14jOnQ(this));
        }
        
        public void onProvisioningStatus(final int n) {
            String s = null;
            switch (n) {
                default: {
                    s = null;
                    break;
                }
                case 8:
                case 9:
                case 10:
                case 11: {
                    s = this.this$0.mContext.getString(R$string.osu_completing_sign_up);
                    break;
                }
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    s = String.format(this.this$0.mContext.getString(R$string.osu_opening_provider), this.this$0.mOsuProvider.getFriendlyName());
                    break;
                }
            }
            final boolean equals = TextUtils.equals((CharSequence)this.this$0.mOsuStatus, (CharSequence)s);
            this.this$0.mOsuStatus = s;
            this.this$0.mOsuFailure = null;
            this.this$0.mOsuProvisioningComplete = false;
            if (true ^ equals) {
                ThreadUtils.postOnMainThread(new _$$Lambda$AccessPoint$AccessPointProvisioningCallback$ko59tOsAuz6AC9y5Nq_UikXZo9s(this));
            }
        }
    }
}
