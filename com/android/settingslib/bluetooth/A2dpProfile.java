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
import android.bluetooth.BluetoothUuid;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.os.ParcelUuid;

public class A2dpProfile implements LocalBluetoothProfile
{
    static final ParcelUuid[] SINK_UUIDS;
    private final BluetoothAdapter mBluetoothAdapter;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothA2dp mService;
    
    static {
        SINK_UUIDS = new ParcelUuid[] { BluetoothUuid.A2DP_SINK, BluetoothUuid.ADV_AUDIO_DIST };
    }
    
    A2dpProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager mProfileManager) {
        this.mDeviceManager = mDeviceManager;
        this.mProfileManager = mProfileManager;
        (this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()).getProfileProxy(context, (BluetoothProfile$ServiceListener)new A2dpServiceListener(), 2);
    }
    
    @Override
    protected void finalize() {
        Log.d("A2dpProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(2, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("A2dpProfile", "Error cleaning up A2DP proxy", t);
            }
        }
    }
    
    public BluetoothDevice getActiveDevice() {
        final BluetoothA2dp mService = this.mService;
        if (mService == null) {
            return null;
        }
        return mService.getActiveDevice();
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothA2dp mService = this.mService;
        if (mService == null) {
            return 0;
        }
        return mService.getConnectionState(bluetoothDevice);
    }
    
    @Override
    public int getDrawableResource(final BluetoothClass bluetoothClass) {
        return 17302320;
    }
    
    @Override
    public int getProfileId() {
        return 2;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothA2dp mService = this.mService;
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
        return "A2DP";
    }
    
    private final class A2dpServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            A2dpProfile.this.mService = (BluetoothA2dp)bluetoothProfile;
            final List connectedDevices = A2dpProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = A2dpProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("A2dpProfile found new device: ");
                    sb.append(obj);
                    Log.w("A2dpProfile", sb.toString());
                    cachedBluetoothDevice = A2dpProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(A2dpProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            A2dpProfile.this.mIsProfileReady = true;
            A2dpProfile.this.mProfileManager.callServiceConnectedListeners();
        }
        
        public void onServiceDisconnected(final int n) {
            A2dpProfile.this.mIsProfileReady = false;
        }
    }
}
