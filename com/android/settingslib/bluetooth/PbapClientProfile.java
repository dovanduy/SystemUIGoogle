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
import android.bluetooth.BluetoothPbapClient;
import android.os.ParcelUuid;

public final class PbapClientProfile implements LocalBluetoothProfile
{
    static final ParcelUuid[] SRC_UUIDS;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private BluetoothPbapClient mService;
    
    static {
        SRC_UUIDS = new ParcelUuid[] { BluetoothUuid.PBAP_PSE };
    }
    
    PbapClientProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = mDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new PbapClientServiceListener(), 17);
    }
    
    @Override
    protected void finalize() {
        Log.d("PbapClientProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(17, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("PbapClientProfile", "Error cleaning up PBAP Client proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothPbapClient mService = this.mService;
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
        return 17;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothPbapClient mService = this.mService;
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
        return "PbapClient";
    }
    
    private final class PbapClientServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            PbapClientProfile.this.mService = (BluetoothPbapClient)bluetoothProfile;
            final List connectedDevices = PbapClientProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = PbapClientProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("PbapClientProfile found new device: ");
                    sb.append(obj);
                    Log.w("PbapClientProfile", sb.toString());
                    cachedBluetoothDevice = PbapClientProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(PbapClientProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            PbapClientProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            PbapClientProfile.this.mIsProfileReady = false;
        }
    }
}
