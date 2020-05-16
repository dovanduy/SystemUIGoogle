// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.Message;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.UserHandle;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import java.util.Iterator;
import android.app.ActivityManager;
import java.util.ArrayList;
import android.os.Looper;
import android.content.Context;
import android.util.Log;
import android.os.UserManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.List;
import java.util.WeakHashMap;
import android.os.Handler;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.BluetoothCallback;

public class BluetoothControllerImpl implements BluetoothController, BluetoothCallback, CachedBluetoothDevice.Callback, ServiceListener
{
    private static final boolean DEBUG;
    private boolean mAudioProfileOnly;
    private final Handler mBgHandler;
    private final WeakHashMap<CachedBluetoothDevice, ActuallyCachedState> mCachedState;
    private final List<CachedBluetoothDevice> mConnectedDevices;
    private int mConnectionState;
    private final int mCurrentUser;
    private boolean mEnabled;
    private final H mHandler;
    private boolean mIsActive;
    private final LocalBluetoothManager mLocalBluetoothManager;
    private int mState;
    private final UserManager mUserManager;
    
    static {
        DEBUG = Log.isLoggable("BluetoothController", 3);
    }
    
    public BluetoothControllerImpl(final Context context, final Looper looper, final Looper looper2, final LocalBluetoothManager mLocalBluetoothManager) {
        this.mCachedState = new WeakHashMap<CachedBluetoothDevice, ActuallyCachedState>();
        this.mConnectedDevices = new ArrayList<CachedBluetoothDevice>();
        this.mConnectionState = 0;
        this.mLocalBluetoothManager = mLocalBluetoothManager;
        this.mBgHandler = new Handler(looper);
        this.mHandler = new H(looper2);
        final LocalBluetoothManager mLocalBluetoothManager2 = this.mLocalBluetoothManager;
        if (mLocalBluetoothManager2 != null) {
            mLocalBluetoothManager2.getEventManager().registerCallback(this);
            this.mLocalBluetoothManager.getProfileManager().addServiceListener((LocalBluetoothProfileManager.ServiceListener)this);
            this.onBluetoothStateChanged(this.mLocalBluetoothManager.getBluetoothAdapter().getBluetoothState());
        }
        this.mUserManager = (UserManager)context.getSystemService("user");
        this.mCurrentUser = ActivityManager.getCurrentUser();
    }
    
    private ActuallyCachedState getCachedState(final CachedBluetoothDevice cachedBluetoothDevice) {
        ActuallyCachedState value;
        if ((value = this.mCachedState.get(cachedBluetoothDevice)) == null) {
            value = new ActuallyCachedState(cachedBluetoothDevice, (Handler)this.mHandler);
            this.mBgHandler.post((Runnable)value);
            this.mCachedState.put(cachedBluetoothDevice, value);
        }
        return value;
    }
    
    private String getDeviceString(final CachedBluetoothDevice cachedBluetoothDevice) {
        final StringBuilder sb = new StringBuilder();
        sb.append(cachedBluetoothDevice.getName());
        sb.append(" ");
        sb.append(cachedBluetoothDevice.getBondState());
        sb.append(" ");
        sb.append(cachedBluetoothDevice.isConnected());
        return sb.toString();
    }
    
    private static String stateToString(final int i) {
        if (i == 0) {
            return "DISCONNECTED";
        }
        if (i == 1) {
            return "CONNECTING";
        }
        if (i == 2) {
            return "CONNECTED";
        }
        if (i != 3) {
            final StringBuilder sb = new StringBuilder();
            sb.append("UNKNOWN(");
            sb.append(i);
            sb.append(")");
            return sb.toString();
        }
        return "DISCONNECTING";
    }
    
