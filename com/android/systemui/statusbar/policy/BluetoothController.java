// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.Collection;
import java.util.List;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.systemui.Dumpable;

public interface BluetoothController extends CallbackController<Callback>, Dumpable
{
    boolean canConfigBluetooth();
    
    void connect(final CachedBluetoothDevice p0);
    
    void disconnect(final CachedBluetoothDevice p0);
    
    int getBluetoothState();
    
    int getBondState(final CachedBluetoothDevice p0);
    
    String getConnectedDeviceName();
    
    List<CachedBluetoothDevice> getConnectedDevices();
    
    Collection<CachedBluetoothDevice> getDevices();
    
    boolean isBluetoothAudioActive();
    
    boolean isBluetoothAudioProfileOnly();
    
    boolean isBluetoothConnected();
    
    boolean isBluetoothConnecting();
    
    boolean isBluetoothEnabled();
    
    boolean isBluetoothSupported();
    
    void setBluetoothEnabled(final boolean p0);
    
    public interface Callback
    {
        void onBluetoothDevicesChanged();
        
        void onBluetoothStateChange(final boolean p0);
    }
}
