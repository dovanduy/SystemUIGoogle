// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import android.net.NetworkCapabilities;
import android.net.Network;
import java.util.Objects;
import com.android.settingslib.utils.ThreadUtils;
import android.widget.Toast;
import com.android.settingslib.R$string;
import android.os.Message;
import android.net.ScoredNetwork;
import android.net.wifi.WifiNetworkScoreCache$CacheListener;
import android.content.ContentResolver;
import android.net.ConnectivityManager$NetworkCallback;
import android.provider.Settings$Global;
import java.io.PrintWriter;
import java.util.Optional;
import android.util.ArrayMap;
import java.util.Collections;
import android.util.Pair;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Map;
import android.net.INetworkScoreCache;
import android.net.wifi.hotspot2.OsuProvider;
import java.util.Collection;
import java.util.ListIterator;
import android.net.wifi.WifiConfiguration;
import java.util.Iterator;
import android.os.SystemClock;
import android.util.Log;
import android.net.NetworkRequest$Builder;
import android.content.Intent;
import android.util.ArraySet;
import java.util.ArrayList;
import android.os.HandlerThread;
import android.os.Handler;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.ScanResult;
import java.util.HashMap;
import android.net.NetworkKey;
import java.util.Set;
import android.content.BroadcastReceiver;
import android.net.NetworkScoreManager;
import android.net.NetworkRequest;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import java.util.List;
import android.content.IntentFilter;
import android.content.Context;
import android.net.ConnectivityManager;
import java.util.concurrent.atomic.AtomicBoolean;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.LifecycleObserver;

public class WifiTracker implements LifecycleObserver, OnStart, OnStop, OnDestroy
{
    static final long MAX_SCAN_RESULT_AGE_MILLIS = 15000L;
    public static boolean sVerboseLogging;
    private final AtomicBoolean mConnected;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final IntentFilter mFilter;
    private final List<AccessPoint> mInternalAccessPoints;
    private WifiInfo mLastInfo;
    private NetworkInfo mLastNetworkInfo;
    private boolean mLastScanSucceeded;
    private final WifiListenerExecutor mListener;
    private final Object mLock;
    private long mMaxSpeedLabelScoreCacheAge;
    private WifiTrackerNetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    private final NetworkScoreManager mNetworkScoreManager;
    private boolean mNetworkScoringUiEnabled;
    final BroadcastReceiver mReceiver;
    private boolean mRegistered;
    private final Set<NetworkKey> mRequestedScores;
    private final HashMap<String, ScanResult> mScanResultCache;
    Scanner mScanner;
    private WifiNetworkScoreCache mScoreCache;
    private boolean mStaleScanResults;
    private final WifiManager mWifiManager;
    Handler mWorkHandler;
    private HandlerThread mWorkThread;
    
