// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import java.util.Iterator;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import android.bluetooth.BluetoothProfile$ServiceListener;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.bluetooth.BluetoothHidDevice;

public class HidDeviceProfile implements LocalBluetoothProfile
{
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private BluetoothHidDevice mService;
    
    HidDeviceProfile(final Context context, final CachedBluetoothDeviceManager mDeviceManager, final LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = mDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new HidDeviceServiceListener(), 19);
    }
    
    @Override
    protected void finalize() {
        Log.d("HidDeviceProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(19, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("HidDeviceProfile", "Error cleaning up HID proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothHidDevice mService = this.mService;
        if (mService == null) {
            return 0;
        }
        return mService.getConnectionState(bluetoothDevice);
    }
    
    @Override
    public int getDrawableResource(final BluetoothClass bluetoothClass) {
        return 17302324;
    }
    
    @Override
    public int getProfileId() {
        return 19;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        boolean setConnectionPolicy = false;
        if (!b) {
            setConnectionPolicy = this.mService.setConnectionPolicy(bluetoothDevice, 0);
        }
        return setConnectionPolicy;
    }
    
    @Override
    public String toString() {
        return "HID DEVICE";
    }
    
    private final class HidDeviceServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            HidDeviceProfile.this.mService = (BluetoothHidDevice)bluetoothProfile;
            for (final BluetoothDevice obj : HidDeviceProfile.this.mService.getConnectedDevices()) {
                CachedBluetoothDevice obj2;
                if ((obj2 = HidDeviceProfile.this.mDeviceManager.findDevice(obj)) == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("HidProfile found new device: ");
                    sb.append(obj);
                    Log.w("HidDeviceProfile", sb.toString());
                    obj2 = HidDeviceProfile.this.mDeviceManager.addDevice(obj);
                }
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Connection status changed: ");
                sb2.append(obj2);
                Log.d("HidDeviceProfile", sb2.toString());
                obj2.onProfileStateChanged(HidDeviceProfile.this, 2);
                obj2.refresh();
            }
            HidDeviceProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            HidDeviceProfile.this.mIsProfileReady = false;
        }
    }
}
