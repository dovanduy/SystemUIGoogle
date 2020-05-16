// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.deviceinfo;

public abstract class AbstractImsStatusPreferenceController extends AbstractConnectivityPreferenceController
{
    private static final String[] CONNECTIVITY_INTENTS;
    static final String KEY_IMS_REGISTRATION_STATE = "ims_reg_state";
    
    static {
        CONNECTIVITY_INTENTS = new String[] { "android.bluetooth.adapter.action.STATE_CHANGED", "android.net.conn.CONNECTIVITY_CHANGE", "android.net.wifi.LINK_CONFIGURATION_CHANGED", "android.net.wifi.STATE_CHANGE" };
    }
    
    @Override
    protected String[] getConnectivityIntents() {
        return AbstractImsStatusPreferenceController.CONNECTIVITY_INTENTS;
    }
}