    private void updateActive() {
        final Iterator<CachedBluetoothDevice> iterator = this.getDevices().iterator();
        boolean mIsActive = false;
        while (iterator.hasNext()) {
            final CachedBluetoothDevice cachedBluetoothDevice = iterator.next();
            boolean b2;
            final boolean b = b2 = true;
            if (!cachedBluetoothDevice.isActiveDevice(1)) {
                b2 = b;
                if (!cachedBluetoothDevice.isActiveDevice(2)) {
                    b2 = (cachedBluetoothDevice.isActiveDevice(21) && b);
                }
            }
            mIsActive |= b2;
        }
        if (this.mIsActive != mIsActive) {
            this.mIsActive = mIsActive;
            this.mHandler.sendEmptyMessage(2);
        }
    }
    
    private void updateAudioProfile() {
        final Iterator<CachedBluetoothDevice> iterator = this.getDevices().iterator();
        final boolean b = false;
        int n2;
        int n = n2 = 0;
        while (iterator.hasNext()) {
            final CachedBluetoothDevice cachedBluetoothDevice = iterator.next();
            final Iterator<LocalBluetoothProfile> iterator2 = cachedBluetoothDevice.getProfiles().iterator();
            int n3 = n2;
            int n4 = n;
            while (true) {
                n = n4;
                n2 = n3;
                if (!iterator2.hasNext()) {
                    break;
                }
                final LocalBluetoothProfile localBluetoothProfile = iterator2.next();
                final int profileId = localBluetoothProfile.getProfileId();
                final boolean connectedProfile = cachedBluetoothDevice.isConnectedProfile(localBluetoothProfile);
                if (profileId != 1 && profileId != 2 && profileId != 21) {
                    n3 |= (connectedProfile ? 1 : 0);
                }
                else {
                    n4 |= (connectedProfile ? 1 : 0);
                }
            }
        }
        boolean mAudioProfileOnly = b;
        if (n != 0) {
            mAudioProfileOnly = b;
            if (n2 == 0) {
                mAudioProfileOnly = true;
            }
        }
        if (mAudioProfileOnly != this.mAudioProfileOnly) {
            this.mAudioProfileOnly = mAudioProfileOnly;
            this.mHandler.sendEmptyMessage(2);
        }
    }
    
    private void updateConnected() {
        int connectionState = this.mLocalBluetoothManager.getBluetoothAdapter().getConnectionState();
        this.mConnectedDevices.clear();
        for (final CachedBluetoothDevice cachedBluetoothDevice : this.getDevices()) {
            final int maxConnectionState = cachedBluetoothDevice.getMaxConnectionState();
            int n;
            if (maxConnectionState > (n = connectionState)) {
                n = maxConnectionState;
            }
            connectionState = n;
            if (cachedBluetoothDevice.isConnected()) {
                this.mConnectedDevices.add(cachedBluetoothDevice);
                connectionState = n;
            }
        }
        int mConnectionState = connectionState;
        if (this.mConnectedDevices.isEmpty() && (mConnectionState = connectionState) == 2) {
            mConnectionState = 0;
        }
        if (mConnectionState != this.mConnectionState) {
            this.mConnectionState = mConnectionState;
            this.mHandler.sendEmptyMessage(2);
        }
        this.updateAudioProfile();
    }
    
    @Override
    public void addCallback(final BluetoothController.Callback callback) {
        this.mHandler.obtainMessage(3, (Object)callback).sendToTarget();
        this.mHandler.sendEmptyMessage(2);
    }
    
    @Override
    public boolean canConfigBluetooth() {
        return !this.mUserManager.hasUserRestriction("no_config_bluetooth", UserHandle.of(this.mCurrentUser)) && !this.mUserManager.hasUserRestriction("no_bluetooth", UserHandle.of(this.mCurrentUser));
    }
    
