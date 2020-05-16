// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import java.util.HashSet;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Iterator;
import android.util.Log;
import android.bluetooth.BluetoothDevice;
import java.util.List;

public class HearingAidDeviceManager
{
    private final LocalBluetoothManager mBtManager;
    private final List<CachedBluetoothDevice> mCachedDevices;
    
    HearingAidDeviceManager(final LocalBluetoothManager mBtManager, final List<CachedBluetoothDevice> mCachedDevices) {
        this.mBtManager = mBtManager;
        this.mCachedDevices = mCachedDevices;
    }
    
    private CachedBluetoothDevice getCachedDevice(final long n) {
        for (int i = this.mCachedDevices.size() - 1; i >= 0; --i) {
            final CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(i);
            if (cachedBluetoothDevice.getHiSyncId() == n) {
                return cachedBluetoothDevice;
            }
        }
        return null;
    }
    
    private long getHiSyncId(final BluetoothDevice bluetoothDevice) {
        final HearingAidProfile hearingAidProfile = this.mBtManager.getProfileManager().getHearingAidProfile();
        if (hearingAidProfile != null) {
            return hearingAidProfile.getHiSyncId(bluetoothDevice);
        }
        return 0L;
    }
    
    private boolean isValidHiSyncId(final long n) {
        return n != 0L;
    }
    
    private void log(final String s) {
        Log.d("HearingAidDeviceManager", s);
    }
    
    CachedBluetoothDevice findMainDevice(final CachedBluetoothDevice cachedBluetoothDevice) {
        for (final CachedBluetoothDevice cachedBluetoothDevice2 : this.mCachedDevices) {
            if (this.isValidHiSyncId(cachedBluetoothDevice2.getHiSyncId())) {
                final CachedBluetoothDevice subDevice = cachedBluetoothDevice2.getSubDevice();
                if (subDevice != null && subDevice.equals(cachedBluetoothDevice)) {
                    return cachedBluetoothDevice2;
                }
                continue;
            }
        }
        return null;
    }
    
    void initHearingAidDeviceIfNeeded(final CachedBluetoothDevice cachedBluetoothDevice) {
        final long hiSyncId = this.getHiSyncId(cachedBluetoothDevice.getDevice());
        if (this.isValidHiSyncId(hiSyncId)) {
            cachedBluetoothDevice.setHiSyncId(hiSyncId);
        }
    }
    
    @VisibleForTesting
    void onHiSyncIdChanged(final long lng) {
        int i = this.mCachedDevices.size() - 1;
        int n = -1;
        while (i >= 0) {
            final CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(i);
            if (cachedBluetoothDevice.getHiSyncId() == lng) {
                if (n != -1) {
                    CachedBluetoothDevice cachedBluetoothDevice2;
                    CachedBluetoothDevice cachedBluetoothDevice3;
                    if (cachedBluetoothDevice.isConnected()) {
                        cachedBluetoothDevice2 = this.mCachedDevices.get(n);
                        i = n;
                        cachedBluetoothDevice3 = cachedBluetoothDevice;
                    }
                    else {
                        cachedBluetoothDevice3 = this.mCachedDevices.get(n);
                        cachedBluetoothDevice2 = cachedBluetoothDevice;
                    }
                    cachedBluetoothDevice3.setSubDevice(cachedBluetoothDevice2);
                    this.mCachedDevices.remove(i);
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onHiSyncIdChanged: removed from UI device =");
                    sb.append(cachedBluetoothDevice2);
                    sb.append(", with hiSyncId=");
                    sb.append(lng);
                    this.log(sb.toString());
                    this.mBtManager.getEventManager().dispatchDeviceRemoved(cachedBluetoothDevice2);
                    break;
                }
                n = i;
            }
            --i;
        }
    }
    
    boolean onProfileConnectionStateChangedIfProcessed(CachedBluetoothDevice mainDevice, final int n) {
        if (n != 0) {
            if (n == 2) {
                this.onHiSyncIdChanged(mainDevice.getHiSyncId());
                mainDevice = this.findMainDevice(mainDevice);
                if (mainDevice != null) {
                    if (mainDevice.isConnected()) {
                        mainDevice.refresh();
                        return true;
                    }
                    this.mBtManager.getEventManager().dispatchDeviceRemoved(mainDevice);
                    mainDevice.switchSubDeviceContent();
                    mainDevice.refresh();
                    this.mBtManager.getEventManager().dispatchDeviceAdded(mainDevice);
                    return true;
                }
            }
        }
        else {
            final CachedBluetoothDevice mainDevice2 = this.findMainDevice(mainDevice);
            if (mainDevice2 != null) {
                mainDevice2.refresh();
                return true;
            }
            final CachedBluetoothDevice subDevice = mainDevice.getSubDevice();
            if (subDevice != null && subDevice.isConnected()) {
                this.mBtManager.getEventManager().dispatchDeviceRemoved(mainDevice);
                mainDevice.switchSubDeviceContent();
                mainDevice.refresh();
                this.mBtManager.getEventManager().dispatchDeviceAdded(mainDevice);
                return true;
            }
        }
        return false;
    }
    
    boolean setSubDeviceIfNeeded(final CachedBluetoothDevice subDevice) {
        final long hiSyncId = subDevice.getHiSyncId();
        if (this.isValidHiSyncId(hiSyncId)) {
            final CachedBluetoothDevice cachedDevice = this.getCachedDevice(hiSyncId);
            if (cachedDevice != null) {
                cachedDevice.setSubDevice(subDevice);
                return true;
            }
        }
        return false;
    }
    
    void updateHearingAidsDevices() {
        final HashSet<Long> set = new HashSet<Long>();
        for (final CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
            if (!this.isValidHiSyncId(cachedBluetoothDevice.getHiSyncId())) {
                final long hiSyncId = this.getHiSyncId(cachedBluetoothDevice.getDevice());
                if (!this.isValidHiSyncId(hiSyncId)) {
                    continue;
                }
                cachedBluetoothDevice.setHiSyncId(hiSyncId);
                set.add(hiSyncId);
            }
        }
        final Iterator<Object> iterator2 = set.iterator();
        while (iterator2.hasNext()) {
            this.onHiSyncIdChanged(iterator2.next());
        }
    }
}
