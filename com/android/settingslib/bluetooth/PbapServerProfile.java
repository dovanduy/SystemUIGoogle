// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import android.bluetooth.BluetoothProfile$ServiceListener;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.BluetoothPbap;
import android.os.ParcelUuid;
import com.android.internal.annotations.VisibleForTesting;

public class PbapServerProfile implements LocalBluetoothProfile
{
    @VisibleForTesting
    public static final String NAME = "PBAP Server";
    static final ParcelUuid[] PBAB_CLIENT_UUIDS;
    private boolean mIsProfileReady;
    private BluetoothPbap mService;
    
    static {
        PBAB_CLIENT_UUIDS = new ParcelUuid[] { BluetoothUuid.HSP, BluetoothUuid.HFP, BluetoothUuid.PBAP_PCE };
    }
    
    PbapServerProfile(final Context context) {
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, (BluetoothProfile$ServiceListener)new PbapServiceListener(), 6);
    }
    
    @Override
    protected void finalize() {
        Log.d("PbapServerProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(6, (BluetoothProfile)this.mService);
                this.mService = null;
            }
            finally {
                final Throwable t;
                Log.w("PbapServerProfile", "Error cleaning up PBAP proxy", t);
            }
        }
    }
    
    @Override
    public int getConnectionStatus(final BluetoothDevice bluetoothDevice) {
        final BluetoothPbap mService = this.mService;
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
        return 6;
    }
    
    @Override
    public boolean setEnabled(final BluetoothDevice bluetoothDevice, final boolean b) {
        final BluetoothPbap mService = this.mService;
        boolean setConnectionPolicy = false;
        if (mService == null) {
            return false;
        }
        if (!b) {
            setConnectionPolicy = mService.setConnectionPolicy(bluetoothDevice, 0);
        }
        return setConnectionPolicy;
    }
    
    @Override
    public String toString() {
        return "PBAP Server";
    }
    
    private final class PbapServiceListener implements BluetoothProfile$ServiceListener
    {
        public void onServiceConnected(final int n, final BluetoothProfile bluetoothProfile) {
            PbapServerProfile.this.mService = (BluetoothPbap)bluetoothProfile;
            PbapServerProfile.this.mIsProfileReady = true;
        }
        
        public void onServiceDisconnected(final int n) {
            PbapServerProfile.this.mIsProfileReady = false;
        }
    }
}
