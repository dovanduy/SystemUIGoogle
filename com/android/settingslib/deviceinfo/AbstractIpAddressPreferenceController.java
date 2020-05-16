// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.deviceinfo;

public abstract class AbstractIpAddressPreferenceController extends AbstractConnectivityPreferenceController
{
    private static final String[] CONNECTIVITY_INTENTS;
    static final String KEY_IP_ADDRESS = "wifi_ip_address";
    
    static {
        CONNECTIVITY_INTENTS = new String[] { "android.net.conn.CONNECTIVITY_CHANGE", "android.net.wifi.LINK_CONFIGURATION_CHANGED", "android.net.wifi.STATE_CHANGE" };
    }
    
    @Override
    protected String[] getConnectivityIntents() {
        return AbstractIpAddressPreferenceController.CONNECTIVITY_INTENTS;
    }
}
