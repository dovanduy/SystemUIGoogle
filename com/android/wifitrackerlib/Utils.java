// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import android.content.pm.ApplicationInfo;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.HashMap;
import java.util.Map;
import android.text.TextUtils;
import java.util.StringJoiner;
import java.util.ArrayList;
import java.util.BitSet;
import android.net.NetworkInfo$DetailedState;
import android.net.NetworkInfo;
import android.net.NetworkCapabilities;
import java.util.Collection;
import java.util.Collections;
import java.util.function.ToIntFunction;
import java.util.Comparator;
import android.net.wifi.ScanResult;
import java.util.List;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.os.RemoteException;
import android.app.AppGlobals;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.UserHandle;
import android.content.Context;

class Utils
{
    static CharSequence getAppLabel(final Context context, final String s) {
        try {
            return context.getPackageManager().getApplicationInfoAsUser(s, 0, UserHandle.getUserId(-2)).loadLabel(context.getPackageManager());
        }
        catch (PackageManager$NameNotFoundException ex) {
            return "";
        }
    }
    
    static CharSequence getAppLabelForSavedNetwork(final Context context, WifiEntry wifiEntry) {
        final WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        if (context == null || wifiEntry == null) {
            return "";
        }
        if (wifiConfiguration == null) {
            return "";
        }
        final PackageManager packageManager = context.getPackageManager();
        final String nameForUid = packageManager.getNameForUid(1000);
        final int userId = UserHandle.getUserId(wifiConfiguration.creatorUid);
        wifiEntry = null;
        final String creatorName = wifiConfiguration.creatorName;
        while (true) {
            if (creatorName != null && creatorName.equals(nameForUid)) {
                wifiEntry = (WifiEntry)context.getApplicationInfo();
                break Label_0092;
            }
            try {
                wifiEntry = (WifiEntry)AppGlobals.getPackageManager().getApplicationInfo(wifiConfiguration.creatorName, 0, userId);
                if (wifiEntry != null && !((ApplicationInfo)wifiEntry).packageName.equals(context.getString(R$string.settings_package)) && !((ApplicationInfo)wifiEntry).packageName.equals(context.getString(R$string.certinstaller_package))) {
                    return ((ApplicationInfo)wifiEntry).loadLabel(packageManager);
                }
                return "";
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
    
    static String getAutoConnectDescription(final Context context, final WifiEntry wifiEntry) {
        String string;
        final String s = string = "";
        if (context != null) {
            string = s;
            if (wifiEntry != null) {
                if (!wifiEntry.canSetAutoJoinEnabled()) {
                    string = s;
                }
                else if (wifiEntry.isAutoJoinEnabled()) {
                    string = s;
                }
                else {
                    string = context.getString(R$string.auto_connect_disable);
                }
            }
        }
        return string;
    }
    
    static ScanResult getBestScanResultByLevel(final List<ScanResult> coll) {
        if (coll.isEmpty()) {
            return null;
        }
        return Collections.max((Collection<? extends ScanResult>)coll, Comparator.comparingInt((ToIntFunction<? super ScanResult>)_$$Lambda$Utils$wGn2sVTZ5l1wFLkqd7rxKtPh0RU.INSTANCE));
    }
    
    static String getCurrentNetworkCapabilitiesInformation(final Context context, final NetworkCapabilities networkCapabilities) {
        if (context != null) {
            if (networkCapabilities != null) {
                if (networkCapabilities.hasCapability(17)) {
                    return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", "android"));
                }
                if (networkCapabilities.hasCapability(24)) {
                    return context.getString(R$string.wifi_limited_connection);
                }
                if (!networkCapabilities.hasCapability(16)) {
                    if (networkCapabilities.isPrivateDnsBroken()) {
                        return context.getString(R$string.private_dns_broken);
                    }
                    return context.getString(R$string.wifi_connected_no_internet);
                }
            }
        }
        return "";
    }
    
    static String getDisconnectedStateDescription(final Context context, final WifiEntry wifiEntry) {
        if (context != null) {
            if (wifiEntry != null) {
                final WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
                if (wifiConfiguration == null) {
                    return null;
                }
                if (wifiConfiguration.hasNoInternetAccess()) {
                    int n;
                    if (wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() == 2) {
                        n = R$string.wifi_no_internet_no_reconnect;
                    }
                    else {
                        n = R$string.wifi_no_internet;
                    }
                    return context.getString(n);
                }
                if (wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
                    final int networkSelectionDisableReason = wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionDisableReason();
                    if (networkSelectionDisableReason == 1) {
                        return context.getString(R$string.wifi_disabled_generic);
                    }
                    if (networkSelectionDisableReason == 2) {
                        return context.getString(R$string.wifi_disabled_password_failure);
                    }
                    if (networkSelectionDisableReason == 3) {
                        return context.getString(R$string.wifi_disabled_network_failure);
                    }
                    if (networkSelectionDisableReason == 8) {
                        return context.getString(R$string.wifi_check_password_try_again);
                    }
                }
                else if (wifiEntry.getLevel() != -1) {
                    if (wifiConfiguration.getRecentFailureReason() == 17) {
                        return context.getString(R$string.wifi_ap_unable_to_handle_new_sta);
                    }
                }
            }
        }
        return "";
    }
    
    static String getMeteredDescription(final Context context, final WifiEntry wifiEntry) {
        String string;
        final String s = string = "";
        if (context != null) {
            if (wifiEntry == null) {
                string = s;
            }
            else {
                if (!wifiEntry.canSetMeteredChoice() && wifiEntry.getMeteredChoice() != 1) {
                    return "";
                }
                if (wifiEntry.getMeteredChoice() == 1) {
                    return context.getString(R$string.wifi_metered_label);
                }
                if (wifiEntry.getMeteredChoice() == 2) {
                    return context.getString(R$string.wifi_unmetered_label);
                }
                string = s;
                if (wifiEntry.isMetered()) {
                    string = context.getString(R$string.wifi_metered_label);
                }
            }
        }
        return string;
    }
    
    static String getNetworkDetailedState(final Context context, final NetworkInfo networkInfo) {
        String s2;
        final String s = s2 = "";
        if (context != null) {
            if (networkInfo == null) {
                s2 = s;
            }
            else {
                final NetworkInfo$DetailedState detailedState = networkInfo.getDetailedState();
                if (detailedState == null) {
                    return "";
                }
                final String[] stringArray = context.getResources().getStringArray(R$array.wifi_status);
                final int ordinal = detailedState.ordinal();
                if (ordinal >= stringArray.length) {
                    s2 = s;
                }
                else {
                    s2 = stringArray[ordinal];
                }
            }
        }
        return s2;
    }
    
    static int getSecurityTypeFromWifiConfiguration(final WifiConfiguration wifiConfiguration) {
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
        if (wifiConfiguration.wepKeys[0] == null) {
            n = 0;
        }
        return n;
    }
    
    static List<Integer> getSecurityTypesFromScanResult(final ScanResult scanResult) {
        final ArrayList<Integer> list = new ArrayList<Integer>();
        final String capabilities = scanResult.capabilities;
        final Integer value = 0;
        if (capabilities == null) {
            list.add(value);
        }
        else if (capabilities.contains("PSK") && scanResult.capabilities.contains("SAE")) {
            list.add(2);
            list.add(5);
        }
        else if (scanResult.capabilities.contains("OWE_TRANSITION")) {
            list.add(value);
            list.add(4);
        }
        else if (scanResult.capabilities.contains("OWE")) {
            list.add(4);
        }
        else if (scanResult.capabilities.contains("WEP")) {
            list.add(1);
        }
        else if (scanResult.capabilities.contains("SAE")) {
            list.add(5);
        }
        else if (scanResult.capabilities.contains("PSK")) {
            list.add(2);
        }
        else if (scanResult.capabilities.contains("EAP_SUITE_B_192")) {
            list.add(6);
        }
        else if (scanResult.capabilities.contains("EAP")) {
            list.add(3);
        }
        else {
            list.add(value);
        }
        return list;
    }
    
    static String getSpeedDescription(final Context context, final WifiEntry wifiEntry) {
        if (context == null || wifiEntry == null) {}
        return "";
    }
    
    static String getVerboseLoggingDescription(final WifiEntry wifiEntry) {
        if (BaseWifiTracker.isVerboseLoggingEnabled() && wifiEntry != null) {
            final StringJoiner stringJoiner = new StringJoiner(" ");
            final String wifiInfoDescription = wifiEntry.getWifiInfoDescription();
            if (!TextUtils.isEmpty((CharSequence)wifiInfoDescription)) {
                stringJoiner.add(wifiInfoDescription);
            }
            final String scanResultDescription = wifiEntry.getScanResultDescription();
            if (!TextUtils.isEmpty((CharSequence)scanResultDescription)) {
                stringJoiner.add(scanResultDescription);
            }
            return stringJoiner.toString();
        }
        return "";
    }
    
    static Map<String, List<ScanResult>> mapScanResultsToKey(final List<ScanResult> list, final boolean b, Map<String, WifiConfiguration> hashMap, final boolean b2, final boolean b3, final boolean b4) {
        if (hashMap == null) {
            hashMap = new HashMap<String, WifiConfiguration>();
        }
        Object o = list.stream().filter((Predicate<? super Object>)_$$Lambda$Utils$YXgA7eQ3EufOS8jlgf9HRQs4bfM.INSTANCE).collect((Collector<? super Object, ?, Map<Object, List<? super Object>>>)Collectors.groupingBy((Function<? super Object, ?>)_$$Lambda$Utils$_MVjtMEczmHvXav1qgSkgxMj5iE.INSTANCE));
        Map<String, List<ScanResult>> map = new HashMap<String, List<ScanResult>>();
        Iterator<String> iterator = ((Map<String, V>)o).keySet().iterator();
        Map<String, WifiConfiguration> map2 = hashMap;
        while (iterator.hasNext()) {
            final String s = iterator.next();
            final boolean containsKey = map2.containsKey(StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(s, 2));
            final int n = 5;
            final boolean containsKey2 = map2.containsKey(StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(s, 5));
            final boolean containsKey3 = map2.containsKey(StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(s, 0));
            final boolean containsKey4 = map2.containsKey(StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(s, 4));
            final Iterator iterator2 = ((List)((Map)o).get(s)).iterator();
            final int n3;
            int n2 = n3 = 0;
            int n5;
            int n4 = n5 = n3;
            int n6 = n3;
            while (iterator2.hasNext()) {
                final List<Integer> securityTypesFromScanResult = getSecurityTypesFromScanResult(iterator2.next());
                if (securityTypesFromScanResult.contains(2)) {
                    n2 = 1;
                }
                if (securityTypesFromScanResult.contains(5)) {
                    n6 = 1;
                }
                if (securityTypesFromScanResult.contains(4)) {
                    n4 = 1;
                }
                if (securityTypesFromScanResult.contains(0)) {
                    n5 = 1;
                }
            }
            final Iterator iterator3 = ((List)((Map)o).get(s)).iterator();
            int n7 = n;
            final Iterator<String> iterator4 = iterator;
            Object o2 = map;
            final Map<String, V> map3 = (Map<String, V>)o;
            final Map<String, WifiConfiguration> map4 = map2;
            while (true) {
                map2 = map4;
                o = map3;
                map = (Map<String, List<ScanResult>>)o2;
                iterator = iterator4;
                if (!iterator3.hasNext()) {
                    break;
                }
                final ScanResult scanResult = iterator3.next();
                final List<Integer> securityTypesFromScanResult2 = getSecurityTypesFromScanResult(scanResult);
                final ArrayList<Object> list2 = new ArrayList<Object>();
                if (!b2) {
                    securityTypesFromScanResult2.remove((Object)n7);
                }
                if (!b3) {
                    securityTypesFromScanResult2.remove((Object)6);
                }
                if (!b4) {
                    securityTypesFromScanResult2.remove((Object)4);
                    n7 = 5;
                }
                final boolean b5 = securityTypesFromScanResult2.contains(n7) && !securityTypesFromScanResult2.contains(2);
                final boolean b6 = securityTypesFromScanResult2.contains(2) && !securityTypesFromScanResult2.contains(5);
                final boolean b7 = securityTypesFromScanResult2.contains(2) && securityTypesFromScanResult2.contains(5);
                final boolean b8 = securityTypesFromScanResult2.contains(4) && !securityTypesFromScanResult2.contains(0);
                final boolean b9 = securityTypesFromScanResult2.contains(0) && securityTypesFromScanResult2.contains(4);
                final boolean b10 = securityTypesFromScanResult2.contains(0) && !securityTypesFromScanResult2.contains(4);
                Object o3 = null;
                Label_1060: {
                    if (b) {
                        if (b6) {
                            if (!containsKey && containsKey2 && n6 != 0) {
                                n7 = 5;
                                continue;
                            }
                            list2.add(2);
                        }
                        else if (b7) {
                            if (!containsKey && containsKey2) {
                                list2.add(5);
                            }
                            else {
                                list2.add(2);
                            }
                        }
                        else if (b5) {
                            if (!containsKey && (containsKey2 || n2 == 0)) {
                                list2.add(5);
                            }
                            else {
                                list2.add(2);
                            }
                        }
                        else if (b8) {
                            if (n5 != 0 && containsKey3 && !containsKey4) {
                                o3 = o2;
                                break Label_1060;
                            }
                            list2.add(4);
                        }
                        else if (b9) {
                            if (!containsKey4 && containsKey3) {
                                list2.add(0);
                            }
                            else {
                                list2.add(4);
                            }
                        }
                        else if (b10) {
                            if (n4 != 0) {
                                o3 = o2;
                                if (containsKey4) {
                                    break Label_1060;
                                }
                                if (!containsKey3) {
                                    o3 = o2;
                                    break Label_1060;
                                }
                            }
                            list2.add(0);
                        }
                        else {
                            list2.addAll(securityTypesFromScanResult2);
                        }
                    }
                    else {
                        list2.addAll(securityTypesFromScanResult2);
                        if (b5) {
                            list2.add(2);
                        }
                    }
                    final Iterator<Integer> iterator5 = list2.iterator();
                    while (true) {
                        o3 = o2;
                        if (!iterator5.hasNext()) {
                            break;
                        }
                        final String ssidAndSecurityToStandardWifiEntryKey = StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(s, iterator5.next());
                        if (!((Map)o2).containsKey(ssidAndSecurityToStandardWifiEntryKey)) {
                            ((Map<String, ArrayList<ScanResult>>)o2).put(ssidAndSecurityToStandardWifiEntryKey, new ArrayList<ScanResult>());
                        }
                        ((Map<String, ArrayList<ScanResult>>)o2).get(ssidAndSecurityToStandardWifiEntryKey).add(scanResult);
                    }
                }
                n7 = 5;
                o2 = o3;
            }
        }
        return map;
    }
}
