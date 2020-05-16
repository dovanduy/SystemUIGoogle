// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.deviceinfo;

public abstract class AbstractBluetoothAddressPreferenceController extends AbstractConnectivityPreferenceController
{
    private static final String[] CONNECTIVITY_INTENTS;
    static final String KEY_BT_ADDRESS = "bt_address";
    
    static {
        CONNECTIVITY_INTENTS = new String[] { "android.bluetooth.adapter.action.STATE_CHANGED" };
    }
    
    @Override
    protected String[] getConnectivityIntents() {
        return AbstractBluetoothAddressPreferenceController.CONNECTIVITY_INTENTS;
    }
}
