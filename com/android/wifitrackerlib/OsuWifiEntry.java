// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import java.util.Collection;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import androidx.core.util.Preconditions;
import java.util.ArrayList;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.ScanResult;
import java.util.List;
import android.content.Context;

class OsuWifiEntry extends WifiEntry
{
    private final Context mContext;
    private final List<ScanResult> mCurrentScanResults;
    private final String mKey;
    private int mLevel;
    private final Object mLock;
    private OsuProvider mOsuProvider;
    private String mOsuStatusString;
    
    OsuWifiEntry(final Context mContext, final Handler handler, final OsuProvider mOsuProvider, final WifiManager wifiManager, final boolean b) throws IllegalArgumentException {
        super(handler, wifiManager, b);
        this.mLock = new Object();
        this.mCurrentScanResults = new ArrayList<ScanResult>();
        this.mLevel = -1;
        Preconditions.checkNotNull(mOsuProvider, "Cannot construct with null osuProvider!");
        this.mContext = mContext;
        this.mOsuProvider = mOsuProvider;
        this.mKey = osuProviderToOsuWifiEntryKey(mOsuProvider);
    }
    
    static String osuProviderToOsuWifiEntryKey(final OsuProvider osuProvider) {
        Preconditions.checkNotNull(osuProvider, "Cannot create key with null OsuProvider!");
        final StringBuilder sb = new StringBuilder();
        sb.append("OsuWifiEntry:");
        sb.append(osuProvider.getFriendlyName());
        sb.append(",");
        sb.append(osuProvider.getServerUri().toString());
        return sb.toString();
    }
    
    @Override
    public boolean canSetAutoJoinEnabled() {
        return false;
    }
    
    @Override
    public boolean canSetMeteredChoice() {
        return false;
    }
    
    @Override
    protected boolean connectionInfoMatches(final WifiInfo wifiInfo, final NetworkInfo networkInfo) {
        return wifiInfo.isOsuAp() && TextUtils.equals((CharSequence)wifiInfo.getPasspointProviderFriendlyName(), (CharSequence)this.mOsuProvider.getFriendlyName());
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
        return 0;
    }
    
    OsuProvider getOsuProvider() {
        return this.mOsuProvider;
    }
    
    @Override
    String getScanResultDescription() {
        return "";
    }
    
    @Override
    public int getSecurity() {
        return 0;
    }
    
    @Override
    public String getSummary(final boolean b) {
        String s = this.mOsuStatusString;
        if (s == null) {
            s = this.mContext.getString(R$string.tap_to_sign_up);
        }
        return s;
    }
    
    @Override
    public String getTitle() {
        return this.mOsuProvider.getFriendlyName();
    }
    
    @Override
    public WifiConfiguration getWifiConfiguration() {
        return null;
    }
    
    @Override
    public boolean isAutoJoinEnabled() {
        return false;
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
        return false;
    }
    
    void updateScanResultInfo(List<ScanResult> o) throws IllegalArgumentException {
        Object o2 = o;
        if (o == null) {
            o2 = new ArrayList<ScanResult>();
        }
        o = this.mLock;
        synchronized (o) {
            this.mCurrentScanResults.clear();
            this.mCurrentScanResults.addAll((Collection<? extends ScanResult>)o2);
            // monitorexit(o)
            o = Utils.getBestScanResultByLevel((List<ScanResult>)o2);
            if (o == null) {
                this.mLevel = -1;
            }
            else {
                this.mLevel = super.mWifiManager.calculateSignalLevel(((ScanResult)o).level);
            }
            this.notifyOnUpdated();
        }
    }
}
