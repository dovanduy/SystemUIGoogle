// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import java.util.Iterator;
import java.net.UnknownHostException;
import android.net.RouteInfo;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.List;
import java.net.Inet6Address;
import android.net.NetworkUtils;
import java.net.InetAddress;
import java.net.Inet4Address;
import android.net.LinkAddress;
import java.util.ArrayList;
import android.net.LinkProperties;
import java.util.StringJoiner;
import android.net.wifi.WifiConfiguration;
import androidx.core.util.Preconditions;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.NetworkInfo;
import android.net.NetworkCapabilities;
import android.os.Handler;

public abstract class WifiEntry implements Comparable<WifiEntry>
{
    protected Handler mCallbackHandler;
    protected boolean mCalledConnect;
    protected boolean mCalledDisconnect;
    protected ConnectCallback mConnectCallback;
    protected ConnectedInfo mConnectedInfo;
    protected DisconnectCallback mDisconnectCallback;
    final boolean mForSavedNetworksPage;
    protected int mLevel;
    private WifiEntryCallback mListener;
    protected NetworkCapabilities mNetworkCapabilities;
    protected NetworkInfo mNetworkInfo;
    protected WifiInfo mWifiInfo;
    protected final WifiManager mWifiManager;
    
    WifiEntry(final Handler mCallbackHandler, final WifiManager mWifiManager, final boolean mForSavedNetworksPage) throws IllegalArgumentException {
        this.mLevel = -1;
        this.mCalledConnect = false;
        this.mCalledDisconnect = false;
        Preconditions.checkNotNull(mCallbackHandler, "Cannot construct with null handler!");
        Preconditions.checkNotNull(mWifiManager, "Cannot construct with null WifiManager!");
        this.mCallbackHandler = mCallbackHandler;
        this.mForSavedNetworksPage = mForSavedNetworksPage;
        this.mWifiManager = mWifiManager;
    }
    
    public abstract boolean canSetAutoJoinEnabled();
    
    public abstract boolean canSetMeteredChoice();
    
    @Override
    public int compareTo(final WifiEntry wifiEntry) {
        if (this.getLevel() != -1 && wifiEntry.getLevel() == -1) {
            return -1;
        }
        if (this.getLevel() == -1 && wifiEntry.getLevel() != -1) {
            return 1;
        }
        if (this.isSubscription() && !wifiEntry.isSubscription()) {
            return -1;
        }
        if (!this.isSubscription() && wifiEntry.isSubscription()) {
            return 1;
        }
        if (this.isSaved() && !wifiEntry.isSaved()) {
            return -1;
        }
        if (!this.isSaved() && wifiEntry.isSaved()) {
            return 1;
        }
        if (this.getLevel() > wifiEntry.getLevel()) {
            return -1;
        }
        if (this.getLevel() < wifiEntry.getLevel()) {
            return 1;
        }
        return this.getTitle().compareTo(wifiEntry.getTitle());
    }
    
