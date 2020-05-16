// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import android.media.MediaRouter$VolumeCallback;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import android.os.Build$VERSION;
import java.lang.reflect.Method;
import android.media.MediaRouter$RouteGroup;
import android.media.MediaRouter$RouteInfo;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.media.MediaRouter$RouteCategory;
import android.media.MediaRouter$UserRouteInfo;
import android.media.MediaRouter$Callback;
import android.media.MediaRouter;

final class MediaRouterJellybean
{
    public static void addCallback(final Object o, final int n, final Object o2) {
        ((MediaRouter)o).addCallback(n, (MediaRouter$Callback)o2);
    }
    
    public static void addUserRoute(final Object o, final Object o2) {
        ((MediaRouter)o).addUserRoute((MediaRouter$UserRouteInfo)o2);
    }
    
    public static Object createCallback(final Callback callback) {
        return new CallbackProxy(callback);
    }
    
    public static Object createRouteCategory(final Object o, final String s, final boolean b) {
        return ((MediaRouter)o).createRouteCategory((CharSequence)s, b);
    }
    
    public static Object createUserRoute(final Object o, final Object o2) {
        return ((MediaRouter)o).createUserRoute((MediaRouter$RouteCategory)o2);
    }
    
    public static Object createVolumeCallback(final VolumeCallback volumeCallback) {
        return new VolumeCallbackProxy(volumeCallback);
    }
    
    public static Object getMediaRouter(final Context context) {
        return context.getSystemService("media_router");
    }
    
    public static List getRoutes(final Object o) {
        final MediaRouter mediaRouter = (MediaRouter)o;
        final int routeCount = mediaRouter.getRouteCount();
        final ArrayList list = new ArrayList<MediaRouter$RouteInfo>(routeCount);
        for (int i = 0; i < routeCount; ++i) {
            list.add(mediaRouter.getRouteAt(i));
        }
        return list;
    }
    
    public static Object getSelectedRoute(final Object o, final int n) {
        return ((MediaRouter)o).getSelectedRoute(n);
    }
    
    public static void removeCallback(final Object o, final Object o2) {
        ((MediaRouter)o).removeCallback((MediaRouter$Callback)o2);
    }
    
    public static void removeUserRoute(final Object o, final Object o2) {
        ((MediaRouter)o).removeUserRoute((MediaRouter$UserRouteInfo)o2);
    }
    
    public static void selectRoute(final Object o, final int n, final Object o2) {
        ((MediaRouter)o).selectRoute(n, (MediaRouter$RouteInfo)o2);
    }
    
    public interface Callback
    {
        void onRouteAdded(final Object p0);
        
        void onRouteChanged(final Object p0);
        
        void onRouteGrouped(final Object p0, final Object p1, final int p2);
        
        void onRouteRemoved(final Object p0);
        
        void onRouteSelected(final int p0, final Object p1);
        
        void onRouteUngrouped(final Object p0, final Object p1);
        
        void onRouteUnselected(final int p0, final Object p1);
        
        void onRouteVolumeChanged(final Object p0);
    }
    
    static class CallbackProxy<T extends Callback> extends MediaRouter$Callback
    {
        protected final T mCallback;
        
        public CallbackProxy(final T mCallback) {
            this.mCallback = mCallback;
        }
        
        public void onRouteAdded(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
            ((Callback)this.mCallback).onRouteAdded(mediaRouter$RouteInfo);
        }
        
        public void onRouteChanged(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
            ((Callback)this.mCallback).onRouteChanged(mediaRouter$RouteInfo);
        }
        
        public void onRouteGrouped(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo, final MediaRouter$RouteGroup mediaRouter$RouteGroup, final int n) {
            ((Callback)this.mCallback).onRouteGrouped(mediaRouter$RouteInfo, mediaRouter$RouteGroup, n);
        }
        
        public void onRouteRemoved(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
            ((Callback)this.mCallback).onRouteRemoved(mediaRouter$RouteInfo);
        }
        
        public void onRouteSelected(final MediaRouter mediaRouter, final int n, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
            ((Callback)this.mCallback).onRouteSelected(n, mediaRouter$RouteInfo);
        }
        
        public void onRouteUngrouped(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo, final MediaRouter$RouteGroup mediaRouter$RouteGroup) {
            ((Callback)this.mCallback).onRouteUngrouped(mediaRouter$RouteInfo, mediaRouter$RouteGroup);
        }
        
        public void onRouteUnselected(final MediaRouter mediaRouter, final int n, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
            ((Callback)this.mCallback).onRouteUnselected(n, mediaRouter$RouteInfo);
        }
        
        public void onRouteVolumeChanged(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
            ((Callback)this.mCallback).onRouteVolumeChanged(mediaRouter$RouteInfo);
        }
    }
    
    public static final class GetDefaultRouteWorkaround
    {
        private Method mGetSystemAudioRouteMethod;
        
        public GetDefaultRouteWorkaround() {
            final int sdk_INT = Build$VERSION.SDK_INT;
            Label_0036: {
                if (sdk_INT < 16 || sdk_INT > 17) {
                    break Label_0036;
                }
                try {
                    this.mGetSystemAudioRouteMethod = MediaRouter.class.getMethod("getSystemAudioRoute", (Class<?>[])new Class[0]);
                    return;
                    throw new UnsupportedOperationException();
                }
                catch (NoSuchMethodException ex) {}
            }
        }
        
        public Object getDefaultRoute(Object obj) {
            obj = obj;
            final Method mGetSystemAudioRouteMethod = this.mGetSystemAudioRouteMethod;
            Label_0026: {
                if (mGetSystemAudioRouteMethod == null) {
                    break Label_0026;
                }
                try {
                    return mGetSystemAudioRouteMethod.invoke(obj, new Object[0]);
                    return ((MediaRouter)obj).getRouteAt(0);
                }
                catch (IllegalAccessException | InvocationTargetException ex) {
                    return ((MediaRouter)obj).getRouteAt(0);
                }
            }
        }
    }
    
