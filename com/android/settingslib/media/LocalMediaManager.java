// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.bluetooth.BluetoothDevice;
import java.util.ArrayList;
import android.text.TextUtils;
import android.util.Log;
import java.util.Iterator;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Comparator;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.List;
import android.content.Context;
import java.util.Collection;
import com.android.internal.annotations.VisibleForTesting;
import android.bluetooth.BluetoothAdapter;
import com.android.settingslib.bluetooth.BluetoothCallback;

public class LocalMediaManager implements BluetoothCallback
{
    @VisibleForTesting
    BluetoothAdapter mBluetoothAdapter;
    private final Collection<DeviceCallback> mCallbacks;
    private Context mContext;
    @VisibleForTesting
    MediaDevice mCurrentConnectedDevice;
    @VisibleForTesting
    DeviceAttributeChangeCallback mDeviceAttributeChangeCallback;
    @VisibleForTesting
    List<MediaDevice> mDisconnectedMediaDevices;
    private InfoMediaManager mInfoMediaManager;
    private LocalBluetoothManager mLocalBluetoothManager;
    @VisibleForTesting
    final MediaDeviceCallback mMediaDeviceCallback;
    @VisibleForTesting
    List<MediaDevice> mMediaDevices;
    private MediaDevice mOnTransferBluetoothDevice;
    private String mPackageName;
    @VisibleForTesting
    MediaDevice mPhoneDevice;
    
    static {
        Comparator.naturalOrder();
    }
    
