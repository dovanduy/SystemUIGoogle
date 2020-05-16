// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import android.bluetooth.BluetoothDevice;
import java.util.ArrayList;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import java.util.List;

public class CachedBluetoothDeviceManager
{
    private final LocalBluetoothManager mBtManager;
    @VisibleForTesting
    final List<CachedBluetoothDevice> mCachedDevices;
    private Context mContext;
    @VisibleForTesting
    HearingAidDeviceManager mHearingAidDeviceManager;
    
    CachedBluetoothDeviceManager(final Context mContext, final LocalBluetoothManager mBtManager) {
        final ArrayList<CachedBluetoothDevice> mCachedDevices = new ArrayList<CachedBluetoothDevice>();
        this.mCachedDevices = mCachedDevices;
        this.mContext = mContext;
        this.mBtManager = mBtManager;
        this.mHearingAidDeviceManager = new HearingAidDeviceManager(mBtManager, mCachedDevices);
    }
    
    private void clearNonBondedSubDevices() {
        for (int i = this.mCachedDevices.size() - 1; i >= 0; --i) {
            final CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(i);
            final CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
            if (subDevice != null && subDevice.getDevice().getBondState() == 10) {
                cachedBluetoothDevice.setSubDevice(null);
            }
        }
    }
    
    public CachedBluetoothDevice addDevice(final BluetoothDevice bluetoothDevice) {
        final LocalBluetoothProfileManager profileManager = this.mBtManager.getProfileManager();
        synchronized (this) {
            CachedBluetoothDevice device;
            if ((device = this.findDevice(bluetoothDevice)) == null) {
                final CachedBluetoothDevice subDeviceIfNeeded = new CachedBluetoothDevice(this.mContext, profileManager, bluetoothDevice);
                this.mHearingAidDeviceManager.initHearingAidDeviceIfNeeded(subDeviceIfNeeded);
                device = subDeviceIfNeeded;
                if (!this.mHearingAidDeviceManager.setSubDeviceIfNeeded(subDeviceIfNeeded)) {
                    this.mCachedDevices.add(subDeviceIfNeeded);
                    this.mBtManager.getEventManager().dispatchDeviceAdded(subDeviceIfNeeded);
                    device = subDeviceIfNeeded;
                }
            }
            return device;
        }
    }
    
    public void clearNonBondedDevices() {
        synchronized (this) {
            this.clearNonBondedSubDevices();
            this.mCachedDevices.removeIf((Predicate<? super Object>)_$$Lambda$CachedBluetoothDeviceManager$1n6G0RUX5KnCwfoBdpyaC68q3xA.INSTANCE);
        }
    }
    
    public CachedBluetoothDevice findDevice(final BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            for (final CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
                if (cachedBluetoothDevice.getDevice().equals((Object)bluetoothDevice)) {
                    return cachedBluetoothDevice;
                }
                final CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
                if (subDevice != null && subDevice.getDevice().equals((Object)bluetoothDevice)) {
                    return subDevice;
                }
            }
            return null;
        }
    }
    
    public Collection<CachedBluetoothDevice> getCachedDevicesCopy() {
        synchronized (this) {
            return new ArrayList<CachedBluetoothDevice>(this.mCachedDevices);
        }
    }
    
    public boolean isSubDevice(final BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            for (final CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
                if (!cachedBluetoothDevice.getDevice().equals((Object)bluetoothDevice)) {
                    final CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
                    if (subDevice != null && subDevice.getDevice().equals((Object)bluetoothDevice)) {
                        return true;
                    }
                    continue;
                }
            }
            return false;
        }
    }
    
    public void onBluetoothStateChanged(int i) {
        // monitorenter(this)
        if (i == 13) {
            try {
                CachedBluetoothDevice cachedBluetoothDevice;
                CachedBluetoothDevice subDevice;
                for (i = this.mCachedDevices.size() - 1; i >= 0; --i) {
                    cachedBluetoothDevice = this.mCachedDevices.get(i);
                    subDevice = cachedBluetoothDevice.getSubDevice();
                    if (subDevice != null && subDevice.getBondState() != 12) {
                        cachedBluetoothDevice.setSubDevice(null);
                    }
                    if (cachedBluetoothDevice.getBondState() != 12) {
                        cachedBluetoothDevice.setJustDiscovered(false);
                        this.mCachedDevices.remove(i);
                    }
                }
            }
            finally {
            }
            // monitorexit(this)
        }
    }
    // monitorexit(this)
    
    public void onDeviceNameUpdated(final BluetoothDevice bluetoothDevice) {
        final CachedBluetoothDevice device = this.findDevice(bluetoothDevice);
        if (device != null) {
            device.refreshName();
        }
    }
    
    public void onDeviceUnpaired(final CachedBluetoothDevice cachedBluetoothDevice) {
        synchronized (this) {
            final CachedBluetoothDevice mainDevice = this.mHearingAidDeviceManager.findMainDevice(cachedBluetoothDevice);
            final CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
            if (subDevice != null) {
                subDevice.unpair();
                cachedBluetoothDevice.setSubDevice(null);
            }
            else if (mainDevice != null) {
                mainDevice.unpair();
                mainDevice.setSubDevice(null);
            }
        }
    }
    
    public boolean onProfileConnectionStateChangedIfProcessed(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
        synchronized (this) {
            return this.mHearingAidDeviceManager.onProfileConnectionStateChangedIfProcessed(cachedBluetoothDevice, n);
        }
    }
    
    public void onScanningStateChanged(final boolean b) {
        // monitorenter(this)
        if (!b) {
            // monitorexit(this)
            return;
        }
        try {
            for (int i = this.mCachedDevices.size() - 1; i >= 0; --i) {
                final CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(i);
                cachedBluetoothDevice.setJustDiscovered(false);
                final CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
                if (subDevice != null) {
                    subDevice.setJustDiscovered(false);
                }
            }
        }
        finally {
        }
        // monitorexit(this)
    }
    
    public void updateHearingAidsDevices() {
        synchronized (this) {
            this.mHearingAidDeviceManager.updateHearingAidsDevices();
        }
    }
}
