// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import android.os.UserHandle;
import android.os.Handler;
import android.content.Context;

public class LocalBluetoothManager
{
    private final CachedBluetoothDeviceManager mCachedDeviceManager;
    private final Context mContext;
    private final BluetoothEventManager mEventManager;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final LocalBluetoothProfileManager mProfileManager;
    
    private LocalBluetoothManager(final LocalBluetoothAdapter mLocalAdapter, Context applicationContext, final Handler handler, final UserHandle userHandle) {
        applicationContext = applicationContext.getApplicationContext();
        this.mContext = applicationContext;
        this.mLocalAdapter = mLocalAdapter;
        final CachedBluetoothDeviceManager mCachedDeviceManager = new CachedBluetoothDeviceManager(applicationContext, this);
        this.mCachedDeviceManager = mCachedDeviceManager;
        final BluetoothEventManager mEventManager = new BluetoothEventManager(this.mLocalAdapter, mCachedDeviceManager, this.mContext, handler, userHandle);
        this.mEventManager = mEventManager;
        (this.mProfileManager = new LocalBluetoothProfileManager(this.mContext, this.mLocalAdapter, this.mCachedDeviceManager, mEventManager)).updateLocalProfiles();
        this.mEventManager.readPairedDevices();
    }
    
    public static LocalBluetoothManager create(final Context context, final Handler handler, final UserHandle userHandle) {
        final LocalBluetoothAdapter instance = LocalBluetoothAdapter.getInstance();
        if (instance == null) {
            return null;
        }
        return new LocalBluetoothManager(instance, context, handler, userHandle);
    }
    
    public LocalBluetoothAdapter getBluetoothAdapter() {
        return this.mLocalAdapter;
    }
    
    public CachedBluetoothDeviceManager getCachedDeviceManager() {
        return this.mCachedDeviceManager;
    }
    
    public BluetoothEventManager getEventManager() {
        return this.mEventManager;
    }
    
    public LocalBluetoothProfileManager getProfileManager() {
        return this.mProfileManager;
    }
}
