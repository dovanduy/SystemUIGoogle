// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

final class OppProfile implements LocalBluetoothProfile
{
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        return 0;
    }
    
    @Override
    public int getDrawableResource(final BluetoothClass bluetoothClass) {
        return 0;
    }
    
    @Override
    public int getProfileId() {
        return 20;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        return false;
    }
    
    @Override
    public String toString() {
        return "OPP";
    }
}
