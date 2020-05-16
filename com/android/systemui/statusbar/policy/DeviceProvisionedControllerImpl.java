// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.util.Log;
import android.provider.Settings$Secure;
import android.provider.Settings$Global;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import android.content.Context;
import android.database.ContentObserver;
import java.util.ArrayList;
import android.net.Uri;
import android.content.ContentResolver;
import com.android.systemui.settings.CurrentUserTracker;

public class DeviceProvisionedControllerImpl extends CurrentUserTracker implements DeviceProvisionedController
{
    protected static final String TAG = "DeviceProvisionedControllerImpl";
    private final ContentResolver mContentResolver;
    private final Uri mDeviceProvisionedUri;
    protected final ArrayList<DeviceProvisionedListener> mListeners;
    protected final ContentObserver mSettingsObserver;
    private final Uri mUserSetupUri;
    
    public DeviceProvisionedControllerImpl(final Context context, final Handler handler, final BroadcastDispatcher broadcastDispatcher) {
        super(broadcastDispatcher);
        this.mListeners = new ArrayList<DeviceProvisionedListener>();
        this.mContentResolver = context.getContentResolver();
        this.mDeviceProvisionedUri = Settings$Global.getUriFor("device_provisioned");
        this.mUserSetupUri = Settings$Secure.getUriFor("user_setup_complete");
        this.mSettingsObserver = new ContentObserver(handler) {
            public void onChange(final boolean b, final Uri obj, final int n) {
                final String tag = DeviceProvisionedControllerImpl.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("Setting change: ");
                sb.append(obj);
                Log.d(tag, sb.toString());
                if (DeviceProvisionedControllerImpl.this.mUserSetupUri.equals((Object)obj)) {
                    DeviceProvisionedControllerImpl.this.notifySetupChanged();
                }
                else {
                    DeviceProvisionedControllerImpl.this.notifyProvisionedChanged();
                }
            }
        };
    }
    
    private void notifyProvisionedChanged() {
        for (int i = this.mListeners.size() - 1; i >= 0; --i) {
            this.mListeners.get(i).onDeviceProvisionedChanged();
        }
    }
    
    private void notifySetupChanged() {
        for (int i = this.mListeners.size() - 1; i >= 0; --i) {
            this.mListeners.get(i).onUserSetupChanged();
        }
    }
    
    private void notifyUserChanged() {
        for (int i = this.mListeners.size() - 1; i >= 0; --i) {
            this.mListeners.get(i).onUserSwitched();
        }
    }
    
    @Override
    public void addCallback(final DeviceProvisionedListener e) {
        this.mListeners.add(e);
        if (this.mListeners.size() == 1) {
            this.startListening(this.getCurrentUser());
        }
        e.onUserSetupChanged();
        e.onDeviceProvisionedChanged();
    }
    
    @Override
    public int getCurrentUser() {
        return ActivityManager.getCurrentUser();
    }
    
    @Override
    public boolean isDeviceProvisioned() {
        final ContentResolver mContentResolver = this.mContentResolver;
        boolean b = false;
        if (Settings$Global.getInt(mContentResolver, "device_provisioned", 0) != 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean isUserSetup(final int n) {
        final ContentResolver mContentResolver = this.mContentResolver;
        boolean b = false;
        if (Settings$Secure.getIntForUser(mContentResolver, "user_setup_complete", 0, n) != 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public void onUserSwitched(final int n) {
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
        this.mContentResolver.registerContentObserver(this.mDeviceProvisionedUri, true, this.mSettingsObserver, 0);
        this.mContentResolver.registerContentObserver(this.mUserSetupUri, true, this.mSettingsObserver, n);
        this.notifyUserChanged();
    }
    
    @Override
    public void removeCallback(final DeviceProvisionedListener o) {
        this.mListeners.remove(o);
        if (this.mListeners.size() == 0) {
            this.stopListening();
        }
    }
    
    protected void startListening(final int n) {
        this.mContentResolver.registerContentObserver(this.mDeviceProvisionedUri, true, this.mSettingsObserver, 0);
        this.mContentResolver.registerContentObserver(this.mUserSetupUri, true, this.mSettingsObserver, n);
        this.startTracking();
    }
    
    protected void stopListening() {
        this.stopTracking();
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
    }
}