    public static final class RouteInfo
    {
        public static CharSequence getName(final Object o, final Context context) {
            return ((MediaRouter$RouteInfo)o).getName(context);
        }
        
        public static int getPlaybackStream(final Object o) {
            return ((MediaRouter$RouteInfo)o).getPlaybackStream();
        }
        
        public static int getPlaybackType(final Object o) {
            return ((MediaRouter$RouteInfo)o).getPlaybackType();
        }
        
        public static int getSupportedTypes(final Object o) {
            return ((MediaRouter$RouteInfo)o).getSupportedTypes();
        }
        
        public static Object getTag(final Object o) {
            return ((MediaRouter$RouteInfo)o).getTag();
        }
        
        public static int getVolume(final Object o) {
            return ((MediaRouter$RouteInfo)o).getVolume();
        }
        
        public static int getVolumeHandling(final Object o) {
            return ((MediaRouter$RouteInfo)o).getVolumeHandling();
        }
        
        public static int getVolumeMax(final Object o) {
            return ((MediaRouter$RouteInfo)o).getVolumeMax();
        }
        
        public static void requestSetVolume(final Object o, final int n) {
            ((MediaRouter$RouteInfo)o).requestSetVolume(n);
        }
        
        public static void requestUpdateVolume(final Object o, final int n) {
            ((MediaRouter$RouteInfo)o).requestUpdateVolume(n);
        }
        
        public static void setTag(final Object o, final Object tag) {
            ((MediaRouter$RouteInfo)o).setTag(tag);
        }
    }
    
    public static final class SelectRouteWorkaround
    {
        private Method mSelectRouteIntMethod;
        
        public SelectRouteWorkaround() {
            final int sdk_INT = Build$VERSION.SDK_INT;
            Label_0047: {
                if (sdk_INT < 16 || sdk_INT > 17) {
                    break Label_0047;
                }
                try {
                    this.mSelectRouteIntMethod = MediaRouter.class.getMethod("selectRouteInt", Integer.TYPE, MediaRouter$RouteInfo.class);
                    return;
                    throw new UnsupportedOperationException();
                }
                catch (NoSuchMethodException ex) {}
            }
        }
        
        public void selectRoute(Object obj, final int i, Object o) {
            obj = obj;
            o = o;
            Label_0092: {
                if ((((MediaRouter$RouteInfo)o).getSupportedTypes() & 0x800000) == 0x0) {
                    final Method mSelectRouteIntMethod = this.mSelectRouteIntMethod;
                    if (mSelectRouteIntMethod != null) {
                        try {
                            mSelectRouteIntMethod.invoke(obj, i, o);
                            return;
                        }
                        catch (InvocationTargetException ex) {
                            Log.w("MediaRouterJellybean", "Cannot programmatically select non-user route.  Media routing may not work.", (Throwable)ex);
                            break Label_0092;
                        }
                        catch (IllegalAccessException ex2) {
                            Log.w("MediaRouterJellybean", "Cannot programmatically select non-user route.  Media routing may not work.", (Throwable)ex2);
                            break Label_0092;
                        }
                    }
                    Log.w("MediaRouterJellybean", "Cannot programmatically select non-user route because the platform is missing the selectRouteInt() method.  Media routing may not work.");
                }
            }
            ((MediaRouter)obj).selectRoute(i, (MediaRouter$RouteInfo)o);
        }
    }
    
    public static final class UserRouteInfo
    {
        public static void setName(final Object o, final CharSequence name) {
            ((MediaRouter$UserRouteInfo)o).setName(name);
        }
        
        public static void setPlaybackStream(final Object o, final int playbackStream) {
            ((MediaRouter$UserRouteInfo)o).setPlaybackStream(playbackStream);
        }
        
        public static void setPlaybackType(final Object o, final int playbackType) {
            ((MediaRouter$UserRouteInfo)o).setPlaybackType(playbackType);
        }
        
        public static void setVolume(final Object o, final int volume) {
            ((MediaRouter$UserRouteInfo)o).setVolume(volume);
        }
        
        public static void setVolumeCallback(final Object o, final Object o2) {
            ((MediaRouter$UserRouteInfo)o).setVolumeCallback((MediaRouter$VolumeCallback)o2);
        }
        
        public static void setVolumeHandling(final Object o, final int volumeHandling) {
            ((MediaRouter$UserRouteInfo)o).setVolumeHandling(volumeHandling);
        }
        
        public static void setVolumeMax(final Object o, final int volumeMax) {
            ((MediaRouter$UserRouteInfo)o).setVolumeMax(volumeMax);
        }
    }
    
    public interface VolumeCallback
    {
        void onVolumeSetRequest(final Object p0, final int p1);
        
        void onVolumeUpdateRequest(final Object p0, final int p1);
    }
    
    static class VolumeCallbackProxy<T extends VolumeCallback> extends MediaRouter$VolumeCallback
    {
        protected final T mCallback;
        
        public VolumeCallbackProxy(final T mCallback) {
            this.mCallback = mCallback;
        }
        
        public void onVolumeSetRequest(final MediaRouter$RouteInfo mediaRouter$RouteInfo, final int n) {
            ((VolumeCallback)this.mCallback).onVolumeSetRequest(mediaRouter$RouteInfo, n);
        }
        
        public void onVolumeUpdateRequest(final MediaRouter$RouteInfo mediaRouter$RouteInfo, final int n) {
            ((VolumeCallback)this.mCallback).onVolumeUpdateRequest(mediaRouter$RouteInfo, n);
        }
    }
}
