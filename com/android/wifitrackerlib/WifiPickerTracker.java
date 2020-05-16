// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import android.net.LinkProperties;
import java.util.Arrays;
import android.util.Log;
import java.util.Collection;
import java.util.stream.Collector;
import android.util.Pair;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.function.Function;
import android.net.wifi.hotspot2.OsuProvider;
import androidx.core.util.Preconditions;
import java.util.Iterator;
import android.os.Handler;
import java.util.Objects;
import java.util.Set;
import android.text.TextUtils;
import android.net.wifi.ScanResult;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import java.util.List;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.hotspot2.PasspointConfiguration;
import java.util.Map;

public class WifiPickerTracker extends BaseWifiTracker
{
    private WifiEntry mConnectedWifiEntry;
    private final WifiPickerTrackerCallback mListener;
    private final Object mLock;
    private final Map<String, OsuWifiEntry> mOsuWifiEntryCache;
    private final Map<String, PasspointConfiguration> mPasspointConfigCache;
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache;
    private final Map<String, StandardWifiEntry> mStandardWifiEntryCache;
    private final Map<String, WifiConfiguration> mSuggestedConfigCache;
    private final Map<String, StandardWifiEntry> mSuggestedWifiEntryCache;
    private final Map<String, WifiConfiguration> mWifiConfigCache;
    private final List<WifiEntry> mWifiEntries;
    