    WifiTracker(final Context mContext, final WifiListener wifiListener, final WifiManager mWifiManager, final ConnectivityManager mConnectivityManager, final NetworkScoreManager mNetworkScoreManager, final IntentFilter mFilter) {
        final boolean b = false;
        this.mConnected = new AtomicBoolean(false);
        this.mLock = new Object();
        this.mInternalAccessPoints = new ArrayList<AccessPoint>();
        this.mRequestedScores = (Set<NetworkKey>)new ArraySet();
        this.mStaleScanResults = true;
        this.mLastScanSucceeded = true;
        this.mScanResultCache = new HashMap<String, ScanResult>();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                    WifiTracker.this.updateWifiState(intent.getIntExtra("wifi_state", 4));
                }
                else if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
                    WifiTracker.this.mStaleScanResults = false;
                    WifiTracker.this.mLastScanSucceeded = intent.getBooleanExtra("resultsUpdated", true);
                    WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                }
                else if (!"android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action) && !"android.net.wifi.LINK_CONFIGURATION_CHANGED".equals(action)) {
                    if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                        WifiTracker.this.updateNetworkInfo((NetworkInfo)intent.getParcelableExtra("networkInfo"));
                        WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                    }
                    else if ("android.net.wifi.RSSI_CHANGED".equals(action)) {
                        WifiTracker.this.updateNetworkInfo(WifiTracker.this.mConnectivityManager.getNetworkInfo(WifiTracker.this.mWifiManager.getCurrentNetwork()));
                    }
                }
                else {
                    WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                }
            }
        };
        this.mContext = mContext;
        this.mWifiManager = mWifiManager;
        this.mListener = new WifiListenerExecutor(wifiListener);
        this.mConnectivityManager = mConnectivityManager;
        final WifiManager mWifiManager2 = this.mWifiManager;
        boolean sVerboseLogging = b;
        if (mWifiManager2 != null) {
            sVerboseLogging = b;
            if (mWifiManager2.isVerboseLoggingEnabled()) {
                sVerboseLogging = true;
            }
        }
        WifiTracker.sVerboseLogging = sVerboseLogging;
        this.mFilter = mFilter;
        this.mNetworkRequest = new NetworkRequest$Builder().clearCapabilities().addCapability(15).addTransportType(1).build();
        this.mNetworkScoreManager = mNetworkScoreManager;
        final StringBuilder sb = new StringBuilder();
        sb.append("WifiTracker{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append("}");
        final HandlerThread workThread = new HandlerThread(sb.toString(), 10);
        workThread.start();
        this.setWorkThread(workThread);
    }
    
    @Deprecated
    public WifiTracker(final Context context, final WifiListener wifiListener, final boolean b, final boolean b2) {
        this(context, wifiListener, (WifiManager)context.getSystemService((Class)WifiManager.class), (ConnectivityManager)context.getSystemService((Class)ConnectivityManager.class), (NetworkScoreManager)context.getSystemService((Class)NetworkScoreManager.class), newIntentFilter());
    }
    
    private static final boolean DBG() {
        return Log.isLoggable("WifiTracker", 3);
    }
    
    private void clearAccessPointsAndConditionallyUpdate() {
        synchronized (this.mLock) {
            if (!this.mInternalAccessPoints.isEmpty()) {
                this.mInternalAccessPoints.clear();
                this.conditionallyNotifyListeners();
            }
        }
    }
    
    private void conditionallyNotifyListeners() {
        if (this.mStaleScanResults) {
            return;
        }
        this.mListener.onAccessPointsChanged();
    }
    
    private void evictOldScans() {
        long n;
        if (this.mLastScanSucceeded) {
            n = 15000L;
        }
        else {
            n = 30000L;
        }
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        final Iterator<ScanResult> iterator = this.mScanResultCache.values().iterator();
        while (iterator.hasNext()) {
            if (elapsedRealtime - iterator.next().timestamp / 1000L > n) {
                iterator.remove();
            }
        }
    }
    
    private void fetchScansAndConfigsAndUpdateAccessPoints() {
        final List<ScanResult> filterScanResultsByCapabilities = this.filterScanResultsByCapabilities(this.mWifiManager.getScanResults());
        if (isVerboseLoggingEnabled()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Fetched scan results: ");
            sb.append(filterScanResultsByCapabilities);
            Log.i("WifiTracker", sb.toString());
        }
        this.updateAccessPoints(filterScanResultsByCapabilities, this.mWifiManager.getConfiguredNetworks());
    }
    
    private List<ScanResult> filterScanResultsByCapabilities(final List<ScanResult> list) {
        if (list == null) {
            return null;
        }
        final boolean enhancedOpenSupported = this.mWifiManager.isEnhancedOpenSupported();
        final boolean wpa3SaeSupported = this.mWifiManager.isWpa3SaeSupported();
        final boolean wpa3SuiteBSupported = this.mWifiManager.isWpa3SuiteBSupported();
        final ArrayList<ScanResult> list2 = new ArrayList<ScanResult>();
        for (final ScanResult scanResult : list) {
            if (scanResult.capabilities.contains("PSK")) {
                list2.add(scanResult);
            }
            else if ((scanResult.capabilities.contains("SUITE_B_192") && !wpa3SuiteBSupported) || (scanResult.capabilities.contains("SAE") && !wpa3SaeSupported) || (scanResult.capabilities.contains("OWE") && !enhancedOpenSupported)) {
                if (!isVerboseLoggingEnabled()) {
                    continue;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("filterScanResultsByCapabilities: Filtering SSID ");
                sb.append(scanResult.SSID);
                sb.append(" with capabilities: ");
                sb.append(scanResult.capabilities);
                Log.v("WifiTracker", sb.toString());
            }
            else {
                list2.add(scanResult);
            }
        }
        return list2;
    }
    
    private AccessPoint getCachedByKey(final List<AccessPoint> list, final String anObject) {
        final ListIterator<AccessPoint> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            final AccessPoint accessPoint = listIterator.next();
            if (accessPoint.getKey().equals(anObject)) {
                listIterator.remove();
                return accessPoint;
            }
        }
        return null;
    }
    
    private AccessPoint getCachedOrCreate(final List<ScanResult> scanResults, final List<AccessPoint> list) {
        final AccessPoint cachedByKey = this.getCachedByKey(list, AccessPoint.getKey(this.mContext, scanResults.get(0)));
        AccessPoint accessPoint;
        if (cachedByKey == null) {
            accessPoint = new AccessPoint(this.mContext, scanResults);
        }
        else {
            cachedByKey.setScanResults(scanResults);
            accessPoint = cachedByKey;
        }
        return accessPoint;
    }
    
    private AccessPoint getCachedOrCreateOsu(final OsuProvider osuProvider, final List<ScanResult> scanResults, final List<AccessPoint> list) {
        final AccessPoint cachedByKey = this.getCachedByKey(list, AccessPoint.getKey(osuProvider));
        AccessPoint accessPoint;
        if (cachedByKey == null) {
            accessPoint = new AccessPoint(this.mContext, osuProvider, scanResults);
        }
        else {
            cachedByKey.setScanResults(scanResults);
            accessPoint = cachedByKey;
        }
        return accessPoint;
    }
    
    private AccessPoint getCachedOrCreatePasspoint(final WifiConfiguration wifiConfiguration, final List<ScanResult> list, final List<ScanResult> list2, final List<AccessPoint> list3) {
        final AccessPoint cachedByKey = this.getCachedByKey(list3, AccessPoint.getKey(wifiConfiguration));
        AccessPoint accessPoint;
        if (cachedByKey == null) {
            accessPoint = new AccessPoint(this.mContext, wifiConfiguration, list, list2);
        }
        else {
            cachedByKey.update(wifiConfiguration);
            cachedByKey.setScanResultsPasspoint(list, list2);
            accessPoint = cachedByKey;
        }
        return accessPoint;
    }
    
    private WifiConfiguration getWifiConfigurationForNetworkId(final int n, final List<WifiConfiguration> list) {
        if (list != null) {
            for (final WifiConfiguration wifiConfiguration : list) {
                if (this.mLastInfo != null && n == wifiConfiguration.networkId) {
                    return wifiConfiguration;
                }
            }
        }
        return null;
    }
    
    private static boolean isSaeOrOwe(final WifiConfiguration wifiConfiguration) {
        final int security = AccessPoint.getSecurity(wifiConfiguration);
        return security == 5 || security == 4;
    }
    
    private static boolean isVerboseLoggingEnabled() {
        return WifiTracker.sVerboseLogging || Log.isLoggable("WifiTracker", 2);
    }
    
    private static IntentFilter newIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        intentFilter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        return intentFilter;
    }
    
    private void pauseScanning() {
        synchronized (this.mLock) {
            if (this.mScanner != null) {
                this.mScanner.pause();
                this.mScanner = null;
            }
            // monitorexit(this.mLock)
            this.mStaleScanResults = true;
        }
    }
    
    private void registerScoreCache() {
        this.mNetworkScoreManager.registerNetworkScoreCache(1, (INetworkScoreCache)this.mScoreCache, 2);
    }
    
    private void requestScoresForNetworkKeys(final Collection<NetworkKey> obj) {
        if (obj.isEmpty()) {
            return;
        }
        if (DBG()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Requesting scores for Network Keys: ");
            sb.append(obj);
            Log.d("WifiTracker", sb.toString());
        }
        this.mNetworkScoreManager.requestScores((NetworkKey[])obj.toArray(new NetworkKey[obj.size()]));
        synchronized (this.mLock) {
            this.mRequestedScores.addAll(obj);
        }
    }
    
    private void unregisterScoreCache() {
        this.mNetworkScoreManager.unregisterNetworkScoreCache(1, (INetworkScoreCache)this.mScoreCache);
        synchronized (this.mLock) {
            this.mRequestedScores.clear();
        }
    }
    
    private void updateAccessPoints(final List<ScanResult> list, final List<WifiConfiguration> list2) {
        final WifiInfo mLastInfo = this.mLastInfo;
        WifiConfiguration wifiConfigurationForNetworkId;
        if (mLastInfo != null) {
            wifiConfigurationForNetworkId = this.getWifiConfigurationForNetworkId(mLastInfo.getNetworkId(), list2);
        }
        else {
            wifiConfigurationForNetworkId = null;
        }
        synchronized (this.mLock) {
            final ArrayMap<String, List<ScanResult>> updateScanResultCache = this.updateScanResultCache(list);
            final ArrayList<AccessPoint> list3 = new ArrayList<AccessPoint>(this.mInternalAccessPoints);
            final ArrayList<Comparable> list4 = (ArrayList<Comparable>)new ArrayList<AccessPoint>();
            final ArrayList<NetworkKey> list5 = new ArrayList<NetworkKey>();
            for (final Map.Entry<K, List> entry : updateScanResultCache.entrySet()) {
                final Iterator<ScanResult> iterator2 = entry.getValue().iterator();
                while (iterator2.hasNext()) {
                    final NetworkKey fromScanResult = NetworkKey.createFromScanResult((ScanResult)iterator2.next());
                    if (fromScanResult != null && !this.mRequestedScores.contains(fromScanResult)) {
                        list5.add(fromScanResult);
                    }
                }
                final AccessPoint cachedOrCreate = this.getCachedOrCreate(entry.getValue(), list3);
                final List<Object> list6 = list2.stream().filter(new _$$Lambda$WifiTracker$Up3TxfI1NaJ1CulBpL22WbeQznY(cachedOrCreate)).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList());
                final int size = list6.size();
                if (size == 0) {
                    cachedOrCreate.update(null);
                }
                else if (size == 1) {
                    cachedOrCreate.update((WifiConfiguration)list6.get(0));
                }
                else {
                    final Optional<WifiConfiguration> first = list6.stream().filter((Predicate<? super WifiConfiguration>)_$$Lambda$WifiTracker$ZaDLRSIZwSj9aj6lj58U997Kj9s.INSTANCE).findFirst();
                    if (first.isPresent()) {
                        cachedOrCreate.update(first.get());
                    }
                    else {
                        cachedOrCreate.update((WifiConfiguration)list6.get(0));
                    }
                }
                list4.add(cachedOrCreate);
            }
            final ArrayList list7 = new ArrayList(this.mScanResultCache.values());
            list4.addAll(this.updatePasspointAccessPoints(this.mWifiManager.getAllMatchingWifiConfigs((List)list7), list3));
            list4.addAll(this.updateOsuAccessPoints(this.mWifiManager.getMatchingOsuProviders((List)list7), list3));
            if (this.mLastInfo != null && this.mLastNetworkInfo != null) {
                final Iterator<AccessPoint> iterator3 = list4.iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().update(wifiConfigurationForNetworkId, this.mLastInfo, this.mLastNetworkInfo);
                }
            }
            if (list4.isEmpty() && wifiConfigurationForNetworkId != null) {
                final AccessPoint e = new AccessPoint(this.mContext, wifiConfigurationForNetworkId);
                e.update(wifiConfigurationForNetworkId, this.mLastInfo, this.mLastNetworkInfo);
                list4.add(e);
                list5.add(NetworkKey.createFromWifiInfo(this.mLastInfo));
            }
            this.requestScoresForNetworkKeys(list5);
            final Iterator<AccessPoint> iterator4 = list4.iterator();
            while (iterator4.hasNext()) {
                iterator4.next().update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge);
            }
            Collections.sort(list4);
            if (DBG()) {
                Log.d("WifiTracker", "------ Dumping AccessPoints that were not seen on this scan ------");
                final Iterator<AccessPoint> iterator5 = this.mInternalAccessPoints.iterator();
            Label_0602:
                while (iterator5.hasNext()) {
                    final String title = iterator5.next().getTitle();
                    while (true) {
                        for (final AccessPoint accessPoint : list4) {
                            if (accessPoint.getTitle() != null && accessPoint.getTitle().equals(title)) {
                                final boolean b = true;
                                if (!b) {
                                    final StringBuilder sb = new StringBuilder();
                                    sb.append("Did not find ");
                                    sb.append(title);
                                    sb.append(" in this scan");
                                    Log.d("WifiTracker", sb.toString());
                                    continue Label_0602;
                                }
                                continue Label_0602;
                            }
                        }
                        final boolean b = false;
                        continue;
                    }
                }
                Log.d("WifiTracker", "---- Done dumping AccessPoints that were not seen on this scan ----");
            }
            this.mInternalAccessPoints.clear();
            this.mInternalAccessPoints.addAll((Collection<? extends AccessPoint>)list4);
            // monitorexit(this.mLock)
            this.conditionallyNotifyListeners();
        }
    }
    
    private void updateNetworkInfo(final NetworkInfo mLastNetworkInfo) {
        if (!this.isWifiEnabled()) {
            this.clearAccessPointsAndConditionallyUpdate();
            return;
        }
        if (mLastNetworkInfo != null) {
            this.mLastNetworkInfo = mLastNetworkInfo;
            if (DBG()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("mLastNetworkInfo set: ");
                sb.append(this.mLastNetworkInfo);
                Log.d("WifiTracker", sb.toString());
            }
            if (mLastNetworkInfo.isConnected() != this.mConnected.getAndSet(mLastNetworkInfo.isConnected())) {
                this.mListener.onConnectedChanged();
            }
        }
        WifiConfiguration wifiConfigurationForNetworkId = null;
        this.mLastInfo = this.mWifiManager.getConnectionInfo();
        if (DBG()) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("mLastInfo set as: ");
            sb2.append(this.mLastInfo);
            Log.d("WifiTracker", sb2.toString());
        }
        final WifiInfo mLastInfo = this.mLastInfo;
        if (mLastInfo != null) {
            wifiConfigurationForNetworkId = this.getWifiConfigurationForNetworkId(mLastInfo.getNetworkId(), this.mWifiManager.getConfiguredNetworks());
        }
        synchronized (this.mLock) {
            int i = this.mInternalAccessPoints.size() - 1;
            int n = 0;
            boolean b = false;
            while (i >= 0) {
                final AccessPoint accessPoint = this.mInternalAccessPoints.get(i);
                final boolean active = accessPoint.isActive();
                int n2 = n;
                if (accessPoint.update(wifiConfigurationForNetworkId, this.mLastInfo, this.mLastNetworkInfo)) {
                    if (active != accessPoint.isActive()) {
                        n2 = ((b = true) ? 1 : 0);
                    }
                    else {
                        b = true;
                        n2 = n;
                    }
                }
                n = n2;
                if (accessPoint.update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge)) {
                    n = ((b = true) ? 1 : 0);
                }
                --i;
            }
            if (n != 0) {
                Collections.sort(this.mInternalAccessPoints);
            }
            if (b) {
                this.conditionallyNotifyListeners();
            }
        }
    }
    
    private void updateNetworkScores() {
        final Object mLock = this.mLock;
        // monitorenter(mLock)
        int i = 0;
        boolean b = false;
        try {
            while (i < this.mInternalAccessPoints.size()) {
                if (this.mInternalAccessPoints.get(i).update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge)) {
                    b = true;
                }
                ++i;
            }
            if (b) {
                Collections.sort(this.mInternalAccessPoints);
                this.conditionallyNotifyListeners();
            }
        }
        finally {
        }
        // monitorexit(mLock)
    }
    
    private ArrayMap<String, List<ScanResult>> updateScanResultCache(final List<ScanResult> list) {
        for (final ScanResult value : list) {
            final String ssid = value.SSID;
            if (ssid != null) {
                if (ssid.isEmpty()) {
                    continue;
                }
                this.mScanResultCache.put(value.BSSID, value);
            }
        }
        this.evictOldScans();
        final ArrayMap arrayMap = new ArrayMap();
        for (final ScanResult scanResult : this.mScanResultCache.values()) {
            final String ssid2 = scanResult.SSID;
            if (ssid2 != null && ssid2.length() != 0) {
                if (scanResult.capabilities.contains("[IBSS]")) {
                    continue;
                }
                final String key = AccessPoint.getKey(this.mContext, scanResult);
                List<ScanResult> list2;
                if (arrayMap.containsKey((Object)key)) {
                    list2 = (List<ScanResult>)arrayMap.get((Object)key);
                }
                else {
                    list2 = new ArrayList<ScanResult>();
                    arrayMap.put((Object)key, (Object)list2);
                }
                list2.add(scanResult);
            }
        }
        return (ArrayMap<String, List<ScanResult>>)arrayMap;
    }
    
    private void updateWifiState(final int i) {
        if (isVerboseLoggingEnabled()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("updateWifiState: ");
            sb.append(i);
            Log.d("WifiTracker", sb.toString());
        }
        Label_0116: {
            if (i == 3) {
                synchronized (this.mLock) {
                    Label_0064: {
                        if (this.mScanner != null) {
                            this.mScanner.resume();
                            break Label_0064;
                        }
                        break Label_0064;
                    }
                    break Label_0116;
                }
            }
            this.clearAccessPointsAndConditionallyUpdate();
            this.mLastInfo = null;
            this.mLastNetworkInfo = null;
            synchronized (this.mLock) {
                if (this.mScanner != null) {
                    this.mScanner.pause();
                }
                // monitorexit(this.mLock)
                this.mStaleScanResults = true;
                this.mListener.onWifiStateChanged(i);
            }
        }
    }
    
    public void dump(final PrintWriter printWriter) {
        printWriter.println("  - wifi tracker ------");
        for (final AccessPoint obj : this.getAccessPoints()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("  ");
            sb.append(obj);
            printWriter.println(sb.toString());
        }
    }
    
    void forceUpdate() {
        this.mLastInfo = this.mWifiManager.getConnectionInfo();
        this.mLastNetworkInfo = this.mConnectivityManager.getNetworkInfo(this.mWifiManager.getCurrentNetwork());
        this.fetchScansAndConfigsAndUpdateAccessPoints();
    }
    
    public List<AccessPoint> getAccessPoints() {
        synchronized (this.mLock) {
            return new ArrayList<AccessPoint>(this.mInternalAccessPoints);
        }
    }
    
    public WifiManager getManager() {
        return this.mWifiManager;
    }
    
    public boolean isWifiEnabled() {
        final WifiManager mWifiManager = this.mWifiManager;
        return mWifiManager != null && mWifiManager.isWifiEnabled();
    }
    
    @Override
    public void onDestroy() {
        this.mWorkThread.quit();
    }
    
    @Override
    public void onStart() {
        this.forceUpdate();
        this.registerScoreCache();
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean mNetworkScoringUiEnabled = false;
        if (Settings$Global.getInt(contentResolver, "network_scoring_ui_enabled", 0) == 1) {
            mNetworkScoringUiEnabled = true;
        }
        this.mNetworkScoringUiEnabled = mNetworkScoringUiEnabled;
        this.mMaxSpeedLabelScoreCacheAge = Settings$Global.getLong(this.mContext.getContentResolver(), "speed_label_cache_eviction_age_millis", 1200000L);
        this.resumeScanning();
        if (!this.mRegistered) {
            this.mContext.registerReceiver(this.mReceiver, this.mFilter, (String)null, this.mWorkHandler);
            final WifiTrackerNetworkCallback mNetworkCallback = new WifiTrackerNetworkCallback();
            this.mNetworkCallback = mNetworkCallback;
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, (ConnectivityManager$NetworkCallback)mNetworkCallback, this.mWorkHandler);
            this.mRegistered = true;
        }
    }
    
    @Override
    public void onStop() {
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mConnectivityManager.unregisterNetworkCallback((ConnectivityManager$NetworkCallback)this.mNetworkCallback);
            this.mRegistered = false;
        }
        this.unregisterScoreCache();
        this.pauseScanning();
        this.mWorkHandler.removeCallbacksAndMessages((Object)null);
    }
    
    public void resumeScanning() {
        synchronized (this.mLock) {
            if (this.mScanner == null) {
                this.mScanner = new Scanner();
            }
            if (this.isWifiEnabled()) {
                this.mScanner.resume();
            }
        }
    }
    
    void setWorkThread(final HandlerThread mWorkThread) {
        this.mWorkThread = mWorkThread;
        this.mWorkHandler = new Handler(mWorkThread.getLooper());
        this.mScoreCache = new WifiNetworkScoreCache(this.mContext, (WifiNetworkScoreCache$CacheListener)new WifiNetworkScoreCache$CacheListener(this.mWorkHandler) {
            public void networkCacheUpdated(final List<ScoredNetwork> obj) {
                if (!WifiTracker.this.mRegistered) {
                    return;
                }
                if (Log.isLoggable("WifiTracker", 2)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Score cache was updated with networks: ");
                    sb.append(obj);
                    Log.v("WifiTracker", sb.toString());
                }
                WifiTracker.this.updateNetworkScores();
            }
        });
    }
    
    List<AccessPoint> updateOsuAccessPoints(final Map<OsuProvider, List<ScanResult>> map, final List<AccessPoint> list) {
        final ArrayList<AccessPoint> list2 = new ArrayList<AccessPoint>();
        final Set keySet = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders((Set)map.keySet()).keySet();
        for (final OsuProvider osuProvider : map.keySet()) {
            if (!keySet.contains(osuProvider)) {
                list2.add(this.getCachedOrCreateOsu(osuProvider, map.get(osuProvider), list));
            }
        }
        return list2;
    }
    
    List<AccessPoint> updatePasspointAccessPoints(final List<Pair<WifiConfiguration, Map<Integer, List<ScanResult>>>> list, final List<AccessPoint> list2) {
        final ArrayList<AccessPoint> list3 = new ArrayList<AccessPoint>();
        final ArraySet set = new ArraySet();
        for (final Pair<WifiConfiguration, Map<Integer, List<ScanResult>>> pair : list) {
            final WifiConfiguration wifiConfiguration = (WifiConfiguration)pair.first;
            if (((Set<String>)set).add(wifiConfiguration.FQDN)) {
                list3.add(this.getCachedOrCreatePasspoint(wifiConfiguration, ((Map)pair.second).get(0), ((Map)pair.second).get(1), list2));
            }
        }
        return list3;
    }
    
    class Scanner extends Handler
    {
        private int mRetry;
        
        Scanner() {
            this.mRetry = 0;
        }
        
        public void handleMessage(final Message message) {
            if (message.what != 0) {
                return;
            }
            if (WifiTracker.this.mWifiManager.startScan()) {
                this.mRetry = 0;
            }
            else if (++this.mRetry >= 3) {
                this.mRetry = 0;
                if (WifiTracker.this.mContext != null) {
                    Toast.makeText(WifiTracker.this.mContext, R$string.wifi_fail_to_scan, 1).show();
                }
                return;
            }
            this.sendEmptyMessageDelayed(0, 10000L);
        }
        
        boolean isScanning() {
            return this.hasMessages(0);
        }
        
        void pause() {
            if (isVerboseLoggingEnabled()) {
                Log.d("WifiTracker", "Scanner pause");
            }
            this.removeMessages(this.mRetry = 0);
        }
        
        void resume() {
            if (isVerboseLoggingEnabled()) {
                Log.d("WifiTracker", "Scanner resume");
            }
            if (!this.hasMessages(0)) {
                this.sendEmptyMessage(0);
            }
        }
    }
    
    public interface WifiListener
    {
        void onAccessPointsChanged();
        
        void onConnectedChanged();
        
        void onWifiStateChanged(final int p0);
    }
    
    class WifiListenerExecutor implements WifiListener
    {
        private final WifiListener mDelegatee;
        
        public WifiListenerExecutor(final WifiListener mDelegatee) {
            this.mDelegatee = mDelegatee;
        }
        
        private void runAndLog(final Runnable runnable, final String s) {
            ThreadUtils.postOnMainThread(new _$$Lambda$WifiTracker$WifiListenerExecutor$BMWc3s6WnR_Ijg_9a3gQADAjI3Y(this, s, runnable));
        }
        
        @Override
        public void onAccessPointsChanged() {
            final WifiListener mDelegatee = this.mDelegatee;
            Objects.requireNonNull(mDelegatee);
            this.runAndLog(new _$$Lambda$evcvquoPxZkPmBIit31UXvhXEJk(mDelegatee), "Invoking onAccessPointsChanged callback");
        }
        
        @Override
        public void onConnectedChanged() {
            final WifiListener mDelegatee = this.mDelegatee;
            Objects.requireNonNull(mDelegatee);
            this.runAndLog(new _$$Lambda$6PbPNXCvqbAnKbPWPJrs_dDWQEQ(mDelegatee), "Invoking onConnectedChanged callback");
        }
        
        @Override
        public void onWifiStateChanged(final int i) {
            this.runAndLog(new _$$Lambda$WifiTracker$WifiListenerExecutor$PZBvWEzpVHhaI95PbZNbzEgAH1I(this, i), String.format("Invoking onWifiStateChanged callback with state %d", i));
        }
    }
    
    private final class WifiTrackerNetworkCallback extends ConnectivityManager$NetworkCallback
    {
        public void onCapabilitiesChanged(final Network network, final NetworkCapabilities networkCapabilities) {
            if (network.equals((Object)WifiTracker.this.mWifiManager.getCurrentNetwork())) {
                WifiTracker.this.updateNetworkInfo(null);
            }
        }
    }
}
