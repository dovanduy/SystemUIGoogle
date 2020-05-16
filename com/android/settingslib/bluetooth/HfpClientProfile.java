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
import android.bluetooth.BluetoothHeadsetClient;

final class HfpClientProfile implements LocalBluetoothProfile
{
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private BluetoothHeadsetClient mService;
    
    static {
        final ParcelUuid hsp_AG = BluetoothUuid.HSP_AG;
        final ParcelUuid hfp_AG = BluetoothUuid.HFP_AG;
    }
    
    HfpClientProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = mDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new HfpClientServiceListener(), 16);
    }
    
    @Override
    protected void finalize() {
        Log.d("HfpClientProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(16, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("HfpClientProfile", "Error cleaning up HfpClient proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothHeadsetClient mService = this.mService;
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
        return 16;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothHeadsetClient mService = this.mService;
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
        return "HEADSET_CLIENT";
    }
    
    private final class HfpClientServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            HfpClientProfile.this.mService = (BluetoothHeadsetClient)bluetoothProfile;
            final List connectedDevices = HfpClientProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = HfpClientProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("HfpClient profile found new device: ");
                    sb.append(obj);
                    Log.w("HfpClientProfile", sb.toString());
                    cachedBluetoothDevice = HfpClientProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(HfpClientProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            HfpClientProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            HfpClientProfile.this.mIsProfileReady = false;
        }
    }
}
