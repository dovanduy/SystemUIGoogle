// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.HashMap;
import com.android.systemui.R$string;
import com.android.systemui.R$drawable;
import java.util.Map;

class TelephonyIcons
{
    static final MobileSignalController.MobileIconGroup CARRIER_NETWORK_CHANGE;
    static final MobileSignalController.MobileIconGroup DATA_DISABLED;
    static final MobileSignalController.MobileIconGroup E;
    static final int FLIGHT_MODE_ICON;
    static final MobileSignalController.MobileIconGroup FOUR_G;
    static final MobileSignalController.MobileIconGroup FOUR_G_PLUS;
    static final MobileSignalController.MobileIconGroup G;
    static final MobileSignalController.MobileIconGroup H;
    static final MobileSignalController.MobileIconGroup H_PLUS;
    static final int ICON_1X;
    static final int ICON_3G;
    static final int ICON_4G;
    static final int ICON_4G_PLUS;
    static final int ICON_5G;
    static final int ICON_5G_E;
    static final int ICON_5G_PLUS;
    static final int ICON_E;
    static final int ICON_G;
    static final int ICON_H;
    static final int ICON_H_PLUS;
    static final int ICON_LTE;
    static final int ICON_LTE_PLUS;
    static final Map<String, MobileSignalController.MobileIconGroup> ICON_NAME_TO_ICON;
    static final MobileSignalController.MobileIconGroup LTE;
    static final MobileSignalController.MobileIconGroup LTE_CA_5G_E;
    static final MobileSignalController.MobileIconGroup LTE_PLUS;
    static final MobileSignalController.MobileIconGroup NOT_DEFAULT_DATA;
    static final MobileSignalController.MobileIconGroup NR_5G;
    static final MobileSignalController.MobileIconGroup NR_5G_PLUS;
    static final MobileSignalController.MobileIconGroup ONE_X;
    static final MobileSignalController.MobileIconGroup THREE_G;
    static final MobileSignalController.MobileIconGroup UNKNOWN;
    static final MobileSignalController.MobileIconGroup WFC;
    
