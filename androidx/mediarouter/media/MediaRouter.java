// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import androidx.core.util.ObjectsCompat;
import android.text.TextUtils;
import java.util.ListIterator;
import android.content.IntentSender;
import android.net.Uri;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.os.Message;
import android.os.Handler;
import java.util.HashSet;
import java.util.Locale;
import java.util.Collections;
import java.util.Iterator;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import androidx.core.app.ActivityManagerCompat;
import android.app.ActivityManager;
import androidx.core.hardware.display.DisplayManagerCompat;
import java.util.Collection;
import java.util.HashMap;
import androidx.core.util.Pair;
import java.lang.ref.WeakReference;
import java.util.Map;
import android.os.Bundle;
import java.util.List;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.Looper;
import android.util.Log;
import android.content.Context;
import java.util.ArrayList;

public final class MediaRouter
{
    static final boolean DEBUG;
    static GlobalMediaRouter sGlobal;
    final ArrayList<CallbackRecord> mCallbackRecords;
    final Context mContext;
    
    static {
        DEBUG = Log.isLoggable("MediaRouter", 3);
    }
    
    MediaRouter(final Context mContext) {
        this.mCallbackRecords = new ArrayList<CallbackRecord>();
        this.mContext = mContext;
    }
    
    static void checkCallingThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return;
        }
        throw new IllegalStateException("The media router service must only be accessed on the application's main thread.");
    }
    
    private int findCallbackRecord(final Callback callback) {
        for (int size = this.mCallbackRecords.size(), i = 0; i < size; ++i) {
            if (this.mCallbackRecords.get(i).mCallback == callback) {
                return i;
            }
        }
        return -1;
    }
    
    public static MediaRouter getInstance(final Context context) {
        if (context != null) {
            checkCallingThread();
            if (MediaRouter.sGlobal == null) {
                (MediaRouter.sGlobal = new GlobalMediaRouter(context.getApplicationContext())).start();
            }
            return MediaRouter.sGlobal.getRouter(context);
        }
        throw new IllegalArgumentException("context must not be null");
    }
    
    public void addCallback(final MediaRouteSelector mediaRouteSelector, final Callback callback) {
        this.addCallback(mediaRouteSelector, callback, 0);
    }
    
    public void addCallback(final MediaRouteSelector obj, final Callback obj2, final int i) {
        if (obj == null) {
            throw new IllegalArgumentException("selector must not be null");
        }
        if (obj2 != null) {
            checkCallingThread();
            if (MediaRouter.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("addCallback: selector=");
                sb.append(obj);
                sb.append(", callback=");
                sb.append(obj2);
                sb.append(", flags=");
                sb.append(Integer.toHexString(i));
                Log.d("MediaRouter", sb.toString());
            }
            final int callbackRecord = this.findCallbackRecord(obj2);
            CallbackRecord e;
            if (callbackRecord < 0) {
                e = new CallbackRecord(this, obj2);
                this.mCallbackRecords.add(e);
            }
            else {
                e = this.mCallbackRecords.get(callbackRecord);
            }
            int n = 0;
            final int mFlags = e.mFlags;
            final int n2 = 1;
            if ((mFlags & i) != 0x0) {
                e.mFlags = (mFlags | i);
                n = 1;
            }
            if (!e.mSelector.contains(obj)) {
                final MediaRouteSelector.Builder builder = new MediaRouteSelector.Builder(e.mSelector);
                builder.addSelector(obj);
                e.mSelector = builder.build();
                n = n2;
            }
            if (n != 0) {
                MediaRouter.sGlobal.updateDiscoveryRequest();
            }
            return;
        }
        throw new IllegalArgumentException("callback must not be null");
    }
    
    public void addMemberToDynamicGroup(final RouteInfo routeInfo) {
        checkCallingThread();
        MediaRouter.sGlobal.addMemberToDynamicGroup(routeInfo);
    }
    
    public MediaSessionCompat.Token getMediaSessionToken() {
        return MediaRouter.sGlobal.getMediaSessionToken();
    }
    
    public List<RouteInfo> getRoutes() {
        checkCallingThread();
        return MediaRouter.sGlobal.getRoutes();
    }
    
    public RouteInfo getSelectedRoute() {
        checkCallingThread();
        return MediaRouter.sGlobal.getSelectedRoute();
    }
    
    public boolean isRouteAvailable(final MediaRouteSelector mediaRouteSelector, final int n) {
        if (mediaRouteSelector != null) {
            checkCallingThread();
            return MediaRouter.sGlobal.isRouteAvailable(mediaRouteSelector, n);
        }
        throw new IllegalArgumentException("selector must not be null");
    }
    
    public void removeCallback(final Callback obj) {
        if (obj != null) {
            checkCallingThread();
            if (MediaRouter.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("removeCallback: callback=");
                sb.append(obj);
                Log.d("MediaRouter", sb.toString());
            }
            final int callbackRecord = this.findCallbackRecord(obj);
            if (callbackRecord >= 0) {
                this.mCallbackRecords.remove(callbackRecord);
                MediaRouter.sGlobal.updateDiscoveryRequest();
            }
            return;
        }
        throw new IllegalArgumentException("callback must not be null");
    }
    
    public void removeMemberFromDynamicGroup(final RouteInfo routeInfo) {
        checkCallingThread();
        MediaRouter.sGlobal.removeMemberFromDynamicGroup(routeInfo);
    }
    
    public void unselect(final int n) {
        if (n >= 0 && n <= 3) {
            checkCallingThread();
            final RouteInfo chooseFallbackRoute = MediaRouter.sGlobal.chooseFallbackRoute();
            if (MediaRouter.sGlobal.getSelectedRoute() != chooseFallbackRoute) {
                MediaRouter.sGlobal.selectRoute(chooseFallbackRoute, n);
            }
            else {
                final GlobalMediaRouter sGlobal = MediaRouter.sGlobal;
                sGlobal.selectRoute(sGlobal.getDefaultRoute(), n);
            }
            return;
        }
        throw new IllegalArgumentException("Unsupported reason to unselect route");
    }
    
    public abstract static class Callback
    {
        public void onProviderAdded(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
        }
        
        public void onProviderChanged(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
        }
        
        public void onProviderRemoved(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
        }
        
        public void onRouteAdded(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
        }
        
        public abstract void onRouteChanged(final MediaRouter p0, final RouteInfo p1);
        
        public void onRoutePresentationDisplayChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
        }
        
        public void onRouteRemoved(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
        }
        
        public void onRouteSelected(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
        }
        
        public void onRouteUnselected(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
        }
        
        public void onRouteUnselected(final MediaRouter mediaRouter, final RouteInfo routeInfo, final int n) {
            this.onRouteUnselected(mediaRouter, routeInfo);
        }
        
        public void onRouteVolumeChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
        }
    }
    
    private static final class CallbackRecord
    {
        public final Callback mCallback;
        public int mFlags;
        public final MediaRouter mRouter;
        public MediaRouteSelector mSelector;
        
        public CallbackRecord(final MediaRouter mRouter, final Callback mCallback) {
            this.mRouter = mRouter;
            this.mCallback = mCallback;
            this.mSelector = MediaRouteSelector.EMPTY;
        }
        
        public boolean filterRouteEvent(final RouteInfo routeInfo) {
            return (this.mFlags & 0x2) != 0x0 || routeInfo.matchesSelector(this.mSelector);
        }
    }
    
    public abstract static class ControlRequestCallback
    {
        public void onError(final String s, final Bundle bundle) {
        }
        
        public void onResult(final Bundle bundle) {
        }
    }
    
    private static final class GlobalMediaRouter implements SyncCallback, RegisteredMediaRouteProviderWatcher.Callback
    {
        final Context mApplicationContext;
        private RouteInfo mBluetoothRoute;
        final CallbackHandler mCallbackHandler;
        private MediaSessionCompat mCompatSession;
        private RouteInfo mDefaultRoute;
        private MediaRouteDiscoveryRequest mDiscoveryRequest;
        OnDynamicRoutesChangedListener mDynamicRoutesListener;
        private final boolean mLowRam;
        private MediaSessionRecord mMediaSession;
        final RemoteControlClientCompat.PlaybackInfo mPlaybackInfo;
        private final ProviderCallback mProviderCallback;
        private final ArrayList<ProviderInfo> mProviders;
        private RegisteredMediaRouteProviderWatcher mRegisteredProviderWatcher;
        private final ArrayList<RemoteControlClientRecord> mRemoteControlClients;
        private final Map<String, RouteController> mRouteControllerMap;
        final ArrayList<WeakReference<MediaRouter>> mRouters;
        private final ArrayList<RouteInfo> mRoutes;
        RouteInfo mSelectedRoute;
        RouteController mSelectedRouteController;
        final SystemMediaRouteProvider mSystemProvider;
        private final Map<Pair<String, String>, String> mUniqueIdMap;
        
        @SuppressLint({ "SyntheticAccessor" })
        GlobalMediaRouter(final Context mApplicationContext) {
            this.mRouters = new ArrayList<WeakReference<MediaRouter>>();
            this.mRoutes = new ArrayList<RouteInfo>();
            this.mUniqueIdMap = new HashMap<Pair<String, String>, String>();
            this.mProviders = new ArrayList<ProviderInfo>();
            this.mRemoteControlClients = new ArrayList<RemoteControlClientRecord>();
            this.mPlaybackInfo = new RemoteControlClientCompat.PlaybackInfo();
            this.mProviderCallback = new ProviderCallback();
            this.mCallbackHandler = new CallbackHandler();
            this.mRouteControllerMap = new HashMap<String, RouteController>();
            this.mDynamicRoutesListener = new OnDynamicRoutesChangedListener() {
                @Override
                public void onRoutesChanged(final DynamicGroupRouteController dynamicGroupRouteController, final Collection<DynamicRouteDescriptor> collection) {
                    final GlobalMediaRouter this$0 = GlobalMediaRouter.this;
                    if (dynamicGroupRouteController == this$0.mSelectedRouteController) {
                        this$0.mSelectedRoute.updateDescriptors(collection);
                    }
                }
            };
            DisplayManagerCompat.getInstance(this.mApplicationContext = mApplicationContext);
            this.mLowRam = ActivityManagerCompat.isLowRamDevice((ActivityManager)mApplicationContext.getSystemService("activity"));
            this.mSystemProvider = SystemMediaRouteProvider.obtain(mApplicationContext, (SystemMediaRouteProvider.SyncCallback)this);
        }
        
        private ProviderInfo findProviderInfo(final MediaRouteProvider mediaRouteProvider) {
            for (int size = this.mProviders.size(), i = 0; i < size; ++i) {
                if (this.mProviders.get(i).mProviderInstance == mediaRouteProvider) {
                    return this.mProviders.get(i);
                }
            }
            return null;
        }
        
        private int findRouteByUniqueId(final String anObject) {
            for (int size = this.mRoutes.size(), i = 0; i < size; ++i) {
                if (this.mRoutes.get(i).mUniqueId.equals(anObject)) {
                    return i;
                }
            }
            return -1;
        }
        
        private boolean isSystemDefaultRoute(final RouteInfo routeInfo) {
            return routeInfo.getProviderInstance() == this.mSystemProvider && routeInfo.mDescriptorId.equals("DEFAULT_ROUTE");
        }
        
        private boolean isSystemLiveAudioOnlyRoute(final RouteInfo routeInfo) {
            return routeInfo.getProviderInstance() == this.mSystemProvider && routeInfo.supportsControlCategory("android.media.intent.category.LIVE_AUDIO") && !routeInfo.supportsControlCategory("android.media.intent.category.LIVE_VIDEO");
        }
        
        private void setSelectedRouteInternal(final RouteInfo routeInfo, final int i) {
            if (MediaRouter.sGlobal == null || (this.mBluetoothRoute != null && routeInfo.isDefault())) {
                final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                final StringBuilder sb = new StringBuilder();
                for (int j = 3; j < stackTrace.length; ++j) {
                    final StackTraceElement stackTraceElement = stackTrace[j];
                    sb.append(stackTraceElement.getClassName());
                    sb.append(".");
                    sb.append(stackTraceElement.getMethodName());
                    sb.append(":");
                    sb.append(stackTraceElement.getLineNumber());
                    sb.append("  ");
                }
                if (MediaRouter.sGlobal == null) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("setSelectedRouteInternal is called while sGlobal is null: pkgName=");
                    sb2.append(this.mApplicationContext.getPackageName());
                    sb2.append(", callers=");
                    sb2.append(sb.toString());
                    Log.w("MediaRouter", sb2.toString());
                }
                else {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("Default route is selected while a BT route is available: pkgName=");
                    sb3.append(this.mApplicationContext.getPackageName());
                    sb3.append(", callers=");
                    sb3.append(sb.toString());
                    Log.w("MediaRouter", sb3.toString());
                }
            }
            final RouteInfo mSelectedRoute = this.mSelectedRoute;
            if (mSelectedRoute != routeInfo) {
                if (mSelectedRoute != null) {
                    if (MediaRouter.DEBUG) {
                        final StringBuilder sb4 = new StringBuilder();
                        sb4.append("Route unselected: ");
                        sb4.append(this.mSelectedRoute);
                        sb4.append(" reason: ");
                        sb4.append(i);
                        Log.d("MediaRouter", sb4.toString());
                    }
                    this.mCallbackHandler.post(263, this.mSelectedRoute, i);
                    final RouteController mSelectedRouteController = this.mSelectedRouteController;
                    if (mSelectedRouteController != null) {
                        mSelectedRouteController.onUnselect(i);
                        this.mSelectedRouteController.onRelease();
                        this.mSelectedRouteController = null;
                    }
                    if (!this.mRouteControllerMap.isEmpty()) {
                        for (final RouteController routeController : this.mRouteControllerMap.values()) {
                            routeController.onUnselect(i);
                            routeController.onRelease();
                        }
                        this.mRouteControllerMap.clear();
                    }
                }
                if (routeInfo.getProvider().supportsDynamicGroup()) {
                    final MediaRouteProvider.DynamicGroupRouteController onCreateDynamicGroupRouteController = routeInfo.getProviderInstance().onCreateDynamicGroupRouteController(routeInfo.mDescriptorId);
                    onCreateDynamicGroupRouteController.setOnDynamicRoutesChangedListener(ContextCompat.getMainExecutor(this.mApplicationContext), this.mDynamicRoutesListener);
                    this.mSelectedRouteController = onCreateDynamicGroupRouteController;
                    this.mSelectedRoute = routeInfo;
                }
                else {
                    this.mSelectedRouteController = routeInfo.getProviderInstance().onCreateRouteController(routeInfo.mDescriptorId);
                    this.mSelectedRoute = routeInfo;
                }
                final RouteController mSelectedRouteController2 = this.mSelectedRouteController;
                if (mSelectedRouteController2 != null) {
                    mSelectedRouteController2.onSelect();
                }
                if (MediaRouter.DEBUG) {
                    final StringBuilder sb5 = new StringBuilder();
                    sb5.append("Route selected: ");
                    sb5.append(this.mSelectedRoute);
                    Log.d("MediaRouter", sb5.toString());
                }
                this.mCallbackHandler.post(262, this.mSelectedRoute);
                if (this.mSelectedRoute.isGroup()) {
                    final List<RouteInfo> memberRoutes = this.mSelectedRoute.getMemberRoutes();
                    this.mRouteControllerMap.clear();
                    for (final RouteInfo routeInfo2 : memberRoutes) {
                        final MediaRouteProvider.RouteController onCreateRouteController = routeInfo2.getProviderInstance().onCreateRouteController(routeInfo2.mDescriptorId, this.mSelectedRoute.mDescriptorId);
                        onCreateRouteController.onSelect();
                        this.mRouteControllerMap.put(routeInfo2.mUniqueId, onCreateRouteController);
                    }
                }
                this.updatePlaybackInfoFromSelectedRoute();
            }
        }
        
        private void updatePlaybackInfoFromSelectedRoute() {
            final RouteInfo mSelectedRoute = this.mSelectedRoute;
            if (mSelectedRoute != null) {
                this.mPlaybackInfo.volume = mSelectedRoute.getVolume();
                this.mPlaybackInfo.volumeMax = this.mSelectedRoute.getVolumeMax();
                this.mPlaybackInfo.volumeHandling = this.mSelectedRoute.getVolumeHandling();
                this.mPlaybackInfo.playbackStream = this.mSelectedRoute.getPlaybackStream();
                this.mPlaybackInfo.playbackType = this.mSelectedRoute.getPlaybackType();
                final int size = this.mRemoteControlClients.size();
                final int n = 0;
                for (int i = 0; i < size; ++i) {
                    this.mRemoteControlClients.get(i).updatePlaybackInfo();
                }
                if (this.mMediaSession != null) {
                    if (this.mSelectedRoute != this.getDefaultRoute() && this.mSelectedRoute != this.getBluetoothRoute()) {
                        int n2 = n;
                        if (this.mPlaybackInfo.volumeHandling == 1) {
                            n2 = 2;
                        }
                        final MediaSessionRecord mMediaSession = this.mMediaSession;
                        final RemoteControlClientCompat.PlaybackInfo mPlaybackInfo = this.mPlaybackInfo;
                        mMediaSession.configureVolume(n2, mPlaybackInfo.volumeMax, mPlaybackInfo.volume);
                    }
                    else {
                        this.mMediaSession.clearVolumeHandling();
                    }
                }
            }
            else {
                final MediaSessionRecord mMediaSession2 = this.mMediaSession;
                if (mMediaSession2 != null) {
                    mMediaSession2.clearVolumeHandling();
                }
            }
        }
        
        private void updateProviderContents(final ProviderInfo obj, final MediaRouteProviderDescriptor obj2) {
            if (!obj.updateDescriptor(obj2)) {
                return;
            }
            int n = 0;
            int j = 0;
            boolean b3;
            if (obj2 != null && (obj2.isValid() || obj2 == this.mSystemProvider.getDescriptor())) {
                final List<MediaRouteDescriptor> routes = obj2.getRoutes();
                final ArrayList<Pair<RouteInfo, MediaRouteDescriptor>> list = new ArrayList<Pair<RouteInfo, MediaRouteDescriptor>>();
                final ArrayList<Pair> list2 = new ArrayList<Pair>();
                final Iterator<MediaRouteDescriptor> iterator = routes.iterator();
                boolean b = false;
                while (iterator.hasNext()) {
                    final MediaRouteDescriptor mediaRouteDescriptor = iterator.next();
                    if (mediaRouteDescriptor != null && mediaRouteDescriptor.isValid()) {
                        final String id = mediaRouteDescriptor.getId();
                        final int routeIndexByDescriptorId = obj.findRouteIndexByDescriptorId(id);
                        if (routeIndexByDescriptorId < 0) {
                            final RouteInfo routeInfo = new RouteInfo(obj, id, this.assignRouteUniqueId(obj, id));
                            obj.mRoutes.add(j, routeInfo);
                            this.mRoutes.add(routeInfo);
                            if (mediaRouteDescriptor.getGroupMemberIds().size() > 0) {
                                list.add(new Pair<RouteInfo, MediaRouteDescriptor>(routeInfo, mediaRouteDescriptor));
                            }
                            else {
                                routeInfo.maybeUpdateDescriptor(mediaRouteDescriptor);
                                if (MediaRouter.DEBUG) {
                                    final StringBuilder sb = new StringBuilder();
                                    sb.append("Route added: ");
                                    sb.append(routeInfo);
                                    Log.d("MediaRouter", sb.toString());
                                }
                                this.mCallbackHandler.post(257, routeInfo);
                            }
                            ++j;
                        }
                        else if (routeIndexByDescriptorId < j) {
                            final StringBuilder sb2 = new StringBuilder();
                            sb2.append("Ignoring route descriptor with duplicate id: ");
                            sb2.append(mediaRouteDescriptor);
                            Log.w("MediaRouter", sb2.toString());
                        }
                        else {
                            final RouteInfo routeInfo2 = obj.mRoutes.get(routeIndexByDescriptorId);
                            Collections.swap(obj.mRoutes, routeIndexByDescriptorId, j);
                            boolean b2;
                            if (mediaRouteDescriptor.getGroupMemberIds().size() > 0) {
                                list2.add(new Pair<RouteInfo, MediaRouteDescriptor>(routeInfo2, mediaRouteDescriptor));
                                b2 = b;
                            }
                            else {
                                b2 = b;
                                if (this.updateRouteDescriptorAndNotify(routeInfo2, mediaRouteDescriptor) != 0) {
                                    b2 = b;
                                    if (routeInfo2 == this.mSelectedRoute) {
                                        b2 = true;
                                    }
                                }
                            }
                            ++j;
                            b = b2;
                        }
                    }
                    else {
                        final StringBuilder sb3 = new StringBuilder();
                        sb3.append("Ignoring invalid system route descriptor: ");
                        sb3.append(mediaRouteDescriptor);
                        Log.w("MediaRouter", sb3.toString());
                    }
                }
                for (final Pair<RouteInfo, MediaRouteDescriptor> pair : list) {
                    final RouteInfo obj3 = pair.first;
                    obj3.maybeUpdateDescriptor(pair.second);
                    if (MediaRouter.DEBUG) {
                        final StringBuilder sb4 = new StringBuilder();
                        sb4.append("Route added: ");
                        sb4.append(obj3);
                        Log.d("MediaRouter", sb4.toString());
                    }
                    this.mCallbackHandler.post(257, obj3);
                }
                final Iterator<Object> iterator3 = list2.iterator();
                while (true) {
                    n = j;
                    b3 = b;
                    if (!iterator3.hasNext()) {
                        break;
                    }
                    final Pair pair2 = iterator3.next();
                    final RouteInfo routeInfo3 = (RouteInfo)pair2.first;
                    if (this.updateRouteDescriptorAndNotify(routeInfo3, (MediaRouteDescriptor)pair2.second) == 0 || routeInfo3 != this.mSelectedRoute) {
                        continue;
                    }
                    b = true;
                }
            }
            else {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append("Ignoring invalid provider descriptor: ");
                sb5.append(obj2);
                Log.w("MediaRouter", sb5.toString());
                b3 = false;
            }
            for (int i = obj.mRoutes.size() - 1; i >= n; --i) {
                final RouteInfo o = obj.mRoutes.get(i);
                o.maybeUpdateDescriptor(null);
                this.mRoutes.remove(o);
            }
            this.updateSelectedRouteIfNeeded(b3);
            for (int k = obj.mRoutes.size() - 1; k >= n; --k) {
                final RouteInfo obj4 = obj.mRoutes.remove(k);
                if (MediaRouter.DEBUG) {
                    final StringBuilder sb6 = new StringBuilder();
                    sb6.append("Route removed: ");
                    sb6.append(obj4);
                    Log.d("MediaRouter", sb6.toString());
                }
                this.mCallbackHandler.post(258, obj4);
            }
            if (MediaRouter.DEBUG) {
                final StringBuilder sb7 = new StringBuilder();
                sb7.append("Provider changed: ");
                sb7.append(obj);
                Log.d("MediaRouter", sb7.toString());
            }
            this.mCallbackHandler.post(515, obj);
        }
        
        private int updateRouteDescriptorAndNotify(final RouteInfo obj, final MediaRouteDescriptor mediaRouteDescriptor) {
            final int maybeUpdateDescriptor = obj.maybeUpdateDescriptor(mediaRouteDescriptor);
            if (maybeUpdateDescriptor != 0) {
                if ((maybeUpdateDescriptor & 0x1) != 0x0) {
                    if (MediaRouter.DEBUG) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Route changed: ");
                        sb.append(obj);
                        Log.d("MediaRouter", sb.toString());
                    }
                    this.mCallbackHandler.post(259, obj);
                }
                if ((maybeUpdateDescriptor & 0x2) != 0x0) {
                    if (MediaRouter.DEBUG) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Route volume changed: ");
                        sb2.append(obj);
                        Log.d("MediaRouter", sb2.toString());
                    }
                    this.mCallbackHandler.post(260, obj);
                }
                if ((maybeUpdateDescriptor & 0x4) != 0x0) {
                    if (MediaRouter.DEBUG) {
                        final StringBuilder sb3 = new StringBuilder();
                        sb3.append("Route presentation display changed: ");
                        sb3.append(obj);
                        Log.d("MediaRouter", sb3.toString());
                    }
                    this.mCallbackHandler.post(261, obj);
                }
            }
            return maybeUpdateDescriptor;
        }
        
        void addMemberToDynamicGroup(final RouteInfo obj) {
            if (this.mSelectedRoute.getDynamicGroupState() == null || !(this.mSelectedRouteController instanceof DynamicGroupRouteController)) {
                throw new IllegalStateException("There is no currently selected dynamic group route.");
            }
            final DynamicGroupState dynamicGroupState = obj.getDynamicGroupState();
            if (!this.mSelectedRoute.getMemberRoutes().contains(obj) && dynamicGroupState != null && dynamicGroupState.isGroupable()) {
                ((DynamicGroupRouteController)this.mSelectedRouteController).onAddMemberRoute(obj.getDescriptorId());
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Ignoring attemp to add a non-groupable route to dynamic group : ");
            sb.append(obj);
            Log.w("MediaRouter", sb.toString());
        }
        
        @Override
        public void addProvider(final MediaRouteProvider mediaRouteProvider) {
            if (this.findProviderInfo(mediaRouteProvider) == null) {
                final ProviderInfo providerInfo = new ProviderInfo(mediaRouteProvider);
                this.mProviders.add(providerInfo);
                if (MediaRouter.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Provider added: ");
                    sb.append(providerInfo);
                    Log.d("MediaRouter", sb.toString());
                }
                this.mCallbackHandler.post(513, providerInfo);
                this.updateProviderContents(providerInfo, mediaRouteProvider.getDescriptor());
                mediaRouteProvider.setCallback((MediaRouteProvider.Callback)this.mProviderCallback);
                mediaRouteProvider.setDiscoveryRequest(this.mDiscoveryRequest);
            }
        }
        
        String assignRouteUniqueId(final ProviderInfo providerInfo, final String s) {
            final String flattenToShortString = providerInfo.getComponentName().flattenToShortString();
            final StringBuilder sb = new StringBuilder();
            sb.append(flattenToShortString);
            sb.append(":");
            sb.append(s);
            final String string = sb.toString();
            if (this.findRouteByUniqueId(string) < 0) {
                this.mUniqueIdMap.put(new Pair<String, String>(flattenToShortString, s), string);
                return string;
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Either ");
            sb2.append(s);
            sb2.append(" isn't unique in ");
            sb2.append(flattenToShortString);
            sb2.append(" or we're trying to assign a unique ID for an already added route");
            Log.w("MediaRouter", sb2.toString());
            int i = 2;
            String format;
            while (true) {
                format = String.format(Locale.US, "%s_%d", string, i);
                if (this.findRouteByUniqueId(format) < 0) {
                    break;
                }
                ++i;
            }
            this.mUniqueIdMap.put(new Pair<String, String>(flattenToShortString, s), format);
            return format;
        }
        
        RouteInfo chooseFallbackRoute() {
            for (final RouteInfo routeInfo : this.mRoutes) {
                if (routeInfo != this.mDefaultRoute && this.isSystemLiveAudioOnlyRoute(routeInfo) && routeInfo.isSelectable()) {
                    return routeInfo;
                }
            }
            return this.mDefaultRoute;
        }
        
        RouteInfo getBluetoothRoute() {
            return this.mBluetoothRoute;
        }
        
        RouteInfo getDefaultRoute() {
            final RouteInfo mDefaultRoute = this.mDefaultRoute;
            if (mDefaultRoute != null) {
                return mDefaultRoute;
            }
            throw new IllegalStateException("There is no default route.  The media router has not yet been fully initialized.");
        }
        
        public MediaSessionCompat.Token getMediaSessionToken() {
            final MediaSessionRecord mMediaSession = this.mMediaSession;
            if (mMediaSession != null) {
                return mMediaSession.getToken();
            }
            final MediaSessionCompat mCompatSession = this.mCompatSession;
            if (mCompatSession != null) {
                return mCompatSession.getSessionToken();
            }
            return null;
        }
        
        public RouteInfo getRoute(final String anObject) {
            for (final RouteInfo routeInfo : this.mRoutes) {
                if (routeInfo.mUniqueId.equals(anObject)) {
                    return routeInfo;
                }
            }
            return null;
        }
        
        public MediaRouter getRouter(final Context context) {
            int size = this.mRouters.size();
            while (--size >= 0) {
                final MediaRouter mediaRouter = this.mRouters.get(size).get();
                if (mediaRouter == null) {
                    this.mRouters.remove(size);
                }
                else {
                    if (mediaRouter.mContext == context) {
                        return mediaRouter;
                    }
                    continue;
                }
            }
            final MediaRouter referent = new MediaRouter(context);
            this.mRouters.add(new WeakReference<MediaRouter>(referent));
            return referent;
        }
        
        public List<RouteInfo> getRoutes() {
            return this.mRoutes;
        }
        
        RouteInfo getSelectedRoute() {
            final RouteInfo mSelectedRoute = this.mSelectedRoute;
            if (mSelectedRoute != null) {
                return mSelectedRoute;
            }
            throw new IllegalStateException("There is no currently selected route.  The media router has not yet been fully initialized.");
        }
        
        String getUniqueId(final ProviderInfo providerInfo, final String s) {
            return this.mUniqueIdMap.get(new Pair(providerInfo.getComponentName().flattenToShortString(), s));
        }
        
        public boolean isRouteAvailable(final MediaRouteSelector mediaRouteSelector, final int n) {
            if (mediaRouteSelector.isEmpty()) {
                return false;
            }
            if ((n & 0x2) == 0x0 && this.mLowRam) {
                return true;
            }
            for (int size = this.mRoutes.size(), i = 0; i < size; ++i) {
                final RouteInfo routeInfo = this.mRoutes.get(i);
                if ((n & 0x1) == 0x0 || !routeInfo.isDefaultOrBluetooth()) {
                    if (routeInfo.matchesSelector(mediaRouteSelector)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public void onSystemRouteSelectedByDescriptorId(final String s) {
            this.mCallbackHandler.removeMessages(262);
            final ProviderInfo providerInfo = this.findProviderInfo(this.mSystemProvider);
            if (providerInfo != null) {
                final RouteInfo routeByDescriptorId = providerInfo.findRouteByDescriptorId(s);
                if (routeByDescriptorId != null) {
                    routeByDescriptorId.select();
                }
            }
        }
        
        void removeMemberFromDynamicGroup(final RouteInfo obj) {
            if (this.mSelectedRoute.getDynamicGroupState() == null || !(this.mSelectedRouteController instanceof DynamicGroupRouteController)) {
                throw new IllegalStateException("There is no currently selected dynamic group route.");
            }
            final DynamicGroupState dynamicGroupState = obj.getDynamicGroupState();
            if (!this.mSelectedRoute.getMemberRoutes().contains(obj) || dynamicGroupState == null || !dynamicGroupState.isUnselectable()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Ignoring attempt to remove a non-unselectable member route : ");
                sb.append(obj);
                Log.w("MediaRouter", sb.toString());
                return;
            }
            if (this.mSelectedRoute.getMemberRoutes().size() <= 1) {
                Log.w("MediaRouter", "Ignoring attempt to remove the last member route.");
                return;
            }
            ((DynamicGroupRouteController)this.mSelectedRouteController).onRemoveMemberRoute(obj.getDescriptorId());
        }
        
        @Override
        public void removeProvider(final MediaRouteProvider mediaRouteProvider) {
            final ProviderInfo providerInfo = this.findProviderInfo(mediaRouteProvider);
            if (providerInfo != null) {
                mediaRouteProvider.setCallback(null);
                mediaRouteProvider.setDiscoveryRequest(null);
                this.updateProviderContents(providerInfo, null);
                if (MediaRouter.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Provider removed: ");
                    sb.append(providerInfo);
                    Log.d("MediaRouter", sb.toString());
                }
                this.mCallbackHandler.post(514, providerInfo);
                this.mProviders.remove(providerInfo);
            }
        }
        
        public void requestSetVolume(final RouteInfo routeInfo, final int n) {
            if (routeInfo == this.mSelectedRoute) {
                final RouteController mSelectedRouteController = this.mSelectedRouteController;
                if (mSelectedRouteController != null) {
                    mSelectedRouteController.onSetVolume(n);
                    return;
                }
            }
            if (!this.mRouteControllerMap.isEmpty()) {
                final RouteController routeController = this.mRouteControllerMap.get(routeInfo.mUniqueId);
                if (routeController != null) {
                    routeController.onSetVolume(n);
                }
            }
        }
        
        public void requestUpdateVolume(final RouteInfo routeInfo, final int n) {
            if (routeInfo == this.mSelectedRoute) {
                final RouteController mSelectedRouteController = this.mSelectedRouteController;
                if (mSelectedRouteController != null) {
                    mSelectedRouteController.onUpdateVolume(n);
                }
            }
        }
        
        void selectRoute(final RouteInfo routeInfo) {
            this.selectRoute(routeInfo, 3);
        }
        
        void selectRoute(final RouteInfo obj, final int n) {
            if (!this.mRoutes.contains(obj)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Ignoring attempt to select removed route: ");
                sb.append(obj);
                Log.w("MediaRouter", sb.toString());
                return;
            }
            if (!obj.mEnabled) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Ignoring attempt to select disabled route: ");
                sb2.append(obj);
                Log.w("MediaRouter", sb2.toString());
                return;
            }
            this.setSelectedRouteInternal(obj, n);
        }
        
        public void start() {
            this.addProvider(this.mSystemProvider);
            (this.mRegisteredProviderWatcher = new RegisteredMediaRouteProviderWatcher(this.mApplicationContext, (RegisteredMediaRouteProviderWatcher.Callback)this)).start();
        }
        
        public void updateDiscoveryRequest() {
            final MediaRouteSelector.Builder builder = new MediaRouteSelector.Builder();
            int size = this.mRouters.size();
            final int n = 0;
            boolean b2;
            boolean b = b2 = false;
            while (true) {
                final int n2 = size - 1;
                if (n2 < 0) {
                    break;
                }
                final MediaRouter mediaRouter = this.mRouters.get(n2).get();
                if (mediaRouter == null) {
                    this.mRouters.remove(n2);
                    size = n2;
                }
                else {
                    final int size2 = mediaRouter.mCallbackRecords.size();
                    int index = 0;
                    boolean b3 = b2;
                    boolean b4 = b;
                    while (true) {
                        size = n2;
                        b = b4;
                        b2 = b3;
                        if (index >= size2) {
                            break;
                        }
                        final CallbackRecord callbackRecord = mediaRouter.mCallbackRecords.get(index);
                        builder.addSelector(callbackRecord.mSelector);
                        if ((callbackRecord.mFlags & 0x1) != 0x0) {
                            b4 = (b3 = true);
                        }
                        boolean b5 = b4;
                        if ((callbackRecord.mFlags & 0x4) != 0x0) {
                            b5 = b4;
                            if (!this.mLowRam) {
                                b5 = true;
                            }
                        }
                        b4 = b5;
                        if ((callbackRecord.mFlags & 0x8) != 0x0) {
                            b4 = true;
                        }
                        ++index;
                    }
                }
            }
            MediaRouteSelector mediaRouteSelector;
            if (b) {
                mediaRouteSelector = builder.build();
            }
            else {
                mediaRouteSelector = MediaRouteSelector.EMPTY;
            }
            final MediaRouteDiscoveryRequest mDiscoveryRequest = this.mDiscoveryRequest;
            if (mDiscoveryRequest != null && mDiscoveryRequest.getSelector().equals(mediaRouteSelector) && this.mDiscoveryRequest.isActiveScan() == b2) {
                return;
            }
            if (mediaRouteSelector.isEmpty() && !b2) {
                if (this.mDiscoveryRequest == null) {
                    return;
                }
                this.mDiscoveryRequest = null;
            }
            else {
                this.mDiscoveryRequest = new MediaRouteDiscoveryRequest(mediaRouteSelector, b2);
            }
            if (MediaRouter.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Updated discovery request: ");
                sb.append(this.mDiscoveryRequest);
                Log.d("MediaRouter", sb.toString());
            }
            if (b && !b2 && this.mLowRam) {
                Log.i("MediaRouter", "Forcing passive route discovery on a low-RAM device, system performance may be affected.  Please consider using CALLBACK_FLAG_REQUEST_DISCOVERY instead of CALLBACK_FLAG_FORCE_DISCOVERY.");
            }
            for (int size3 = this.mProviders.size(), i = n; i < size3; ++i) {
                this.mProviders.get(i).mProviderInstance.setDiscoveryRequest(this.mDiscoveryRequest);
            }
        }
        
        void updateProviderDescriptor(final MediaRouteProvider mediaRouteProvider, final MediaRouteProviderDescriptor mediaRouteProviderDescriptor) {
            final ProviderInfo providerInfo = this.findProviderInfo(mediaRouteProvider);
            if (providerInfo != null) {
                this.updateProviderContents(providerInfo, mediaRouteProviderDescriptor);
            }
        }
        
        void updateSelectedRouteIfNeeded(final boolean b) {
            final RouteInfo mDefaultRoute = this.mDefaultRoute;
            if (mDefaultRoute != null && !mDefaultRoute.isSelectable()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Clearing the default route because it is no longer selectable: ");
                sb.append(this.mDefaultRoute);
                Log.i("MediaRouter", sb.toString());
                this.mDefaultRoute = null;
            }
            if (this.mDefaultRoute == null && !this.mRoutes.isEmpty()) {
                for (final RouteInfo mDefaultRoute2 : this.mRoutes) {
                    if (this.isSystemDefaultRoute(mDefaultRoute2) && mDefaultRoute2.isSelectable()) {
                        this.mDefaultRoute = mDefaultRoute2;
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Found default route: ");
                        sb2.append(this.mDefaultRoute);
                        Log.i("MediaRouter", sb2.toString());
                        break;
                    }
                }
            }
            final RouteInfo mBluetoothRoute = this.mBluetoothRoute;
            if (mBluetoothRoute != null && !mBluetoothRoute.isSelectable()) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Clearing the bluetooth route because it is no longer selectable: ");
                sb3.append(this.mBluetoothRoute);
                Log.i("MediaRouter", sb3.toString());
                this.mBluetoothRoute = null;
            }
            if (this.mBluetoothRoute == null && !this.mRoutes.isEmpty()) {
                for (final RouteInfo mBluetoothRoute2 : this.mRoutes) {
                    if (this.isSystemLiveAudioOnlyRoute(mBluetoothRoute2) && mBluetoothRoute2.isSelectable()) {
                        this.mBluetoothRoute = mBluetoothRoute2;
                        final StringBuilder sb4 = new StringBuilder();
                        sb4.append("Found bluetooth route: ");
                        sb4.append(this.mBluetoothRoute);
                        Log.i("MediaRouter", sb4.toString());
                        break;
                    }
                }
            }
            final RouteInfo mSelectedRoute = this.mSelectedRoute;
            if (mSelectedRoute != null && mSelectedRoute.isEnabled()) {
                if (b) {
                    if (this.mSelectedRoute.isGroup()) {
                        final List<RouteInfo> memberRoutes = this.mSelectedRoute.getMemberRoutes();
                        final HashSet<String> set = new HashSet<String>();
                        final Iterator<RouteInfo> iterator3 = memberRoutes.iterator();
                        while (iterator3.hasNext()) {
                            set.add(((RouteInfo)iterator3.next()).mUniqueId);
                        }
                        final Iterator<Map.Entry<String, RouteController>> iterator4 = this.mRouteControllerMap.entrySet().iterator();
                        while (iterator4.hasNext()) {
                            final Map.Entry<String, RouteController> entry = iterator4.next();
                            if (!set.contains(entry.getKey())) {
                                final RouteController routeController = entry.getValue();
                                routeController.onUnselect();
                                routeController.onRelease();
                                iterator4.remove();
                            }
                        }
                        for (final RouteInfo routeInfo : memberRoutes) {
                            if (!this.mRouteControllerMap.containsKey(routeInfo.mUniqueId)) {
                                final MediaRouteProvider.RouteController onCreateRouteController = routeInfo.getProviderInstance().onCreateRouteController(routeInfo.mDescriptorId, this.mSelectedRoute.mDescriptorId);
                                onCreateRouteController.onSelect();
                                this.mRouteControllerMap.put(routeInfo.mUniqueId, onCreateRouteController);
                            }
                        }
                    }
                    this.updatePlaybackInfoFromSelectedRoute();
                }
            }
            else {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append("Unselecting the current route because it is no longer selectable: ");
                sb5.append(this.mSelectedRoute);
                Log.i("MediaRouter", sb5.toString());
                this.setSelectedRouteInternal(this.chooseFallbackRoute(), 0);
            }
        }
        
        private final class CallbackHandler extends Handler
        {
            private final ArrayList<CallbackRecord> mTempCallbackRecords;
            
            CallbackHandler() {
                this.mTempCallbackRecords = new ArrayList<CallbackRecord>();
            }
            
            private void invokeCallback(final CallbackRecord callbackRecord, final int n, final Object o, final int n2) {
                final MediaRouter mRouter = callbackRecord.mRouter;
                final MediaRouter.Callback mCallback = callbackRecord.mCallback;
                final int n3 = 0xFF00 & n;
                if (n3 != 256) {
                    if (n3 == 512) {
                        final ProviderInfo providerInfo = (ProviderInfo)o;
                        switch (n) {
                            case 515: {
                                mCallback.onProviderChanged(mRouter, providerInfo);
                                break;
                            }
                            case 514: {
                                mCallback.onProviderRemoved(mRouter, providerInfo);
                                break;
                            }
                            case 513: {
                                mCallback.onProviderAdded(mRouter, providerInfo);
                                break;
                            }
                        }
                    }
                }
                else {
                    final RouteInfo routeInfo = (RouteInfo)o;
                    if (callbackRecord.filterRouteEvent(routeInfo)) {
                        switch (n) {
                            case 263: {
                                mCallback.onRouteUnselected(mRouter, routeInfo, n2);
                                break;
                            }
                            case 262: {
                                mCallback.onRouteSelected(mRouter, routeInfo);
                                break;
                            }
                            case 261: {
                                mCallback.onRoutePresentationDisplayChanged(mRouter, routeInfo);
                                break;
                            }
                            case 260: {
                                mCallback.onRouteVolumeChanged(mRouter, routeInfo);
                                break;
                            }
                            case 259: {
                                mCallback.onRouteChanged(mRouter, routeInfo);
                                break;
                            }
                            case 258: {
                                mCallback.onRouteRemoved(mRouter, routeInfo);
                                break;
                            }
                            case 257: {
                                mCallback.onRouteAdded(mRouter, routeInfo);
                                break;
                            }
                        }
                    }
                }
            }
            
            private void syncWithSystemProvider(final int n, final Object o) {
                if (n != 262) {
                    switch (n) {
                        case 259: {
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteChanged((RouteInfo)o);
                            break;
                        }
                        case 258: {
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteRemoved((RouteInfo)o);
                            break;
                        }
                        case 257: {
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteAdded((RouteInfo)o);
                            break;
                        }
                    }
                }
                else {
                    GlobalMediaRouter.this.mSystemProvider.onSyncRouteSelected((RouteInfo)o);
                }
            }
            
            public void handleMessage(final Message message) {
                final int what = message.what;
                final Object obj = message.obj;
                final int arg1 = message.arg1;
                if (what == 259 && GlobalMediaRouter.this.getSelectedRoute().getId().equals(((RouteInfo)obj).getId())) {
                    GlobalMediaRouter.this.updateSelectedRouteIfNeeded(true);
                }
                this.syncWithSystemProvider(what, obj);
                try {
                    int size = GlobalMediaRouter.this.mRouters.size();
                    while (--size >= 0) {
                        final MediaRouter mediaRouter = GlobalMediaRouter.this.mRouters.get(size).get();
                        if (mediaRouter == null) {
                            GlobalMediaRouter.this.mRouters.remove(size);
                        }
                        else {
                            this.mTempCallbackRecords.addAll((Collection<? extends CallbackRecord>)mediaRouter.mCallbackRecords);
                        }
                    }
                    for (int size2 = this.mTempCallbackRecords.size(), i = 0; i < size2; ++i) {
                        this.invokeCallback(this.mTempCallbackRecords.get(i), what, obj, arg1);
                    }
                }
                finally {
                    this.mTempCallbackRecords.clear();
                }
            }
            
            public void post(final int n, final Object o) {
                this.obtainMessage(n, o).sendToTarget();
            }
            
            public void post(final int n, final Object o, final int arg1) {
                final Message obtainMessage = this.obtainMessage(n, o);
                obtainMessage.arg1 = arg1;
                obtainMessage.sendToTarget();
            }
        }
        
        private final class MediaSessionRecord
        {
            public abstract void clearVolumeHandling();
            
            public abstract void configureVolume(final int p0, final int p1, final int p2);
            
            public abstract MediaSessionCompat.Token getToken();
        }
        
        private final class ProviderCallback extends MediaRouteProvider.Callback
        {
            ProviderCallback() {
            }
            
            @Override
            public void onDescriptorChanged(final MediaRouteProvider mediaRouteProvider, final MediaRouteProviderDescriptor mediaRouteProviderDescriptor) {
                GlobalMediaRouter.this.updateProviderDescriptor(mediaRouteProvider, mediaRouteProviderDescriptor);
            }
        }
        
        private final class RemoteControlClientRecord
        {
            private final RemoteControlClientCompat mRccCompat;
            final /* synthetic */ GlobalMediaRouter this$0;
            
            public void updatePlaybackInfo() {
                this.mRccCompat.setPlaybackInfo(this.this$0.mPlaybackInfo);
            }
        }
    }
    
    public static final class ProviderInfo
    {
        private MediaRouteProviderDescriptor mDescriptor;
        private final MediaRouteProvider.ProviderMetadata mMetadata;
        final MediaRouteProvider mProviderInstance;
        final List<RouteInfo> mRoutes;
        
        ProviderInfo(final MediaRouteProvider mProviderInstance) {
            this.mRoutes = new ArrayList<RouteInfo>();
            this.mProviderInstance = mProviderInstance;
            this.mMetadata = mProviderInstance.getMetadata();
        }
        
        RouteInfo findRouteByDescriptorId(final String anObject) {
            for (int size = this.mRoutes.size(), i = 0; i < size; ++i) {
                if (this.mRoutes.get(i).mDescriptorId.equals(anObject)) {
                    return this.mRoutes.get(i);
                }
            }
            return null;
        }
        
        int findRouteIndexByDescriptorId(final String anObject) {
            for (int size = this.mRoutes.size(), i = 0; i < size; ++i) {
                if (this.mRoutes.get(i).mDescriptorId.equals(anObject)) {
                    return i;
                }
            }
            return -1;
        }
        
        public ComponentName getComponentName() {
            return this.mMetadata.getComponentName();
        }
        
        public String getPackageName() {
            return this.mMetadata.getPackageName();
        }
        
        public MediaRouteProvider getProviderInstance() {
            MediaRouter.checkCallingThread();
            return this.mProviderInstance;
        }
        
        public List<RouteInfo> getRoutes() {
            MediaRouter.checkCallingThread();
            return Collections.unmodifiableList((List<? extends RouteInfo>)this.mRoutes);
        }
        
        boolean supportsDynamicGroup() {
            final MediaRouteProviderDescriptor mDescriptor = this.mDescriptor;
            return mDescriptor != null && mDescriptor.supportsDynamicGroupRoute();
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("MediaRouter.RouteProviderInfo{ packageName=");
            sb.append(this.getPackageName());
            sb.append(" }");
            return sb.toString();
        }
        
        boolean updateDescriptor(final MediaRouteProviderDescriptor mDescriptor) {
            if (this.mDescriptor != mDescriptor) {
                this.mDescriptor = mDescriptor;
                return true;
            }
            return false;
        }
    }
    
    public static class RouteInfo
    {
        private boolean mCanDisconnect;
        private int mConnectionState;
        private final ArrayList<IntentFilter> mControlFilters;
        private String mDescription;
        MediaRouteDescriptor mDescriptor;
        final String mDescriptorId;
        private int mDeviceType;
        MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor mDynamicDescriptor;
        private DynamicGroupState mDynamicGroupState;
        boolean mEnabled;
        private Bundle mExtras;
        private Uri mIconUri;
        private List<RouteInfo> mMemberRoutes;
        private String mName;
        private int mPlaybackStream;
        private int mPlaybackType;
        private int mPresentationDisplayId;
        private final ProviderInfo mProvider;
        private IntentSender mSettingsIntent;
        final String mUniqueId;
        private int mVolume;
        private int mVolumeHandling;
        private int mVolumeMax;
        
        RouteInfo(final ProviderInfo mProvider, final String mDescriptorId, final String mUniqueId) {
            this.mControlFilters = new ArrayList<IntentFilter>();
            this.mPresentationDisplayId = -1;
            this.mMemberRoutes = new ArrayList<RouteInfo>();
            this.mProvider = mProvider;
            this.mDescriptorId = mDescriptorId;
            this.mUniqueId = mUniqueId;
        }
        
        private boolean isSameControlFilter(final IntentFilter intentFilter, final IntentFilter intentFilter2) {
            if (intentFilter == intentFilter2) {
                return true;
            }
            if (intentFilter == null || intentFilter2 == null) {
                return false;
            }
            final int countActions = intentFilter.countActions();
            if (countActions != intentFilter2.countActions()) {
                return false;
            }
            for (int i = 0; i < countActions; ++i) {
                if (!intentFilter.getAction(i).equals(intentFilter2.getAction(i))) {
                    return false;
                }
            }
            final int countCategories = intentFilter.countCategories();
            if (countCategories != intentFilter2.countCategories()) {
                return false;
            }
            for (int j = 0; j < countCategories; ++j) {
                if (!intentFilter.getCategory(j).equals(intentFilter2.getCategory(j))) {
                    return false;
                }
            }
            return true;
        }
        
        private boolean isSameControlFilters(final List<IntentFilter> list, final List<IntentFilter> list2) {
            boolean b = true;
            if (list == list2) {
                return true;
            }
            if (list != null && list2 != null) {
                final ListIterator<IntentFilter> listIterator = list.listIterator();
                final ListIterator<IntentFilter> listIterator2 = list2.listIterator();
                while (listIterator.hasNext() && listIterator2.hasNext()) {
                    if (!this.isSameControlFilter(listIterator.next(), listIterator2.next())) {
                        return false;
                    }
                }
                if (listIterator.hasNext() || listIterator2.hasNext()) {
                    b = false;
                }
                return b;
            }
            return false;
        }
        
        private static boolean isSystemMediaRouteProvider(final RouteInfo routeInfo) {
            return TextUtils.equals((CharSequence)routeInfo.getProviderInstance().getMetadata().getPackageName(), (CharSequence)"android");
        }
        
        public boolean canDisconnect() {
            return this.mCanDisconnect;
        }
        
        RouteInfo findRouteByDynamicRouteDescriptor(final MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor dynamicRouteDescriptor) {
            return this.getProvider().findRouteByDescriptorId(dynamicRouteDescriptor.getRouteDescriptor().getId());
        }
        
        public int getConnectionState() {
            return this.mConnectionState;
        }
        
        public String getDescription() {
            return this.mDescription;
        }
        
        String getDescriptorId() {
            return this.mDescriptorId;
        }
        
        public int getDeviceType() {
            return this.mDeviceType;
        }
        
        public MediaRouteProvider.DynamicGroupRouteController getDynamicGroupController() {
            final MediaRouteProvider.RouteController mSelectedRouteController = MediaRouter.sGlobal.mSelectedRouteController;
            if (mSelectedRouteController instanceof MediaRouteProvider.DynamicGroupRouteController) {
                return (MediaRouteProvider.DynamicGroupRouteController)mSelectedRouteController;
            }
            return null;
        }
        
        public DynamicGroupState getDynamicGroupState() {
            if (this.mDynamicGroupState == null && this.mDynamicDescriptor != null) {
                this.mDynamicGroupState = new DynamicGroupState();
            }
            return this.mDynamicGroupState;
        }
        
        public Uri getIconUri() {
            return this.mIconUri;
        }
        
        public String getId() {
            return this.mUniqueId;
        }
        
        public List<RouteInfo> getMemberRoutes() {
            return Collections.unmodifiableList((List<? extends RouteInfo>)this.mMemberRoutes);
        }
        
        public String getName() {
            return this.mName;
        }
        
        public int getPlaybackStream() {
            return this.mPlaybackStream;
        }
        
        public int getPlaybackType() {
            return this.mPlaybackType;
        }
        
        public int getPresentationDisplayId() {
            return this.mPresentationDisplayId;
        }
        
        public ProviderInfo getProvider() {
            return this.mProvider;
        }
        
        public MediaRouteProvider getProviderInstance() {
            return this.mProvider.getProviderInstance();
        }
        
        public int getVolume() {
            return this.mVolume;
        }
        
        public int getVolumeHandling() {
            return this.mVolumeHandling;
        }
        
        public int getVolumeMax() {
            return this.mVolumeMax;
        }
        
        public boolean isDefault() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getDefaultRoute() == this;
        }
        
        public boolean isDefaultOrBluetooth() {
            final boolean default1 = this.isDefault();
            boolean b2;
            final boolean b = b2 = true;
            if (!default1) {
                if (this.mDeviceType == 3) {
                    b2 = b;
                }
                else {
                    b2 = (isSystemMediaRouteProvider(this) && this.supportsControlCategory("android.media.intent.category.LIVE_AUDIO") && !this.supportsControlCategory("android.media.intent.category.LIVE_VIDEO") && b);
                }
            }
            return b2;
        }
        
        public boolean isEnabled() {
            return this.mEnabled;
        }
        
        public boolean isGroup() {
            final int size = this.getMemberRoutes().size();
            boolean b = true;
            if (size < 1) {
                b = false;
            }
            return b;
        }
        
        boolean isSelectable() {
            return this.mDescriptor != null && this.mEnabled;
        }
        
        public boolean isSelected() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getSelectedRoute() == this;
        }
        
        public boolean matchesSelector(final MediaRouteSelector mediaRouteSelector) {
            if (mediaRouteSelector != null) {
                MediaRouter.checkCallingThread();
                return mediaRouteSelector.matchesControlFilters(this.mControlFilters);
            }
            throw new IllegalArgumentException("selector must not be null");
        }
        
        int maybeUpdateDescriptor(final MediaRouteDescriptor mediaRouteDescriptor) {
            int updateDescriptor;
            if (this.mDescriptor != mediaRouteDescriptor) {
                updateDescriptor = this.updateDescriptor(mediaRouteDescriptor);
            }
            else {
                updateDescriptor = 0;
            }
            return updateDescriptor;
        }
        
        public void requestSetVolume(final int b) {
            MediaRouter.checkCallingThread();
            MediaRouter.sGlobal.requestSetVolume(this, Math.min(this.mVolumeMax, Math.max(0, b)));
        }
        
        public void requestUpdateVolume(final int n) {
            MediaRouter.checkCallingThread();
            if (n != 0) {
                MediaRouter.sGlobal.requestUpdateVolume(this, n);
            }
        }
        
        public void select() {
            MediaRouter.checkCallingThread();
            MediaRouter.sGlobal.selectRoute(this);
        }
        
        public boolean supportsControlCategory(final String s) {
            if (s != null) {
                MediaRouter.checkCallingThread();
                for (int size = this.mControlFilters.size(), i = 0; i < size; ++i) {
                    if (this.mControlFilters.get(i).hasCategory(s)) {
                        return true;
                    }
                }
                return false;
            }
            throw new IllegalArgumentException("category must not be null");
        }
        
        @Override
        public String toString() {
            if (this.isGroup()) {
                final StringBuilder sb = new StringBuilder(super.toString());
                sb.append('[');
                for (int size = this.mMemberRoutes.size(), i = 0; i < size; ++i) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(this.mMemberRoutes.get(i));
                }
                sb.append(']');
                return sb.toString();
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("MediaRouter.RouteInfo{ uniqueId=");
            sb2.append(this.mUniqueId);
            sb2.append(", name=");
            sb2.append(this.mName);
            sb2.append(", description=");
            sb2.append(this.mDescription);
            sb2.append(", iconUri=");
            sb2.append(this.mIconUri);
            sb2.append(", enabled=");
            sb2.append(this.mEnabled);
            sb2.append(", connectionState=");
            sb2.append(this.mConnectionState);
            sb2.append(", canDisconnect=");
            sb2.append(this.mCanDisconnect);
            sb2.append(", playbackType=");
            sb2.append(this.mPlaybackType);
            sb2.append(", playbackStream=");
            sb2.append(this.mPlaybackStream);
            sb2.append(", deviceType=");
            sb2.append(this.mDeviceType);
            sb2.append(", volumeHandling=");
            sb2.append(this.mVolumeHandling);
            sb2.append(", volume=");
            sb2.append(this.mVolume);
            sb2.append(", volumeMax=");
            sb2.append(this.mVolumeMax);
            sb2.append(", presentationDisplayId=");
            sb2.append(this.mPresentationDisplayId);
            sb2.append(", extras=");
            sb2.append(this.mExtras);
            sb2.append(", settingsIntent=");
            sb2.append(this.mSettingsIntent);
            sb2.append(", providerPackageName=");
            sb2.append(this.mProvider.getPackageName());
            sb2.append(" }");
            return sb2.toString();
        }
        
        int updateDescriptor(final MediaRouteDescriptor mDescriptor) {
            this.mDescriptor = mDescriptor;
            int n = 0;
            final int n2 = 0;
            if (mDescriptor != null) {
                boolean b;
                if (!ObjectsCompat.equals(this.mName, mDescriptor.getName())) {
                    this.mName = mDescriptor.getName();
                    b = true;
                }
                else {
                    b = false;
                }
                boolean b2 = b;
                if (!ObjectsCompat.equals(this.mDescription, mDescriptor.getDescription())) {
                    this.mDescription = mDescriptor.getDescription();
                    b2 = (b | true);
                }
                boolean b3 = b2;
                if (!ObjectsCompat.equals(this.mIconUri, mDescriptor.getIconUri())) {
                    this.mIconUri = mDescriptor.getIconUri();
                    b3 = (b2 | true);
                }
                boolean b4 = b3;
                if (this.mEnabled != mDescriptor.isEnabled()) {
                    this.mEnabled = mDescriptor.isEnabled();
                    b4 = (b3 | true);
                }
                boolean b5 = b4;
                if (this.mConnectionState != mDescriptor.getConnectionState()) {
                    this.mConnectionState = mDescriptor.getConnectionState();
                    b5 = (b4 | true);
                }
                boolean b6 = b5;
                if (!this.isSameControlFilters(this.mControlFilters, mDescriptor.getControlFilters())) {
                    this.mControlFilters.clear();
                    this.mControlFilters.addAll(mDescriptor.getControlFilters());
                    b6 = (b5 | true);
                }
                boolean b7 = b6;
                if (this.mPlaybackType != mDescriptor.getPlaybackType()) {
                    this.mPlaybackType = mDescriptor.getPlaybackType();
                    b7 = (b6 | true);
                }
                boolean b8 = b7;
                if (this.mPlaybackStream != mDescriptor.getPlaybackStream()) {
                    this.mPlaybackStream = mDescriptor.getPlaybackStream();
                    b8 = (b7 | true);
                }
                int n3 = b8 ? 1 : 0;
                if (this.mDeviceType != mDescriptor.getDeviceType()) {
                    this.mDeviceType = mDescriptor.getDeviceType();
                    n3 = ((b8 ? 1 : 0) | 0x1);
                }
                int n4 = n3;
                if (this.mVolumeHandling != mDescriptor.getVolumeHandling()) {
                    this.mVolumeHandling = mDescriptor.getVolumeHandling();
                    n4 = (n3 | 0x3);
                }
                int n5 = n4;
                if (this.mVolume != mDescriptor.getVolume()) {
                    this.mVolume = mDescriptor.getVolume();
                    n5 = (n4 | 0x3);
                }
                int n6 = n5;
                if (this.mVolumeMax != mDescriptor.getVolumeMax()) {
                    this.mVolumeMax = mDescriptor.getVolumeMax();
                    n6 = (n5 | 0x3);
                }
                int n7 = n6;
                if (this.mPresentationDisplayId != mDescriptor.getPresentationDisplayId()) {
                    this.mPresentationDisplayId = mDescriptor.getPresentationDisplayId();
                    n7 = (n6 | 0x5);
                }
                int n8 = n7;
                if (!ObjectsCompat.equals(this.mExtras, mDescriptor.getExtras())) {
                    this.mExtras = mDescriptor.getExtras();
                    n8 = (n7 | 0x1);
                }
                int n9 = n8;
                if (!ObjectsCompat.equals(this.mSettingsIntent, mDescriptor.getSettingsActivity())) {
                    this.mSettingsIntent = mDescriptor.getSettingsActivity();
                    n9 = (n8 | 0x1);
                }
                n = n9;
                if (this.mCanDisconnect != mDescriptor.canDisconnectAndKeepPlaying()) {
                    this.mCanDisconnect = mDescriptor.canDisconnectAndKeepPlaying();
                    n = (n9 | 0x5);
                }
                final List<String> groupMemberIds = mDescriptor.getGroupMemberIds();
                final ArrayList<RouteInfo> mMemberRoutes = new ArrayList<RouteInfo>();
                int n10 = n2;
                if (groupMemberIds.size() != this.mMemberRoutes.size()) {
                    n10 = 1;
                }
                final Iterator<String> iterator = groupMemberIds.iterator();
                while (iterator.hasNext()) {
                    final RouteInfo route = MediaRouter.sGlobal.getRoute(MediaRouter.sGlobal.getUniqueId(this.getProvider(), iterator.next()));
                    if (route != null) {
                        mMemberRoutes.add(route);
                        if (n10 != 0 || this.mMemberRoutes.contains(route)) {
                            continue;
                        }
                        n10 = 1;
                    }
                }
                if (n10 != 0) {
                    this.mMemberRoutes = mMemberRoutes;
                    n |= 0x1;
                }
            }
            return n;
        }
        
        void updateDescriptors(final Collection<MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> collection) {
            this.mMemberRoutes.clear();
            for (final MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor mDynamicDescriptor : collection) {
                final RouteInfo routeByDynamicRouteDescriptor = this.findRouteByDynamicRouteDescriptor(mDynamicDescriptor);
                if (routeByDynamicRouteDescriptor == null) {
                    continue;
                }
                routeByDynamicRouteDescriptor.mDynamicDescriptor = mDynamicDescriptor;
                if (mDynamicDescriptor.getSelectionState() != 2 && mDynamicDescriptor.getSelectionState() != 3) {
                    continue;
                }
                this.mMemberRoutes.add(routeByDynamicRouteDescriptor);
            }
            MediaRouter.sGlobal.mCallbackHandler.post(259, this);
        }
        
        public class DynamicGroupState
        {
            public int getSelectionState() {
                final MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor mDynamicDescriptor = RouteInfo.this.mDynamicDescriptor;
                int selectionState;
                if (mDynamicDescriptor != null) {
                    selectionState = mDynamicDescriptor.getSelectionState();
                }
                else {
                    selectionState = 1;
                }
                return selectionState;
            }
            
            public boolean isGroupable() {
                final MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor mDynamicDescriptor = RouteInfo.this.mDynamicDescriptor;
                return mDynamicDescriptor != null && mDynamicDescriptor.isGroupable();
            }
            
            public boolean isTransferable() {
                final MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor mDynamicDescriptor = RouteInfo.this.mDynamicDescriptor;
                return mDynamicDescriptor != null && mDynamicDescriptor.isTransferable();
            }
            
            public boolean isUnselectable() {
                final MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor mDynamicDescriptor = RouteInfo.this.mDynamicDescriptor;
                return mDynamicDescriptor == null || mDynamicDescriptor.isUnselectable();
            }
        }
    }
}
