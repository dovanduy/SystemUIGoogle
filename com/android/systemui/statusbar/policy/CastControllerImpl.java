// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.R$string;
import java.util.List;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Objects;
import android.media.MediaRouter$Callback;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.text.TextUtils;
import com.android.systemui.util.Utils;
import java.util.UUID;
import android.os.Handler;
import android.util.Log;
import android.media.MediaRouter$RouteInfo;
import android.util.ArrayMap;
import android.media.projection.MediaProjectionManager;
import android.media.projection.MediaProjectionManager$Callback;
import android.media.projection.MediaProjectionInfo;
import android.media.MediaRouter;
import android.media.MediaRouter$SimpleCallback;
import android.content.Context;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;

public class CastControllerImpl implements CastController
{
    private static final boolean DEBUG;
    private boolean mCallbackRegistered;
    @GuardedBy({ "mCallbacks" })
    private final ArrayList<Callback> mCallbacks;
    private final Context mContext;
    private boolean mDiscovering;
    private final Object mDiscoveringLock;
    private final MediaRouter$SimpleCallback mMediaCallback;
    private final MediaRouter mMediaRouter;
    private MediaProjectionInfo mProjection;
    private final MediaProjectionManager$Callback mProjectionCallback;
    private final Object mProjectionLock;
    private final MediaProjectionManager mProjectionManager;
    private final ArrayMap<String, MediaRouter$RouteInfo> mRoutes;
    
    static {
        DEBUG = Log.isLoggable("CastController", 3);
    }
    