    private void conditionallyCreateConnectedPasspointWifiEntry(final WifiInfo wifiInfo, final NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp()) {
            return;
        }
        this.mPasspointConfigCache.values().stream().filter(new _$$Lambda$WifiPickerTracker$IwY04Dtq_yvY6mAa_CnMkJoH9gQ(this, wifiInfo.getPasspointFqdn())).findAny().ifPresent(new _$$Lambda$WifiPickerTracker$5NTh96838xaFazdRGDdEVX7f_3I(this, wifiInfo, networkInfo));
    }
    
    private void conditionallyCreateConnectedStandardWifiEntry(final WifiInfo wifiInfo, final NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp()) {
            if (!wifiInfo.isOsuAp()) {
                this.mWifiConfigCache.values().stream().filter(new _$$Lambda$WifiPickerTracker$bOJNwNfjdG_rOa9pbi53SnnuXA4(this, wifiInfo.getNetworkId())).findAny().ifPresent(new _$$Lambda$WifiPickerTracker$1Bp3r5OS64i5Q67b16BM0WMxWJs(this, wifiInfo, networkInfo));
            }
        }
    }
    
    private void conditionallyCreateConnectedSuggestedWifiEntry(final WifiInfo wifiInfo, final NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp()) {
            if (!wifiInfo.isOsuAp()) {
                this.mSuggestedConfigCache.values().stream().filter(new _$$Lambda$WifiPickerTracker$moJVdCxjTQOs8gLSkmymRRZz0jA(this, wifiInfo)).findAny().ifPresent(new _$$Lambda$WifiPickerTracker$3LTCDnSLVZpPDTvPrvf7CZPS6QE(this, wifiInfo, networkInfo));
            }
        }
    }
    
    private void conditionallyUpdateScanResults(final boolean b) {
        if (super.mWifiManager.getWifiState() == 1) {
            this.updateStandardWifiEntryScans(Collections.emptyList());
            this.updateSuggestedWifiEntryScans(Collections.emptyList());
            this.updatePasspointWifiEntryScans(Collections.emptyList());
            this.updateOsuWifiEntryScans(Collections.emptyList());
            return;
        }
        long mMaxScanAgeMillis = super.mMaxScanAgeMillis;
        if (b) {
            super.mScanResultUpdater.update(super.mWifiManager.getScanResults());
        }
        else {
            mMaxScanAgeMillis += super.mScanIntervalMillis;
        }
        final List<ScanResult> scanResults = super.mScanResultUpdater.getScanResults(mMaxScanAgeMillis);
        this.updateStandardWifiEntryScans(scanResults);
        this.updateSuggestedWifiEntryScans(scanResults);
        this.updatePasspointWifiEntryScans(scanResults);
        this.updateOsuWifiEntryScans(scanResults);
    }
    
    private void notifyOnNumSavedNetworksChanged() {
        final WifiPickerTrackerCallback mListener = this.mListener;
        if (mListener != null) {
            final Handler mMainHandler = super.mMainHandler;
            Objects.requireNonNull(mListener);
            mMainHandler.post((Runnable)new _$$Lambda$fOs_tKYRPCSHYuhDjdNG__Oy_no(mListener));
        }
    }
    
    private void notifyOnNumSavedSubscriptionsChanged() {
        final WifiPickerTrackerCallback mListener = this.mListener;
        if (mListener != null) {
            final Handler mMainHandler = super.mMainHandler;
            Objects.requireNonNull(mListener);
            mMainHandler.post((Runnable)new _$$Lambda$pqRUdfn3mQVx4DYE6gYgaQJgj0E(mListener));
        }
    }
    
    private void notifyOnWifiEntriesChanged() {
        final WifiPickerTrackerCallback mListener = this.mListener;
        if (mListener != null) {
            final Handler mMainHandler = super.mMainHandler;
            Objects.requireNonNull(mListener);
            mMainHandler.post((Runnable)new _$$Lambda$5hHTXQ3X9FmFmB6qtFasRAuw8jY(mListener));
        }
    }
    
    private void updateConnectionInfo(final WifiInfo wifiInfo, final NetworkInfo networkInfo) {
        final Iterator<StandardWifiEntry> iterator = this.mStandardWifiEntryCache.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
        final Iterator<StandardWifiEntry> iterator2 = this.mSuggestedWifiEntryCache.values().iterator();
        while (iterator2.hasNext()) {
            iterator2.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
        final Iterator<PasspointWifiEntry> iterator3 = this.mPasspointWifiEntryCache.values().iterator();
        while (iterator3.hasNext()) {
            iterator3.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
        final Iterator<OsuWifiEntry> iterator4 = this.mOsuWifiEntryCache.values().iterator();
        while (iterator4.hasNext()) {
            iterator4.next().updateConnectionInfo(wifiInfo, networkInfo);
        }
    }
    
    private void updateOsuWifiEntryScans(final List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final Map matchingOsuProviders = super.mWifiManager.getMatchingOsuProviders((List)list);
        final Set keySet = super.mWifiManager.getMatchingPasspointConfigsForOsuProviders((Set)matchingOsuProviders.keySet()).keySet();
        for (final OsuWifiEntry osuWifiEntry : this.mOsuWifiEntryCache.values()) {
            osuWifiEntry.updateScanResultInfo(matchingOsuProviders.remove(osuWifiEntry.getOsuProvider()));
        }
        for (final OsuProvider osuProvider : matchingOsuProviders.keySet()) {
            final OsuWifiEntry osuWifiEntry2 = new OsuWifiEntry(super.mContext, super.mMainHandler, osuProvider, super.mWifiManager, false);
            osuWifiEntry2.updateScanResultInfo(matchingOsuProviders.get(osuProvider));
            this.mOsuWifiEntryCache.put(OsuWifiEntry.osuProviderToOsuWifiEntryKey(osuProvider), osuWifiEntry2);
        }
        this.mOsuWifiEntryCache.entrySet().removeIf(new _$$Lambda$WifiPickerTracker$zjeiWJkGp_0avCQ2VQY96pOVW0k(keySet));
    }
    
    private void updatePasspointWifiEntryConfigs(final List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mPasspointConfigCache.clear();
        this.mPasspointConfigCache.putAll(list.stream().collect(Collectors.toMap((Function<? super Object, ? extends String>)_$$Lambda$WifiPickerTracker$J_8ErDyQ_GXwIL_5p02mOb9PHRs.INSTANCE, (Function<? super Object, ? extends PasspointConfiguration>)Function.identity())));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new _$$Lambda$WifiPickerTracker$r64c2uwvAPtDG5sY3cZ0gHhikp0(this));
    }
    
    private void updatePasspointWifiEntryScans(final List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final TreeSet<String> set = new TreeSet<String>();
        for (final Pair pair : super.mWifiManager.getAllMatchingWifiConfigs((List)list)) {
            final WifiConfiguration wifiConfiguration = (WifiConfiguration)pair.first;
            final List<ScanResult> list2 = ((Map)pair.second).get(0);
            final List<ScanResult> list3 = ((Map)pair.second).get(1);
            final String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            set.add(uniqueIdToPasspointWifiEntryKey);
            if (!this.mPasspointConfigCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                continue;
            }
            if (!this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                this.mPasspointWifiEntryCache.put(uniqueIdToPasspointWifiEntryKey, new PasspointWifiEntry(super.mContext, super.mMainHandler, this.mPasspointConfigCache.get(uniqueIdToPasspointWifiEntryKey), super.mWifiManager, false));
            }
            this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey).updateScanResultInfo(wifiConfiguration, list2, list3);
        }
        this.mPasspointWifiEntryCache.entrySet().removeIf(new _$$Lambda$WifiPickerTracker$cBzaztuOljpiubhsS5OgXrliU0E(set));
    }
    
    private void updateStandardWifiEntryConfigs(final List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mWifiConfigCache.clear();
        this.mWifiConfigCache.putAll(list.stream().collect(Collectors.toMap((Function<? super Object, ? extends String>)_$$Lambda$eRhiL3TPu1j8op3nmit378jGeyk.INSTANCE, (Function<? super Object, ? extends WifiConfiguration>)Function.identity())));
        this.mStandardWifiEntryCache.entrySet().forEach(new _$$Lambda$WifiPickerTracker$qX0D_8Uc8Nr27s3neUWqdEyKbac(this));
    }
    
    private void updateStandardWifiEntryScans(final List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final Map<String, List<ScanResult>> mapScanResultsToKey = Utils.mapScanResultsToKey(list, true, this.mWifiConfigCache, super.mWifiManager.isWpa3SaeSupported(), super.mWifiManager.isWpa3SuiteBSupported(), super.mWifiManager.isEnhancedOpenSupported());
        this.mStandardWifiEntryCache.entrySet().removeIf(new _$$Lambda$WifiPickerTracker$B0nwXsR4XUOS0WnHBLJmABRjIFo(mapScanResultsToKey));
        for (final Map.Entry<String, List<ScanResult>> entry : mapScanResultsToKey.entrySet()) {
            final StandardWifiEntry standardWifiEntry = new StandardWifiEntry(super.mContext, super.mMainHandler, entry.getKey(), entry.getValue(), super.mWifiManager, false);
            standardWifiEntry.updateConfig(this.mWifiConfigCache.get(standardWifiEntry.getKey()));
            this.mStandardWifiEntryCache.put(standardWifiEntry.getKey(), standardWifiEntry);
        }
    }
    
    private void updateSuggestedWifiEntryConfigs(final List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mSuggestedConfigCache.clear();
        this.mSuggestedConfigCache.putAll(list.stream().collect(Collectors.toMap((Function<? super Object, ? extends String>)_$$Lambda$eRhiL3TPu1j8op3nmit378jGeyk.INSTANCE, (Function<? super Object, ? extends WifiConfiguration>)Function.identity())));
        this.mSuggestedWifiEntryCache.entrySet().removeIf(new _$$Lambda$WifiPickerTracker$1rUCuZkcaLae2xVYp3OydB_l_Kg(this));
    }
    
    private void updateSuggestedWifiEntryScans(final List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final Map<String, List<ScanResult>> mapScanResultsToKey = Utils.mapScanResultsToKey(list, true, this.mWifiConfigCache, super.mWifiManager.isWpa3SaeSupported(), super.mWifiManager.isWpa3SuiteBSupported(), super.mWifiManager.isEnhancedOpenSupported());
        final Map map = (Map)super.mWifiManager.getWifiConfigForMatchedNetworkSuggestionsSharedWithUser((List)list).stream().collect(Collectors.toMap((Function<? super Object, ?>)_$$Lambda$eRhiL3TPu1j8op3nmit378jGeyk.INSTANCE, (Function<? super Object, ?>)Function.identity()));
        final TreeSet<String> set = new TreeSet<String>();
        for (final String s : map.keySet()) {
            set.add(s);
            if (!this.mSuggestedWifiEntryCache.containsKey(s)) {
                this.mSuggestedWifiEntryCache.put(s, new StandardWifiEntry(super.mContext, super.mMainHandler, s, (WifiConfiguration)map.get(s), super.mWifiManager, false));
            }
            final StandardWifiEntry standardWifiEntry = this.mSuggestedWifiEntryCache.get(s);
            standardWifiEntry.setUserShareable(true);
            standardWifiEntry.updateScanResultInfo(mapScanResultsToKey.get(s));
        }
        this.mSuggestedWifiEntryCache.entrySet().removeIf(new _$$Lambda$WifiPickerTracker$Uuugp7BFIa_XEgGY_VHT5X4vsus(this, set));
    }
    
    private void updateWifiEntries() {
        synchronized (this.mLock) {
            final StandardWifiEntry mConnectedWifiEntry = this.mStandardWifiEntryCache.values().stream().filter((Predicate<? super StandardWifiEntry>)_$$Lambda$WifiPickerTracker$hvLce06jYq_zgj5QNbTVmIPFigo.INSTANCE).findAny().orElse(null);
            this.mConnectedWifiEntry = mConnectedWifiEntry;
            if (mConnectedWifiEntry == null) {
                this.mConnectedWifiEntry = this.mSuggestedWifiEntryCache.values().stream().filter((Predicate<? super StandardWifiEntry>)_$$Lambda$WifiPickerTracker$CZgqbuYbXTc6MQ_6NDRShQis8LA.INSTANCE).findAny().orElse(null);
            }
            if (this.mConnectedWifiEntry == null) {
                this.mConnectedWifiEntry = this.mPasspointWifiEntryCache.values().stream().filter((Predicate<? super PasspointWifiEntry>)_$$Lambda$WifiPickerTracker$WKgAoiYch4kxijsyncL19Qk1UE8.INSTANCE).findAny().orElse(null);
            }
            if (this.mConnectedWifiEntry == null) {
                this.mConnectedWifiEntry = this.mOsuWifiEntryCache.values().stream().filter((Predicate<? super OsuWifiEntry>)_$$Lambda$WifiPickerTracker$mzRxZpSNrk0X2p6ZMFGh4_dZXQc.INSTANCE).findAny().orElse(null);
            }
            this.mWifiEntries.clear();
            for (final String s : this.mStandardWifiEntryCache.keySet()) {
                if (this.mConnectedWifiEntry != null && TextUtils.equals((CharSequence)s, (CharSequence)this.mConnectedWifiEntry.getKey())) {
                    continue;
                }
                final StandardWifiEntry standardWifiEntry = this.mStandardWifiEntryCache.get(s);
                final StandardWifiEntry standardWifiEntry2 = this.mSuggestedWifiEntryCache.get(s);
                if (!standardWifiEntry.isSaved() && standardWifiEntry2 != null && standardWifiEntry2.isUserShareable()) {
                    if (standardWifiEntry2.getConnectedState() != 0) {
                        continue;
                    }
                    this.mWifiEntries.add(standardWifiEntry2);
                }
                else {
                    if (standardWifiEntry.getConnectedState() != 0) {
                        continue;
                    }
                    this.mWifiEntries.add(standardWifiEntry);
                }
            }
            this.mWifiEntries.addAll(this.mPasspointWifiEntryCache.values().stream().filter((Predicate<? super PasspointWifiEntry>)_$$Lambda$WifiPickerTracker$hqVV8tbFG8E0GArr8lV1BedQK9I.INSTANCE).collect((Collector<? super PasspointWifiEntry, ?, Collection<? extends WifiEntry>>)Collectors.toList()));
            this.mWifiEntries.addAll(this.mOsuWifiEntryCache.values().stream().filter((Predicate<? super OsuWifiEntry>)_$$Lambda$WifiPickerTracker$3daSMyov5B0XsLIA6buocEPJRtk.INSTANCE).collect((Collector<? super OsuWifiEntry, ?, Collection<? extends WifiEntry>>)Collectors.toList()));
            Collections.sort(this.mWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Connected WifiEntry: ");
                sb.append(this.mConnectedWifiEntry);
                Log.v("WifiPickerTracker", sb.toString());
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Updated WifiEntries: ");
                sb2.append(Arrays.toString(this.mWifiEntries.toArray()));
                Log.v("WifiPickerTracker", sb2.toString());
            }
            // monitorexit(this.mLock)
            this.notifyOnWifiEntriesChanged();
        }
    }
    
    protected void handleLinkPropertiesChanged(final LinkProperties linkProperties) {
        final WifiEntry mConnectedWifiEntry = this.mConnectedWifiEntry;
        if (mConnectedWifiEntry != null && mConnectedWifiEntry.getConnectedState() == 2) {
            this.mConnectedWifiEntry.updateLinkProperties(linkProperties);
        }
    }
    
    @Override
    protected void handleOnStart() {
        this.updateStandardWifiEntryConfigs(super.mWifiManager.getConfiguredNetworks());
        this.updateSuggestedWifiEntryConfigs((List<WifiConfiguration>)super.mWifiManager.getPrivilegedConfiguredNetworks().stream().filter((Predicate)_$$Lambda$WifiPickerTracker$_R4oh51Tff9Q6yrhfLZpyhJNRl8.INSTANCE).collect(Collectors.toList()));
        this.updatePasspointWifiEntryConfigs(super.mWifiManager.getPasspointConfigurations());
        super.mScanResultUpdater.update(super.mWifiManager.getScanResults());
        this.conditionallyUpdateScanResults(true);
        final WifiInfo connectionInfo = super.mWifiManager.getConnectionInfo();
        final NetworkInfo activeNetworkInfo = super.mConnectivityManager.getActiveNetworkInfo();
        this.updateConnectionInfo(connectionInfo, activeNetworkInfo);
        this.conditionallyCreateConnectedStandardWifiEntry(connectionInfo, activeNetworkInfo);
        this.conditionallyCreateConnectedSuggestedWifiEntry(connectionInfo, activeNetworkInfo);
        this.conditionallyCreateConnectedPasspointWifiEntry(connectionInfo, activeNetworkInfo);
        this.handleLinkPropertiesChanged(super.mConnectivityManager.getLinkProperties(super.mWifiManager.getCurrentNetwork()));
        this.notifyOnNumSavedNetworksChanged();
        this.notifyOnNumSavedSubscriptionsChanged();
        this.updateWifiEntries();
    }
    
    public interface WifiPickerTrackerCallback
    {
        void onNumSavedNetworksChanged();
        
        void onNumSavedSubscriptionsChanged();
        
        void onWifiEntriesChanged();
    }
}
