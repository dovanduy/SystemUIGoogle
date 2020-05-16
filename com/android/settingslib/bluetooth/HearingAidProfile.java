// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import java.util.ArrayList;
import android.bluetooth.BluetoothDevice;
import java.util.List;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import android.bluetooth.BluetoothProfile$ServiceListener;
import android.content.Context;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothAdapter;

public class HearingAidProfile implements LocalBluetoothProfile
{
    private static boolean V = true;
    private final BluetoothAdapter mBluetoothAdapter;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothHearingAid mService;
    
    HearingAidProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager mProfileManager) {
        this.mDeviceManager = mDeviceManager;
        this.mProfileManager = mProfileManager;
        (this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()).getProfileProxy(context, (BluetoothProfile$ServiceListener)new HearingAidServiceListener(), 21);
    }
    
    @Override
    protected void finalize() {
        Log.d("HearingAidProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(21, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("HearingAidProfile", "Error cleaning up Hearing Aid proxy", t);
            }
        }
    }
    
    public List<BluetoothDevice> getActiveDevices() {
        final BluetoothHearingAid mService = this.mService;
        if (mService == null) {
            return new ArrayList<BluetoothDevice>();
        }
        return (List<BluetoothDevice>)mService.getActiveDevices();
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothHearingAid mService = this.mService;
        if (mService == null) {
            return 0;
        }
        return mService.getConnectionState(bluetoothDevice);
    }
    
    @Override
    public int getDrawableResource(final BluetoothClass bluetoothClass) {
        return 17302322;
    }
    
    public long getHiSyncId(final BluetoothDevice bluetoothDevice) {
        final BluetoothHearingAid mService = this.mService;
        if (mService != null && bluetoothDevice != null) {
            return mService.getHiSyncId(bluetoothDevice);
        }
        return 0L;
    }
    
    @Override
    public int getProfileId() {
        return 21;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothHearingAid mService = this.mService;
        boolean b3;
        final boolean b2 = b3 = false;
        if (mService != null) {
            if (bluetoothDevice == null) {
                b3 = b2;
            }
            else if (b) {
                b3 = b2;
                if (mService.getConnectionPolicy(bluetoothDevice) < 100) {
                    b3 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
                }
            }
            else {
                b3 = mService.setConnectionPolicy(bluetoothDevice, 0);
            }
        }
        return b3;
    }
    
    @Override
    public String toString() {
        return "HearingAid";
    }
    
    private final class HearingAidServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            HearingAidProfile.this.mService = (BluetoothHearingAid)bluetoothProfile;
            final List connectedDevices = HearingAidProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                final BluetoothDevice obj = connectedDevices.remove(0);
                CachedBluetoothDevice cachedBluetoothDevice;
                if ((cachedBluetoothDevice = HearingAidProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    if (HearingAidProfile.V) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("HearingAidProfile found new device: ");
                        sb.append(obj);
                        Log.d("HearingAidProfile", sb.toString());
                    }
                    cachedBluetoothDevice = HearingAidProfile.this.mDeviceManager.addDevice(obj);
                }
                cachedBluetoothDevice.onProfileStateChanged(HearingAidProfile.this, 2);
                cachedBluetoothDevice.refresh();
            }
            HearingAidProfile.this.mDeviceManager.updateHearingAidsDevices();
            HearingAidProfile.this.mIsProfileReady = true;
            HearingAidProfile.this.mProfileManager.callServiceConnectedListeners();
        }
        
        public void onServiceDisconnected(final int n) {
            HearingAidProfile.this.mIsProfileReady = false;
        }
    }
}
