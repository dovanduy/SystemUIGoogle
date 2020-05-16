// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.Message;
import java.util.function.Consumer;
import com.android.settingslib.Utils;
import android.content.Intent;
import android.app.ActivityManager;
import android.location.LocationManager;
import java.util.List;
import android.app.AppOpsManager$OpEntry;
import android.app.AppOpsManager$PackageOps;
import android.os.UserManager;
import android.app.StatusBarManager;
import android.os.UserHandle;
import android.os.Handler;
import android.content.IntentFilter;
import android.os.Looper;
import java.util.ArrayList;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.BootCompleteCache;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;

public class LocationControllerImpl extends BroadcastReceiver implements LocationController
{
    private static final int[] mHighPowerRequestAppOpArray;
    private AppOpsManager mAppOpsManager;
    private boolean mAreActiveLocationRequests;
    private BootCompleteCache mBootCompleteCache;
    private BroadcastDispatcher mBroadcastDispatcher;
    private Context mContext;
    private final H mHandler;
    private ArrayList<LocationChangeCallback> mSettingsChangeCallbacks;
    
    static {
        mHighPowerRequestAppOpArray = new int[] { 42 };
    }
    
    public LocationControllerImpl(final Context mContext, final Looper looper, final BroadcastDispatcher mBroadcastDispatcher, final BootCompleteCache mBootCompleteCache) {
        this.mSettingsChangeCallbacks = new ArrayList<LocationChangeCallback>();
        this.mHandler = new H();
        this.mContext = mContext;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mBootCompleteCache = mBootCompleteCache;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.location.HIGH_POWER_REQUEST_CHANGE");
        intentFilter.addAction("android.location.MODE_CHANGED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, new Handler(looper), UserHandle.ALL);
        this.mAppOpsManager = (AppOpsManager)mContext.getSystemService("appops");
        final StatusBarManager statusBarManager = (StatusBarManager)mContext.getSystemService("statusbar");
        this.updateActiveLocationRequests();
    }
    
    private boolean isUserLocationRestricted(final int n) {
        return ((UserManager)this.mContext.getSystemService("user")).hasUserRestriction("no_share_location", UserHandle.of(n));
    }
    
    private void updateActiveLocationRequests() {
        final boolean mAreActiveLocationRequests = this.mAreActiveLocationRequests;
        final boolean activeHighPowerLocationRequests = this.areActiveHighPowerLocationRequests();
        this.mAreActiveLocationRequests = activeHighPowerLocationRequests;
        if (activeHighPowerLocationRequests != mAreActiveLocationRequests) {
            this.mHandler.sendEmptyMessage(2);
        }
    }
    
    public void addCallback(final LocationChangeCallback e) {
        this.mSettingsChangeCallbacks.add(e);
        this.mHandler.sendEmptyMessage(1);
    }
    
    protected boolean areActiveHighPowerLocationRequests() {
        final List packagesForOps = this.mAppOpsManager.getPackagesForOps(LocationControllerImpl.mHighPowerRequestAppOpArray);
        if (packagesForOps != null) {
            for (int size = packagesForOps.size(), i = 0; i < size; ++i) {
                final List ops = packagesForOps.get(i).getOps();
                if (ops != null) {
                    for (int size2 = ops.size(), j = 0; j < size2; ++j) {
                        final AppOpsManager$OpEntry appOpsManager$OpEntry = ops.get(j);
                        if (appOpsManager$OpEntry.getOp() == 42 && appOpsManager$OpEntry.isRunning()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isLocationActive() {
        return this.mAreActiveLocationRequests;
    }
    
    public boolean isLocationEnabled() {
        final LocationManager locationManager = (LocationManager)this.mContext.getSystemService("location");
        return this.mBootCompleteCache.isBootComplete() && locationManager.isLocationEnabledForUser(UserHandle.of(ActivityManager.getCurrentUser()));
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        if ("android.location.HIGH_POWER_REQUEST_CHANGE".equals(action)) {
            this.updateActiveLocationRequests();
        }
        else if ("android.location.MODE_CHANGED".equals(action)) {
            this.mHandler.sendEmptyMessage(1);
        }
    }
    
    public void removeCallback(final LocationChangeCallback o) {
        this.mSettingsChangeCallbacks.remove(o);
    }
    
    public boolean setLocationEnabled(final boolean b) {
        final int currentUser = ActivityManager.getCurrentUser();
        if (this.isUserLocationRestricted(currentUser)) {
            return false;
        }
        Utils.updateLocationEnabled(this.mContext, b, currentUser, 2);
        return true;
    }
    
    private final class H extends Handler
    {
        private void locationActiveChanged() {
            com.android.systemui.util.Utils.safeForeach(LocationControllerImpl.this.mSettingsChangeCallbacks, (Consumer<Object>)new _$$Lambda$LocationControllerImpl$H$vKTe7eMzgWgCJvXCt8UIIkFyg78(this));
        }
        
        private void locationSettingsChanged() {
            com.android.systemui.util.Utils.safeForeach(LocationControllerImpl.this.mSettingsChangeCallbacks, (Consumer<Object>)new _$$Lambda$LocationControllerImpl$H$xXVOboFsQOHoRY_EFzvZu_IOYh0(LocationControllerImpl.this.isLocationEnabled()));
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 1) {
                if (what == 2) {
                    this.locationActiveChanged();
                }
            }
            else {
                this.locationSettingsChanged();
            }
        }
    }
}
