// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

public interface LocalBluetoothProfile
{
    int getConnectionStatus(final BluetoothDevice p0);
    
    int getDrawableResource(final BluetoothClass p0);
    
    int getProfileId();
    
    boolean setEnabled(final BluetoothDevice p0, final boolean p1);
}
