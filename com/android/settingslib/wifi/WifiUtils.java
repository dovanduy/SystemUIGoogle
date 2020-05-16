// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import java.util.Iterator;
import android.os.SystemClock;
import java.util.Map;
import android.net.wifi.ScanResult;
import com.android.settingslib.R$string;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiConfiguration$NetworkSelectionStatus;
import android.net.wifi.WifiConfiguration;

public class WifiUtils
{
    public static String buildLoggingSummary(final AccessPoint accessPoint, final WifiConfiguration wifiConfiguration) {
        final StringBuilder sb = new StringBuilder();
        final WifiInfo info = accessPoint.getInfo();
        if (accessPoint.isActive() && info != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(" f=");
            sb2.append(Integer.toString(info.getFrequency()));
            sb.append(sb2.toString());
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(" ");
        sb3.append(getVisibilityStatus(accessPoint));
        sb.append(sb3.toString());
        if (wifiConfiguration != null && wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append(" (");
            sb4.append(wifiConfiguration.getNetworkSelectionStatus().getNetworkStatusString());
            sb.append(sb4.toString());
            if (wifiConfiguration.getNetworkSelectionStatus().getDisableTime() > 0L) {
                final long n = (System.currentTimeMillis() - wifiConfiguration.getNetworkSelectionStatus().getDisableTime()) / 1000L;
                final long i = n / 60L % 60L;
                final long j = i / 60L % 60L;
                sb.append(", ");
                if (j > 0L) {
                    final StringBuilder sb5 = new StringBuilder();
                    sb5.append(Long.toString(j));
                    sb5.append("h ");
                    sb.append(sb5.toString());
                }
                final StringBuilder sb6 = new StringBuilder();
                sb6.append(Long.toString(i));
                sb6.append("m ");
                sb.append(sb6.toString());
                final StringBuilder sb7 = new StringBuilder();
                sb7.append(Long.toString(n % 60L));
                sb7.append("s ");
                sb.append(sb7.toString());
            }
            sb.append(")");
        }
        if (wifiConfiguration != null) {
            final WifiConfiguration$NetworkSelectionStatus networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus();
            for (int k = 0; k <= WifiConfiguration$NetworkSelectionStatus.getMaxNetworkSelectionDisableReason(); ++k) {
                if (networkSelectionStatus.getDisableReasonCounter(k) != 0) {
                    sb.append(" ");
                    sb.append(WifiConfiguration$NetworkSelectionStatus.getNetworkSelectionDisableReasonString(k));
                    sb.append("=");
                    sb.append(networkSelectionStatus.getDisableReasonCounter(k));
                }
            }
        }
        return sb.toString();
    }
    
    public static String getMeteredLabel(final Context context, final WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.meteredOverride != 1 && (!wifiConfiguration.meteredHint || isMeteredOverridden(wifiConfiguration))) {
            return context.getString(R$string.wifi_unmetered_label);
        }
        return context.getString(R$string.wifi_metered_label);
    }
    
    private static int getSpecificApSpeed(final ScanResult scanResult, final Map<String, TimestampedScoredNetwork> map) {
        final TimestampedScoredNetwork timestampedScoredNetwork = map.get(scanResult.BSSID);
        if (timestampedScoredNetwork == null) {
            return 0;
        }
        return timestampedScoredNetwork.getScore().calculateBadge(scanResult.level);
    }
    
    static String getVisibilityStatus(final AccessPoint accessPoint) {
        final WifiInfo info = accessPoint.getInfo();
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb2 = new StringBuilder();
        final StringBuilder sb3 = new StringBuilder();
        final boolean active = accessPoint.isActive();
        int i = 0;
        String bssid;
        if (active && info != null) {
            bssid = info.getBSSID();
            if (bssid != null) {
                sb.append(" ");
                sb.append(bssid);
            }
            sb.append(" standard = ");
            sb.append(info.getWifiStandard());
            sb.append(" rssi=");
            sb.append(info.getRssi());
            sb.append(" ");
            sb.append(" score=");
            sb.append(info.getScore());
            if (accessPoint.getSpeed() != 0) {
                sb.append(" speed=");
                sb.append(accessPoint.getSpeedLabel());
            }
            sb.append(String.format(" tx=%.1f,", info.getSuccessfulTxPacketsPerSecond()));
            sb.append(String.format("%.1f,", info.getRetriedTxPacketsPerSecond()));
            sb.append(String.format("%.1f ", info.getLostTxPacketsPerSecond()));
            sb.append(String.format("rx=%.1f", info.getSuccessfulRxPacketsPerSecond()));
        }
        else {
            bssid = null;
        }
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        final Iterator<ScanResult> iterator = accessPoint.getScanResults().iterator();
        int k;
        int j = k = -127;
        int l = 0;
        while (iterator.hasNext()) {
            final ScanResult scanResult = iterator.next();
            if (scanResult == null) {
                continue;
            }
            final int frequency = scanResult.frequency;
            if (frequency >= 4900 && frequency <= 5900) {
                final int n = l + 1;
                final int level = scanResult.level;
                int n2;
                if (level > (n2 = k)) {
                    n2 = level;
                }
                l = n;
                k = n2;
                if (n > 4) {
                    continue;
                }
                sb3.append(verboseScanResultSummary(accessPoint, scanResult, bssid, elapsedRealtime));
                l = n;
                k = n2;
            }
            else {
                final int frequency2 = scanResult.frequency;
                if (frequency2 < 2400 || frequency2 > 2500) {
                    continue;
                }
                final int n3 = i + 1;
                final int level2 = scanResult.level;
                int n4;
                if (level2 > (n4 = j)) {
                    n4 = level2;
                }
                i = n3;
                j = n4;
                if (n3 > 4) {
                    continue;
                }
                sb2.append(verboseScanResultSummary(accessPoint, scanResult, bssid, elapsedRealtime));
                i = n3;
                j = n4;
            }
        }
        sb.append(" [");
        if (i > 0) {
            sb.append("(");
            sb.append(i);
            sb.append(")");
            if (i > 4) {
                sb.append("max=");
                sb.append(j);
                sb.append(",");
            }
            sb.append(sb2.toString());
        }
        sb.append(";");
        if (l > 0) {
            sb.append("(");
            sb.append(l);
            sb.append(")");
            if (l > 4) {
                sb.append("max=");
                sb.append(k);
                sb.append(",");
            }
            sb.append(sb3.toString());
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static boolean isMeteredOverridden(final WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.meteredOverride != 0;
    }
    
    static String verboseScanResultSummary(final AccessPoint accessPoint, final ScanResult scanResult, final String anObject, final long n) {
        final StringBuilder sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        if (scanResult.BSSID.equals(anObject)) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        sb.append(",");
        sb.append(scanResult.level);
        final int specificApSpeed = getSpecificApSpeed(scanResult, accessPoint.getScoredNetworkCache());
        if (specificApSpeed != 0) {
            sb.append(",");
            sb.append(accessPoint.getSpeedLabel(specificApSpeed));
        }
        final int i = (int)(n - scanResult.timestamp / 1000L) / 1000;
        sb.append(",");
        sb.append(i);
        sb.append("s");
        sb.append("}");
        return sb.toString();
    }
}
