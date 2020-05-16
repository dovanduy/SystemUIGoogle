// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.Iterator;
import android.net.TetheringManager$StartTetheringCallback;
import com.android.internal.util.ConcurrentUtils;
import android.net.TetheringManager$TetheringRequest$Builder;
import android.net.wifi.WifiClient;
import java.util.List;
import android.app.ActivityManager;
import android.os.UserManager;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Collection;
import java.util.concurrent.Executor;
import android.os.HandlerExecutor;
import android.net.TetheringManager$TetheringInterfaceRegexps;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.TetheringManager;
import android.net.TetheringManager$TetheringEventCallback;
import android.os.Handler;
import android.content.Context;
import java.util.ArrayList;
import android.net.wifi.WifiManager$SoftApCallback;

public class HotspotControllerImpl implements HotspotController, WifiManager$SoftApCallback
{
    private static final boolean DEBUG;
    private final ArrayList<Callback> mCallbacks;
    private final Context mContext;
    private volatile boolean mHasTetherableWifiRegexs;
    private int mHotspotState;
    private volatile boolean mIsTetheringSupported;
    private final Handler mMainHandler;
    private volatile int mNumConnectedDevices;
    private TetheringManager$TetheringEventCallback mTetheringCallback;
    private final TetheringManager mTetheringManager;
    private boolean mWaitingForTerminalState;
    private final WifiManager mWifiManager;
    
    static {
        DEBUG = Log.isLoggable("HotspotController", 3);
    }
    
    public HotspotControllerImpl(final Context mContext, final Handler mMainHandler, final Handler handler) {
        this.mCallbacks = new ArrayList<Callback>();
        this.mIsTetheringSupported = true;
        this.mHasTetherableWifiRegexs = true;
        this.mTetheringCallback = (TetheringManager$TetheringEventCallback)new TetheringManager$TetheringEventCallback() {
            public void onTetherableInterfaceRegexpsChanged(final TetheringManager$TetheringInterfaceRegexps tetheringManager$TetheringInterfaceRegexps) {
                final boolean b = tetheringManager$TetheringInterfaceRegexps.getTetherableWifiRegexs().size() != 0;
                if (HotspotControllerImpl.this.mHasTetherableWifiRegexs != b) {
                    HotspotControllerImpl.this.mHasTetherableWifiRegexs = b;
                    HotspotControllerImpl.this.fireHotspotAvailabilityChanged();
                }
            }
            
            public void onTetheringSupported(final boolean b) {
                if (HotspotControllerImpl.this.mIsTetheringSupported != b) {
                    HotspotControllerImpl.this.mIsTetheringSupported = b;
                    HotspotControllerImpl.this.fireHotspotAvailabilityChanged();
                }
            }
        };
        this.mContext = mContext;
        this.mTetheringManager = (TetheringManager)mContext.getSystemService((Class)TetheringManager.class);
        this.mWifiManager = (WifiManager)mContext.getSystemService("wifi");
        this.mMainHandler = mMainHandler;
        this.mTetheringManager.registerTetheringEventCallback((Executor)new HandlerExecutor(handler), this.mTetheringCallback);
    }
    
    private void fireHotspotAvailabilityChanged() {
        Object o = this.mCallbacks;
        synchronized (o) {
            final ArrayList<Callback> list = (ArrayList<Callback>)new ArrayList<Object>(this.mCallbacks);
            // monitorexit(o)
            o = list.iterator();
            while (((Iterator)o).hasNext()) {
                ((Iterator<Callback>)o).next().onHotspotAvailabilityChanged(this.isHotspotSupported());
            }
        }
    }
    
    private void fireHotspotChangedCallback() {
        Object o = this.mCallbacks;
        synchronized (o) {
            final ArrayList<Callback> list = (ArrayList<Callback>)new ArrayList<Object>(this.mCallbacks);
            // monitorexit(o)
            o = list.iterator();
            while (((Iterator)o).hasNext()) {
                ((Iterator<Callback>)o).next().onHotspotChanged(this.isHotspotEnabled(), this.mNumConnectedDevices);
            }
        }
    }
    
    private void maybeResetSoftApState() {
        if (!this.mWaitingForTerminalState) {
            return;
        }
        final int mHotspotState = this.mHotspotState;
        if (mHotspotState != 11 && mHotspotState != 13) {
            if (mHotspotState != 14) {
                return;
            }
            this.mTetheringManager.stopTethering(0);
        }
        this.mWaitingForTerminalState = false;
    }
    
