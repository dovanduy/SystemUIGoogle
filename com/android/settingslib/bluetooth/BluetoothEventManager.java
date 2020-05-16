// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import com.android.settingslib.R$string;
import android.util.Log;
import android.content.Intent;
import java.util.Set;
import android.bluetooth.BluetoothDevice;
import java.util.Objects;
import java.util.Iterator;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import android.os.UserHandle;
import android.os.Handler;
import java.util.Map;
import android.content.Context;
import java.util.Collection;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class BluetoothEventManager
{
    private final IntentFilter mAdapterIntentFilter;
    private final BroadcastReceiver mBroadcastReceiver;
    private final Collection<BluetoothCallback> mCallbacks;
    private final Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private final Map<String, Handler> mHandlerMap;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final BroadcastReceiver mProfileBroadcastReceiver;
    private final IntentFilter mProfileIntentFilter;
    private final android.os.Handler mReceiverHandler;
    private final UserHandle mUserHandle;
    
    BluetoothEventManager(final LocalBluetoothAdapter mLocalAdapter, final CachedBluetoothDeviceManager mDeviceManager, final Context mContext, final android.os.Handler mReceiverHandler, final UserHandle mUserHandle) {
        this.mBroadcastReceiver = new BluetoothBroadcastReceiver();
        this.mProfileBroadcastReceiver = new BluetoothBroadcastReceiver();
        this.mCallbacks = new CopyOnWriteArrayList<BluetoothCallback>();
        this.mLocalAdapter = mLocalAdapter;
        this.mDeviceManager = mDeviceManager;
        this.mAdapterIntentFilter = new IntentFilter();
        this.mProfileIntentFilter = new IntentFilter();
        this.mHandlerMap = new HashMap<String, Handler>();
        this.mContext = mContext;
        this.mUserHandle = mUserHandle;
        this.mReceiverHandler = mReceiverHandler;
        this.addHandler("android.bluetooth.adapter.action.STATE_CHANGED", (Handler)new AdapterStateChangedHandler());
        this.addHandler("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED", (Handler)new ConnectionStateChangedHandler());
        this.addHandler("android.bluetooth.adapter.action.DISCOVERY_STARTED", (Handler)new ScanningStateChangedHandler(true));
        this.addHandler("android.bluetooth.adapter.action.DISCOVERY_FINISHED", (Handler)new ScanningStateChangedHandler(false));
        this.addHandler("android.bluetooth.device.action.FOUND", (Handler)new DeviceFoundHandler());
        this.addHandler("android.bluetooth.device.action.NAME_CHANGED", (Handler)new NameChangedHandler());
        this.addHandler("android.bluetooth.device.action.ALIAS_CHANGED", (Handler)new NameChangedHandler());
        this.addHandler("android.bluetooth.device.action.BOND_STATE_CHANGED", (Handler)new BondStateChangedHandler());
        this.addHandler("android.bluetooth.device.action.CLASS_CHANGED", (Handler)new ClassChangedHandler());
        this.addHandler("android.bluetooth.device.action.UUID", (Handler)new UuidChangedHandler());
        this.addHandler("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED", (Handler)new BatteryLevelChangedHandler());
        this.addHandler("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED", (Handler)new ActiveDeviceChangedHandler());
        this.addHandler("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED", (Handler)new ActiveDeviceChangedHandler());
        this.addHandler("android.bluetooth.hearingaid.profile.action.ACTIVE_DEVICE_CHANGED", (Handler)new ActiveDeviceChangedHandler());
        this.addHandler("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED", (Handler)new AudioModeChangedHandler());
        this.addHandler("android.intent.action.PHONE_STATE", (Handler)new AudioModeChangedHandler());
        this.addHandler("android.bluetooth.device.action.ACL_CONNECTED", (Handler)new AclStateChangedHandler());
        this.addHandler("android.bluetooth.device.action.ACL_DISCONNECTED", (Handler)new AclStateChangedHandler());
        this.registerAdapterIntentReceiver();
    }
    
    private void dispatchAclStateChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
        final Iterator<BluetoothCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onAclConnectionStateChanged(cachedBluetoothDevice, n);
        }
    }
    
    private void dispatchAudioModeChanged() {
        final Iterator<CachedBluetoothDevice> iterator = this.mDeviceManager.getCachedDevicesCopy().iterator();
        while (iterator.hasNext()) {
            iterator.next().onAudioModeChanged();
        }
        final Iterator<BluetoothCallback> iterator2 = this.mCallbacks.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().onAudioModeChanged();
        }
    }
    
    private void dispatchConnectionStateChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
        final Iterator<BluetoothCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onConnectionStateChanged(cachedBluetoothDevice, n);
        }
    }
    
    private void registerIntentReceiver(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter) {
        final UserHandle mUserHandle = this.mUserHandle;
        if (mUserHandle == null) {
            this.mContext.registerReceiver(broadcastReceiver, intentFilter, (String)null, this.mReceiverHandler);
        }
        else {
            this.mContext.registerReceiverAsUser(broadcastReceiver, mUserHandle, intentFilter, (String)null, this.mReceiverHandler);
        }
    }
    
    void addHandler(final String s, final Handler handler) {
        this.mHandlerMap.put(s, handler);
        this.mAdapterIntentFilter.addAction(s);
    }
    
    void addProfileHandler(final String s, final Handler handler) {
        this.mHandlerMap.put(s, handler);
        this.mProfileIntentFilter.addAction(s);
    }
    
    void dispatchActiveDeviceChanged(final CachedBluetoothDevice b, final int n) {
        for (final CachedBluetoothDevice a : this.mDeviceManager.getCachedDevicesCopy()) {
            a.onActiveDeviceChanged(Objects.equals(a, b), n);
        }
        final Iterator<BluetoothCallback> iterator2 = this.mCallbacks.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().onActiveDeviceChanged(b, n);
        }
    }
    
    void dispatchDeviceAdded(final CachedBluetoothDevice cachedBluetoothDevice) {
        final Iterator<BluetoothCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDeviceAdded(cachedBluetoothDevice);
        }
    }
    
    void dispatchDeviceRemoved(final CachedBluetoothDevice cachedBluetoothDevice) {
        final Iterator<BluetoothCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDeviceDeleted(cachedBluetoothDevice);
        }
    }
    
    void dispatchProfileConnectionStateChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n, final int n2) {
        final Iterator<BluetoothCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onProfileConnectionStateChanged(cachedBluetoothDevice, n, n2);
        }
    }
    
    boolean readPairedDevices() {
        final Set<BluetoothDevice> bondedDevices = this.mLocalAdapter.getBondedDevices();
        boolean b = false;
        if (bondedDevices == null) {
            return false;
        }
        for (final BluetoothDevice bluetoothDevice : bondedDevices) {
            if (this.mDeviceManager.findDevice(bluetoothDevice) == null) {
                this.mDeviceManager.addDevice(bluetoothDevice);
                b = true;
            }
        }
        return b;
    }
    
    void registerAdapterIntentReceiver() {
        this.registerIntentReceiver(this.mBroadcastReceiver, this.mAdapterIntentFilter);
    }
    
    public void registerCallback(final BluetoothCallback bluetoothCallback) {
        this.mCallbacks.add(bluetoothCallback);
    }
    
    void registerProfileIntentReceiver() {
        this.registerIntentReceiver(this.mProfileBroadcastReceiver, this.mProfileIntentFilter);
    }
    
    private class AclStateChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            if (bluetoothDevice == null) {
                Log.w("BluetoothEventManager", "AclStateChangedHandler: device is null");
                return;
            }
            if (BluetoothEventManager.this.mDeviceManager.isSubDevice(bluetoothDevice)) {
                return;
            }
            final String action = intent.getAction();
            if (action == null) {
                Log.w("BluetoothEventManager", "AclStateChangedHandler: action is null");
                return;
            }
            final CachedBluetoothDevice device = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (device == null) {
                Log.w("BluetoothEventManager", "AclStateChangedHandler: activeDevice is null");
                return;
            }
            int n = -1;
            final int hashCode = action.hashCode();
            int n2 = 0;
            if (hashCode != -301431627) {
                if (hashCode == 1821585647) {
                    if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                        n = 1;
                    }
                }
            }
            else if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                n = 0;
            }
            if (n != 0) {
                if (n != 1) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("ActiveDeviceChangedHandler: unknown action ");
                    sb.append(action);
                    Log.w("BluetoothEventManager", sb.toString());
                    return;
                }
            }
            else {
                n2 = 2;
            }
            BluetoothEventManager.this.dispatchAclStateChanged(device, n2);
        }
    }
    
    private class ActiveDeviceChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            final String action = intent.getAction();
            if (action == null) {
                Log.w("BluetoothEventManager", "ActiveDeviceChangedHandler: action is null");
                return;
            }
            final CachedBluetoothDevice device = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            int n;
            if (Objects.equals(action, "android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED")) {
                n = 2;
            }
            else if (Objects.equals(action, "android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED")) {
                n = 1;
            }
            else {
                if (!Objects.equals(action, "android.bluetooth.hearingaid.profile.action.ACTIVE_DEVICE_CHANGED")) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("ActiveDeviceChangedHandler: unknown action ");
                    sb.append(action);
                    Log.w("BluetoothEventManager", sb.toString());
                    return;
                }
                n = 21;
            }
            BluetoothEventManager.this.dispatchActiveDeviceChanged(device, n);
        }
    }
    
    private class AdapterStateChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            final int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            BluetoothEventManager.this.mLocalAdapter.setBluetoothStateInt(intExtra);
            final Iterator<BluetoothCallback> iterator = BluetoothEventManager.this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onBluetoothStateChanged(intExtra);
            }
            BluetoothEventManager.this.mDeviceManager.onBluetoothStateChanged(intExtra);
        }
    }
    
    private class AudioModeChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            if (intent.getAction() == null) {
                Log.w("BluetoothEventManager", "AudioModeChangedHandler() action is null");
                return;
            }
            BluetoothEventManager.this.dispatchAudioModeChanged();
        }
    }
    
    private class BatteryLevelChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            final CachedBluetoothDevice device = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (device != null) {
                device.refresh();
            }
        }
    }
    
    private class BluetoothBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            final BluetoothDevice bluetoothDevice = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            final Handler handler = BluetoothEventManager.this.mHandlerMap.get(action);
            if (handler != null) {
                handler.onReceive(context, intent, bluetoothDevice);
            }
        }
    }
    
    private class BondStateChangedHandler implements Handler
    {
        private void showUnbondMessage(final Context context, final String s, int i) {
            switch (i) {
                default: {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("showUnbondMessage: Not displaying any message for reason: ");
                    sb.append(i);
                    Log.w("BluetoothEventManager", sb.toString());
                    return;
                }
                case 5:
                case 6:
                case 7:
                case 8: {
                    i = R$string.bluetooth_pairing_error_message;
                    break;
                }
                case 4: {
                    i = R$string.bluetooth_pairing_device_down_error_message;
                    break;
                }
                case 2: {
                    i = R$string.bluetooth_pairing_rejected_error_message;
                    break;
                }
                case 1: {
                    i = R$string.bluetooth_pairing_pin_error_message;
                    break;
                }
            }
            BluetoothUtils.showError(context, s, i);
        }
        
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice obj) {
            if (obj == null) {
                Log.e("BluetoothEventManager", "ACTION_BOND_STATE_CHANGED with no EXTRA_DEVICE");
                return;
            }
            final int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
            CachedBluetoothDevice cachedBluetoothDevice;
            if ((cachedBluetoothDevice = BluetoothEventManager.this.mDeviceManager.findDevice(obj)) == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Got bonding state changed for ");
                sb.append(obj);
                sb.append(", but we have no record of that device.");
                Log.w("BluetoothEventManager", sb.toString());
                cachedBluetoothDevice = BluetoothEventManager.this.mDeviceManager.addDevice(obj);
            }
            final Iterator<BluetoothCallback> iterator = BluetoothEventManager.this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onDeviceBondStateChanged(cachedBluetoothDevice, intExtra);
            }
            cachedBluetoothDevice.onBondingStateChanged(intExtra);
            if (intExtra == 10) {
                if (cachedBluetoothDevice.getHiSyncId() != 0L) {
                    BluetoothEventManager.this.mDeviceManager.onDeviceUnpaired(cachedBluetoothDevice);
                }
                this.showUnbondMessage(context, cachedBluetoothDevice.getName(), intent.getIntExtra("android.bluetooth.device.extra.REASON", Integer.MIN_VALUE));
            }
        }
    }
    
    private class ClassChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            final CachedBluetoothDevice device = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (device != null) {
                device.refresh();
            }
        }
    }
    
    private class ConnectionStateChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.dispatchConnectionStateChanged(BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice), intent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", Integer.MIN_VALUE));
        }
    }
    
    private class DeviceFoundHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            final short shortExtra = intent.getShortExtra("android.bluetooth.device.extra.RSSI", (short)(-32768));
            intent.getStringExtra("android.bluetooth.device.extra.NAME");
            CachedBluetoothDevice obj = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (obj == null) {
                obj = BluetoothEventManager.this.mDeviceManager.addDevice(bluetoothDevice);
                final StringBuilder sb = new StringBuilder();
                sb.append("DeviceFoundHandler created new CachedBluetoothDevice: ");
                sb.append(obj);
                Log.d("BluetoothEventManager", sb.toString());
            }
            else if (obj.getBondState() == 12 && !obj.getDevice().isConnected()) {
                BluetoothEventManager.this.dispatchDeviceAdded(obj);
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("DeviceFoundHandler found bonded and not connected device:");
                sb2.append(obj);
                Log.d("BluetoothEventManager", sb2.toString());
            }
            else {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("DeviceFoundHandler found existing CachedBluetoothDevice:");
                sb3.append(obj);
                Log.d("BluetoothEventManager", sb3.toString());
            }
            obj.setRssi(shortExtra);
            obj.setJustDiscovered(true);
        }
    }
    
    interface Handler
    {
        void onReceive(final Context p0, final Intent p1, final BluetoothDevice p2);
    }
    
    private class NameChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.mDeviceManager.onDeviceNameUpdated(bluetoothDevice);
        }
    }
    
    private class ScanningStateChangedHandler implements Handler
    {
        private final boolean mStarted;
        
        ScanningStateChangedHandler(final boolean mStarted) {
            this.mStarted = mStarted;
        }
        
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            final Iterator<BluetoothCallback> iterator = BluetoothEventManager.this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onScanningStateChanged(this.mStarted);
            }
            BluetoothEventManager.this.mDeviceManager.onScanningStateChanged(this.mStarted);
        }
    }
    
    private class UuidChangedHandler implements Handler
    {
        @Override
        public void onReceive(final Context context, final Intent intent, final BluetoothDevice bluetoothDevice) {
            final CachedBluetoothDevice device = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (device != null) {
                device.onUuidChanged();
            }
        }
    }
}
