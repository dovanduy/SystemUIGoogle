// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import android.view.Display;
import android.media.MediaRouter$RouteInfo;
import android.media.MediaRouter;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import android.os.Build$VERSION;
import android.content.Context;
import java.lang.reflect.Method;
import android.os.Handler;
import android.hardware.display.DisplayManager;

final class MediaRouterJellybeanMr1
{
    public static Object createCallback(final Callback callback) {
        return new CallbackProxy(callback);
    }
    
    public static final class ActiveScanWorkaround implements Runnable
    {
        private boolean mActivelyScanningWifiDisplays;
        private final DisplayManager mDisplayManager;
        private final Handler mHandler;
        private Method mScanWifiDisplaysMethod;
        
        public ActiveScanWorkaround(final Context context, final Handler mHandler) {
            Label_0046: {
                if (Build$VERSION.SDK_INT != 17) {
                    break Label_0046;
                }
                this.mDisplayManager = (DisplayManager)context.getSystemService("display");
                this.mHandler = mHandler;
                try {
                    this.mScanWifiDisplaysMethod = DisplayManager.class.getMethod("scanWifiDisplays", (Class<?>[])new Class[0]);
                    return;
                    throw new UnsupportedOperationException();
                }
                catch (NoSuchMethodException ex) {}
            }
        }
        
        @Override
        public void run() {
            if (this.mActivelyScanningWifiDisplays) {
                try {
                    this.mScanWifiDisplaysMethod.invoke(this.mDisplayManager, new Object[0]);
                }
                catch (InvocationTargetException ex) {
                    Log.w("MediaRouterJellybeanMr1", "Cannot scan for wifi displays.", (Throwable)ex);
                }
                catch (IllegalAccessException ex2) {
                    Log.w("MediaRouterJellybeanMr1", "Cannot scan for wifi displays.", (Throwable)ex2);
                }
                this.mHandler.postDelayed((Runnable)this, 15000L);
            }
        }
        
        public void setActiveScanRouteTypes(final int n) {
            if ((n & 0x2) != 0x0) {
                if (!this.mActivelyScanningWifiDisplays) {
                    if (this.mScanWifiDisplaysMethod != null) {
                        this.mActivelyScanningWifiDisplays = true;
                        this.mHandler.post((Runnable)this);
                    }
                    else {
                        Log.w("MediaRouterJellybeanMr1", "Cannot scan for wifi displays because the DisplayManager.scanWifiDisplays() method is not available on this device.");
                    }
                }
            }
            else if (this.mActivelyScanningWifiDisplays) {
                this.mActivelyScanningWifiDisplays = false;
                this.mHandler.removeCallbacks((Runnable)this);
            }
        }
    }
    
    public interface Callback extends MediaRouterJellybean.Callback
    {
        void onRoutePresentationDisplayChanged(final Object p0);
    }
    
    static class CallbackProxy<T extends MediaRouterJellybeanMr1.Callback> extends MediaRouterJellybean.CallbackProxy<T>
    {
        public CallbackProxy(final T t) {
            super(t);
        }
        
        public void onRoutePresentationDisplayChanged(final MediaRouter mediaRouter, final MediaRouter$RouteInfo mediaRouter$RouteInfo) {
            ((MediaRouterJellybeanMr1.Callback)super.mCallback).onRoutePresentationDisplayChanged(mediaRouter$RouteInfo);
        }
    }
    
    public static final class IsConnectingWorkaround
    {
        private Method mGetStatusCodeMethod;
        private int mStatusConnecting;
        
        public IsConnectingWorkaround() {
            Label_0043: {
                if (Build$VERSION.SDK_INT != 17) {
                    break Label_0043;
                }
                try {
                    this.mStatusConnecting = MediaRouter$RouteInfo.class.getField("STATUS_CONNECTING").getInt(null);
                    this.mGetStatusCodeMethod = MediaRouter$RouteInfo.class.getMethod("getStatusCode", (Class<?>[])new Class[0]);
                    return;
                    throw new UnsupportedOperationException();
                }
                catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException ex) {}
            }
        }
        
        public boolean isConnecting(final Object o) {
            final MediaRouter$RouteInfo obj = (MediaRouter$RouteInfo)o;
            final Method mGetStatusCodeMethod = this.mGetStatusCodeMethod;
            boolean b2;
            final boolean b = b2 = false;
            if (mGetStatusCodeMethod == null) {
                return b2;
            }
            try {
                final int intValue = (int)mGetStatusCodeMethod.invoke(obj, new Object[0]);
                final int mStatusConnecting = this.mStatusConnecting;
                b2 = b;
                if (intValue == mStatusConnecting) {
                    b2 = true;
                }
                return b2;
            }
            catch (IllegalAccessException | InvocationTargetException ex) {
                b2 = b;
                return b2;
            }
        }
    }
    
    public static final class RouteInfo
    {
        public static Display getPresentationDisplay(final Object o) {
            try {
                return ((MediaRouter$RouteInfo)o).getPresentationDisplay();
            }
            catch (NoSuchMethodError noSuchMethodError) {
                Log.w("MediaRouterJellybeanMr1", "Cannot get presentation display for the route.", (Throwable)noSuchMethodError);
                return null;
            }
        }
        
        public static boolean isEnabled(final Object o) {
            return ((MediaRouter$RouteInfo)o).isEnabled();
        }
    }
}
