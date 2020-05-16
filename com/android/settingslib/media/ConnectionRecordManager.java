// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import android.content.SharedPreferences$Editor;
import android.content.SharedPreferences;
import android.content.Context;

public class ConnectionRecordManager
{
    private static ConnectionRecordManager sInstance;
    private static final Object sInstanceSync;
    private String mLastSelectedDevice;
    
    static {
        sInstanceSync = new Object();
    }
    
    public static ConnectionRecordManager getInstance() {
        synchronized (ConnectionRecordManager.sInstanceSync) {
            if (ConnectionRecordManager.sInstance == null) {
                ConnectionRecordManager.sInstance = new ConnectionRecordManager();
            }
            return ConnectionRecordManager.sInstance;
        }
    }
    
    private SharedPreferences getSharedPreferences(final Context context) {
        return context.getSharedPreferences("seamless_transfer_record", 0);
    }
    
    public int fetchConnectionRecord(final Context context, final String s) {
        synchronized (this) {
            return this.getSharedPreferences(context).getInt(s, 0);
        }
    }
    
    public void fetchLastSelectedDevice(final Context context) {
        synchronized (this) {
            this.mLastSelectedDevice = this.getSharedPreferences(context).getString("last_selected_device", (String)null);
        }
    }
    
    public String getLastSelectedDevice() {
        synchronized (this) {
            return this.mLastSelectedDevice;
        }
    }
    
    public void setConnectionRecord(final Context context, final String mLastSelectedDevice, final int n) {
        synchronized (this) {
            final SharedPreferences$Editor edit = this.getSharedPreferences(context).edit();
            edit.putInt(this.mLastSelectedDevice = mLastSelectedDevice, n);
            edit.putString("last_selected_device", this.mLastSelectedDevice);
            edit.apply();
        }
    }
}
