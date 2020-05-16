// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.R$drawable;

public class WifiIcons
{
    public static final int[][] QS_WIFI_SIGNAL_STRENGTH;
    static final int[] WIFI_FULL_ICONS;
    static final int WIFI_LEVEL_COUNT;
    private static final int[] WIFI_NO_INTERNET_ICONS;
    static final int[][] WIFI_SIGNAL_STRENGTH;
    
    static {
        WIFI_FULL_ICONS = new int[] { 17302863, 17302864, 17302865, 17302866, 17302867 };
        WIFI_LEVEL_COUNT = (WIFI_SIGNAL_STRENGTH = (QS_WIFI_SIGNAL_STRENGTH = new int[][] { WIFI_NO_INTERNET_ICONS = new int[] { R$drawable.ic_qs_wifi_0, R$drawable.ic_qs_wifi_1, R$drawable.ic_qs_wifi_2, R$drawable.ic_qs_wifi_3, R$drawable.ic_qs_wifi_4 }, WifiIcons.WIFI_FULL_ICONS }))[0].length;
    }
}
