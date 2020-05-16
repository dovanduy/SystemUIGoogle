// 
// Decompiled by Procyon v0.5.36
// 

package com.android.wifitrackerlib;

import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.NetworkRequest;
import android.net.ConnectivityManager$NetworkCallback;
import android.os.Handler;
import android.content.Context;
import android.net.ConnectivityManager;
import android.content.BroadcastReceiver;
import androidx.lifecycle.LifecycleObserver;

public class BaseWifiTracker implements LifecycleObserver
{
    private static boolean sVerboseLogging;
    private final BroadcastReceiver mBroadcastReceiver;
    protected final ConnectivityManager mConnectivityManager;
    protected final Context mContext;
    protected final Handler mMainHandler;
    protected final long mMaxScanAgeMillis;
    private final ConnectivityManager$NetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    protected final long mScanIntervalMillis;
    protected final ScanResultUpdater mScanResultUpdater;
    private final Scanner mScanner;
    private final String mTag;
    protected final WifiManager mWifiManager;
    protected final Handler mWorkerHandler;
    
    public static boolean isVerboseLoggingEnabled() {
        return BaseWifiTracker.sVerboseLogging;
    }
    
    protected void handleOnStart() {
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter, (String)null, this.mWorkerHandler);
        this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mWorkerHandler);
        if (this.mWifiManager.getWifiState() == 3) {
            this.mScanner.start();
        }
        else {
            this.mScanner.stop();
        }
        this.mWorkerHandler.post((Runnable)new _$$Lambda$S9fuCAjG_YC38JCa05_AkNB8B_E(this));
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mScanner.stop();
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
    }
    
    private class Scanner extends Handler
    {
        private int mRetry;
        final /* synthetic */ BaseWifiTracker this$0;
        
        private void postScan() {
            if (this.this$0.mWifiManager.startScan()) {
                this.mRetry = 0;
            }
            else if (++this.mRetry >= 3) {
                if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                    final String access$000 = this.this$0.mTag;
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Scanner failed to start scan ");
                    sb.append(this.mRetry);
                    sb.append(" times!");
                    Log.v(access$000, sb.toString());
                }
                this.mRetry = 0;
                return;
            }
            this.postDelayed((Runnable)new _$$Lambda$BaseWifiTracker$Scanner$Lob1PHu6bdjiK_7H86IDLNF_WiM(this), this.this$0.mScanIntervalMillis);
        }
        
        private void start() {
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v(this.this$0.mTag, "Scanner start");
            }
            this.post((Runnable)new _$$Lambda$BaseWifiTracker$Scanner$Lob1PHu6bdjiK_7H86IDLNF_WiM(this));
        }
        
        private void stop() {
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v(this.this$0.mTag, "Scanner stop");
            }
            this.mRetry = 0;
            this.removeCallbacksAndMessages((Object)null);
        }
    }
}