    public LocalMediaManager(final Context mContext, final LocalBluetoothManager mLocalBluetoothManager, final InfoMediaManager mInfoMediaManager, final String mPackageName) {
        this.mCallbacks = new CopyOnWriteArrayList<DeviceCallback>();
        this.mMediaDeviceCallback = new MediaDeviceCallback();
        this.mMediaDevices = new CopyOnWriteArrayList<MediaDevice>();
        this.mDisconnectedMediaDevices = new CopyOnWriteArrayList<MediaDevice>();
        this.mDeviceAttributeChangeCallback = new DeviceAttributeChangeCallback();
        this.mContext = mContext;
        this.mLocalBluetoothManager = mLocalBluetoothManager;
        this.mInfoMediaManager = mInfoMediaManager;
        this.mPackageName = mPackageName;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    
    private Collection<DeviceCallback> getCallbacks() {
        return new CopyOnWriteArrayList<DeviceCallback>(this.mCallbacks);
    }
    
    private boolean isActiveDevice(final CachedBluetoothDevice cachedBluetoothDevice) {
        return cachedBluetoothDevice.isActiveDevice(2) || cachedBluetoothDevice.isActiveDevice(21);
    }
    
    private void unRegisterDeviceAttributeChangeCallback() {
        final Iterator<MediaDevice> iterator = this.mDisconnectedMediaDevices.iterator();
        while (iterator.hasNext()) {
            ((BluetoothMediaDevice)iterator.next()).getCachedDevice().unregisterCallback((CachedBluetoothDevice.Callback)this.mDeviceAttributeChangeCallback);
        }
    }
    
    private MediaDevice updateCurrentConnectedDevice() {
        final Iterator<MediaDevice> iterator = this.mMediaDevices.iterator();
        MediaDevice mediaDevice = null;
        MediaDevice mediaDevice2 = null;
        while (iterator.hasNext()) {
            final MediaDevice mediaDevice3 = iterator.next();
            if (mediaDevice3 instanceof BluetoothMediaDevice) {
                if (this.isActiveDevice(((BluetoothMediaDevice)mediaDevice3).getCachedDevice())) {
                    return mediaDevice3;
                }
                continue;
            }
            else {
                if (!(mediaDevice3 instanceof PhoneMediaDevice)) {
                    continue;
                }
                mediaDevice2 = mediaDevice3;
            }
        }
        if (this.mMediaDevices.contains(mediaDevice2)) {
            mediaDevice = mediaDevice2;
        }
        return mediaDevice;
    }
    
    public void connectDevice(MediaDevice mCurrentConnectedDevice) {
        final MediaDevice mediaDeviceById = this.getMediaDeviceById(this.mMediaDevices, mCurrentConnectedDevice.getId());
        if (mediaDeviceById instanceof BluetoothMediaDevice) {
            final CachedBluetoothDevice cachedDevice = ((BluetoothMediaDevice)mediaDeviceById).getCachedDevice();
            if (!cachedDevice.isConnected() && !cachedDevice.isBusy()) {
                this.mOnTransferBluetoothDevice = mCurrentConnectedDevice;
                mediaDeviceById.setState(1);
                cachedDevice.connect();
                return;
            }
        }
        mCurrentConnectedDevice = this.mCurrentConnectedDevice;
        if (mediaDeviceById == mCurrentConnectedDevice) {
            final StringBuilder sb = new StringBuilder();
            sb.append("connectDevice() this device all ready connected! : ");
            sb.append(mediaDeviceById.getName());
            Log.d("LocalMediaManager", sb.toString());
            return;
        }
        if (mCurrentConnectedDevice != null) {
            mCurrentConnectedDevice.disconnect();
        }
        mediaDeviceById.setState(1);
        if (TextUtils.isEmpty((CharSequence)this.mPackageName)) {
            this.mInfoMediaManager.connectDeviceWithoutPackageName(mediaDeviceById);
        }
        else {
            mediaDeviceById.connect();
        }
    }
    
    void dispatchDeviceAttributesChanged() {
        final Iterator<DeviceCallback> iterator = this.getCallbacks().iterator();
        while (iterator.hasNext()) {
            iterator.next().onDeviceAttributesChanged();
        }
    }
    
    void dispatchDeviceListUpdate() {
        final Iterator<DeviceCallback> iterator = this.getCallbacks().iterator();
        while (iterator.hasNext()) {
            iterator.next().onDeviceListUpdate(new ArrayList<MediaDevice>(this.mMediaDevices));
        }
    }
    
    void dispatchOnRequestFailed(final int n) {
        final Iterator<DeviceCallback> iterator = this.getCallbacks().iterator();
        while (iterator.hasNext()) {
            iterator.next().onRequestFailed(n);
        }
    }
    
    void dispatchSelectedDeviceStateChanged(final MediaDevice mediaDevice, final int n) {
        final Iterator<DeviceCallback> iterator = this.getCallbacks().iterator();
        while (iterator.hasNext()) {
            iterator.next().onSelectedDeviceStateChanged(mediaDevice, n);
        }
    }
    
    public MediaDevice getCurrentConnectedDevice() {
        return this.mCurrentConnectedDevice;
    }
    
    public MediaDevice getMediaDeviceById(final List<MediaDevice> list, final String s) {
        for (final MediaDevice mediaDevice : list) {
            if (TextUtils.equals((CharSequence)mediaDevice.getId(), (CharSequence)s)) {
                return mediaDevice;
            }
        }
        Log.i("LocalMediaManager", "getMediaDeviceById() can't found device");
        return null;
    }
    
    public void registerCallback(final DeviceCallback deviceCallback) {
        this.mCallbacks.add(deviceCallback);
    }
    
    public void startScan() {
        this.mMediaDevices.clear();
        this.mInfoMediaManager.registerCallback((MediaManager.MediaDeviceCallback)this.mMediaDeviceCallback);
        this.mInfoMediaManager.startScan();
    }
    
    public void stopScan() {
        this.mInfoMediaManager.unregisterCallback((MediaManager.MediaDeviceCallback)this.mMediaDeviceCallback);
        this.mInfoMediaManager.stopScan();
        this.unRegisterDeviceAttributeChangeCallback();
    }
    
    public void unregisterCallback(final DeviceCallback deviceCallback) {
        this.mCallbacks.remove(deviceCallback);
    }
    
    @VisibleForTesting
    class DeviceAttributeChangeCallback implements Callback
    {
        @Override
        public void onDeviceAttributesChanged() {
            if (LocalMediaManager.this.mOnTransferBluetoothDevice != null && !((BluetoothMediaDevice)LocalMediaManager.this.mOnTransferBluetoothDevice).getCachedDevice().isBusy() && !LocalMediaManager.this.mOnTransferBluetoothDevice.isConnected()) {
                LocalMediaManager.this.mOnTransferBluetoothDevice.setState(2);
                LocalMediaManager.this.mOnTransferBluetoothDevice = null;
            }
            LocalMediaManager.this.dispatchDeviceAttributesChanged();
        }
    }
    
    public interface DeviceCallback
    {
        default void onDeviceAttributesChanged() {
        }
        
        default void onDeviceListUpdate(final List<MediaDevice> list) {
        }
        
        default void onRequestFailed(final int n) {
        }
        
        default void onSelectedDeviceStateChanged(final MediaDevice mediaDevice, final int n) {
        }
    }
    
    class MediaDeviceCallback implements MediaManager.MediaDeviceCallback
    {
        private List<MediaDevice> buildDisconnectedBluetoothDevice() {
            final BluetoothAdapter mBluetoothAdapter = LocalMediaManager.this.mBluetoothAdapter;
            if (mBluetoothAdapter == null) {
                Log.w("LocalMediaManager", "buildDisconnectedBluetoothDevice() BluetoothAdapter is null");
                return new ArrayList<MediaDevice>();
            }
            final List mostRecentlyConnectedDevices = mBluetoothAdapter.getMostRecentlyConnectedDevices();
            final CachedBluetoothDeviceManager cachedDeviceManager = LocalMediaManager.this.mLocalBluetoothManager.getCachedDeviceManager();
            final ArrayList<CachedBluetoothDevice> list = new ArrayList<CachedBluetoothDevice>();
            int n = 0;
            final Iterator<BluetoothDevice> iterator = mostRecentlyConnectedDevices.iterator();
            while (iterator.hasNext()) {
                final CachedBluetoothDevice device = cachedDeviceManager.findDevice(iterator.next());
                if (device != null && device.getBondState() == 12 && !device.isConnected()) {
                    final int n2 = n + 1;
                    list.add(device);
                    if ((n = n2) >= 5) {
                        break;
                    }
                    continue;
                }
            }
            LocalMediaManager.this.unRegisterDeviceAttributeChangeCallback();
            LocalMediaManager.this.mDisconnectedMediaDevices.clear();
            for (final CachedBluetoothDevice cachedBluetoothDevice : list) {
                final BluetoothMediaDevice bluetoothMediaDevice = new BluetoothMediaDevice(LocalMediaManager.this.mContext, cachedBluetoothDevice, null, null, LocalMediaManager.this.mPackageName);
                if (!LocalMediaManager.this.mMediaDevices.contains(bluetoothMediaDevice)) {
                    cachedBluetoothDevice.registerCallback((CachedBluetoothDevice.Callback)LocalMediaManager.this.mDeviceAttributeChangeCallback);
                    LocalMediaManager.this.mDisconnectedMediaDevices.add(bluetoothMediaDevice);
                }
            }
            return new ArrayList<MediaDevice>(LocalMediaManager.this.mDisconnectedMediaDevices);
        }
        
        @Override
        public void onConnectedDeviceChanged(final String s) {
            final LocalMediaManager this$0 = LocalMediaManager.this;
            MediaDevice mCurrentConnectedDevice = this$0.getMediaDeviceById(this$0.mMediaDevices, s);
            if (mCurrentConnectedDevice == null) {
                mCurrentConnectedDevice = LocalMediaManager.this.updateCurrentConnectedDevice();
            }
            if (mCurrentConnectedDevice != null) {
                mCurrentConnectedDevice.setState(0);
            }
            final LocalMediaManager this$2 = LocalMediaManager.this;
            if (mCurrentConnectedDevice == this$2.mCurrentConnectedDevice) {
                Log.d("LocalMediaManager", "onConnectedDeviceChanged() this device all ready connected!");
                return;
            }
            this$2.dispatchSelectedDeviceStateChanged(this$2.mCurrentConnectedDevice = mCurrentConnectedDevice, 0);
        }
        
        @Override
        public void onDeviceListAdded(final List<MediaDevice> list) {
            LocalMediaManager.this.mMediaDevices.clear();
            LocalMediaManager.this.mMediaDevices.addAll(list);
            LocalMediaManager.this.mMediaDevices.addAll(this.buildDisconnectedBluetoothDevice());
            MediaDevice mCurrentConnectedDevice = LocalMediaManager.this.mInfoMediaManager.getCurrentConnectedDevice();
            final LocalMediaManager this$0 = LocalMediaManager.this;
            if (mCurrentConnectedDevice == null) {
                mCurrentConnectedDevice = this$0.updateCurrentConnectedDevice();
            }
            this$0.mCurrentConnectedDevice = mCurrentConnectedDevice;
            LocalMediaManager.this.dispatchDeviceListUpdate();
            if (LocalMediaManager.this.mOnTransferBluetoothDevice != null && LocalMediaManager.this.mOnTransferBluetoothDevice.isConnected()) {
                final LocalMediaManager this$2 = LocalMediaManager.this;
                this$2.connectDevice(this$2.mOnTransferBluetoothDevice);
                LocalMediaManager.this.mOnTransferBluetoothDevice.setState(0);
                LocalMediaManager.this.mOnTransferBluetoothDevice = null;
            }
        }
        
        @Override
        public void onRequestFailed(final int n) {
            for (final MediaDevice mediaDevice : LocalMediaManager.this.mMediaDevices) {
                if (mediaDevice.getState() == 1) {
                    mediaDevice.setState(3);
                }
            }
            LocalMediaManager.this.dispatchOnRequestFailed(n);
        }
    }
}
