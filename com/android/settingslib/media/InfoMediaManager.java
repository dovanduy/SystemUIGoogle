// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import android.media.MediaRouter2Manager$Callback;
import java.util.List;
import android.media.RoutingSessionInfo;
import java.util.Iterator;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.media.MediaRoute2Info;
import android.text.TextUtils;
import java.util.concurrent.Executors;
import android.app.Notification;
import android.content.Context;
import android.media.MediaRouter2Manager;
import com.android.internal.annotations.VisibleForTesting;
import java.util.concurrent.Executor;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class InfoMediaManager extends MediaManager
{
    private LocalBluetoothManager mBluetoothManager;
    private MediaDevice mCurrentConnectedDevice;
    @VisibleForTesting
    final Executor mExecutor;
    @VisibleForTesting
    final RouterManagerCallback mMediaRouterCallback;
    @VisibleForTesting
    String mPackageName;
    @VisibleForTesting
    MediaRouter2Manager mRouterManager;
    
    public InfoMediaManager(final Context context, final String mPackageName, final Notification notification, final LocalBluetoothManager mBluetoothManager) {
        super(context, notification);
        this.mMediaRouterCallback = new RouterManagerCallback();
        this.mExecutor = Executors.newSingleThreadExecutor();
        this.mRouterManager = MediaRouter2Manager.getInstance(context);
        this.mBluetoothManager = mBluetoothManager;
        if (!TextUtils.isEmpty((CharSequence)mPackageName)) {
            this.mPackageName = mPackageName;
        }
    }
    
    private void addMediaDevice(final MediaRoute2Info mediaRoute2Info) {
        final int type = mediaRoute2Info.getType();
        MediaDevice mediaDevice = null;
        Label_0241: {
            if (type != 0) {
                if (type == 8 || type == 23) {
                    mediaDevice = new BluetoothMediaDevice(super.mContext, this.mBluetoothManager.getCachedDeviceManager().findDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mediaRoute2Info.getOriginalId())), this.mRouterManager, mediaRoute2Info, this.mPackageName);
                    break Label_0241;
                }
                if (type != 2000) {
                    if (type == 2 || type == 3 || type == 4) {
                        mediaDevice = new PhoneMediaDevice(super.mContext, this.mRouterManager, mediaRoute2Info, this.mPackageName);
                        break Label_0241;
                    }
                    if (type != 1001 && type != 1002) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("addMediaDevice() unknown device type : ");
                        sb.append(type);
                        Log.w("InfoMediaManager", sb.toString());
                        mediaDevice = null;
                        break Label_0241;
                    }
                }
            }
            final InfoMediaDevice mCurrentConnectedDevice = (InfoMediaDevice)(mediaDevice = new InfoMediaDevice(super.mContext, this.mRouterManager, mediaRoute2Info, this.mPackageName));
            if (!TextUtils.isEmpty((CharSequence)this.mPackageName)) {
                mediaDevice = mCurrentConnectedDevice;
                if (this.getRoutingSessionInfo().getSelectedRoutes().contains(mediaRoute2Info.getId())) {
                    mediaDevice = mCurrentConnectedDevice;
                    if (this.mCurrentConnectedDevice == null) {
                        this.mCurrentConnectedDevice = mCurrentConnectedDevice;
                        mediaDevice = mCurrentConnectedDevice;
                    }
                }
            }
        }
        if (mediaDevice != null) {
            super.mMediaDevices.add(mediaDevice);
        }
    }
    
    private void buildAllRoutes() {
        for (final MediaRoute2Info mediaRoute2Info : this.mRouterManager.getAllRoutes()) {
            if (mediaRoute2Info.isSystemRoute()) {
                this.addMediaDevice(mediaRoute2Info);
            }
        }
    }
    
    private void buildAvailableRoutes() {
        final Iterator<MediaRoute2Info> iterator = this.mRouterManager.getAvailableRoutes(this.mPackageName).iterator();
        while (iterator.hasNext()) {
            this.addMediaDevice(iterator.next());
        }
    }
    
    private RoutingSessionInfo getRoutingSessionInfo() {
        final List routingSessions = this.mRouterManager.getRoutingSessions(this.mPackageName);
        return routingSessions.get(routingSessions.size() - 1);
    }
    
    private void refreshDevices() {
        super.mMediaDevices.clear();
        this.mCurrentConnectedDevice = null;
        if (TextUtils.isEmpty((CharSequence)this.mPackageName)) {
            this.buildAllRoutes();
        }
        else {
            this.buildAvailableRoutes();
        }
        this.dispatchDeviceListAdded();
    }
    
    boolean connectDeviceWithoutPackageName(final MediaDevice mediaDevice) {
        final List activeSessions = this.mRouterManager.getActiveSessions();
        final int size = activeSessions.size();
        boolean b = false;
        if (size > 0) {
            this.mRouterManager.getControllerForSession((RoutingSessionInfo)activeSessions.get(0)).transferToRoute(mediaDevice.mRouteInfo);
            b = true;
        }
        return b;
    }
    
    MediaDevice getCurrentConnectedDevice() {
        return this.mCurrentConnectedDevice;
    }
    
    public void startScan() {
        super.mMediaDevices.clear();
        this.mRouterManager.registerCallback(this.mExecutor, (MediaRouter2Manager$Callback)this.mMediaRouterCallback);
        this.refreshDevices();
    }
    
    public void stopScan() {
        this.mRouterManager.unregisterCallback((MediaRouter2Manager$Callback)this.mMediaRouterCallback);
    }
    
    class RouterManagerCallback extends MediaRouter2Manager$Callback
    {
        public void onControlCategoriesChanged(final String s, final List<String> list) {
            if (TextUtils.equals((CharSequence)InfoMediaManager.this.mPackageName, (CharSequence)s)) {
                InfoMediaManager.this.refreshDevices();
            }
        }
        
        public void onRequestFailed(final int n) {
            InfoMediaManager.this.dispatchOnRequestFailed(n);
        }
        
        public void onRoutesAdded(final List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }
        
        public void onRoutesChanged(final List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }
        
        public void onRoutesRemoved(final List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }
        
        public void onTransferFailed(final RoutingSessionInfo routingSessionInfo, final MediaRoute2Info mediaRoute2Info) {
            InfoMediaManager.this.dispatchOnRequestFailed(0);
        }
        
        public void onTransferred(final RoutingSessionInfo routingSessionInfo, final RoutingSessionInfo routingSessionInfo2) {
            InfoMediaManager.this.mMediaDevices.clear();
            final InfoMediaManager this$0 = InfoMediaManager.this;
            String id = null;
            this$0.mCurrentConnectedDevice = null;
            if (TextUtils.isEmpty((CharSequence)InfoMediaManager.this.mPackageName)) {
                InfoMediaManager.this.buildAllRoutes();
            }
            else {
                InfoMediaManager.this.buildAvailableRoutes();
            }
            if (InfoMediaManager.this.mCurrentConnectedDevice != null) {
                id = InfoMediaManager.this.mCurrentConnectedDevice.getId();
            }
            InfoMediaManager.this.dispatchConnectedDeviceChanged(id);
        }
    }
}
