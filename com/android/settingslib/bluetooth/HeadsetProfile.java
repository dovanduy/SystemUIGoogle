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
import android.content.Context;
import android.os.ParcelUuid;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothAdapter;

public class HeadsetProfile implements LocalBluetoothProfile
{
    private final BluetoothAdapter mBluetoothAdapter;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothHeadset mService;
    
    static {
        final ParcelUuid hsp = BluetoothUuid.HSP;
        final ParcelUuid hfp = BluetoothUuid.HFP;
    }
    
    HeadsetProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager mProfileManager) {
        this.mDeviceManager = mDeviceManager;
        this.mProfileManager = mProfileManager;
        (this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()).getProfileProxy(context, (BluetoothProfile$ServiceListener)new HeadsetServiceListener(), 1);
    }
    
    @Override
    protected void finalize() {
        Log.d("HeadsetProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(1, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("HeadsetProfile", "Error cleaning up HID proxy", t);
            }
        }
    }
    
    public BluetoothDevice getActiveDevice() {
        final BluetoothHeadset mService = this.mService;
        if (mService == null) {
            return null;
        }
        return mService.getActiveDevice();
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothHeadset mService = this.mService;
        if (mService == null) {
            return 0;
        }
        return mService.getConnectionState(bluetoothDevice);
    }
    
    @Override
    public int getDrawableResource(final BluetoothClass bluetoothClass) {
        return 17302321;
    }
    
    @Override
    public int getProfileId() {
        return 1;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothHeadset mService = this.mService;
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
        return "HEADSET";
    }
    
    private final class HeadsetServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            HeadsetProfile.this.mService = (BluetoothHeadset)bluetoothProfile;
            final List connectedDevices = HeadsetProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = HeadsetProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("HeadsetProfile found new device: ");
                    sb.append(obj);
                    Log.w("HeadsetProfile", sb.toString());
                    cachedBluetoothDevice = HeadsetProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(HeadsetProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            HeadsetProfile.this.mIsProfileReady = true;
            HeadsetProfile.this.mProfileManager.callServiceConnectedListeners();
        }
        
        public void onServiceDisconnected(final int n) {
            HeadsetProfile.this.mProfileManager.callServiceDisconnectedListeners();
            HeadsetProfile.this.mIsProfileReady = false;
        }
    }
}