    static {
        FLIGHT_MODE_ICON = R$drawable.stat_sys_airplane_mode;
        ICON_LTE = R$drawable.ic_lte_mobiledata;
        ICON_LTE_PLUS = R$drawable.ic_lte_plus_mobiledata;
        ICON_G = R$drawable.ic_g_mobiledata;
        ICON_E = R$drawable.ic_e_mobiledata;
        ICON_H = R$drawable.ic_h_mobiledata;
        ICON_H_PLUS = R$drawable.ic_h_plus_mobiledata;
        ICON_3G = R$drawable.ic_3g_mobiledata;
        ICON_4G = R$drawable.ic_4g_mobiledata;
        ICON_4G_PLUS = R$drawable.ic_4g_plus_mobiledata;
        ICON_5G_E = R$drawable.ic_5g_e_mobiledata;
        ICON_1X = R$drawable.ic_1x_mobiledata;
        ICON_5G = R$drawable.ic_5g_mobiledata;
        ICON_5G_PLUS = R$drawable.ic_5g_plus_mobiledata;
        final int[] phone_SIGNAL_STRENGTH = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        CARRIER_NETWORK_CHANGE = new MobileSignalController.MobileIconGroup("CARRIER_NETWORK_CHANGE", null, null, phone_SIGNAL_STRENGTH, 0, 0, 0, 0, phone_SIGNAL_STRENGTH[0], R$string.carrier_network_change_mode, 0, false);
        final int[] phone_SIGNAL_STRENGTH2 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        THREE_G = new MobileSignalController.MobileIconGroup("3G", null, null, phone_SIGNAL_STRENGTH2, 0, 0, 0, 0, phone_SIGNAL_STRENGTH2[0], R$string.data_connection_3g, TelephonyIcons.ICON_3G, true);
        final int[] phone_SIGNAL_STRENGTH3 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        WFC = new MobileSignalController.MobileIconGroup("WFC", null, null, phone_SIGNAL_STRENGTH3, 0, 0, 0, 0, phone_SIGNAL_STRENGTH3[0], 0, 0, false);
        final int[] phone_SIGNAL_STRENGTH4 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        UNKNOWN = new MobileSignalController.MobileIconGroup("Unknown", null, null, phone_SIGNAL_STRENGTH4, 0, 0, 0, 0, phone_SIGNAL_STRENGTH4[0], 0, 0, false);
        final int[] phone_SIGNAL_STRENGTH5 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        E = new MobileSignalController.MobileIconGroup("E", null, null, phone_SIGNAL_STRENGTH5, 0, 0, 0, 0, phone_SIGNAL_STRENGTH5[0], R$string.data_connection_edge, TelephonyIcons.ICON_E, false);
        final int[] phone_SIGNAL_STRENGTH6 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        ONE_X = new MobileSignalController.MobileIconGroup("1X", null, null, phone_SIGNAL_STRENGTH6, 0, 0, 0, 0, phone_SIGNAL_STRENGTH6[0], R$string.data_connection_cdma, TelephonyIcons.ICON_1X, true);
        final int[] phone_SIGNAL_STRENGTH7 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        G = new MobileSignalController.MobileIconGroup("G", null, null, phone_SIGNAL_STRENGTH7, 0, 0, 0, 0, phone_SIGNAL_STRENGTH7[0], R$string.data_connection_gprs, TelephonyIcons.ICON_G, false);
        final int[] phone_SIGNAL_STRENGTH8 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        H = new MobileSignalController.MobileIconGroup("H", null, null, phone_SIGNAL_STRENGTH8, 0, 0, 0, 0, phone_SIGNAL_STRENGTH8[0], R$string.data_connection_3_5g, TelephonyIcons.ICON_H, false);
        final int[] phone_SIGNAL_STRENGTH9 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        H_PLUS = new MobileSignalController.MobileIconGroup("H+", null, null, phone_SIGNAL_STRENGTH9, 0, 0, 0, 0, phone_SIGNAL_STRENGTH9[0], R$string.data_connection_3_5g_plus, TelephonyIcons.ICON_H_PLUS, false);
        final int[] phone_SIGNAL_STRENGTH10 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        FOUR_G = new MobileSignalController.MobileIconGroup("4G", null, null, phone_SIGNAL_STRENGTH10, 0, 0, 0, 0, phone_SIGNAL_STRENGTH10[0], R$string.data_connection_4g, TelephonyIcons.ICON_4G, true);
        final int[] phone_SIGNAL_STRENGTH11 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        FOUR_G_PLUS = new MobileSignalController.MobileIconGroup("4G+", null, null, phone_SIGNAL_STRENGTH11, 0, 0, 0, 0, phone_SIGNAL_STRENGTH11[0], R$string.data_connection_4g_plus, TelephonyIcons.ICON_4G_PLUS, true);
        final int[] phone_SIGNAL_STRENGTH12 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        LTE = new MobileSignalController.MobileIconGroup("LTE", null, null, phone_SIGNAL_STRENGTH12, 0, 0, 0, 0, phone_SIGNAL_STRENGTH12[0], R$string.data_connection_lte, TelephonyIcons.ICON_LTE, true);
        final int[] phone_SIGNAL_STRENGTH13 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        LTE_PLUS = new MobileSignalController.MobileIconGroup("LTE+", null, null, phone_SIGNAL_STRENGTH13, 0, 0, 0, 0, phone_SIGNAL_STRENGTH13[0], R$string.data_connection_lte_plus, TelephonyIcons.ICON_LTE_PLUS, true);
        final int[] phone_SIGNAL_STRENGTH14 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        LTE_CA_5G_E = new MobileSignalController.MobileIconGroup("5Ge", null, null, phone_SIGNAL_STRENGTH14, 0, 0, 0, 0, phone_SIGNAL_STRENGTH14[0], R$string.data_connection_5ge_html, TelephonyIcons.ICON_5G_E, true);
        final int[] phone_SIGNAL_STRENGTH15 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        NR_5G = new MobileSignalController.MobileIconGroup("5G", null, null, phone_SIGNAL_STRENGTH15, 0, 0, 0, 0, phone_SIGNAL_STRENGTH15[0], R$string.data_connection_5g, TelephonyIcons.ICON_5G, true);
        final int[] phone_SIGNAL_STRENGTH16 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        NR_5G_PLUS = new MobileSignalController.MobileIconGroup("5G_PLUS", null, null, phone_SIGNAL_STRENGTH16, 0, 0, 0, 0, phone_SIGNAL_STRENGTH16[0], R$string.data_connection_5g_plus, TelephonyIcons.ICON_5G_PLUS, true);
        final int[] phone_SIGNAL_STRENGTH17 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        DATA_DISABLED = new MobileSignalController.MobileIconGroup("DataDisabled", null, null, phone_SIGNAL_STRENGTH17, 0, 0, 0, 0, phone_SIGNAL_STRENGTH17[0], R$string.cell_data_off_content_description, 0, false);
        final int[] phone_SIGNAL_STRENGTH18 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        NOT_DEFAULT_DATA = new MobileSignalController.MobileIconGroup("NotDefaultData", null, null, phone_SIGNAL_STRENGTH18, 0, 0, 0, 0, phone_SIGNAL_STRENGTH18[0], R$string.not_default_data_content_description, 0, false);
        (ICON_NAME_TO_ICON = new HashMap<String, MobileSignalController.MobileIconGroup>()).put("carrier_network_change", TelephonyIcons.CARRIER_NETWORK_CHANGE);
        TelephonyIcons.ICON_NAME_TO_ICON.put("3g", TelephonyIcons.THREE_G);
        TelephonyIcons.ICON_NAME_TO_ICON.put("wfc", TelephonyIcons.WFC);
        TelephonyIcons.ICON_NAME_TO_ICON.put("unknown", TelephonyIcons.UNKNOWN);
        TelephonyIcons.ICON_NAME_TO_ICON.put("e", TelephonyIcons.E);
        TelephonyIcons.ICON_NAME_TO_ICON.put("1x", TelephonyIcons.ONE_X);
        TelephonyIcons.ICON_NAME_TO_ICON.put("g", TelephonyIcons.G);
        TelephonyIcons.ICON_NAME_TO_ICON.put("h", TelephonyIcons.H);
        TelephonyIcons.ICON_NAME_TO_ICON.put("h+", TelephonyIcons.H_PLUS);
        TelephonyIcons.ICON_NAME_TO_ICON.put("4g", TelephonyIcons.FOUR_G);
        TelephonyIcons.ICON_NAME_TO_ICON.put("4g+", TelephonyIcons.FOUR_G_PLUS);
        TelephonyIcons.ICON_NAME_TO_ICON.put("5ge", TelephonyIcons.LTE_CA_5G_E);
        TelephonyIcons.ICON_NAME_TO_ICON.put("lte", TelephonyIcons.LTE);
        TelephonyIcons.ICON_NAME_TO_ICON.put("lte+", TelephonyIcons.LTE_PLUS);
        TelephonyIcons.ICON_NAME_TO_ICON.put("5g", TelephonyIcons.NR_5G);
        TelephonyIcons.ICON_NAME_TO_ICON.put("5g_plus", TelephonyIcons.NR_5G_PLUS);
        TelephonyIcons.ICON_NAME_TO_ICON.put("datadisable", TelephonyIcons.DATA_DISABLED);
        TelephonyIcons.ICON_NAME_TO_ICON.put("notdefaultdata", TelephonyIcons.NOT_DEFAULT_DATA);
    }
}
