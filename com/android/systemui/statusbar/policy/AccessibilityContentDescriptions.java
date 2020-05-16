// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.R$string;

public class AccessibilityContentDescriptions
{
    static final int[] ETHERNET_CONNECTION_VALUES;
    static final int[] PHONE_SIGNAL_STRENGTH;
    static final int[] WIFI_CONNECTION_STRENGTH;
    static final int WIFI_NO_CONNECTION;
    
    static {
        PHONE_SIGNAL_STRENGTH = new int[] { R$string.accessibility_no_phone, R$string.accessibility_phone_one_bar, R$string.accessibility_phone_two_bars, R$string.accessibility_phone_three_bars, R$string.accessibility_phone_signal_full };
        WIFI_CONNECTION_STRENGTH = new int[] { R$string.accessibility_no_wifi, R$string.accessibility_wifi_one_bar, R$string.accessibility_wifi_two_bars, R$string.accessibility_wifi_three_bars, R$string.accessibility_wifi_signal_full };
        WIFI_NO_CONNECTION = R$string.accessibility_no_wifi;
        ETHERNET_CONNECTION_VALUES = new int[] { R$string.accessibility_ethernet_disconnected, R$string.accessibility_ethernet_connected };
    }
}