    protected abstract boolean connectionInfoMatches(final WifiInfo p0, final NetworkInfo p1);
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof WifiEntry && this.getKey().equals(((WifiEntry)o).getKey());
    }
    
    public ConnectedInfo getConnectedInfo() {
        if (this.getConnectedState() != 2) {
            return null;
        }
        return this.mConnectedInfo;
    }
    
    public int getConnectedState() {
        final NetworkInfo mNetworkInfo = this.mNetworkInfo;
        if (mNetworkInfo == null) {
            return 0;
        }
        switch (WifiEntry$1.$SwitchMap$android$net$NetworkInfo$DetailedState[mNetworkInfo.getDetailedState().ordinal()]) {
            default: {
                return 0;
            }
            case 7: {
                return 2;
            }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: {
                return 1;
            }
        }
    }
    
    public String getHelpUriString() {
        return null;
    }
    
    public abstract String getKey();
    
    public abstract int getLevel();
    
    public abstract int getMeteredChoice();
    
    abstract String getScanResultDescription();
    
    public abstract int getSecurity();
    
    public String getSummary() {
        return this.getSummary(true);
    }
    
    public abstract String getSummary(final boolean p0);
    
    public abstract String getTitle();
    
    public abstract WifiConfiguration getWifiConfiguration();
    
    String getWifiInfoDescription() {
        final StringJoiner stringJoiner = new StringJoiner(" ");
        if (this.getConnectedState() == 2 && this.mWifiInfo != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("f = ");
            sb.append(this.mWifiInfo.getFrequency());
            stringJoiner.add(sb.toString());
            final String bssid = this.mWifiInfo.getBSSID();
            if (bssid != null) {
                stringJoiner.add(bssid);
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("standard = ");
            sb2.append(this.mWifiInfo.getWifiStandard());
            stringJoiner.add(sb2.toString());
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("rssi = ");
            sb3.append(this.mWifiInfo.getRssi());
            stringJoiner.add(sb3.toString());
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("score = ");
            sb4.append(this.mWifiInfo.getScore());
            stringJoiner.add(sb4.toString());
            stringJoiner.add(String.format(" tx=%.1f,", this.mWifiInfo.getSuccessfulTxPacketsPerSecond()));
            stringJoiner.add(String.format("%.1f,", this.mWifiInfo.getRetriedTxPacketsPerSecond()));
            stringJoiner.add(String.format("%.1f ", this.mWifiInfo.getLostTxPacketsPerSecond()));
            stringJoiner.add(String.format("rx=%.1f", this.mWifiInfo.getSuccessfulRxPacketsPerSecond()));
        }
        return stringJoiner.toString();
    }
    
    public abstract boolean isAutoJoinEnabled();
    
    public abstract boolean isMetered();
    
    public abstract boolean isSaved();
    
    public abstract boolean isSubscription();
    
    protected void notifyOnUpdated() {
        if (this.mListener != null) {
            this.mCallbackHandler.post((Runnable)new _$$Lambda$WifiEntry$Z7qIuj7K1pqbGQNunqibzqO18s0(this));
        }
    }
    
    public void setListener(final WifiEntryCallback mListener) {
        this.mListener = mListener;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getKey());
        sb.append(",title:");
        sb.append(this.getTitle());
        sb.append(",summary:");
        sb.append(this.getSummary());
        sb.append(",level:");
        sb.append(this.getLevel());
        sb.append(",security:");
        sb.append(this.getSecurity());
        sb.append(",connected:");
        String str;
        if (this.getConnectedState() == 2) {
            str = "true";
        }
        else {
            str = "false";
        }
        sb.append(str);
        sb.append(",connectedInfo:");
        sb.append(this.getConnectedInfo());
        return sb.toString();
    }
    
    void updateConnectionInfo(final WifiInfo mWifiInfo, final NetworkInfo mNetworkInfo) {
        if (mWifiInfo != null && mNetworkInfo != null && this.connectionInfoMatches(mWifiInfo, mNetworkInfo)) {
            this.mWifiInfo = mWifiInfo;
            this.mNetworkInfo = mNetworkInfo;
            final int rssi = mWifiInfo.getRssi();
            if (rssi != -127) {
                this.mLevel = this.mWifiManager.calculateSignalLevel(rssi);
            }
            if (this.getConnectedState() == 2) {
                if (this.mCalledConnect) {
                    this.mCalledConnect = false;
                    this.mCallbackHandler.post((Runnable)new _$$Lambda$WifiEntry$mJZ4Rvce3_rMD9mRMpz_vWu5ItY(this));
                }
                if (this.mConnectedInfo == null) {
                    this.mConnectedInfo = new ConnectedInfo();
                }
                this.mConnectedInfo.frequencyMhz = mWifiInfo.getFrequency();
                this.mConnectedInfo.linkSpeedMbps = mWifiInfo.getLinkSpeed();
            }
        }
        else {
            this.mNetworkInfo = null;
            this.mNetworkCapabilities = null;
            this.mConnectedInfo = null;
            if (this.mCalledDisconnect) {
                this.mCalledDisconnect = false;
                this.mCallbackHandler.post((Runnable)new _$$Lambda$WifiEntry$Wbjrmqbh_0TpH4DVTSRzGNL2Aks(this));
            }
        }
        this.notifyOnUpdated();
    }
    
    void updateLinkProperties(final LinkProperties linkProperties) {
        Label_0278: {
            if (linkProperties == null || this.getConnectedState() != 2) {
                break Label_0278;
            }
            if (this.mConnectedInfo == null) {
                this.mConnectedInfo = new ConnectedInfo();
            }
            Object iterator = new ArrayList<String>();
            Object iterator2 = linkProperties.getLinkAddresses().iterator();
            LinkAddress linkAddress;
            Block_8_Outer:Label_0191_Outer:Block_11_Outer:
            while (true) {
                Label_0173: {
                    if (!((Iterator)iterator2).hasNext()) {
                        break Label_0173;
                    }
                    linkAddress = ((Iterator<LinkAddress>)iterator2).next();
                    Label_0144: {
                        if (!(linkAddress.getAddress() instanceof Inet4Address)) {
                            break Label_0144;
                        }
                        this.mConnectedInfo.ipAddress = linkAddress.getAddress().getHostAddress();
                        try {
                            this.mConnectedInfo.subnetMask = NetworkUtils.getNetworkPart(InetAddress.getByAddress(new byte[] { -1, -1, -1, -1 }), linkAddress.getPrefixLength()).getHostAddress();
                            continue Block_8_Outer;
                            // iftrue(Label_0238:, !iterator.hasNext())
                            // iftrue(Label_0051:, !linkAddress.getAddress() instanceof Inet6Address)
                            // iftrue(Label_0191:, !iterator2.isIPv4Default() || !iterator2.hasGateway())
                            while (true) {
                            Block_9:
                                while (true) {
                                    while (true) {
                                        ((List<String>)iterator).add(linkAddress.getAddress().getHostAddress());
                                        continue Block_8_Outer;
                                        break Block_9;
                                        continue Label_0191_Outer;
                                    }
                                    this.mConnectedInfo.gateway = ((RouteInfo)iterator2).getGateway().getHostAddress();
                                    Label_0238: {
                                        this.mConnectedInfo.dnsServers = (List<String>)linkProperties.getDnsServers().stream().map((Function)_$$Lambda$XZAGhHrbkIDyusER4MAM6luKcT0.INSTANCE).collect(Collectors.toList());
                                    }
                                    this.notifyOnUpdated();
                                    return;
                                    this.mConnectedInfo.ipv6Addresses = (List<String>)iterator;
                                    iterator = linkProperties.getRoutes().iterator();
                                    continue Block_11_Outer;
                                }
                                iterator2 = ((Iterator<RouteInfo>)iterator).next();
                                continue;
                            }
                            this.mConnectedInfo = null;
                            this.notifyOnUpdated();
                        }
                        catch (UnknownHostException ex) {
                            continue;
                        }
                    }
                }
                break;
            }
        }
    }
    
    public interface ConnectCallback
    {
        void onConnectResult(final int p0);
    }
    
    public static class ConnectedInfo
    {
        public List<String> dnsServers;
        public int frequencyMhz;
        public String gateway;
        public String ipAddress;
        public List<String> ipv6Addresses;
        public int linkSpeedMbps;
        public String subnetMask;
        
        public ConnectedInfo() {
            new ArrayList();
            new ArrayList();
        }
    }
    
    public interface DisconnectCallback
    {
        void onDisconnectResult(final int p0);
    }
    
    public interface WifiEntryCallback
    {
        void onUpdated();
    }
}
