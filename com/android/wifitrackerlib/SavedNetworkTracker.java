// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import java.util.function.Consumer;
import android.util.Log;
import java.util.Arrays;
import java.util.Collection;
import android.util.Pair;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.function.Function;
import androidx.core.util.Preconditions;
import android.os.Handler;
import java.util.Objects;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.ScanResult;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SavedNetworkTracker extends BaseWifiTracker
{
    private final SavedNetworkTrackerCallback mListener;
    private final Object mLock;
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache;
    private final List<WifiEntry> mSavedWifiEntries;
    private final Map<String, StandardWifiEntry> mStandardWifiEntryCache;
    private final List<WifiEntry> mSubscriptionWifiEntries;
    
    private void conditionallyUpdateScanResults(final boolean b) {
        if (super.mWifiManager.getWifiState() == 1) {
            this.updateStandardWifiEntryScans(Collections.emptyList());
            this.updatePasspointWifiEntryScans(Collections.emptyList());
            return;
        }
        long mMaxScanAgeMillis = super.mMaxScanAgeMillis;
        if (b) {
            super.mScanResultUpdater.update(super.mWifiManager.getScanResults());
        }
        else {
            mMaxScanAgeMillis += super.mScanIntervalMillis;
        }
        this.updateStandardWifiEntryScans(super.mScanResultUpdater.getScanResults(mMaxScanAgeMillis));
        this.updatePasspointWifiEntryScans(super.mScanResultUpdater.getScanResults(mMaxScanAgeMillis));
    }
    
    private void notifyOnSavedWifiEntriesChanged() {
        final SavedNetworkTrackerCallback mListener = this.mListener;
        if (mListener != null) {
            final Handler mMainHandler = super.mMainHandler;
            Objects.requireNonNull(mListener);
            mMainHandler.post((Runnable)new _$$Lambda$h1BuAemuREs5Akn8nay_wLEicng(mListener));
        }
    }
    
    private void notifyOnSubscriptionWifiEntriesChanged() {
        final SavedNetworkTrackerCallback mListener = this.mListener;
        if (mListener != null) {
            final Handler mMainHandler = super.mMainHandler;
            Objects.requireNonNull(mListener);
            mMainHandler.post((Runnable)new _$$Lambda$bWK0YQGHCIgfdyydB3bBJO0pQoM(mListener));
        }
    }
    
    private void updatePasspointWifiEntryConfigs(final List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        final Map<Object, Object> map = list.stream().collect(Collectors.toMap((Function<? super Object, ?>)_$$Lambda$SavedNetworkTracker$GiPU_7UrK85F3w9N7PMlA7M9niw.INSTANCE, (Function<? super Object, ?>)Function.identity()));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new _$$Lambda$SavedNetworkTracker$eyseTp76DXJATqUf1N_bJ_EBfOw(map));
        for (final String s : map.keySet()) {
            this.mPasspointWifiEntryCache.put(s, new PasspointWifiEntry(super.mContext, super.mMainHandler, map.get(s), super.mWifiManager, true));
        }
    }
    
    private void updatePasspointWifiEntryScans(final List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final TreeSet<String> set = new TreeSet<String>();
        for (final Pair pair : super.mWifiManager.getAllMatchingWifiConfigs((List)list)) {
            final WifiConfiguration wifiConfiguration = (WifiConfiguration)pair.first;
            final String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            set.add(uniqueIdToPasspointWifiEntryKey);
            if (!this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                continue;
            }
            this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey).updateScanResultInfo(wifiConfiguration, ((Map)pair.second).get(0), ((Map)pair.second).get(1));
        }
        for (final PasspointWifiEntry passpointWifiEntry : this.mPasspointWifiEntryCache.values()) {
            if (!set.contains(passpointWifiEntry.getKey())) {
                passpointWifiEntry.updateScanResultInfo(null, null, null);
            }
        }
    }
    
    private void updateSavedWifiEntries() {
        synchronized (this.mLock) {
            this.mSavedWifiEntries.clear();
            this.mSavedWifiEntries.addAll(this.mStandardWifiEntryCache.values());
            Collections.sort(this.mSavedWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Updated SavedWifiEntries: ");
                sb.append(Arrays.toString(this.mSavedWifiEntries.toArray()));
                Log.v("SavedNetworkTracker", sb.toString());
            }
            // monitorexit(this.mLock)
            this.notifyOnSavedWifiEntriesChanged();
        }
    }
    
    private void updateStandardWifiEntryConfigs(final List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        final Map<Object, Object> map = list.stream().collect(Collectors.toMap((Function<? super Object, ?>)_$$Lambda$eRhiL3TPu1j8op3nmit378jGeyk.INSTANCE, (Function<? super Object, ?>)Function.identity()));
        this.mStandardWifiEntryCache.entrySet().removeIf(new _$$Lambda$SavedNetworkTracker$Uwm7U8CRSvjmODKGQBR_weIFtK8(map));
        for (final String s : map.keySet()) {
            this.mStandardWifiEntryCache.put(s, new StandardWifiEntry(super.mContext, super.mMainHandler, s, map.get(s), super.mWifiManager, true));
        }
    }
    
    private void updateStandardWifiEntryScans(final List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        this.mStandardWifiEntryCache.entrySet().forEach(new _$$Lambda$SavedNetworkTracker$RngE0pMCth0_JbStnHNgSxFSuio(Utils.mapScanResultsToKey(list, false, null, super.mWifiManager.isWpa3SaeSupported(), super.mWifiManager.isWpa3SuiteBSupported(), super.mWifiManager.isEnhancedOpenSupported())));
    }
    
    private void updateSubscriptionWifiEntries() {
        synchronized (this.mLock) {
            this.mSubscriptionWifiEntries.clear();
            this.mSubscriptionWifiEntries.addAll(this.mPasspointWifiEntryCache.values());
            Collections.sort(this.mSubscriptionWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Updated SubscriptionWifiEntries: ");
                sb.append(Arrays.toString(this.mSubscriptionWifiEntries.toArray()));
                Log.v("SavedNetworkTracker", sb.toString());
            }
            // monitorexit(this.mLock)
            this.notifyOnSubscriptionWifiEntriesChanged();
        }
    }
    
    @Override
    protected void handleOnStart() {
        this.updateStandardWifiEntryConfigs(super.mWifiManager.getConfiguredNetworks());
        this.updatePasspointWifiEntryConfigs(super.mWifiManager.getPasspointConfigurations());
        this.conditionallyUpdateScanResults(true);
        this.updateSavedWifiEntries();
        this.updateSubscriptionWifiEntries();
    }
    
    public interface SavedNetworkTrackerCallback
    {
        void onSavedWifiEntriesChanged();
        
        void onSubscriptionWifiEntriesChanged();
    }
}
