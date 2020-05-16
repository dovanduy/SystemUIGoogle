// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.deviceinfo;

public abstract class AbstractWifiMacAddressPreferenceController extends AbstractConnectivityPreferenceController
{
    private static final String[] CONNECTIVITY_INTENTS;
    static final String KEY_WIFI_MAC_ADDRESS = "wifi_mac_address";
    static final int OFF = 0;
    static final int ON = 1;
    
    static {
        CONNECTIVITY_INTENTS = new String[] { "android.net.conn.CONNECTIVITY_CHANGE", "android.net.wifi.LINK_CONFIGURATION_CHANGED", "android.net.wifi.STATE_CHANGE" };
    }
    
    @Override
    protected String[] getConnectivityIntents() {
        return AbstractWifiMacAddressPreferenceController.CONNECTIVITY_INTENTS;
    }
}
