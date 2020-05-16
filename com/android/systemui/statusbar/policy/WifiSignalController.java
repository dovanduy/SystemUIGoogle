// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.text.TextUtils;
import java.util.Objects;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.R$bool;
import android.content.Intent;
import android.net.wifi.WifiManager$TrafficStateCallback;
import android.net.NetworkScoreManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.content.Context;
import com.android.settingslib.wifi.WifiStatusTracker;

public class WifiSignalController extends SignalController<WifiState, IconGroup>
{
    private final boolean mHasMobileDataFeature;
    private final WifiStatusTracker mWifiTracker;
    
    public WifiSignalController(final Context context, final boolean mHasMobileDataFeature, final CallbackHandler callbackHandler, final NetworkControllerImpl networkControllerImpl, final WifiManager wifiManager, final ConnectivityManager connectivityManager, final NetworkScoreManager networkScoreManager) {
        super("WifiSignalController", context, 1, callbackHandler, networkControllerImpl);
        (this.mWifiTracker = new WifiStatusTracker(super.mContext, wifiManager, networkScoreManager, connectivityManager, new _$$Lambda$WifiSignalController$AffzGdHvQakHA4bIzi_tW1MVLCY(this))).setListening(true);
        this.mHasMobileDataFeature = mHasMobileDataFeature;
        if (wifiManager != null) {
            wifiManager.registerTrafficStateCallback(context.getMainExecutor(), (WifiManager$TrafficStateCallback)new WifiTrafficStateCallback());
        }
        final WifiState wifiState = (WifiState)super.mCurrentState;
        final WifiState wifiState2 = (WifiState)super.mLastState;
        final IconGroup iconGroup = new IconGroup("Wi-Fi Icons", WifiIcons.WIFI_SIGNAL_STRENGTH, WifiIcons.QS_WIFI_SIGNAL_STRENGTH, AccessibilityContentDescriptions.WIFI_CONNECTION_STRENGTH, 17302863, 17302863, 17302863, 17302863, AccessibilityContentDescriptions.WIFI_NO_CONNECTION);
        wifiState2.iconGroup = iconGroup;
        wifiState.iconGroup = iconGroup;
    }
    
    private void handleStatusUpdated() {
        ((WifiState)super.mCurrentState).statusLabel = this.mWifiTracker.statusLabel;
        this.notifyListenersIfNecessary();
    }
    
    @Override
    protected WifiState cleanState() {
        return new WifiState();
    }
    
    public void handleBroadcast(final Intent intent) {
        this.mWifiTracker.handleBroadcast(intent);
        final State mCurrentState = super.mCurrentState;
        final WifiState wifiState = (WifiState)mCurrentState;
        final WifiStatusTracker mWifiTracker = this.mWifiTracker;
        wifiState.enabled = mWifiTracker.enabled;
        ((WifiState)mCurrentState).connected = mWifiTracker.connected;
        ((WifiState)mCurrentState).ssid = mWifiTracker.ssid;
        ((WifiState)mCurrentState).rssi = mWifiTracker.rssi;
        ((WifiState)mCurrentState).level = mWifiTracker.level;
        ((WifiState)mCurrentState).statusLabel = mWifiTracker.statusLabel;
        this.notifyListenersIfNecessary();
    }
    
    @Override
    public void notifyListeners(final NetworkController.SignalCallback signalCallback) {
        final boolean boolean1 = super.mContext.getResources().getBoolean(R$bool.config_showWifiIndicatorWhenEnabled);
        final State mCurrentState = super.mCurrentState;
        final boolean b = ((WifiState)mCurrentState).enabled && ((((WifiState)mCurrentState).connected && ((WifiState)mCurrentState).inetCondition == 1) || !this.mHasMobileDataFeature || this.mWifiTracker.isDefaultNetwork || boolean1);
        final State mCurrentState2 = super.mCurrentState;
        String ssid;
        if (((WifiState)mCurrentState2).connected) {
            ssid = ((WifiState)mCurrentState2).ssid;
        }
        else {
            ssid = null;
        }
        final boolean b2 = b && ((WifiState)super.mCurrentState).ssid != null;
        String s;
        final String str = s = this.getTextIfExists(this.getContentDescription()).toString();
        if (((WifiState)super.mCurrentState).inetCondition == 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(",");
            sb.append(super.mContext.getString(R$string.data_connection_no_internet));
            s = sb.toString();
        }
        final NetworkController.IconState iconState = new NetworkController.IconState(b, this.getCurrentIconId(), s);
        final boolean connected = ((WifiState)super.mCurrentState).connected;
        int n;
        if (this.mWifiTracker.isCaptivePortal) {
            n = R$drawable.ic_qs_wifi_disconnected;
        }
        else {
            n = this.getQsCurrentIconId();
        }
        final NetworkController.IconState iconState2 = new NetworkController.IconState(connected, n, s);
        final State mCurrentState3 = super.mCurrentState;
        final boolean enabled = ((WifiState)mCurrentState3).enabled;
        final boolean b3 = b2 && ((WifiState)mCurrentState3).activityIn;
        final boolean b4 = b2 && ((WifiState)super.mCurrentState).activityOut;
        final State mCurrentState4 = super.mCurrentState;
        signalCallback.setWifiIndicators(enabled, iconState, iconState2, b3, b4, ssid, ((WifiState)mCurrentState4).isTransient, ((WifiState)mCurrentState4).statusLabel);
    }
    
    void refreshLocale() {
        this.mWifiTracker.refreshLocale();
    }
    
    @VisibleForTesting
    void setActivity(final int n) {
        final WifiState wifiState = (WifiState)super.mCurrentState;
        final boolean b = false;
        wifiState.activityIn = (n == 3 || n == 1);
        final WifiState wifiState2 = (WifiState)super.mCurrentState;
        boolean activityOut = false;
        Label_0062: {
            if (n != 3) {
                activityOut = b;
                if (n != 2) {
                    break Label_0062;
                }
            }
            activityOut = true;
        }
        wifiState2.activityOut = activityOut;
        this.notifyListenersIfNecessary();
    }
    
    static class WifiState extends State
    {
        boolean isTransient;
        String ssid;
        String statusLabel;
        
        @Override
        public void copyFrom(final State state) {
            super.copyFrom(state);
            final WifiState wifiState = (WifiState)state;
            this.ssid = wifiState.ssid;
            this.isTransient = wifiState.isTransient;
            this.statusLabel = wifiState.statusLabel;
        }
        
        @Override
        public boolean equals(final Object o) {
            final boolean equals = super.equals(o);
            final boolean b = false;
            if (!equals) {
                return false;
            }
            final WifiState wifiState = (WifiState)o;
            boolean b2 = b;
            if (Objects.equals(wifiState.ssid, this.ssid)) {
                b2 = b;
                if (wifiState.isTransient == this.isTransient) {
                    b2 = b;
                    if (TextUtils.equals((CharSequence)wifiState.statusLabel, (CharSequence)this.statusLabel)) {
                        b2 = true;
                    }
                }
            }
            return b2;
        }
        
        @Override
        protected void toString(final StringBuilder sb) {
            super.toString(sb);
            sb.append(",ssid=");
            sb.append(this.ssid);
            sb.append(",isTransient=");
            sb.append(this.isTransient);
            sb.append(",statusLabel=");
            sb.append(this.statusLabel);
        }
    }
    
    private class WifiTrafficStateCallback implements WifiManager$TrafficStateCallback
    {
        public void onStateChanged(final int activity) {
            WifiSignalController.this.setActivity(activity);
        }
    }
}
