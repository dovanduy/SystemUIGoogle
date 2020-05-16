// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.io.PrintWriter;
import android.os.UserHandle;
import android.content.Intent;
import java.util.Iterator;
import com.android.settingslib.wifi.AccessPoint;
import java.util.List;
import android.app.ActivityManager;
import android.util.Log;
import android.os.UserManager;
import android.content.Context;
import android.net.wifi.WifiManager$ActionListener;
import java.util.ArrayList;
import com.android.settingslib.wifi.WifiTracker;

public class AccessPointControllerImpl implements AccessPointController, WifiListener
{
    private static final boolean DEBUG;
    private static final int[] ICONS;
    private final ArrayList<AccessPointCallback> mCallbacks;
    private final WifiManager$ActionListener mConnectListener;
    private final Context mContext;
    private int mCurrentUser;
    private final UserManager mUserManager;
    private final WifiTracker mWifiTracker;
    
    static {
        DEBUG = Log.isLoggable("AccessPointController", 3);
        ICONS = WifiIcons.WIFI_FULL_ICONS;
    }
    
    public AccessPointControllerImpl(final Context mContext) {
        this.mCallbacks = new ArrayList<AccessPointCallback>();
        this.mConnectListener = (WifiManager$ActionListener)new WifiManager$ActionListener() {
            public void onFailure(final int i) {
                if (AccessPointControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("connect failure reason=");
                    sb.append(i);
                    Log.d("AccessPointController", sb.toString());
                }
            }
            
            public void onSuccess() {
                if (AccessPointControllerImpl.DEBUG) {
                    Log.d("AccessPointController", "connect success");
                }
            }
        };
        this.mContext = mContext;
        this.mUserManager = (UserManager)mContext.getSystemService("user");
        this.mWifiTracker = new WifiTracker(mContext, (WifiTracker.WifiListener)this, false, true);
        this.mCurrentUser = ActivityManager.getCurrentUser();
    }
    
    private void fireAcccessPointsCallback(final List<AccessPoint> list) {
        final Iterator<AccessPointCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onAccessPointsChanged(list);
        }
    }
    
    private void fireSettingsIntentCallback(final Intent intent) {
        final Iterator<AccessPointCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onSettingsActivityTriggered(intent);
        }
    }
    
    @Override
    public void addAccessPointCallback(final AccessPointCallback e) {
        if (e != null) {
            if (!this.mCallbacks.contains(e)) {
                if (AccessPointControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("addCallback ");
                    sb.append(e);
                    Log.d("AccessPointController", sb.toString());
                }
                this.mCallbacks.add(e);
                if (this.mCallbacks.size() == 1) {
                    this.mWifiTracker.onStart();
                }
            }
        }
    }
    
    @Override
    public boolean canConfigWifi() {
        return this.mUserManager.hasUserRestriction("no_config_wifi", new UserHandle(this.mCurrentUser)) ^ true;
    }
    
    @Override
    public boolean connect(final AccessPoint accessPoint) {
        if (accessPoint == null) {
            return false;
        }
        if (AccessPointControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("connect networkId=");
            sb.append(accessPoint.getConfig().networkId);
            Log.d("AccessPointController", sb.toString());
        }
        if (accessPoint.isSaved()) {
            this.mWifiTracker.getManager().connect(accessPoint.getConfig().networkId, this.mConnectListener);
        }
        else {
            if (accessPoint.getSecurity() != 0) {
                final Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                intent.putExtra("wifi_start_connect_ssid", accessPoint.getSsidStr());
                intent.addFlags(268435456);
                this.fireSettingsIntentCallback(intent);
                return true;
            }
            accessPoint.generateOpenNetworkConfig();
            this.mWifiTracker.getManager().connect(accessPoint.getConfig(), this.mConnectListener);
        }
        return false;
    }
    
    public void dump(final PrintWriter printWriter) {
        this.mWifiTracker.dump(printWriter);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.mWifiTracker.onDestroy();
    }
    
    @Override
    public int getIcon(final AccessPoint accessPoint) {
        int level = accessPoint.getLevel();
        final int[] icons = AccessPointControllerImpl.ICONS;
        if (level < 0) {
            level = 0;
        }
        return icons[level];
    }
    
    @Override
    public void onAccessPointsChanged() {
        this.fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }
    
    @Override
    public void onConnectedChanged() {
        this.fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }
    
    public void onUserSwitched(final int mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }
    
    @Override
    public void onWifiStateChanged(final int n) {
    }
    
    @Override
    public void removeAccessPointCallback(final AccessPointCallback accessPointCallback) {
        if (accessPointCallback == null) {
            return;
        }
        if (AccessPointControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("removeCallback ");
            sb.append(accessPointCallback);
            Log.d("AccessPointController", sb.toString());
        }
        this.mCallbacks.remove(accessPointCallback);
        if (this.mCallbacks.isEmpty()) {
            this.mWifiTracker.onStop();
        }
    }
    
    @Override
    public void scanForAccessPoints() {
        this.fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }
}
