// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import java.util.List;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import android.bluetooth.BluetoothProfile$ServiceListener;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.ParcelUuid;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.BluetoothSap;

final class SapProfile implements LocalBluetoothProfile
{
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothSap mService;
    
    static {
        final ParcelUuid sap = BluetoothUuid.SAP;
    }
    
    SapProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager mProfileManager) {
        this.mDeviceManager = mDeviceManager;
        this.mProfileManager = mProfileManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new SapServiceListener(), 10);
    }
    
    @Override
    protected void finalize() {
        Log.d("SapProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(10, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("SapProfile", "Error cleaning up SAP proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothSap mService = this.mService;
        if (mService == null) {
            return 0;
        }
        return mService.getConnectionState(bluetoothDevice);
    }
    
    @Override
    public int getDrawableResource(final BluetoothClass bluetoothClass) {
        return 17302783;
    }
    
    @Override
    public int getProfileId() {
        return 10;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothSap mService = this.mService;
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
        return "SAP";
    }
    
    private final class SapServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            SapProfile.this.mService = (BluetoothSap)bluetoothProfile;
            final List connectedDevices = SapProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = SapProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("SapProfile found new device: ");
                    sb.append(obj);
                    Log.w("SapProfile", sb.toString());
                    cachedBluetoothDevice = SapProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(SapProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            SapProfile.this.mProfileManager.callServiceConnectedListeners();
            SapProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            SapProfile.this.mProfileManager.callServiceDisconnectedListeners();
            SapProfile.this.mIsProfileReady = false;
        }
    }
}
