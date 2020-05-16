// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import java.util.Iterator;
import java.util.List;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import android.bluetooth.BluetoothProfile$ServiceListener;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothDevice;
import java.util.HashMap;

public class PanProfile implements LocalBluetoothProfile
{
    private final HashMap<BluetoothDevice, Integer> mDeviceRoleMap;
    private boolean mIsProfileReady;
    private BluetoothPan mService;
    
    PanProfile(final Context context) {
        this.mDeviceRoleMap = new HashMap<BluetoothDevice, Integer>();
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new PanServiceListener(), 5);
    }
    
    @Override
    protected void finalize() {
        Log.d("PanProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(5, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("PanProfile", "Error cleaning up PAN proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothPan mService = this.mService;
        if (mService == null) {
            return 0;
        }
        return mService.getConnectionState(bluetoothDevice);
    }
    
    @Override
    public int getDrawableResource(final BluetoothClass bluetoothClass) {
        return 17302325;
    }
    
    @Override
    public int getProfileId() {
        return 5;
    }
    
    boolean isLocalRoleNap(final BluetoothDevice bluetoothDevice) {
        final boolean containsKey = this.mDeviceRoleMap.containsKey(bluetoothDevice);
        boolean b = false;
        if (containsKey) {
            b = b;
            if (this.mDeviceRoleMap.get(bluetoothDevice) == 1) {
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothPan mService = this.mService;
        if (mService == null) {
            return false;
        }
        boolean b2;
        if (b) {
            final List connectedDevices = mService.getConnectedDevices();
            if (connectedDevices != null) {
                final Iterator<BluetoothDevice> iterator = connectedDevices.iterator();
                while (iterator.hasNext()) {
                    this.mService.setConnectionPolicy((BluetoothDevice)iterator.next(), 0);
                }
            }
            b2 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        else {
            b2 = mService.setConnectionPolicy(bluetoothDevice, 0);
        }
        return b2;
    }
    
    void setLocalRole(final BluetoothDevice key, final int i) {
        this.mDeviceRoleMap.put(key, i);
    }
    
    @Override
    public String toString() {
        return "PAN";
    }
    
    private final class PanServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            PanProfile.this.mService = (BluetoothPan)bluetoothProfile;
            PanProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            PanProfile.this.mIsProfileReady = false;
        }
    }
}
