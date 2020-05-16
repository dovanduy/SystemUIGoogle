// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import java.util.List;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothProfile$ServiceListener;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.bluetooth.BluetoothHidHost;

public class HidProfile implements LocalBluetoothProfile
{
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private BluetoothHidHost mService;
    
    HidProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = mDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new HidHostServiceListener(), 4);
    }
    
    public static int getHidClassDrawable(final BluetoothClass bluetoothClass) {
        final int deviceClass = bluetoothClass.getDeviceClass();
        if (deviceClass != 1344) {
            if (deviceClass == 1408) {
                return 17302326;
            }
            if (deviceClass != 1472) {
                return 17302324;
            }
        }
        return 17302498;
    }
    
    @Override
    protected void finalize() {
        Log.d("HidProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(4, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("HidProfile", "Error cleaning up HID proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothHidHost mService = this.mService;
        if (mService == null) {
            return 0;
        }
        return mService.getConnectionState(bluetoothDevice);
    }
    
    @Override
    public int getDrawableResource(final BluetoothClass bluetoothClass) {
        if (bluetoothClass == null) {
            return 17302498;
        }
        return getHidClassDrawable(bluetoothClass);
    }
    
    @Override
    public int getProfileId() {
        return 4;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothHidHost mService = this.mService;
        final boolean b2 = false;
        if (mService == null) {
            return false;
        }
        boolean b3;
        if (b) {
            b3 = b2;
            if (mService.getConnectionPolicy(bluetoothDevice) < 100) {
                b3 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
            }
        }
        else {
            b3 = mService.setConnectionPolicy(bluetoothDevice, 0);
        }
        return b3;
    }
    
    @Override
    public String toString() {
        return "HID";
    }
    
    private final class HidHostServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            HidProfile.this.mService = (BluetoothHidHost)bluetoothProfile;
            final List connectedDevices = HidProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = HidProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("HidProfile found new device: ");
                    sb.append(obj);
                    Log.w("HidProfile", sb.toString());
                    cachedBluetoothDevice = HidProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(HidProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            HidProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            HidProfile.this.mIsProfileReady = false;
        }
    }
}
