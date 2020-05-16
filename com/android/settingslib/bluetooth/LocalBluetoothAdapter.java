// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import java.util.Set;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.BluetoothAdapter;

@Deprecated
public class LocalBluetoothAdapter
{
    private static LocalBluetoothAdapter sInstance;
    private final BluetoothAdapter mAdapter;
    private LocalBluetoothProfileManager mProfileManager;
    private int mState;
    
    private LocalBluetoothAdapter(final BluetoothAdapter mAdapter) {
        this.mState = Integer.MIN_VALUE;
        this.mAdapter = mAdapter;
    }
    
    static LocalBluetoothAdapter getInstance() {
        synchronized (LocalBluetoothAdapter.class) {
            if (LocalBluetoothAdapter.sInstance == null) {
                final BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (defaultAdapter != null) {
                    LocalBluetoothAdapter.sInstance = new LocalBluetoothAdapter(defaultAdapter);
                }
            }
            return LocalBluetoothAdapter.sInstance;
        }
    }
    
    public boolean enable() {
        return this.mAdapter.enable();
    }
    
    public BluetoothLeScanner getBluetoothLeScanner() {
        return this.mAdapter.getBluetoothLeScanner();
    }
    
    public int getBluetoothState() {
        synchronized (this) {
            this.syncBluetoothState();
            return this.mState;
        }
    }
    
    public Set<BluetoothDevice> getBondedDevices() {
        return (Set<BluetoothDevice>)this.mAdapter.getBondedDevices();
    }
    
    public int getConnectionState() {
        return this.mAdapter.getConnectionState();
    }
    
    public int getState() {
        return this.mAdapter.getState();
    }
    
    public boolean setBluetoothEnabled(final boolean b) {
        boolean b2;
        if (b) {
            b2 = this.mAdapter.enable();
        }
        else {
            b2 = this.mAdapter.disable();
        }
        if (b2) {
            int bluetoothStateInt;
            if (b) {
                bluetoothStateInt = 11;
            }
            else {
                bluetoothStateInt = 13;
            }
            this.setBluetoothStateInt(bluetoothStateInt);
        }
        else {
            this.syncBluetoothState();
        }
        return b2;
    }
    
    void setBluetoothStateInt(final int mState) {
        synchronized (this) {
            if (this.mState == mState) {
                return;
            }
            this.mState = mState;
            // monitorexit(this)
            if (mState == 12) {
                final LocalBluetoothProfileManager mProfileManager = this.mProfileManager;
                if (mProfileManager != null) {
                    mProfileManager.setBluetoothStateOn();
                }
            }
        }
    }
    
    void setProfileManager(final LocalBluetoothProfileManager mProfileManager) {
        this.mProfileManager = mProfileManager;
    }
    
    boolean syncBluetoothState() {
        if (this.mAdapter.getState() != this.mState) {
            this.setBluetoothStateInt(this.mAdapter.getState());
            return true;
        }
        return false;
    }
}