    public CastControllerImpl(final Context mContext) {
        this.mCallbacks = new ArrayList<Callback>();
        this.mRoutes = (ArrayMap<String, MediaRouter$RouteInfo>)new ArrayMap();
        this.mDiscoveringLock = new Object();
        this.mProjectionLock = new Object();
        this.mMediaCallback = new MediaRouter$SimpleCallback() {
            public void onRouteAdded(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
                if (CastControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRouteAdded: ");
                    sb.append(routeToString(mediaRouter$RouteInfo));
                    Log.d("CastController", sb.toString());
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }
            
            public void onRouteChanged(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
                if (CastControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRouteChanged: ");
                    sb.append(routeToString(mediaRouter$RouteInfo));
                    Log.d("CastController", sb.toString());
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }
            
            public void onRouteRemoved(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
                if (CastControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRouteRemoved: ");
                    sb.append(routeToString(mediaRouter$RouteInfo));
                    Log.d("CastController", sb.toString());
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }
            
            public void onRouteSelected(final MediaRouter mediaRouter, final int i, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
                if (CastControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRouteSelected(");
                    sb.append(i);
                    sb.append("): ");
                    sb.append(routeToString(mediaRouter$RouteInfo));
                    Log.d("CastController", sb.toString());
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }
            
            public void onRouteUnselected(final MediaRouter mediaRouter, final int i, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
                if (CastControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRouteUnselected(");
                    sb.append(i);
                    sb.append("): ");
                    sb.append(routeToString(mediaRouter$RouteInfo));
                    Log.d("CastController", sb.toString());
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }
        };
        this.mProjectionCallback = new MediaProjectionManager$Callback() {
            public void onStart(final MediaProjectionInfo mediaProjectionInfo) {
                CastControllerImpl.this.setProjection(mediaProjectionInfo, true);
            }
            
            public void onStop(final MediaProjectionInfo mediaProjectionInfo) {
                CastControllerImpl.this.setProjection(mediaProjectionInfo, false);
            }
        };
        this.mContext = mContext;
        (this.mMediaRouter = (MediaRouter)mContext.getSystemService("media_router")).setRouterGroupId("android.media.mirroring_group");
        final MediaProjectionManager mProjectionManager = (MediaProjectionManager)mContext.getSystemService("media_projection");
        this.mProjectionManager = mProjectionManager;
        this.mProjection = mProjectionManager.getActiveProjectionInfo();
        this.mProjectionManager.addCallback(this.mProjectionCallback, new Handler());
        if (CastControllerImpl.DEBUG) {
            Log.d("CastController", "new CastController()");
        }
    }
    
    private void ensureTagExists(final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
        if (mediaRouter$RouteInfo.getTag() == null) {
            mediaRouter$RouteInfo.setTag((Object)UUID.randomUUID().toString());
        }
    }
    
    private void fireOnCastDevicesChanged(final Callback callback) {
        callback.onCastDevicesChanged();
    }
    
    private String getAppName(final String s) {
        final PackageManager packageManager = this.mContext.getPackageManager();
        if (Utils.isHeadlessRemoteDisplayProvider(packageManager, s)) {
            return "";
        }
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(s, 0);
            if (applicationInfo != null) {
                final CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                if (!TextUtils.isEmpty(loadLabel)) {
                    return loadLabel.toString();
                }
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("No label found for package: ");
            sb.append(s);
            Log.w("CastController", sb.toString());
        }
        catch (PackageManager$NameNotFoundException ex) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Error getting appName for package: ");
            sb2.append(s);
            Log.w("CastController", sb2.toString(), (Throwable)ex);
        }
        return s;
    }
    
    private void handleDiscoveryChangeLocked() {
        if (this.mCallbackRegistered) {
            this.mMediaRouter.removeCallback((MediaRouter$Callback)this.mMediaCallback);
            this.mCallbackRegistered = false;
        }
        if (this.mDiscovering) {
            this.mMediaRouter.addCallback(4, (MediaRouter$Callback)this.mMediaCallback, 4);
            this.mCallbackRegistered = true;
            return;
        }
        synchronized (this.mCallbacks) {
            final boolean empty = this.mCallbacks.isEmpty();
            // monitorexit(this.mCallbacks)
            if (!empty) {
                this.mMediaRouter.addCallback(4, (MediaRouter$Callback)this.mMediaCallback, 8);
                this.mCallbackRegistered = true;
            }
        }
    }
    
    private static String routeToString(final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
        if (mediaRouter$RouteInfo == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(mediaRouter$RouteInfo.getName());
        sb.append('/');
        sb.append(mediaRouter$RouteInfo.getDescription());
        sb.append('@');
        sb.append(mediaRouter$RouteInfo.getDeviceAddress());
        sb.append(",status=");
        sb.append(mediaRouter$RouteInfo.getStatus());
        if (mediaRouter$RouteInfo.isDefault()) {
            sb.append(",default");
        }
        if (mediaRouter$RouteInfo.isEnabled()) {
            sb.append(",enabled");
        }
        if (mediaRouter$RouteInfo.isConnecting()) {
            sb.append(",connecting");
        }
        if (mediaRouter$RouteInfo.isSelected()) {
            sb.append(",selected");
        }
        sb.append(",id=");
        sb.append(mediaRouter$RouteInfo.getTag());
        return sb.toString();
    }
    
    private void setProjection(final MediaProjectionInfo mediaProjectionInfo, final boolean b) {
        final MediaProjectionInfo mProjection = this.mProjection;
        synchronized (this.mProjectionLock) {
            final boolean equals = Objects.equals(mediaProjectionInfo, this.mProjection);
            boolean b2 = true;
            if (b && !equals) {
                this.mProjection = mediaProjectionInfo;
            }
            else if (!b && equals) {
                this.mProjection = null;
            }
            else {
                b2 = false;
            }
            // monitorexit(this.mProjectionLock)
            if (b2) {
                if (CastControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("setProjection: ");
                    sb.append(mProjection);
                    sb.append(" -> ");
                    sb.append(this.mProjection);
                    Log.d("CastController", sb.toString());
                }
                this.fireOnCastDevicesChanged();
            }
        }
    }
    
    private void updateRemoteDisplays() {
        synchronized (this.mRoutes) {
            this.mRoutes.clear();
            for (int routeCount = this.mMediaRouter.getRouteCount(), i = 0; i < routeCount; ++i) {
                final MediaRouter$RouteInfo route = this.mMediaRouter.getRouteAt(i);
                if (route.isEnabled()) {
                    if (route.matchesTypes(4)) {
                        this.ensureTagExists(route);
                        this.mRoutes.put((Object)route.getTag().toString(), (Object)route);
                    }
                }
            }
            final MediaRouter$RouteInfo selectedRoute = this.mMediaRouter.getSelectedRoute(4);
            if (selectedRoute != null && !selectedRoute.isDefault()) {
                this.ensureTagExists(selectedRoute);
                this.mRoutes.put((Object)selectedRoute.getTag().toString(), (Object)selectedRoute);
            }
            // monitorexit(this.mRoutes)
            this.fireOnCastDevicesChanged();
        }
    }
    
    @Override
    public void addCallback(final Callback e) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.add(e);
            // monitorexit(this.mCallbacks)
            this.fireOnCastDevicesChanged(e);
            final Object mDiscoveringLock = this.mDiscoveringLock;
            synchronized (this.mCallbacks) {
                this.handleDiscoveryChangeLocked();
            }
        }
    }
    
    @Override
    public void dump(FileDescriptor mCallbacks, final PrintWriter printWriter, final String[] array) {
        printWriter.println("CastController state:");
        printWriter.print("  mDiscovering=");
        printWriter.println(this.mDiscovering);
        printWriter.print("  mCallbackRegistered=");
        printWriter.println(this.mCallbackRegistered);
        printWriter.print("  mCallbacks.size=");
        mCallbacks = (FileDescriptor)this.mCallbacks;
        synchronized (mCallbacks) {
            printWriter.println(this.mCallbacks.size());
            // monitorexit(mCallbacks)
            printWriter.print("  mRoutes.size=");
            printWriter.println(this.mRoutes.size());
            for (int i = 0; i < this.mRoutes.size(); ++i) {
                mCallbacks = (FileDescriptor)this.mRoutes.valueAt(i);
                printWriter.print("    ");
                printWriter.println(routeToString((MediaRouter$RouteInfo)mCallbacks));
            }
            printWriter.print("  mProjection=");
            printWriter.println(this.mProjection);
        }
    }
    
    void fireOnCastDevicesChanged() {
        synchronized (this.mCallbacks) {
            final Iterator<Callback> iterator = this.mCallbacks.iterator();
            while (iterator.hasNext()) {
                this.fireOnCastDevicesChanged(iterator.next());
            }
        }
    }
    
    @Override
    public List<CastDevice> getCastDevices() {
        final ArrayList<CastDevice> list = new ArrayList<CastDevice>();
        Object mRoutes = this.mRoutes;
        synchronized (mRoutes) {
            for (final MediaRouter$RouteInfo tag : this.mRoutes.values()) {
                final CastDevice e = new CastDevice();
                e.id = tag.getTag().toString();
                final CharSequence name = tag.getName(this.mContext);
                String string;
                if (name != null) {
                    string = name.toString();
                }
                else {
                    string = null;
                }
                e.name = string;
                final CharSequence description = tag.getDescription();
                if (description != null) {
                    description.toString();
                }
                final int statusCode = tag.getStatusCode();
                if (statusCode == 2) {
                    e.state = 1;
                }
                else if (!tag.isSelected() && statusCode != 6) {
                    e.state = 0;
                }
                else {
                    e.state = 2;
                }
                e.tag = tag;
                list.add(e);
            }
            // monitorexit(mRoutes)
            synchronized (this.mProjectionLock) {
                if (this.mProjection != null) {
                    mRoutes = new CastDevice();
                    ((CastDevice)mRoutes).id = this.mProjection.getPackageName();
                    ((CastDevice)mRoutes).name = this.getAppName(this.mProjection.getPackageName());
                    this.mContext.getString(R$string.quick_settings_casting);
                    ((CastDevice)mRoutes).state = 2;
                    ((CastDevice)mRoutes).tag = this.mProjection;
                    list.add((CastDevice)mRoutes);
                }
                return list;
            }
        }
    }
    
    @Override
    public void removeCallback(final Callback o) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(o);
            // monitorexit(this.mCallbacks)
            final Object mDiscoveringLock = this.mDiscoveringLock;
            synchronized (this.mCallbacks) {
                this.handleDiscoveryChangeLocked();
            }
        }
    }
    
    @Override
    public void setCurrentUserId(final int n) {
        this.mMediaRouter.rebindAsUser(n);
    }
    
    @Override
    public void setDiscovering(final boolean b) {
        synchronized (this.mDiscoveringLock) {
            if (this.mDiscovering == b) {
                return;
            }
            this.mDiscovering = b;
            if (CastControllerImpl.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("setDiscovering: ");
                sb.append(b);
                Log.d("CastController", sb.toString());
            }
            this.handleDiscoveryChangeLocked();
        }
    }
    
    @Override
    public void startCasting(final CastDevice castDevice) {
        if (castDevice != null) {
            final Object tag = castDevice.tag;
            if (tag != null) {
                final MediaRouter$RouteInfo mediaRouter$RouteInfo = (MediaRouter$RouteInfo)tag;
                if (CastControllerImpl.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("startCasting: ");
                    sb.append(routeToString(mediaRouter$RouteInfo));
                    Log.d("CastController", sb.toString());
                }
                this.mMediaRouter.selectRoute(4, mediaRouter$RouteInfo);
            }
        }
    }
    
    @Override
    public void stopCasting(final CastDevice castDevice) {
        final boolean b = castDevice.tag instanceof MediaProjectionInfo;
        if (CastControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("stopCasting isProjection=");
            sb.append(b);
            Log.d("CastController", sb.toString());
        }
        if (b) {
            final MediaProjectionInfo mediaProjectionInfo = (MediaProjectionInfo)castDevice.tag;
            if (Objects.equals(this.mProjectionManager.getActiveProjectionInfo(), mediaProjectionInfo)) {
                this.mProjectionManager.stopActiveProjection();
            }
            else {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Projection is no longer active: ");
                sb2.append(mediaProjectionInfo);
                Log.w("CastController", sb2.toString());
            }
        }
        else {
            this.mMediaRouter.getFallbackRoute().select();
        }
    }
}
