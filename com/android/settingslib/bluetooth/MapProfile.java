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
import android.bluetooth.BluetoothMap;

public class MapProfile implements LocalBluetoothProfile
{
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothMap mService;
    
    static {
        final ParcelUuid map = BluetoothUuid.MAP;
        final ParcelUuid mns = BluetoothUuid.MNS;
        final ParcelUuid mas = BluetoothUuid.MAS;
    }
    
    MapProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager mProfileManager) {
        this.mDeviceManager = mDeviceManager;
        this.mProfileManager = mProfileManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new MapServiceListener(), 9);
    }
    
    @Override
    protected void finalize() {
        Log.d("MapProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(9, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("MapProfile", "Error cleaning up MAP proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothMap mService = this.mService;
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
        return 9;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothMap mService = this.mService;
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
        return "MAP";
    }
    
    private final class MapServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            MapProfile.this.mService = (BluetoothMap)bluetoothProfile;
            final List connectedDevices = MapProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = MapProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("MapProfile found new device: ");
                    sb.append(obj);
                    Log.w("MapProfile", sb.toString());
                    cachedBluetoothDevice = MapProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(MapProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            MapProfile.this.mProfileManager.callServiceConnectedListeners();
            MapProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            MapProfile.this.mProfileManager.callServiceDisconnectedListeners();
            MapProfile.this.mIsProfileReady = false;
        }
    }
}