    @Override
    public void connect(final CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mLocalBluetoothManager != null) {
            if (cachedBluetoothDevice != null) {
                cachedBluetoothDevice.connect(true);
            }
        }
    }
    
    @Override
    public void disconnect(final CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mLocalBluetoothManager != null) {
            if (cachedBluetoothDevice != null) {
                cachedBluetoothDevice.disconnect();
            }
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("BluetoothController state:");
        printWriter.print("  mLocalBluetoothManager=");
        printWriter.println(this.mLocalBluetoothManager);
        if (this.mLocalBluetoothManager == null) {
            return;
        }
        printWriter.print("  mEnabled=");
        printWriter.println(this.mEnabled);
        printWriter.print("  mConnectionState=");
        printWriter.println(stateToString(this.mConnectionState));
        printWriter.print("  mAudioProfileOnly=");
        printWriter.println(this.mAudioProfileOnly);
        printWriter.print("  mIsActive=");
        printWriter.println(this.mIsActive);
        printWriter.print("  mConnectedDevices=");
        printWriter.println(this.mConnectedDevices);
        printWriter.print("  mCallbacks.size=");
        printWriter.println(this.mHandler.mCallbacks.size());
        printWriter.println("  Bluetooth Devices:");
        for (final CachedBluetoothDevice cachedBluetoothDevice : this.getDevices()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("    ");
            sb.append(this.getDeviceString(cachedBluetoothDevice));
            printWriter.println(sb.toString());
        }
    }
    
    @Override
    public int getBluetoothState() {
        return this.mState;
    }
    
    @Override
    public int getBondState(final CachedBluetoothDevice cachedBluetoothDevice) {
        return this.getCachedState(cachedBluetoothDevice).mBondState;
    }
    
    @Override
    public String getConnectedDeviceName() {
        if (this.mConnectedDevices.size() == 1) {
            return this.mConnectedDevices.get(0).getName();
        }
        return null;
    }
    
    @Override
    public List<CachedBluetoothDevice> getConnectedDevices() {
        return this.mConnectedDevices;
    }
    
    @Override
    public Collection<CachedBluetoothDevice> getDevices() {
        final LocalBluetoothManager mLocalBluetoothManager = this.mLocalBluetoothManager;
        Collection<CachedBluetoothDevice> cachedDevicesCopy;
        if (mLocalBluetoothManager != null) {
            cachedDevicesCopy = mLocalBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy();
        }
        else {
            cachedDevicesCopy = null;
        }
        return cachedDevicesCopy;
    }
    
    @Override
    public boolean isBluetoothAudioActive() {
        return this.mIsActive;
    }
    
    @Override
    public boolean isBluetoothAudioProfileOnly() {
        return this.mAudioProfileOnly;
    }
    
    @Override
    public boolean isBluetoothConnected() {
        return this.mConnectionState == 2;
    }
    
    @Override
    public boolean isBluetoothConnecting() {
        final int mConnectionState = this.mConnectionState;
        boolean b = true;
        if (mConnectionState != 1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public boolean isBluetoothEnabled() {
        return this.mEnabled;
    }
    
    @Override
    public boolean isBluetoothSupported() {
        return this.mLocalBluetoothManager != null;
    }
    
    @Override
    public void onAclConnectionStateChanged(final CachedBluetoothDevice key, final int n) {
        if (BluetoothControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("ACLConnectionStateChanged=");
            sb.append(key.getAddress());
            sb.append(" ");
            sb.append(stateToString(n));
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(key);
        this.updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }
    
    @Override
    public void onActiveDeviceChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int i) {
        if (BluetoothControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("ActiveDeviceChanged=");
            sb.append(cachedBluetoothDevice.getAddress());
            sb.append(" profileId=");
            sb.append(i);
            Log.d("BluetoothController", sb.toString());
        }
        this.updateActive();
        this.mHandler.sendEmptyMessage(2);
    }
    
    @Override
    public void onBluetoothStateChanged(final int mState) {
        if (BluetoothControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("BluetoothStateChanged=");
            sb.append(stateToString(mState));
            Log.d("BluetoothController", sb.toString());
        }
        this.mEnabled = (mState == 12 || mState == 11);
        this.mState = mState;
        this.updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }
    
    @Override
    public void onConnectionStateChanged(final CachedBluetoothDevice key, final int n) {
        if (BluetoothControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("ConnectionStateChanged=");
            sb.append(key.getAddress());
            sb.append(" ");
            sb.append(stateToString(n));
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(key);
        this.updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }
    
    @Override
    public void onDeviceAdded(final CachedBluetoothDevice cachedBluetoothDevice) {
        if (BluetoothControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("DeviceAdded=");
            sb.append(cachedBluetoothDevice.getAddress());
            Log.d("BluetoothController", sb.toString());
        }
        cachedBluetoothDevice.registerCallback((CachedBluetoothDevice.Callback)this);
        this.updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }
    
    @Override
    public void onDeviceAttributesChanged() {
        if (BluetoothControllerImpl.DEBUG) {
            Log.d("BluetoothController", "DeviceAttributesChanged");
        }
        this.updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }
    
    @Override
    public void onDeviceBondStateChanged(final CachedBluetoothDevice key, final int n) {
        if (BluetoothControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("DeviceBondStateChanged=");
            sb.append(key.getAddress());
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(key);
        this.updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }
    
    @Override
    public void onDeviceDeleted(final CachedBluetoothDevice key) {
        if (BluetoothControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("DeviceDeleted=");
            sb.append(key.getAddress());
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(key);
        this.updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }
    
    @Override
    public void onProfileConnectionStateChanged(final CachedBluetoothDevice key, final int n, final int i) {
        if (BluetoothControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("ProfileConnectionStateChanged=");
            sb.append(key.getAddress());
            sb.append(" ");
            sb.append(stateToString(n));
            sb.append(" profileId=");
            sb.append(i);
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(key);
        this.updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }
    
    @Override
    public void onServiceConnected() {
        this.updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }
    
    @Override
    public void onServiceDisconnected() {
    }
    
    @Override
    public void removeCallback(final BluetoothController.Callback callback) {
        this.mHandler.obtainMessage(4, (Object)callback).sendToTarget();
    }
    
    @Override
    public void setBluetoothEnabled(final boolean bluetoothEnabled) {
        final LocalBluetoothManager mLocalBluetoothManager = this.mLocalBluetoothManager;
        if (mLocalBluetoothManager != null) {
            mLocalBluetoothManager.getBluetoothAdapter().setBluetoothEnabled(bluetoothEnabled);
        }
    }
    
    private static class ActuallyCachedState implements Runnable
    {
        private int mBondState;
        private final WeakReference<CachedBluetoothDevice> mDevice;
        private final Handler mUiHandler;
        
        private ActuallyCachedState(final CachedBluetoothDevice referent, final Handler mUiHandler) {
            this.mBondState = 10;
            this.mDevice = new WeakReference<CachedBluetoothDevice>(referent);
            this.mUiHandler = mUiHandler;
        }
        
        @Override
        public void run() {
            final CachedBluetoothDevice cachedBluetoothDevice = this.mDevice.get();
            if (cachedBluetoothDevice != null) {
                this.mBondState = cachedBluetoothDevice.getBondState();
                cachedBluetoothDevice.getMaxConnectionState();
                this.mUiHandler.removeMessages(1);
                this.mUiHandler.sendEmptyMessage(1);
            }
        }
    }
    
    private final class H extends Handler
    {
        private final ArrayList<BluetoothController.Callback> mCallbacks;
        
        public H(final Looper looper) {
            super(looper);
            this.mCallbacks = new ArrayList<BluetoothController.Callback>();
        }
        
        private void firePairedDevicesChanged() {
            final Iterator<BluetoothController.Callback> iterator = this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onBluetoothDevicesChanged();
            }
        }
        
        private void fireStateChange() {
            final Iterator<BluetoothController.Callback> iterator = this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                this.fireStateChange(iterator.next());
            }
        }
        
        private void fireStateChange(final BluetoothController.Callback callback) {
            callback.onBluetoothStateChange(BluetoothControllerImpl.this.mEnabled);
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 1) {
                if (what != 2) {
                    if (what != 3) {
                        if (what == 4) {
                            this.mCallbacks.remove(message.obj);
                        }
                    }
                    else {
                        this.mCallbacks.add((BluetoothController.Callback)message.obj);
                    }
                }
                else {
                    this.fireStateChange();
                }
            }
            else {
                this.firePairedDevicesChanged();
            }
        }
    }
}
