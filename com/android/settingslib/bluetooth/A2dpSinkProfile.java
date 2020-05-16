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
import android.bluetooth.BluetoothUuid;
import android.bluetooth.BluetoothA2dpSink;
import android.os.ParcelUuid;

final class A2dpSinkProfile implements LocalBluetoothProfile
{
    static final ParcelUuid[] SRC_UUIDS;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private BluetoothA2dpSink mService;
    
    static {
        SRC_UUIDS = new ParcelUuid[] { BluetoothUuid.A2DP_SOURCE, BluetoothUuid.ADV_AUDIO_DIST };
    }
    
    A2dpSinkProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = mDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new A2dpSinkServiceListener(), 11);
    }
    
    @Override
    protected void finalize() {
        Log.d("A2dpSinkProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(11, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("A2dpSinkProfile", "Error cleaning up A2DP proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothA2dpSink mService = this.mService;
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
        return 11;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothA2dpSink mService = this.mService;
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
        return "A2DPSink";
    }
    
    private final class A2dpSinkServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            A2dpSinkProfile.this.mService = (BluetoothA2dpSink)bluetoothProfile;
            final List connectedDevices = A2dpSinkProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = A2dpSinkProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("A2dpSinkProfile found new device: ");
                    sb.append(obj);
                    Log.w("A2dpSinkProfile", sb.toString());
                    cachedBluetoothDevice = A2dpSinkProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(A2dpSinkProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            A2dpSinkProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            A2dpSinkProfile.this.mIsProfileReady = false;
        }
    }
}
