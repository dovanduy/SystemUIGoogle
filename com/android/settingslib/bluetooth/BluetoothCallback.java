// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

public interface BluetoothCallback
{
    default void onAclConnectionStateChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
    }
    
    default void onActiveDeviceChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
    }
    
    default void onAudioModeChanged() {
    }
    
    default void onBluetoothStateChanged(final int n) {
    }
    
    default void onConnectionStateChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
    }
    
    default void onDeviceAdded(final CachedBluetoothDevice cachedBluetoothDevice) {
    }
    
    default void onDeviceBondStateChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
    }
    
    default void onDeviceDeleted(final CachedBluetoothDevice cachedBluetoothDevice) {
    }
    
    default void onProfileConnectionStateChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n, final int n2) {
    }
    
    default void onScanningStateChanged(final boolean b) {
    }
}