    private static String stateToString(final int n) {
        switch (n) {
            default: {
                return null;
            }
            case 14: {
                return "FAILED";
            }
            case 13: {
                return "ENABLED";
            }
            case 12: {
                return "ENABLING";
            }
            case 11: {
                return "DISABLED";
            }
            case 10: {
                return "DISABLING";
            }
        }
    }
    
    public void addCallback(final Callback e) {
        final ArrayList<Callback> mCallbacks = this.mCallbacks;
        // monitorenter(mCallbacks)
        if (e == null) {
            return;
        }
        try {
            if (this.mCallbacks.contains(e)) {
                return;
            }
            if (HotspotControllerImpl.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("addCallback ");
                sb.append(e);
                Log.d("HotspotController", sb.toString());
            }
            this.mCallbacks.add(e);
            if (this.mWifiManager != null) {
                if (this.mCallbacks.size() == 1) {
                    this.mWifiManager.registerSoftApCallback((Executor)new HandlerExecutor(this.mMainHandler), (WifiManager$SoftApCallback)this);
                }
                else {
                    this.mMainHandler.post((Runnable)new _$$Lambda$HotspotControllerImpl$C17PPPxxCR_pTmr2izVaDhyC9AQ(this, e));
                }
            }
        }
        finally {
        }
        // monitorexit(mCallbacks)
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("HotspotController state:");
        printWriter.print("  available=");
        printWriter.println(this.isHotspotSupported());
        printWriter.print("  mHotspotState=");
        printWriter.println(stateToString(this.mHotspotState));
        printWriter.print("  mNumConnectedDevices=");
        printWriter.println(this.mNumConnectedDevices);
        printWriter.print("  mWaitingForTerminalState=");
        printWriter.println(this.mWaitingForTerminalState);
    }
    
    @Override
    public int getNumConnectedDevices() {
        return this.mNumConnectedDevices;
    }
    
    @Override
    public boolean isHotspotEnabled() {
        return this.mHotspotState == 13;
    }
    
    @Override
    public boolean isHotspotSupported() {
        return this.mIsTetheringSupported && this.mHasTetherableWifiRegexs && UserManager.get(this.mContext).isUserAdmin(ActivityManager.getCurrentUser());
    }
    
    @Override
    public boolean isHotspotTransient() {
        return this.mWaitingForTerminalState || this.mHotspotState == 12;
    }
    
    public void onConnectedClientsChanged(final List<WifiClient> list) {
        this.mNumConnectedDevices = list.size();
        this.fireHotspotChangedCallback();
    }
    
    public void onStateChanged(final int mHotspotState, final int n) {
        this.mHotspotState = mHotspotState;
        this.maybeResetSoftApState();
        if (!this.isHotspotEnabled()) {
            this.mNumConnectedDevices = 0;
        }
        this.fireHotspotChangedCallback();
    }
    
    public void removeCallback(final Callback callback) {
        if (callback == null) {
            return;
        }
        if (HotspotControllerImpl.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("removeCallback ");
            sb.append(callback);
            Log.d("HotspotController", sb.toString());
        }
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(callback);
            if (this.mCallbacks.isEmpty() && this.mWifiManager != null) {
                this.mWifiManager.unregisterSoftApCallback((WifiManager$SoftApCallback)this);
            }
        }
    }
    
    @Override
    public void setHotspotEnabled(final boolean b) {
        if (this.mWaitingForTerminalState) {
            if (HotspotControllerImpl.DEBUG) {
                Log.d("HotspotController", "Ignoring setHotspotEnabled; waiting for terminal state.");
            }
            return;
        }
        if (b) {
            this.mWaitingForTerminalState = true;
            if (HotspotControllerImpl.DEBUG) {
                Log.d("HotspotController", "Starting tethering");
            }
            this.mTetheringManager.startTethering(new TetheringManager$TetheringRequest$Builder(0).build(), ConcurrentUtils.DIRECT_EXECUTOR, (TetheringManager$StartTetheringCallback)new TetheringManager$StartTetheringCallback() {
                public void onTetheringFailed(final int n) {
                    if (HotspotControllerImpl.DEBUG) {
                        Log.d("HotspotController", "onTetheringFailed");
                    }
                    HotspotControllerImpl.this.maybeResetSoftApState();
                    HotspotControllerImpl.this.fireHotspotChangedCallback();
                }
            });
        }
        else {
            this.mTetheringManager.stopTethering(0);
        }
    }
}
